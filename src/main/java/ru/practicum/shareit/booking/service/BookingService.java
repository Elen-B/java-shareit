package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

@Transactional(readOnly = true)
public interface BookingService {

    @Transactional
    BookingResponseDto add(BookingRequestDto bookingRequestDto, Long userId);

    @Transactional
    BookingResponseDto setStatus(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto getById(Long bookingId, Long userId);

    Collection<BookingResponseDto> getByUserAndState(Long userId, BookingState state);

    Collection<BookingResponseDto> getByOwnerAndState(Long ownerId, BookingState state);
}
