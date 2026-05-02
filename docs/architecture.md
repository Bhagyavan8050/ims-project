# Architecture

The system follows event-driven architecture:

1. Signals are ingested via REST API
2. Stored temporarily in in-memory queue
3. Worker processes signals asynchronously
4. Raw signals → MongoDB
5. Incidents → PostgreSQL
6. Cache layer improves UI performance