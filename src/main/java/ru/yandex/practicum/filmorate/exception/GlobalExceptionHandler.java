package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.warn("Ошибка валидации в поле '{}': {}", fieldName, errorMessage);

        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        Map<String, String> error = Map.of("error", ex.getMessage());
        log.warn("Ошибка при создании пользователя - {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleObjectNotFoundException(ObjectNotFoundException ex) {
        Map<String, String> error = Map.of("error", ex.getMessage());
        log.warn("Ошибка при обновлении объекта - {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        UUID uuid = UUID.randomUUID();
        if (stackTrace.length > 0) {
            StackTraceElement stackTraceElement = stackTrace[0];
            int lineNumber = stackTraceElement.getLineNumber();
            String fileName = stackTraceElement.getFileName();
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            log.warn("Произошла необработанная ошибка: id ошибки: {}, fileName: {}, className: {}, " +
                    "methodName: {}, lineNumber: {}, errorClass: {}, " +
                    "errorText: {}", uuid, fileName, className, methodName, lineNumber, ex.getClass(), ex.getMessage());
        } else {
            log.warn("Произошла необработанная ошибка - {}, id ошибки {}", ex.getMessage(), uuid);
        }
        error.put("error", "Произошла ошибка на сервере, id ошибки: " + uuid);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
