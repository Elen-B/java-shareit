package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.core.mapper.JsonNullableMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

/**
 * Class with methods to map Item to ItemDto/ItemUpdateDto and ItemDto/ItemUpdateDto to Item
 */

@Mapper(uses = JsonNullableMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "ownerId", source = "userId")
    Item map(ItemDto entity, Long userId);

    ItemDto map(Item entity);

    @Mapping(target = "id", source = "itemId")
    @Mapping(target = "ownerId", source = "userId")
    Item map(ItemUpdateDto entity, Long itemId, Long userId);

    @InheritConfiguration
    void update(ItemUpdateDto update, @MappingTarget Item destination);
}