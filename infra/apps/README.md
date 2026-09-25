# ec2-apps — Los 8 microservicios de AndesStay

## Levantarlo

```bash
cd infra/apps
cp .env.example .env   # completa tus valores reales de Cognito y las IPs de ec2-mq/ec2-kafka
docker compose up -d --build
```

La primera vez tarda varios minutos (compila los 8 proyectos Maven). Las
siguientes veces es mucho más rápido (usa caché de capas de Docker).

## Servicios y puertos

| Servicio | Puerto | Público/Interno |
|---|---|---|
| ms-andesstay-bff | 8080 | **Público** (único expuesto detrás del API Gateway) |
| ms-andesstay-reservations | 8081 | Interno |
| ms-andesstay-catalog | 8082 | Interno |
| ms-andesstay-notify | 8083 | Interno (sin API pública, solo consume RabbitMQ) |
| ms-andesstay-audit | 8084 | Interno |
| ms-andesstay-report | 8085 | Interno |
| ms-andesstay-rabbitmq-admin | 8086 | Interno |
| ms-andesstay-kafka-admin | 8087 | Interno |

En este compose de desarrollo, todos los puertos quedan expuestos al host
para poder probarlos individualmente. **En el despliegue real en AWS**,
el Security Group de `ec2-apps` debe abrir SOLO el puerto 8080 hacia
Internet (a través del API Gateway) — los demás puertos deben quedar
cerrados hacia afuera y solo accesibles entre los contenedores de esta
misma instancia (ya lo están, vía la red Docker `andesstay-apps-net`).

## Security Group (AWS)

- Puerto **8080** (BFF) — origen: el API Gateway / Load Balancer únicamente
- Todo lo demás — **cerrado hacia Internet**; el tráfico entre microservicios
  ocurre dentro de la red interna de Docker en esta misma instancia.

## Verificar que todo levantó bien

```bash
docker compose ps
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
# ...etc para cada servicio
```
