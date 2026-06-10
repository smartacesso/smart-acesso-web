package br.com.startjob.acesso.to.app;

import br.com.startjob.acesso.modelo.entity.EmpresaEntity;

public class EmpresaResumoDTO {

	private Long id;
	private String nome;

	public static EmpresaResumoDTO from(EmpresaEntity entity) {
		if (entity == null) {
			return null;
		}
		EmpresaResumoDTO dto = new EmpresaResumoDTO();
		dto.setId(entity.getId());
		dto.setNome(entity.getNome());
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
}
