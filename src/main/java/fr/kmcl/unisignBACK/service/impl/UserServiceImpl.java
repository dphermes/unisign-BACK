package fr.kmcl.unisignBACK.service.impl;

import fr.kmcl.unisignBACK.exception.model.EmailExistException;
import fr.kmcl.unisignBACK.exception.model.EmailNotFoundException;
import fr.kmcl.unisignBACK.exception.model.UserNotFoundException;
import fr.kmcl.unisignBACK.exception.model.UsernameExistException;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.repo.UserRepo;
import fr.kmcl.unisignBACK.security.Role;
import fr.kmcl.unisignBACK.security.UserPrincipal;
import fr.kmcl.unisignBACK.service.EmailService;
import fr.kmcl.unisignBACK.service.LoginAttemptService;
import fr.kmcl.unisignBACK.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static fr.kmcl.unisignBACK.constant.FileConstant.*;
import static fr.kmcl.unisignBACK.constant.UserImplConstant.*;
import static fr.kmcl.unisignBACK.security.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;

    /**
     * Override loadUserByUsername method from UserDetailsService for Spring Security
     * @param username String: user's username
     * @return UserDetails: user's details from Spring Security
     * @throws UsernameNotFoundException: Exception thrown if username not found in DB
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepo.findAppUserByUsername(username);
        if(user == null) {
            log.error(NO_USER_FOUND_WITH_USERNAME);
            throw new UsernameNotFoundException(NO_USER_FOUND_WITH_USERNAME);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepo.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(USER_FOUND_IN_DB, username);
            return userPrincipal;
        }
    }

    /**
     * Checks if user max attempts exceeded and lock or unlock user's account accordingly
     * @param user AppUser: user logging in
     */
    private void validateLoginAttempt(AppUser user) {
        if (user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    /**
     * Save User in the database
     * @param user AppUser: user to save in DB
     * @return saved user
     */
    @Override
    public AppUser saveUser(AppUser user) {
        log.info(SAVING_USER_TO_DB, user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public AppUser registerUser(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        AppUser user = new AppUser();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileUrl(username));
        userRepo.save(user);
        emailService.sendNewPasswordEmail(firstName, password, email);
        return user;
    }

    /**
     * Fetch a user from the database by his username
     * @param username String: user's username
     * @return AppUser: user fetched from DB
     */
    @Override
    public AppUser findUserByUsername(String username) {
        return userRepo.findAppUserByUsername(username);
    }

    /**
     * Fetch a user from the database by his username
     * @param email String: user's username
     * @return AppUser: user fetched from DB
     */
    @Override
    public AppUser findUserByEmail(String email) {
        return userRepo.findAppUserByEmail(email);
    }

    /**
     * Fetch all users from the database
     * @return List<AppUser>: all users
     */
    @Override
    public List<AppUser> getUsers() {
        /* Assuming there's not a lot of users using this application
        ** Otherwise return a page instead of all users */
        return userRepo.findAll();
    }

    @Override
    public AppUser addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        AppUser user = new AppUser();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setActive(isActive);
        user.setNotLocked(isNotLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileUrl(username));
        userRepo.save(user);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public AppUser updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        AppUser currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepo.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public void deleteUser(long id) {
        userRepo.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        AppUser user = userRepo.findAppUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_WITH_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepo.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }

    @Override
    public AppUser updateProfileImage(String username, MultipartFile newProfileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        AppUser user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, newProfileImage);
        return user;
    }

    private void saveProfileImage(AppUser user, MultipartFile profileImage) throws IOException {
        String username = user.getUsername();
        if (profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER + username).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED);
            }
            Files.deleteIfExists(Paths.get(userFolder + username + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(username + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(username));
            userRepo.save(user);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String getTemporaryProfileUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private AppUser validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        AppUser userByUsername = findUserByUsername(newUsername);
        AppUser userByEmail = findUserByEmail(newEmail);
        if (isNotBlank(currentUsername)) {
            AppUser currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_WITH_USERNAME + currentUsername);
            }
            if (userByUsername != null && currentUser.getId().equals(userByUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByEmail != null && currentUser.getId().equals(userByEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
