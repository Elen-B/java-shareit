package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Transactional(readOnly = true)
public interface UserService {

    UserDto getById(Long userId);

    @Transactional
    UserDto add(UserDto userDto);

    @Transactional
    UserDto update(UserUpdateDto userUpdateDto, Long userId);

    @Transactional
    void delete(Long userId);

    Boolean existsUser(Long userId);
}
