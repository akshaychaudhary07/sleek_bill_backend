package com.example.rolebasedauth.security;

//import com.example.rolebasedauth.security.jwt.JwtAuthenticationEntryPoint;
import com.example.rolebasedauth.security.jwt.JwtRequestFilter;
import com.example.rolebasedauth.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

//    @Autowired
//    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private com.example.rolebasedauth.security.CustomAccessDeniedHandler accessDeniedHandler;

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance(); // For demo purposes only; use a stronger encoder in production
//    }

//    public WebSecurityConfig(JwtRequestFilter jwtRequestFilter,
//                                      MyUserDetailsService userDetailsService,
//                                      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
//        this.jwtRequestFilter = jwtRequestFilter;
//        this.userDetailsService = userDetailsService;
//        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCryptPasswordEncoder
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Disables CSRF
                .authorizeHttpRequests(authz -> authz
                                .requestMatchers("/static/**", "/templates/**", "/auth/**", "/h2-console/**").permitAll()
                                .requestMatchers("/admin/registerAdmin").permitAll()
                                .requestMatchers("/user/").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERADMIN")
//                                .requestMatchers("/admin/").hasAnyAuthority("ROLE_SUPERADMIN")
//                                .requestMatchers("/superadmin/").hasAnyAuthority("ROLE_SUPERADMIN")
                                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPERADMIN")
                                .requestMatchers("/user/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER","ROLE_SUPERADMIN")
//                                .requestMatchers("/admin/registerAdmin").permitAll() // Allow access to /registerAdmin without authentication
//                                .requestMatchers("/admin/{adminId}").hasAuthority("ROLE_ADMIN") // Only admins can access /admin/{adminId}
//                                .requestMatchers("/admin/user/{userId}").hasAuthority("ROLE_ADMIN") // Only admins can access /admin/user/{userId}
//                                .requestMatchers("/admin/users").hasAuthority("ROLE_ADMIN") // Only admins can access /admin/users
//                                .requestMatchers("/admin/**").hasAuthority("ROLE_USER") // Users can access other admin endpoints
////                                .requestMatchers("/user/{userId}").hasAuthority("ROLE_USER")
////                                .requestMatchers("/user/{userId}").hasAuthority("ROLE_ADMIN")
//                                .requestMatchers("/user/").hasAuthority("ROLE_ADMIN")
                                .anyRequest().authenticated()
//                        .requestMatchers("/static/**", "/templates/**", "/auth/**", "/h2-console/**").permitAll()
////                        .requestMatchers("/user/**").hasAuthority("ROLE_USER") // Users can access user endpoints
//                        .requestMatchers("/admin/registerAdmin").permitAll() // Allow access to /registerAdmin without authentication
//                        .requestMatchers("/admin/{adminId}").permitAll()
//                        .requestMatchers("/admin/user/{userId}").permitAll()
//                        .requestMatchers("/admin/users").permitAll()
//                        .requestMatchers("/admin/**").hasAuthority("ROLE_USER")
////                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // Admins can access admin endpoints
////                        .requestMatchers("/user/**").hasAuthority("ROLE_USER") // Admins can also access user endpoints
//                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(accessDeniedHandler)  // Use custom AccessDeniedHandler
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> {
                    headers.addHeaderWriter(new ContentSecurityPolicyHeaderWriter("default-src 'self'; frame-ancestors 'none';"));
                    headers.addHeaderWriter(new HstsHeaderWriter());
                });

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
