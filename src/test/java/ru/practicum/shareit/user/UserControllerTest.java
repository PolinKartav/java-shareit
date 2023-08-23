package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private static CreateUpdateUserDto correctUser;
    private static List<UserDto> listOfUsers;
    private static UserDto getUserDto;

    @BeforeAll
    static void beforeAll() {
        correctUser = CreateUpdateUserDto.builder()
                .name("user")
                .email("user@user.com")
                .build();

        getUserDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        listOfUsers = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            listOfUsers.add(getUserDto.toBuilder().id(i + 2L).build());
        }
    }

    @Test
    void shouldCreateUser() throws Exception {
        when(userService.createUser(any(CreateUpdateUserDto.class)))
                .thenReturn(getUserDto);

        String jsonUser = objectMapper.writeValueAsString(correctUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getUserDto.getId()))
                .andExpect(jsonPath("$.name").value(getUserDto.getName()))
                .andExpect(jsonPath("$.email").value(getUserDto.getEmail()));

        verify(userService, times(1)).createUser(correctUser);
    }

    @Test
    void shouldUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any(CreateUpdateUserDto.class)))
                .thenReturn(getUserDto);

        String jsonUser = objectMapper.writeValueAsString(correctUser);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getUserDto.getId()))
                .andExpect(jsonPath("$.name").value(getUserDto.getName()))
                .andExpect(jsonPath("$.email").value(getUserDto.getEmail()));
        verify(userService, times(1)).updateUser(anyLong(), any(CreateUpdateUserDto.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1")
                .contentType(MediaType.APPLICATION_JSON));
        verify(userService, times(1)).deleteUser(anyLong());
    }

    @Test
    void shouldGetById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(getUserDto);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getUserDto.getId()))
                .andExpect(jsonPath("$.name").value(getUserDto.getName()))
                .andExpect(jsonPath("$.email").value(getUserDto.getEmail()));
        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    void shouldGetAll() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(listOfUsers);
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(userService, times(1)).getAllUsers();
    }
}