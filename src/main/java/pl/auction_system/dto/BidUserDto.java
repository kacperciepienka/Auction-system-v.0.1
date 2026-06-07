package pl.auction_system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidUserDto {
    private String bidIdNumber;
    private String bidderUsername;
    private BigDecimal amount;
    private LocalDateTime bidTime;
    private String auctionReferenceNumber;
    private String auctionTitle;
}
