package io.blueharvest.jfall2019.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUtil {
    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

    public static String getFieldValue(String inputJson, String fieldName) {
        try {
            ObjectNode objectNode = new ObjectMapper().readValue(inputJson, ObjectNode.class);
            return objectNode.get(fieldName).textValue();
        } catch(IOException e) {
            LOG.error("Error [{}]",e.getMessage());
        }
        return "";
    }
}
