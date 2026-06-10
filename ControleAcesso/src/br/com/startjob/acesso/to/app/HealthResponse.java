package br.com.startjob.acesso.to.app;

public class HealthResponse {

	private String status = "ok";
	private String service = "smart-acesso-app-api";
	private boolean jwtConfigured;
	private boolean firebasePathConfigured;
	private boolean firebaseFileExists;
	private boolean firebaseReady;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public boolean isJwtConfigured() {
		return jwtConfigured;
	}

	public void setJwtConfigured(boolean jwtConfigured) {
		this.jwtConfigured = jwtConfigured;
	}

	public boolean isFirebasePathConfigured() {
		return firebasePathConfigured;
	}

	public void setFirebasePathConfigured(boolean firebasePathConfigured) {
		this.firebasePathConfigured = firebasePathConfigured;
	}

	public boolean isFirebaseFileExists() {
		return firebaseFileExists;
	}

	public void setFirebaseFileExists(boolean firebaseFileExists) {
		this.firebaseFileExists = firebaseFileExists;
	}

	public boolean isFirebaseReady() {
		return firebaseReady;
	}

	public void setFirebaseReady(boolean firebaseReady) {
		this.firebaseReady = firebaseReady;
	}
}
