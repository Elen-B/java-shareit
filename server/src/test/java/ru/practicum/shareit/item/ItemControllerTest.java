package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemDto itemDto2;
    private DatesDto datesDto;
    private DatesDto datesDto2;
    private ItemDatesDto itemDatesDto;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemDto = new ItemDto(
                3L,
                "item first",
                "description for item first",
                true,
                5L
        );

        datesDto = new DatesDto(LocalDateTime.now().toString(), LocalDateTime.now().plusHours(1).toString());
        datesDto2 = new DatesDto(LocalDateTime.now().minusDays(1).toString(), LocalDateTime.now().toString());
        commentRequestDto = new CommentRequestDto("new comment for item");
        commentResponseDto = new CommentResponseDto(
                100L,
                "comment text",
                "author",
                LocalDateTime.now().minusDays(1).toString()
        );
        itemDatesDto = new ItemDatesDto(
                1L,
                "item name",
                "description",
                true,
                null,
                datesDto2,
                datesDto,
                List.of(commentResponseDto)
        );
    }

    @Test
    @DisplayName(value = "GET /items/{itemId}")
    void shouldGetByIdTest() throws Exception {
        when(itemService.getItemDateDtoById(any()))
                .thenReturn(itemDatesDto);

        mvc.perform(get("/items/{itemId}", itemDatesDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDatesDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDatesDto.getName())))
                .andExpect(jsonPath("$.lastBooking", notNullValue()))
                .andExpect(jsonPath("$.nextBooking", notNullValue()))
                .andExpect(jsonPath("$.comments", hasSize(1)));
    }

    @Test
    @DisplayName(value = "GET /items")
    void shouldGetItemsByOwnerIdTest() throws Exception {
        when(itemService.getByOwnerId(any()))
                .thenReturn(List.of(itemDatesDto, itemDatesDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName(value = "POST /items")
    void shouldAddItemTest() throws Exception {
        when(itemService.add(any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    @DisplayName(value = "PATCH /items/{itemId}")
    void shouldNotUpdateTest() throws Exception {
        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(null))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName(value = "GET /items/search")
    void shouldSearchItemsTest() throws Exception {
        when(itemService.search(any()))
                .thenReturn(List.of(itemDto, itemDto, itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    @Test
    @DisplayName(value = "POST /items/{itemId}/comment")
    void shouldAddCommentTest() throws Exception {
        when(itemService.addComment(any(), any(), any()))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .header("X-Sharer-User-Id", 5L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName())));
    }
}