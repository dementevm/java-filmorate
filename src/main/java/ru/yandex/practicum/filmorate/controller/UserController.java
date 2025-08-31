package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody final User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody final User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") @Positive long id) {
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") @Positive long id) {
        User user = userService.getUser(id);
        return user.getFriends().stream().map(userService::getUser).collect(Collectors.toList());
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") @Positive long id, @PathVariable("friendId") @Positive long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") @Positive long id, @PathVariable("friendId") @Positive long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getFriendsCommon(@PathVariable("id") @Positive long id, @PathVariable("otherId") @Positive long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
