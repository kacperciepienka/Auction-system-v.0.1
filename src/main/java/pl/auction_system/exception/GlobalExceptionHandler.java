package pl.auction_system.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.auction_system.dto.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //400 - BAD_REQUEST
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidHandler(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult()
                .getFieldErrors()
                .getFirst()
                .getDefaultMessage();

        log.error("Bad Request Exception: {}", message);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BAD_REQUEST")
                .message(message)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //404 - NOT_FOUND
    @ExceptionHandler({
            UserNotFoundByUsernameException.class,
            UserNotFoundByUserNumberException.class,
            UserNotFoundByEmailException.class,
            BidNotFoundByIdNumberException.class,
            AuctionNotFoundByReferenceNumber.class
    })
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(RuntimeException ex){

        log.error("Not Found Exception: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    //409 - CONFLICT
    @ExceptionHandler({
            UserUsernameAlreadyExistException.class,
            UserEmailAlreadyExistException.class,
    })
    public ResponseEntity<ErrorResponse> conflictExceptionHandler(RuntimeException ex){

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("CONFLICT")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    //422 - UNPROCESSABLE_CONFLICT
    @ExceptionHandler({
            InvalidNewTitleException.class,
            AuctionIsAlreadyFinishedException.class,
            BadBidException.class,
            InvalidBidException.class,
            CantChangeStartingPriceException.class,
            OwnerCantBidException.class,

    })
    public ResponseEntity<ErrorResponse> unprocessableConflictException(RuntimeException ex){

        log.error("Unprocessable Conflict Exception: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_CONTENT.value())
                .error("UNPROCESSABLE_CONFLICT")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
