package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Пользователь с ID = %d не найден.", id)));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        UserDto formerUser = getUserById(id);
        formerUser.setName(userDto.getName() != null ? userDto.getName() : formerUser.getName());
        formerUser.setEmail(userDto.getEmail() != null ? userDto.getEmail() : formerUser.getEmail());
        User user = UserMapper.toUser(formerUser);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(long id) {
        getUserById(id);
        userRepository.deleteById(id);
    }
}
