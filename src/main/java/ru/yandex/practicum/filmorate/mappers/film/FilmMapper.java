package ru.yandex.practicum.filmorate.mappers.film;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setLikes(0);
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setLikes(film.getLikes());
        if (film.getLikesFromUsers() != null) {
            dto.setLikesFromUsers(film.getLikesFromUsers());
        }

        MpaRating mpa = film.getMpa();
        if (mpa != null) {
            Map<String, Object> mpaMap = new LinkedHashMap<>(2);
            mpaMap.put("id", mpa.getId());
            mpaMap.put("name", mpa.getName());
            dto.setMpa(mpaMap);
        }

        if (film.getGenres() != null) {
            List<Genre> genresSorted = new ArrayList<>(film.getGenres());
            genresSorted.sort(Comparator.comparing(Genre::getId));
            Set<Short> addedGenres = new LinkedHashSet<>();
            List<Map<String, Object>> genreViews = new ArrayList<>();
            for (Genre g : genresSorted) {
                if (g == null || g.getId() == null) continue;
                Short id = g.getId();
                if (addedGenres.add(id)) {
                    Map<String, Object> m = new LinkedHashMap<>(2);
                    m.put("id", id);
                    m.put("name", g.getName());
                    genreViews.add(m);
                }
            }
            dto.setGenres(genreViews);
        } else {
            dto.setGenres(Collections.emptyList());
        }
        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasMpa()) {
            film.setMpa(request.getMpa());
        }
        if (request.hasLikes()) {
            film.setLikes(request.getLikes());
        }
        return film;
    }
}
