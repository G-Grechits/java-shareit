package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    private UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> users = new ArrayList<>();
        users.add(getUserDto());
        users.add(getUserDto());
        when(userService.getAllUsers())
                .thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"name\": \"test user\",\"email\": \"testuser@mail.com\"}," +
                        " {\"id\": 1,\"name\": \"test user\",\"email\": \"testuser@mail.com\"}]"));
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById() throws Exception {
        UserDto userDto = getUserDto();
        when(userService.getUserById(1))
                .thenReturn(userDto);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test user\",\"email\": \"testuser@mail.com\"}"));
        verify(userService, times(1)).getUserById(1);
    }

    @Test
    void createUser() throws Exception {
        UserDto userDto = getUserDto();
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(userDto);
        mockMvc.perform(post("/users").content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test user\",\"email\": \"testuser@mail.com\"}"));
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDto1 = getUserDto();
        UserDto userDto2 = getUserDto();
        userDto2.setName("test user2");
        userDto2.setEmail("testuser2@mail.com");
        when(userService.updateUser(userDto2, 1))
                .thenReturn(userDto2);
        mockMvc.perform(patch("/users/1").content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test user2\",\"email\": \"testuser2@mail.com\"}"));
        verify(userService, times(1)).updateUser(userDto2, 1);
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(1);
    }

    private UserDto getUserDto() {
        return new UserDto(1L, "test user", "testuser@mail.com");
    }
}