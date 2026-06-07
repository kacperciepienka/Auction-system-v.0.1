package pl.auction_system.exception;

public class UserEmailAlreadyExistException extends RuntimeException {
    public UserEmailAlreadyExistException(String email) {
        super("User with email: " + email + " already exist");
    }
}
