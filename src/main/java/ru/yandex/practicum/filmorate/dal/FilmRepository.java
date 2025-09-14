package ru.yandex.practicum.filmorate.dal;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Primary
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
    private static final String SELECT_FILM_GENRES_QUERY = "SELECT g.id, g.name FROM film_genres fg JOIN genres g ON g.id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.id";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String INSERT_FILM_GENRES_QUERY = "INSERT INTO film_genres(film_id, genre_id) VALUES(?, ?)";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> filmRowMapper) {
        super(jdbc, filmRowMapper);
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(this::hydrateRefs);
        return films;
    }

    public Optional<Film> findById(long id) {
        Optional<Film> f = findOne(FIND_BY_ID_QUERY, id);
        f.ifPresent(this::hydrateRefs);
        return f;
    }

    public Film save(Film film, long mpaId, List<Short> genreIds) {
        long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaId);
        film.setId(id);
        upsertFilmGenres(film.getId(), genreIds);
        hydrateRefs(film);
        return film;
    }

    public Film update(Film film, short mpaId, List<Short> genreIds) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaId, film.getId());
        upsertFilmGenres(film.getId(), genreIds);
        hydrateRefs(film);
        return film;
    }

    private void hydrateRefs(Film film) {
        Long mpaId = jdbc.queryForObject("SELECT mpa_rating_id FROM films WHERE id = ?", Long.class, film.getId());
        if (mpaId != null) {
            MpaRating mpa = jdbc.query("SELECT id, name FROM mpa_ratings WHERE id = ?", (rs, rn) -> {
                MpaRating m = new MpaRating();
                m.setId(rs.getShort("id"));
                m.setName(rs.getString("name"));
                return m;
            }, mpaId).stream().findFirst().orElse(null);
            film.setMpa(mpa);
        }

        List<Genre> genres = jdbc.query(SELECT_FILM_GENRES_QUERY, (rs, rn) -> {
            Genre genre = new Genre();
            genre.setId(rs.getShort("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        LinkedHashMap<Short, Genre> uniq = new LinkedHashMap<>();
        for (Genre g : genres) uniq.putIfAbsent(g.getId(), g);
        film.getGenres().clear();
        film.getGenres().addAll(uniq.values());

        List<Long> likerIds = jdbc.queryForList("SELECT user_id FROM film_likes WHERE film_id = ? ORDER BY user_id", Long.class, film.getId());
        film.setLikesFromUsers(likerIds);
        film.setLikes(likerIds.size());
    }

    private void upsertFilmGenres(long filmId, List<Short> genreIds) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
        if (genreIds == null || genreIds.isEmpty()) return;
        List<Short> ids = genreIds.stream().filter(Objects::nonNull).distinct().sorted().toList();
        jdbc.batchUpdate(INSERT_FILM_GENRES_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, ids.get(i));
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        });
    }
}
