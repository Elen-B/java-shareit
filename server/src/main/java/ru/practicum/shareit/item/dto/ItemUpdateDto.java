package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.jackson.nullable.JsonNullable;

import java.io.Serializable;

/**
 * Dto class for Item to use in PATCH-requests
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ItemUpdateDto implements Serializable {

    JsonNullable<String> name = JsonNullable.undefined();

    JsonNullable<String> description = JsonNullable.undefined();

    JsonNullable<Boolean> available = JsonNullable.undefined();

}