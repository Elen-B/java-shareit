package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDatesDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    DatesDto lastBooking;
    DatesDto nextBooking;
    List<CommentResponseDto> comments;
}
