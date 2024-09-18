package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {

    UserDto getById(Long userId);

    UserDto add(UserDto userDto);

    UserDto update(UserUpdateDto userUpdateDto, Long userId);

    void delete(Long userId);
}
