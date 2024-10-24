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
@Table(name="RE_PEDESTRE_EQUIPAMENTO")
@NamedQueries({
	@NamedQuery(name  = "PedestreEquipamentoEntity.findAll", 
				query = "select obj "
				      + "from PedestreEquipamentoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "PedestreEquipamentoEntity.findById", 
				query = "select obj from PedestreEquipamentoEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class PedestreEquipamentoEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_PEDESTRE_EQUIPAMENTO", nullable=false, length=4)
	private Long id;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_EQUIPAMENTO", nullable=true)
	private EquipamentoEntity equipamento;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_PEDESTRE", nullable=true)
	private PedestreEntity pedestre;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="VALIDADE", nullable=true, length=11)
	private Date validade;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public EquipamentoEntity getEquipamento() {
		return equipamento;
	}
	public void setEquipamento(EquipamentoEntity equipamento) {
		this.equipamento = equipamento;
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
