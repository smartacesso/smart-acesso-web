package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;

@Entity
@Table(name="TB_NEWS_LETTER")
@NamedQueries({
	@NamedQuery(name = "NewsLetterEntity.findByIDResposible", 
			query = "select obj from NewsLetterEntity obj " 
					+ "where obj.responsavel.id = :ID_RESPONSIBLE "				
					+ "order by obj.id asc")
})
public class NewsLetterEntity extends ClienteBaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_NEWS_LETTER", nullable=false, length=4)
	private Long id;
	
	@Column(name="DESCRICAO", nullable=true, length=255)
	private String descricao;
	
	@Column(name="TITULO", nullable=true, length=255)
	private String title;
	@Lob
	@Column(name = "IMAGEM", nullable = true, length = 10)
	private byte[] image;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_NEWS", nullable = true, length = 10)
	private Date eventDate;
	
	@OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinColumn(name = "ID_RESPONSAVEL", nullable = true)
	private ResponsibleEntity responsavel;
	
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public Long getId() {
		return id;
	}
	public ResponsibleEntity getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(ResponsibleEntity responsavel) {
		this.responsavel = responsavel;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}


}
