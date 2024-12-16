package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;

@Entity
@Table(name="TB_CLUBE_REGIONAL")
@NamedQueries({
	@NamedQuery(name  = "ClubeRegionalEntity.findAll", 
				query = "select obj "
				      + "from ClubeRegionalEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "ClubeRegionalEntity.findById", 
				query = "select obj from ClubeRegionalEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class ClubeRegionalEntity extends ClienteBaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_ACESSO", nullable=false, length=4)
	private Long id;
	
	@Column(name="FILIAL_ID", nullable=false, length=100)
	private String filialId;
	
	@Column(name="FILIAL_CIDADE", nullable=false, length=100)
	private String filialCidade;
	
	@Column(name="FILIAL_NOME", nullable=false, length=100)
	private String filialNome;
	
	@Column(name="CODIGO_REGIONAL", nullable=false, length=100)
	private String codigoRegional;
	
	@Column(name="NOME_REGIONAL", nullable=false, length=100)
	private String nomeRegional;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilialId() {
		return filialId;
	}

	public void setFilialId(String filialId) {
		this.filialId = filialId;
	}

	public String getFilialCidade() {
		return filialCidade;
	}

	public void setFilialCidade(String filialCidade) {
		this.filialCidade = filialCidade;
	}

	public String getFilialNome() {
		return filialNome;
	}

	public void setFilialNome(String filialNome) {
		this.filialNome = filialNome;
	}

	public String getCodigoRegional() {
		return codigoRegional;
	}

	public void setCodigoRegional(String codigoRegional) {
		this.codigoRegional = codigoRegional;
	}

	public String getNomeRegional() {
		return nomeRegional;
	}

	public void setNomeRegional(String nomeRegional) {
		this.nomeRegional = nomeRegional;
	}
	
}
