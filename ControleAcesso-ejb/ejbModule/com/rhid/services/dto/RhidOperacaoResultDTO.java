package com.rhid.services.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RhidOperacaoResultDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean completa;
	private int totalProcessados;
	private int totalCriados;
	private int totalAtualizados;
	private int totalErros;
	private List<String> mensagens = new ArrayList<>();

	public void addMensagem(String mensagem) {
		mensagens.add(mensagem);
	}

	public boolean isCompleta() {
		return completa;
	}

	public void setCompleta(boolean completa) {
		this.completa = completa;
	}

	public int getTotalProcessados() {
		return totalProcessados;
	}

	public void setTotalProcessados(int totalProcessados) {
		this.totalProcessados = totalProcessados;
	}

	public int getTotalCriados() {
		return totalCriados;
	}

	public void incrementCriados() {
		totalCriados++;
	}

	public int getTotalAtualizados() {
		return totalAtualizados;
	}

	public void incrementAtualizados() {
		totalAtualizados++;
	}

	public int getTotalErros() {
		return totalErros;
	}

	public void incrementErros() {
		totalErros++;
	}

	public void incrementProcessados() {
		totalProcessados++;
	}

	public void absorver(RhidOperacaoResultDTO outro) {
		if (outro == null) {
			return;
		}
		totalProcessados += outro.totalProcessados;
		totalCriados += outro.totalCriados;
		totalAtualizados += outro.totalAtualizados;
		totalErros += outro.totalErros;
		if (outro.mensagens != null) {
			mensagens.addAll(outro.mensagens);
		}
	}

	public List<String> getMensagens() {
		return mensagens;
	}

	public void setMensagens(List<String> mensagens) {
		this.mensagens = mensagens;
	}
}
