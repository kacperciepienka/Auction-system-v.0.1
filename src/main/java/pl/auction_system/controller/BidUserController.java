package pl.auction_system.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auction_system.dto.BidUserDto;
import pl.auction_system.dto.CreateBidRequest;
import pl.auction_system.mapper.BidUserMapper;
import pl.auction_system.model.Bid;
import pl.auction_system.service.BidService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0.1/bids")
public class BidUserController {
    private final BidService bidService;
    private final BidUserMapper bidUserMapper;

    // POST - user musi móc dać jakąś ofertę
    @PostMapping("/auction/{referenceNumber}")
    public ResponseEntity<BidUserDto> addBid(@Valid @RequestBody CreateBidRequest bitToAdd,
                                             @RequestParam String username,
                                             @PathVariable String referenceNumber){
        Bid bit = bidService.addBid(bitToAdd, username, referenceNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(bidUserMapper.toDto(bit));
    }

    // GET
    @GetMapping("/{bidIdNumber}")
    public ResponseEntity<BidUserDto> findBidByIdNumber(@PathVariable String bidIdNumber){
        Bid bid = bidService.findByBidIdNumberEqualsIgnoreCase(bidIdNumber);
        return ResponseEntity.status(HttpStatus.OK).body(bidUserMapper.toDto(bid));
    }

    @GetMapping(params = "bidderUsername")
    public ResponseEntity<Page<BidUserDto>> findAllBidsByBidderUsername(@RequestParam String bidderUsername,
                                                                        Pageable pageable){
        Page<Bid> bids = bidService.findAllByBidder_UsernameEqualsIgnoreCase(bidderUsername, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "maxAmount"})
    public ResponseEntity<Page<BidUserDto>> findAllByBidder_UsernameAndAmountIsLessThanEqual(@RequestParam String bidderUsername,
                                                                                             @RequestParam BigDecimal maxAmount,
                                                                                             Pageable pageable){
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndAmountIsLessThanEqual(bidderUsername, maxAmount, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "minAmount"})
    public ResponseEntity<Page<BidUserDto>> findAllByBidder_UsernameAndAmountIsGreaterThanEqual(@RequestParam String bidderUsername,
                                                                                                @RequestParam BigDecimal minAmount,
                                                                                                Pageable pageable){
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndAmountIsGreaterThanEqual(bidderUsername, minAmount, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "minAmount", "maxAmount"})
    public ResponseEntity<Page<BidUserDto>> findAllByBidder_UsernameAndAmountBetween(@RequestParam String bidderUsername,
                                                                                     @RequestParam BigDecimal minAmount,
                                                                                     @RequestParam BigDecimal maxAmount,
                                                                                     Pageable pageable){
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndAmountIsBetween(bidderUsername, minAmount, maxAmount, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "bidTime"})
    public ResponseEntity<Page<BidUserDto>> findAllByBidder_UsernameAndBidTime(@RequestParam String bidderUsername,
                                                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTime,
                                                                               Pageable pageable){
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndBidTime(bidderUsername, bidTime, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "bidTimeAfter"})
    public ResponseEntity<Page<BidUserDto>> findAllByBidder_UsernameAndBidTimeIsAfter(@RequestParam String bidderUsername,
                                                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeAfter,
                                                                                      Pageable pageable){
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndBidTimeIsAfter(bidderUsername, bidTimeAfter, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "bidTimeBefore"})
    public ResponseEntity<Page<BidUserDto>> findAllByBidder_UsernameAndBidTimeIsBefore(@RequestParam String bidderUsername,
                                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeBefore,
                                                                                       Pageable pageable){
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndBidTimeIsBefore(bidderUsername, bidTimeBefore, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "bidTimeAfter", "bidTimeBefore"})
    public ResponseEntity<Page<BidUserDto>> findAllByBidder_UsernameAndBidTimeIsBetween(@RequestParam String bidderUsername,
                                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeAfter,
                                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeBefore,
                                                                                        Pageable pageable){
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndBidTimeIsBetween(bidderUsername, bidTimeAfter, bidTimeBefore, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "auctionReferenceNumber")
    public ResponseEntity<Page<BidUserDto>> findAllByAuction_ReferenceNumberEqualsIgnoreCase(@RequestParam String auctionReferenceNumber,
                                                                                             Pageable pageable){
        Page<Bid> bids = bidService.findAllByAuction_ReferenceNumberEqualsIgnoreCase(auctionReferenceNumber, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "auctionTitle")
    public ResponseEntity<Page<BidUserDto>> findAllByAuction_TitleContainingIgnoreCase(@RequestParam String auctionTitle,
                                                                                       Pageable pageable){
        Page<Bid> bids = bidService.findAllByAuction_TitleContainingIgnoreCase(auctionTitle, pageable);
        Page<BidUserDto> dtoPage = bids.map(bidUserMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

}
