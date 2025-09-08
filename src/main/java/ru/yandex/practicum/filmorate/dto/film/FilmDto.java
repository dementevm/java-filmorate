package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotations.MinDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @MinDate(value = "1895-12-28")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;
    private Integer likes;
    private Map<String, Object> mpa;
    private List<Map<String, Object>> genres;
    private List<Long> likesFromUsers;
}
