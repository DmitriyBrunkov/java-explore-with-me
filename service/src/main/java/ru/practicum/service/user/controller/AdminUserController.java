package ru.practicum.service.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.user.UserMapper;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        log.info("{}: POST: userDto: {} ", this.getClass().getSimpleName(), userDto);
        return UserMapper.toUserDto(userService.addUser(UserMapper.toUser(userDto)));
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Set<Long> ids,
                                  @RequestParam(defaultValue = "0", required = false) Integer from,
                                  @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("{}: GET: ALL: ids: {} from: {} size: {}", this.getClass().getSimpleName(), ids, from, size);
        return userService.getUsers(ids, from, size).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Positive Long userId) {
        log.info("{}: DELETE: userId: {} ", this.getClass().getSimpleName(), userId);
        userService.deleteUser(userId);
    }
}
