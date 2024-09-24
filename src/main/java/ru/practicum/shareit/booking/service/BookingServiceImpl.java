package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.core.exception.WrongArgumentException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingResponseDto add(BookingRequestDto bookingRequestDto, Long userId) {
        UserDto booker = userService.getById(userId);
        ItemDto item = itemService.getById(bookingRequestDto.getItemId());

        if (!item.getAvailable()) {
            throw new WrongArgumentException("Позиция недоступна для бронирования");
        }

        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new WrongArgumentException("Дата конца бронирования должна быть после даты начала");
        }
        Booking booking = bookingMapper.map(bookingRequestDto, item, booker);
        booking = bookingRepository.save(booking);

        return bookingMapper.map(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto setStatus(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Бронирование с ид %s не найдено", bookingId))
        );

        if (!userService.existsUser(userId)) {
            throw new WrongArgumentException("неверное ид пользователя");
        }
        UserDto owner = userService.getById(userId);
        if (!Objects.equals(owner.getId(), booking.getItem().getOwner().getId())) {
            throw new WrongArgumentException("Изменять статус бронирования может только владелец позиции");
        }

        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        booking = bookingRepository.save(booking);

        return bookingMapper.map(booking);
    }

    @Override
    public BookingResponseDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Бронирование с ид %s не найдено", bookingId))
        );

        if (!Objects.equals(booking.getBooker().getId(), userId) &&
                !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new WrongArgumentException("Позиция доступна для просмотра только арендатором либо владельцем");
        }

        return bookingMapper.map(booking);
    }

    @Override
    public Collection<BookingResponseDto> getByUserAndState(Long userId, BookingState state) {
        if (!userService.existsUser(userId)) {
            throw new WrongArgumentException("неверное ид пользователя");
        }
        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerId(userId, Sort.by("endDate"));
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(userId, LocalDateTime.now(), LocalDateTime.now(), Sort.by("endDate"));
            case PAST ->
                    bookingRepository.findByBookerIdAndEndDateBefore(userId, LocalDateTime.now(), Sort.by("endDate"));
            case FUTURE ->
                    bookingRepository.findByBookerIdAndStartDateAfter(userId, LocalDateTime.now(), Sort.by("endDate"));
            case WAITING ->
                    bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, Sort.by("endDate"));
            case REJECTED ->
                    bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, Sort.by("endDate"));
        };

        return bookings.stream().map(bookingMapper::map).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingResponseDto> getByOwnerAndState(Long ownerId, BookingState state) {
        if (!userService.existsUser(ownerId)) {
            throw new NotFoundException("неверное ид пользователя");
        }
        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findBookingByItem_OwnerId(ownerId, Sort.by("endDate"));
            case CURRENT -> bookingRepository.findBookingByStartDateBeforeAndEndDateAfterAndItem_OwnerId(
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    ownerId,
                    Sort.by("endDate"));
            case PAST ->
                    bookingRepository.findBookingByEndDateBeforeAndItem_OwnerId(LocalDateTime.now(), ownerId, Sort.by("endDate"));
            case FUTURE ->
                    bookingRepository.findBookingByStartDateAfterAndItem_OwnerId(LocalDateTime.now(), ownerId, Sort.by("endDate"));
            case WAITING ->
                    bookingRepository.findBookingByStatusAndItem_OwnerId(BookingStatus.WAITING, ownerId, Sort.by("endDate"));
            case REJECTED ->
                    bookingRepository.findBookingByStatusAndItem_OwnerId(BookingStatus.REJECTED, ownerId, Sort.by("endDate"));
        };

        return bookings.stream().map(bookingMapper::map).collect(Collectors.toList());
    }
}
