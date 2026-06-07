package pl.auction_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.auction_system.model.Bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    // Optional: to jest klucz naturalny
    Optional<Bid> findByBidIdNumberEqualsIgnoreCase(String bidIdNumber);

    // by bidder
        Page<Bid> findAllByBidder_UsernameEqualsIgnoreCase(String bidderUsername, Pageable pageable);
        Page<Bid> findAllByBidder_UserNumberEqualsIgnoreCase(String bidderUserNumber, Pageable pageable);

    // by amount of bid
        Page<Bid> findAllByAmountIsLessThanEqual(BigDecimal amountIsLessThan, Pageable pageable);
        Page<Bid> findAllByAmountIsGreaterThanEqual(BigDecimal amountIsGreaterThan, Pageable pageable);
        Page<Bid> findAllByAmountBetween(BigDecimal amountAfter, BigDecimal amountBefore, Pageable pageable);

    // by amount of bid with username
        Page<Bid> findAllByBidder_UsernameAndAmountIsLessThanEqual(String bidderUsername, BigDecimal amountIsLessThan, Pageable pageable);
        Page<Bid> findAllByBidder_UsernameAndAmountIsGreaterThanEqual(String bidderUsername, BigDecimal amountIsGreaterThan, Pageable pageable);
        Page<Bid> findAllByBidder_UsernameAndAmountBetween(String bidderUsername, BigDecimal amountAfter, BigDecimal amountBefore, Pageable pageable);

    // by date of bid
        Page<Bid> findAllByBidTime(LocalDateTime bidTime, Pageable pageable); // for exact date
        Page<Bid> findAllByBidTimeIsAfter(LocalDateTime bidTimeAfter, Pageable pageable);
        Page<Bid> findAllByBidTimeIsBefore(LocalDateTime bidTimeBefore, Pageable pageable);
        Page<Bid> findAllByBidTimeIsBetween(LocalDateTime bidTimeAfter, LocalDateTime bidTimeBefore, Pageable pageable);

    // by date of bid
        Page<Bid> findAllByBidder_UsernameAndBidTime(String bidderUsername, LocalDateTime bidTime, Pageable pageable);
        Page<Bid> findAllByBidder_UsernameAndBidTimeIsAfter(String bidderUsername, LocalDateTime bidTimeAfter, Pageable pageable);
        Page<Bid> findAllByBidder_UsernameAndBidTimeIsBefore(String bidderUsername, LocalDateTime bidTimeBefore, Pageable pageable);
        Page<Bid> findAllByBidder_UsernameAndBidTimeIsBetween(String bidderUsername, LocalDateTime bidTimeAfter, LocalDateTime bidTimeBefore, Pageable pageable);

    // by auction
        Page<Bid> findAllByAuction_ReferenceNumberEqualsIgnoreCase(String auctionReferenceNumber, Pageable pageable);
        Page<Bid> findAllByAuction_TitleContainingIgnoreCase(String title, Pageable pageable);
}
