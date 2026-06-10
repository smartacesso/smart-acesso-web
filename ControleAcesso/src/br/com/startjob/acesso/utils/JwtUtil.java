package br.com.startjob.acesso.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    private static final long EXPIRATION = 1000 * 60 * 60 * 8; // 8h

    public static String gerarToken(Long userId, String cliente, String perfil) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("cliente", cliente)
                .claim("perfil", perfil)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, JwtConfig.requireSecret())
                .compact();
    }

    public static Claims validarToken(String token) {
        return Jwts.parser()
                .setSigningKey(JwtConfig.requireSecret())
                .parseClaimsJws(token)
                .getBody();
    }
}