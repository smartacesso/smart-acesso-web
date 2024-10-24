package br.com.startjob.acesso.modelo.entity;

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

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;

@Entity
@Table(name="TB_EQUIPAMENTO")
@NamedQueries({
	@NamedQuery(name  = "EquipamentoEntity.findAll", 
				query = "select obj "
				      + "from EquipamentoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "EquipamentoEntity.findById", 
				query = "select obj from EquipamentoEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "EquipamentoEntity.findAllByIdCliente", 
				query = "select obj from EquipamentoEntity obj "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "EquipamentoEntity.findByIdentificador",
				query = "select obj from EquipamentoEntity obj "
					  + "where obj.identificador = :ID_EQUIPAMENTO "
					  + "order by obj.id asc")
})
@SuppressWarnings("serial")
public class EquipamentoEntity extends ClienteBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_EQUIPAMENTO", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Column(name="IP", nullable=true, length=255)
	private String ip;
	
	@Column(name="IDENTIFICADOR", nullable=true, length=255)
	private String identificador;
	
	@Column(name="MARCA", nullable=true, length=255)
	private String marca;
	
	@Column(name="MODELO", nullable=true, length=255)
	private String modelo;
	
	@Column(name="LOCAL", nullable=true, length=255)
	private String local;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_USUARIO", nullable=true)
	protected UsuarioEntity usuario;
	
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
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public UsuarioEntity getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioEntity usuario) {
		this.usuario = usuario;
	}
	
}
