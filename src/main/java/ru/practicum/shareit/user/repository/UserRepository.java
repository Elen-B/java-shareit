package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> getById(Long userId);

    Optional<User> add(User user);

    Optional<User> update(User user);

    void delete(Long userId);
}
