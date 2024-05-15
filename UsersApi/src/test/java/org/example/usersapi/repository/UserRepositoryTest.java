package org.example.usersapi.repository;

import org.example.usersapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    void saveUser_SuccessfullySavesUser() {
        User user = new User(null, "John", "Doe", "john.doe@example.com",
                LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("John", savedUser.getFirstName());
        assertTrue(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void findUserById_UserExists_ReturnsUser() {
        User user = new User(null, "John", "Doe", "john.doe@example.com", LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
    }

    @Test
    void findUserById_UserDoesNotExist_ReturnsEmpty() {
        Optional<User> foundUser = userRepository.findById("nonexistent-id");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void existsById_UserExists_ReturnsTrue() {
        User user = new User(null, "John", "Doe", "john.doe@example.com", LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User savedUser = userRepository.save(user);

        assertTrue(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void existsById_UserDoesNotExist_ReturnsFalse() {
        assertFalse(userRepository.existsById("nonexistent-id"));
    }

    @Test
    void deleteById_UserExists_DeletesUser() {
        User user = new User(null, "John", "Doe", "john.doe@example.com", LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User savedUser = userRepository.save(user);

        assertTrue(userRepository.deleteById(savedUser.getId()));
        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void deleteById_UserDoesNotExist_ReturnsFalse() {
        assertFalse(userRepository.deleteById("nonexistent-id"));
    }

    @Test
    void getUsersByDateRange_ValidRange_ReturnsMatchingUsers() {
        User user1 = userRepository.save(new User(null, "John", "Doe", "john.doe@example.com", LocalDate.of(1990, 1, 1), "123 Street", "1234567890"));
        User user2 = userRepository.save(new User(null, "Jane", "Doe", "jane.doe@example.com", LocalDate.of(1995, 1, 1), "456 Lane", "9876543210"));

        List<User> users = userRepository.getUsersByDateRange(LocalDate.of(1989, 12, 31), LocalDate.of(1991, 1, 2));
        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getFirstName());
    }

    @Test
    void getUsersByDateRange_NoMatchingUsers_ReturnsEmptyList() {
        userRepository.save(new User(null, "John", "Doe", "john.doe@example.com", LocalDate.of(1990, 1, 1), "123 Street", "1234567890"));

        List<User> users = userRepository.getUsersByDateRange(LocalDate.of(1991, 1, 2), LocalDate.of(1992, 1, 1));
        assertTrue(users.isEmpty());
    }

}