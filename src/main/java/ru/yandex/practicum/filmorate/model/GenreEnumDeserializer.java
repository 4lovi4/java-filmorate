package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class GenreEnumDeserializer extends StdDeserializer<Genre> {

    public GenreEnumDeserializer(Class<?> c) {
        super(c);
    }

    @Override
    public Genre deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int genreId = (Integer) node.get("id").numberValue();
        return Genre.valueOfId(genreId);
    }
}
