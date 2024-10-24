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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.FilterDef;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;

@Entity
@Table(name="TB_DEPARTAMENTO")
@NamedQueries({
	@NamedQuery(name  = "DepartamentoEntity.findAll", 
				query = "select obj "
				      + "from DepartamentoEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "DepartamentoEntity.findById", 
				query = "select obj from DepartamentoEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "DepartamentoEntity.findAllByIdEmpresa",
				query = "select obj from DepartamentoEntity obj "
					  + "where obj.empresa.id = :ID_EMPRESA "
					  + "and (obj.removido = false or obj.removido is null) " 
					  + "order by obj.id asc"),
	@NamedQuery(name  = "DepartamentoEntity.findAllWithRemovidosByIdEmpresa",
				query = "select obj from DepartamentoEntity obj "
					  + "where obj.empresa.id = :ID_EMPRESA "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "DepartamentoEntity.findByNomeDepartamento",
				query = "select obj from DepartamentoEntity obj "
					  + "where obj.nome = :NOME_DEPARTAMENTO "
					  + "and (obj.removido = false or obj.removido is null) "
					  + " order by obj.id asc")
})
@FilterDef(name="departValido", defaultCondition="(removido = false or removido is null)")
@SuppressWarnings("serial")
public class DepartamentoEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_DEPARTAMENTO", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS", nullable=true, length=100)
	private Status status;
	
	@ManyToOne(cascade={}, fetch=FetchType.LAZY)
	@JoinColumn(name="ID_EMPRESA", nullable=true)
	private EmpresaEntity empresa;
	
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
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public EmpresaEntity getEmpresa() {
		return empresa;
	}
	public void setEmpresa(EmpresaEntity empresa) {
		this.empresa = empresa;
	}
	
}
