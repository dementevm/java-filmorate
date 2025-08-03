package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film createFilm(Film film) {
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Создан фильм с id - {}: {}", id, film);
        id += 1;
        return film;
    }

    public Film updateFilm(Film film) {
        int filmId = film.getId();
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