package pl.auction_system.exception;

public class AuctionIsAlreadyFinishedException extends RuntimeException {
    public AuctionIsAlreadyFinishedException(String title, String referenceNumber) {
        super("You can not bid auction: " + title + " | reference number: " + referenceNumber + ", because it is already over" );
    }
}
