package io.blueharvest.jfall2019.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blueharvest.jfall2019.event.AccountCreatedEvent;
import io.blueharvest.jfall2019.event.MoneyDepositedEvent;
import io.blueharvest.jfall2019.entity.Account;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class EventHandler implements ValueTransformer<String, String> {

    private static final Logger LOG = LoggerFactory.getLogger(EventHandler.class);

    final private String storeName;
    private KeyValueStore<String, Account> stateStore;
    private ProcessorContext context;

    public EventHandler(final String storeName) {
        Objects.requireNonNull(storeName,"Store Name can't be null");
        this.storeName = storeName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.context = context;
        stateStore = (KeyValueStore) this.context.getStateStore(storeName);
    }

    @Override
    public String transform(String jsonAsString) {
        LOG.info("In transform. Input [{}]", jsonAsString);
        String resultJson=null;

        if(jsonAsString != null) {
            LOG.info("In transform. Class [{}]", jsonAsString.getClass().getName());

            Account account = null;

            final ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode jsonTree = objectMapper.readTree(jsonAsString);
                String eventType = jsonTree.get("eventType").asText();
                LOG.info("EventType: "+eventType);

                switch (eventType) {
                    case "AccountCreatedEvent":
                        AccountCreatedEvent ace = objectMapper.treeToValue(jsonTree, AccountCreatedEvent.class);
                        account = new Account(ace.getId(), ace.getBalance());

                        stateStore.put(ace.getId(), account);
                        break;
                    case "MoneyDepositedEvent":
                        MoneyDepositedEvent mde = objectMapper.treeToValue(jsonTree, MoneyDepositedEvent.class);
                        account = stateStore.get(mde.getId());

                        account.setBalance(account.getBalance() + mde.getAmount());

                        stateStore.put(mde.getId(), account);
                        break;
                    case "MoneyWithdrawnEvent":
                        MoneyDepositedEvent mwe = objectMapper.treeToValue(jsonTree, MoneyDepositedEvent.class);
                        account = stateStore.get(mwe.getId());

                        account.setBalance(account.getBalance() - mwe.getAmount());

                        stateStore.put(mwe.getId(), account);
                        break;
                }
                if (account != null) {
                    resultJson = objectMapper.writeValueAsString(account);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LOG.error("Error : [{}]",e.getMessage());
            }

        }
        LOG.info("Result: [{}]", resultJson);

        return resultJson;
    }


    @Override
    public void close() {
        // TODO Auto-generated method stub

    }


}