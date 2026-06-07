package pl.auction_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auction_system.dto.AuctionAdminDto;
import pl.auction_system.dto.CreateAuctionRequest;
import pl.auction_system.mapper.AuctionAdminMapper;
import pl.auction_system.model.Auction;
import pl.auction_system.model.AuctionCategory;
import pl.auction_system.model.AuctionStatus;
import pl.auction_system.service.AuctionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0.1/admin/auctions")
public class AuctionAdminController {
    private final AuctionService auctionService;
    private final AuctionAdminMapper auctionAdminMapper;

    // POST
    @PostMapping("/user/{userUsername}")
    public ResponseEntity<AuctionAdminDto> addAuction(@Valid @RequestBody CreateAuctionRequest auction,
                                                      @PathVariable String userUsername) {
        Auction auctionToAdd = auctionService.addAuction(auction, userUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionAdminMapper.toDto(auctionToAdd));
    }

    // DELETE
    @DeleteMapping("/{referenceNumber}")
    public ResponseEntity<Void> deleteAuction(@PathVariable String referenceNumber) {
        auctionService.deleteAuction(referenceNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // PUT
    @PutMapping("/{referenceNumber}/title")
    public ResponseEntity<AuctionAdminDto> changeAuctionTitle(@PathVariable String referenceNumber,
                                                              @RequestParam String newTitle) {
        Auction auction = auctionService.changeTitle(referenceNumber, newTitle);
        return ResponseEntity.status(HttpStatus.OK).body(auctionAdminMapper.toDto(auction));
    }

    @PutMapping("/{referenceNumber}/description")
    public ResponseEntity<AuctionAdminDto> changeAuctionDescription(@PathVariable String referenceNumber,
                                                                    @RequestParam String newDescription) {
        Auction auction = auctionService.changeDescription(referenceNumber, newDescription);
        return ResponseEntity.status(HttpStatus.OK).body(auctionAdminMapper.toDto(auction));
    }

    @PutMapping("/{referenceNumber}/category")
    public ResponseEntity<AuctionAdminDto> changeAuctionCategory(@PathVariable String referenceNumber,
                                                                 @RequestParam AuctionCategory category) {
        Auction auction = auctionService.changeCategory(referenceNumber, category);
        return ResponseEntity.status(HttpStatus.OK).body(auctionAdminMapper.toDto(auction));
    }

    @PutMapping("/{referenceNumber}/startingPrice")
    public ResponseEntity<AuctionAdminDto> changeAuctionStartingPrice(@PathVariable String referenceNumber,
                                                                      @RequestParam BigDecimal newStartingPrice) {
        Auction auction = auctionService.changeStartingPrice(referenceNumber, newStartingPrice);
        return ResponseEntity.status(HttpStatus.OK).body(auctionAdminMapper.toDto(auction));
    }

    @PutMapping("/{referenceNumber}/end")
    public ResponseEntity<AuctionAdminDto> finishAuctionEarlier(@PathVariable String referenceNumber) {
        Auction auction = auctionService.finishAuctionEarlier(referenceNumber);
        return ResponseEntity.status(HttpStatus.OK).body(auctionAdminMapper.toDto(auction));
    }


    // GET
    @GetMapping("/{referenceNumber}")
    public ResponseEntity<AuctionAdminDto> findAuctionByReferenceNumber(@PathVariable String referenceNumber) {
        Auction auction = auctionService.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        return ResponseEntity.status(HttpStatus.OK).body(auctionAdminMapper.toDto(auction));
    }

    @GetMapping()
    public ResponseEntity<Page<AuctionAdminDto>> allAuctions(Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllAuctions(pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "title")
    public ResponseEntity<Page<AuctionAdminDto>> findByTitle(@RequestParam String title,
                                                             Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByTitleContainingIgnoreCase(title, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "category")
    public ResponseEntity<Page<AuctionAdminDto>> findByAuctionCategory(@RequestParam AuctionCategory category,
                                                                       Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByAuctionCategory(category, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "maxCurrentPrice")
    public ResponseEntity<Page<AuctionAdminDto>> findByCurrentPriceIsLessThanEqual(@RequestParam BigDecimal maxCurrentPrice,
                                                                                   Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByCurrentPriceIsLessThanEqual(maxCurrentPrice, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "minCurrentPrice")
    public ResponseEntity<Page<AuctionAdminDto>> findByCurrentPriceIsGreaterThanEqual(@RequestParam BigDecimal minCurrentPrice,
                                                                                      Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByCurrentPriceIsGreaterThanEqual(minCurrentPrice, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"minCurrentPrice","maxCurrentPrice"})
    public ResponseEntity<Page<AuctionAdminDto>> findByCurrentPriceIsBetween(@RequestParam BigDecimal minCurrentPrice,
                                                                             @RequestParam BigDecimal maxCurrentPrice,
                                                                             Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByCurrentPriceIsBetween(minCurrentPrice, maxCurrentPrice, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "startTime")
    public ResponseEntity<Page<AuctionAdminDto>> findByStartingTime(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                                                    Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByStartTime(startTime, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "startTimeAfter")
    public ResponseEntity<Page<AuctionAdminDto>> findByStartingTimeAfter(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeAfter,
                                                                         Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByStartTimeIsAfter(startTimeAfter, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "startTimeBefore")
    public ResponseEntity<Page<AuctionAdminDto>> findByStartingTimeBefore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeBefore,
                                                                          Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByStartTimeIsBefore(startTimeBefore, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"startTimeAfter", "startTimeBefore"})
    public ResponseEntity<Page<AuctionAdminDto>> findByStartingTimeBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeAfter,
                                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeBefore,
                                                                           Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByStartTimeIsBetween(startTimeAfter, startTimeBefore, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "endTime")
    public ResponseEntity<Page<AuctionAdminDto>> findByEndingTime(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                                                  Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByEndTime(endTime, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "endTimeAfter")
    public ResponseEntity<Page<AuctionAdminDto>> findByEndingTimeAfter(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimeAfter,
                                                                       Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByEndTimeIsAfter(endTimeAfter, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "endTimeBefore")
    public ResponseEntity<Page<AuctionAdminDto>> findByEndingTimeBefore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimeBefore,
                                                                        Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByEndTimeIsBefore(endTimeBefore, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"endTimeAfter", "endTimeBefore"})
    public ResponseEntity<Page<AuctionAdminDto>> findByEndingTimeBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimeAfter,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimeBefore,
                                                                         Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByEndTimeIsBetween(endTimeAfter, endTimeBefore, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "ownerUsername")
    public ResponseEntity<Page<AuctionAdminDto>> findAllByOwnerUsername(@RequestParam String ownerUsername,
                                                                        Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByOwnerUsernameEqualsIgnoreCase(ownerUsername, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "userNumber")
    public ResponseEntity<Page<AuctionAdminDto>> findAllByOwnerUserNumber(@RequestParam String userNumber,
                                                                          Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByOwner_UserNumberEqualsIgnoreCase(userNumber, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "auctionStatus")
    public ResponseEntity<Page<AuctionAdminDto>> findAllAuctionByAuctionStatus(@RequestParam AuctionStatus auctionStatus,
                                                                               Pageable pageable){
        Page<Auction> auctions = auctionService.findAllByAuctionStatus(auctionStatus, pageable);
        Page<AuctionAdminDto> dtoPage = auctions.map(auctionAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }
}
