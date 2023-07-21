package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(CreateUpdateUserDto createUpdateUserDto);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long userId, CreateUpdateUserDto createUpdateUserDto);

    void deleteUser(Long userId);
}
