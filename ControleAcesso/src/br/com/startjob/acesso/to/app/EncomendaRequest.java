package br.com.startjob.acesso.to.app;

public class EncomendaRequest {
    private int pagina = 0;
    private int tamanho = 20;
    
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
    
    
}
