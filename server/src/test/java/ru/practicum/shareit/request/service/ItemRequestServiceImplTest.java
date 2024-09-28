package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final EntityManager em;
    private final ItemRequestService service;
    private final UserService userService;

    @Test
    @DisplayName(value = "Добавление запроса на позицию")
    void addItemRequestTest() {
        ItemRequestDto itemRequestDto = makeItemRequestDto("new item request");
        UserDto userDto = userService.add(makeUserDto("user", "user@mail.ru"));
        ItemRequestResponseDto result = service.add(itemRequestDto, userDto.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select u from ItemRequest u where u.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", result.getId())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequestor().getId(), equalTo(userDto.getId()));
    }

    @Test
    @DisplayName(value = "Получение всех запросов")
    void getAllItemRequestsTest() {
        UserDto userDto = userService.add(makeUserDto("user", "user@mail.ru"));
        ItemRequestDto itemRequestDto = makeItemRequestDto("new item request");
        ItemRequestDto itemRequestDto2 = makeItemRequestDto("another item request");
        ItemRequestDto itemRequestDto3 = makeItemRequestDto("third item request");
        service.add(itemRequestDto, userDto.getId());
        service.add(itemRequestDto2, userDto.getId());
        service.add(itemRequestDto3, userDto.getId());

        Collection<ItemRequestResponseDto> result = service.getAll();
        assertThat(result, hasSize(3));
    }

    @Test
    @DisplayName(value = "Получение запросов по ид пользователя")
    void getItemRequestsByRequestorIdTest() {
        UserDto userDto = userService.add(makeUserDto("user", "user@mail.ru"));
        UserDto userDto2 = userService.add(makeUserDto("other user", "user2@mail.ru"));
        ItemRequestDto itemRequestDto = makeItemRequestDto("new item request");
        ItemRequestDto itemRequestDto2 = makeItemRequestDto("another item request");
        service.add(itemRequestDto, userDto.getId());
        service.add(itemRequestDto2, userDto2.getId());

        List<ItemRequestResponseDto> result = service.getByRequestorId(userDto.getId()).stream().toList();
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDescription(), is(itemRequestDto.getDescription()));
        assertThat(result.get(0).getRequestorId(), is(userDto.getId()));
    }

    @Test
    @DisplayName(value = "Получение отдельного запроса ИД")
    void getItemRequestByIdTest() {
        UserDto userDto = userService.add(makeUserDto("user", "user@mail.ru"));
        ItemRequestDto itemRequestDto = makeItemRequestDto("new item request");
        ItemRequestResponseDto addedItemRequest = service.add(itemRequestDto, userDto.getId());
        ItemRequestResponseDto result = service.getById(addedItemRequest.getId());

        assertThat(result.getId(), notNullValue());
        assertThat(result.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(result.getRequestorId(), equalTo(userDto.getId()));
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        return new ItemRequestDto(description);
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userDto;
    }
}