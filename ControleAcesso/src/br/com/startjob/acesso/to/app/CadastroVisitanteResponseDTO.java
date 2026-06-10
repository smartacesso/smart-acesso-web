package br.com.startjob.acesso.to.app;

import br.com.startjob.acesso.modelo.entity.PedestreEntity;

public class CadastroVisitanteResponseDTO {

	private Long id;
	private String nome;
	private String cpf;
	private String celular;

	public static CadastroVisitanteResponseDTO from(PedestreEntity entity) {
		if (entity == null) {
			return null;
		}
		CadastroVisitanteResponseDTO dto = new CadastroVisitanteResponseDTO();
		dto.setId(entity.getId());
		dto.setNome(entity.getNome());
		dto.setCpf(entity.getCpf());
		dto.setCelular(entity.getCelular());
		return dto;
	}

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

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}
}
