package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.model.BookingDates;
import ru.practicum.shareit.core.mapper.JsonNullableMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collection;

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

    @Mapping(target = "author", source = "author")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "createDate", source = "date")
    @Mapping(target = "id", ignore = true)
    Comment map(CommentRequestDto text, ItemDto item, UserDto author, LocalDateTime date);

    ItemDto map(Item entity);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "ownerId", source = "entity.owner.id")
    ItemOwnerDto mapToItemOwnerDto(Item entity);

    @Mapping(target = "id", source = "entity.id")
    ItemDatesDto map(Item entity, DatesDto lastBooking, DatesDto nextBooking, Collection<CommentResponseDto> comments);

    DatesDto map(BookingDates dates);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "authorName", source = "entity.author.name")
    @Mapping(target = "created", source = "entity.createDate")
    CommentResponseDto map(Comment entity);

    @Mapping(target = "id", source = "itemId")
    void update(ItemUpdateDto update, Long itemId, Long userId, @MappingTarget Item destination);
}