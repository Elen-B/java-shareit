package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    UserDto getById(Long userId);

    UserDto add(UserDto userDto);

    UserDto update(UserUpdateDto userUpdateDto, Long userId);

    void delete(Long userId);

    User getUserById(Long userId);

    Boolean existsUser(Long userId);
}
