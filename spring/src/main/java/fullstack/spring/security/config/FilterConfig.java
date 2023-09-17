package fullstack.spring.security.config;

import fullstack.spring.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class FilterConfig {
    private CorsConfigurationSource corsConfigurationSource;
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // login filter등 추가해야함
        return http
                .httpBasic((httpBasicConfigurer) -> httpBasicConfigurer.disable()) // jwt를 사용하기 위해 베이직한 인증 방법은 제거한다.
                .formLogin((form) -> form.disable()) // 내가 직접 만들거라서 사용안함
                .csrf(AbstractHttpConfigurer::disable) // 서버에 인증 정보를 포함하지 않는다면 csrf protection은 사용하지 않아도 된다고 한다. (난 jwt 사용)
                .authorizeHttpRequests(
                        (authorize) ->
                                authorize.requestMatchers("/api/**").permitAll()
                                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                        .requestMatchers(request -> CorsUtils.isPreFlightRequest(request)).permitAll()
                        )
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session을 stateless하게 만들기
                .headers((headers)->headers.disable()) // iframe 사용 시 오류 발생(클릭 재킹 차단)을 막기 위해 사용한다는데 나는 굳이 필요없긴 할 듯
                .addFilterAfter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager())
                .cors((cors) -> cors.configurationSource(configuration())) // CorsConfigurationSource로 세팅한 값으로 설정됨
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);

        return new ProviderManager(provider);
    }

    @Bean
    public UrlBasedCorsConfigurationSource configuration () {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setExposedHeaders(List.of("*:"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
