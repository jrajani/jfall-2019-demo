package io.blueharvest.jfall2019.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blueharvest.jfall2019.handler.CommandHandler;
import io.blueharvest.jfall2019.handler.EventHandler;
import io.blueharvest.jfall2019.kafka.serde.AccountDeserializer;
import io.blueharvest.jfall2019.kafka.serde.AccountSerializer;
import io.blueharvest.jfall2019.entity.Account;
import io.blueharvest.jfall2019.util.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamConfig {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamConfig.class);

    @Value("${kafka-streams.applicationId}")
    private String applicationId;

    @Value("${kafka.bootstrapAddress}")
    private String bootstrapServers;

    @Value("${kafka.topic.account.command}")
    String kafkaTopicAccountCommand;

    @Value("${kafka.topic.event.account.created}")
    String kafkaTopicAccountCreated;

    @Value("${kafka.topic.event.money.deposited}")
    String kafkaTopicMoneyDeposited;

    @Value("${kafka.topic.event.money.withdrawn}")
    String kafkaTopicMoneyWithdrawn;

    @Value("${kafka.topic.account.snapshot}")
    String kafkaTopicAccountSnapshot;

    @Value("${kafka.store.account.snapshot}")
    String kafkaStoreAccountSnapshot;

    @Autowired
    ObjectMapper objectMapper;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfigs() {
        System.out.println("=====> in kafkaStreamsConfigs");
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        // Specify default (de)serializers for record keys and for record values.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<?, ?> kafkaStream(StreamsBuilder kStreamBuilder) {

        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
        final Serde<Account> accountSerde = Serdes.serdeFrom(new AccountSerializer(), new AccountDeserializer());

        final StoreBuilder<KeyValueStore<String, Account>> accountSnapshotStore = getAccountSnapshotStore(kafkaStoreAccountSnapshot, accountSerde);
        kStreamBuilder.addStateStore(accountSnapshotStore);

        LOG.info("Step-1: Define the input topic");

        KStream<String, JsonNode> stream = kStreamBuilder.stream(kafkaTopicAccountCommand, Consumed.with(Serdes.String(), jsonSerde));

        LOG.info("Step-2: Transformation : CommandHandler");

        //  check if it should be checked for expiration of a record against the store
        KStream<String, String> eventStream = stream.transformValues(() -> new CommandHandler(accountSnapshotStore.name()), accountSnapshotStore.name());

        LOG.info("Step-3: Transformation : EventHandler");

        eventStream.transformValues(() -> new EventHandler(accountSnapshotStore.name()), accountSnapshotStore.name())
                .to(kafkaTopicAccountSnapshot, Produced.with(Serdes.String(), Serdes.String()));

        LOG.info("Step-4: Publish Events");

        publishEventsInTopicsForOtherConsumers(eventStream);

        LOG.info("Stream started here...");
        return stream;
    }

    private StoreBuilder<KeyValueStore<String, Account>> getAccountSnapshotStore(String name, Serde<Account> accountSerde) {
        return Stores
                .keyValueStoreBuilder(Stores.persistentKeyValueStore(name), Serdes.String(), accountSerde)
                .withCachingEnabled();
    }

    private void publishEventsInTopicsForOtherConsumers(KStream<String, String> eventStream) {
        KStream<String, String>[] eventStreams = eventStream.branch(
                (key,value) -> ("AccountCreatedEvent".equals(JsonUtil.getFieldValue(value, "eventType"))),
                (key,value) -> ("MoneyDepositedEvent".equals(JsonUtil.getFieldValue(value, "eventType"))),
                (key,value) -> true
        );

        System.out.println("EventStream Initialized");

        // AccountCreatedEvent
        eventStreams[0].to(kafkaTopicAccountCreated, Produced.with(Serdes.String(), Serdes.String()));

        // MoneyDepositedEvent
        eventStreams[1].to(kafkaTopicMoneyDeposited, Produced.with(Serdes.String(), Serdes.String()));

        // MoneyWithdrawnEvent
        eventStreams[2].to(kafkaTopicMoneyWithdrawn, Produced.with(Serdes.String(), Serdes.String()));
    }


}
