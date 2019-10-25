package io.blueharvest.jfall2019.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    @KafkaListener(topics = "${kafka.topic.account.command}", containerFactory = "concurrentKafkaListenerContainerFactory")
    public void receive(ConsumerRecord<String, String> command) {
        String commandValue = command.value();
        LOGGER.info("received payload='{}'", command.toString());
    }
}
