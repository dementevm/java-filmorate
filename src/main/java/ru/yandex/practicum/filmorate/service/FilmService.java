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
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    public FilmDto createFilm(NewFilmRequest request) {
        mpaRepository.findById(request.getMpaId())
                .orElseThrow(() -> new ObjectNotFoundException("MPA id=%d не найден".formatted(request.getMpaId())));
        List<Short> genreIds = getGenreIdsForFilm(request.getGenreIds());
        validateGenresExist(genreIds);

        Film film = FilmMapper.mapToFilm(request);
        Film saved = filmRepository.save(film, request.getMpaId(), genreIds);
        return FilmMapper.mapToFilmDto(saved);
    }


    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film current = filmRepository.findById(request.getId())
                .orElseThrow(() -> new ObjectNotFoundException("Фильм id=%d не найден".formatted(request.getId())));

        short mpaId = request.getMpa().getId();
        mpaRepository.findById(mpaId)
                .orElseThrow(() -> new ObjectNotFoundException("MPA id=%d не найден".formatted(mpaId)));
        List<Short> genreIds = getGenreIdsForFilm(request.getGenreIds());
        validateGenresExist(genreIds);

        FilmMapper.updateFilmFields(current, request);
        Film updated = filmRepository.update(current, (short) mpaId, genreIds);
        return FilmMapper.mapToFilmDto(updated);
    }

    private List<Short> getGenreIdsForFilm(List<Short> rawIds) {
        if (rawIds == null || rawIds.isEmpty()) return List.of();
        return rawIds.stream().filter(Objects::nonNull).distinct().toList();
    }

    private void validateGenresExist(List<Short> ids) {
        if (ids == null || ids.isEmpty()) return;
        List<Genre> found = genreRepository.findAllByIds(ids);
        Set<Short> existingGenreIds = found.stream().map(Genre::getId).collect(Collectors.toSet());
        for (Short gid : ids) {
            if (!existingGenreIds.contains(gid)) {
                throw new ObjectNotFoundException("Жанр id=%d не найден".formatted(gid));
            }
        }
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
