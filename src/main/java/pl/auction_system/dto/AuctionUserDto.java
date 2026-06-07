package pl.auction_system.dto;

import lombok.Data;
import pl.auction_system.model.AuctionCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AuctionUserDto {
    private String referenceNumber;
    private String title;
    private String description;
    private AuctionCategory auctionCategory;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String ownerUsername;
    private String ownerEmail;
}
