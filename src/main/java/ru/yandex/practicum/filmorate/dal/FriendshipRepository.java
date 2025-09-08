package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipRepository {

    private final JdbcTemplate jdbc;

    private static final String EXISTS_QUERY =
            "SELECT COUNT(*) FROM friendships WHERE user1_id = ? AND user2_id = ?";

    private static final String INSERT_CONFIRMED_QUERY =
            "INSERT INTO friendships (user1_id, user2_id, requested_by_id, status) VALUES (?, ?, ?, 'CONFIRMED')";

    private static final String INSERT_PENDING_QUERY =
            "INSERT INTO friendships (user1_id, user2_id, requested_by_id, status) VALUES (?, ?, ?, 'PENDING')";

    private static final String UPDATE_CONFIRM_QUERY =
            "UPDATE friendships SET status = 'CONFIRMED' WHERE user1_id = ? AND user2_id = ?";

    private static final String DELETE_ONE_QUERY =
            "DELETE FROM friendships WHERE user1_id = ? AND user2_id = ?";

    private static final String DELETE_BOTH_QUERY =
            "DELETE FROM friendships WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

    private static final String FRIEND_IDS_QUERY =
            "SELECT DISTINCT user2_id AS friend_id " +
                    "FROM friendships " +
                    "WHERE user1_id = ? " +
                    "  AND (status = 'CONFIRMED' OR (status = 'PENDING' AND requested_by_id = ?))";

    private static final String COMMON_FRIEND_IDS_QUERY =
            "SELECT f1.user2_id AS friend_id " +
                    "FROM friendships f1 " +
                    "JOIN friendships f2 ON f2.user2_id = f1.user2_id " +
                    "WHERE f1.user1_id = ? AND f1.status = 'CONFIRMED' " +
                    "  AND f2.user1_id = ? AND f2.status = 'CONFIRMED'";

    public void addDirected(long initiatorId, long otherId) {
        Integer forwardCount = jdbc.queryForObject(EXISTS_QUERY, Integer.class, initiatorId, otherId);
        if (forwardCount == 0) {
            jdbc.update(INSERT_CONFIRMED_QUERY, initiatorId, otherId, initiatorId);
        }
        Integer reverseCount = jdbc.queryForObject(EXISTS_QUERY, Integer.class, otherId, initiatorId);
        if (reverseCount == 0) {
            jdbc.update(INSERT_PENDING_QUERY, otherId, initiatorId, initiatorId);
        }
    }

    public void confirm(long acceptorId, long requesterId) {
        jdbc.update(UPDATE_CONFIRM_QUERY, acceptorId, requesterId);
    }

    public void deleteOne(long id, long friendId) {
        jdbc.update(DELETE_ONE_QUERY, id, friendId);
    }

    public void deleteBoth(long userId1, long userId2) {
        jdbc.update(DELETE_BOTH_QUERY, userId1, userId2, userId2, userId1);
    }

    public List<Long> findFriendIds(long userId) {
        return jdbc.queryForList(FRIEND_IDS_QUERY, Long.class, userId, userId);
    }

    public List<Long> findCommonFriendIds(long id, long otherId) {
        return jdbc.queryForList(COMMON_FRIEND_IDS_QUERY, Long.class, id, otherId);
    }
}
