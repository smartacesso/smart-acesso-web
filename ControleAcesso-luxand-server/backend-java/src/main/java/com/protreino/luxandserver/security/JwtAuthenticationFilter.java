package com.protreino.luxandserver.security;

import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.protreino.luxandserver.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;

/***
 * Classe de filtro executada para qualquer solicitação recebida. 
 * Ela verifica se a solicitação possui um token JWT válido. 
 * Se tiver um token JWT válido, ela define a autenticação no contexto para especificar que o usuário atual é autenticado.
 * @author Juscelino
 *
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		
		SecurityContextHolder.getContext().setAuthentication(null);
		
		if (userRepository.count() > 0l) {
			
			logger.info("=====================================================");
			logger.info(request.getMethod() + " " + request.getRequestURI());
			logger.info("Validando o token JWT...");
			
			String jwtToken = jwtTokenUtil.getTokenFromRequest(request);
			
			if (jwtToken != null) {
				try {
					String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
					
					if (jwtTokenUtil.validateToken(jwtToken, username)) {
						
						logger.info("Token OK!");
						
						List<GrantedAuthority> authorities = emptyList();
						
						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								username, null, authorities);
						
						usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						
						// define a autenticação no contexto para especificar que o usuário atual é autenticado
						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
						
					}
					else {
						logger.error("Token de acesso inválido.");
					}
					
				} 
				catch (IllegalArgumentException e) {
					logger.error("N�o foi possível encontrar um token de acesso.");
				} 
				catch (ExpiredJwtException e) {
					logger.error("Token de acesso expirado. Faça login novamente.");
				}
			} 
			else {
				logger.error("N�o foi possível encontrar um token de acesso.");
			}
			
		}
		
		chain.doFilter(request, response);
	}
	
	public class JwtResponseRequestWrapper extends HttpServletResponseWrapper{
	    public JwtResponseRequestWrapper(HttpServletResponse response) {
	        super(response);
	    }
	}

}