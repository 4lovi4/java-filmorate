package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class RatingEnumDeserializer extends StdDeserializer<Rating> {

    public RatingEnumDeserializer(Class<?> c) {
        super(c);
    }

    @Override
    public Rating deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int enumId = (Integer) node.get("id").numberValue();
        return Rating.valueOfId(enumId);
    }
}
