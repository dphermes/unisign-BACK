package fr.kmcl.unisignBACK.service;

import fr.kmcl.unisignBACK.exception.model.EmailExistException;
import fr.kmcl.unisignBACK.exception.model.UserNotFoundException;
import fr.kmcl.unisignBACK.exception.model.UsernameExistException;
import fr.kmcl.unisignBACK.model.AppUser;

import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface UserService {
    AppUser saveUser(AppUser user);
    AppUser registerUser(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailExistException, UsernameExistException;
    AppUser findUserByUsername(String username);
    AppUser findUserByEmail(String email);
    /* Assuming there's not a lot of users using this application
     Otherwise return a page instead of all users */
    List<AppUser> getUsers();
}
