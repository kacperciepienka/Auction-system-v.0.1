package pl.auction_system.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.auction_system.dto.CreateAuctionRequest;
import pl.auction_system.exception.AuctionNotFoundByReferenceNumber;
import pl.auction_system.exception.CantChangeStartingPriceException;
import pl.auction_system.exception.InvalidNewTitleException;
import pl.auction_system.exception.UserNotFoundByUsernameException;
import pl.auction_system.mapper.CreateAuctionRequestMapper;
import pl.auction_system.model.Auction;
import pl.auction_system.model.AuctionCategory;
import pl.auction_system.model.AuctionStatus;
import pl.auction_system.model.User;
import pl.auction_system.repository.AuctionRepository;
import pl.auction_system.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final CreateAuctionRequestMapper createAuctionRequestMapper;

    // POST
    public Auction addAuction(@NotNull CreateAuctionRequest auctionToAdd, String userUsername) {
        User owner = userRepository.findUserByUsernameEqualsIgnoreCase(userUsername)
                .orElseThrow(() -> new UserNotFoundByUsernameException(userUsername));

        Auction auction = createAuctionRequestMapper.toEntity(auctionToAdd);
        auction.setOwner(owner);
        auction.setCurrentPrice(auction.getStartingPrice());
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(auction.getStartTime().plusDays(14));
        auction.setAuctionStatus(AuctionStatus.ACTIVE);

        Random random = new Random();
        int randomNumber = random.nextInt(1000000, 99999999);
        String categoryPrefix = auction.getAuctionCategory().name().substring(0, 2).toUpperCase();
        int year = auction.getStartTime().getYear();
        int day = auction.getStartTime().getDayOfMonth();
        auction.setReferenceNumber(categoryPrefix + "-" + year + day + randomNumber);

        return auctionRepository.save(auction);
    }

    // DELETE
    public void deleteAuction(String referenceNumber) {
        Auction auction = auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber)
                .orElseThrow(() -> new AuctionNotFoundByReferenceNumber(referenceNumber));

        auction.setEndTime(LocalDateTime.now());
        auctionRepository.delete(auction);
    }

    // PUT
    public Auction changeTitle(String referenceNumber, String newTitle) {
        Auction auction = auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber)
                .orElseThrow(() -> new AuctionNotFoundByReferenceNumber(referenceNumber));

        // dodatkowe zabezpieczenie
        if (newTitle.isBlank() || newTitle.length() > 100) {
            throw new InvalidNewTitleException(newTitle);
        }

        auction.setTitle(newTitle);
        return auctionRepository.save(auction);
    }

    public Auction changeDescription(String referenceNumber, String newDescription) {
        Auction auction = auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber)
                .orElseThrow(() -> new AuctionNotFoundByReferenceNumber(referenceNumber));

        auction.setDescription(newDescription);
        return auctionRepository.save(auction);
    }

    public Auction changeCategory(String referenceNumber, AuctionCategory newAuctionCategory) {
        Auction auction = auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber)
                .orElseThrow(() -> new AuctionNotFoundByReferenceNumber(referenceNumber));

        auction.setAuctionCategory(newAuctionCategory);
        return auctionRepository.save(auction);
    }

    public Auction changeStartingPrice(String referenceNumber, BigDecimal newStartingPrice) {
        Auction auction = auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber)
                .orElseThrow(() -> new AuctionNotFoundByReferenceNumber(referenceNumber));

        if (auction.getCurrentPrice().compareTo(auction.getStartingPrice()) != 0) {
            throw new CantChangeStartingPriceException(referenceNumber);
        }

        auction.setStartingPrice(newStartingPrice);
        auction.setCurrentPrice(newStartingPrice);
        return auctionRepository.save(auction);
    }

    public Auction finishAuctionEarlier(String referenceNumber) {
        Auction auction = auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber)
                .orElseThrow(() -> new AuctionNotFoundByReferenceNumber(referenceNumber));

        auction.setEndTime(LocalDateTime.now());
        auction.setAuctionStatus(AuctionStatus.FINISHED);
        return auctionRepository.save(auction);
    }

    // GET
    public Auction findAuctionByReferenceNumberEqualsIgnoreCase(String referenceNumber) {
        return auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber)
                .orElseThrow(() -> new AuctionNotFoundByReferenceNumber(referenceNumber));
    }

    public Page<Auction> findAllAuctions(Pageable pageable) {
        return auctionRepository.findAll(pageable);
    }

    public Page<Auction> findAllByTitleContainingIgnoreCase(String title, Pageable pageable) {
        return auctionRepository.findAllByTitleContainingIgnoreCase(title, pageable);
    }

    public Page<Auction> findAllByAuctionCategory(AuctionCategory auctionCategory, Pageable pageable) {
        return auctionRepository.findAllByAuctionCategory(auctionCategory, pageable);
    }

    // by current price
    public Page<Auction> findAllByCurrentPriceIsLessThanEqual(BigDecimal currentPriceIsLessThan, Pageable pageable) {
        return auctionRepository.findAllByCurrentPriceIsLessThanEqual(currentPriceIsLessThan, pageable);
    }

    public Page<Auction> findAllByCurrentPriceIsGreaterThanEqual(BigDecimal currentPriceIsGreaterThan, Pageable pageable) {
        return auctionRepository.findAllByCurrentPriceIsGreaterThanEqual(currentPriceIsGreaterThan, pageable);
    }

    public Page<Auction> findAllByCurrentPriceIsBetween(BigDecimal currentPriceAfter, BigDecimal currentPriceBefore, Pageable pageable) {
        return auctionRepository.findAllByCurrentPriceBetween(currentPriceAfter, currentPriceBefore, pageable);
    }

    // by start date
    public Page<Auction> findAllByStartTime(LocalDateTime startTime, Pageable pageable) {
        return auctionRepository.findAllByStartTime(startTime, pageable);
    }

    public Page<Auction> findAllByStartTimeIsAfter(LocalDateTime startTimeAfter, Pageable pageable) {
        return auctionRepository.findAllByStartTimeIsAfter(startTimeAfter, pageable);
    }

    public Page<Auction> findAllByStartTimeIsBefore(LocalDateTime startTimeBefore, Pageable pageable) {
        return auctionRepository.findAllByStartTimeIsBefore(startTimeBefore, pageable);
    }

    public Page<Auction> findAllByStartTimeIsBetween(LocalDateTime startTimeAfter, LocalDateTime startTimeBefore, Pageable pageable) {
        return auctionRepository.findAllByStartTimeIsBetween(startTimeAfter, startTimeBefore, pageable);
    }

    // by end date
    public Page<Auction> findAllByEndTime(LocalDateTime endTime, Pageable pageable) {
        return auctionRepository.findAllByEndTime(endTime, pageable);
    }

    public Page<Auction> findAllByEndTimeIsAfter(LocalDateTime endTimeAfter, Pageable pageable) {
        return auctionRepository.findAllByEndTimeIsAfter(endTimeAfter, pageable);
    }

    public Page<Auction> findAllByEndTimeIsBefore(LocalDateTime endTimeBefore, Pageable pageable) {
        return auctionRepository.findAllByEndTimeIsBefore(endTimeBefore, pageable);
    }

    public Page<Auction> findAllByEndTimeIsBetween(LocalDateTime endTimeAfter, LocalDateTime endTimeBefore, Pageable pageable) {
        return auctionRepository.findAllByEndTimeIsBetween(endTimeAfter, endTimeBefore, pageable);
    }

    public Page<Auction> findAllByAuctionStatus(AuctionStatus auctionStatus, Pageable pageable) {
        return auctionRepository.findAllByAuctionStatus(auctionStatus, pageable);
    }

    // by Owner
    public Page<Auction> findAllByOwner_UserNumberEqualsIgnoreCase(String ownerUserNumber, Pageable pageable){
        return auctionRepository.findAllByOwner_UserNumberEqualsIgnoreCase(ownerUserNumber, pageable);
    }

    public Page<Auction> findAllByOwnerUsernameEqualsIgnoreCase(String ownerUsername, Pageable pageable) {
        return auctionRepository.findAllByOwner_UsernameEqualsIgnoreCase(ownerUsername, pageable);
    }
}
