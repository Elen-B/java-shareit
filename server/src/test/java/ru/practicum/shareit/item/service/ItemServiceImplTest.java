package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.exception.AccessException;
import ru.practicum.shareit.core.exception.ConditionsNotMetException;
import ru.practicum.shareit.core.exception.WrongArgumentException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService service;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Test
    @DisplayName(value = "Добавление позиции")
    void addItemTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = service.add(item, owner.getId());

        TypedQuery<Item> queryItem = em.createQuery("Select u from Item u where u.id = :id", Item.class);
        Item result = queryItem.setParameter("id", item.getId())
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getOwner().getId(), equalTo(owner.getId()));
    }

    @Test
    @DisplayName(value = "Обновление неизвестной позиции")
    void updateNullItemTest() {
        ItemUpdateDto itemUpdateDto = makeItemUpdateDto("updated item", null, null);
        assertThrows(ConditionsNotMetException.class, () -> service.update(itemUpdateDto, null, 1L));
    }

    @Test
    @DisplayName(value = "Обновление позиции не владельцем")
    void updateItemByWrongOwnerTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = service.add(item, owner.getId());
        Long id = item.getId();
        ItemUpdateDto itemUpdateDto = makeItemUpdateDto("updated item", null, null);
        assertThrows(AccessException.class, () -> service.update(itemUpdateDto, id, 199L));
    }

    @Test
    @DisplayName(value = "Обновление позиции")
    void updateItemTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = service.add(item, owner.getId());
        Long id = item.getId();
        Long ownerId = owner.getId();
        ItemUpdateDto itemUpdateDto = makeItemUpdateDto("updated item", null, null);
        service.update(itemUpdateDto, id, ownerId);

        TypedQuery<Item> queryItem = em.createQuery("Select u from Item u where u.id = :id", Item.class);
        Item result = queryItem.setParameter("id", item.getId())
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(itemUpdateDto.getName().get()));
    }

    @Test
    @DisplayName(value = "Получение позиции по ид")
    void getItemByIdTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = service.add(item, owner.getId());

        ItemDto result = service.getById(item.getId());

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    @DisplayName(value = "Получение позиции с данными бронирования по ид")
    void getItemDateDtoById() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = service.add(item, owner.getId());

        ItemDatesDto result = service.getItemDateDtoById(item.getId());

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getLastBooking(), nullValue());
    }

    @Test
    @DisplayName(value = "Получение позиции с данными бронирования по ид")
    void getItemsByOwnerIdTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = service.add(item, owner.getId());

        List<ItemDatesDto> result = service.getByOwnerId(owner.getId()).stream().toList();

        assertThat(result, hasSize(1));
    }

    @Test
    @DisplayName(value = "Поиск позиций")
    void searchItemsTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        ItemDto item2 = makeItemDto(null, "not for search", "description");
        ItemDto item3 = makeItemDto(null, "new item", "description");
        item = service.add(item, owner.getId());
        item2 = service.add(item2, owner.getId());
        item3 = service.add(item3, owner.getId());

        List<ItemDto> result =  service.search("item").stream().toList();

        assertThat(result, hasSize(2));
    }

    @Test
    @DisplayName(value = "Получение позиции по ид")
    void getExistItemByIdTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = service.add(item, owner.getId());

        Item result = service.getItemById(item.getId());
        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getOwner().getId(), equalTo(owner.getId()));
    }

    @Test
    @DisplayName(value = "Добавление комментария без бронирования")
    void addCommentForNoBookingTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        UserDto booker = userService.add(makeUserDto("booker", "booker@mail.ru"));
        ItemDto item = makeItemDto(null, "item", "description");
        item = service.add(item, owner.getId());
        Long id = item.getId();

        assertThrows(WrongArgumentException.class, () ->
                service.addComment(id, booker.getId(), makeCommentRequestDto("wonderful item!")));
    }

    @Test
    @DisplayName(value = "Получение позиций по списку запросов")
    void getItemByRequestIdTest() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.add(makeItemRequestDto("search item"), owner.getId());
        ItemDto item = makeItemDto(null, "item", "description");
        item.setRequestId(itemRequestResponseDto.getId());
        item = service.add(item, owner.getId());

        List<ItemOwnerDto> result = service.getByRequestId(itemRequestResponseDto.getId()).stream().toList();
        assertThat(result, hasSize(1));
    }

    @Test
    void getByRequestIds() {
        UserDto owner = userService.add(makeUserDto("owner", "user@mail.ru"));
        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.add(makeItemRequestDto("search item"), owner.getId());
        ItemDto item = makeItemDto(null, "item", "description");
        item.setRequestId(itemRequestResponseDto.getId());
        item = service.add(item, owner.getId());

        Map<Long, List<ItemOwnerDto>> result = service.getByRequestIds(List.of(itemRequestResponseDto.getId()));
        assertThat(result.get(itemRequestResponseDto.getId()), hasSize(1));
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

        return itemDto;
    }

    private ItemUpdateDto makeItemUpdateDto(String name, String description, Boolean avaialble) {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName(JsonNullable.of(name));
        itemUpdateDto.setDescription(JsonNullable.of(description));
        itemUpdateDto.setAvailable(JsonNullable.of(avaialble));

        return itemUpdateDto;
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        return new ItemRequestDto(description);
    }

    private CommentRequestDto makeCommentRequestDto(String description) {
        return new CommentRequestDto(description);
    }
}