package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemUpdateDto itemUpdateDto, Long itemId, Long userId);

    ItemDto getById(Long itemId);

    ItemDatesDto getItemDateDtoById(Long itemId);

    Collection<ItemDatesDto> getByOwnerId(Long ownerId);

    Collection<ItemDto> search(String text);

    Item getItemById(Long itemId);

    CommentResponseDto addComment(Long itemId, Long authorId, CommentRequestDto commentDto);

    Collection<ItemOwnerDto> getByRequestId(Long requestId);

    Map<Long, List<ItemOwnerDto>> getByRequestIds(Collection<Long> requestIds);
}
