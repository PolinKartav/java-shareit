package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDto createUser(CreateUpdateUserDto createUpdateUserDto) {
        return UserMapper.toUserDtoFromUser(userStorage.createUser(
                UserMapper.toUserFromCreateUpdateUserDto(createUpdateUserDto)).orElseThrow(() ->
                new AlreadyExistedException("Такой пользователь уже существует.")));
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDtoFromUser(userStorage.getUserById(id).orElseThrow(() ->
                new NotFoundException("Такого пользователя нет.")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = userStorage.getAllUsers();

        if (users.isEmpty()) {
            log.warn("Список пользователей пуст!");
        }
        for (User user : users) {
            UserDto userDto = UserMapper.toUserDtoFromUser(user);
            usersDto.add(userDto);
        }
        return usersDto;
    }

    @Override
    public UserDto updateUser(Long userId, CreateUpdateUserDto createUpdateUserDto) {
        User user = userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Такого пользователя нет."));
        if (createUpdateUserDto.getName() != null && !createUpdateUserDto.getName().isBlank()) {
            user.setName(createUpdateUserDto.getName());
        }
        if (createUpdateUserDto.getEmail() != null && !createUpdateUserDto.getEmail().isBlank()) {
            user.setEmail(createUpdateUserDto.getEmail());
        }
        return UserMapper.toUserDtoFromUser(userStorage.updateUser(user));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userStorage.getAllUsers().contains(userId)) {
            new NotFoundException("Такого пользователя нет.");
        }
        userStorage.deleteUser(userId);
    }
}
