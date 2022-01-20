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

import static fr.kmcl.unisignBACK.constant.UserImplConstant.*;
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
            log.error(NO_USER_FOUND_WITH_USERNAME);
            throw new UsernameNotFoundException(NO_USER_FOUND_WITH_USERNAME);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepo.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(USER_FOUND_IN_DB, username);
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
        log.info(SAVING_USER_TO_DB, user.getUsername());
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
        return user;
    }

    private String getTemporaryProfileUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString();
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
        if (StringUtils.isNotBlank(currentUsername)) {
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
     * @param username String: user's username
     * @return AppUser: user fetched from DB
     */
    @Override
    public AppUser findUserByEmail(String username) {
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
        return userRepo.findAll();
    }
}
