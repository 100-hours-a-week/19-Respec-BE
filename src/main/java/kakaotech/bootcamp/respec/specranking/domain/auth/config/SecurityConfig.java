package kakaotech.bootcamp.respec.specranking.domain.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.CustomSuccessHandler;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.JWTFilter;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.JWTUtil;
import kakaotech.bootcamp.respec.specranking.domain.auth.repository.CustomAuthorizationRequestRepository;
import kakaotech.bootcamp.respec.specranking.domain.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile("!no-auth")
public class SecurityConfig {

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    private final CustomAuthorizationRequestRepository authorizationRequestRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    private static final List<String> PUBLIC_GET_URLS = List.of(
            "/api/health",
            "/api/users/{userId}",
            "/api/specs",
            "/api/specs/{specId}"
    );

    private static final List<String> PUBLIC_POST_URLS = List.of(
            "/api/auth/token/refresh",
            "/api/users"
    );

    private static final List<String> PUBLIC_ALL_URLS = List.of(
            "/oauth2/**",
            "/login/oauth2/**",
            "/ws/chat/**"
    );

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(createCorsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2Configurer -> oauth2Configurer
                        .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.authorizationRequestRepository(authorizationRequestRepository))
                        .userInfoEndpoint(userInfoEndpointConfigurer ->
                                userInfoEndpointConfigurer.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler))
                .authorizeHttpRequests(authRequestConfigurer -> authRequestConfigurer
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_URLS.toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST_URLS.toArray(new String[0])).permitAll()
                        .requestMatchers(PUBLIC_ALL_URLS.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sessionConfigurer ->
                        sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    private CorsConfigurationSource createCorsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(List.of(frontendBaseUrl));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(List.of("*"));
                config.setMaxAge(3600L);
                config.setExposedHeaders(List.of("Set-Cookie", "Authorization"));

                return config;
            }
        };
    }
}

