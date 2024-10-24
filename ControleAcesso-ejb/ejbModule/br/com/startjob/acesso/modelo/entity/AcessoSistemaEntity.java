package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name="TB_ACESSO_SISTEMA")
@NamedQueries({
	@NamedQuery(name  = "AcessoSistemaEntity.findAll", 
				query = "select obj "
				      + "from AcessoSistemaEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "AcessoSistemaEntity.findById", 
				query = "select obj from AcessoSistemaEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class AcessoSistemaEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_ACESSO_SISTEMA", nullable=false, length=4)
	private Long id;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_USUARIO", nullable=true)
	private UsuarioEntity usuario;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_PEDESTRE", nullable=true)
	private PedestreEntity pedestre;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA", nullable=true, length=11)
	private Date data;
	
	@Column(name="DISPOSITICO", nullable=true, length=255)
	private String dispositivo;
	
	@Column(name="TIPO", nullable=true, length=100)
	private String tipo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UsuarioEntity getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioEntity usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDispositivo() {
		return dispositivo;
	}

	public void setDispositivo(String dispositivo) {
		this.dispositivo = dispositivo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public PedestreEntity getPedestre() {
		return pedestre;
	}

	public void setPedestre(PedestreEntity pedestre) {
		this.pedestre = pedestre;
	}
	
	

}
