package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.core.exception.WrongArgumentException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingService service;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    @DisplayName(value = "Добавление бронирования")
    void addBookingTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));

        BookingResponseDto result = service.add(bookingRequestDto, booker.getId());

        TypedQuery<Booking> query = em.createQuery("Select u from Booking u where u.id = :id", Booking.class);
        Booking booking = query.setParameter("id", result.getId())
                .getSingleResult();
        TypedQuery<Item> queryItem = em.createQuery("Select u from Item u where u.id = :id", Item.class);
        Item bookedItem = queryItem.setParameter("id", booking.getItem().getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStartDate(), equalTo(bookingRequestDto.getStart()));
        assertThat(booking.getEndDate(), equalTo(bookingRequestDto.getEnd()));
        assertThat(bookedItem.getOwner().getId(), equalTo(owner.getId()));
        assertThat(booking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    @DisplayName(value = "Добавление бронирования с некорректными датами")
    void addBookingIncorrectDatesTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(10), LocalDateTime.now().plusHours(1));

        assertThrows(WrongArgumentException.class, () -> service.add(bookingRequestDto, booker.getId()));
    }

    @Test
    @DisplayName(value = "Добавление бронирования для недоступной позиции")
    void addBookingNotAvailableItemTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item.setAvailable(false);
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));

        assertThrows(WrongArgumentException.class, () -> service.add(bookingRequestDto, booker.getId()));
    }

    @Test
    @DisplayName(value = "Подтверждение бронирования не владельцем")
    void setBookingStatusWithWrongOwnerTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));

        BookingResponseDto result = service.add(bookingRequestDto, booker.getId());

        assertThrows(WrongArgumentException.class, () -> service.setStatus(result.getId(), 999L, true));
    }

    @Test
    @DisplayName(value = "Получение данных о бронировании по ид")
    void getBookingByIdTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));

        BookingResponseDto addedBooking = service.add(bookingRequestDto, booker.getId());
        BookingResponseDto result = service.getById(addedBooking.getId(), booker.getId());

        assertThat(result.getId(), notNullValue());
        assertThat(result.getStart(), equalTo(bookingRequestDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(result.getEnd(), equalTo(bookingRequestDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(result.getItem().getId(), equalTo(item.getId()));
        assertThat(result.getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях для несуществующего пользователя")
    void getBookingsByNonExistUserAndStateTest() {
        assertThrows(WrongArgumentException.class, () -> service.getByUserAndState(999L, BookingState.ALL));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу ALL и создателю")
    void getBookingsByUserAndAllStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByUserAndState(booker.getId(), BookingState.ALL).stream().toList();

        assertThat(result, hasSize(2));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу FUTURE и создателю")
    void getBookingsByUserAndFutureStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByUserAndState(booker.getId(), BookingState.FUTURE).stream().toList();

        assertThat(result, hasSize(1));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу CURRENT и создателю")
    void getBookingsByUserAndCurrentStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByUserAndState(booker.getId(), BookingState.CURRENT).stream().toList();

        assertThat(result, hasSize(0));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу PAST и создателю")
    void getBookingsByUserAndPastStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "pastbooker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByUserAndState(booker.getId(), BookingState.PAST).stream().toList();

        assertThat(result, hasSize(1));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу WAITING и создателю")
    void getBookingsByUserAndWaitingStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "pastbooker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByUserAndState(booker.getId(), BookingState.WAITING).stream().toList();

        assertThat(result, hasSize(2));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу REJECTED и создателю")
    void getBookingsByUserAndRejectedStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "pastbooker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByUserAndState(booker.getId(), BookingState.REJECTED).stream().toList();

        assertThat(result, hasSize(0));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях для несуществующего владельца")
    void getBookingsByNonExistOwnerAndStateTest() {
        assertThrows(NotFoundException.class, () -> service.getByOwnerAndState(999L, BookingState.ALL));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу ALL и владельцу позиции")
    void getBookingsByOwnerAndAllStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByOwnerAndState(owner.getId(), BookingState.ALL).stream().toList();

        assertThat(result, hasSize(2));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу FUTURE и владельцу позиции")
    void getBookingsByOwnerAndFutureStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByOwnerAndState(owner.getId(), BookingState.FUTURE).stream().toList();

        assertThat(result, hasSize(1));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу CURRENT и владельцу позиции")
    void getBookingsByOwnerAndCurrentStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByOwnerAndState(owner.getId(), BookingState.CURRENT).stream().toList();

        assertThat(result, hasSize(0));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу PAST и владельцу позиции")
    void getBookingsByOwnerAndPastStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "pastbooker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByOwnerAndState(owner.getId(), BookingState.PAST).stream().toList();

        assertThat(result, hasSize(1));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу WAITING и владельцу позиции")
    void getBookingsByOwnerAndWaitingStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "pastbooker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByOwnerAndState(owner.getId(), BookingState.WAITING).stream().toList();

        assertThat(result, hasSize(2));
    }

    @Test
    @DisplayName(value = "Получение данных о бронированиях по статусу REJECTED и владельцу позиции")
    void getBookingsByOwnerAndRejectedStateTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "pastbooker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = itemService.add(item, owner.getId());
        BookingRequestDto bookingRequestDto = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(10));
        BookingRequestDto bookingRequestDto2 = makeBookingRequestDto(item.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        service.add(bookingRequestDto, booker.getId());
        service.add(bookingRequestDto2, booker.getId());

        List<BookingResponseDto> result = service.getByOwnerAndState(owner.getId(), BookingState.REJECTED).stream().toList();

        assertThat(result, hasSize(0));
    }

    private BookingRequestDto makeBookingRequestDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        return new BookingRequestDto(itemId, start, end);
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userDto;
    }

    private ItemDto makeItemDto(Long id, String name, String description) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        return  itemDto;
    }
}