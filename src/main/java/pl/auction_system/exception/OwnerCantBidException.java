package pl.auction_system.exception;

public class OwnerCantBidException extends RuntimeException {
    public OwnerCantBidException(String ownerUsername) {
        super("User with username: " + ownerUsername + " can not bid own auction");
    }
}
