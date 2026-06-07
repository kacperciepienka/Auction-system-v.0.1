package pl.auction_system.exception;

import java.math.BigDecimal;

public class InvalidBidException extends RuntimeException {
    public InvalidBidException(BigDecimal amount) {
      super("Your bid: " + amount + " PLN is invalid");
    }
}
