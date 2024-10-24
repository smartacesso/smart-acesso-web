package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;
import br.com.startjob.acesso.modelo.enumeration.StatusCadastroExterno;

@SuppressWarnings("serial")
@Entity
@Table(name="TB_CADASTRO_EXTERNO")
@NamedQueries({
	@NamedQuery(name  = "CadastroExternoEntity.findAll", 
				query = "select obj "
					  + "from CadastroExternoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "CadastroExternoEntity.findAllComplete", 
				query = "select obj "
					  + "from CadastroExternoEntity obj "
					  + "     join fetch obj.pedestre p "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "CadastroExternoEntity.findByIdComplete", 
				query = "select obj from CadastroExternoEntity obj "
					  + "     join fetch obj.pedestre p "
					  + "where obj.id = :ID "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "CadastroExternoEntity.findById", 
				query = "select obj from CadastroExternoEntity obj "
					  + "where obj.id = :ID "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "CadastroExternoEntity.findAllTokensActive",
				query = "select obj from CadastroExternoEntity obj "
					  + "where obj.pedestre.id = :ID_PEDESTRE "
					  + "and obj.statusCadastroExterno = :STATUS "
					  + "and obj.token > :TOKEN "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "CadastroExternoEntity.findByTokenAndIdPedestreAndIdCliente",
				query = "select obj from CadastroExternoEntity obj "
					  + "where obj.pedestre.id = :ID_PEDESTRE "
					  + "and obj.statusCadastroExterno = :STATUS "
					  + "and obj.token = :TOKEN "
					  + "and obj.cliente.id = :ID_CLIENTE "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "CadastroExternoEntity.findAllByIdClienteAndStatus",
				query = "select obj "
					  + "from CadastroExternoEntity obj "
					  + "     join fetch obj.pedestre p "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and obj.statusCadastroExterno = :STATUS "
					  + "and (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "CadastroExternoEntity.findLastByIdPedestre",
				query = "select obj from CadastroExternoEntity obj "
					  + "where obj.pedestre.id = :ID_PEDESTRE "
					  + "and (obj.removido = false or obj.removido is null) "
					  + "order by obj.dataCriacao desc")
})
public class CadastroExternoEntity extends ClienteBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_CADASTRO_EXTERNO", nullable=false, length=4)
	private Long id;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_PEDESTRE", nullable=true)
	private PedestreEntity pedestre;
	
	@Column(name="TOKEN", nullable=true, length=40)
	private Long token;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS_CADASTRO_EXTERNO", nullable=true, length=30)
	private StatusCadastroExterno statusCadastroExterno;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO_FACE", nullable=true, length=10)
	private Date dataCadastroDaFace;
	
	@Column(name="CODIGO_RESULTADO_PROCESSAMENTO", nullable=true, length=10)
	private Integer codigoResultadoProcessamento;
	
	@Column(name="DESCRICAO_RESULTADO_PROCESSAMENTO", nullable=true, length=10)
	private String descricaoResultadoProcessamento;
	
	@Lob
	@Column(name="PRIMEIRA_FOTO", nullable=true)
	private byte[] primeiraFoto;
	
	@Lob
	@Column(name="SEGUNDA_FOTO", nullable=true)
	private byte[] segundaFoto;
	
	@Lob
	@Column(name="TERCEIRA_FOTO", nullable=true)
	private byte[] terceiraFoto;
	
	@Column(name="IMAGE_WIDTH", nullable=true)
	private Integer imageWidth;
	
	@Column(name="IMAGE_HEIGHT", nullable=true)
	private Integer imageHeight;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PedestreEntity getPedestre() {
		return pedestre;
	}

	public void setPedestre(PedestreEntity pedestre) {
		this.pedestre = pedestre;
	}

	public Long getToken() {
		return token;
	}

	public void setToken(Long token) {
		this.token = token;
	}

	public StatusCadastroExterno getStatusCadastroExterno() {
		return statusCadastroExterno;
	}

	public void setStatusCadastroExterno(StatusCadastroExterno statusCadastroExterno) {
		this.statusCadastroExterno = statusCadastroExterno;
	}

	public byte[] getPrimeiraFoto() {
		return primeiraFoto;
	}

	public void setPrimeiraFoto(byte[] primeiraFoto) {
		this.primeiraFoto = primeiraFoto;
	}

	public byte[] getSegundaFoto() {
		return segundaFoto;
	}

	public void setSegundaFoto(byte[] segundaFoto) {
		this.segundaFoto = segundaFoto;
	}

	public byte[] getTerceiraFoto() {
		return terceiraFoto;
	}

	public void setTerceiraFoto(byte[] terceiraFoto) {
		this.terceiraFoto = terceiraFoto;
	}

	public Date getDataCadastroDaFace() {
		return dataCadastroDaFace;
	}

	public void setDataCadastroDaFace(Date dataCadastroDaFace) {
		this.dataCadastroDaFace = dataCadastroDaFace;
	}

	public Integer getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(Integer imageWidth) {
		this.imageWidth = imageWidth;
	}

	public Integer getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(Integer imageHeight) {
		this.imageHeight = imageHeight;
	}

	public Integer getCodigoResultadoProcessamento() {
		return codigoResultadoProcessamento;
	}

	public void setCodigoResultadoProcessamento(Integer codigoResultadoProcessamento) {
		this.codigoResultadoProcessamento = codigoResultadoProcessamento;
	}

	public String getDescricaoResultadoProcessamento() {
		return descricaoResultadoProcessamento;
	}

	public void setDescricaoResultadoProcessamento(String descricaoResultadoProcessamento) {
		this.descricaoResultadoProcessamento = descricaoResultadoProcessamento;
	}
	
}
