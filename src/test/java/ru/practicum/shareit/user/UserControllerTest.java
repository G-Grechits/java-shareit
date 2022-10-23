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
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

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
        when(userService.getUserById(userDto.getId()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test user\",\"email\": \"testuser@mail.com\"}"));
        verify(userService, times(1)).getUserById(userDto.getId());
    }

    @Test
    void createUser() throws Exception {
        UserDto userDto = getUserDto();
        when(userService.createUser(userDto))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test user\",\"email\": \"testuser@mail.com\"}"));
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDto = getUserDto();
        userDto.setName("test user2");
        userDto.setEmail("testuser2@mail.com");
        when(userService.updateUser(userDto, userDto.getId()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test user2\",\"email\": \"testuser2@mail.com\"}"));
        verify(userService, times(1)).updateUser(userDto, userDto.getId());
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