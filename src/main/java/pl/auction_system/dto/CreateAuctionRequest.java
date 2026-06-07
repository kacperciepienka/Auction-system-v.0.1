package pl.auction_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import pl.auction_system.model.AuctionCategory;

import java.math.BigDecimal;

@Data
public class CreateAuctionRequest {
    @NotBlank(message = "Auction title can't be empty")
    @Length(min = 1, max = 100, message = "Title max character is 100")
    private String title;

    @NotBlank(message = "Auction description can't be empty")
    private String description;

    @NotNull(message = "Auction category can't be empty")
    private AuctionCategory auctionCategory;

    @NotNull(message = "Auction starting price can't be empty")
    @DecimalMin(value = "0.00", message = "Starting price can't be lesser than 0.00PLN")
    private BigDecimal startingPrice;
}
