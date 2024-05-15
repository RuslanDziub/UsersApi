package org.example.usersapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.usersapi.dto.UserDto;
import org.example.usersapi.exception.UserNotFoundException;
import org.example.usersapi.model.User;
import org.example.usersapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.min-user-age:18}")
    private int minUserAge;

    public User createUser(@Valid UserDto userDto) {
        if (isValid(userDto.getBirthDate())) {
            return userRepository.save(userDtoToUser(userDto));
        }

        throw new IllegalArgumentException("Invalid birth date");
    }

    public void updateUserFirstNameAndLastName(String id, String firstName, String lastName) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setFirstName(firstName);
        user.setLastName(lastName);

        userRepository.save(user);
    }

    public User updateUser(String id, UserDto userDto) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found.");
        }

        User updatedUser = userDtoToUser(userDto);
        updatedUser.setId(id);

        userRepository.save(updatedUser);

        return updatedUser;
    }

    public void deleteUser(String id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found.");
        }

        userRepository.deleteById(id);
    }

    public List<User> getUsersByDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date range must be before 'to'.");
        }

        return userRepository.getUsersByDateRange(from, to);
    }

    private boolean isValid(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= minUserAge;
    }

    private User userDtoToUser(UserDto userDto) {
        return objectMapper.convertValue(userDto, User.class);
    }
}
