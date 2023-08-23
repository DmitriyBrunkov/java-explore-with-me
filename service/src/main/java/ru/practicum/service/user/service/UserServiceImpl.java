package ru.practicum.service.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.exception.model.ObjectNotFoundException;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;
import ru.practicum.service.validation.PageableValidation;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers(Set<Long> idSet, int from, int size) {
        Pageable pageable = PageableValidation.validatePageable(from, size);
        return userRepository.getUsers(idSet, pageable);
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User id: " + userId + " not found"));
    }

    @Override
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User id: " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}
