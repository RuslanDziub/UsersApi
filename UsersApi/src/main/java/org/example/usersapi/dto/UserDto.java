package org.example.usersapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class UserDto {
    @NotBlank(message = "First name must not be empty.")
    private String firstName;

    @NotBlank(message = "Last name must not be empty.")
    private String lastName;

    @NotBlank(message = "Email must not be empty.")
    @Email(message = "Wrong email format")
    private String email;

    @NotNull(message = "Birth date must not be empty.")
    @Past(message = "Birth date must be earlier than current date.")
    private LocalDate birthDate;

    private String address;
    private String phoneNumber;
}
