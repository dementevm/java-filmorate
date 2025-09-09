package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FilmServiceTest {

    @InjectMocks
    private FilmService filmService;

    @Mock
    private FilmRepository filmRepository;
    @Mock
    private FilmLikeRepository filmLikeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private MpaRatingRepository mpaRatingRepository;

    private NewFilmRequest newFilm;

    @BeforeEach
    void setUp() {
        newFilm = new NewFilmRequest();
        newFilm.setName("Test Film");
        newFilm.setDescription("Desc");
        newFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        newFilm.setDuration(100);
        newFilm.setMpaFromJson(java.util.Map.of("id", 1));
    }

    @Test
    @DisplayName("Создание фильма")
    void testCreateFilmSuccess() {
        MpaRating mpa = new MpaRating();
        mpa.setId((short) 1);
        mpa.setName("G");

        when(mpaRatingRepository.findById(1L)).thenReturn(Optional.of(mpa));
        when(filmRepository.save(any(Film.class), eq(1L), any())).thenAnswer(inv -> {
            Film f = inv.getArgument(0);
            f.setId(1L);
            return f;
        });

        FilmDto created = filmService.createFilm(newFilm);

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getName()).isEqualTo("Test Film");
        verify(filmRepository).save(any(Film.class), eq(1L), any());
    }

    @Test
    @DisplayName("Лайк фильму")
    void testAddLike() {
        Film f = new Film();
        f.setId(1L);
        when(filmRepository.findById(1L)).thenReturn(Optional.of(f));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        filmService.addLike(1L, 2L);

        verify(filmLikeRepository).addLike(1L, 2L);
    }

    @Test
    @DisplayName("Популярные фильмы сортируются по лайкам")
    void testGetPopularFilms() {
        Film f1 = new Film();
        f1.setId(1L);
        f1.setName("A");
        f1.setLikes(1);

        Film f2 = new Film();
        f2.setId(2L);
        f2.setName("B");
        f2.setLikes(3);

        when(filmRepository.findAll()).thenReturn(List.of(f1, f2));

        List<FilmDto> popular = filmService.getPopularFilms(2);

        assertThat(popular).hasSize(2);
        assertThat(popular.get(0).getId()).isEqualTo(2L);
        assertThat(popular.get(1).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Исключение, если фильм не найден при лайке")
    void testAddLikeFilmNotFound() {
        when(filmRepository.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> filmService.addLike(100L, 1L))
                .isInstanceOf(ObjectNotFoundException.class);
    }
}
