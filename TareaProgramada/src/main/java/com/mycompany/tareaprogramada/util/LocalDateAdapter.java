// src/main/java/com/mycompany/tareaprogramada/util/LocalDateAdapter.java
package com.mycompany.tareaprogramada.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * (De)serializa LocalDate como cadena ISO (yyyy-MM-dd).
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    @Override
    public JsonElement serialize(LocalDate fecha, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(fecha.toString()); // “2025-06-06”
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDate.parse(json.getAsString());
    }
}
