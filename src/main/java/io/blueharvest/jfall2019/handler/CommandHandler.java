package io.blueharvest.jfall2019.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blueharvest.jfall2019.command.CreateAccountCommand;
import io.blueharvest.jfall2019.command.DepositMoneyCommand;
import io.blueharvest.jfall2019.command.WithdrawMoneyCommand;
import io.blueharvest.jfall2019.event.AccountCreatedEvent;
import io.blueharvest.jfall2019.event.MoneyDepositedEvent;
import io.blueharvest.jfall2019.event.MoneyWithdrawnEvent;
import io.blueharvest.jfall2019.entity.Account;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class CommandHandler implements ValueTransformer<JsonNode, String> {

    private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);

    final private String storeName;
    private KeyValueStore<String, Account> stateStore;
    private ProcessorContext context;

    public CommandHandler(final String storeName) {
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
    public String transform(JsonNode jsonAsString) {
        LOG.info("In transform. Input [{}]", jsonAsString);
        String resultJson = null;

        if(jsonAsString != null) {
            LOG.info("In transform. ClassType [{}]", jsonAsString.getClass().getName());

            Account account = null;
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode jsonTree = objectMapper.readTree(jsonAsString.asText());
                String command = jsonTree.get("commandType").asText();
                LOG.info("commandType: [{}]",command);


                switch (command) {
                    case "CreateAccountCommand":
                        LOG.info("Case: CreateAccountCommand");
                        CreateAccountCommand createAccountCommand = objectMapper.treeToValue(jsonTree, CreateAccountCommand.class);

                        if (stateStore.get(createAccountCommand.getId()) != null) {
                            throw new RuntimeException("an account witht the id " + createAccountCommand.getId()+ " already exists!");
                        } else {
                            AccountCreatedEvent ace = new AccountCreatedEvent(createAccountCommand.getId(), createAccountCommand.getFirstName(), createAccountCommand.getBalance());
                            resultJson = objectMapper.writeValueAsString(ace);
                        }

                        break;
                    case "DepositMoneyCommand":
                        LOG.info("Case: DepositMoneyCommand");
                        DepositMoneyCommand dmc = objectMapper.treeToValue(jsonTree, DepositMoneyCommand.class);

                        account = stateStore.get(dmc.getId());
                        if (account != null) {
                            MoneyDepositedEvent mde = new MoneyDepositedEvent(dmc.getId(), dmc.getAmount());
                            resultJson = objectMapper.writeValueAsString(mde);
                        }
                        break;
                    case "WithdrawMoneyCommand":
                        LOG.info("Case: WithdrawMoneyCommand");
                        WithdrawMoneyCommand wmc = objectMapper.treeToValue(jsonTree, WithdrawMoneyCommand.class);

                        account = stateStore.get(wmc.getId());
                        if (account != null) {
                            if (account.getBalance() > wmc.getAmount()) {
                                MoneyWithdrawnEvent mwe = new MoneyWithdrawnEvent(wmc.getId(), wmc.getAmount());
                                resultJson = objectMapper.writeValueAsString(mwe);
                            }
                        }
                        break;
                }
            } catch (IOException e) {
                LOG.error("Error [{}]", e.getMessage());
            }
            LOG.info("Result: [{}]", resultJson);

        }
        return resultJson;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
    }
}
