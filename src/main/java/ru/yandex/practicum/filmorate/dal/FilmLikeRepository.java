package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmLikeRepository {
    private final JdbcTemplate jdbc;

    private static final String MERGE_LIKE =
            "MERGE INTO film_likes (film_id, user_id, created_at) KEY(film_id, user_id) " +
                    "VALUES (?, ?, CURRENT_TIMESTAMP)";

    private static final String DELETE_LIKE =
            "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

    private static final String SELECT_LIKE_USERS =
            "SELECT user_id FROM film_likes WHERE film_id = ? ORDER BY user_id";

    public int addLike(long filmId, long userId) {
        return jdbc.update(MERGE_LIKE, filmId, userId);
    }

    public int removeLike(long filmId, long userId) {
        return jdbc.update(DELETE_LIKE, filmId, userId);
    }

    public List<Long> findUserIds(long filmId) {
        return jdbc.queryForList(SELECT_LIKE_USERS, Long.class, filmId);
    }
}
