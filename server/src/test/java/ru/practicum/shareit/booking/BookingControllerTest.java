package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;
    private BookingResponseDto bookingResponseDto2;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        bookingRequestDto = new BookingRequestDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        bookingResponseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now().plusHours(1).toString(),
                LocalDateTime.now().plusHours(2).toString(),
                BookingStatus.APPROVED,
                new UserDto(
                        1L,
                        "user",
                        "user@mail.ru"
                ),
                new ItemDto(
                        1L,
                        "item",
                        "item description",
                        true,
                        null
                )
        );

        bookingResponseDto2 = new BookingResponseDto(
                2L,
                LocalDateTime.now().plusHours(1).toString(),
                LocalDateTime.now().plusHours(2).toString(),
                BookingStatus.WAITING,
                new UserDto(
                        1L,
                        "user",
                        "user@mail.ru"
                ),
                new ItemDto(
                        2L,
                        "new item",
                        "new item description",
                        true,
                        null
                )
        );
    }

    @Test
    @DisplayName(value = "POST /bookings")
    void shouldAddBookingTest() throws Exception {
        when(bookingService.add(bookingRequestDto, bookingResponseDto.getBooker().getId()))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", bookingResponseDto.getBooker().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())));
    }

    @Test
    @DisplayName(value = "PATCH /bookings/{bookingId}")
    void shouldSetApprovedStatusTest() throws Exception {
        when(bookingService.setStatus(any(), any(), any()))
                .thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingResponseDto.getId())
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", bookingResponseDto.getBooker().getId())
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    @DisplayName(value = "GET /bookings")
    void shouldGetAllTest() throws Exception {
        when(bookingService.getByUserAndState(any(), any()))
                .thenReturn(List.of(bookingResponseDto2, bookingResponseDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookingResponseDto.getBooker().getId())
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("WAITING")))
                .andExpect(jsonPath("$[1].status", is("APPROVED")));
    }

    @Test
    @DisplayName(value = "GET /bookings/owner")
    void shouldNotGetAllByOwnerTest() throws Exception {
        when(bookingService.getByOwnerAndState(any(), any()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 99L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName(value = "GET /bookings/{bookingId}")
    void shouldGetByIdTest() throws Exception {
        when(bookingService.getById(any(), any()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/{bookingId}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd())))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));
    }
}