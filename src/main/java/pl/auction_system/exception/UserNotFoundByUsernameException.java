package pl.auction_system.exception;

public class UserNotFoundByUsernameException extends RuntimeException {
    public UserNotFoundByUsernameException(String username) {
        super("User with username: " + username + " does not exist");
    }
}
