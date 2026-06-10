package br.com.startjob.acesso.service;

public class FirebasePushResult {

	private final boolean success;
	private final String messageId;
	private final String error;
	private final boolean tokenInvalid;

	private FirebasePushResult(boolean success, String messageId, String error, boolean tokenInvalid) {
		this.success = success;
		this.messageId = messageId;
		this.error = error;
		this.tokenInvalid = tokenInvalid;
	}

	public static FirebasePushResult success(String messageId) {
		return new FirebasePushResult(true, messageId, null, false);
	}

	public static FirebasePushResult failure(String error, boolean tokenInvalid) {
		return new FirebasePushResult(false, null, error, tokenInvalid);
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getError() {
		return error;
	}

	public boolean isTokenInvalid() {
		return tokenInvalid;
	}
}
