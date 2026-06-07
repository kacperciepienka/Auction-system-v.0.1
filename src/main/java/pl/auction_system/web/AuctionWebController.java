package pl.auction_system.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.auction_system.dto.CreateAuctionRequest;
import pl.auction_system.dto.CreateBidRequest;
import pl.auction_system.model.AccType;
import pl.auction_system.model.Auction;
import pl.auction_system.model.AuctionCategory;
import pl.auction_system.model.Bid;
import pl.auction_system.model.User;
import pl.auction_system.service.AuctionService;
import pl.auction_system.service.BidService;

import java.util.List;

/**
 * Web kontroler dla aukcji — szczegóły, dodawanie nowej aukcji, składanie ofert.
 * Realizuje wymaganie pkt 4 zadania (interfejs użytkownika).
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/auctions")
public class AuctionWebController {

    private final AuctionService auctionService;
    private final BidService bidService;

    // ============ Lista kategorii dla formularzy ============
    @ModelAttribute("categories")
    public AuctionCategory[] categories() {
        return AuctionCategory.values();
    }

    // ============ Szczegóły aukcji ============
    @GetMapping("/{referenceNumber}")
    public String detail(@PathVariable String referenceNumber, Model model) {
        Auction auction = auctionService.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        List<Bid> bids = bidService
                .findAllByAuction_ReferenceNumberEqualsIgnoreCase(
                        referenceNumber,
                        PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "amount")))
                .getContent();

        model.addAttribute("auction", auction);
        model.addAttribute("bids", bids);
        if (!model.containsAttribute("createBidRequest")) {
            model.addAttribute("createBidRequest", new CreateBidRequest());
        }
        return "auction-detail";
    }

    // ============ Nowa aukcja - formularz ============
    @GetMapping("/new")
    public String newAuctionForm(HttpSession session, Model model, RedirectAttributes ra) {
        if (session.getAttribute("currentUser") == null) {
            ra.addFlashAttribute("error", "Musisz być zalogowany, aby wystawić aukcję");
            return "redirect:/login";
        }
        if (!model.containsAttribute("createAuctionRequest")) {
            model.addAttribute("createAuctionRequest", new CreateAuctionRequest());
        }
        return "auction-form";
    }

    // ============ Nowa aukcja - zapis ============
    @PostMapping
    public String createAuction(@Valid @ModelAttribute("createAuctionRequest") CreateAuctionRequest request,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes ra,
                                Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            ra.addFlashAttribute("error", "Musisz być zalogowany");
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            return "auction-form";
        }
        try {
            Auction created = auctionService.addAuction(request, user.getUsername());
            ra.addFlashAttribute("success", "Aukcja została wystawiona!");
            return "redirect:/auctions/" + created.getReferenceNumber();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auction-form";
        }
    }

    // ============ Złożenie oferty ============
    @PostMapping("/{referenceNumber}/bid")
    public String placeBid(@PathVariable String referenceNumber,
                           @Valid @ModelAttribute("createBidRequest") CreateBidRequest bidRequest,
                           BindingResult bindingResult,
                           HttpSession session,
                           RedirectAttributes ra) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            ra.addFlashAttribute("error", "Musisz być zalogowany, aby licytować");
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Nieprawidłowa kwota oferty");
            return "redirect:/auctions/" + referenceNumber;
        }
        try {
            bidService.addBid(bidRequest, currentUser.getUsername(), referenceNumber);
            ra.addFlashAttribute("success", "Twoja oferta została przyjęta!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/auctions/" + referenceNumber;
    }

    // ============ Zakończenie aukcji wcześniej ============
    @PostMapping("/{referenceNumber}/end")
    public String endAuction(@PathVariable String referenceNumber,
                             HttpSession session,
                             RedirectAttributes ra) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            ra.addFlashAttribute("error", "Musisz być zalogowany");
            return "redirect:/login";
        }
        Auction auction = auctionService.findAuctionByReferenceNumberEqualsIgnoreCase(referenceNumber);
        boolean isOwner = auction.getOwner().getUsername().equalsIgnoreCase(user.getUsername());
        boolean isAdmin = user.getAccType() == AccType.ADMIN;
        if (!isOwner && !isAdmin) {
            ra.addFlashAttribute("error", "Tylko właściciel lub administrator może zakończyć aukcję");
            return "redirect:/auctions/" + referenceNumber;
        }
        try {
            auctionService.finishAuctionEarlier(referenceNumber);
            ra.addFlashAttribute("success", "Aukcja została zakończona");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/auctions/" + referenceNumber;
    }

    // ============ Usunięcie aukcji (admin) ============
    @PostMapping("/{referenceNumber}/delete")
    public String deleteAuction(@PathVariable String referenceNumber,
                                HttpSession session,
                                RedirectAttributes ra) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getAccType() != AccType.ADMIN) {
            ra.addFlashAttribute("error", "Brak uprawnień");
            return "redirect:/auctions/" + referenceNumber;
        }
        try {
            auctionService.deleteAuction(referenceNumber);
            ra.addFlashAttribute("success", "Aukcja została usunięta");
            return "redirect:/";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auctions/" + referenceNumber;
        }
    }
}
