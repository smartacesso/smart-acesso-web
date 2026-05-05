package br.com.startjob.acesso.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

	private static final String SECRET = System.getProperty("jwt.secret");
    private static final long EXPIRATION = 1000 * 60 * 60 * 8; // 8h

    public static String gerarToken(Long userId, String cliente) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("cliente", cliente)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static Claims validarToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}