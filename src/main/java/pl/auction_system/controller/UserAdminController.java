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
import pl.auction_system.dto.UserAdminDto;
import pl.auction_system.mapper.UserAdminMapper;
import pl.auction_system.model.AccType;
import pl.auction_system.model.User;
import pl.auction_system.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0.1/admin/users")
public class UserAdminController {
    private final UserService userService;
    private final UserAdminMapper userAdminMapper;
    // POST
    @PostMapping("/register")
    public ResponseEntity<UserAdminDto> addNewAdminAcc(@Valid @RequestBody CreateUserRequest user){
        User userAdmin = userService.addAdmin(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAdminMapper.toDto(userAdmin));
    }

    @PostMapping("/login")
    public ResponseEntity<UserAdminDto> loginToAdminAcc(@Valid @RequestBody LoginRequest login){
        User user = userService.findUserByUsernameEqualsIgnoreCase(login.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(userAdminMapper.toDto(user));
    }

    // DELETE
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteAdminUser(@PathVariable String username){
        userService.deleteUserByUsername(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // PUT
    @PutMapping("/{username}/username")
    public ResponseEntity<UserAdminDto> changeUsername(@PathVariable String username,
                                                       @RequestParam String newUsername){
        User user = userService.changeUsername(username, newUsername);
        return ResponseEntity.status(HttpStatus.OK).body(userAdminMapper.toDto(user));
    }

    @PutMapping("/{username}/email")
    public ResponseEntity<UserAdminDto> changeEmail(@PathVariable String username,
                                                    @RequestParam String newEmail){
        User user = userService.changeEmail(username, newEmail);
        return ResponseEntity.status(HttpStatus.OK).body(userAdminMapper.toDto(user));
    }

    @PutMapping("/{username}/accType")
    public ResponseEntity<UserAdminDto> changeAccType(@PathVariable String username,
                                                      @RequestParam AccType accType){
        User user = userService.changeAccTypeByUsername(username, accType);
        return ResponseEntity.status(HttpStatus.OK).body(userAdminMapper.toDto(user));
    }

    // GET
    @GetMapping("/me")
    public ResponseEntity<UserAdminDto> getMyProfile(@RequestParam String username){
        User user = userService.findUserByUsernameEqualsIgnoreCase(username);
        return ResponseEntity.status(HttpStatus.OK).body(userAdminMapper.toDto(user));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserAdminDto> findUserByUsername(@PathVariable String username){
        User user = userService.findUserByUsernameEqualsIgnoreCase(username);
        return ResponseEntity.status(HttpStatus.OK).body(userAdminMapper.toDto(user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserAdminDto> findUserByEmail(@PathVariable String email){
        User user = userService.findUserByEmailEqualsIgnoreCase(email);
        return ResponseEntity.status(HttpStatus.OK).body(userAdminMapper.toDto(user));
    }

    @GetMapping("/user-number/{userNumber}")
    public ResponseEntity<UserAdminDto> findUserByUserNumber(@PathVariable String userNumber){
        User user = userService.findUserByUserNumberEqualsIgnoreCase(userNumber);
        return ResponseEntity.status(HttpStatus.OK).body(userAdminMapper.toDto(user));
    }

    @GetMapping
    public ResponseEntity<Page<UserAdminDto>> allUsers(Pageable pageable){
        Page<User> users = userService.findAllUsers(pageable);
        Page<UserAdminDto> dtoPage = users.map(userAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "accType")
    public ResponseEntity<Page<UserAdminDto>> allUsersByAccType(@RequestParam AccType accType,
                                                                Pageable pageable){
        Page<User> users = userService.findAllByAccType(accType, pageable);
        Page<UserAdminDto> dtoPage = users.map(userAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "firstName")
    public ResponseEntity<Page<UserAdminDto>> allUsersByFirstName(@RequestParam String firstName,
                                                                  Pageable pageable){
        Page<User> users = userService.findAllByFirstNameEqualsIgnoreCase(firstName, pageable);
        Page<UserAdminDto> dtoPage = users.map(userAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @GetMapping(params = "lastName")
    public ResponseEntity<Page<UserAdminDto>> allUsersByLastName(@RequestParam String lastName,
                                                                 Pageable pageable){
        Page<User> users = userService.findAllByLastNameEqualsIgnoreCase(lastName, pageable);
        Page<UserAdminDto> dtoPage = users.map(userAdminMapper::toDto);

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }
}
