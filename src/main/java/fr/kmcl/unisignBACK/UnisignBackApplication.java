package fr.kmcl.unisignBACK;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

import static fr.kmcl.unisignBACK.constant.FileConstant.USER_FOLDER;

@SpringBootApplication
public class UnisignBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnisignBackApplication.class, args);
		new File(USER_FOLDER).mkdirs();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
