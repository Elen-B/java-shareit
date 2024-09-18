package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> add(Item item);

    Optional<Item> update(Item item);

    Optional<Item> getById(Long itemId);

    Collection<Item> getByOwnerId(Long ownerId);

    Collection<Item> search(String text);
}
