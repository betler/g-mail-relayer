package pro.cvitae.gmailrelayer.web;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author https://mkyong.com/spring-boot/spring-rest-validation-example/
 *
 */
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * error handle for @Valid
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

        final Map<String, Object> body = this.getDefaultBody(status);

        List<String> errors;
        if (ex.getBindingResult().hasFieldErrors()) {
            // Field errors for field annotations
            errors = ex.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + " " + fe.getDefaultMessage()).collect(Collectors.toList());
        } else {
            // Global errors for class annotations
            errors = ex.getBindingResult().getAllErrors().stream()
                    .map(oe -> oe.getObjectName() + " " + oe.getDefaultMessage()).collect(Collectors.toList());

        }

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }

    private Map<String, Object> getDefaultBody(final HttpStatus status) {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        return body;
    }

    /**
     * Builds a {@link ResponseEntity} with the given body and a
     * {@link HttpStatus#INTERNAL_SERVER_ERROR} status code
     *
     * @param <T>
     * @param body
     * @return
     */
    private <T> ResponseEntity<T> error500(final T body) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}