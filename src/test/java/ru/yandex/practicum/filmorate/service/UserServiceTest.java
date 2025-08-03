package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExists;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testLogin");
        testUser.setName("TestName");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateUserSuccess() {
        User createdUser = userService.createUser(testUser);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(1);
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");
        assertThat(userService.getAllUsers()).hasSize(1);
    }

    @Test
    void testCreateUserDuplicateEmail() {
        userService.createUser(testUser);
        User duplicateUser = new User();
        duplicateUser.setEmail("test@example.com");
        duplicateUser.setLogin("login");
        duplicateUser.setBirthday(LocalDate.of(1990, 1, 1));

        assertThatThrownBy(() -> userService.createUser(duplicateUser))
                .isInstanceOf(UserAlreadyExists.class)
                .hasMessage("Пользователь с таким email уже существует");
    }

    @Test
    void testIfNameIsEmptyEqualsLogin() {
        testUser.setName("");
        User createdUser = userService.createUser(testUser);
        assertThat(createdUser.getName()).isEqualTo(createdUser.getLogin());
    }

    @Test
    void testUpdateUserSuccess() {
        User created = userService.createUser(testUser);
        created.setName("Updated Name");
        User updated = userService.updateUser(created);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(userService.getAllUsers().getFirst().getName()).isEqualTo("Updated Name");
    }

    @Test
    void testUpdateUserCreatesNewIfNotExists() {
        testUser.setId(999);
        User updated = userService.updateUser(testUser);

        assertThat(updated.getId()).isNotEqualTo(999);
        assertThat(userService.getAllUsers()).hasSize(1);
    }
}