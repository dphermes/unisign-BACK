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
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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
import java.io.File;
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

    /**
     * Register User in the database
     * @param firstName String: user's first name
     * @param lastName String: user's last name
     * @param username String: user's username
     * @param email String: user's email
     * @return AppUser: newly registered user
     * @throws UserNotFoundException: UserNotFoundException exception can be thrown
     * @throws EmailExistException: EmailExistException exception can be thrown
     * @throws UsernameExistException: UsernameExistException exception can be thrown
     * @throws MessagingException: MessagingException exception can be thrown
     */
    @Override
    public AppUser registerUser(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        AppUser user = new AppUser();
        user.setUserId(generateUserId());
        String password = generatePassword();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
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
     * Add new User in the database
     * @param firstName String: user's first name
     * @param lastName String: user's last name
     * @param username String: user's username
     * @param email String: user's email
     * @param role String: user's role
     * @param isNonLocked boolean: if user is not locked or is
     * @param isActive boolean: if user is active or not
     * @param profileImage MultipartFile: profile image file
     * @return AppUser: newly added user
     * @throws UserNotFoundException: UserNotFoundException exception can be thrown
     * @throws EmailExistException: EmailExistException exception can be thrown
     * @throws UsernameExistException: UsernameExistException exception can be thrown
     * @throws IOException: IOException exception can be thrown
     */
    @Override
    public AppUser addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
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
        user.setNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileUrl(username));
        userRepo.save(user);
        saveProfileImage(user, profileImage);
        return user;
    }

    /**
     * Update User in the database
     * @param currentUsername String: user's current username
     * @param newFirstName String: user's first name to update
     * @param newLastName String: user's last name to update
     * @param newUsername String: user's username to update
     * @param newEmail String: user's email to update
     * @param role String: user's role to update
     * @param isNotLocked boolean: if user is not locked (true) or is locked (false)
     * @param isActive boolean: if user is active or not
     * @param profileImage MultipartFile: profile image file to update
     * @return AppUser: updated user
     * @throws UserNotFoundException: UserNotFoundException exception can be thrown
     * @throws EmailExistException: EmailExistException exception can be thrown
     * @throws UsernameExistException: UsernameExistException exception can be thrown
     * @throws IOException: IOException exception can be thrown
     */
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

    /**
     * Delete a user from the database
     * @param username String: user's username
     */
    @Override
    public void deleteUser(String username) throws IOException {
        AppUser user = userRepo.findAppUserByUsername(username);
        Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepo.deleteById(user.getId());
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

    /**
     * Reset a user password
     * @param email String: user's email to find him/her in the DB
     * @throws EmailNotFoundException: EmailExistException exception can be thrown
     * @throws MessagingException: MessagingException exception can be thrown
     */
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

    /**
     * Update a user's profile image
     * @param username String: user's username
     * @param newProfileImage MultipartFile: new profile image file
     * @return AppUser: updated user
     * @throws UserNotFoundException: UserNotFoundException exception can be thrown
     * @throws EmailExistException: EmailExistException exception can be thrown
     * @throws UsernameExistException: UsernameExistException exception can be thrown
     * @throws IOException: IOException exception can be thrown
     */
    @Override
    public AppUser updateProfileImage(String username, MultipartFile newProfileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        AppUser user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, newProfileImage);
        return user;
    }

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
     * Save a user's profile image
     * @param user AppUser: user to add profile image to
     * @param profileImage MultipartFile: profile image file
     * @throws IOException: IOException exception can be thrown
     */
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

    /**
     * Set a profile image url path
     * @param username String: user's username
     * @return String: profile image url path
     */
    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }

    /**
     * Get correct role name from enum
     * @param role String: role
     * @return Role
     */
    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    /**
     * Generate a random profile picture image
     * @param username String: user's username
     * @return String: profile image url path
     */
    private String getTemporaryProfileUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    /**
     * Generates a random user id
     * @return String: generated user id
     */
    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    /**
     * Generates a random alphanumeric password
     * @return String: not encoded but random password
     */
    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    /**
     * Encodes a password
     * @param password String: a not encoded password
     * @return String: encoded password
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Validates username and Email to check if it already exists in the database
     * @param currentUsername String: user's current username
     * @param newUsername String: user's new username
     * @param newEmail String: user's new email
     * @return AppUser: validated user
     * @throws UserNotFoundException: UserNotFoundException exception can be thrown
     * @throws UsernameExistException: UsernameExistException exception can be thrown
     * @throws EmailExistException: EmailExistException exception can be thrown
     */
    private AppUser validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        AppUser userByNewUsername = findUserByUsername(newUsername);
        AppUser userByNewEmail = findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUsername)) {
            AppUser currentUser = findUserByUsername(currentUsername);
            if(currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_WITH_USERNAME + currentUsername);
            }
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if(userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
