package pl.auction_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bid")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Length(min = 8, max = 25)
    private String bidIdNumber;

    @NotNull(message = "Bid amount can't be empty")
    @DecimalMin(value = "0.01", message = "Min. bid is 0.01PLN")
    private BigDecimal amount;

    // nadpisywane w serwisie
    private LocalDateTime bidTime;

    @ManyToOne
    @JoinColumn(name = "bidder_id")
    private User bidder;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;
}
