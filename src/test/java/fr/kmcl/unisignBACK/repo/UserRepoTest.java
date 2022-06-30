package fr.kmcl.unisignBACK.repo;

import fr.kmcl.unisignBACK.model.AppUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Date;

import static fr.kmcl.unisignBACK.security.Role.ROLE_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepoTest {

    @Autowired
    private UserRepo underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindAppUserByUsername() {
        // given
        String username = "jDoe";

        AppUser user = new AppUser();

        user.setUserId("userId465789");
        user.setFirstName("John");
        user.setLastName("DOE");
        user.setUsername(username);
        user.setEmail("jdoe@kmcl.fr");
        user.setPassword("GtReD-s6Redf-ezy6tG-gartyd");
        user.setJoinDate(new Date());
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl("https://robohash.org/" + username);
        underTest.save(user);

        // when
        AppUser expected = underTest.findAppUserByUsername(username);

        // then
        assertThat(expected).isEqualTo(user);
    }

    @Test
    void itShouldFindAppUserByEmail() {
        // given
        String username = "jDoe";
        String email = "jdoe@kmcl.fr";

        AppUser user = new AppUser();

        user.setUserId("userId465789");
        user.setFirstName("John");
        user.setLastName("DOE");
        user.setUsername(username);
        user.setEmail("jdoe@kmcl.fr");
        user.setPassword("GtReD-s6Redf-ezy6tG-gartyd");
        user.setJoinDate(new Date());
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl("https://robohash.org/" + username);
        underTest.save(user);

        // when
        AppUser expected = underTest.findAppUserByEmail(email);

        // then
        assertThat(expected).isEqualTo(user);
    }
}