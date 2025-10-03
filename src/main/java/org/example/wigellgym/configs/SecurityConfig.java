package org.example.wigellgym.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/wigellgym/workouts").hasRole("USER")
                        .requestMatchers("/api/wigellgym/instructors").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/wigellgym/addinstructor").hasRole("ADMIN")
                        .requestMatchers("/api/wigellgym/addworkout").hasRole("ADMIN")
                        .requestMatchers("/api/wigellgym/updateworkout").hasRole("ADMIN")
                        .requestMatchers("/api/wigellgym/remworkout/**").hasRole("ADMIN")
                        .requestMatchers("/api/wigellgym/bookworkout").hasRole("USER")
                        .requestMatchers("/api/wigellgym/cancelworkout").hasRole("USER")
                        .requestMatchers("/api/wigellgym/mybookings").hasRole("USER")
                        .requestMatchers("/api/wigellgym/listcanceled").hasRole("ADMIN")
                        .requestMatchers("/api/wigellgym/listupcoming").hasRole("ADMIN")
                        .requestMatchers("/api/wigellgym/listpast").hasRole("ADMIN")
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .headers(h -> h
                .frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 = User
                .withUsername("sven1")
                .password("{noop}sven1")
                .roles("USER")
                .build();

        UserDetails user2 = User
                .withUsername("sven2")
                .password("{noop}sven2")
                .roles("USER")
                .build();

        UserDetails user3 = User
                .withUsername("sven3")
                .password("{noop}sven3")
                .roles("USER")
                .build();

        UserDetails admin = User
                .withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, user2, user3, admin);
    }
}
