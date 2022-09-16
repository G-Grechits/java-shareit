package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь с ID = %d не найден.", id)));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmail(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        UserDto formerUser = getUserById(id);
        if (userDto.getName() != null) {
            formerUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            checkEmail(userDto.getEmail());
            formerUser.setEmail(userDto.getEmail());
        }
        User user = UserMapper.toUser(formerUser);
        return UserMapper.toUserDto(userRepository.updateUser(user));
    }

    @Override
    public void deleteUser(long id) {
        getUserById(id);
        userRepository.deleteUser(id);
    }

    private void checkEmail(String email) {
        if (userRepository.getAllUsers()
                .stream()
                .map(User::getEmail)
                .anyMatch(str -> str.equals(email))) {
            throw new ValidationException(String.format("Пользователь с электронной почтой %s уже существует.", email));
        }
    }
}
