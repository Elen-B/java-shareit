package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository{
    private final Map<Long, User> users = new HashMap<>();
    private static Integer globalId = 0;
    @Override
    public User add(User user) {
        long id = getNextId();
        user.setId(id);
        users.put(user.getId(), user);
        return user;
        //return Optional.of(item);
    }

    private long getNextId() {
        return ++globalId;
    }
}
