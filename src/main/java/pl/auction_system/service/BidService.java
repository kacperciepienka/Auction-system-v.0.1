package pl.auction_system.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.auction_system.dto.CreateBidRequest;
import pl.auction_system.exception.*;
import pl.auction_system.mapper.CreateBidRequestMapper;
import pl.auction_system.model.Auction;
import pl.auction_system.model.AuctionStatus;
import pl.auction_system.model.Bid;
import pl.auction_system.model.User;
import pl.auction_system.repository.AuctionRepository;
import pl.auction_system.repository.BidRepository;
import pl.auction_system.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class BidService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final CreateBidRequestMapper createBidRequestMapper;
    //DELETE ->bid nie da się usunąć

    //POST
    public Bid addBid(@NotNull CreateBidRequest bidToAdd, String bidderUsername, String referenceNumber) {
        // find bidder
        User bidder = userRepository.findUserByUsernameEqualsIgnoreCase(bidderUsername)
                .orElseThrow(() -> new UserNotFoundByUsernameException(bidderUsername));

        Auction auction = auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber)
                .orElseThrow(() -> new AuctionNotFoundByReferenceNumber(referenceNumber));

        // właściciel nie może licytować
        if (auction.getOwner().getUsername().equals(bidder.getUsername())) {
            throw new OwnerCantBidException(bidderUsername);
        }

        // object
        Bid bid = createBidRequestMapper.toEntity(bidToAdd);

        // if block
        if (auction.getCurrentPrice().compareTo(bid.getAmount()) >= 0) {
            // cena bid nie może być niższa lub równa obecnemu bid
            throw new BadBidException(bid.getAmount());
        }
        // podwójne zabezpieczenie
        if (bid.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBidException(bid.getAmount());
        }
        if (auction.getAuctionStatus().equals(AuctionStatus.FINISHED)) {
            throw new AuctionIsAlreadyFinishedException(auction.getTitle(), auction.getReferenceNumber());
        }

        bid.setBidder(bidder);
        bid.setAuction(auction);
        bid.setBidTime(LocalDateTime.now());

        Random random = new Random();
        int randomNumber = random.nextInt(1000000, 99999999);
        String usernamePrefix = bidder.getUsername() != null && bidder.getUsername().length() >= 2
                ? bidder.getUsername().substring(0,2).toUpperCase()
                : "QZ";
        int year = LocalDateTime.now().getYear();
        int day = LocalDateTime.now().getDayOfMonth();
        bid.setBidIdNumber(usernamePrefix + "-" + year + day + randomNumber);

        // zmiana ceny aukcji
        auction.setCurrentPrice(bid.getAmount());

        return bidRepository.save(bid);
    }


    //PUT
    // zaakceptowanego bid nie można edytować
    //GET
    public Bid findByBidIdNumberEqualsIgnoreCase(String bidIdNumber) {
        return bidRepository.findByBidIdNumberEqualsIgnoreCase(bidIdNumber)
                .orElseThrow(() -> new BidNotFoundByIdNumberException(bidIdNumber));
    }

    public Page<Bid> findAllBids(Pageable pageable) {
        return bidRepository.findAll(pageable);
    }


    public Page<Bid> findAllByBidder_UsernameEqualsIgnoreCase(String bidderUsername, Pageable pageable) {
        return bidRepository.findAllByBidder_UsernameEqualsIgnoreCase(bidderUsername, pageable);
    }

    public Page<Bid> findAllByBidder_UserNumberEqualsIgnoreCase(String userNumber, Pageable pageable) {
        return bidRepository.findAllByBidder_UserNumberEqualsIgnoreCase(userNumber, pageable);
    }

    // by amount of bid
    public Page<Bid> findAllByAmountIsLessThanEqual(BigDecimal amountIsLessThan, Pageable pageable) {
        return bidRepository.findAllByAmountIsLessThanEqual(amountIsLessThan, pageable);
    }

    public Page<Bid> findAllByAmountIsGreaterThanEqual(BigDecimal amountIsGreaterThan, Pageable pageable) {
        return bidRepository.findAllByAmountIsGreaterThanEqual(amountIsGreaterThan, pageable);
    }

    public Page<Bid> findAllByAmountIsBetween(BigDecimal amountAfter, BigDecimal amountBefore, Pageable pageable) {
        return bidRepository.findAllByAmountBetween(amountAfter, amountBefore, pageable);
    }

    public Page<Bid> findAllByBidder_UsernameAndAmountIsLessThanEqual(String bidderUsername, BigDecimal amountIsLessThan, Pageable pageable) {
        return bidRepository.findAllByBidder_UsernameAndAmountIsLessThanEqual(bidderUsername, amountIsLessThan, pageable);
    }

    public Page<Bid> findAllByBidder_UsernameAndAmountIsGreaterThanEqual(String bidderUsername, BigDecimal amountIsGreaterThan, Pageable pageable) {
        return bidRepository.findAllByBidder_UsernameAndAmountIsGreaterThanEqual(bidderUsername, amountIsGreaterThan, pageable);
    }

    public Page<Bid> findAllByBidder_UsernameAndAmountIsBetween(String bidderUsername, BigDecimal amountAfter, BigDecimal amountBefore, Pageable pageable) {
        return bidRepository.findAllByBidder_UsernameAndAmountBetween(bidderUsername, amountAfter, amountBefore, pageable);
    }


    // by date of bid
    public Page<Bid> findAllByBidTime(LocalDateTime bidTime, Pageable pageable) {
        return bidRepository.findAllByBidTime(bidTime, pageable);
    }

    public Page<Bid> findAllByBidTimeIsAfter(LocalDateTime bidTimeAfter, Pageable pageable) {
        return bidRepository.findAllByBidTimeIsAfter(bidTimeAfter, pageable);
    }

    public Page<Bid> findAllByBidTimeIsBefore(LocalDateTime bidTimeBefore, Pageable pageable) {
        return bidRepository.findAllByBidTimeIsBefore(bidTimeBefore, pageable);
    }

    public Page<Bid> findAllByBidTimeIsBetween(LocalDateTime bidTimeAfter, LocalDateTime bidTimeBefore, Pageable pageable) {
        return bidRepository.findAllByBidTimeIsBetween(bidTimeAfter, bidTimeBefore, pageable);
    }

    // by date of bid
    public Page<Bid> findAllByBidder_UsernameAndBidTime(String bidderUsername, LocalDateTime bidTime, Pageable pageable) {
        return bidRepository.findAllByBidder_UsernameAndBidTime(bidderUsername, bidTime, pageable);
    }

    public Page<Bid> findAllByBidder_UsernameAndBidTimeIsAfter(String bidderUsername, LocalDateTime bidTimeAfter, Pageable pageable) {
        return bidRepository.findAllByBidder_UsernameAndBidTimeIsAfter(bidderUsername, bidTimeAfter, pageable);
    }

    public Page<Bid> findAllByBidder_UsernameAndBidTimeIsBefore(String bidderUsername, LocalDateTime bidTimeBefore, Pageable pageable) {
        return bidRepository.findAllByBidder_UsernameAndBidTimeIsBefore(bidderUsername, bidTimeBefore, pageable);
    }

    public Page<Bid> findAllByBidder_UsernameAndBidTimeIsBetween(String bidderUsername, LocalDateTime bidTimeAfter, LocalDateTime bidTimeBefore, Pageable pageable) {
        return bidRepository.findAllByBidder_UsernameAndBidTimeIsBetween(bidderUsername, bidTimeAfter, bidTimeBefore, pageable);
    }

    // by auction
    public Page<Bid> findAllByAuction_ReferenceNumberEqualsIgnoreCase(String referenceNumber, Pageable pageable) {
        return bidRepository.findAllByAuction_ReferenceNumberEqualsIgnoreCase(referenceNumber, pageable);
    }

    public Page<Bid> findAllByAuction_TitleContainingIgnoreCase(String title, Pageable pageable) {
        return bidRepository.findAllByAuction_TitleContainingIgnoreCase(title, pageable);
    }
}
