package pl.auction_system.exception;

import java.math.BigDecimal;

public class BadBidException extends RuntimeException {
    public BadBidException(BigDecimal amount) {
        super("Your bid: " + amount + " PLN is lower or equal as current bid");
    }
}
