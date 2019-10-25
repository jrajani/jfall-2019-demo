package io.blueharvest.jfall2019.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blueharvest.jfall2019.command.BaseCommand;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic.account.command}")
    private String commandTopicName;

    public void sendMessage(BaseCommand command) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(commandTopicName,
                    command.getId(),
                    objectMapper.writeValueAsString(command));
            record.headers().add(new RecordHeader("command", command.getClass().getName().getBytes()));
            kafkaTemplate.send(record);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
