package pl.auction_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auction_system.dto.AuctionUserDto;
import pl.auction_system.dto.CreateAuctionRequest;
import pl.auction_system.mapper.AuctionUserMapper;
import pl.auction_system.model.Auction;
import pl.auction_system.model.AuctionCategory;
import pl.auction_system.service.AuctionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0.1/auctions")
public class AuctionUserController {
    private final AuctionService auctionService;
    private final AuctionUserMapper auctionUserMapper;

    // POST
    @PostMapping("/user/{userUsername}")
    public ResponseEntity<AuctionUserDto> addAuction(@Valid @RequestBody CreateAuctionRequest auction,
                                                     @PathVariable String userUsername) {
        Auction auctionToAdd = auctionService.addAuction(auction, userUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionUserMapper.toDto(auctionToAdd));
    }

    // DELETE - doesn't exist for user

    // PUT
    @PutMapping("/{referenceNumber}/title")
    public ResponseEntity<AuctionUserDto> changeAuctionTitle(@PathVariable String referenceNumber,
                                                             @RequestParam String newTitle) {
        Auction auction = auctionService.changeTitle(referenceNumber, newTitle);
        return ResponseEntity.status(HttpStatus.OK).body(auctionUserMapper.toDto(auction));
    }

    @PutMapping("/{referenceNumber}/description")
    public ResponseEntity<AuctionUserDto> changeAuctionDescription(@PathVariable String referenceNumber,
                                                                   @RequestParam String newDescription) {
        Auction auction = auctionService.changeDescription(referenceNumber, newDescription);
        return ResponseEntity.status(HttpStatus.OK).body(auctionUserMapper.toDto(auction));
    }

    @PutMapping("/{referenceNumber}/category")
    public ResponseEntity<AuctionUserDto> changeAuctionCategory(@PathVariable String referenceNumber,
                                                                @RequestParam AuctionCategory category) {
        Auction auction = auctionService.changeCategory(referenceNumber, category);
        return ResponseEntity.status(HttpStatus.OK).body(auctionUserMapper.toDto(auction));
    }

    @PutMapping("/{referenceNumber}/startingPrice")
    public ResponseEntity<AuctionUserDto> changeAuctionStartingPrice(@PathVariable String referenceNumber,
                                                                     @RequestParam BigDecimal newStartingPrice) {
        Auction auction = auctionService.changeStartingPrice(referenceNumber, newStartingPrice);
        return ResponseEntity.status(HttpStatus.OK).body(auctionUserMapper.toDto(auction));
    }

    @PutMapping("/{referenceNumber}/end")
    public ResponseEntity<AuctionUserDto> finishAuctionEarlier(@PathVariable String referenceNumber) {
        Auction auction = auctionService.finishAuctionEarlier(referenceNumber);
        return ResponseEntity.status(HttpStatus.OK).body(auctionUserMapper.toDto(auction));
    }


    // GET
    @GetMapping("/{referenceNumber}")
    public ResponseEntity<AuctionUserDto> findAuctionByReferenceNumber(@PathVariable String referenceNumber) {
        Auction auction = auctionService.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        return ResponseEntity.status(HttpStatus.OK).body(auctionUserMapper.toDto(auction));
    }

    @GetMapping()
    public ResponseEntity<Page<AuctionUserDto>> allAuctions(Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllAuctions(pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "title")
    public ResponseEntity<Page<AuctionUserDto>> findByTitle(@RequestParam String title,
                                                            Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByTitleContainingIgnoreCase(title, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "category")
    public ResponseEntity<Page<AuctionUserDto>> findByAuctionCategory(@RequestParam AuctionCategory category,
                                                                      Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByAuctionCategory(category, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "maxCurrentPrice")
    public ResponseEntity<Page<AuctionUserDto>> findByCurrentPriceIsLessThanEqual(@RequestParam BigDecimal maxCurrentPrice,
                                                                                  Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByCurrentPriceIsLessThanEqual(maxCurrentPrice, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "minCurrentPrice")
    public ResponseEntity<Page<AuctionUserDto>> findByCurrentPriceIsGreaterThanEqual(@RequestParam BigDecimal minCurrentPrice,
                                                                                     Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByCurrentPriceIsGreaterThanEqual(minCurrentPrice, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"minCurrentPrice", "maxCurrentPrice"})
    public ResponseEntity<Page<AuctionUserDto>> findByCurrentPriceIsBetween(@RequestParam BigDecimal minCurrentPrice,
                                                                            @RequestParam BigDecimal maxCurrentPrice,
                                                                            Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByCurrentPriceIsBetween(minCurrentPrice, maxCurrentPrice, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "startTime")
    public ResponseEntity<Page<AuctionUserDto>> findByStartingTime(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                                                   Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByStartTime(startTime, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "startTimeAfter")
    public ResponseEntity<Page<AuctionUserDto>> findByStartingTimeAfter(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeAfter,
                                                                        Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByStartTimeIsAfter(startTimeAfter, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "startTimeBefore")
    public ResponseEntity<Page<AuctionUserDto>> findByStartingTimeBefore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeBefore,
                                                                         Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByStartTimeIsBefore(startTimeBefore, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"startTimeAfter", "startTimeBefore"})
    public ResponseEntity<Page<AuctionUserDto>> findByStartingTimeBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeAfter,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeBefore,
                                                                          Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByStartTimeIsBetween(startTimeAfter, startTimeBefore, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "endTime")
    public ResponseEntity<Page<AuctionUserDto>> findByEndingTime(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                                                        Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByEndTime(endTime, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "endTimeAfter")
    public ResponseEntity<Page<AuctionUserDto>> findByEndingTimeAfter(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimeAfter,
                                                                        Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByEndTimeIsAfter(endTimeAfter, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "endTimeBefore")
    public ResponseEntity<Page<AuctionUserDto>> findByEndingTimeBefore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimeBefore,
                                                                        Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByEndTimeIsBefore(endTimeBefore, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"endTimeAfter", "endTimeBefore"})
    public ResponseEntity<Page<AuctionUserDto>> findByEndingTimeBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimeAfter,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimeBefore,
                                                                        Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByEndTimeIsBetween(endTimeAfter, endTimeBefore, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "ownerUsername")
    public ResponseEntity<Page<AuctionUserDto>> findAllByOwnerUsername(@RequestParam String ownerUsername,
                                                                       Pageable pageable) {
        Page<Auction> auctions = auctionService.findAllByOwnerUsernameEqualsIgnoreCase(ownerUsername, pageable);
        Page<AuctionUserDto> dtoPage = auctions.map(auctionUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }
}
