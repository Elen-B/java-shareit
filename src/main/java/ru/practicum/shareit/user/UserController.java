package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

/**
 * API for User
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable(name = "userId") Long userId) {
        User result = userService.getById(userId);
        return userMapper.map(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody UserDto user) {
        User result = userService.add(userMapper.map(user));
        return userMapper.map(result);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable(name = "userId") Long userId,
                          @Valid @RequestBody UserUpdateDto user) {
        User result = userService.update(userMapper.map(user, userId));
        return userMapper.map(result);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable(name = "userId") Long userId) {
        userService.delete(userId);
    }

}
