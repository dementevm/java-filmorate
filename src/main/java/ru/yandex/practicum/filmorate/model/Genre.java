package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Genre {
    private Short id;

    @Size(max = 20)
    private String name;
}
