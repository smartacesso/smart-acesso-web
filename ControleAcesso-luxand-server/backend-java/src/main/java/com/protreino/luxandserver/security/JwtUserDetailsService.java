package com.protreino.luxandserver.security;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.protreino.luxandserver.entity.UserEntity;
import com.protreino.luxandserver.repository.UserRepository;

/***
 * Classe responsável por buscar os dados do usuário baseado no username.
 * @author Juscelino
 *
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);
	
	@Autowired
	private UserRepository userRepository;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		logger.info("========= UserDetails: " + username + " ==================");
		
		UserEntity user = userRepository.findByUsername(username); // username é o email
		
		if (user != null) {
			return new User(user.getUsername(), user.getPassword(), new ArrayList<GrantedAuthority>());
		}
		
		return null;
	}
	
}
