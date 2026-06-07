package pl.auction_system.exception;

public class UserNotFoundByEmailException extends RuntimeException {
    public UserNotFoundByEmailException(String email) {
        super("User with email: " + email + " does not exist");
    }
}
