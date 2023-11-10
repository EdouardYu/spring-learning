package edouard.yu.springsecuritylearning.controller.advice;

import edouard.yu.springsecuritylearning.dto.ErrorEntity;
import edouard.yu.springsecuritylearning.exception.AlreadyProcessedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationControllerAdvice {
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({RuntimeException.class})
    public @ResponseBody ErrorEntity handleRuntimeException(RuntimeException e) {
        return new ErrorEntity(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler({AlreadyProcessedException.class})
    public @ResponseBody ErrorEntity handleAlreadyProcessedException(AlreadyProcessedException e) {
        return new ErrorEntity(HttpStatus.CONFLICT.value(), e.getMessage());
    }
}
