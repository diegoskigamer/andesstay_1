# AndesStay — Caso semestral

Plataforma de reservas para una red de hostales, cabañas y lodges, con
arquitectura de **microservicios**, login federado **Cognito + Azure AD**,
mensajería con **RabbitMQ** y **Kafka**, y despliegue con **Docker Compose**
en 3 instancias EC2, tal como pide el caso.

## Estructura del repositorio

```
andesstay/
├── ms-andesstay-bff/           Único punto público. Valida JWT de Cognito, reenvía a los demás.
├── ms-andesstay-reservations/  Reservas: CRUD, estados, disponibilidad, eventos.
├── ms-andesstay-catalog/       Catálogo: unidades, tarifas, disponibilidad.
├── ms-andesstay-notify/        Notificaciones: consume RabbitMQ (email, housekeeping, voucher).
├── ms-andesstay-audit/         Auditoría: consume Kafka, persiste timeline (solo lectura).
├── ms-andesstay-report/        Reportería/KPIs: consume Kafka (solo lectura).
├── ms-andesstay-rabbitmq-admin/ API administrativa sobre RabbitMQ Management API.
├── ms-andesstay-kafka-admin/    API administrativa sobre Kafka AdminClient.
├── frontend/                   React (Vite) — habla con el BFF.
└── infra/
    ├── apps/    compose.yml + .env.example para ec2-apps (los 8 microservicios)
    ├── mq/      compose.yml + README para ec2-mq (clúster RabbitMQ)
    └── kafka/   compose.yml + README para ec2-kafka (Zookeeper + Kafka + Kafka UI)
```

## Arquitectura (resumen)

```
Usuario → Cognito Hosted UI → Azure AD (login) → Cognito emite tokens
   → Frontend React → ms-andesstay-bff (valida JWT, único punto público)
        → ms-andesstay-reservations ──→ ms-andesstay-catalog (REST, disponibilidad)
        │        │
        │        ├──→ RabbitMQ ──→ ms-andesstay-notify (email, housekeeping, voucher)
        │        └──→ Kafka ──→ ms-andesstay-audit (timeline)
        │                   └──→ ms-andesstay-report (KPIs)
        → ms-andesstay-catalog (lectura directa)
        → ms-andesstay-audit (lectura directa)
        → ms-andesstay-report (lectura directa)
```

## Cómo correrlo todo en LOCAL (sin Docker, para desarrollar)

Cada microservicio es un proyecto Maven independiente. Necesitas 8
terminales (o usa el botón Run de tu IDE en cada uno), más el frontend:

```bash
# Terminal 1
cd ms-andesstay-catalog && mvn spring-boot:run

# Terminal 2 (después de que catalog esté arriba)
cd ms-andesstay-reservations && mvn spring-boot:run

# Terminal 3
cd ms-andesstay-notify && mvn spring-boot:run

# Terminal 4
cd ms-andesstay-audit && mvn spring-boot:run

# Terminal 5
cd ms-andesstay-report && mvn spring-boot:run

# Terminal 6
cd ms-andesstay-rabbitmq-admin && mvn spring-boot:run

# Terminal 7
cd ms-andesstay-kafka-admin && mvn spring-boot:run

# Terminal 8 (al final, ya que reenvía a todos los anteriores)
cd ms-andesstay-bff && mvn spring-boot:run

# Terminal 9 — frontend
cd frontend && npm install && npm run dev
```

**Nota importante:** en local, sin RabbitMQ ni Kafka corriendo, reservations
y catalog funcionan igual (las reservas se crean bien), pero vas a ver
warnings en el log de `ms-andesstay-reservations` del tipo "No se pudo
publicar en Kafka/RabbitMQ" — es esperado, el código no bloquea la
operación de negocio si la mensajería no está disponible (ver
`ReservationEventPublisher.java`). Para probar el flujo completo con
notificaciones y auditoría en tiempo real, levanta también `infra/mq` e
`infra/kafka` (ver más abajo) y configura las variables
`RABBITMQ_HOST`/`KAFKA_BOOTSTRAP_SERVERS` apuntando a `localhost` con los
puertos expuestos.

## Cómo correrlo todo con Docker Compose (más cercano a producción)

Ver `infra/README.md` para el detalle completo. Resumen:

```bash
cd infra/kafka && docker compose up -d        # espera ~1 min
cd ../mq && docker compose up -d
cd ../apps && cp .env.example .env            # edita .env primero
docker compose up -d --build
```

## Login (Cognito federado con Azure AD)

Toda la lógica de autenticación (intercambio del código OAuth por
tokens, usando el client secret de Cognito) vive ahora en
**ms-andesstay-bff**, no en un microservicio de dominio — es la razón por
la que el BFF es el único servicio con Spring Security. Ver
`ms-andesstay-bff/src/main/java/com/andesstay/service/CognitoAuthService.java`
y `SecurityConfig.java`.

Variables de entorno necesarias en el BFF para activar el login real:
```
ANDESSTAY_SECURITY_ENABLED=true
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://cognito-idp.<region>.amazonaws.com/<userPoolId>
COGNITO_DOMAIN=tu-dominio.auth.<region>.amazoncognito.com
COGNITO_CLIENT_ID=<client id>
COGNITO_CLIENT_SECRET=<client secret, si tu App Client lo tiene>
```

El frontend no cambió — sigue apuntando a `VITE_API_BASE_URL=http://localhost:8080`,
que ahora es el BFF en vez del monolito. Ver `frontend/README.md`.

## Qué quedó implementado

- ✅ 8 microservicios Spring Boot separados, cada uno con su propia base de datos (H2 en memoria; ver nota sobre Oracle abajo)
- ✅ BFF como único punto público, con Spring Security validando JWT de Cognito
- ✅ Comunicación síncrona entre reservations y catalog vía REST (`CatalogClient`)
- ✅ RabbitMQ: colas `q.cmd.email`, `q.cmd.housekeeping`, `q.cmd.voucher` + DLQ, consumidas por notify
- ✅ Kafka: topic `reservations.events`, consumido por audit y report de forma independiente
- ✅ Docker Compose separado por instancia EC2 (apps / mq / kafka), con Dockerfiles multi-stage por servicio
- ✅ Login real Cognito + Azure AD, con roles

## Qué queda pendiente / simplificado a propósito

- **Oracle**: el caso pide Oracle para cada base de datos de dominio. Acá
  se usa H2 en memoria para poder correr y probar todo sin necesitar una
  instancia de Oracle real. Cada `pom.xml` de los microservicios con base
  de datos está listo para agregar el driver de Oracle
  (`com.oracle.database.jdbc:ojdbc11`) y cambiar el `application.yml`
  cuando tengas acceso a una instancia real.
- **AWS API Gateway**: no se creó (es un recurso de AWS, no código del
  repo). El BFF ya está preparado para vivir detrás de uno — solo hay que
  crear el HTTP API en la consola de AWS con un JWT Authorizer apuntando
  al mismo issuer de Cognito, y apuntarlo al puerto 8080 de `ec2-apps`.
- **Clúster real de RabbitMQ/Kafka en AWS**: los `compose.yml` de
  `infra/mq` e `infra/kafka` están listos para copiarse a las instancias
  EC2 reales; ver los README de cada carpeta para el paso a paso de
  clusterización y Security Groups.
