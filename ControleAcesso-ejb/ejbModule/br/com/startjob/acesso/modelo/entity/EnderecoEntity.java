package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name="TB_ENDERECO")
@NamedQueries({
	@NamedQuery(name  = "EnderecoEntity.findAll", 
				query = "select obj "
				      + "from EnderecoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "EnderecoEntity.findById", 
				query = "select obj from EnderecoEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class EnderecoEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_ENDERECO", nullable=false, length=4)
	private Long id;
	
	@Column(name="CEP", nullable=true, length=20)
	private String cep;
	
	@Column(name="LOGRADOURO", nullable=true, length=255)
	private String logradouro;
	
	@Column(name="NUMERO", nullable=true, length=10)
	private String numero;
	
	@Column(name="COMPLEMENTO", nullable=true, length=255)
	private String complemento;

	@Column(name="BAIRRO", nullable=true, length=100)
	private String bairro;
	
	@Column(name="CIDADE", nullable=true, length=255)
	private String cidade;
	
	@Column(name="ESTADO", nullable=true, length=100)
	private String estado;
	
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	
}
