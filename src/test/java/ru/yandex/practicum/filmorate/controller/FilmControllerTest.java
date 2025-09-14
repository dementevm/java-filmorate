package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@AutoConfigureMockMvc(addFilters = false)
class FilmControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private FilmService filmService;

    private FilmDto filmDto;
    private Map<String, Object> validCreate;
    private Map<String, Object> validUpdate;

    @BeforeEach
    void setUp() {
        filmDto = new FilmDto();
        filmDto.setId(1L);
        filmDto.setName("Test Film");
        filmDto.setDescription("Description");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);
        filmDto.setLikes(0);
        filmDto.setMpa(new LinkedHashMap<>(Map.of("id", (short)1, "name", "G")));
        List<Map<String, Object>> genres = new ArrayList<>();
        genres.add(new LinkedHashMap<>(Map.of("id", (short)2, "name", "Драма")));
        filmDto.setGenres(genres);
        filmDto.setLikesFromUsers(Collections.emptyList());

        validCreate = new LinkedHashMap<>();
        validCreate.put("name", "Test Film");
        validCreate.put("description", "Description");
        validCreate.put("releaseDate", "2000-01-01");
        validCreate.put("duration", 120);
        validCreate.put("mpa", Map.of("id", 1));
        validCreate.put("genres", List.of(Map.of("id", 2)));

        validUpdate = new LinkedHashMap<>(validCreate);
        validUpdate.put("id", 1);
    }

    @Test
    @DisplayName("Полчить фильм")
    void testGetFilms() throws Exception {
        when(filmService.getAllFilms()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Создать фильм")
    void testCreateFilmSuccess() throws Exception {
        when(filmService.createFilm(any(NewFilmRequest.class))).thenReturn(filmDto);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Film"))
                .andExpect(jsonPath("$.mpa.id").value(1))
                .andExpect(jsonPath("$.genres[0].id").value(2));
    }

    @Test
    @DisplayName("Создать фильм с неправильным названием")
    void testCreateFilmInvalidName() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>(validCreate);
        body.put("name", "");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Название не может быть пустым"));
    }

    @Test
    @DisplayName("Добавить фильм с слишком длинным описанием")
    void testCreateFilmTooLongDescription() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>(validCreate);
        body.put("description", "a".repeat(201));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Максимальная длина описания - 200 символов"));
    }

    @Test
    @DisplayName("Добавить фильм с неправильной датой")
    void testCreateFilmInvalidReleaseDate() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>(validCreate);
        body.put("releaseDate", "1800-01-01");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.releaseDate").value("Дата не может быть раньше - 1895-12-28"));
    }

    @Test
    @DisplayName("Добавить фильм с отрицательной продолжительностью")
    void testCreateFilmNegativeDuration() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>(validCreate);
        body.put("duration", -1);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.duration").value("Продолжительность должна быть положительным числом"));
    }

    @Test
    @DisplayName("Обновить фильм")
    void testUpdateFilmSuccess() throws Exception {
        when(filmService.updateFilm(any(UpdateFilmRequest.class))).thenReturn(filmDto);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Film"))
                .andExpect(jsonPath("$.id").value(1));
    }
}
