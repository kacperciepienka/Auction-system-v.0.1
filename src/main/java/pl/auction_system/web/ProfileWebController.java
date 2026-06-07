package pl.auction_system.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.auction_system.model.User;
import pl.auction_system.service.AuctionService;
import pl.auction_system.service.BidService;
import pl.auction_system.service.UserService;

/**
 * Profil zalogowanego użytkownika:
 *  - dane konta,
 *  - lista wystawionych aukcji,
 *  - lista złożonych ofert,
 *  - zmiana username / email.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class ProfileWebController {

    private final UserService userService;
    private final AuctionService auctionService;
    private final BidService bidService;

    @GetMapping
    public String profile(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            ra.addFlashAttribute("error", "Musisz być zalogowany");
            return "redirect:/login";
        }
        var myAuctions = auctionService.findAllByOwnerUsernameEqualsIgnoreCase(
                user.getUsername(),
                PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "startTime"))
        ).getContent();

        var myBids = bidService.findAllByBidder_UsernameEqualsIgnoreCase(
                user.getUsername(),
                PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "bidTime"))
        ).getContent();

        model.addAttribute("user", user);
        model.addAttribute("myAuctions", myAuctions);
        model.addAttribute("myBids", myBids);
        return "profile";
    }

    @PostMapping("/username")
    public String changeUsername(@RequestParam String newUsername,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";
        try {
            User updated = userService.changeUsername(user.getUsername(), newUsername);
            session.setAttribute("currentUser", updated);
            ra.addFlashAttribute("success", "Nazwa użytkownika zmieniona");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account";
    }

    @PostMapping("/email")
    public String changeEmail(@RequestParam String newEmail,
                              HttpSession session,
                              RedirectAttributes ra) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";
        try {
            User updated = userService.changeEmail(user.getUsername(), newEmail);
            session.setAttribute("currentUser", updated);
            ra.addFlashAttribute("success", "Email zmieniony");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/account";
    }
}
