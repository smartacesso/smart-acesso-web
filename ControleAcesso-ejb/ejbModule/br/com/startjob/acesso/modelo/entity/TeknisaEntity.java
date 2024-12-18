package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "TEKNISA")
public class TeknisaEntity {

	/*
	SELECT c.ID_CLIENTE,c.CNPJ, p.nome, a.DATA FROM `controle-acesso`.tb_cliente c
	left join  `controle-acesso`.tb_pedestre p
	on c.ID_CLIENTE = p.ID_CLIENTE
	left join  `controle-acesso`.tb_acesso a
	on c.ID_CLIENTE = a.ID_CLIENTE;
	
	*/
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_TEKNISA", nullable=false, length=4)
	private Long id;
	
	private Long numeroDaOrganizacao;
	private Long codigoDaFilial;
	
	
	public Long getNumeroDaOrganizacao() {
		return numeroDaOrganizacao;
	}
	public void setNumeroDaOrganizacao(Long numeroDaOrganizacao) {
		this.numeroDaOrganizacao = numeroDaOrganizacao;
	}
	public Long getCodigoDaFilial() {
		return codigoDaFilial;
	}
	public void setCodigoDaFilial(Long codigoDaFilial) {
		this.codigoDaFilial = codigoDaFilial;
	}

	
}

