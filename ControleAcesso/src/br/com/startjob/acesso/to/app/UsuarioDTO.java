package br.com.startjob.acesso.to.app;

public class UsuarioDTO {
    private Long id;
    private String nome;
    private String cliente;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

    // getters e setters
}