package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;


@Entity
@Table(name="TB_INTEGRACAO_SALESIANO")
@NamedQueries({
	@NamedQuery(name  = "IntegracaoSalesianoEntity.findAll", 
				query = "select obj "
				      + "from IntegracaoSalesianoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id desc"),
	@NamedQuery(name  = "IntegracaoSalesianoEntity.findById", 
				query = "select obj from IntegracaoSalesianoEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
})
@SuppressWarnings("serial")
public class IntegracaoSalesianoEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_IMPORTACAO_SALESIANO", nullable = false, length = 4)
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULT_IMPORTACAO", nullable = true, length = 11)
	private Date lastUpate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getLastUpate() {
		return lastUpate;
	}

	public void setLastUpate(Date lastUpate) {
		this.lastUpate = lastUpate;
	}
	
	
}
