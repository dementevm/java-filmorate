package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film getFilm(long id);

    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);
}
