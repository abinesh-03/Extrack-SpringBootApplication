package ExpenseTracker.ProjectExpenseTracker.config;


import ExpenseTracker.ProjectExpenseTracker.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {


@Bean
PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

@Bean
UserDetailsService userDetailsService(UserRepository users) {
	return username -> users.findByEmail(username)
		.map(u -> org.springframework.security.core.userdetails.User
			.withUsername(u.getEmail())
			.password(u.getPassword())
			.disabled(!u.isEnabled())
			.roles(u.getRole().name())
			.build())
		.orElseThrow(() -> new UsernameNotFoundException("User not found"));
}


@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
http
.csrf(csrf -> csrf.disable())
.authorizeHttpRequests(auth -> auth
.requestMatchers("/css/**","/register","/verify","/reset/**","/ws/**").permitAll()
.anyRequest().authenticated())
.formLogin(form -> form.loginPage("/login").permitAll().defaultSuccessUrl("/dashboard", true))
.logout(Customizer.withDefaults());
return http.build();
}
}