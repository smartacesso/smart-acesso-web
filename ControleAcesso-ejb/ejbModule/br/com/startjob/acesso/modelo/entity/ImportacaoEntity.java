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
@Table(name="TB_IMPORTACAO")
@NamedQueries({
	@NamedQuery(name = "ImportacaoEntity.findAll", 
				query = "select obj from ImportacaoEntity obj order by obj.id asc"),
	@NamedQuery(name = "ImportacaoEntity.findAllEager", 
				query = "select obj from ImportacaoEntity obj "
					  + "join fetch obj.usuario u "
					  + "join fetch obj.cliente c "
					  + "order by obj.id asc"),
	@NamedQuery(name = "ImportacaoEntity.findById", 
				query = "select obj from ImportacaoEntity obj "
					 + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class ImportacaoEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID", nullable=false, length=4)
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA", length=11, nullable=true)
	private Date data;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_FIM", length=11, nullable=true)
	private Date dataFim;
	
	@Lob
	@Column(name="ARQUIVO", nullable=true)
	private byte [] arquivo;
	
	@Column(name="NOME_ARQUIVO", length=100, nullable=true)
	private String nomeArquivo;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_USUARIO", nullable=true)
	private UsuarioEntity usuario;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_CLIENTE", nullable=true)
	private ClienteEntity cliente;
	
	@Column(name="TIPO_IMPORTACAO", length=100, nullable=true)
	private String tipoImportacao;
	
	@Column(name="OBSERVACAO", length=5000, nullable=true)
	private String observacao;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public byte[] getArquivo() {
		return arquivo;
	}

	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public UsuarioEntity getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioEntity usuario) {
		this.usuario = usuario;
	}

	public ClienteEntity getCliente() {
		return cliente;
	}

	public void setCliente(ClienteEntity cliente) {
		this.cliente = cliente;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getTipoImportacao() {
		return tipoImportacao;
	}

	public void setTipoImportacao(String tipoImportacao) {
		this.tipoImportacao = tipoImportacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}

