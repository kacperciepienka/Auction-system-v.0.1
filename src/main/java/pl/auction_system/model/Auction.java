package pl.auction_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "auction")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Length(min = 12, max = 25)
    private String referenceNumber;

    @NotBlank(message = "Auction title can't be empty")
    @Column(length = 100)
    @Length(min = 1, max = 100, message = "Title max character is 100")
    private String title;

    @NotBlank(message = "Auction description can't be empty")
    private String description;

    @NotNull(message = "Auction category can't be empty")
    @Enumerated(EnumType.STRING)
    private AuctionCategory auctionCategory;

    @NotNull(message = "Auction starting price can't be empty")
    @DecimalMin(value = "0.00", message = "Starting price can't be lesser than 0.00PLN")
    private BigDecimal startingPrice;

    // zawsze ma cene od razu przy tworzeniu
    @DecimalMin(value = "0.00", message = "Starting price can't be lesser than 0.00PLN")
    private BigDecimal currentPrice;

    // automatycznie nadpisywane w serwisie
    private LocalDateTime startTime;

    // automatycznie nadpisywane w serwisie
    private LocalDateTime endTime;

    // automatycznie nadpisywane w serwisie
    @Enumerated(EnumType.STRING)
    private AuctionStatus auctionStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}
