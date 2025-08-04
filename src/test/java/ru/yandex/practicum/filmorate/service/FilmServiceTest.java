package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FilmServiceTest {
    private FilmService filmService;
    private Film testFilm;

    @BeforeEach
    void setUp() {
        filmService = new FilmService();
        testFilm = new Film();
        testFilm.setName("TestFilm");
        testFilm.setDescription("TestDescription");
        testFilm.setDuration(120);
        testFilm.setReleaseDate(LocalDate.of(1999, 1, 1));
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
        testFilm.setId(999);

        assertThatThrownBy(() -> filmService.updateFilm(testFilm))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Фильма с ID 999 не существует");
    }
}
