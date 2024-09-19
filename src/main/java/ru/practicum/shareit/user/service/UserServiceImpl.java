package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exception.ConditionsNotMetException;
import ru.practicum.shareit.core.exception.InternalServerException;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * Implementation of UserService that implement operations with users
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(Long userId) {
        return userMapper.map(getUserById(userId));
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = userRepository.save(userMapper.map(userDto));
        if (user.getId() == null) {
            throw new InternalServerException(
                    "Ошибка создания пользователя");
        }
        return userMapper.map(user);
    }

    @Override
    public UserDto update(UserUpdateDto userUpdateDto, Long userId) {
        log.info("UserServiceImpl/update args: {}, {}", userUpdateDto, userId);
        if (userId == null) {
            throw new ConditionsNotMetException("Id пользователя должен быть указан");
        }
        User oldUser = getUserById(userId);
        userMapper.update(userUpdateDto, userId, oldUser);
        log.info("UserServiceImpl/update map update: {}", oldUser);
        User updUser = userRepository.save(oldUser);
        log.info("UserServiceImpl/update result: {}", oldUser);

        return userMapper.map(updUser);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с ид %s не найден", userId))
        );
    }
}
