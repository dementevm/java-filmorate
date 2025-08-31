package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 1;
    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    @Override
    public Film getFilm(long id) {
        if (!films.containsKey(id)) {
            throw new ObjectNotFoundException("Film not found");
        }
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Создан фильм с id - {}: {}", id, film);
        id += 1;
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        long filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
            log.info("Фильм с id - {} обновлен. Старые данные - {}. Новые данные - {}",
                    filmId, films.get(filmId), film);
            return film;
        } else {
            throw new ObjectNotFoundException(String.format("Фильма с ID %d не существует", filmId));
        }
    }
}
