package fr.kmcl.unisignBACK;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

import static fr.kmcl.unisignBACK.constant.FileConstant.USER_FOLDER;

@SpringBootApplication
@Slf4j
public class UnisignBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnisignBackApplication.class, args);
		boolean wasSuccessful = new File(USER_FOLDER).mkdirs();
		if (!wasSuccessful) {
			log.error("System failed to create user folders for profile image.");
		}
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
