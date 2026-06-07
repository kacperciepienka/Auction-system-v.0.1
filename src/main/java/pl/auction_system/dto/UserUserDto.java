package pl.auction_system.dto;

import lombok.Data;
import pl.auction_system.model.AccType;

@Data
public class UserUserDto {
    private String userNumber;
    private String username;
    private String email;
    private String name;
    private AccType accType;
}
