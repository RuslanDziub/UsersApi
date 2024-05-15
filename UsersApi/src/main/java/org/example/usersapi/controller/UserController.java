package org.example.usersapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.usersapi.dto.UserDto;
import org.example.usersapi.exception.UserNotFoundException;
import org.example.usersapi.model.User;
import org.example.usersapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUserFirstNameAndLastName(@PathVariable String id,
                                                               String firstName,
                                                               String lastName) throws UserNotFoundException {
        userService.updateUserFirstNameAndLastName(id, firstName, lastName);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id,
                                           @RequestBody @Valid UserDto userDto) throws UserNotFoundException {
        return new ResponseEntity<>(userService.updateUser(id, userDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) throws UserNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<User>> getUsersByDateRange(LocalDate from, LocalDate to) {
        return new ResponseEntity<>(userService.getUsersByDateRange(from, to), HttpStatus.OK);
    }
}
