package br.com.startjob.acesso.to;

import java.util.Objects;

public class HikivisionDeviceSimplificadoTO {
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    HikivisionDeviceSimplificadoTO that = (HikivisionDeviceSimplificadoTO) o;
	    return Objects.equals(devIndex, that.devIndex);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(devIndex);
	}


	public HikivisionDeviceSimplificadoTO(String nome, String id) {
		this.devName = nome;
		this.devIndex = id;
	}

	public HikivisionDeviceSimplificadoTO() {
	}

	private String devName;
	private String devIndex;

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getDevIndex() {
		return devIndex;
	}

	public void setDevIndex(String devIndex) {
		this.devIndex = devIndex;
	}

}