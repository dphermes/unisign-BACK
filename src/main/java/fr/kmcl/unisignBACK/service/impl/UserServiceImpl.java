package fr.kmcl.unisignBACK.service.impl;

import fr.kmcl.unisignBACK.exception.model.EmailExistException;
import fr.kmcl.unisignBACK.exception.model.UserNotFoundException;
import fr.kmcl.unisignBACK.exception.model.UsernameExistException;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.repo.UserRepo;
import fr.kmcl.unisignBACK.security.UserPrincipal;
import fr.kmcl.unisignBACK.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

import static fr.kmcl.unisignBACK.security.Role.ROLE_USER;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@Qualifier("UserServiceDetails")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

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
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepo.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info("User {} found in the database", username);
            return userPrincipal;
        }
    }

    /**
     * Save User in the database
     * @param user AppUser: user to save in DB
     * @return saved user
     */
    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving user {} to the database", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public AppUser registerUser(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailExistException, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
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
        user.setProfileImageUrl(getTemporaryProfileUrl());
        userRepo.save(user);
        log.info("New user password: " + password);
        return null;
    }

    private String getTemporaryProfileUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
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
        if (StringUtils.isNotBlank(currentUsername)) {
            AppUser currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException("No user found with username " + currentUsername);
            }
            AppUser userByNewUsername = findUserByUsername(newUsername);
            if (userByNewUsername != null && currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException("This username already exists");
            }
            AppUser userByNewEmail = findUserByEmail(newEmail);
            if (userByNewEmail != null && currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException("This email already exists");
            }
            return currentUser;
        } else {
            AppUser userByUsername = findUserByUsername(newUsername);
            if (userByUsername != null) {
                throw new UsernameExistException("This username already exists");
            }
            AppUser userByEmail = findUserByEmail(newEmail);
            if (userByEmail != null) {
                throw new EmailExistException("This email already exists");
            }
            return null;
        }
    }

    /**
     * Fetch a user from the database by his username
     * @param username String: user's username
     * @return AppUser: user fetched from DB
     */
    @Override
    public AppUser findUserByUsername(String username) {
        log.info("Fetching user {}", username);
        return userRepo.findAppUserByUsername(username);
    }

    /**
     * Fetch a user from the database by his username
     * @param username String: user's username
     * @return AppUser: user fetched from DB
     */
    @Override
    public AppUser findUserByEmail(String username) {
        log.info("Fetching user {}", username);
        return userRepo.findAppUserByUsername(username);
    }

    /**
     * Fetch all users from the database
     * @return List<AppUser>: all users
     */
    @Override
    public List<AppUser> getUsers() {
        /* Assuming there's not a lot of users using this application
        ** Otherwise return a page instead of all users */
        log.info("Fetching all users");
        return userRepo.findAll();
    }
}
