package pl.auction_system.exception;

public class UserNotFoundByUserNumberException extends RuntimeException {
    public UserNotFoundByUserNumberException(String userNumber) {
        super("User with user number: " + userNumber + " does not exist");
    }
}
