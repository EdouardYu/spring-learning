package edouard.yu.springbootlearning.controller.advice;

import edouard.yu.springbootlearning.dto.ErrorEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

// Troisième méthode pour gérer les exceptions : @ControllerAdvice (gestion d'erreur pour tout l'API : recommandé)
@ControllerAdvice
public class ApplicationControllerAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class}) // permet de définir quels sont les exceptions qu'on traite avec cette méthode
    // On peut bien sûr traiter plusieurs types d'exception
    // @ResponseBody permet de retourner la méthode dans le body de la réponse
    public @ResponseBody ErrorEntity handleNotFoundException(EntityNotFoundException e) {
        return new ErrorEntity(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
