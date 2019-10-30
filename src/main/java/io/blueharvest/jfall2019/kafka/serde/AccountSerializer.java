package io.blueharvest.jfall2019.kafka.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.blueharvest.jfall2019.entity.Account;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class AccountSerializer implements Serializer<Account> {

    @Override public void configure(Map<String, ?> map, boolean b) { }

    @Override public byte[] serialize(String topic, Account data) {
        byte[] retVal = null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retVal;
    }

    @Override public void close() { }
}