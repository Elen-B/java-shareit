package ru.practicum.shareit.request.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.core.mapper.JsonNullableMapper;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(uses = JsonNullableMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(target = "description", source = "itemRequestDto.description")
    @Mapping(target = "requestor", source = "requestor")
    @Mapping(target = "createdDate", source = "date")
    @Mapping(target = "id", ignore = true)
    ItemRequest map(ItemRequestDto itemRequestDto, UserDto requestor, LocalDateTime date);

    @Mapping(target = "requestorId", source = "entity.requestor.id")
    @Mapping(target = "created", source = "entity.createdDate")
    ItemRequestResponseDto map(ItemRequest entity, List<ItemOwnerDto> items);
}
