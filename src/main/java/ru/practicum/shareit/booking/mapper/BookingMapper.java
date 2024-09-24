package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.core.mapper.JsonNullableMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(uses = JsonNullableMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "startDate", source = "entity.start")
    @Mapping(target = "endDate", source = "entity.end")
    @Mapping(target = "id", ignore = true)
    Booking map(BookingRequestDto entity, ItemDto item, UserDto booker);

    @Mapping(target = "start", source = "entity.startDate")
    @Mapping(target = "end", source = "entity.endDate")
    BookingResponseDto map(Booking entity);
}
