package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long id);

    void deleteUser(long id);
}
