package org.example.usersapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.usersapi.data.UserDataGenerator;
import org.example.usersapi.dto.UserDto;
import org.example.usersapi.exception.UserNotFoundException;
import org.example.usersapi.model.User;
import org.example.usersapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User expectedUser;


    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userService, "minUserAge", 18);
        userDto = UserDataGenerator.generateUserDto();
        expectedUser = UserDataGenerator.generateUser();
    }

    @Test
    void createUser_ValidUser_ReturnsUser() {
        when(objectMapper.convertValue(userDto, User.class)).thenReturn(expectedUser);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User result = userService.createUser(userDto);
        assertEquals(expectedUser, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_InvalidBirthDate_ThrowsException() {
        userDto.setBirthDate(LocalDate.now().minusYears(1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userDto);
        });

        assertEquals("Invalid birth date", exception.getMessage());
    }

    @Test
    void updateUserFirstNameAndLastName_SuccessfulUpdate() throws UserNotFoundException {
        String id = "1";
        String firstName = "UpdatedFirstName";
        String lastName = "UpdatedLastName";
        User user = new User("1", "OldFirstName", "OldLastName", "email@example.com", LocalDate.now(), "123 Street", "1234567890");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        userService.updateUserFirstNameAndLastName(id, firstName, lastName);

        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserFirstNameAndLastName_UserNotFound_ThrowsException() {
        String id = "nonexistent-id";
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserFirstNameAndLastName(id, "AnyFirstName", "AnyLastName");
        });

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void updateUser_SuccessfulUpdate() throws UserNotFoundException {
        String id = "1";
        UserDto userDto = new UserDto("UpdatedFirstName", "UpdatedLastName", "email@example.com", LocalDate.of(2000, 1, 1), "123 Street", "1234567890");
        User existingUser = new User("1", "OldFirstName", "OldLastName", "old.email@example.com", LocalDate.of(1999, 1, 1), "Old Address", "9876543210");
        User updatedUser = new User("1", "UpdatedFirstName", "UpdatedLastName", "email@example.com", LocalDate.of(2000, 1, 1), "123 Street", "1234567890");

        when(userRepository.existsById(id)).thenReturn(true);
        when(objectMapper.convertValue(userDto, User.class)).thenReturn(updatedUser);

        userService.updateUser(id, userDto);

        verify(userRepository).save(updatedUser);
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        String id = "nonexistent-id";
        UserDto userDto = new UserDto("FirstName", "LastName", "email@example.com", LocalDate.of(2000, 1, 1), "123 Street", "1234567890");

        when(userRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(id, userDto);
        });

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void deleteUser_UserExists_DeletesSuccessfully() throws UserNotFoundException {
        String id = "1";
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        String id = "nonexistent-id";
        when(userRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(id);
        });

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void getUsersByDateRange_ValidRange_ReturnsUsers() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 12, 31);
        List<User> expectedUsers = List.of(new User(), new User());

        when(userRepository.getUsersByDateRange(from, to)).thenReturn(expectedUsers);

        List<User> users = userService.getUsersByDateRange(from, to);

        assertEquals(expectedUsers, users);
        verify(userRepository).getUsersByDateRange(from, to);
    }

    @Test
    void getUsersByDateRange_InvalidRange_ThrowsException() {
        LocalDate from = LocalDate.of(2020, 12, 31);
        LocalDate to = LocalDate.of(2020, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUsersByDateRange(from, to);
        });

        assertEquals("'from' date range must be before 'to'.", exception.getMessage());
    }
}
