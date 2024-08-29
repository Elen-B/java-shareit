package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * TODO Sprint add-controllers.
 */

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
public class User {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    @Email
    String email;
}
