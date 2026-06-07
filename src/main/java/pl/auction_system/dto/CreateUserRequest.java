package pl.auction_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateUserRequest {
    @NotBlank(message = "User username can't be empty")
    @Length(min = 4, max = 30, message = "User username must be between 4 and 30 characters")
    private String username;

    @NotBlank(message = "User email can't be empty")
    @Email
    @Length(max = 70, message = "Maximum length for user emails is 70 characters")
    private String email;

    @NotBlank(message = "User first name can't be empty")
    @Length(max = 60, message = "Maximum length for user first name is 60 characters")
    private String firstName;

    @NotBlank(message = "User last name can't be empty")
    @Length(max = 60, message = "Maximum length for user last name is 60 characters")
    private String lastName;
}
