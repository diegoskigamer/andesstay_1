# ec2-kafka — Zookeeper (3 nodos) + Kafka (3 brokers) + Kafka UI

## Levantarlo

```bash
cd infra/kafka
docker compose up -d
```

Espera ~30-60 segundos a que los 3 brokers terminen de elegir líder y
sincronizarse con Zookeeper antes de mandar tráfico.

## Acceso

- Desde dentro de la red Docker (otros servicios): `kafka1:9092,kafka2:9092,kafka3:9092`
- Desde el host (fuera de Docker, para debug): `localhost:19092`, `localhost:19093`, `localhost:19094`
- Kafka UI: http://localhost:8090

## Al desplegar en EC2 real

Reemplaza los hostnames `kafka1`/`kafka2`/`kafka3` en `KAFKA_ADVERTISED_LISTENERS`
por la IP privada o DNS interno real de la instancia `ec2-kafka`, para que
los microservicios en `ec2-apps` puedan resolverlos correctamente.

## Security Group (AWS)

Abre solo:
- Puerto **9092** (Kafka broker) — origen: Security Group de `ec2-apps` únicamente
- Puerto **2181** (Zookeeper) — origen: Security Group de `ec2-kafka` (entre nodos)
- Puerto **8090** (Kafka UI) — origen: tu IP o VPN, NUNCA `0.0.0.0/0`
