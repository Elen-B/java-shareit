package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Dto class for User to use in PATCH-requests
 */
@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserUpdateDto {
    JsonNullable<@NotBlank String> name = JsonNullable.undefined();

    JsonNullable<@NotBlank @Email String> email = JsonNullable.undefined();
}
