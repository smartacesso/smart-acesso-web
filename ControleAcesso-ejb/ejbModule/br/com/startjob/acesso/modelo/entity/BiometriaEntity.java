package br.com.startjob.acesso.modelo.entity;

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

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Dedo;

@Entity
@Table(name="TB_BIOMETRIA")
@NamedQueries({
	@NamedQuery(name  = "BiometriaEntity.findAll", 
				query = "select obj "
				      + "from BiometriaEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "BiometriaEntity.findById", 
				query = "select obj from BiometriaEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "BiometriaEntity.findByUserId", 
				query = "select obj from BiometriaEntity obj "
					  + "where obj.pedestre.id = :ID_PEDESTRE order by obj.id asc")
})
@SuppressWarnings("serial")
public class BiometriaEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_BIOMETRIA", nullable=false, length=4)
	private Long id;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_PEDESTRE", nullable=true)
	private PedestreEntity pedestre;
	
	@Enumerated(EnumType.STRING)
	@Column(name="DEDO", nullable=true, length=50)
	private Dedo dedo;
	
	@Lob
	@Column(name="TEMPLATE", nullable=true)
	private byte[] template;
	
	@Lob
	@Column(name="SAMPLE", nullable=true)
	private byte[] sample;
	
	public BiometriaEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public BiometriaEntity(PedestreEntity user, Dedo finger, byte[] template2, byte[] sample2) {
		this.pedestre = user;
		this.dedo = finger;
		this.template = template2;
		this.sample = sample2;
	}
	
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
	public byte[] getTemplate() {
		return template;
	}
	public void setTemplate(byte[] template) {
		this.template = template;
	}
	public byte[] getSample() {
		return sample;
	}
	public void setSample(byte[] sample) {
		this.sample = sample;
	}
	public Dedo getDedo() {
		return dedo;
	}
	public void setDedo(Dedo dedo) {
		this.dedo = dedo;
	}
	
	

}
