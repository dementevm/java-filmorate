package ru.yandex.practicum.filmorate.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validation.validators.MinDateValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinDate {
    String message() default "Дата не может быть раньше - {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();
}