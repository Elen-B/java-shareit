package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService{
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final ItemService itemService;
    @Override
    @Transactional
    public ItemRequestResponseDto add(ItemRequestDto itemRequestDto, Long userId) {
        UserDto requestor = userService.getById(userId);
        ItemRequest itemRequest = itemRequestMapper.map(itemRequestDto, requestor, LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.map(itemRequest, null);
    }

    @Override
    public Collection<ItemRequestResponseDto> getAll() {
        return itemRequestRepository.findAll(Sort.by("createdDate"))
                .stream()
                .map(itemRequest -> itemRequestMapper.map(itemRequest, null))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestResponseDto> getByRequestorId(Long requestorId) {
        Collection<ItemRequest> requests = itemRequestRepository.findByRequestorId(requestorId, Sort.by("createdDate"));
        Map<Long, List<ItemOwnerDto>> mapItems = itemService.getByRequestIds(requests.stream().map(ItemRequest::getId).toList());

        return requests.stream()
                .map(itemRequest -> itemRequestMapper.map(itemRequest, mapItems.get(itemRequest.getId())))
                .toList();
    }

    @Override
    public ItemRequestResponseDto getById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с ид %s не найдена", requestId)));
        List<ItemOwnerDto> items = itemService.getByRequestId(requestId).stream().toList();

        return itemRequestMapper.map(itemRequest, items);
    }
}
