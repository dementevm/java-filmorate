package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotations.MinDate;

import java.time.LocalDate;

@Data
public class Film {
    int id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    String description;

    @MinDate(value = "1895-12-28")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    int duration;
}
