# Backpressure Strategy

To prevent system overload:

- Used queue buffering
- Worker processes signals gradually
- Avoid direct DB writes from API
- Ensures no request blocking

Future improvement:
- Kafka or RabbitMQ for distributed scaling