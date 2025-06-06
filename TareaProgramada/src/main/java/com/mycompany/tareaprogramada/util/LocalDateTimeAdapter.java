// src/main/java/com/mycompany/tareaprogramada/util/LocalDateTimeAdapter.java
package com.mycompany.tareaprogramada.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * (De)serializa LocalDateTime como cadena ISO (yyyy-MM-dd'T'HH:mm:ss).
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime fechaHora, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(fechaHora.format(FORMATO)); // “2025-06-06T14:30:00”
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), FORMATO);
    }
}
