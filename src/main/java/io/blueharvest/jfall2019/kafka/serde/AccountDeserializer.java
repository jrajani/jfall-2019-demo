package io.blueharvest.jfall2019.kafka.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.blueharvest.jfall2019.entity.Account;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class AccountDeserializer implements Deserializer<Account> {

    @Override public void close() { }

    @Override public void configure(Map<String, ?> arg0, boolean arg1) { }

    @Override
    public Account deserialize(String toipic, byte[] data) {

        ObjectMapper mapper = new ObjectMapper();

        Account account = null;

        try {
            account = mapper.readValue(data, Account.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }
}
