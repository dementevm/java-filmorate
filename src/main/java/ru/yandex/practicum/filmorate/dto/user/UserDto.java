package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Positive
    private long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть валидным адресом")
    @Size(max = 50)
    private String email;

    @NotBlank(message = "Login не может быть пустым")
    @Size(max = 20)
    private String login;

    @Size(max = 30)
    private String name;

    @PastOrPresent(message = "Дата не может быть в будущем.")
    private LocalDate birthday;
}
