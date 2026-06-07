package pl.auction_system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.auction_system.dto.CreateAuctionRequest;
import pl.auction_system.dto.CreateUserRequest;
import pl.auction_system.model.AccType;
import pl.auction_system.model.AuctionCategory;
import pl.auction_system.repository.UserRepository;
import pl.auction_system.service.AuctionService;
import pl.auction_system.service.UserService;

import java.math.BigDecimal;

/**
 * Wczytanie danych przykładowych przy starcie aplikacji.
 * Tworzy 1 administratora, 3 użytkowników i kilka aukcji w różnych kategoriach.
 *
 * Działa tylko dla H2 in-memory (dane znikają po restarcie).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final AuctionService auctionService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // pomiń jeśli baza już ma dane
        if (userRepository.count() > 0) {
            log.info("DataSeeder: dane już istnieją, pomijam seeding");
            return;
        }
        log.info("DataSeeder: tworzę dane przykładowe...");

        try {
            // ===== Admin =====
            CreateUserRequest admin = new CreateUserRequest();
            admin.setUsername("Admin1");
            admin.setEmail("admin@aukcje.pl");
            admin.setFirstName("Admin");
            admin.setLastName("Adminowski");
            var adminUser = userService.addUser(admin);
            userService.changeAccTypeByUsername(adminUser.getUsername(), AccType.ADMIN);

            // ===== Users =====
            CreateUserRequest u1 = new CreateUserRequest();
            u1.setUsername("Kacper99");
            u1.setEmail("kacper@example.com");
            u1.setFirstName("Kacper");
            u1.setLastName("Nowak");
            userService.addUser(u1);

            CreateUserRequest u2 = new CreateUserRequest();
            u2.setUsername("Anna2024");
            u2.setEmail("anna@example.com");
            u2.setFirstName("Anna");
            u2.setLastName("Kowalska");
            userService.addUser(u2);

            CreateUserRequest u3 = new CreateUserRequest();
            u3.setUsername("Marek77");
            u3.setEmail("marek@example.com");
            u3.setFirstName("Marek");
            u3.setLastName("Wiśniewski");
            userService.addUser(u3);

            // ===== Aukcje =====
            addAuction("Konsola PlayStation 5 Slim", "Nowa, fabrycznie zapakowana, 1TB pamięci",
                    AuctionCategory.ELECTRONICS, new BigDecimal("1899.00"), "Kacper99");
            addAuction("Smartfon iPhone 13 Pro 128GB", "Stan idealny, bateria 88%, bez rys",
                    AuctionCategory.ELECTRONICS, new BigDecimal("1499.00"), "Anna2024");
            addAuction("Obraz olejny na płótnie", "Pejzaż 50x70, sygnowany przez autora",
                    AuctionCategory.ART, new BigDecimal("450.00"), "Marek77");
            addAuction("Alufelgi 17 cali 5x112", "Uniwersalne, czarne, łatwy montaż",
                    AuctionCategory.CAR, new BigDecimal("800.00"), "Kacper99");
            addAuction("Kurtka puchowa North Face", "Bardzo ciepła, rozmiar M, kolor czarny",
                    AuctionCategory.FASHION, new BigDecimal("350.00"), "Anna2024");
            addAuction("Ekspres ciśnieniowy DeLonghi", "Świeżo po serwisie, pyszna kawa",
                    AuctionCategory.HOME, new BigDecimal("750.00"), "Marek77");
            addAuction("Perfumy Dior Sauvage", "Flakon 100ml, ubytek tylko 5ml",
                    AuctionCategory.BEAUTY, new BigDecimal("280.00"), "Anna2024");

            log.info("DataSeeder: dane przykładowe utworzone pomyślnie ✓");
            log.info("DataSeeder: zaloguj się jako 'Admin1' (admin) lub 'Kacper99' / 'Anna2024' / 'Marek77' (user)");

        } catch (Exception e) {
            log.error("DataSeeder error: {}", e.getMessage());
        }
    }

    private void addAuction(String title, String description, AuctionCategory category,
                            BigDecimal startingPrice, String ownerUsername) {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setTitle(title);
        req.setDescription(description);
        req.setAuctionCategory(category);
        req.setStartingPrice(startingPrice);
        auctionService.addAuction(req, ownerUsername);
    }
}
