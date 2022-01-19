package fr.kmcl.unisignBACK.repo;

import fr.kmcl.unisignBACK.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface UserRepo extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByUsername(String username);
    AppUser findAppUserByEmail(String email);
}
