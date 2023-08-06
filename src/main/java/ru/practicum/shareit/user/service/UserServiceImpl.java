package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public UserDto createUser(CreateUpdateUserDto createUpdateUserDto) {
        try {
            return UserMapper.toUserDtoFromUser(
                    userRepository.save(UserMapper.toUserFromCreateUpdateUserDto(createUpdateUserDto))
            );
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistedException(String.format(
                    "Пользователь с %s уже зарегистрирован", createUpdateUserDto.getEmail()
            ));
        }
    }

    @Override
    public UserDto updateUser(Long userId, CreateUpdateUserDto createUpdateUserDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Такого пользователя нет."));

        if (createUpdateUserDto.getName() != null && !createUpdateUserDto.getName().isBlank()) {
            user.setName(createUpdateUserDto.getName());
        }
        if (createUpdateUserDto.getEmail() != null && !createUpdateUserDto.getEmail().isBlank()) {
            user.setEmail(createUpdateUserDto.getEmail());
        }

        try {
            return UserMapper.toUserDtoFromUser(
                    userRepository.saveAndFlush(user)
            );
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistedException(String.format(
                    "Пользователь с %s уже зарегистрирован", createUpdateUserDto.getEmail()
            ));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDtoFromUser(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Такого пользователя нет.")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = userRepository.findAll();

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
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Такого пользователя нет."));

        userRepository.delete(user);
    }



    /*private final UserStorage userStorage;
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
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет.");
        }
        userStorage.deleteUser(userId);
    }*/
}
