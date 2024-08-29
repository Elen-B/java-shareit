package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collection;
import java.util.Objects;

/**
 * Implementation of ItemService that implement operations with items using ItemRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;

    @Override
    public Item add(Item item) {
        return itemRepository.add(item).orElseThrow(() -> new InternalServerException(
                "Ошибка создания позиции"));
    }

    @Override
    public Item update(Item item) {
        log.error(item.toString());
        if (item.getId() == null) {
            throw new ConditionsNotMetException("Id позиции должен быть указан");
        }
        Item oldItem = getById(item.getId());
        if (!Objects.equals(item.getOwnerId(), oldItem.getOwnerId())) {
            throw new AccessException("Обновлять позицию может только владелец");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return itemRepository.update(oldItem).orElseThrow(() -> new InternalServerException(
                "Ошибка создания позиции"));
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.getById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Позиция с ид %s не найдена", itemId))
        );
    }

    @Override
    public Collection<Item> getByOwnerId(Long ownerId) {
        return itemRepository.getByOwnerId(ownerId);
    }

    @Override
    public Collection<Item> search(String text) {
        return itemRepository.search(text);
    }
}
