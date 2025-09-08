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
public class NewFilmRequest {
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @MinDate(value = "1895-12-28")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;

    private short mpaId;

    private List<Short> genreIds;

    @JsonProperty("mpa")
    public void setMpaFromJson(Map<String, Object> mpa) {
        if (mpa == null) return;
        Object id = mpa.get("id");
        if (id instanceof Number n) this.mpaId = n.shortValue();
    }

    @JsonProperty("genres")
    public void setGenresFromJson(List<Map<String, Object>> genres) {
        if (genres == null) {
            this.genreIds = List.of();
            return;
        }
        var uniq = new java.util.LinkedHashSet<Short>();
        for (Map<String, Object> g : genres) {
            if (g == null) continue;
            Object id = g.get("id");
            if (id instanceof Number n) {
                uniq.add(n.shortValue());
            } else if (id instanceof String s && !s.isBlank()) {
                try {
                    uniq.add(Short.parseShort(s.trim()));
                } catch (NumberFormatException ignore) {
                }
            }
        }
        this.genreIds = new java.util.ArrayList<>(uniq);
    }
}
