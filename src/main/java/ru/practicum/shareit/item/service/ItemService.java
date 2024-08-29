package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item add(Item item);

    Item update(Item item);

    Item getById(Long itemId);

    Collection<Item> getByOwnerId(Long ownerId);

    Collection<Item> search(String text);
}
