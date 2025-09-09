package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotations.MinDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 100)
    private String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @MinDate(value = "1895-12-28")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration;

    private int likes = 0;

    public void increaseLikes() {
        likes++;
    }

    public void decreaseLikes() {
        likes--;
    }

    private Set<Genre> genres = new HashSet<>();

    private MpaRating mpa;

    private List<Long> likesFromUsers = new ArrayList<>();

}
