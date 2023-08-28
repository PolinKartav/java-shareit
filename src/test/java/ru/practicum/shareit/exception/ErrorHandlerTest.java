package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ValidationException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {
    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void testHandleNotFound() {
        NotFoundException notFoundException = new NotFoundException("Не найден");
        Map<String, String> result = errorHandler.handleNotFound(notFoundException);
        assertEquals("Не найден", result.get("error"));
    }

    @Test
    void testHandleConflict() {
        AlreadyExistedException alreadyExistsException = new AlreadyExistedException("Уже существует");
        Map<String, String> result = errorHandler.handleAlreadyExists(alreadyExistsException);
        assertEquals("Уже существует", result.get("error"));
    }

    @Test
    void testHandleInternalError() {
        Throwable throwable = new Throwable("Ошибка приложения");
        Map<String, String> result = errorHandler.handleThrowable(throwable);
        assertEquals("Ошибка приложения", result.get("error"));
    }

    @Test
    void testHandleArgumentException() {
        ArgumentException argumentException = new ArgumentException("Некорректный параметр");
        Map<String, String> result = errorHandler.handleServletRequestBinding(argumentException);
        assertEquals("Некорректный параметр", result.get("error"));
    }

    @Test
    void testHandleRuntimeException() {
        ValidationException exception = new ValidationException("Некорректный параметр");
        Map<String, String> result = errorHandler.handleRuntime(exception);
        assertEquals("Некорректный параметр", result.get("error"));
    }
}