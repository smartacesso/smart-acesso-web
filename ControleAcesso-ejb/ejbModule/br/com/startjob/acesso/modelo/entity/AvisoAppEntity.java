package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;

/**
 * Avisos exibidos no app mobile (independente de {@link NewsLetterEntity} do app antigo).
 */
@Entity
@Table(name = "TB_AVISO_APP")
@NamedQueries({
		@NamedQuery(name = "AvisoAppEntity.findAll", query = "SELECT obj FROM AvisoAppEntity obj "
				+ "WHERE (obj.removido = false OR obj.removido IS NULL) ORDER BY obj.dataPublicacao DESC, obj.id DESC"),
		@NamedQuery(name = "AvisoAppEntity.findAllByIdCliente", query = "SELECT obj FROM AvisoAppEntity obj "
				+ "WHERE obj.cliente.id = :ID_CLIENTE "
				+ "AND (obj.removido = false OR obj.removido IS NULL) "
				+ "ORDER BY obj.dataPublicacao DESC, obj.id DESC"),
		@NamedQuery(name = "AvisoAppEntity.findById", query = "SELECT obj FROM AvisoAppEntity obj "
				+ "WHERE obj.id = :ID AND (obj.removido = false OR obj.removido IS NULL)"),
		@NamedQuery(name = "AvisoAppEntity.findIdsComImagemByCliente", query = "SELECT obj.id FROM AvisoAppEntity obj "
				+ "WHERE obj.cliente.id = :ID_CLIENTE AND obj.imagem IS NOT NULL "
				+ "AND (obj.removido = false OR obj.removido IS NULL)")
})
public class AvisoAppEntity extends ClienteBaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_AVISO_APP", nullable = false)
	private Long id;

	@Column(name = "TITULO", nullable = false, length = 255)
	private String titulo;

	@Column(name = "DESCRICAO", nullable = true, length = 2000)
	private String descricao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_PUBLICACAO", nullable = false)
	private Date dataPublicacao;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "IMAGEM", nullable = true)
	private byte[] imagem;

	/** Preenchido na listagem web sem carregar o LOB. */
	@Transient
	private boolean possuiImagem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public boolean isTemImagem() {
		return imagem != null && imagem.length > 0;
	}

	public boolean isPossuiImagem() {
		return possuiImagem;
	}

	public void setPossuiImagem(boolean possuiImagem) {
		this.possuiImagem = possuiImagem;
	}
}
