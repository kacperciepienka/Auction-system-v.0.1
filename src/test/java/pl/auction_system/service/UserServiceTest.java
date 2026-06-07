package pl.auction_system.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import pl.auction_system.dto.CreateUserRequest;
import pl.auction_system.exception.*;
import pl.auction_system.mapper.CreateUserRequestMapper;
import pl.auction_system.model.AccType;
import pl.auction_system.model.User;
import pl.auction_system.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreateUserRequestMapper createUserRequestMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Test: add user happy path")
    void shouldAddUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Kac01");
        request.setEmail("kacper@gmail.com");
        request.setFirstName("Kacper");
        request.setLastName("Ciepienka");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        when(userRepository.findUserByUsernameEqualsIgnoreCase(request.getUsername()))
                .thenReturn(Optional.empty());

        when(userRepository.findUserByEmailEqualsIgnoreCase(request.getEmail()))
                .thenReturn(Optional.empty());

        when(createUserRequestMapper.toEntity(request))
                .thenReturn(user);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.addUser(request);

        assertAll(
                () -> assertThat(result.getUsername()).isEqualTo("Kac01"),
                () -> assertThat(result.getEmail()).isEqualTo("kacper@gmail.com"),
                () -> assertThat(result.getFirstName()).isEqualTo("Kacper"),
                () -> assertThat(result.getLastName()).isEqualTo("Ciepienka"),
                () -> assertThat(result.getAccType()).isEqualTo(AccType.USER),
                () -> assertThat(result.getUserNumber()).isNotNull(),
                () -> assertThat(result.getUserNumber()).startsWith("USR-")
        );

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(request.getUsername());
        verify(userRepository, times(1)).findUserByEmailEqualsIgnoreCase(request.getEmail());
        verify(createUserRequestMapper, times(1)).toEntity(request);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Test: add admin happy path")
    void shouldAddAdmin() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Admin1");
        request.setEmail("admin@gmail.com");
        request.setFirstName("Admin");
        request.setLastName("Adminowski");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        when(userRepository.findUserByUsernameEqualsIgnoreCase(request.getUsername()))
                .thenReturn(Optional.empty());

        when(userRepository.findUserByEmailEqualsIgnoreCase(request.getEmail()))
                .thenReturn(Optional.empty());

        when(createUserRequestMapper.toEntity(request))
                .thenReturn(user);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.addAdmin(request);

        assertAll(
                () -> assertThat(result.getUsername()).isEqualTo("Admin1"),
                () -> assertThat(result.getEmail()).isEqualTo("admin@gmail.com"),
                () -> assertThat(result.getAccType()).isEqualTo(AccType.ADMIN),
                () -> assertThat(result.getUserNumber()).isNotNull(),
                () -> assertThat(result.getUserNumber()).startsWith("ADM-")
        );

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Test: add user should throw when username already exists")
    void shouldThrowWhenUsernameAlreadyExistsWhileAddingUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Kac01");
        request.setEmail("kacper@gmail.com");
        request.setFirstName("Kacper");
        request.setLastName("Ciepienka");

        User existingUser = new User();
        existingUser.setUsername("Kac01");

        when(userRepository.findUserByUsernameEqualsIgnoreCase(request.getUsername()))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.addUser(request))
                .isInstanceOf(UserUsernameAlreadyExistException.class);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(request.getUsername());
        verify(userRepository, never()).findUserByEmailEqualsIgnoreCase(anyString());
        verify(createUserRequestMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: add user should throw when email already exists")
    void shouldThrowWhenEmailAlreadyExistsWhileAddingUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Kac01");
        request.setEmail("kacper@gmail.com");
        request.setFirstName("Kacper");
        request.setLastName("Ciepienka");

        User existingUser = new User();
        existingUser.setEmail("kacper@gmail.com");

        when(userRepository.findUserByUsernameEqualsIgnoreCase(request.getUsername()))
                .thenReturn(Optional.empty());

        when(userRepository.findUserByEmailEqualsIgnoreCase(request.getEmail()))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.addUser(request))
                .isInstanceOf(UserEmailAlreadyExistException.class);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(request.getUsername());
        verify(userRepository, times(1)).findUserByEmailEqualsIgnoreCase(request.getEmail());
        verify(createUserRequestMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: delete user happy path")
    void shouldDeleteUserByUsername() {
        String username = "Kac01";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(user));

        userService.deleteUserByUsername(username);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(username);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Test: delete user should throw when user does not exist")
    void shouldThrowWhenDeletingUserThatDoesNotExist() {
        String username = "NoUser123";

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserByUsername(username))
                .isInstanceOf(UserNotFoundByUsernameException.class);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(username);
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Test: change username happy path")
    void shouldChangeUsername() {
        String oldUsername = "Kac01";
        String newUsername = "Kacper99";

        User user = new User();
        user.setUsername(oldUsername);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(oldUsername))
                .thenReturn(Optional.of(user));

        when(userRepository.findUserByUsernameEqualsIgnoreCase(newUsername))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.changeUsername(oldUsername, newUsername);

        assertThat(result.getUsername()).isEqualTo(newUsername);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(oldUsername);
        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(newUsername);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Test: change username should return user when username is the same")
    void shouldReturnUserWhenUsernameIsTheSame() {
        String username = "Kac01";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(user));

        User result = userService.changeUsername(username, username);

        assertThat(result.getUsername()).isEqualTo(username);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(username);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: change username should throw when new username already exists")
    void shouldThrowWhenNewUsernameAlreadyExists() {
        String oldUsername = "Kac01";
        String newUsername = "Lax3";

        User user = new User();
        user.setUsername(oldUsername);

        User existingUser = new User();
        existingUser.setUsername(newUsername);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(oldUsername))
                .thenReturn(Optional.of(user));

        when(userRepository.findUserByUsernameEqualsIgnoreCase(newUsername))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.changeUsername(oldUsername, newUsername))
                .isInstanceOf(UserUsernameAlreadyExistException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: change email happy path")
    void shouldChangeEmail() {
        String username = "Kac01";
        String oldEmail = "old@gmail.com";
        String newEmail = "new@gmail.com";

        User user = new User();
        user.setUsername(username);
        user.setEmail(oldEmail);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(user));

        when(userRepository.findUserByEmailEqualsIgnoreCase(newEmail))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.changeEmail(username, newEmail);

        assertThat(result.getEmail()).isEqualTo(newEmail);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(username);
        verify(userRepository, times(1)).findUserByEmailEqualsIgnoreCase(newEmail);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Test: change email should return user when email is the same")
    void shouldReturnUserWhenEmailIsTheSame() {
        String username = "Kac01";
        String email = "kacper@gmail.com";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(user));

        User result = userService.changeEmail(username, email);

        assertThat(result.getEmail()).isEqualTo(email);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(username);
        verify(userRepository, never()).findUserByEmailEqualsIgnoreCase(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: change email should throw when new email already exists")
    void shouldThrowWhenNewEmailAlreadyExists() {
        String username = "Kac01";
        String oldEmail = "old@gmail.com";
        String newEmail = "taken@gmail.com";

        User user = new User();
        user.setUsername(username);
        user.setEmail(oldEmail);

        User existingUser = new User();
        existingUser.setEmail(newEmail);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(user));

        when(userRepository.findUserByEmailEqualsIgnoreCase(newEmail))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.changeEmail(username, newEmail))
                .isInstanceOf(UserEmailAlreadyExistException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: change account type happy path")
    void shouldChangeAccType() {
        String username = "Kac01";

        User user = new User();
        user.setUsername(username);
        user.setAccType(AccType.USER);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.changeAccTypeByUsername(username, AccType.ADMIN);

        assertThat(result.getAccType()).isEqualTo(AccType.ADMIN);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Test: change account type should return user when account type is the same")
    void shouldReturnUserWhenAccTypeIsTheSame() {
        String username = "Kac01";

        User user = new User();
        user.setUsername(username);
        user.setAccType(AccType.USER);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(user));

        User result = userService.changeAccTypeByUsername(username, AccType.USER);

        assertThat(result.getAccType()).isEqualTo(AccType.USER);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: find user by username happy path")
    void shouldFindUserByUsername() {
        String username = "Kac01";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.of(user));

        User result = userService.findUserByUsernameEqualsIgnoreCase(username);

        assertThat(result.getUsername()).isEqualTo(username);

        verify(userRepository, times(1)).findUserByUsernameEqualsIgnoreCase(username);
    }

    @Test
    @DisplayName("Test: find user by username should throw when user does not exist")
    void shouldThrowWhenUserByUsernameDoesNotExist() {
        String username = "NoUser123";

        when(userRepository.findUserByUsernameEqualsIgnoreCase(username))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByUsernameEqualsIgnoreCase(username))
                .isInstanceOf(UserNotFoundByUsernameException.class);
    }

    @Test
    @DisplayName("Test: find user by email happy path")
    void shouldFindUserByEmail() {
        String email = "kacper@gmail.com";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findUserByEmailEqualsIgnoreCase(email))
                .thenReturn(Optional.of(user));

        User result = userService.findUserByEmailEqualsIgnoreCase(email);

        assertThat(result.getEmail()).isEqualTo(email);

        verify(userRepository, times(1)).findUserByEmailEqualsIgnoreCase(email);
    }

    @Test
    @DisplayName("Test: find user by email should throw when user does not exist")
    void shouldThrowWhenUserByEmailDoesNotExist() {
        String email = "notfound@gmail.com";

        when(userRepository.findUserByEmailEqualsIgnoreCase(email))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByEmailEqualsIgnoreCase(email))
                .isInstanceOf(UserNotFoundByEmailException.class);
    }

    @Test
    @DisplayName("Test: find user by user number happy path")
    void shouldFindUserByUserNumber() {
        String userNumber = "USR-TEST-123";

        User user = new User();
        user.setUserNumber(userNumber);

        when(userRepository.findUserByUserNumberEqualsIgnoreCase(userNumber))
                .thenReturn(Optional.of(user));

        User result = userService.findUserByUserNumberEqualsIgnoreCase(userNumber);

        assertThat(result.getUserNumber()).isEqualTo(userNumber);

        verify(userRepository, times(1)).findUserByUserNumberEqualsIgnoreCase(userNumber);
    }

    @Test
    @DisplayName("Test: find user by user number should throw when user does not exist")
    void shouldThrowWhenUserByUserNumberDoesNotExist() {
        String userNumber = "NO-USER-NUMBER";

        when(userRepository.findUserByUserNumberEqualsIgnoreCase(userNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByUserNumberEqualsIgnoreCase(userNumber))
                .isInstanceOf(UserNotFoundByUserNumberException.class);
    }

    @Test
    @DisplayName("Test: find all users")
    void shouldFindAllUsers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("username").ascending());

        User user1 = new User();
        user1.setUsername("Kac01");

        User user2 = new User();
        user2.setUsername("Michu2");

        Page<User> page = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.findAllUsers(pageable);

        assertAll(
                () -> assertThat(result.getContent()).hasSize(2),
                () -> assertThat(result.getContent().getFirst().getUsername()).isEqualTo("Kac01"),
                () -> assertThat(result.getTotalElements()).isEqualTo(2)
        );

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Test: find all users by account type")
    void shouldFindAllByAccType() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setUsername("Kac01");
        user.setAccType(AccType.USER);

        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAllByAccType(AccType.USER, pageable))
                .thenReturn(page);

        Page<User> result = userService.findAllByAccType(AccType.USER, pageable);

        assertAll(
                () -> assertThat(result.getContent()).hasSize(1),
                () -> assertThat(result.getContent().getFirst().getAccType()).isEqualTo(AccType.USER)
        );

        verify(userRepository, times(1)).findAllByAccType(AccType.USER, pageable);
    }

    @Test
    @DisplayName("Test: find all users by first name")
    void shouldFindAllByFirstName() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "Kacper";

        User user = new User();
        user.setUsername("Kac01");
        user.setFirstName(firstName);

        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAllByFirstNameEqualsIgnoreCase(firstName, pageable))
                .thenReturn(page);

        Page<User> result = userService.findAllByFirstNameEqualsIgnoreCase(firstName, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getFirstName()).isEqualTo(firstName);

        verify(userRepository, times(1))
                .findAllByFirstNameEqualsIgnoreCase(firstName, pageable);
    }

    @Test
    @DisplayName("Test: find all users by last name")
    void shouldFindAllByLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Ciepienka";

        User user = new User();
        user.setUsername("Kac01");
        user.setLastName(lastName);

        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAllByLastNameEqualsIgnoreCase(lastName, pageable))
                .thenReturn(page);

        Page<User> result = userService.findAllByLastNameEqualsIgnoreCase(lastName, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getLastName()).isEqualTo(lastName);

        verify(userRepository, times(1))
                .findAllByLastNameEqualsIgnoreCase(lastName, pageable);
    }
}