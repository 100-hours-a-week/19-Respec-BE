package kakaotech.bootcamp.respec.specranking.domain.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.JWTFilter;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.JWTUtil;
import kakaotech.bootcamp.respec.specranking.domain.auth.jwt.CustomSuccessHandler;
import kakaotech.bootcamp.respec.specranking.domain.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("✅ [SecurityConfig] 필터체인 등록 완료");
        // cors 설정
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration config = new CorsConfiguration();

                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L);

                        config.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                        return config;
                    }
                }));


        // csrf disable
        http
                .csrf((auth) -> auth.disable());

        // form 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        // HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        // JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                        .failureHandler(new CustomOAuth2FailureHandler())
                );

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                "/",
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/**")
                        .permitAll()
                        .anyRequest().authenticated());

        // 세션 설정 : stateless
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 인증 실패 시 302 대신 401 반환
        http
                .exceptionHandling(exception -> exception
                        .defaultAuthenticationEntryPointFor(
                                customAuthenticationEntryPoint,
                                new AntPathRequestMatcher("/api/**")
                        )
                );

        return http.build();
    }
}
