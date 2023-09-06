package com.quadrangle.projects.blog;

import com.quadrangle.projects.blog.entity.auth.ERole;
import com.quadrangle.projects.blog.entity.auth.Role;
import com.quadrangle.projects.blog.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (!roleRepository.existsByName(ERole.ROLE_ADMIN)) {
				roleRepository.save(new Role(ERole.ROLE_ADMIN));
			}
			if (!roleRepository.existsByName(ERole.ROLE_USER)) {
				roleRepository.save(new Role(ERole.ROLE_USER));
			}
			if (!roleRepository.existsByName(ERole.ROLE_MANAGER)) {
				roleRepository.save(new Role(ERole.ROLE_MANAGER));
			}
		};
	}
}
