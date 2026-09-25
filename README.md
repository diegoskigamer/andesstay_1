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

