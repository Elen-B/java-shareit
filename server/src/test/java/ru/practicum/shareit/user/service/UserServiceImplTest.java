package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;
    private final UserMapper mapper;

    @Test
    @DisplayName(value = "Получение пользователя по ИД")
    void getUserByIdTest() {
        UserDto userDto = makeUserDto("User", "some@email.com");
        UserDto savedUserDto = service.add(userDto);

        UserDto result = service.getById(savedUserDto.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(savedUserDto.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @DisplayName(value = "Добавление пользователя")
    void addUserTest() {
        UserDto userDto = makeUserDto("User", "some@email.com");
        service.add(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @DisplayName(value = "Успешное обновление почты пользователя")
    void updateEmailForUserTest() {
        UserDto userDto = makeUserDto("User", "some@email.com");
        UserDto savedUserDto = service.add(userDto);
        String newEmail = "another@email.com";
        UserUpdateDto userUpdateDto = makeUserUpdateDto(null, newEmail);
        service.update(userUpdateDto, savedUserDto.getId());
        UserDto result = service.getById(savedUserDto.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(savedUserDto.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(newEmail));
    }

    @Test
    @DisplayName(value = "Удаление пользователя")
    void deleteUserTest() {
        UserDto userDto = makeUserDto("User", "some@email.com");
        UserDto savedUserDto = service.add(userDto);
        service.delete(savedUserDto.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);

        Assertions.assertThrows(NoResultException.class, () -> query.setParameter("email", userDto.getEmail())
                .getSingleResult());
    }

    @Test
    @DisplayName(value = "Получение несуществующего пользователя")
    void throwExceptionWhenGetUserByIdTest() {
        Assertions.assertThrows(NotFoundException.class, () -> service.getUserById(999999L));
    }

    @Test
    @DisplayName(value = "Успешная проверка существования пользователя")
    void returnTrueForExistedUserTest() {
        UserDto userDto = makeUserDto("User", "some@email.com");
        UserDto savedUserDto = service.add(userDto);
        Boolean userExists = service.existsUser(savedUserDto.getId());

        assertTrue(userExists);
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userDto;
    }

    private UserUpdateDto makeUserUpdateDto(String name, String email) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        if (name != null) {
            userUpdateDto.setName(JsonNullable.of(name));
        }
        userUpdateDto.setEmail(JsonNullable.of(email));
        return userUpdateDto;
    }
}