package com.protreino.luxandserver.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendReceiveTrackerParams {
	
	private long idCliente;
	private String fileTracker;
	
	public SendReceiveTrackerParams() {
	}

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public String getFileTracker() {
		return fileTracker;
	}

	public void setFileTracker(String fileTracker) {
		this.fileTracker = fileTracker;
	}
	
}
