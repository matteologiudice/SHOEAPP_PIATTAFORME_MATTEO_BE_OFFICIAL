package com.ShoeAppBE.configurations;


import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.ShoeAppBE.authentication.JwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfiguration{

   @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
       http
               .csrf().disable()
               .authorizeHttpRequests((auth)->
               auth
                       .requestMatchers("/product/**").permitAll()
                       .requestMatchers("/user/**").permitAll()
                       .requestMatchers("/category/**").permitAll()
                       .requestMatchers("/brand/**").permitAll()
                       .requestMatchers("/cart/**").permitAll()
                       .requestMatchers("/purchase/**").permitAll()
                       .anyRequest().authenticated()
                )
               .sessionManagement()
               .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().oauth2ResourceServer().jwt().jwtAuthenticationConverter(new JwtAuthenticationConverter());
       http.httpBasic(Customizer.withDefaults());
       return http.build();
   }


   @Bean
    public CorsFilter corsFilter(){
       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       CorsConfiguration configuration = new CorsConfiguration();
       configuration.setAllowCredentials(false);
       configuration.addAllowedOrigin("*");//indirizzo da dove provengono le chiamate del front-end
       configuration.addAllowedHeader("*");
       configuration.addAllowedMethod("OPTIONS");
       configuration.addAllowedMethod("GET");
       configuration.addAllowedMethod("POST");
       configuration.addAllowedMethod("DELETE");
       configuration.addAllowedMethod("PUT");
       source.registerCorsConfiguration("/**", configuration);
       return new CorsFilter(source);
   }
}