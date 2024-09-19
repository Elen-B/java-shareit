package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of ItemService that implement operations with items using ItemRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto add(ItemDto itemDto, Long userId) {
        UserDto user = userService.getById(userId);
        Item item = itemMapper.map(itemDto, user);
        item = itemRepository.save(item);
        return itemMapper.map(item);
    }

    @Override
    @Transactional
    public ItemDto update(ItemUpdateDto itemUpdateDto, Long itemId, Long userId) {
        log.info("ItemServiceImpl/update args: {}, {}, {}", itemUpdateDto, itemId, userId);
        if (itemId == null) {
            throw new ConditionsNotMetException("Id позиции должен быть указан");
        }
        Item oldItem = getItemById(itemId);
        if (!Objects.equals(userId, oldItem.getOwner().getId())) {
            throw new AccessException("Обновлять позицию может только владелец");
        }
        itemMapper.update(itemUpdateDto, itemId, userId, oldItem);
        log.info("ItemServiceImpl/update map update: {}", oldItem);
        oldItem = itemRepository.save((oldItem));
        log.info("ItemServiceImpl/update result: {}", oldItem);
        return itemMapper.map(oldItem);
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemMapper.map(getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getByOwnerId(Long ownerId) {
        Collection<Item> items = itemRepository.findByOwnerId(ownerId);
        return items.stream()
                .map(itemMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        Collection<Item> items = itemRepository.search(text);
        return items.stream()
                .map(itemMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Позиция с ид %s не найдена", itemId))
        );
    }
}
