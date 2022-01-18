package fr.kmcl.unisignBACK;

import fr.kmcl.unisignBACK.model.AppRole;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.security.AppUserRole;
import fr.kmcl.unisignBACK.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static fr.kmcl.unisignBACK.security.AppUserRole.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Stream;

@SpringBootApplication
public class UnisignBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnisignBackApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner runner(UserService userService) {
		return args -> {
			Stream.of(AppUserRole.values())
					.forEach(role -> userService.saveRole(new AppRole(null, role.name())));

			userService.saveUser(
					new AppUser(
							null, "Damien", "PUAUD", "dPuaud", "dpuaud@kmcl.fr",
							"Bonjour.1234", null, null, null, null,
							new ArrayList<>(), null, true, true
					));
			userService.saveUser(
					new AppUser(
							null, "Christophe", "LUCAS", "cLucas", "clucas@kmcl.fr",
							"Bonjour.1234", null, null, null, null,
							new ArrayList<>(), null, true, true
					));
			userService.saveUser(
					new AppUser(
							null, "Claudia", "MANCO", "cManco", "cmanco@kmcl.fr",
							"Bonjour.1234", null, null, null, null,
							new ArrayList<>(), null, true, true
					));
			userService.saveUser(
					new AppUser(
							null, "Julie", "LEVEQUE", "jLeveque", "jLeveque@kmcl.fr",
							"Bonjour.1234", null, null, null, null,
							new ArrayList<>(), null, true, true
					));

			userService.addRoleToUser("dPuaud", SUPER_ADMIN.name());
			userService.addRoleToUser("dPuaud", ADMIN.name());
			userService.addRoleToUser("dPuaud", MANAGER.name());
			userService.addRoleToUser("dPuaud", USER.name());
			userService.addRoleToUser("cLucas", ADMIN.name());
			userService.addRoleToUser("cLucas", MANAGER.name());
			userService.addRoleToUser("cLucas", USER.name());
			userService.addRoleToUser("cManco", MANAGER.name());
			userService.addRoleToUser("cManco", USER.name());
			userService.addRoleToUser("jLeveque", USER.name());
		};
	}

}
