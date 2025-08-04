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
@Table(name="TB_IMPORTACAO_SALESIANO")
@NamedQueries({
	@NamedQuery(name  = "ImportacaoSalesianoEntity.findAll", 
				query = "select obj "
				      + "from ImportacaoSalesianoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id desc"),
	@NamedQuery(name  = "ImportacaoSalesianoEntity.findById", 
				query = "select obj from ImportacaoSalesianoEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
})
@SuppressWarnings("serial")
public class ImportacaoSalesianoEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_IMPORTACAO_SALESIANO", nullable = false, length = 4)
	private Long id;
	
	@Column(name = "COD_COLIGADA", nullable = true, length = 100)
	private String codColigada;
	
	@Column(name = "FILTRO_ALUNO", nullable = true, length = 100)
	private String filtroAluno;
	
	@Column(name = "FILTRO_FUNC", nullable = true, length = 100)
	private String filtroFunc;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULT_IMPORTACAO", nullable = true, length = 11)
	private Date lastUpate;
	
	
}
