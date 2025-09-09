package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.mappers.user.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Service
public class UserService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    public UserDto getUser(long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден с ID: " + userId));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new InternalServerException("Email должен быть указан");
        }
        User user = UserMapper.mapToUser(request);
        user = userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User updatedUser = userRepository.findById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        updatedUser = userRepository.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void addFriend(long id, long friendId) {
        if (id == friendId) throw new InternalServerException("Нельзя добавить самого себя");
        userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь с id=%d не найден".formatted(id)));
        userRepository.findById(friendId).orElseThrow(() -> new ObjectNotFoundException("Пользователь с id=%d не найден".formatted(friendId)));
        friendshipRepository.addDirected(id, friendId);
    }

    public void removeFriend(long id, long friendId) {
        userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь с id=%d не найден".formatted(id)));
        userRepository.findById(friendId).orElseThrow(() -> new ObjectNotFoundException("Пользователь с id=%d не найден".formatted(friendId)));
        friendshipRepository.deleteOne(id, friendId);
    }

    public void confirmFriend(long acceptorId, long requesterId) {
        friendshipRepository.confirm(acceptorId, requesterId);
    }

    public List<UserDto> getUserFriends(long id) {
        checkUserExist(id);
        List<Long> ids = friendshipRepository.findFriendIds(id);
        return getUserDto(ids);
    }

    public List<UserDto> getCommonFriends(long id, long otherId) {
        checkUserExist(id);
        checkUserExist(otherId);
        List<Long> ids = friendshipRepository.findCommonFriendIds(id, otherId);
        return getUserDto(ids);
    }

    private List<UserDto> getUserDto(List<Long> ids) {
        if (ids.isEmpty()) return List.of();
        List<User> users = userRepository.findAllByIds(ids);
        Map<Long, User> map = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    private void checkUserExist(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ObjectNotFoundException("Пользователь id=%d не найден".formatted(userId));
        }
    }
}