package ru.practicum.service.user;

import lombok.experimental.UtilityClass;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.dto.UserShortDto;
import ru.practicum.service.user.model.User;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
