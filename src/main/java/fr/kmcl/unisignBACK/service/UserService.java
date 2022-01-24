package fr.kmcl.unisignBACK.service;

import fr.kmcl.unisignBACK.exception.model.EmailExistException;
import fr.kmcl.unisignBACK.exception.model.EmailNotFoundException;
import fr.kmcl.unisignBACK.exception.model.UserNotFoundException;
import fr.kmcl.unisignBACK.exception.model.UsernameExistException;
import fr.kmcl.unisignBACK.model.AppUser;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface UserService {
    AppUser saveUser(AppUser user);
    AppUser registerUser(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException;
    AppUser addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;
    AppUser updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;
    void deleteUser(long id);
    AppUser findUserByUsername(String username);
    AppUser findUserByEmail(String email);
    /* Assuming there's not a lot of users using this application
     Otherwise return a page instead of all users */
    List<AppUser> getUsers();
    void resetPassword(String email) throws EmailNotFoundException, MessagingException;
    AppUser updateProfileImage(String username, MultipartFile newProfileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;
}
