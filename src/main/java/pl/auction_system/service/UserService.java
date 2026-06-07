package pl.auction_system.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.auction_system.dto.CreateUserRequest;
import pl.auction_system.exception.*;
import pl.auction_system.mapper.CreateUserRequestMapper;
import pl.auction_system.model.AccType;
import pl.auction_system.model.User;
import pl.auction_system.repository.UserRepository;

import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final CreateUserRequestMapper createUserRequestMapper;
    Random random = new Random();

    // POST
    public User addUser(@NonNull CreateUserRequest userToAdd) {
        userRepository.findUserByUsernameEqualsIgnoreCase(userToAdd.getUsername())
                .ifPresent(user -> {
                    throw new UserUsernameAlreadyExistException(userToAdd.getUsername());
                });

        userRepository.findUserByEmailEqualsIgnoreCase(userToAdd.getEmail())
                .ifPresent(user -> {
                    throw new UserEmailAlreadyExistException(userToAdd.getEmail());
                });

        User user = createUserRequestMapper.toEntity(userToAdd);
        user.setAccType(AccType.USER);
        int randomNum = random.nextInt(1000000, 99999999);
        String userPrefix = userToAdd.getFirstName() != null && userToAdd.getFirstName().length() >= 2
                ? userToAdd.getFirstName().substring(0,2).toUpperCase()
                : "US";
        int year = LocalDate.now().getYear();
        int day = LocalDate.now().getDayOfMonth();
        user.setUserNumber("USR-" + year + userPrefix + day + randomNum);

        return userRepository.save(user);
    }

    public User addAdmin(@NonNull CreateUserRequest userToAdd) {
        userRepository.findUserByUsernameEqualsIgnoreCase(userToAdd.getUsername())
                .ifPresent(user -> {
                    throw new UserUsernameAlreadyExistException(userToAdd.getUsername());
                });

        userRepository.findUserByEmailEqualsIgnoreCase(userToAdd.getEmail())
                .ifPresent(user -> {
                    throw new UserEmailAlreadyExistException(userToAdd.getEmail());
                });

        User user = createUserRequestMapper.toEntity(userToAdd);
        user.setAccType(AccType.ADMIN);
        int randomNum = random.nextInt(1000000, 99999999);
        int year = LocalDate.now().getYear();
        int day = LocalDate.now().getDayOfMonth();
        user.setUserNumber("ADM-" + year + day + randomNum);

        return userRepository.save(user);
    }

    // DELETE
    public void deleteUserByUsername(String username) {
        User user = userRepository.findUserByUsernameEqualsIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundByUsernameException(username));

        userRepository.delete(user);
    }

    // PUT
    public User changeUsername(String username, String newUsername){
        User user = userRepository.findUserByUsernameEqualsIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundByUsernameException(username));

        if (username.equals(newUsername)){
            return user;
        }

        userRepository.findUserByUsernameEqualsIgnoreCase(newUsername)
                .ifPresent(u -> {
                    throw new UserUsernameAlreadyExistException(newUsername);
                });

        user.setUsername(newUsername);
        return userRepository.save(user);
    }

    public User changeEmail(String username, String newEmail){
        User user = userRepository.findUserByUsernameEqualsIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundByUsernameException(username));

        if (user.getEmail().equals(newEmail)) {
            return user;
        }

        userRepository.findUserByEmailEqualsIgnoreCase(newEmail)
                .ifPresent(u -> {
                    throw new UserEmailAlreadyExistException(newEmail);
                });

        user.setEmail(newEmail);
        return userRepository.save(user);
    }

    public User changeAccTypeByUsername(String username, AccType accType){
        User user = userRepository.findUserByUsernameEqualsIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundByUsernameException(username));

        if (accType.equals(user.getAccType())){
            return user;
        }

        user.setAccType(accType);
        return userRepository.save(user);
    }

    // GET
    public User findUserByUsernameEqualsIgnoreCase(String username) {
        return userRepository.findUserByUsernameEqualsIgnoreCase(username).
                orElseThrow(() -> new UserNotFoundByUsernameException(username));
    }

    public User findUserByEmailEqualsIgnoreCase(String email) {
        return userRepository.findUserByEmailEqualsIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundByEmailException(email));
    }

    public User findUserByUserNumberEqualsIgnoreCase(String userNumber){
        return userRepository.findUserByUserNumberEqualsIgnoreCase(userNumber)
            .orElseThrow(() -> new UserNotFoundByUserNumberException(userNumber));
    }

    public Page<User> findAllUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public Page<User> findAllByAccType(AccType accType, Pageable pageable) {
        return userRepository.findAllByAccType(accType, pageable);
    }

    //by first and last name
    public Page<User> findAllByFirstNameEqualsIgnoreCase(String firstName, Pageable pageable) {
        return userRepository.findAllByFirstNameEqualsIgnoreCase(firstName, pageable);
    }

    public Page<User> findAllByLastNameEqualsIgnoreCase(String lastName, Pageable pageable) {
        return userRepository.findAllByLastNameEqualsIgnoreCase(lastName, pageable);
    }
}

