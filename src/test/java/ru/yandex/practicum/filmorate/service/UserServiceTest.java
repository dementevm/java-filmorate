package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    private NewUserRequest newUser;

    @BeforeEach
    void setUp() {
        newUser = new NewUserRequest();
        newUser.setEmail("test@example.com");
        newUser.setLogin("testLogin");
        newUser.setName("TestName");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Создание пользователя")
    void testCreateUserSuccess() {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDto created = userService.createUser(newUser);

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Обновление пользователя")
    void testUpdateUserSuccess() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setLogin("old");
        existing.setName("Old");
        existing.setBirthday(LocalDate.of(1980, 1, 1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserRequest upd = new UpdateUserRequest();
        upd.setId(1L);
        upd.setEmail("new@example.com");
        upd.setLogin("newLogin");
        upd.setName("NewName");
        upd.setBirthday(LocalDate.of(1991, 2, 2));

        UserDto updated = userService.updateUser(upd);

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getLogin()).isEqualTo("newLogin");
        assertThat(updated.getName()).isEqualTo("NewName");
        verify(userRepository).update(any(User.class));
    }

    @Test
    @DisplayName("Исключение при получении несуществующего пользователя")
    void testGetUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUser(99L))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Друзья пользователя")
    void testGetUserFriends() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setLogin("owner");
        owner.setName("Owner");
        owner.setBirthday(LocalDate.of(1990, 1, 1));

        User friend1 = new User();
        friend1.setId(2L);
        friend1.setEmail("f1@example.com");
        friend1.setLogin("f1");
        friend1.setName("F1");
        friend1.setBirthday(LocalDate.of(1992, 2, 2));

        User friend2 = new User();
        friend2.setId(3L);
        friend2.setEmail("f2@example.com");
        friend2.setLogin("f2");
        friend2.setName("F2");
        friend2.setBirthday(LocalDate.of(1993, 3, 3));

        when(friendshipRepository.findFriendIds(1L)).thenReturn(List.of(2L, 3L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(userRepository.findAllByIds(List.of(2L, 3L))).thenReturn(List.of(friend1, friend2));

        List<UserDto> friends = userService.getUserFriends(1L);

        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(UserDto::getId).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    @DisplayName("Общие друзья")
    void testGetCommonFriends() {
        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(2L);

        User common = new User();
        common.setId(5L);
        common.setEmail("common@example.com");
        common.setLogin("common");
        common.setName("Common");
        common.setBirthday(LocalDate.of(1995, 5, 5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(u1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(u2));
        when(friendshipRepository.findCommonFriendIds(1L, 2L)).thenReturn(List.of(5L));
        when(userRepository.findAllByIds(List.of(5L))).thenReturn(List.of(common));

        List<UserDto> commonFriends = userService.getCommonFriends(1L, 2L);

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.getFirst().getId()).isEqualTo(5L);
    }
}
