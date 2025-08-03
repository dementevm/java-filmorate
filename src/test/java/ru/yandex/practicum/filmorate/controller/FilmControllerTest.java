package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
    }

    @Test
    void testGetFilms() throws Exception {
        when(filmService.getAllFilms()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testCreateFilmSuccess() throws Exception {
        when(filmService.createFilm(any(Film.class))).thenReturn(testFilm);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Film"));
    }

    @Test
    void testCreateFilmInvalidName() throws Exception {
        testFilm.setName("");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Название не может быть пустым"));
    }

    @Test
    void testCreateFilmTooLongDescription() throws Exception {
        testFilm.setDescription("a".repeat(201));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Максимальная длина описания - 200 символов"));
    }

    @Test
    void testCreateFilmInvalidReleaseDate() throws Exception {
        testFilm.setReleaseDate(LocalDate.of(1800, 1, 1));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.releaseDate").value("Дата не может быть раньше - 1895-12-28"));
    }

    @Test
    void testCreateFilmNegativeDuration() throws Exception {
        testFilm.setDuration(-1);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.duration").value("Продолжительность должна быть положительным числом"));
    }

    @Test
    void testUpdateFilmSuccess() throws Exception {
        when(filmService.updateFilm(any(Film.class))).thenReturn(testFilm);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Film"));
    }
}
