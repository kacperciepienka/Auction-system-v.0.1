package pl.auction_system.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import pl.auction_system.dto.CreateAuctionRequest;
import pl.auction_system.exception.AuctionNotFoundByReferenceNumber;
import pl.auction_system.exception.CantChangeStartingPriceException;
import pl.auction_system.exception.InvalidNewTitleException;
import pl.auction_system.exception.UserNotFoundByUsernameException;
import pl.auction_system.mapper.CreateAuctionRequestMapper;
import pl.auction_system.model.*;
import pl.auction_system.repository.AuctionRepository;
import pl.auction_system.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreateAuctionRequestMapper createAuctionRequestMapper;

    @InjectMocks
    private AuctionService auctionService;

    @Test
    @DisplayName("Test: add auction happy path")
    void shouldAddAuction() {
        String username = "Mateo21";

        CreateAuctionRequest request = new CreateAuctionRequest();
        request.setTitle("PlayStation 5");
        request.setDescription("Konsola w bardzo dobrym stanie");
        request.setAuctionCategory(AuctionCategory.ELECTRONICS);
        request.setStartingPrice(BigDecimal.valueOf(850));

        User owner = new User();
        owner.setUsername(username);

        Auction auction = new Auction();
        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setAuctionCategory(request.getAuctionCategory());
        auction.setStartingPrice(request.getStartingPrice());

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(owner));

        when(createAuctionRequestMapper.toEntity(request))
                .thenReturn(auction);

        when(auctionRepository.save(any(Auction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Auction result = auctionService.addAuction(request, username);

        assertAll(
                () -> assertThat(result.getTitle()).isEqualTo("PlayStation 5"),
                () -> assertThat(result.getDescription()).isEqualTo("Konsola w bardzo dobrym stanie"),
                () -> assertThat(result.getAuctionCategory()).isEqualTo(AuctionCategory.ELECTRONICS),
                () -> assertThat(result.getStartingPrice()).isEqualByComparingTo(BigDecimal.valueOf(850)),
                () -> assertThat(result.getCurrentPrice()).isEqualByComparingTo(BigDecimal.valueOf(850)),
                () -> assertThat(result.getOwner().getUsername()).isEqualTo(username),
                () -> assertThat(result.getAuctionStatus()).isEqualTo(AuctionStatus.ACTIVE),
                () -> assertThat(result.getStartTime()).isNotNull(),
                () -> assertThat(result.getEndTime()).isNotNull(),
                () -> assertThat(result.getEndTime()).isAfter(result.getStartTime()),
                () -> assertThat(result.getReferenceNumber()).startsWith("EL-")
        );

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(username);
        verify(createAuctionRequestMapper, times(1)).toEntity(request);
        verify(auctionRepository, times(1)).save(any(Auction.class));
    }

    @Test
    @DisplayName("Test: add auction should throw when user does not exist")
    void shouldThrowWhenUserDoesNotExistWhileAddingAuction() {
        String username = "NoUser123";

        CreateAuctionRequest request = new CreateAuctionRequest();
        request.setTitle("PlayStation 5");
        request.setDescription("Konsola");
        request.setAuctionCategory(AuctionCategory.ELECTRONICS);
        request.setStartingPrice(BigDecimal.valueOf(850));

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> auctionService.addAuction(request, username))
                .isInstanceOf(UserNotFoundByUsernameException.class);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(username);
        verify(createAuctionRequestMapper, never()).toEntity(any());
        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: delete auction happy path")
    void shouldDeleteAuction() {
        String referenceNumber = "EL-TEST-123";

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        auctionService.deleteAuction(referenceNumber);

        assertThat(auction.getEndTime()).isNotNull();

        verify(auctionRepository, times(1)).findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        verify(auctionRepository, times(1)).delete(auction);
    }

    @Test
    @DisplayName("Test: delete auction should throw when auction does not exist")
    void shouldThrowWhenDeletingAuctionThatDoesNotExist() {
        String referenceNumber = "NO-AUCTION-123";

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> auctionService.deleteAuction(referenceNumber))
                .isInstanceOf(AuctionNotFoundByReferenceNumber.class);

        verify(auctionRepository, times(1)).findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        verify(auctionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Test: change title happy path")
    void shouldChangeTitle() {
        String referenceNumber = "EL-TEST-123";
        String newTitle = "Nowy tytuł aukcji";

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setTitle("Stary tytuł");

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(auctionRepository.save(any(Auction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Auction result = auctionService.changeTitle(referenceNumber, newTitle);

        assertThat(result.getTitle()).isEqualTo(newTitle);

        verify(auctionRepository, times(1)).findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        verify(auctionRepository, times(1)).save(auction);
    }

    @Test
    @DisplayName("Test: change title should throw when title is blank")
    void shouldThrowWhenNewTitleIsBlank() {
        String referenceNumber = "EL-TEST-123";

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setTitle("Stary tytuł");

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        assertThatThrownBy(() -> auctionService.changeTitle(referenceNumber, ""))
                .isInstanceOf(InvalidNewTitleException.class);

        verify(auctionRepository, times(1)).findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: change title should throw when title is too long")
    void shouldThrowWhenNewTitleIsTooLong() {
        String referenceNumber = "EL-TEST-123";
        String tooLongTitle = "A".repeat(101);

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        assertThatThrownBy(() -> auctionService.changeTitle(referenceNumber, tooLongTitle))
                .isInstanceOf(InvalidNewTitleException.class);

        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: change description happy path")
    void shouldChangeDescription() {
        String referenceNumber = "EL-TEST-123";
        String newDescription = "Nowy opis aukcji";

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setDescription("Stary opis");

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(auctionRepository.save(any(Auction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Auction result = auctionService.changeDescription(referenceNumber, newDescription);

        assertThat(result.getDescription()).isEqualTo(newDescription);

        verify(auctionRepository, times(1)).save(auction);
    }

    @Test
    @DisplayName("Test: change category happy path")
    void shouldChangeCategory() {
        String referenceNumber = "EL-TEST-123";

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setAuctionCategory(AuctionCategory.HOME);

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(auctionRepository.save(any(Auction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Auction result = auctionService.changeCategory(referenceNumber, AuctionCategory.ELECTRONICS);

        assertThat(result.getAuctionCategory()).isEqualTo(AuctionCategory.ELECTRONICS);

        verify(auctionRepository, times(1)).save(auction);
    }

    @Test
    @DisplayName("Test: change starting price happy path")
    void shouldChangeStartingPrice() {
        String referenceNumber = "EL-TEST-123";
        BigDecimal oldPrice = BigDecimal.valueOf(100);
        BigDecimal newPrice = BigDecimal.valueOf(150);

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setStartingPrice(oldPrice);
        auction.setCurrentPrice(oldPrice);

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(auctionRepository.save(any(Auction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Auction result = auctionService.changeStartingPrice(referenceNumber, newPrice);

        assertAll(
                () -> assertThat(result.getStartingPrice()).isEqualByComparingTo(newPrice),
                () -> assertThat(result.getCurrentPrice()).isEqualByComparingTo(newPrice)
        );

        verify(auctionRepository, times(1)).save(auction);
    }

    @Test
    @DisplayName("Test: change starting price should throw when someone already bid")
    void shouldThrowWhenChangingStartingPriceAfterBid() {
        String referenceNumber = "EL-TEST-123";

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setStartingPrice(BigDecimal.valueOf(100));
        auction.setCurrentPrice(BigDecimal.valueOf(150));

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        assertThatThrownBy(() -> auctionService.changeStartingPrice(referenceNumber, BigDecimal.valueOf(200)))
                .isInstanceOf(CantChangeStartingPriceException.class);

        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: finish auction earlier happy path")
    void shouldFinishAuctionEarlier() {
        String referenceNumber = "EL-TEST-123";

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);
        auction.setAuctionStatus(AuctionStatus.ACTIVE);

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        when(auctionRepository.save(any(Auction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Auction result = auctionService.finishAuctionEarlier(referenceNumber);

        assertAll(
                () -> assertThat(result.getAuctionStatus()).isEqualTo(AuctionStatus.FINISHED),
                () -> assertThat(result.getEndTime()).isNotNull()
        );

        verify(auctionRepository, times(1)).save(auction);
    }

    @Test
    @DisplayName("Test: find auction by reference number happy path")
    void shouldFindAuctionByReferenceNumber() {
        String referenceNumber = "EL-TEST-123";

        Auction auction = new Auction();
        auction.setReferenceNumber(referenceNumber);

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.of(auction));

        Auction result = auctionService.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);

        assertThat(result.getReferenceNumber()).isEqualTo(referenceNumber);

        verify(auctionRepository, times(1)).findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
    }

    @Test
    @DisplayName("Test: find auction should throw when auction does not exist")
    void shouldThrowWhenAuctionDoesNotExist() {
        String referenceNumber = "NO-AUCTION-123";

        when(auctionRepository.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> auctionService.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber))
                .isInstanceOf(AuctionNotFoundByReferenceNumber.class);
    }

    @Test
    @DisplayName("Test: find all auctions")
    void shouldFindAllAuctions() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

        Auction auction1 = new Auction();
        auction1.setTitle("PlayStation 5");

        Auction auction2 = new Auction();
        auction2.setTitle("iPhone 13");

        Page<Auction> page = new PageImpl<>(List.of(auction1, auction2), pageable, 2);

        when(auctionRepository.findAll(pageable)).thenReturn(page);

        Page<Auction> result = auctionService.findAllAuctions(pageable);

        assertAll(
                () -> assertThat(result.getContent()).hasSize(2),
                () -> assertThat(result.getContent().getFirst().getTitle()).isEqualTo("PlayStation 5"),
                () -> assertThat(result.getTotalElements()).isEqualTo(2)
        );

        verify(auctionRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Test: find auctions by category")
    void shouldFindAuctionsByCategory() {
        Pageable pageable = PageRequest.of(0, 10);

        Auction auction = new Auction();
        auction.setAuctionCategory(AuctionCategory.ELECTRONICS);

        Page<Auction> page = new PageImpl<>(List.of(auction), pageable, 1);

        when(auctionRepository.findAllByAuctionCategory(AuctionCategory.ELECTRONICS, pageable))
                .thenReturn(page);

        Page<Auction> result = auctionService.findAllByAuctionCategory(AuctionCategory.ELECTRONICS, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAuctionCategory()).isEqualTo(AuctionCategory.ELECTRONICS);

        verify(auctionRepository, times(1))
                .findAllByAuctionCategory(AuctionCategory.ELECTRONICS, pageable);
    }

    @Test
    @DisplayName("Test: find auctions by current price between")
    void shouldFindAuctionsByCurrentPriceBetween() {
        Pageable pageable = PageRequest.of(0, 10);

        BigDecimal min = BigDecimal.valueOf(100);
        BigDecimal max = BigDecimal.valueOf(500);

        Auction auction = new Auction();
        auction.setCurrentPrice(BigDecimal.valueOf(250));

        Page<Auction> page = new PageImpl<>(List.of(auction), pageable, 1);

        when(auctionRepository.findAllByCurrentPriceBetween(min, max, pageable))
                .thenReturn(page);

        Page<Auction> result = auctionService.findAllByCurrentPriceIsBetween(min, max, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getCurrentPrice()).isEqualByComparingTo(BigDecimal.valueOf(250));

        verify(auctionRepository, times(1))
                .findAllByCurrentPriceBetween(min, max, pageable);
    }

    @Test
    @DisplayName("Test: find auctions by owner username")
    void shouldFindAuctionsByOwnerUsername() {
        Pageable pageable = PageRequest.of(0, 10);
        String ownerUsername = "Mateo21";

        User owner = new User();
        owner.setUsername(ownerUsername);

        Auction auction = new Auction();
        auction.setOwner(owner);

        Page<Auction> page = new PageImpl<>(List.of(auction), pageable, 1);

        when(auctionRepository.findAllByOwner_UsernameEqualsIgnoreCase(ownerUsername, pageable))
                .thenReturn(page);

        Page<Auction> result = auctionService.findAllByOwnerUsernameEqualsIgnoreCase(ownerUsername, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getOwner().getUsername()).isEqualTo(ownerUsername);

        verify(auctionRepository, times(1))
                .findAllByOwner_UsernameEqualsIgnoreCase(ownerUsername, pageable);
    }
}