package pl.auction_system.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.auction_system.model.AccType;
import pl.auction_system.model.User;
import pl.auction_system.service.AuctionService;
import pl.auction_system.service.BidService;
import pl.auction_system.service.UserService;

/**
 * Panel administratora: zarządzanie użytkownikami, aukcjami i ofertami.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminWebController {

    private final UserService userService;
    private final AuctionService auctionService;
    private final BidService bidService;

    /** Sprawdzenie czy zalogowany jest adminem — wspólne dla wszystkich akcji. */
    private boolean isAdmin(HttpSession session) {
        User u = (User) session.getAttribute("currentUser");
        return u != null && u.getAccType() == AccType.ADMIN;
    }

    @GetMapping
    public String dashboard(HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) {
            ra.addFlashAttribute("error", "Tylko administrator ma dostęp");
            return "redirect:/login";
        }
        return "redirect:/admin/users";
    }

    // ===== USERS =====
    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(required = false) AccType accType,
                        HttpSession session, Model model, RedirectAttributes ra) {
        if (!isAdmin(session)) {
            ra.addFlashAttribute("error", "Tylko administrator ma dostęp");
            return "redirect:/login";
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));
        var users = (accType != null)
                ? userService.findAllByAccType(accType, pageable)
                : userService.findAllUsers(pageable);

        model.addAttribute("users", users);
        model.addAttribute("filterAccType", accType);
        model.addAttribute("accTypes", AccType.values());
        model.addAttribute("currentPage", page);
        return "admin/users";
    }

    @PostMapping("/users/{username}/delete")
    public String deleteUser(@PathVariable String username,
                             HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            userService.deleteUserByUsername(username);
            ra.addFlashAttribute("success", "Użytkownik " + username + " usunięty");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{username}/acctype")
    public String changeAccType(@PathVariable String username,
                                @RequestParam AccType accType,
                                HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            userService.changeAccTypeByUsername(username, accType);
            ra.addFlashAttribute("success", "Zmieniono typ konta dla " + username);
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ===== AUCTIONS =====
    @GetMapping("/auctions")
    public String auctions(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           HttpSession session, Model model, RedirectAttributes ra) {
        if (!isAdmin(session)) {
            ra.addFlashAttribute("error", "Tylko administrator ma dostęp");
            return "redirect:/login";
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        var auctions = auctionService.findAllAuctions(pageable);
        model.addAttribute("auctions", auctions);
        model.addAttribute("currentPage", page);
        return "admin/auctions";
    }

    // ===== BIDS =====
    @GetMapping("/bids")
    public String bids(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       HttpSession session, Model model, RedirectAttributes ra) {
        if (!isAdmin(session)) {
            ra.addFlashAttribute("error", "Tylko administrator ma dostęp");
            return "redirect:/login";
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "bidTime"));
        var bids = bidService.findAllBids(pageable);
        model.addAttribute("bids", bids);
        model.addAttribute("currentPage", page);
        return "admin/bids";
    }
}
