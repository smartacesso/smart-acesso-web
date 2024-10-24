package com.protreino.services.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendReceiveTrackerParams {

    private long idCliente;
    private byte [] fileTracker;

    public SendReceiveTrackerParams() {
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public byte [] getFileTracker() {
        return fileTracker;
    }

    public void setFileTracker(byte [] fileTracker) {
        this.fileTracker = fileTracker;
    }

}
