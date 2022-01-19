package fr.kmcl.unisignBACK.service.impl;

import fr.kmcl.unisignBACK.model.AppRole;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.repo.RoleRepo;
import fr.kmcl.unisignBACK.repo.UserRepo;
import fr.kmcl.unisignBACK.security.UserPrincipal;
import fr.kmcl.unisignBACK.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

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

    /**
     * Save Role in the database
     * @param role AppRole: role to save in DB
     * @return saved role
     */
    @Override
    public AppRole saveRole(AppRole role) {
        log.info("Saving role {} to the database", role.getName());
        return roleRepo.save(role);
    }

    /**
     * Save user and role connexion in the database
     * @param username String: user's username
     * @param roleName String: role name
     */
    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to the user {} in the database", roleName, username);
        AppUser user = userRepo.findAppUserByUsername(username);
        AppRole role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    /**
     * Fetch a user from the database by his username
     * @param username String: user's username
     * @return AppUser: user fetched from DB
     */
    @Override
    public AppUser getUser(String username) {
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
