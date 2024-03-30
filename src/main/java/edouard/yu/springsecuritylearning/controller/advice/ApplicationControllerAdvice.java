package edouard.yu.springsecuritylearning.controller.advice;

import edouard.yu.springsecuritylearning.dto.ErrorEntity;
import edouard.yu.springsecuritylearning.exception.AlreadyProcessedException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
// @RestControllerAdvice est une spécialisation de @ControllerAdvice pour les API RESTFUL
@RestControllerAdvice
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

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({BadCredentialsException.class})
    public @ResponseBody ProblemDetail handleBadCredentialsException(BadCredentialsException e) {
        log.error(e.getMessage(), e);
        // Entité permettant de retourner une exception sous forme d'objet de façon complet
        // et qui correspond à un standard d'entité lié à la sécurité
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        problemDetail.setProperty("error", "We were unable to identify you");
        // Une URL qui renvoie une page permettant d'identifier le type de problème.
        // Elle devrait fournir de l'information sur l'erreur et comment la résoudre si possible.
        //problemDetail.setType("https://example.com/probs/custom-error");

        return problemDetail;
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({SignatureException.class, MalformedJwtException.class})
    public @ResponseBody ProblemDetail handleSignatureExceptionException(Exception e) {
        log.error(e.getMessage(), e);
        // Entité permettant de retourner une exception sous forme d'objet de façon complet
        // et qui correspond à un standard d'entité lié à la sécurité
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        problemDetail.setProperty("error", "We were unable to identify you");
        // Une URL qui renvoie une page permettant d'identifier le type de problème.
        // Elle devrait fournir de l'information sur l'erreur et comment la résoudre si possible.
        //problemDetail.setType("https://example.com/probs/custom-error");

        return problemDetail;
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public @ResponseBody ErrorEntity handleAccessDeniedException(AccessDeniedException e) {
        return new ErrorEntity(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    // Autre manière de faire :
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({Exception.class})
    public Map<String, String> exceptionsHandler(Exception e) {
        log.error(e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }
}
