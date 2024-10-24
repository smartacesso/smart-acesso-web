package com.senior.services.Enum;

public enum SoapOperation {
	EMPRESA("empresa"),
    LOCAL("local"),
    PEDESTRE("pedestre"),
    PEDESTRE_DEMITIDOS("pedestreDemitidos"),
    PEDESTRE_ADMITIDOS("pedestreAdmitidos"),
    CARGO("cargo"),
    CENTRO_CUSTO("centroCusto");
	

    private final String operation;

    SoapOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
