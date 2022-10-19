package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

//    private UserRepository userRepository;
//    private UserService userService;
//
//    @BeforeEach
//    void initialize() {
//        userRepository = mock(UserRepository.class);
//        userService = new UserServiceImpl(userRepository);
//    }

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
        assertNotNull(userDto);
        assertEquals(1, userDto.getId());
        assertEquals("test user", userDto.getName());
        assertEquals("testuser@mail.com", userDto.getEmail());
        assertNotNull(throwable.getMessage());
        assertEquals("Пользователь с ID = 2 не найден.", throwable.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void createUser() {
        User user = getUser();
        when(userRepository.save(user))
                .thenReturn(user);
        UserDto userDto = userService.createUser(getUserDto());

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
        User user = getUser();

    }

    private User getUser() {
        return new User(1L, "test user", "testuser@mail.com");
    }

    private UserDto getUserDto() {
        return new UserDto(null, "test user", "testuser@mail.com");
    }
}