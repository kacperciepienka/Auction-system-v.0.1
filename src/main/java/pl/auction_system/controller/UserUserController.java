package pl.auction_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auction_system.dto.CreateUserRequest;
import pl.auction_system.dto.LoginRequest;
import pl.auction_system.dto.UserPublicDto;
import pl.auction_system.dto.UserUserDto;
import pl.auction_system.mapper.UserPublicMapper;
import pl.auction_system.mapper.UserUserMapper;
import pl.auction_system.model.User;
import pl.auction_system.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0.1/users")
public class UserUserController {
    private final UserService userService;
    private final UserUserMapper userUserMapper;
    private final UserPublicMapper userPublicMapper;

    // Post (logowanie/rejestracja)
    @PostMapping("/register")
    public ResponseEntity<UserUserDto> addNewUser(@Valid @RequestBody CreateUserRequest user){
        User userToAdd = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userUserMapper.toDto(userToAdd));
    }

    @PostMapping("/login")
    public ResponseEntity<UserUserDto> loginToAcc(@Valid @RequestBody LoginRequest login){
        User user = userService.findUserByUsernameEqualsIgnoreCase(login.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(userUserMapper.toDto(user));
    }

    // DELETE
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username){
        userService.deleteUserByUsername(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // PUT
    @PutMapping("/{username}/username")
    public ResponseEntity<UserUserDto> changeUsername(@PathVariable String username,
                                                      @RequestParam String newUsername){
        User user = userService.changeUsername(username, newUsername);
        return ResponseEntity.status(HttpStatus.OK).body(userUserMapper.toDto(user));
    }

    @PutMapping("/{username}/email")
    public ResponseEntity<UserUserDto> changeEmail(@PathVariable String username,
                                                   @RequestParam String newEmail){
        User user = userService.changeEmail(username, newEmail);
        return ResponseEntity.status(HttpStatus.OK).body(userUserMapper.toDto(user));
    }

    // GET
    @GetMapping("/me")
    public ResponseEntity<UserUserDto> getMyProfile(@RequestParam String username) {
        User user = userService.findUserByUsernameEqualsIgnoreCase(username);
        return ResponseEntity.status(HttpStatus.OK).body(userUserMapper.toDto(user));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserPublicDto> findUserByUsername(@PathVariable String username){
        User user = userService.findUserByUsernameEqualsIgnoreCase(username);
        return ResponseEntity.status(HttpStatus.OK).body(userPublicMapper.toDto(user));
    }

    @GetMapping
    public ResponseEntity<Page<UserPublicDto>> allUsers(Pageable pageable){
        Page<User> users = userService.findAllUsers(pageable);
        Page<UserPublicDto> dtoPage = users.map(userPublicMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "firstName")
    public ResponseEntity<Page<UserPublicDto>> allUsersByFirstName(@RequestParam String firstName,
                                                                   Pageable pageable){
        Page<User> users = userService.findAllByFirstNameEqualsIgnoreCase(firstName, pageable);
        Page<UserPublicDto> dtoPage = users.map(userPublicMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "lastName")
    public ResponseEntity<Page<UserPublicDto>> allUsersByLastName(@RequestParam String lastName,
                                                                  Pageable pageable){
        Page<User> users = userService.findAllByLastNameEqualsIgnoreCase(lastName, pageable);
        Page<UserPublicDto> dtoPage = users.map(userPublicMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }
}
