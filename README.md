# jfall-2019-demo

This is an example for J-Fall 2019.

The following diagram explains the flow.

![Kafka + Event Sourcing](/doc/diagram.png)


## Run the demo

1. Start the kafka broker

```bash
docker-compose up
```

2. Run the Spring boot application

```bash
mvn spring-boot:run
```

3. Kafka consumer

```bash
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic account-snapshot \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```