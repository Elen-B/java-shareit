package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

public interface UserService {

    User getById(Long userId);

    User add(User user);

    User update(User user);

    void delete(Long userId);
}
