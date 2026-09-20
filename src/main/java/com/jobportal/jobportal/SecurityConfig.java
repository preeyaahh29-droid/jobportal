package com.jobportal.jobportal;

import com.jobportal.jobportal.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // -------------------------------------------------
                // CSRF
                // -------------------------------------------------

                .csrf(csrf -> csrf.disable())

                // -------------------------------------------------
                // CORS
                // -------------------------------------------------

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // -------------------------------------------------
                // JWT = STATELESS
                // -------------------------------------------------

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // -------------------------------------------------
                // AUTHORIZATION
                // -------------------------------------------------

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // PUBLIC ENDPOINTS
                        // =================================================

                        .requestMatchers(
                                "/api/health"
                        ).permitAll()

                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login"
                        ).permitAll()


                        // =================================================
                        // JOBS
                        // =================================================

                        // Anyone can VIEW jobs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs",
                                "/api/jobs/**"
                        ).permitAll()

                        // Only recruiter can CREATE jobs
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/jobs",
                                "/api/jobs/**"
                        ).hasRole("RECRUITER")

                        // Only recruiter can UPDATE jobs
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/jobs",
                                "/api/jobs/**"
                        ).hasRole("RECRUITER")

                        // Only recruiter can DELETE jobs
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/jobs",
                                "/api/jobs/**"
                        ).hasRole("RECRUITER")


                        // =================================================
                        // APPLICATIONS - JOB SEEKER
                        // =================================================

                        // Job seeker applies for a job
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/applications/job/**"
                        ).hasRole("JOB_SEEKER")

                        // Job seeker can view applications by email
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/email/**"
                        ).hasRole("JOB_SEEKER")

                        // Job seeker can delete/withdraw application
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/applications/**"
                        ).hasRole("JOB_SEEKER")


                        // =================================================
                        // APPLICATIONS - RECRUITER
                        // =================================================

                        // Recruiter can see applicants for a job
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/job/**"
                        ).hasRole("RECRUITER")

                        // Recruiter can update application status
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/applications/*/status"
                        ).hasRole("RECRUITER")

                        // Resume viewing requires authentication
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/resume/**"
                        ).authenticated()

                        // Recruiter can see all applications
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications"
                        ).hasRole("RECRUITER")


                        // =================================================
                        // JOB SEEKER PROFILE
                        // =================================================

                        .requestMatchers(
                                "/api/jobseeker/**"
                        ).hasRole("JOB_SEEKER")

                        .requestMatchers(
                                "/api/job-seeker-profiles/**"
                        ).hasRole("JOB_SEEKER")


                        // =================================================
                        // EMPLOYER PROFILE
                        // =================================================

                        .requestMatchers(
                                "/api/employer/**"
                        ).hasRole("RECRUITER")
                        

			
			// =================================================
			// SWAGGER / OPENAPI
			// =================================================

			.requestMatchers(
        			"/swagger-ui/**",
        			"/swagger-ui.html",
        			"/v3/api-docs/**"
			).permitAll()

			// =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        .anyRequest().authenticated()
                )

                // -------------------------------------------------
                // JWT FILTER
                // -------------------------------------------------

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

       	String frontendUrl = System.getenv("FRONTEND_URL");

	if (frontendUrl == null || frontendUrl.isBlank()) {
    		frontendUrl = "http://localhost:5173";
	}

	configuration.setAllowedOrigins(
        	List.of(frontendUrl)
	);
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}