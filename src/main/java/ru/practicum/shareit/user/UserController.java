package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnUpdate;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto createUser(@Validated(OnCreate.class) @RequestBody CreateUpdateUserDto createUpdateUserDto) {
        return userService.createUser(createUpdateUserDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable @NotNull Long userId) {
        UserDto user = userService.getUserById(userId);
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Validated(OnUpdate.class) @RequestBody CreateUpdateUserDto createUpdateUserDto,
                              @PathVariable Long userId) {
        return userService.updateUser(userId, createUpdateUserDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void removeItem(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
