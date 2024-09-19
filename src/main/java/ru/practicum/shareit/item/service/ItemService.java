package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Transactional(readOnly = true)
public interface ItemService {

    @Transactional
    ItemDto add(ItemDto itemDto, Long userId);

    @Transactional
    ItemDto update(ItemUpdateDto itemUpdateDto, Long itemId, Long userId);

    ItemDto getById(Long itemId);

    Collection<ItemDto> getByOwnerId(Long ownerId);

    Collection<ItemDto> search(String text);

    Item getItemById(Long itemId);
}
