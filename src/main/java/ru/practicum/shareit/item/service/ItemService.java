package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemUpdateDto itemUpdateDto, Long itemId, Long userId);

    ItemDto getById(Long itemId);

    Collection<ItemDto> getByOwnerId(Long ownerId);

    Collection<ItemDto> search(String text);
}
