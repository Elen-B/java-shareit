package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exception.ConditionsNotMetException;
import ru.practicum.shareit.core.exception.InternalServerException;
import ru.practicum.shareit.core.exception.NotFoundException;
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

    @Override
    public User getById(Long userId) {
        return userRepository.getById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с ид %s не найден", userId))
        );
    }

    @Override
    public User add(User user) {
        return userRepository.add(user).orElseThrow(() -> new InternalServerException(
                "Ошибка создания пользователя"));
    }

    @Override
    public User update(User user) {
        log.error(user.toString());
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id пользователя должен быть указан");
        }
        User oldUser = getById(user.getId());
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        return userRepository.update(oldUser).orElseThrow(() -> new InternalServerException(
                "Ошибка обновления пользователя"));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }
}
