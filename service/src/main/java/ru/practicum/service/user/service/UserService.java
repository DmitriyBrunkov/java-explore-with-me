package ru.practicum.service.user.service;

import ru.practicum.service.user.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User addUser(User user);

    List<User> getUsers(Set<Long> idSet, int from, int size);

    User getUser(Long userId);

    void deleteUser(long userId);

    boolean exist(long userId);
}
