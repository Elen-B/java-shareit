package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingResponseDto add(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto setStatus(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto getById(Long bookingId, Long userId);

    Collection<BookingResponseDto> getByUserAndState(Long userId, BookingState state);

    Collection<BookingResponseDto> getByOwnerAndState(Long ownerId, BookingState state);
}
