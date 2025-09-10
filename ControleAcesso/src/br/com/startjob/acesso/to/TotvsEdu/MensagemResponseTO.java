package br.com.startjob.acesso.to.TotvsEdu;

public class MensagemResponseTO {

	private int status;
	private String mensagem;

	public MensagemResponseTO() {
	}

	public MensagemResponseTO(Integer status, String mensagem) {
		this.mensagem = mensagem;
		this.setStatus(status);
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
