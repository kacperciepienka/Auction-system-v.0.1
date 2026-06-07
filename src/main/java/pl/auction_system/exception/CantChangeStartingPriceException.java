package pl.auction_system.exception;

public class CantChangeStartingPriceException extends RuntimeException {
    public CantChangeStartingPriceException(String referenceNumber) {
        super("You can not change starting price of Auction ref. number: " + referenceNumber +
                " because someone already bided");
    }
}
