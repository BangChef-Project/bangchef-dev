package com.bangchef.recipe_platform.security;
import com.bangchef.recipe_platform.security.token.repository.RefreshTokenRepository;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
                          RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((auth) -> auth.disable());
        http
                .formLogin((auth) -> auth.disable()); //
        http
                .httpBasic((auth) -> auth.disable());
        http
                .authorizeHttpRequests((auth) -> auth

                        .requestMatchers("/users/role-update/status/**").hasAnyAuthority("USER", "CHEF")
                        .requestMatchers("/reports/**").hasAnyAuthority("USER", "CHEF")
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        .requestMatchers(
                                "/",
                                "/users/logout",
                                "/users/login",
                                "/users/join",
                                "/users/verify/**",
                                "/users/reset-password",
                                "/users/info",
                                "/users/update",
                                "/users/name",
                                "/users/subscribe-update",
                                "/users/subscribe-cancel",
                                "/recipes",
                                "/recipes/**",
                                "/reports/**",
                                "/comments",
                                "/comments/**",
                                "/ratings",
                                "/ratings/**",
                                "/favorites",
                                "/favorites/**",
                                "/h2-console/**" //서브경로 포함
                        ).permitAll()

                        .anyRequest().authenticated());
        http
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // 프레임 옵션 비활성화
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,
                                refreshTokenRepository, userRepository),
                        UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new JWTFilter(userRepository, jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository),
                        LogoutFilter.class);
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}