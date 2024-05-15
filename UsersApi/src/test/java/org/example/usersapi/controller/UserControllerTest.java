package org.example.usersapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.usersapi.data.UserDataGenerator;
import org.example.usersapi.dto.UserDto;
import org.example.usersapi.exception.UserNotFoundException;
import org.example.usersapi.model.User;
import org.example.usersapi.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        ReflectionTestUtils.setField(userService, "minUserAge", 18);
    }

    @Test
    public void createUser_success() throws Exception {
        User user = UserDataGenerator.generateUser();
        UserDto userDto = UserDataGenerator.generateUserDto();

        when(userService.createUser(userDto)).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("c8aa6f36-22d5-4b9a-85a2-be1c88477583"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.birthDate").value("2000-11-03"))
                .andExpect(jsonPath("$.address").value("Square"))
                .andExpect(jsonPath("$.phoneNumber").value("12345678"));
    }

    @Test
    public void updateUserFirstNameAndLastName_success() throws Exception {
        String firstName = "Joe";
        String lastName = "Newel";

        mockMvc.perform(patch("/users/c8aa6f36-22d5-4b9a-85a2-be1c88477583?")
                        .queryParam("firstName", firstName)
                        .queryParam("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserFirstNameAndLastName_throwsException() throws Exception {
        String id = "c8aa6f36-22d5-4b9a-85a2-be1c88477583";
        String firstName = "Joe";
        String lastName = "Newel";

        doThrow(new UserNotFoundException(id))
                .when(userService)
                .updateUserFirstNameAndLastName(id, firstName, lastName);

        mockMvc.perform(patch("/users/c8aa6f36-22d5-4b9a-85a2-be1c88477583?")
                        .queryParam("firstName", firstName)
                        .queryParam("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_success() throws Exception {
        User user = UserDataGenerator.generateUser();
        UserDto userDto = UserDataGenerator.generateUserDto();

        String id = "c8aa6f36-22d5-4b9a-85a2-be1c88477583";

        when(userService.updateUser(id, userDto)).thenReturn(user);

        mockMvc.perform(put("/users/c8aa6f36-22d5-4b9a-85a2-be1c88477583")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c8aa6f36-22d5-4b9a-85a2-be1c88477583"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.birthDate").value("2000-11-03"))
                .andExpect(jsonPath("$.address").value("Square"))
                .andExpect(jsonPath("$.phoneNumber").value("12345678"));
    }

    @Test
    public void updateUser_throwsException() throws Exception {
        UserDto userDto = UserDataGenerator.generateUserDto();

        String id = "c8aa6f36-22d5-4b9a-85a2-be1c88477583";

        when(userService.updateUser(id, userDto)).thenThrow(new UserNotFoundException(id));

        mockMvc.perform(put("/users/c8aa6f36-22d5-4b9a-85a2-be1c88477583")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/users/c8aa6f36-22d5-4b9a-85a2-be1c88477583"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUser_throwsException() throws Exception {
        String id = "c8aa6f36-22d5-4b9a-85a2-be1c88477583";

        doThrow(new UserNotFoundException(id))
                .when(userService)
                .deleteUser(id);

        mockMvc.perform(delete("/users/c8aa6f36-22d5-4b9a-85a2-be1c88477583"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUsersByDateRange_success() throws Exception {
        LocalDate from = LocalDate.now().minusYears(2);
        LocalDate to = LocalDate.now().minusYears(1);

        when(userService.getUsersByDateRange(from, to))
                .thenReturn(List.of(UserDataGenerator.generateUser()));

        mockMvc.perform(get("/users/filter?")
                        .queryParam("from", from.toString())
                        .queryParam("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
