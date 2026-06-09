package br.com.startjob.acesso.to;

import br.com.startjob.acesso.modelo.enumeration.WebPermissao;

public class WebPermissaoLinhaTO {

	private final WebPermissao permissao;
	private boolean administrador;
	private boolean gerente;
	private boolean operador;
	private boolean porteiro;
	private boolean cuidador;
	private boolean responsavel;

	public WebPermissaoLinhaTO(WebPermissao permissao) {
		this.permissao = permissao;
	}

	public WebPermissao getPermissao() {
		return permissao;
	}

	public String getMessageKey() {
		return permissao.getMessageKey();
	}

	public String getCodigo() {
		return permissao.getCodigo();
	}

	public boolean isAdministrador() {
		return administrador;
	}

	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

	public boolean isGerente() {
		return gerente;
	}

	public void setGerente(boolean gerente) {
		this.gerente = gerente;
	}

	public boolean isOperador() {
		return operador;
	}

	public void setOperador(boolean operador) {
		this.operador = operador;
	}

	public boolean isPorteiro() {
		return porteiro;
	}

	public void setPorteiro(boolean porteiro) {
		this.porteiro = porteiro;
	}

	public boolean isCuidador() {
		return cuidador;
	}

	public void setCuidador(boolean cuidador) {
		this.cuidador = cuidador;
	}

	public boolean isResponsavel() {
		return responsavel;
	}

	public void setResponsavel(boolean responsavel) {
		this.responsavel = responsavel;
	}
}
