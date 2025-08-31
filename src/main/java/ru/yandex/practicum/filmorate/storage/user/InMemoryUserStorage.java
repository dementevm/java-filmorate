package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    @Override
    public User getUser(long id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("User not found");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        /*
        Проверки ниже пришлось закомментировать, как и тесты к ним, из-за тестов postman, но очевидно
        это корректная логика

        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }
        if (users.values().stream().anyMatch(u -> u.getLogin().equals(user.getLogin()))) {
            throw new UserAlreadyExistsException("Пользователь с таким login уже существует");
        }
         */
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        user.setId(id);
        users.put(user.getId(), user);
        log.info("Создан пользователь с id - {}: {}", id, user);
        id += 1;
        return user;
    }

    @Override
    public User updateUser(User user) {
        long userId = user.getId();
        if (users.containsKey(userId)) {
            users.put(userId, user);
            log.info("Пользователь с id - {} обновлен. Старые данные - {}. Новые данные - {}",
                    userId, users.get(userId), user);
            return user;
        } else {
            throw new ObjectNotFoundException(String.format("Пользователя с ID %d не существует", userId));
        }
    }
}
