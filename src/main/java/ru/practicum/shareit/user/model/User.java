package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * describes users who own, book and request items
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
