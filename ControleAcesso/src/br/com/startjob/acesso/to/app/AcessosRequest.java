package br.com.startjob.acesso.to.app;

public class AcessosRequest {
    private String dataInicio; // Formato "yyyy-MM-dd" ou "yyyy-MM-dd HH:mm:ss"
    private String dataFim;
    private int pagina = 0;
    private int tamanho = 20;
    /** ENTRADA, SAIDA ou null para todos. */
    private String sentido;
    private String busca;

	public String getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}
	public String getDataFim() {
		return dataFim;
	}
	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}
	public int getPagina() {
		return pagina;
	}
	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	public int getTamanho() {
		return tamanho;
	}
	public void setTamanho(int tamanho) {
		this.tamanho = tamanho;
	}

	public String getSentido() {
		return sentido;
	}

	public void setSentido(String sentido) {
		this.sentido = sentido;
	}

	public String getBusca() {
		return busca;
	}

	public void setBusca(String busca) {
		this.busca = busca;
	}
}