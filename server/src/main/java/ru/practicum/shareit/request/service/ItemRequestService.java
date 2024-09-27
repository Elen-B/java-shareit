package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestResponseDto add(ItemRequestDto itemRequestDto, Long userId);

    Collection<ItemRequestResponseDto> getAll();

    Collection<ItemRequestResponseDto> getByRequestorId(Long requestorId);

    ItemRequestResponseDto getById(Long requestId);
}
