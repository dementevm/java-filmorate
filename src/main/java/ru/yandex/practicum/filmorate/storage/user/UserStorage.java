package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User getUser(long id);

    public List<User> getAllUsers();

    public User createUser(User user);

    public User updateUser(User user);
}
