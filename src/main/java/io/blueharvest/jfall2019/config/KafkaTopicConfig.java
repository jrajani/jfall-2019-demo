package io.blueharvest.jfall2019.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.account.command}")
    private String topicAccountCommand;

    @Value("${kafka.topic.event.account.created}")
    String kafkaTopicAccountCreated;

    @Value("${kafka.topic.event.money.deposited}")
    String kafkaTopicMoneyDeposited;

    @Value("${kafka.topic.event.money.withdrawn}")
    String kafkaTopicMoneyWithdrawn;

    @Value("${kafka.topic.account.snapshot}")
    String kafkaTopicAccountSnapshot;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic accountCommand() {
        return new NewTopic(topicAccountCommand, 1, (short) 1);
    }

    @Bean
    public NewTopic accountCreatedTopic() {
        return new NewTopic(kafkaTopicAccountCreated, 1, (short) 1);
    }

    @Bean
    public NewTopic accountWithdrawnTopic() {
        return new NewTopic(kafkaTopicMoneyWithdrawn, 1, (short) 1);
    }

    @Bean
    public NewTopic accountDepositedTopic() {
        return new NewTopic(kafkaTopicMoneyDeposited, 1, (short) 1);
    }

    @Bean
    public NewTopic accountSnapshot() {
        return new NewTopic(kafkaTopicAccountSnapshot, 1, (short) 1);
    }

}
