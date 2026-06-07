package pl.auction_system.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import pl.auction_system.dto.CreateBidRequest;
import pl.auction_system.exception.*;
import pl.auction_system.mapper.CreateBidRequestMapper;
import pl.auction_system.model.*;
import pl.auction_system.repository.AuctionRepository;
import pl.auction_system.repository.BidRepository;
import pl.auction_system.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @Mock
    private BidRepository bidRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreateBidRequestMapper createBidRequestMapper;

    @InjectMocks
    private BidService bidService;

    @Test
    @DisplayName("Test: add bid happy path")
    void shouldAddBid() {
        String bidderUsername = "Kinga77";
        String ownerUsername = "Mateo21";
        String referenceNumber = "EL-TEST-123";

        BigDecimal currentPrice = BigDecimal.valueOf(100);
        BigDecimal bidAmount = BigDecimal.valueOf(150);

        CreateBidRequest request = new CreateBidRequest();
        request.setAmount(bidAmount);

        User bidder = new User();
        bidder.setUsername(bidderUsername);

        User owner = new User();
        owner.setUsername(ownerUsername);

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setTitle("PlayStation 5");
        auction.setCurrentPrice(currentPrice);
        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        auction.setOwner(owner);

        Bid bid = new Bid();
        bid.setAmount(bidAmount);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(bidderUsername))
                .thenReturn(Optional.of(bidder));

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(createBidRequestMapper.toEntity(request))
                .thenReturn(bid);

        when(bidRepository.save(any(Bid.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Bid result = bidService.addBid(request, bidderUsername, referenceNumber);

        assertAll(
                () -> assertThat(result.getAmount()).isEqualByComparingTo(bidAmount),
                () -> assertThat(result.getBidder().getUsername()).isEqualTo(bidderUsername),
                () -> assertThat(result.getAuction().getReferenceNumber()).isEqualTo(referenceNumber),
                () -> assertThat(result.getBidTime()).isNotNull(),
                () -> assertThat(result.getBidIdNumber()).isNotNull(),
                () -> assertThat(result.getBidIdNumber()).startsWith("KI-"),
                () -> assertThat(auction.getCurrentPrice()).isEqualByComparingTo(bidAmount)
        );

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(bidderUsername);
        verify(auctionRepository, times(1)).findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        verify(createBidRequestMapper, times(1)).toEntity(request);
        verify(bidRepository, times(1)).save(any(Bid.class));
    }

    @Test
    @DisplayName("Test: add bid should throw when bidder does not exist")
    void shouldThrowWhenBidderDoesNotExist() {
        String bidderUsername = "NoUser123";
        String referenceNumber = "EL-TEST-123";

        CreateBidRequest request = new CreateBidRequest();
        request.setAmount(BigDecimal.valueOf(150));

        when(userRepository.findUserByUsernameEqualsIgnoreCase(bidderUsername))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bidService.addBid(request, bidderUsername, referenceNumber))
                .isInstanceOf(UserNotFoundByUsernameException.class);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(bidderUsername);
        verify(auctionRepository, never()).findAuctionByReferenceNumberEqualsIgnoreCase(anyString());
        verify(bidRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: add bid should throw when auction does not exist")
    void shouldThrowWhenAuctionDoesNotExist() {
        String bidderUsername = "Kinga77";
        String referenceNumber = "NO-AUCTION-123";

        CreateBidRequest request = new CreateBidRequest();
        request.setAmount(BigDecimal.valueOf(150));

        User bidder = new User();
        bidder.setUsername(bidderUsername);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(bidderUsername))
                .thenReturn(Optional.of(bidder));

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bidService.addBid(request, bidderUsername, referenceNumber))
                .isInstanceOf(AuctionNotFoundByReferenceNumber.class);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(bidderUsername);
        verify(auctionRepository, times(1)).findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        verify(createBidRequestMapper, never()).toEntity(any());
        verify(bidRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: add bid should throw when owner tries to bid own auction")
    void shouldThrowWhenOwnerTriesToBidOwnAuction() {
        String username = "Mateo21";
        String referenceNumber = "EL-TEST-123";

        CreateBidRequest request = new CreateBidRequest();
        request.setAmount(BigDecimal.valueOf(150));

        User bidder = new User();
        bidder.setUsername(username);

        User owner = new User();
        owner.setUsername(username);

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setOwner(owner);
        auction.setCurrentPrice(BigDecimal.valueOf(100));
        auction.setAuctionStatus(AuctionStatus.ACTIVE);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(bidder));

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        assertThatThrownBy(() -> bidService.addBid(request, username, referenceNumber))
                .isInstanceOf(OwnerCantBidException.class);

        verify(createBidRequestMapper, never()).toEntity(any());
        verify(bidRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: add bid should throw when bid is lower than current price")
    void shouldThrowWhenBidIsLowerThanCurrentPrice() {
        String bidderUsername = "Kinga77";
        String ownerUsername = "Mateo21";
        String referenceNumber = "EL-TEST-123";

        CreateBidRequest request = new CreateBidRequest();
        request.setAmount(BigDecimal.valueOf(50));

        User bidder = new User();
        bidder.setUsername(bidderUsername);

        User owner = new User();
        owner.setUsername(ownerUsername);

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setOwner(owner);
        auction.setCurrentPrice(BigDecimal.valueOf(100));
        auction.setAuctionStatus(AuctionStatus.ACTIVE);

        Bid bid = new Bid();
        bid.setAmount(BigDecimal.valueOf(50));

        when(userRepository.findUserByUsernameEqualsIgnoreCase(bidderUsername))
                .thenReturn(Optional.of(bidder));

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(createBidRequestMapper.toEntity(request))
                .thenReturn(bid);

        assertThatThrownBy(() -> bidService.addBid(request, bidderUsername, referenceNumber))
                .isInstanceOf(BadBidException.class);

        verify(bidRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: add bid should throw when bid is equal to current price")
    void shouldThrowWhenBidIsEqualToCurrentPrice() {
        String bidderUsername = "Kinga77";
        String ownerUsername = "Mateo21";
        String referenceNumber = "EL-TEST-123";

        CreateBidRequest request = new CreateBidRequest();
        request.setAmount(BigDecimal.valueOf(100));

        User bidder = new User();
        bidder.setUsername(bidderUsername);

        User owner = new User();
        owner.setUsername(ownerUsername);

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setOwner(owner);
        auction.setCurrentPrice(BigDecimal.valueOf(100));
        auction.setAuctionStatus(AuctionStatus.ACTIVE);

        Bid bid = new Bid();
        bid.setAmount(BigDecimal.valueOf(100));

        when(userRepository.findUserByUsernameEqualsIgnoreCase(bidderUsername))
                .thenReturn(Optional.of(bidder));

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(createBidRequestMapper.toEntity(request))
                .thenReturn(bid);

        assertThatThrownBy(() -> bidService.addBid(request, bidderUsername, referenceNumber))
                .isInstanceOf(BadBidException.class);

        verify(bidRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: add bid should throw when bid is invalid")
    void shouldThrowWhenBidIsInvalid() {
        String bidderUsername = "Kinga77";
        String ownerUsername = "Mateo21";
        String referenceNumber = "EL-TEST-123";

        CreateBidRequest request = new CreateBidRequest();
        request.setAmount(BigDecimal.valueOf(-10));

        User bidder = new User();
        bidder.setUsername(bidderUsername);

        User owner = new User();
        owner.setUsername(ownerUsername);

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setOwner(owner);
        auction.setCurrentPrice(BigDecimal.valueOf(-20));
        auction.setAuctionStatus(AuctionStatus.ACTIVE);

        Bid bid = new Bid();
        bid.setAmount(BigDecimal.valueOf(-10));

        when(userRepository.findUserByUsernameEqualsIgnoreCase(bidderUsername))
                .thenReturn(Optional.of(bidder));

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(createBidRequestMapper.toEntity(request))
                .thenReturn(bid);

        assertThatThrownBy(() -> bidService.addBid(request, bidderUsername, referenceNumber))
                .isInstanceOf(InvalidBidException.class);

        verify(bidRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: add bid should throw when auction is finished")
    void shouldThrowWhenAuctionIsFinished() {
        String bidderUsername = "Kinga77";
        String ownerUsername = "Mateo21";
        String referenceNumber = "EL-TEST-123";

        CreateBidRequest request = new CreateBidRequest();
        request.setAmount(BigDecimal.valueOf(150));

        User bidder = new User();
        bidder.setUsername(bidderUsername);

        User owner = new User();
        owner.setUsername(ownerUsername);

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setTitle("PlayStation 5");
        auction.setOwner(owner);
        auction.setCurrentPrice(BigDecimal.valueOf(100));
        auction.setAuctionStatus(AuctionStatus.FINISHED);

        Bid bid = new Bid();
        bid.setAmount(BigDecimal.valueOf(150));

        when(userRepository.findUserByUsernameEqualsIgnoreCase(bidderUsername))
                .thenReturn(Optional.of(bidder));

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(createBidRequestMapper.toEntity(request))
                .thenReturn(bid);

        assertThatThrownBy(() -> bidService.addBid(request, bidderUsername, referenceNumber))
                .isInstanceOf(AuctionIsAlreadyFinishedException.class);

        verify(bidRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: find bid by id number happy path")
    void shouldFindBidByIdNumber() {
        String bidIdNumber = "KI-TEST-123";

        Bid bid = new Bid();
        bid.setBidIdNumber(bidIdNumber);

        when(bidRepository.findByBidIdNumberEqualsIgnoreCase(bidIdNumber))
                .thenReturn(Optional.of(bid));

        Bid result = bidService.findByBidIdNumberEqualsIgnoreCase(bidIdNumber);

        assertThat(result.getBidIdNumber()).isEqualTo(bidIdNumber);

        verify(bidRepository, times(1)).findByBidIdNumberEqualsIgnoreCase(bidIdNumber);
    }

    @Test
    @DisplayName("Test: find bid by id number should throw when bid does not exist")
    void shouldThrowWhenBidByIdNumberDoesNotExist() {
        String bidIdNumber = "NO-BID-123";

        when(bidRepository.findByBidIdNumberEqualsIgnoreCase(bidIdNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bidService.findByBidIdNumberEqualsIgnoreCase(bidIdNumber))
                .isInstanceOf(BidNotFoundByIdNumberException.class);

        verify(bidRepository, times(1)).findByBidIdNumberEqualsIgnoreCase(bidIdNumber);
    }

    @Test
    @DisplayName("Test: find all bids")
    void shouldFindAllBids() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("amount").ascending());

        Bid bid1 = new Bid();
        bid1.setAmount(BigDecimal.valueOf(100));

        Bid bid2 = new Bid();
        bid2.setAmount(BigDecimal.valueOf(200));

        Page<Bid> page = new PageImpl<>(List.of(bid1, bid2), pageable, 2);

        when(bidRepository.findAll(pageable)).thenReturn(page);

        Page<Bid> result = bidService.findAllBids(pageable);

        assertAll(
                () -> assertThat(result.getContent()).hasSize(2),
                () -> assertThat(result.getTotalElements()).isEqualTo(2),
                () -> assertThat(result.getContent().getFirst().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100))
        );

        verify(bidRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Test: find bids by bidder username")
    void shouldFindBidsByBidderUsername() {
        Pageable pageable = PageRequest.of(0, 10);
        String bidderUsername = "Kinga77";

        User bidder = new User();
        bidder.setUsername(bidderUsername);

        Bid bid = new Bid();
        bid.setBidder(bidder);

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByBidder_UsernameEqualsIgnoreCase(bidderUsername, pageable))
                .thenReturn(page);

        Page<Bid> result = bidService.findAllByBidder_UsernameEqualsIgnoreCase(bidderUsername, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getBidder().getUsername()).isEqualTo(bidderUsername);

        verify(bidRepository, times(1))
                .findAllByBidder_UsernameEqualsIgnoreCase(bidderUsername, pageable);
    }

    @Test
    @DisplayName("Test: find bids by bidder user number")
    void shouldFindBidsByBidderUserNumber() {
        Pageable pageable = PageRequest.of(0, 10);
        String userNumber = "USR-TEST-123";

        User bidder = new User();
        bidder.setUserNumber(userNumber);

        Bid bid = new Bid();
        bid.setBidder(bidder);

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByBidder_UserNumberEqualsIgnoreCase(userNumber, pageable))
                .thenReturn(page);

        Page<Bid> result = bidService.findAllByBidder_UserNumberEqualsIgnoreCase(userNumber, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getBidder().getUserNumber()).isEqualTo(userNumber);

        verify(bidRepository, times(1))
                .findAllByBidder_UserNumberEqualsIgnoreCase(userNumber, pageable);
    }

    @Test
    @DisplayName("Test: find bids by amount less than equal")
    void shouldFindBidsByAmountLessThanEqual() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal amountMax = BigDecimal.valueOf(500);

        Bid bid = new Bid();
        bid.setAmount(BigDecimal.valueOf(300));

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByAmountIsLessThanEqual(amountMax, pageable))
                .thenReturn(page);

        Page<Bid> result = bidService.findAllByAmountIsLessThanEqual(amountMax, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAmount()).isLessThanOrEqualTo(amountMax);

        verify(bidRepository, times(1))
                .findAllByAmountIsLessThanEqual(amountMax, pageable);
    }

    @Test
    @DisplayName("Test: find bids by amount greater than equal")
    void shouldFindBidsByAmountGreaterThanEqual() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal amountMin = BigDecimal.valueOf(100);

        Bid bid = new Bid();
        bid.setAmount(BigDecimal.valueOf(300));

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByAmountIsGreaterThanEqual(amountMin, pageable))
                .thenReturn(page);

        Page<Bid> result = bidService.findAllByAmountIsGreaterThanEqual(amountMin, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAmount()).isGreaterThanOrEqualTo(amountMin);

        verify(bidRepository, times(1))
                .findAllByAmountIsGreaterThanEqual(amountMin, pageable);
    }

    @Test
    @DisplayName("Test: find bids by amount between")
    void shouldFindBidsByAmountBetween() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal amountMin = BigDecimal.valueOf(100);
        BigDecimal amountMax = BigDecimal.valueOf(500);

        Bid bid = new Bid();
        bid.setAmount(BigDecimal.valueOf(300));

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByAmountBetween(amountMin, amountMax, pageable))
                .thenReturn(page);

        Page<Bid> result = bidService.findAllByAmountIsBetween(amountMin, amountMax, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(300));

        verify(bidRepository, times(1))
                .findAllByAmountBetween(amountMin, amountMax, pageable);
    }

    @Test
    @DisplayName("Test: find bids by bidder username and amount between")
    void shouldFindBidsByBidderUsernameAndAmountBetween() {
        Pageable pageable = PageRequest.of(0, 10);
        String bidderUsername = "Kinga77";
        BigDecimal amountMin = BigDecimal.valueOf(100);
        BigDecimal amountMax = BigDecimal.valueOf(500);

        User bidder = new User();
        bidder.setUsername(bidderUsername);

        Bid bid = new Bid();
        bid.setBidder(bidder);
        bid.setAmount(BigDecimal.valueOf(300));

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByBidder_UsernameAndAmountBetween(
                bidderUsername,
                amountMin,
                amountMax,
                pageable
        )).thenReturn(page);

        Page<Bid> result = bidService.findAllByBidder_UsernameAndAmountIsBetween(
                bidderUsername,
                amountMin,
                amountMax,
                pageable
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getBidder().getUsername()).isEqualTo(bidderUsername);
        assertThat(result.getContent().getFirst().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(300));

        verify(bidRepository, times(1))
                .findAllByBidder_UsernameAndAmountBetween(bidderUsername, amountMin, amountMax, pageable);
    }

    @Test
    @DisplayName("Test: find bids by bid time after")
    void shouldFindBidsByBidTimeAfter() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime date = LocalDateTime.of(2026, 1, 1, 0, 0);

        Bid bid = new Bid();
        bid.setBidTime(LocalDateTime.of(2026, 6, 1, 12, 0));

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByBidTimeIsAfter(date, pageable))
                .thenReturn(page);

        Page<Bid> result = bidService.findAllByBidTimeIsAfter(date, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getBidTime()).isAfter(date);

        verify(bidRepository, times(1))
                .findAllByBidTimeIsAfter(date, pageable);
    }

    @Test
    @DisplayName("Test: find bids by bid time between")
    void shouldFindBidsByBidTimeBetween() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime after = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime before = LocalDateTime.of(2030, 1, 1, 0, 0);

        Bid bid = new Bid();
        bid.setBidTime(LocalDateTime.of(2026, 6, 1, 12, 0));

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByBidTimeIsBetween(after, before, pageable))
                .thenReturn(page);

        Page<Bid> result = bidService.findAllByBidTimeIsBetween(after, before, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getBidTime()).isAfter(after);
        assertThat(result.getContent().getFirst().getBidTime()).isBefore(before);

        verify(bidRepository, times(1))
                .findAllByBidTimeIsBetween(after, before, pageable);
    }

    @Test
    @DisplayName("Test: find bids by bidder username and bid time between")
    void shouldFindBidsByBidderUsernameAndBidTimeBetween() {
        Pageable pageable = PageRequest.of(0, 10);
        String bidderUsername = "Kinga77";
        LocalDateTime after = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime before = LocalDateTime.of(2030, 1, 1, 0, 0);

        User bidder = new User();
        bidder.setUsername(bidderUsername);

        Bid bid = new Bid();
        bid.setBidder(bidder);
        bid.setBidTime(LocalDateTime.of(2026, 6, 1, 12, 0));

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByBidder_UsernameAndBidTimeIsBetween(
                bidderUsername,
                after,
                before,
                pageable
        )).thenReturn(page);

        Page<Bid> result = bidService.findAllByBidder_UsernameAndBidTimeIsBetween(
                bidderUsername,
                after,
                before,
                pageable
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getBidder().getUsername()).isEqualTo(bidderUsername);
        assertThat(result.getContent().getFirst().getBidTime()).isAfter(after);
        assertThat(result.getContent().getFirst().getBidTime()).isBefore(before);

        verify(bidRepository, times(1))
                .findAllByBidder_UsernameAndBidTimeIsBetween(bidderUsername, after, before, pageable);
    }

    @Test
    @DisplayName("Test: find bids by auction reference number")
    void shouldFindBidsByAuctionReferenceNumber() {
        Pageable pageable = PageRequest.of(0, 10);
        String referenceNumber = "EL-TEST-123";

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);

        Bid bid = new Bid();
        bid.setAuction(auction);

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByAuction_ReferenceNumberEqualsIgnoreCase(referenceNumber, pageable))
                .thenReturn(page);

        Page<Bid> result = bidService.findAllByAuction_ReferenceNumberEqualsIgnoreCase(referenceNumber, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAuction().getReferenceNumber()).isEqualTo(referenceNumber);

        verify(bidRepository, times(1))
                .findAllByAuction_ReferenceNumberEqualsIgnoreCase(referenceNumber, pageable);
    }

    @Test
    @DisplayName("Test: find bids by auction title")
    void shouldFindBidsByAuctionTitle() {
        Pageable pageable = PageRequest.of(0, 10);
        String title = "PlayStation";

        Auction auction = new Auction();
        auction.setTitle("PlayStation 5");

        Bid bid = new Bid();
        bid.setAuction(auction);

        Page<Bid> page = new PageImpl<>(List.of(bid), pageable, 1);

        when(bidRepository.findAllByAuction_TitleContainingIgnoreCase(title, pageable))
                .thenReturn(page);

        Page<Bid> result = bidService.findAllByAuction_TitleContainingIgnoreCase(title, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAuction().getTitle()).contains(title);

        verify(bidRepository, times(1))
                .findAllByAuction_TitleContainingIgnoreCase(title, pageable);
    }
}