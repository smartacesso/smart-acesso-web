package br.com.startjob.acesso.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.startjob.acesso.modelo.ejb.AppEJBRemote;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;

/**
 * Envio de push via Firebase Cloud Messaging (Admin SDK).
 * Credenciais: ver {@link FirebaseConfig}.
 */
public final class FirebasePushService {

	private static final Logger LOG = Logger.getLogger(FirebasePushService.class.getName());
	private static final Object INIT_LOCK = new Object();
	private static volatile boolean initialized = false;

	private FirebasePushService() {
	}

	public static FirebasePushResult send(AppEJBRemote appEjb, String token, String title, String body,
			Map<String, String> dataMap) {
		FirebasePushResult result = send(token, title, body, dataMap);
		if (!result.isSuccess() && result.isTokenInvalid() && appEjb != null) {
			try {
				appEjb.invalidarDeviceTokenPorFcm(token);
				LOG.info("Token FCM marcado como inativo: " + maskToken(token));
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Falha ao marcar token FCM como inativo", e);
			}
		}
		return result;
	}

	public static FirebasePushResult send(String token, String title, String body, Map<String, String> dataMap) {
		if (token == null || token.trim().isEmpty()) {
			return FirebasePushResult.failure("Token FCM vazio", true);
		}
		if (!ensureInitialized()) {
			return FirebasePushResult.failure("Firebase não configurado no servidor", false);
		}

		try {
			Message.Builder builder = Message.builder().setToken(token.trim());
			if (title != null || body != null) {
				builder.setNotification(Notification.builder()
						.setTitle(title)
						.setBody(body)
						.build());
			}
			if (dataMap != null && !dataMap.isEmpty()) {
				builder.putAllData(dataMap);
			}

			String messageId = FirebaseMessaging.getInstance().send(builder.build());
			LOG.info("Push enviado com sucesso. messageId=" + messageId + ", token=" + maskToken(token));
			return FirebasePushResult.success(messageId);
		} catch (FirebaseMessagingException e) {
			boolean tokenInvalid = isInvalidTokenError(e);
			LOG.log(Level.WARNING, "Falha ao enviar push: " + e.getMessagingErrorCode() + " - " + e.getMessage()
					+ ", token=" + maskToken(token), e);
			return FirebasePushResult.failure(e.getMessage(), tokenInvalid);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro inesperado ao enviar push", e);
			return FirebasePushResult.failure(e.getMessage(), false);
		}
	}

	public static boolean isConfigured() {
		return FirebaseConfig.isServiceAccountConfigured();
	}

	/** Inicializa o SDK sem propagar erro de classpath (ex.: Guava ausente). */
	public static boolean tryEnsureInitialized() {
		try {
			return ensureInitialized();
		} catch (LinkageError e) {
			LOG.log(Level.SEVERE,
					"Dependencias Firebase incompletas no WAR deployado (ex.: guava, google-http-client-jackson2). "
							+ "Execute scripts/download-firebase-libs.ps1, scripts/verify-firebase-libs.ps1 e redeploy do EAR",
					e);
			return false;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Falha ao inicializar Firebase", e);
			return false;
		}
	}

	public static boolean ensureInitialized() {
		if (initialized) {
			return true;
		}
		synchronized (INIT_LOCK) {
			if (initialized) {
				return true;
			}
			String path = FirebaseConfig.resolveServiceAccountPath();
			if (path == null) {
				LOG.warning("Service account Firebase não configurado (ver FirebaseConfig)");
				return false;
			}
			if (!new java.io.File(path).isFile()) {
				LOG.warning("Arquivo service account Firebase não encontrado: " + path);
				return false;
			}
			try (InputStream in = new FileInputStream(path)) {
				byte[] jsonBytes = readAllBytes(in);
				GoogleCredentials credentials = GoogleCredentials.fromStream(new java.io.ByteArrayInputStream(jsonBytes));
				FirebaseOptions options = FirebaseOptions.builder()
						.setCredentials(credentials)
						.build();
				if (FirebaseApp.getApps().isEmpty()) {
					FirebaseApp.initializeApp(options);
				}
				initialized = true;
				LOG.info("Firebase Admin SDK inicializado. projectId=" + extrairProjectId(jsonBytes));
				return true;
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Falha ao inicializar Firebase Admin SDK", e);
				return false;
			}
		}
	}

	private static boolean isInvalidTokenError(FirebaseMessagingException e) {
		MessagingErrorCode code = e.getMessagingErrorCode();
		return code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT;
	}

	private static byte[] readAllBytes(InputStream in) throws java.io.IOException {
		java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
		byte[] data = new byte[4096];
		int n;
		while ((n = in.read(data)) != -1) {
			buffer.write(data, 0, n);
		}
		return buffer.toByteArray();
	}

	private static String extrairProjectId(byte[] jsonBytes) {
		try {
			String json = new String(jsonBytes, "UTF-8");
			java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\"project_id\"\\s*:\\s*\"([^\"]+)\"")
					.matcher(json);
			return matcher.find() ? matcher.group(1) : "desconhecido";
		} catch (Exception e) {
			return "desconhecido";
		}
	}

	private static String maskToken(String token) {
		if (token == null || token.length() <= 8) {
			return "***";
		}
		return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
	}
}
