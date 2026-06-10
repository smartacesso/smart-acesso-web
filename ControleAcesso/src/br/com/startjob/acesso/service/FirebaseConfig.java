package br.com.startjob.acesso.service;

import java.io.File;

import br.com.startjob.acesso.modelo.utils.AppAmbienteUtils;

/**
 * Resolução do caminho do JSON da service account Firebase (Admin SDK).
 * Prioridade: system property → variável de ambiente → ambiente-config.properties.
 */
public final class FirebaseConfig {

	public static final String SYSTEM_PROPERTY = "firebase.service.account.path";
	public static final String ENV_VARIABLE = "FIREBASE_SERVICE_ACCOUNT_PATH";
	public static final String AMBIENTE_CONFIG_KEY = AppAmbienteUtils.CONFIG_FIREBASE_SERVICE_ACCOUNT_PATH;

	private FirebaseConfig() {
	}

	public static String resolveServiceAccountPath() {
		String path = System.getProperty(SYSTEM_PROPERTY);
		if (isBlank(path)) {
			path = System.getenv(ENV_VARIABLE);
		}
		if (isBlank(path)) {
			path = AppAmbienteUtils.getConfig(AMBIENTE_CONFIG_KEY);
		}
		return isBlank(path) ? null : path.trim();
	}

	public static boolean isServiceAccountConfigured() {
		String path = resolveServiceAccountPath();
		if (path == null) {
			return false;
		}
		return new File(path).isFile();
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
