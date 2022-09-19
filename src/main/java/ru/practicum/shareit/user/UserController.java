package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        log.info("Получен список всех пользователей.");
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        UserDto userDto = userService.getUserById(userId);
        log.info("Получен пользователь {}.", userDto.getName());
        return userDto;
    }

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        log.info("Пользователь {} зарегистрирован.", createdUser.getName());
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @Validated({Update.class}) @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(userDto, userId);
        log.info("Данные пользователя {} обновлены.", updatedUser.getName());
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        log.info("Пользователь с ID = {} удалён.", userId);
    }
}
