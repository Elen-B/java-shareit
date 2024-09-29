package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    JsonNullable<@NotBlank String> name = JsonNullable.undefined();

    JsonNullable<@NotBlank String> description = JsonNullable.undefined();

    JsonNullable<@NotNull Boolean> available = JsonNullable.undefined();

}