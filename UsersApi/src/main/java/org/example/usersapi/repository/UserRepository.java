package org.example.usersapi.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.usersapi.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
@Slf4j
public class UserRepository {
    private final List<User> users = new ArrayList<>();

    public User save(User user) {
        user.setId(UUID.randomUUID().toString());
        users.add(user);
        log.info("Saving user: {}", user);
        return user;
    }

    public Optional<User> findById(String id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    public boolean existsById(String id) {
        return users.stream().anyMatch(user -> user.getId().equals(id));
    }

    public boolean deleteById(String id) {
        return users.removeIf(user -> user.getId().equals(id));
    }

    public List<User> getUsersByDateRange(LocalDate from, LocalDate to) {
        return users.stream()
                .filter(u -> !u.getBirthDate().isBefore(from))
                .filter(u -> !u.getBirthDate().isAfter(to))
                .toList();
    }
}
