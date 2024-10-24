package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name="TB_DOCUMENTO")
@NamedQueries({
	@NamedQuery(name  = "DocumentoEntity.findAll", 
				query = "select obj "
				      + "from DocumentoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "DocumentoEntity.findById", 
				query = "select obj from DocumentoEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class DocumentoEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_DOCUMENTO", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Lob
	@Column(name="ARQUIVO", nullable=true)
	private byte[] arquivo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="VALIDADE", nullable=true, length=11)
	private Date validade;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_PEDESTRE", nullable=true)
	private PedestreEntity pedestre;
	
	public DocumentoEntity() {}
	
	public DocumentoEntity(Long id, String nome, byte[] arquivo, Date validade) {
		this.id = id;
		this.nome = nome;
		this.arquivo = arquivo;
		this.validade = validade;
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
	public byte[] getArquivo() {
		return arquivo;
	}
	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}
	public PedestreEntity getPedestre() {
		return pedestre;
	}
	public void setPedestre(PedestreEntity pedestre) {
		this.pedestre = pedestre;
	}
	public Date getValidade() {
		return validade;
	}
	public void setValidade(Date validade) {
		this.validade = validade;
	}

}
