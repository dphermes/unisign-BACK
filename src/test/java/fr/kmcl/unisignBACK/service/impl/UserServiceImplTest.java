package fr.kmcl.unisignBACK.service.impl;

import com.google.common.base.Verify;
import fr.kmcl.unisignBACK.exception.model.EmailExistException;
import fr.kmcl.unisignBACK.exception.model.NotImageFileException;
import fr.kmcl.unisignBACK.exception.model.UserNotFoundException;
import fr.kmcl.unisignBACK.exception.model.UsernameExistException;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.repo.UserRepo;
import fr.kmcl.unisignBACK.service.EmailService;
import fr.kmcl.unisignBACK.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static fr.kmcl.unisignBACK.security.Role.ROLE_USER;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private LoginAttemptService loginAttemptService;
    @Mock
    private EmailService emailService;
    private UserServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserServiceImpl(userRepo, new BCryptPasswordEncoder(), loginAttemptService, emailService);
    }

    @Test
    @Disabled
    void saveUser() {
    }

    @Test
    @Disabled
    void registerUser() {
    }

    @Test
    void canAddNewUser() throws UserNotFoundException, EmailExistException, NotImageFileException, IOException, UsernameExistException {
        // given
        String firstName = "John";
        String lastName = "Doe";
        String username = "jDoe";
        String email = "jdoe@kmcl.fr";
        boolean iActive = true;
        boolean isNotLocked = true;
        String role = ROLE_USER.name();
        // when
        underTest.addNewUser(firstName, lastName, username, email, role, isNotLocked, iActive, null);
        // then
        ArgumentCaptor<AppUser> userArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);
//        Verify(userRepo).save(userArgumentCaptor.capture());
    }

    @Test
    @Disabled
    void updateUser() {
    }

    @Test
    @Disabled
    void deleteUser() {
    }

    @Test
    @Disabled
    void findUserByUsername() {
    }

    @Test
    @Disabled
    void findUserByEmail() {
    }

    @Test
    void canGetAllUsers() {
        // when
        underTest.getUsers();
        // then
        verify(userRepo).findAll();
    }

    @Test
    @Disabled
    void resetPassword() {
    }

    @Test
    @Disabled
    void updateProfileImage() {
    }

    @Test
    @Disabled
    void loadUserByUsername() {
    }
}