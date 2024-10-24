package com.protreino.luxandserver.security;

import java.util.Date;
import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***
 * Classe de utilidades para manipulação de tokens JWT.
 * @author Juscelino
 *
 */
@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;
	public static final long JWT_TOKEN_VALIDITY = 30 * 60; // 30 minutos

	@Value("${jwt.secret}")
	private String secret;
	
	public String generateToken(String subject) {
		return Jwts.builder().setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	
	public Boolean validateToken(String token, String username) {
		try {
			String usernameOnToken = getUsernameFromToken(token);
			return (usernameOnToken.equals(username) && !isTokenExpired(token));
		}
		catch (Exception e) {
		}
		return false;
	}
	
	public Boolean isTokenExpired(String token) {
		try {
			Date expiration = getExpirationDateFromToken(token);
			return expiration.before(new Date());
		}
		catch (Exception e) {
		}
		return true;
	}
	
	public String getUsernameFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
	}

	public Date getExpirationDateFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
	}

	public void addAuthenticationToRequest(HttpServletResponse res, String username) {
		String token = generateToken(username);
		res.addHeader("Authorization", "Bearer " + token);
	}
	
	public String getTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer")) {
			return token.replace("Bearer ", "");
		}
		return null;
	}

}
