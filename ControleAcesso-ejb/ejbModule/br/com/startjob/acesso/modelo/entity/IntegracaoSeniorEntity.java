package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name="TB_INTEGRACAO_SENIOR")
@NamedQueries({
	@NamedQuery(name  = "IntegracaoSeniorEntity.findAll", 
				query = "select obj "
				      + "from IntegracaoSeniorEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "IntegracaoSeniorEntity.findById", 
				query = "select obj from IntegracaoSeniorEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class IntegracaoSeniorEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_INTEGRACAO_SENIOR", nullable=false, length=4)
	private Long id;
	
	@Column(name="URL", nullable=true, length=255)
	private String url;
	
	@Column(name="USUARIO", nullable=true, length=255)
	private String usuario;
	
	@Column(name="SENHA", nullable=true, length=255)
	private String senha;
	
	@Column(name = "EMPRESA_SENIOR", nullable = true)
	private String numEmp;
	
	@Column(name = "COD_FIL", nullable = true)
	private String codFil;

	@Column(name = "TIP_COL", nullable = true)
	private String tipCol;
	
	@Column(name = "HABILITAR_PERMISSAO", nullable = true)
	private Boolean habilitaPermissao;
	
	@Column(name = "HABILITAR_REGRAS_HORARIO", nullable = true)
	private Boolean habilitaRegrasDeHorario;
	
	@Column(name = "HORA_INICIO", nullable = true)
	private Integer horaInicio;
	
	@Column(name = "QUANTIDADE_EXECUCOES", nullable = true)
	private Integer quantidadeExecucoes;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getHabilitaPermissao() {
		return habilitaPermissao;
	}

	public void setHabilitaPermissao(Boolean habilitaPermissao) {
		this.habilitaPermissao = habilitaPermissao;
	}

	public Boolean getHabilitaRegrasDeHorario() {
		return habilitaRegrasDeHorario;
	}

	public void setHabilitaRegrasDeHorario(Boolean habilitaRegrasDeHorario) {
		this.habilitaRegrasDeHorario = habilitaRegrasDeHorario;
	}

	public boolean isPermissaoHabilitada() {
		return getHabilitaPermissao() != null ? habilitaPermissao : false;
	}

	public boolean isRegraHabilitada() {
		return getHabilitaRegrasDeHorario() != null ? habilitaRegrasDeHorario : false;
	}

	public String getCodFil() {
		return codFil;
	}

	public void setCodFil(String codFil) {
		this.codFil = codFil;
	}

	public String getTipCol() {
		return tipCol;
	}

	public void setTipCol(String tipCol) {
		this.tipCol = tipCol;
	}

	public Integer getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Integer horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Integer getQuantidadeExecucoes() {
		return quantidadeExecucoes;
	}

	public void setQuantidadeExecucoes(Integer quantidadeExecucoes) {
		this.quantidadeExecucoes = quantidadeExecucoes;
	}

	public String getNumEmp() {
		return numEmp;
	}

	public void setNumEmp(String numEmp) {
		this.numEmp = numEmp;
	}
}
