package pl.auction_system.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.auction_system.model.Auction;
import pl.auction_system.model.AuctionStatus;
import pl.auction_system.repository.AuctionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionCloseScheduler {
    private final AuctionRepository auctionRepository;

    // fixed rate = 3600000 oznacza, że automat odpala się co godzinę
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void closeExpiredAuctions() {
        log.info("Automat: Sprawdzam aukcje do zamknięcia...");

        // Szukamy aukcji, które są ACTIVE i ich czas minął
        List<Auction> expiredAuctions = auctionRepository
                .findAllByAuctionStatusAndEndTimeLessThanEqual(AuctionStatus.ACTIVE, LocalDateTime.now());

        if (expiredAuctions.isEmpty()) {
            return;
        }

        for (Auction auction : expiredAuctions) {
            auction.setAuctionStatus(AuctionStatus.FINISHED);
            log.info("Automat: Aukcja {} została automatycznie zakończona.", auction.getReferenceNumber());
        }
    }
}