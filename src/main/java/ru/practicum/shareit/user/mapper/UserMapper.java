package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.core.mapper.JsonNullableMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

/**
 * Class with methods to map User to UserDto/UserUpdateDto and UserDto/UserUpdateDto to Item
 */

@Mapper(uses = JsonNullableMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface UserMapper {

    User map(UserDto entity);

    @Mapping(target = "id", source = "userId")
    User map(UserDto entity, Long userId);

    UserDto map(User entity);

    @Mapping(target = "id", source = "userId")
    User map(UserUpdateDto entity, Long userId);
}
