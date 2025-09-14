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
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private NewUserRequest createReq;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        createReq = new NewUserRequest();
        createReq.setEmail("user@example.com");
        createReq.setLogin("user");
        createReq.setName("User");
        createReq.setBirthday(LocalDate.of(1990, 1, 1));

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("user@example.com");
        userDto.setLogin("user");
        userDto.setName("User");
        userDto.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Создать пользователя")
    void testCreateUser() throws Exception {
        when(userService.createUser(any(NewUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("Обновить пользователя")
    void testUpdateUser() throws Exception {
        UpdateUserRequest upd = new UpdateUserRequest();
        upd.setId(1L);
        upd.setEmail("user@example.com");
        upd.setLogin("user");
        upd.setName("New");
        upd.setBirthday(LocalDate.of(1990, 1, 1));

        UserDto updated = new UserDto();
        updated.setId(1L);
        updated.setEmail("user@example.com");
        updated.setLogin("user");
        updated.setName("New");
        updated.setBirthday(LocalDate.of(1990, 1, 1));

        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.login").value("user"));
    }

    @Test
    @DisplayName("Получить всех пользователей")
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("Добавить друга")
    void testAddFriend() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", 1, 2))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Список друзей пользователя")
    void testGetFriends() throws Exception {
        when(userService.getUserFriends(1L)).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users/{id}/friends", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
