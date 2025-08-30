package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FilmServiceTest {
    private FilmService filmService;
    private UserService userService;
    private Film testFilm;
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
        filmService = new FilmService(new InMemoryFilmStorage(), userService);
        testFilm = new Film();
        testFilm.setName("TestFilm");
        testFilm.setDescription("TestDescription");
        testFilm.setDuration(120);
        testFilm.setReleaseDate(LocalDate.of(1999, 1, 1));

        testUser = new User();
        testUser.setEmail("test@mail.com");
        testUser.setLogin("testuser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        userService.createUser(testUser);
    }

    @Test
    void testCreateFilmSuccess() {
        Film createdFilm = filmService.createFilm(testFilm);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isEqualTo(1);
        assertThat(createdFilm.getName()).isEqualTo(testFilm.getName());
        assertThat(filmService.getAllFilms().size()).isEqualTo(1);
    }

    @Test
    void testUpdateFilmSuccess() {
        Film created = filmService.createFilm(testFilm);
        created.setName("Updated Film");
        Film updated = filmService.updateFilm(created);

        assertThat(updated.getName()).isEqualTo("Updated Film");
    }

    @Test
    void testUpdateFilmCreatesNewIfNotExists() {
        testFilm.setId(999L);

        assertThatThrownBy(() -> filmService.updateFilm(testFilm))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Фильма с ID 999 не существует");
    }

    @Test
    void testLikeFilm() {
        Film createdFilm = filmService.createFilm(testFilm);
        filmService.likeFilm(createdFilm.getId(), testUser.getId());

        assertThat(createdFilm.getLikes()).isEqualTo(1);
        assertThat(testUser.getLikedFilms()).contains(createdFilm.getId());
    }

    @Test
    void testUnlikeFilm() {
        Film createdFilm = filmService.createFilm(testFilm);
        filmService.likeFilm(createdFilm.getId(), testUser.getId());
        filmService.unlikeFilm(createdFilm.getId(), testUser.getId());

        assertThat(createdFilm.getLikes()).isEqualTo(0);
        assertThat(testUser.getLikedFilms()).doesNotContain(createdFilm.getId());
    }

    @Test
    void testGetPopularFilms() {
        Film film1 = filmService.createFilm(testFilm);

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Desc2");
        film2.setDuration(100);
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2 = filmService.createFilm(film2);

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));
        userService.createUser(user2);

        filmService.likeFilm(film1.getId(), testUser.getId());
        filmService.likeFilm(film2.getId(), testUser.getId());
        filmService.likeFilm(film2.getId(), user2.getId());

        List<Film> popular = filmService.getPopularFilms(2);
        assertThat(popular.get(0).getId()).isEqualTo(film2.getId());
        assertThat(popular.get(1).getId()).isEqualTo(film1.getId());
    }
}
