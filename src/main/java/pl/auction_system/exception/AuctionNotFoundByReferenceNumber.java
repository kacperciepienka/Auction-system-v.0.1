package pl.auction_system.exception;

public class AuctionNotFoundByReferenceNumber extends RuntimeException {
    public AuctionNotFoundByReferenceNumber(String referenceNumber) {
        super("Auction with reference number: " + referenceNumber + " does not exist");
    }
}
