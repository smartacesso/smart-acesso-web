package br.com.startjob.acesso.utils;

public final class JwtConfig {

	private JwtConfig() {
	}

	public static boolean isConfigured() {
		String secret = System.getProperty("jwt.secret");
		return secret != null && !secret.trim().isEmpty();
	}

	public static String requireSecret() {
		String secret = System.getProperty("jwt.secret");
		if (secret == null || secret.trim().isEmpty()) {
			throw new IllegalStateException(
					"jwt.secret não configurado no servidor (system property WildFly)");
		}
		return secret;
	}
}
