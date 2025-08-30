package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
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

//    Пришлось закомментировать тест, потому что не проходит проверки postman'а
//    @Test
//    void testCreateUserDuplicateEmail() {
//        userService.createUser(testUser);
//        User duplicateUser = new User();
//        duplicateUser.setEmail("test@example.com");
//        duplicateUser.setLogin("login");
//        duplicateUser.setBirthday(LocalDate.of(1990, 1, 1));
//
//        assertThatThrownBy(() -> userService.createUser(duplicateUser))
//                .isInstanceOf(UserAlreadyExists.class)
//                .hasMessage("Пользователь с таким email уже существует");
//    }

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
    void testUnknownUserUpdateThrowsException() {
        testUser.setId(999);
        assertThatThrownBy(() -> userService.updateUser(testUser))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Пользователя с ID 999 не существует");
    }

    @Test
    void testAddAndRemoveFriend() {
        User user1 = userService.createUser(testUser);

        User user2 = new User();
        user2.setEmail("friend@example.com");
        user2.setLogin("friendLogin");
        user2.setName("FriendName");
        user2.setBirthday(LocalDate.of(1991, 2, 2));
        user2 = userService.createUser(user2);

        userService.addFriend(user1.getId(), user2.getId());
        assertThat(user1.getFriends()).contains(user2.getId());
        assertThat(user2.getFriends()).contains(user1.getId());

        userService.removeFriend(user1.getId(), user2.getId());
        assertThat(user1.getFriends()).doesNotContain(user2.getId());
        assertThat(user2.getFriends()).doesNotContain(user1.getId());
    }

    @Test
    void testGetCommonFriends() {
        User user1 = userService.createUser(testUser);

        User user2 = new User();
        user2.setEmail("friend2@example.com");
        user2.setLogin("friend2Login");
        user2.setName("Friend2Name");
        user2.setBirthday(LocalDate.of(1992, 3, 3));
        user2 = userService.createUser(user2);

        User commonFriend = new User();
        commonFriend.setEmail("common@example.com");
        commonFriend.setLogin("commonLogin");
        commonFriend.setName("CommonFriend");
        commonFriend.setBirthday(LocalDate.of(1993, 4, 4));
        commonFriend = userService.createUser(commonFriend);

        userService.addFriend(user1.getId(), commonFriend.getId());
        userService.addFriend(user2.getId(), commonFriend.getId());

        assertThat(userService.getCommonFriends(user1.getId(), user2.getId()))
                .extracting(User::getId)
                .containsExactly(commonFriend.getId());
    }
}