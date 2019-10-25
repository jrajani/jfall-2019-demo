package io.blueharvest.jfall2019.kafka.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.blueharvest.jfall2019.snapshot.AccountSnapshot;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class AccountSerializer implements Serializer<AccountSnapshot> {

    @Override public void configure(Map<String, ?> map, boolean b) { }

    @Override public byte[] serialize(String topic, AccountSnapshot data) {
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