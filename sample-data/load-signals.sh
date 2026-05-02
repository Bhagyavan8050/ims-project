#!/bin/bash

for i in {1..20}
do
  curl -X POST http://localhost:8082/api/signal \
  -H "Content-Type: application/json" \
  -d '{"componentId":"DB_CLUSTER","message":"Failure spike"}'
done