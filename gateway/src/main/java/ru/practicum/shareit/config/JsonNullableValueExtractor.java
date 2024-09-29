package ru.practicum.shareit.config;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;
import org.openapitools.jackson.nullable.JsonNullable;


@UnwrapByDefault
public class JsonNullableValueExtractor implements ValueExtractor<JsonNullable<@ExtractedValue ?>> {
    @Override
    public void extractValues(JsonNullable<?> jsonNullable, ValueReceiver valueReceiver) {
        if (jsonNullable.isPresent()) {
            valueReceiver.value(null, jsonNullable.get());
        }
    }
}
