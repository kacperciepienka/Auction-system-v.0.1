package pl.auction_system.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;
import pl.auction_system.model.User;

/**
 * Globalna porada dla wszystkich kontrolerów Web.
 * Automatycznie dodaje aktualnie zalogowanego użytkownika do każdego modelu,
 * dzięki czemu w szablonach Thymeleaf można używać ${currentUser} wszędzie.
 */
@ControllerAdvice(basePackages = "pl.auction_system.web")
public class GlobalModelAdvice {

    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}
