package ru.practicum.service.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.user.UserMapper;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@Validated
@Slf4j
public class AdminUserController {
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info(this.getClass().getSimpleName() + ": POST: userDto: {} ", userDto);
        return UserMapper.toUserDto(userService.addUser(UserMapper.toUser(userDto)));
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Set<Long> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info(this.getClass().getSimpleName() + ": GET: ALL: ids: {} from: {} size: {}", ids, from, size);
        return userService.getUsers(ids, from, size).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Positive @NotNull @PathVariable Long userId) {
        log.info(this.getClass().getSimpleName() + ": DELETE: userId: {} ", userId);
        userService.deleteUser(userId);
    }
}
