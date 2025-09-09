package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PutMapping
    public UserDto updateUser(@RequestBody @Valid UpdateUserRequest userRequest) {
        return userService.updateUser(userRequest);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") @Positive long id) {
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getUserFriends(@PathVariable("id") @Positive long id) {
        return userService.getUserFriends(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") @Positive long id,
                          @PathVariable("friendId") @Positive long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") @Positive long id,
                             @PathVariable("friendId") @Positive long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getFriendsCommon(@PathVariable("id") @Positive long id,
                                          @PathVariable("otherId") @Positive long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
