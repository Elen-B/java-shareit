package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingDates;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.core.exception.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

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
    public ItemDatesDto getItemDateDtoById(Long itemId) {
        Item item = getItemById(itemId);
        Collection<CommentResponseDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(itemMapper::map)
                .toList();

        return itemMapper.map(item, null, null, comments);
    }

    @Override
    public Collection<ItemDatesDto> getByOwnerId(Long ownerId) {
        Map<Long, BookingDates> lastBookings = bookingRepository
                .lastBookings(LocalDateTime.now(), ownerId)
                .stream()
                .collect(Collectors.toMap(BookingDates::getItemId, Function.identity()));

        Map<Long, BookingDates> nextBookings = bookingRepository
                .nextBookings(LocalDateTime.now(), ownerId)
                .stream()
                .collect(Collectors.toMap(BookingDates::getItemId, Function.identity()));

        Collection<Item> items = itemRepository.findByOwnerId(ownerId);

        Map<Long, List<CommentResponseDto>> mapComments = commentRepository
                .findByItemIdIn(items.stream().map(Item::getId).collect(toList()))
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        Collectors.mapping(itemMapper::map, toList())));

        return items.stream().map(item -> itemMapper.map(
                        item,
                        itemMapper.map(lastBookings.get(item.getId())),
                        itemMapper.map(nextBookings.get(item.getId())),
                        mapComments.get(item.getId())))
                .collect(toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        Collection<Item> items = itemRepository.search(text);
        return items.stream().map(itemMapper::map).collect(toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String.format("Позиция с ид %s не найдена", itemId)));
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long itemId, Long authorId, CommentRequestDto commentDto) {
        ItemDto item = getById(itemId);
        UserDto author = userService.getById(authorId);
        log.info("LocalDateTime.now() = " + LocalDateTime.now());
        if (bookingRepository.findByItemIdAndBookerIdAndEndDateBeforeAndStatus(itemId, authorId, LocalDateTime.now(), BookingStatus.APPROVED).isEmpty()) {
            throw new WrongArgumentException("Не найдено успешное бронирование позиции");
        }

        Comment comment = commentRepository.save(itemMapper.map(commentDto, item, author, LocalDateTime.now()));
        return itemMapper.map(comment);
    }

    @Override
    public Collection<ItemOwnerDto> getByRequestId(Long requestId) {
        return itemRepository.findByRequestId(requestId)
                .stream()
                .map(itemMapper::mapToItemOwnerDto)
                .collect(toList());
    }

    @Override
    public Map<Long, List<ItemOwnerDto>> getByRequestIds(Collection<Long> requestIds) {
        return itemRepository
                .findByRequestIdIn(requestIds.stream().toList())
                .stream()
                .collect(Collectors.groupingBy(Item::getRequestId,
                        Collectors.mapping(itemMapper::mapToItemOwnerDto, toList())));
    }
}
