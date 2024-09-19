package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Transactional(readOnly = true)
public interface ItemService {

    @Transactional
    ItemDto add(ItemDto itemDto, Long userId);

    @Transactional
    ItemDto update(ItemUpdateDto itemUpdateDto, Long itemId, Long userId);

    ItemDto getById(Long itemId);

    ItemDatesDto getItemDateDtoById(Long itemId);

    Collection<ItemDatesDto> getByOwnerId(Long ownerId);

    Collection<ItemDto> search(String text);

    Item getItemById(Long itemId);


    @Transactional
    CommentResponseDto addComment(Long itemId, Long authorId, CommentRequestDto commentDto);
}
