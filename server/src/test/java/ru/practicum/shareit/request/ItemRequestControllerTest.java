package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;
    private ItemRequestResponseDto itemRequestResponseDto;
    private ItemRequestResponseDto itemRequestResponseDto2;
    private ItemRequestResponseDto itemRequestResponseDto3;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemRequestDto = new ItemRequestDto(
                "description for wanted item");

        itemRequestResponseDto = new ItemRequestResponseDto(
                1L,
                itemRequestDto.getDescription(),
                LocalDateTime.now().toString(),
                99L,
                List.of(
                        new ItemOwnerDto(1L, "item 1", 7L),
                        new ItemOwnerDto(10L, "item 2", 1L)
                )
        );
        itemRequestResponseDto2 = new ItemRequestResponseDto(
                2L,
                itemRequestDto.getDescription(),
                LocalDateTime.now().toString(),
                109L,
                List.of(
                        new ItemOwnerDto(1L, "item 1", 7L)
                )
        );
        itemRequestResponseDto3 = new ItemRequestResponseDto(
                3L,
                itemRequestDto.getDescription(),
                LocalDateTime.now().plusHours(1).toString(),
                99L,
                null
        );
    }

    @Test
    @DisplayName(value = "POST /requests")
    void shouldAddItemRequestTest() throws Exception {
        when(itemRequestService.add(itemRequestDto, itemRequestResponseDto.getRequestorId()))
                .thenReturn(itemRequestResponseDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", itemRequestResponseDto.getRequestorId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(itemRequestResponseDto.getRequestorId()), Long.class));
    }

    @Test
    @DisplayName(value = "GET /requests/all")
    void shouldGetAllTest() throws Exception {
        when(itemRequestService.getAll())
                .thenReturn(List.of(itemRequestResponseDto3, itemRequestResponseDto2, itemRequestResponseDto));

        mvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto3.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto3.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestResponseDto3.getCreated())))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestResponseDto3.getRequestorId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemRequestResponseDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestResponseDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(itemRequestResponseDto2.getCreated())))
                .andExpect(jsonPath("$[1].requestorId", is(itemRequestResponseDto2.getRequestorId()), Long.class))
                .andExpect(jsonPath("$[2].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[2].description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$[2].created", is(itemRequestResponseDto.getCreated())))
                .andExpect(jsonPath("$[2].requestorId", is(itemRequestResponseDto.getRequestorId()), Long.class));
    }

    @Test
    @DisplayName(value = "GET /requests")
    void shouldGetByRequestorIdTest() throws Exception {
        when(itemRequestService.getByRequestorId(any()))
                .thenReturn(List.of(itemRequestResponseDto3, itemRequestResponseDto));

        mvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto3.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto3.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestResponseDto3.getCreated())))
                .andExpect(jsonPath("$[0].requestorId", is(itemRequestResponseDto3.getRequestorId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$[1].created", is(itemRequestResponseDto.getCreated())))
                .andExpect(jsonPath("$[1].requestorId", is(itemRequestResponseDto.getRequestorId()), Long.class));
    }

    @Test
    @DisplayName(value = "GET /requests/{requestId}")
    void shouldGetRequestByIdTest() throws Exception {
        when(itemRequestService.getById(any()))
                .thenReturn(itemRequestResponseDto);

        mvc.perform(get("/requests/{requestId}", itemRequestResponseDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestResponseDto.getCreated())))
                .andExpect(jsonPath("$.requestorId", is(itemRequestResponseDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestResponseDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemRequestResponseDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].ownerId", is(itemRequestResponseDto.getItems().get(0).getOwnerId()), Long.class))
                .andExpect(jsonPath("$.items[1].id", is(itemRequestResponseDto.getItems().get(1).getId()), Long.class))
                .andExpect(jsonPath("$.items[1].name", is(itemRequestResponseDto.getItems().get(1).getName())))
                .andExpect(jsonPath("$.items[1].ownerId", is(itemRequestResponseDto.getItems().get(1).getOwnerId()), Long.class));
    }
}