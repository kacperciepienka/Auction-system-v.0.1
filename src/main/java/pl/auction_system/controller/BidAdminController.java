package pl.auction_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auction_system.dto.BidAdminDto;
import pl.auction_system.dto.CreateBidRequest;
import pl.auction_system.mapper.BidAdminMapper;
import pl.auction_system.model.Bid;
import pl.auction_system.service.BidService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0.1/admin/bids")
public class BidAdminController {
    private final BidService bidService;
    private final BidAdminMapper bidAdminMapper;

    // POST
    @PostMapping("/auction/{referenceNumber}")
    public ResponseEntity<BidAdminDto> addBid(@Valid @RequestBody CreateBidRequest bitToAdd,
                                              @RequestParam String username,
                                              @PathVariable String referenceNumber) {
        Bid bit = bidService.addBid(bitToAdd, username, referenceNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(bidAdminMapper.toDto(bit));
    }

    // GET
    @GetMapping("/{bidIdNumber}")
    public ResponseEntity<BidAdminDto> findBidByIdNumber(@PathVariable String bidIdNumber) {
        Bid bid = bidService.findByBidIdNumberEqualsIgnoreCase(bidIdNumber);
        return ResponseEntity.status(HttpStatus.OK).body(bidAdminMapper.toDto(bid));
    }

    @GetMapping()
    public ResponseEntity<Page<BidAdminDto>> findAllBids(Pageable pageable) {
        Page<Bid> bids = bidService.findAllBids(pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "bidderUsername")
    public ResponseEntity<Page<BidAdminDto>> findAllBidsByBidderUsername(@RequestParam String bidderUsername,
                                                                         Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidder_UsernameEqualsIgnoreCase(bidderUsername, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "userNumber")
    public ResponseEntity<Page<BidAdminDto>> findAllByBidder_UserNumber(@RequestParam String userNumber,
                                                                        Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidder_UserNumberEqualsIgnoreCase(userNumber, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "amountMax")
    public ResponseEntity<Page<BidAdminDto>> findAllByAmountIsLessThanEqual(@RequestParam BigDecimal amountMax,
                                                                            Pageable pageable) {
        Page<Bid> bids = bidService.findAllByAmountIsLessThanEqual(amountMax, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "amountMin")
    public ResponseEntity<Page<BidAdminDto>> findAllByAmountIsGreaterThanEqual(@RequestParam BigDecimal amountMin,
                                                                               Pageable pageable) {
        Page<Bid> bids = bidService.findAllByAmountIsGreaterThanEqual(amountMin, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"amountMin", "amountMax"})
    public ResponseEntity<Page<BidAdminDto>> findAllByAmountIsBetween(@RequestParam BigDecimal amountMin,
                                                                      @RequestParam BigDecimal amountMax,
                                                                      Pageable pageable) {
        Page<Bid> bids = bidService.findAllByAmountIsBetween(amountMin, amountMax, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "amountMax"})
    public ResponseEntity<Page<BidAdminDto>> findAllByBidder_UsernameAndAmountIsLessThanEqual(@RequestParam String bidderUsername,
                                                                                              @RequestParam BigDecimal amountMax,
                                                                                              Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndAmountIsLessThanEqual(bidderUsername, amountMax, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "amountMin"})
    public ResponseEntity<Page<BidAdminDto>> findAllByBidder_UsernameAndAmountIsGreaterThanEqual(@RequestParam String bidderUsername,
                                                                                                 @RequestParam BigDecimal amountMin,
                                                                                                 Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndAmountIsGreaterThanEqual(bidderUsername, amountMin, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "amountMin", "amountMax"})
    public ResponseEntity<Page<BidAdminDto>> findAllByBidder_UsernameAndAmountBetween(@RequestParam String bidderUsername,
                                                                                      @RequestParam BigDecimal amountMin,
                                                                                      @RequestParam BigDecimal amountMax,
                                                                                      Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndAmountIsBetween(bidderUsername, amountMin, amountMax, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "bidTime")
    public ResponseEntity<Page<BidAdminDto>> findAllByBidTime(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTime,
                                                              Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidTime(bidTime, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "bidTimeAfter")
    public ResponseEntity<Page<BidAdminDto>> findAllByBidTimeIsAfter(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeAfter,
                                                                     Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidTimeIsAfter(bidTimeAfter, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "bidTimeBefore")
    public ResponseEntity<Page<BidAdminDto>> findAllByBidTimeIsBefore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeBefore,
                                                                      Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidTimeIsBefore(bidTimeBefore, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidTimeAfter", "bidTimeBefore"})
    public ResponseEntity<Page<BidAdminDto>> findAllByBidTimeIsBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeAfter,
                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeBefore,
                                                                       Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidTimeIsBetween(bidTimeAfter, bidTimeBefore, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "bidTime"})
    public ResponseEntity<Page<BidAdminDto>> findAllByBidder_UsernameAndBidTime(@RequestParam String bidderUsername,
                                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTime,
                                                                                Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndBidTime(bidderUsername, bidTime, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "bidTimeAfter"})
    public ResponseEntity<Page<BidAdminDto>> findAllByBidder_UsernameAndBidTimeIsAfter(@RequestParam String bidderUsername,
                                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeAfter,
                                                                                       Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndBidTimeIsAfter(bidderUsername, bidTimeAfter, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "bidTimeBefore"})
    public ResponseEntity<Page<BidAdminDto>> findAllByBidder_UsernameAndBidTimeIsBefore(@RequestParam String bidderUsername,
                                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeBefore,
                                                                                        Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndBidTimeIsBefore(bidderUsername, bidTimeBefore, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = {"bidderUsername", "bidTimeAfter", "bidTimeBefore"})
    public ResponseEntity<Page<BidAdminDto>> findAllByBidder_UsernameAndBidTimeIsBetween(@RequestParam String bidderUsername,
                                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeAfter,
                                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bidTimeBefore,
                                                                                         Pageable pageable) {
        Page<Bid> bids = bidService.findAllByBidder_UsernameAndBidTimeIsBetween(bidderUsername, bidTimeAfter, bidTimeBefore, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "auctionReferenceNumber")
    public ResponseEntity<Page<BidAdminDto>> findAllByAuction_ReferenceNumber(@RequestParam String auctionReferenceNumber,
                                                                              Pageable pageable) {
        Page<Bid> bids = bidService.findAllByAuction_ReferenceNumberEqualsIgnoreCase(auctionReferenceNumber, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "title")
    public ResponseEntity<Page<BidAdminDto>> findAllByAuction_Title(@RequestParam String title,
                                                                    Pageable pageable) {
        Page<Bid> bids = bidService.findAllByAuction_TitleContainingIgnoreCase(title, pageable);
        Page<BidAdminDto> dtoPage = bids.map(bidAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }
}
