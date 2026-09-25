# Infraestructura de despliegue — AndesStay

Tal como pide el caso, el despliegue se separa en **3 instancias EC2**,
cada una con su propio `compose.yml`:

| Carpeta | Instancia EC2 | Contiene |
|---|---|---|
| `apps/` | `ec2-apps` | Los 8 microservicios Spring Boot (BFF + 5 de dominio + 2 admin) |
| `mq/` | `ec2-mq` | Clúster de RabbitMQ (2 nodos) + Management UI |
| `kafka/` | `ec2-kafka` | Zookeeper (3 nodos) + Kafka (3 brokers) + Kafka UI |

Cada carpeta tiene su propio `README.md` con instrucciones específicas y
la configuración de Security Group recomendada para esa instancia.

## Orden recomendado para levantar todo (en local, con Docker, para probar)

1. `cd infra/kafka && docker compose up -d` (espera ~1 min a que los brokers elijan líder)
2. `cd infra/mq && docker compose up -d`
3. `cd infra/apps`, edita `.env` para que `KAFKA_BOOTSTRAP_SERVERS` y
   `RABBITMQ_HOST` apunten a `localhost` con los puertos expuestos
   (`localhost:19092`, `localhost:5672`) en vez de los placeholders de
   IP de EC2, y luego `docker compose up -d --build`

## Al desplegar en AWS real

1. Crea las 3 instancias EC2 (`ec2-apps`, `ec2-mq`, `ec2-kafka`), cada una
   con Docker y Docker Compose instalados.
2. Configura los Security Groups como se indica en cada README.
3. Copia la carpeta correspondiente a cada instancia (`apps/` a
   `ec2-apps`, etc. — junto con el código de los microservicios que
   referencian con rutas relativas `../../ms-andesstay-*`, así que en
   `ec2-apps` necesitas todo el repo, no solo `infra/apps/`).
4. En `infra/apps/.env`, reemplaza los placeholders `rabbitmq-host-placeholder`
   y `kafka-host-placeholder` por las IPs privadas reales de `ec2-mq` y
   `ec2-kafka`.
5. Levanta cada instancia en el orden: `ec2-kafka` → `ec2-mq` → `ec2-apps`.
