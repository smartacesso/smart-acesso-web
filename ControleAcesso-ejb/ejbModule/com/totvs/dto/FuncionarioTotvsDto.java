package com.totvs.dto;

import java.time.LocalDate;

import br.com.startjob.acesso.modelo.entity.CargoEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;

public class FuncionarioTotvsDto {

    private String codigoHorario;
    private String matricula;
    private String nome;
    private String nomeHorario;
    private String situacaoFolha;

	public PedestreEntity toPedestreEntity(ClienteEntity cliente) {
		PedestreEntity pedestre = new PedestreEntity();
		pedestre.setNome(nome);
		pedestre.setMatricula(matricula);
		pedestre.setCodigoCartaoAcesso(matricula);
		pedestre.setEmpresa(null);
		pedestre.setCargo(null);
		pedestre.setCliente(cliente);
		pedestre.setTipo(TipoPedestre.PEDESTRE);
		pedestre.setExistente(true);
		pedestre.setVersao(0);
		
		if(situacaoFolha.trim().equals("OK")) {
			pedestre.setObservacoes("Importado dia " + LocalDate.now().toString());
			pedestre.setStatus(Status.ATIVO);

		}else {
			pedestre.setStatus(Status.INATIVO);
			pedestre.setObservacoes("Funcionario com situação : " + situacaoFolha);
		}
		
		return pedestre;
	}

	public String getCodigoHorario() {
		return codigoHorario;
	}

	public void setCodigoHorario(String codigoHorario) {
		this.codigoHorario = codigoHorario;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeHorario() {
		return nomeHorario;
	}

	public void setNomeHorario(String nomeHorario) {
		this.nomeHorario = nomeHorario;
	}

	public String getSituacaoFolha() {
		return situacaoFolha;
	}

	public void setSituacaoFolha(String situacaoFolha) {
		this.situacaoFolha = situacaoFolha;
	}

}
