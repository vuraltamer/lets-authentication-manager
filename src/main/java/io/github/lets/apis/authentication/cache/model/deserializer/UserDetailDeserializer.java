package io.github.lets.apis.authentication.cache.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.lets.apis.authentication.mapper.Mapper;
import io.github.lets.apis.authentication.cache.model.UserDetailCache;

import java.io.IOException;

public class UserDetailDeserializer extends JsonDeserializer<UserDetailCache> {

    @Override
    public UserDetailCache deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = Mapper.getInstance().readTree(parser);

        String className = node.get("className").asText();
        JsonNode principal = node.get("principal");
        try {
            Class<?> clazz = Class.forName(className);
            Object detail = Mapper.getInstance().treeToValue(principal, clazz);
            return new UserDetailCache(className, detail);
        } catch (ClassNotFoundException e) {
            throw new IOException("Class not found for deserialization: " + className, e);
        }
    }
}