package pl.auction_system.exception;

public class InvalidNewTitleException extends RuntimeException {
    public InvalidNewTitleException(String newTitle) {
        super("Your new title: " + newTitle + " is blank or invalid (max length: 100 characters)");
    }
}
