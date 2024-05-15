package org.example.usersapi.data;

import org.example.usersapi.dto.UserDto;
import org.example.usersapi.model.User;

import java.time.LocalDate;

public class UserDataGenerator {
    public static User generateUser() {
        return User.builder()
                .id("c8aa6f36-22d5-4b9a-85a2-be1c88477583")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(2000, 11,3))
                .address("Square")
                .phoneNumber("12345678")
                .build();
    }

    public static UserDto generateUserDto() {
        return UserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(2000, 11,3))
                .address("Square")
                .phoneNumber("12345678")
                .build();
    }
}
