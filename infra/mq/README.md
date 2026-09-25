# ec2-mq — Clúster de RabbitMQ

## Levantarlo

```bash
cd infra/mq
cp .env.example .env   # cambia la clave y la cookie
docker compose up -d
```

Esto levanta 2 nodos (`rabbitmq1`, `rabbitmq2`) con el plugin de management
habilitado. Por defecto corren como instancias independientes; para
formar un clúster real de RabbitMQ:

1. Verifica que ambos usen la MISMA `RABBITMQ_ERLANG_COOKIE` (ya viene así
   por defecto en este compose — es lo que permite que se reconozcan
   entre sí).
2. Entra al contenedor de `rabbitmq2`:
   ```bash
   docker exec -it andesstay-mq-rabbitmq2-1 bash
   rabbitmqctl stop_app
   rabbitmqctl join_cluster rabbit@rabbitmq1
   rabbitmqctl start_app
   exit
   ```
3. Verifica el clúster:
   ```bash
   docker exec -it andesstay-mq-rabbitmq1-1 rabbitmqctl cluster_status
   ```

## Acceso

- AMQP (para las apps): puerto `5672` (nodo 1) / `5673` (nodo 2)
- Management UI: http://localhost:15672 (usuario/clave del `.env`)

## Security Group (AWS)

Al desplegar en la instancia EC2 real `ec2-mq`, abre solo:
- Puerto **5672** (AMQP) — origen: Security Group de `ec2-apps` únicamente
- Puerto **15672** (Management UI) — origen: tu IP o VPN, NUNCA `0.0.0.0/0`
- Puerto **25672** y rango **35672-35682** (comunicación interna del clúster) — origen: Security Group de `ec2-mq` (entre nodos)
