package com.protreino.luxandserver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/***
 * Classe responsável por alterar as configurações de segurança da aplicação.
 * @author Juscelino
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
    private JwtUserDetailsService jwtUserDetailsService;
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.cors()
			.and()
			.csrf().disable()
			.authorizeRequests()
				.antMatchers("/auth/**", "/recognition/**").permitAll() // Permite essas rotas. Mas no RecognitionController é feita validação se há usuário logado
				.anyRequest().authenticated() // As outras precisam de autenticação. No caso seria a /configurations.
			.and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Filtra os outros requests para validar o token
	}
	
	@Autowired
    protected void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
		builder
			.userDetailsService(jwtUserDetailsService)
			.passwordEncoder(new BCryptPasswordEncoder());
    }
	
}
