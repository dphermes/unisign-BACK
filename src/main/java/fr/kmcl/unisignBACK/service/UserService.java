package fr.kmcl.unisignBACK.service;

import fr.kmcl.unisignBACK.exception.model.*;
import fr.kmcl.unisignBACK.model.Agency;
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
    AppUser addNewUser(String firstName, String lastName, String username, String email, String agencyLabel, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotImageFileException;
    AppUser updateUser(AppUser user) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotImageFileException;
    void deleteUser(String username) throws IOException;
    AppUser findUserByUsername(String username);
    AppUser findUserByEmail(String email);
    /* Assuming there's not a lot of users using this application
     Otherwise return a page instead of all users */
    List<AppUser> getUsers();
    void resetPassword(String email) throws EmailNotFoundException, MessagingException;
    AppUser updateProfileImage(String username, MultipartFile newProfileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotImageFileException;
}
