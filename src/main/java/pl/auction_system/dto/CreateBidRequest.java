package pl.auction_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateBidRequest {
    @NotNull(message = "Bid amount can't be empty")
    @DecimalMin(value = "0.01", message = "Min. bid is 0.01PLN")
    private BigDecimal amount;
}
