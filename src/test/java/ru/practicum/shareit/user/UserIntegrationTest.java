package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserIntegrationTest {
    private final UserService userService;
    private final EntityManager entityManager;

    private static CreateUpdateUserDto createUpdateUserDto;

    @BeforeAll
    static void beforeAll() {
        createUpdateUserDto = CreateUpdateUserDto.builder()
                .name("name")
                .email("email@ya.ru")
                .build();
    }

    @Test
    void shouldCreateUser() {
        userService.createUser(createUpdateUserDto);

        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", 1L).getSingleResult();

        assertThat(createUpdateUserDto.getName(), equalTo(user.getName()));
        assertThat(createUpdateUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void shouldUpdateUser() {
        userService.createUser(createUpdateUserDto);

        CreateUpdateUserDto updateUserDto = CreateUpdateUserDto.builder().name("newName").build();

        userService.updateUser(1L, updateUserDto);

        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        User updatedUser = query.setParameter("id", 1L).getSingleResult();

        assertThat(updateUserDto.getName(), equalTo(updatedUser.getName()));
    }

    @Test
    void shouldDeleteById() {
        userService.createUser(createUpdateUserDto);

        assertThat(userService.getAllUsers().size(), equalTo(1));

        userService.deleteUser(1L);

        assertThat(userService.getAllUsers().size(), equalTo(0));
    }

    @Test
    void shouldGetById() {
        userService.createUser(createUpdateUserDto);

        UserDto user = userService.getUserById(1L);

        assertThat(createUpdateUserDto.getName(), equalTo(user.getName()));
        assertThat(createUpdateUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void shouldGetAll() {
        CreateUpdateUserDto userDto2 = createUpdateUserDto.toBuilder().email("mail2@ya.ru").build();
        CreateUpdateUserDto userDto3 = createUpdateUserDto.toBuilder().email("mail3@ya.ru").build();
        CreateUpdateUserDto userDto4 = createUpdateUserDto.toBuilder().email("mail4@ya.ru").build();
        CreateUpdateUserDto userDto5 = createUpdateUserDto.toBuilder().email("mail5@ya.ru").build();
        CreateUpdateUserDto userDto6 = createUpdateUserDto.toBuilder().email("mail6@ya.ru").build();
        CreateUpdateUserDto userDto7 = createUpdateUserDto.toBuilder().email("mail7@ya.ru").build();

        userService.createUser(createUpdateUserDto);
        userService.createUser(userDto2);
        userService.createUser(userDto3);
        userService.createUser(userDto4);
        userService.createUser(userDto5);
        userService.createUser(userDto6);
        userService.createUser(userDto7);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users)
                .isNotEmpty()
                .hasSize(7)
                .satisfies(list -> {
                    assertThat(list.get(2)).hasFieldOrPropertyWithValue("id", 3L);
                    assertThat(list.get(2)).hasFieldOrPropertyWithValue("email", userDto3.getEmail());
                    assertThat(list.get(3)).hasFieldOrPropertyWithValue("id", 4L);
                    assertThat(list.get(3)).hasFieldOrPropertyWithValue("email", userDto4.getEmail());
                    assertThat(list.get(4)).hasFieldOrPropertyWithValue("id", 5L);
                    assertThat(list.get(4)).hasFieldOrPropertyWithValue("email", userDto5.getEmail());
                });
    }
}
