package tr.com.beb.boardgame.utils;

import java.io.IOException;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private JsonUtils() {
    }

    public static String toJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Converting object to JSON failed", ex);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, clazz);
        } catch (IOException ex) {
            logger.error("String can not be converted to " + clazz.getName(), ex);
            return null;
        }
    }

    public static void write(Writer writer, Object value) throws IOException {
        new ObjectMapper().writeValue(writer, value);
    }

}
