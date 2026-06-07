package pl.auction_system.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.auction_system.dto.CreateUserRequest;
import pl.auction_system.dto.LoginRequest;
import pl.auction_system.model.User;
import pl.auction_system.service.UserService;

/**
 * Logowanie, rejestracja i wylogowanie — sesyjne (HttpSession).
 * Bez Spring Security — prostota i czytelność dla projektu zaliczeniowego.
 */
@Controller
@RequiredArgsConstructor
public class AuthWebController {

    private final UserService userService;

    // ============ LOGIN ============
    @GetMapping("/login")
    public String loginForm(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        try {
            User user = userService.findUserByUsernameEqualsIgnoreCase(loginRequest.getUsername());
            session.setAttribute("currentUser", user);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("loginError", "Nie znaleziono użytkownika: " + loginRequest.getUsername());
            return "login";
        }
    }

    // ============ REGISTER ============
    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("createUserRequest")) {
            model.addAttribute("createUserRequest", new CreateUserRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("createUserRequest") CreateUserRequest request,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            User user = userService.addUser(request);
            session.setAttribute("currentUser", user);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("registerError", e.getMessage());
            return "register";
        }
    }

    // ============ LOGOUT ============
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
