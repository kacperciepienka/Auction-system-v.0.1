package pl.auction_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Entity
@Data
@Table(name = "user_account")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Length(min = 10, max = 35)
    private String userNumber;

    @NotBlank(message = "User username can't be empty")
    @Column(unique = true, length = 30)
    @Length(min = 4, max = 30, message = "User username must be between 4 and 30 characters")
    private String username;

    @NotBlank(message = "User email can't be empty")
    @Email
    @Column(unique = true, length = 70)
    @Length(max = 70, message = "Maximum length for user emails is 70 characters")
    private String email;

    @NotBlank(message = "User first name can't be empty")
    @Column(length = 60)
    @Length(max = 60, message = "Maximum length for user first name is 60 characters")
    private String firstName;

    @NotBlank(message = "User last name can't be empty")
    @Column(length = 60)
    @Length(max = 60, message = "Maximum length for user last name is 60 characters")
    private String lastName;

    // dodamy automatycznie w serwisie
    @Enumerated(EnumType.STRING)
    private AccType accType;
}
