package pl.auction_system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidAdminDto {
    private String bidIdNumber;
    private String bidderNumber;
    private String bidderUsername;
    private String bidderEmail;
    private BigDecimal amount;
    private LocalDateTime bidTime;
    private String auctionReferenceNumber;
    private String auctionTitle;
}

