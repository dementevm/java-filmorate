package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Validated
public class MpaController {
    private final MpaRatingRepository mpaRepository;

    public MpaController(MpaRatingRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    @GetMapping
    public List<MpaRating> getAll() {
        return mpaRepository.findAll();
    }

    @GetMapping("/{id}")
    public MpaRating getById(@PathVariable("id") @Positive short id) {
        return mpaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Рейтинг с id=%d не найден".formatted(id)));
    }
}
