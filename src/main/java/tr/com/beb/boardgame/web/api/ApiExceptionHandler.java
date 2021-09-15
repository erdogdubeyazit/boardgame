package tr.com.beb.boardgame.web.api;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tr.com.beb.boardgame.web.results.ApiResult;
import tr.com.beb.boardgame.web.results.Result;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity<ApiResult> handle(RuntimeException e) {
        String errorReferenceCode = UUID.randomUUID().toString();
        logger.error("Unhandled exception error [code=" + errorReferenceCode + "]", e);
        return Result.serverError("Error on the server side.", errorReferenceCode);
    }
}
