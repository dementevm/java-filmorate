package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Service
public class UserService {
    private final UserStorage userStorage;
    private final static Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId1, long userId2) {
        User user1 = userStorage.getUser(userId1);
        User user2 = userStorage.getUser(userId2);
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
        log.info("User {} and user {} became friends", user1, user2);
    }

    public void removeFriend(long userId1, long userId2) {
        User user1 = userStorage.getUser(userId1);
        User user2 = userStorage.getUser(userId2);
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
        log.info("User {} and user {} removed from friends", user1, user2);
    }

    public List<User> getCommonFriends(long userId1, long userId2) {
        User user1 = userStorage.getUser(userId1);
        User user2 = userStorage.getUser(userId2);
        HashSet<Long> commonFriends = new HashSet<>(user1.getFriends());
        commonFriends.retainAll(user2.getFriends());
        return commonFriends.stream().map(userStorage::getUser).collect(Collectors.toList());
    }
}