package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void initialize() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAllUsers() {
        User user = getUser();
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));
        List<UserDto> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(1, users.get(0).getId());
        assertEquals("test user", users.get(0).getName());
        assertEquals("testuser@mail.com", users.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById() {
        User user = getUser();
        Long userId = user.getId();
        Long wrongId = userId + 1;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.findById(wrongId))
                .thenThrow(new ObjectNotFoundException(String.format("Пользователь с ID = %d не найден.", wrongId)));
        UserDto userDto = userService.getUserById(userId);

        Throwable throwable = assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(wrongId));
        assertNotNull(throwable);
        assertEquals("Пользователь с ID = 2 не найден.", throwable.getMessage());
        assertNotNull(userDto);
        assertEquals(1, userDto.getId());
        assertEquals("test user", userDto.getName());
        assertEquals("testuser@mail.com", userDto.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void createUser() {
        User user = getUser();
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser() {
        User user1 = getUser();
        Long userId = user1.getId();
        User user2 = getUser();
        user2.setName("new user");
        user2.setEmail("newuser@mail.com");
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class)))
                .thenReturn(user2);
        UserDto userDto = userService.updateUser(UserMapper.toUserDto(user2), userId);

        assertNotNull(userDto);
        assertEquals(user2.getId(), userDto.getId());
        assertEquals(user2.getName(), userDto.getName());
        assertEquals(user2.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser() {
        User user = getUser();
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    private User getUser() {
        return new User(1L, "test user", "testuser@mail.com");
    }
}