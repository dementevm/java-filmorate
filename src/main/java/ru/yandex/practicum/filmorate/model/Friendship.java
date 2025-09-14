package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatus;

@Data
public class Friendship {
    @NotNull
    private Long user1Id;
    @NotNull
    private Long user2Id;
    private Long requestById;
    private FriendshipStatus status;
}
