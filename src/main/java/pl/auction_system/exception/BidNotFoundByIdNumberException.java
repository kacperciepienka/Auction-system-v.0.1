package pl.auction_system.exception;

public class BidNotFoundByIdNumberException extends RuntimeException {
    public BidNotFoundByIdNumberException(String idNumber) {
        super("Bid with id number: " + idNumber + " does not exist");
    }
}
