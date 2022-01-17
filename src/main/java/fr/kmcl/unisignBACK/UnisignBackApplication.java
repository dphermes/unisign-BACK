package fr.kmcl.unisignBACK;

import fr.kmcl.unisignBACK.model.AppRole;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class UnisignBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnisignBackApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserService userService) {
		return args -> {
			userService.saveRole(new AppRole(null, "ROLE_USER"));
			userService.saveRole(new AppRole(null, "ROLE_MANAGER"));
			userService.saveRole(new AppRole(null, "ROLE_ADMIN"));
			userService.saveRole(new AppRole(null, "ROLE_SUPER_ADMIN"));

			userService.saveUser(new AppUser(null, "Damien", "PUAUD", "dPuaud", "dpuaud@kmcl.fr", "Bonjour.1234", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Christophe", "LUCAS", "cLucas", "clucas@kmcl.fr", "Bonjour.1234", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Claudia", "MANCO", "cManco", "cmanco@kmcl.fr", "Bonjour.1234", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Julie", "LEVEQUE", "jLeveque", "jleveque@kmcl.fr", "Bonjour.1234", new ArrayList<>()));

			userService.addRoleToUser("dPuaud", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("dPuaud", "ROLE_ADMIN");
			userService.addRoleToUser("dPuaud", "ROLE_MANAGER");
			userService.addRoleToUser("dPuaud", "ROLE_USER");
			userService.addRoleToUser("cLucas", "ROLE_ADMIN");
			userService.addRoleToUser("cLucas", "ROLE_MANAGER");
			userService.addRoleToUser("cLucas", "ROLE_USER");
			userService.addRoleToUser("cManco", "ROLE_MANAGER");
			userService.addRoleToUser("cManco", "ROLE_USER");
			userService.addRoleToUser("jLeveque", "ROLE_USER");
		};
	}

}
