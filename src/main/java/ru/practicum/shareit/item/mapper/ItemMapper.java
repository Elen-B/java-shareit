package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.model.BookingDates;
import ru.practicum.shareit.core.mapper.JsonNullableMapper;
import ru.practicum.shareit.item.dto.DatesDto;
import ru.practicum.shareit.item.dto.ItemDatesDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Class with methods to map Item to ItemDto/ItemUpdateDto and ItemDto/ItemUpdateDto to Item
 */

@Mapper(uses = JsonNullableMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "owner", source = "user")
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    Item map(ItemDto entity, UserDto user);

    ItemDto map(Item entity);

    @Mapping(target = "id", source = "entity.id")
    ItemDatesDto map(Item entity, DatesDto lastBooking, DatesDto nextBooking);

    DatesDto map(BookingDates dates);

    @Mapping(target = "id", source = "itemId")
    void update(ItemUpdateDto update, Long itemId, Long userId, @MappingTarget Item destination);
}