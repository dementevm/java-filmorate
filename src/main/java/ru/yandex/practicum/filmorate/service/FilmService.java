package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.mappers.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final MpaRatingRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final FilmLikeRepository filmLikeRepository;

    public FilmService(FilmRepository filmRepository, MpaRatingRepository mpaRepository,
                       GenreRepository genreRepository, UserRepository userRepository,
                       FilmLikeRepository filmLikeRepository) {
        this.filmRepository = filmRepository;
        this.mpaRepository = mpaRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.filmLikeRepository = filmLikeRepository;
    }

    public FilmDto getFilm(long id) {
        return filmRepository.findById(id)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new ObjectNotFoundException("Фильм с id=%d не найден".formatted(id)));
    }

    public List<FilmDto> getAllFilms() {
        return filmRepository.findAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto createFilm(NewFilmRequest r) {
        mpaRepository.findById(r.getMpaId())
                .orElseThrow(() -> new ObjectNotFoundException("MPA id=%d не найден".formatted(r.getMpaId())));
        List<Short> genreIds = r.getGenreIds() == null ? List.of()
                : r.getGenreIds().stream().toList();
        for (Short gid : genreIds) {
            genreRepository.findById(gid).orElseThrow(() -> new ObjectNotFoundException("Жанр id=%d не найден".formatted(gid)));
        }

        Film toSave = FilmMapper.mapToFilm(r);
        Film saved = filmRepository.save(toSave, r.getMpaId(), genreIds);
        return FilmMapper.mapToFilmDto(saved);
    }

    public FilmDto updateFilm(UpdateFilmRequest r) {
        Film current = filmRepository.findById(r.getId())
                .orElseThrow(() -> new ObjectNotFoundException("Фильм id=%d не найден".formatted(r.getId())));
        mpaRepository.findById(r.getMpa().getId())
                .orElseThrow(() -> new ObjectNotFoundException("MPA id=%d не найден".formatted(r.getMpa().getId())));
        List<Short> genreIds = r.getGenreIds() == null ? List.of()
                : r.getGenreIds().stream().toList();

        FilmMapper.updateFilmFields(current, r);
        Film updated = filmRepository.update(current, r.getMpa().getId(), genreIds);
        return FilmMapper.mapToFilmDto(updated);
    }

    public List<FilmDto> getPopularFilms(int count) {
        return filmRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public void addLike(long filmId, long userId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new ObjectNotFoundException("Фильм id=%d не найден".formatted(filmId)));
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь id=%d не найден".formatted(userId)));

        filmLikeRepository.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new ObjectNotFoundException("Фильм id=%d не найден".formatted(filmId)));
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь id=%d не найден".formatted(userId)));

        filmLikeRepository.removeLike(filmId, userId);
    }
}
