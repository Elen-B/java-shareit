package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Dto class for User to use in PATCH-requests
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserUpdateDto {
    JsonNullable<String> name = JsonNullable.undefined();

    JsonNullable<String> email = JsonNullable.undefined();
}
