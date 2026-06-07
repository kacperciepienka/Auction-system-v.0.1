package pl.auction_system.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.auction_system.model.Auction;
import pl.auction_system.model.AuctionCategory;
import pl.auction_system.service.AuctionService;

import java.math.BigDecimal;

/**
 * Główna strona aplikacji — lista aukcji z filtrowaniem,
 * sortowaniem i paginacją (zgodnie z wymaganiami zadania pkt 4 i 6).
 */
@Controller
@RequiredArgsConstructor
public class HomeWebController {

    private final AuctionService auctionService;

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) AuctionCategory category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "startTime,desc") String sort,
            Model model) {

        // parse sort parameter "field,direction"
        String[] sortParts = sort.split(",");
        Sort sortObj = (sortParts.length == 2)
                ? Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0])
                : Sort.by(Sort.Direction.DESC, "startTime");

        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Backend dostępnych REST API wymaga jednego typu filtra na raz
        // dlatego stosujemy if-else (priorytetowo).
        Page<Auction> auctions;
        if (title != null && !title.isBlank()) {
            auctions = auctionService.findAllByTitleContainingIgnoreCase(title.trim(), pageable);
        } else if (category != null) {
            auctions = auctionService.findAllByAuctionCategory(category, pageable);
        } else if (minPrice != null && maxPrice != null) {
            auctions = auctionService.findAllByCurrentPriceIsBetween(minPrice, maxPrice, pageable);
        } else if (minPrice != null) {
            auctions = auctionService.findAllByCurrentPriceIsGreaterThanEqual(minPrice, pageable);
        } else if (maxPrice != null) {
            auctions = auctionService.findAllByCurrentPriceIsLessThanEqual(maxPrice, pageable);
        } else {
            auctions = auctionService.findAllAuctions(pageable);
        }

        model.addAttribute("auctions", auctions);
        model.addAttribute("categories", AuctionCategory.values());
        model.addAttribute("filterTitle", title);
        model.addAttribute("filterCategory", category);
        model.addAttribute("filterMinPrice", minPrice);
        model.addAttribute("filterMaxPrice", maxPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);

        return "home";
    }
}
