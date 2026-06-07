package pl.auction_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.auction_system.model.Auction;
import pl.auction_system.model.AuctionCategory;
import pl.auction_system.model.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    // Optional: to jest klucz naturalny
    Optional<Auction> findAuctionByReferenceNumberEqualsIgnoreCase(String referenceNumber);

    Page<Auction> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Auction> findAllByAuctionCategory(AuctionCategory auctionCategory, Pageable pageable);

    // by current price
        Page<Auction> findAllByCurrentPriceIsLessThanEqual(BigDecimal currentPriceIsLessThan, Pageable pageable);
        Page<Auction> findAllByCurrentPriceIsGreaterThanEqual(BigDecimal currentPriceIsGreaterThan, Pageable pageable);
        Page<Auction> findAllByCurrentPriceBetween(BigDecimal currentPriceAfter, BigDecimal currentPriceBefore, Pageable pageable);

    // by start date
        Page<Auction> findAllByStartTime(LocalDateTime startTime, Pageable pageable);
        Page<Auction> findAllByStartTimeIsAfter(LocalDateTime startTimeAfter, Pageable pageable);
        Page<Auction> findAllByStartTimeIsBefore(LocalDateTime startTimeBefore, Pageable pageable);
        Page<Auction> findAllByStartTimeIsBetween(LocalDateTime startTimeAfter, LocalDateTime startTimeBefore, Pageable pageable);

    // by end date
        Page<Auction> findAllByEndTime(LocalDateTime endTime, Pageable pageable); // for exact date
        Page<Auction> findAllByEndTimeIsAfter(LocalDateTime endTimeAfter, Pageable pageable);
        Page<Auction> findAllByEndTimeIsBefore(LocalDateTime endTimeBefore, Pageable pageable);
        Page<Auction> findAllByEndTimeIsBetween(LocalDateTime endTimeAfter, LocalDateTime endTimeBefore, Pageable pageable);

    Page<Auction> findAllByAuctionStatus(AuctionStatus auctionStatus, Pageable pageable);

    // by Owner
        Page<Auction> findAllByOwner_UserNumberEqualsIgnoreCase(String ownerUserNumber, Pageable pageable);
        Page<Auction> findAllByOwner_UsernameEqualsIgnoreCase(String ownerUsername, Pageable pageable);
    List<Auction> findAllByAuctionStatusAndEndTimeLessThanEqual(AuctionStatus auctionStatus, LocalDateTime date);
}
