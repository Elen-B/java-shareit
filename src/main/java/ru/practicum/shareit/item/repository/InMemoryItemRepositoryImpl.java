package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ItemRepository that implement operations with items to book, such as add, get, search
 */
@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private static Integer globalId = 0;
    @Override
    public Optional<Item> add(Item item) {
        long id = getNextId();
        item.setId(id);
        items.put(item.getId(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> update(Item item) {
        if (items.containsKey(item.getId())) {
            Item oldItem = items.get(item.getId());
            oldItem.setName(item.getName());
            oldItem.setDescription(item.getDescription());
            oldItem.setAvailable(item.getAvailable());
            return Optional.of(oldItem);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> search(String text) {
        return items.values()
                .stream()
                .filter(item -> item.getAvailable() && !text.isEmpty())
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return ++globalId;
    }
}
