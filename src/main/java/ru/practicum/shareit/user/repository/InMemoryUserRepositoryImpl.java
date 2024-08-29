package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.core.exception.DataConflictException;
import ru.practicum.shareit.core.exception.InternalServerException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of UserRepository that implement CRUD-operations for users
 */

@Slf4j
@Repository
public class InMemoryUserRepositoryImpl implements UserRepository{
    private final Map<Long, User> users = new HashMap<>();
    private static Integer globalId = 0;

    @Override
    public Optional<User> getById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> add(User user) {
        log.info("UserRepository/add for {}", user);
        checkUser(user);
        long id = getNextId();
        user.setId(id);
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        checkUser(user);
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            oldUser.setName(user.getName());
            oldUser.setEmail(user.getEmail());
            return Optional.of(oldUser);
        }
        return Optional.empty();
    }

    @Override
    public void delete(Long userId) {
        User user = users.remove(userId);
        if (user == null) {
            throw new InternalServerException("Ошибка удаления пользователя");
        }
    }

    private long getNextId() {
        return ++globalId;
    }

    private void checkUser(User user) {
        if (isEmailExist(user.getEmail(), user.getId())) {
            throw new DataConflictException("Пользователь с таким email уже существует");
        }
    }

    private Boolean isEmailExist(String email, Long userId) {
        if (email == null) {
            return false;
        }
        return users.values()
                .stream()
                .filter(user -> !Objects.equals(user.getId(), userId))
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}
