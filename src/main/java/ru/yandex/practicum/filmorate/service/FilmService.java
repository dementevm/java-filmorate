package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void likeFilm(long filmId, long userId) {
        Film film = filmStorage.getFilm(filmId);
        User user = userService.getUserStorage().getUser(userId);
        film.increaseLikes();
        user.getLikedFilms().add(filmId);
    }

    public void unlikeFilm(long filmId, long userId) {
        Film film = filmStorage.getFilm(filmId);
        User user = userService.getUserStorage().getUser(userId);
        film.decreaseLikes();
        user.getLikedFilms().remove(filmId);
    }

    public List<Film> getPopularFilms(int limit) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .limit(limit).toList();
    }
}