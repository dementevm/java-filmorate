package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExists;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
//        Указанною проверку пришлось закомментировать, потому что не проходит тесты postman'а
//        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
//            throw new UserAlreadyExists("Пользователь с таким email уже существует");
//        }
        if (users.values().stream().anyMatch(u -> u.getLogin().equals(user.getLogin()))) {
            throw new UserAlreadyExists("Пользователь с таким login уже существует");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Создан пользователь с id - {}: {}", id, user);
        id += 1;
        return user;
    }

    public User updateUser(User user) {
        int userId = user.getId();
        if (users.containsKey(userId)) {
            users.put(userId, user);
            log.info("Пользователь с id - {} обновлен. Старые данные - {}. Новые данные - {}",
                    userId, users.get(userId), user);
            return user;
        } else {
            return createUser(user);
        }
    }
}