package com.github.bsamartins.springboot.notifications.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Optional;

public class MediaTypeDeserializer extends StdDeserializer<MediaType> {

    public MediaTypeDeserializer() {
        this(MediaType.class);
    }

    protected MediaTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MediaType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        return Optional.ofNullable(value)
                .map(MediaType::parseMediaType)
                .orElse(null);
    }
}
