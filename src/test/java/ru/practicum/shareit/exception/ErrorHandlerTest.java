package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleWrongParameterException() {
        WrongParameterException e = new WrongParameterException("Возникла ошибка 400");
        ErrorResponse er = errorHandler.handleWrongParameterException(e);

        assertNotNull(er);
        assertEquals("Возникла ошибка 400", er.getError());
    }

    @Test
    void handleAccessDeniedException() {
        AccessDeniedException e = new AccessDeniedException("Возникла ошибка 404");
        ErrorResponse er = errorHandler.handleAccessDeniedException(e);

        assertNotNull(er);
        assertEquals("Возникла ошибка 404", er.getError());
    }

    @Test
    void handleObjectNotFoundException() {
        ObjectNotFoundException e = new ObjectNotFoundException("Возникла ошибка 404");
        ErrorResponse er = errorHandler.handleObjectNotFoundException(e);

        assertNotNull(er);
        assertEquals("Возникла ошибка 404", er.getError());
    }

    @Test
    void handleValidationException() {
        ValidationException e = new ValidationException("Возникла ошибка 409");
        ErrorResponse er = errorHandler.handleValidationException(e);

        assertNotNull(er);
        assertEquals("Возникла ошибка 409", er.getError());
    }

    @Test
    void handleOtherException() {
        Throwable e = new Throwable("Возникла непредвиденная ошибка");
        ErrorResponse er = errorHandler.handleOtherException(e);

        assertNotNull(er);
        assertEquals("Возникла непредвиденная ошибка", er.getError());
    }
}