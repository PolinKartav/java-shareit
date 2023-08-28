package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.AlreadyExistedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    private static UserService userService;
    private static UserRepository userRepository;
    private static CreateUpdateUserDto createUserDto;
    private static CreateUpdateUserDto updateNameUserDto;
    private static CreateUpdateUserDto updateEmailUserDto;
    private static UserDto getUserDto;
    private static User getUser;
    private static List<User> listOfUser;

    @BeforeAll
    static void beforeAll() {
        createUserDto = CreateUpdateUserDto.builder()
                .name("name")
                .email("email@ya.ru")
                .build();

        getUser = User.builder()
                .id(1L)
                .name("name")
                .email("email@ya.ru")
                .build();

        getUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@ya.ru")
                .build();

        updateNameUserDto = CreateUpdateUserDto.builder()
                .name("newName")
                .build();

        updateEmailUserDto = CreateUpdateUserDto.builder()
                .email("newMail@ya.ru")
                .build();

        listOfUser = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfUser.add(getUser.toBuilder().id(i + 1L).build());
        }
    }

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(getUser.toBuilder().build());

        UserDto userDto = userService.createUser(createUserDto);

        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", getUserDto.getId())
                .hasFieldOrPropertyWithValue("name", getUserDto.getName())
                .hasFieldOrPropertyWithValue("email", getUserDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldGetAlreadyExistsExceptionCreateUser() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("error"));

        final AlreadyExistedException exception = Assertions.assertThrows(
                AlreadyExistedException.class,
                () -> userService.createUser(createUserDto)
        );

        assertEquals(String.format("Пользователь с %s уже зарегистрирован", createUserDto.getEmail()),
                exception.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldGetNotFoundExceptionWithUpdate() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(1L, updateNameUserDto)
        );

        assertEquals("Такого пользователя нет.",
                exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAlreadyExistsExceptionUpdateUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("error"));

        final AlreadyExistedException exception = Assertions.assertThrows(
                AlreadyExistedException.class,
                () -> userService.updateUser(1L, updateEmailUserDto)
        );

        assertEquals(String.format("Пользователь с %s уже зарегистрирован", updateEmailUserDto.getEmail()),
                exception.getMessage());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldUpdateUserName() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));
        when(userRepository.saveAndFlush(any(User.class)))
                .thenReturn(getUser.toBuilder().name(updateNameUserDto.getName()).build());

        UserDto userDto = userService.updateUser(1L, updateNameUserDto);

        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", getUserDto.getId())
                .hasFieldOrPropertyWithValue("name", updateNameUserDto.getName())
                .hasFieldOrPropertyWithValue("email", getUserDto.getEmail());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldUpdateUserEmail() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));
        when(userRepository.saveAndFlush(any(User.class)))
                .thenReturn(getUser.toBuilder().email(updateEmailUserDto.getEmail()).build());

        UserDto userDto = userService.updateUser(1L, updateEmailUserDto);

        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", getUserDto.getId())
                .hasFieldOrPropertyWithValue("name", getUserDto.getName())
                .hasFieldOrPropertyWithValue("email", updateEmailUserDto.getEmail());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldGetNotFoundExceptionWithDeleteById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.deleteUser(1L)
        );

        assertEquals("Такого пользователя нет.",
                exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldDeleteById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void shouldGetExceptionGetById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(1L)
        );

        assertEquals("Такого пользователя нет.",
                exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));

        UserDto userDto = userService.getUserById(1L);

        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", getUserDto.getId())
                .hasFieldOrPropertyWithValue("name", getUserDto.getName())
                .hasFieldOrPropertyWithValue("email", getUserDto.getEmail());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAll() {

        when(userRepository.findAll())
                .thenReturn(listOfUser);
        List<UserDto> listUsers = userService.getAllUsers();

        assertThat(listUsers)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "name");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "email@ya.ru");
                });
        verify(userRepository, times(1)).findAll();
    }
}