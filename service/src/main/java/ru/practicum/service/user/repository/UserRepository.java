package ru.practicum.service.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.service.user.model.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User as u " +
            "where u.id in :idSet or :idSet is null " +
            "order by u.id ")
    List<User> getUsers(Set<Long> idSet, Pageable pageable);
}
