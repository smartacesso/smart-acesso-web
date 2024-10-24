package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;

@Entity
@Table(name="TB_PARAMETRO")
@NamedQueries({
	@NamedQuery(name  = "ParametroEntity.findAll", 
				query = "select obj "
				      + "from ParametroEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "ParametroEntity.findAllComplete", 
				query = "select obj from ParametroEntity obj "
				      + "    join fetch obj.cliente c "
				      + "    left join fetch c.integracaoSoc "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "ParametroEntity.findById", 
				query = "select obj from ParametroEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "ParametroEntity.findByClienteIdNome",
				query = "select obj from ParametroEntity obj "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and obj.nome = :NOME "
					  + "and (obj.removido = false or obj.removido is null) "),
	@NamedQuery(name  = "ParametroEntity.findAllByIdClienteAfterLastSync",
				query = "select obj from ParametroEntity obj "
					  + "where obj.cliente.id = :ID_CLIENTE "
					  + "and obj.dataAlteracao >= :LAST_SYNC "
					  + "and (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "ParametroEntity.findClienteIdPreencherCartao",
				query = "select obj from ParametroEntity obj "
					  + "WHERE obj.nome = 'preencher cartao com matricula' "
					  + "and obj.valor = 'true' ")

	
})
@SuppressWarnings("serial")
public class ParametroEntity extends ClienteBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_PARAMETRO", nullable=false, length=4)
	private Long id;
	
	@Column(name="NOME", nullable=true, length=255)
	private String nome;
	
	@Column(name="VALOR", nullable=true, length=255)
	private String valor;
	
	@Transient
	private String nomeAux;
	
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
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public ParametroEntity(){}

	public ParametroEntity(String nome, String valor, ClienteEntity cliente){
		this.nome = nome;
		this.valor = valor;
		this.cliente = cliente;
	}
	
	public String getNomeAux() {
		return nome;
	}
	
	public void setNomeAux(String nomeAux) {
		this.nomeAux = nomeAux;
	}

}
