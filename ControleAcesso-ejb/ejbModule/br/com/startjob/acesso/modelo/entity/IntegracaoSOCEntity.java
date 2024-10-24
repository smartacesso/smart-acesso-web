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
@Table(name="TB_INTEGRACAO_SOC")
@NamedQueries({
	@NamedQuery(name  = "IntegracaoSOCEntity.findAll", 
				query = "select obj "
				      + "from IntegracaoSOCEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "IntegracaoSOCEntity.findById", 
				query = "select obj from IntegracaoSOCEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
	
})
@SuppressWarnings("serial")

public class IntegracaoSOCEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_INTEGRACAO_SOC", nullable=false, length=4)
	private Long id;
	
	@Column(name="EMPRESA", nullable=true, length=255)
	private String empresa;
	
	@Column(name="CODIGO", nullable=true, length=255)
	private String codigo;
	
	@Column(name="CHAVE", nullable=true, length=255)
	private String chave;
	
	@Column(name="CODIGO_EXTERNO", nullable=true, length=255)
	private String codigoExterno;
	
	@Column(name="USUARIO_SOC", nullable=true, length=255)
	private String usuarioSoc;
	
	@Column(name="SENHA_SOC", nullable=true, length=255)
	private String senhaSoc;
	
	@Column(name="CODIGO_EMPRESA", nullable=true, length=255)
	private String codigoEmpresa;
	
	@Column(name="CODIGO_SEQUENCIAL_FICHA", nullable=true, length=255)
	private String codigoSequencialFicha;
	
	@Column(name="CODIGO_GED", nullable=true, length=255)
	private String codigoGed;

	@Column(name="CODIGO_FUNCIONARIO", nullable=true, length=255)
	private String codigoFuncionario;
	
	@Column(name="CHAVE_ACESSO", nullable=true, length=255)
	private String chaveAcesso;
	
	@Column(name="CODIGO_RESPONSAVEL", nullable=true, length=255)
	private String codigoResponsavel;
	
	@Column(name="CODIGO_USUARIO", nullable=true, length=255)
	private String codigoUsuario;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getCodigoExterno() {
		return codigoExterno;
	}

	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}

	public String getUsuarioSoc() {
		return usuarioSoc;
	}

	public void setUsuarioSoc(String usuarioSoc) {
		this.usuarioSoc = usuarioSoc;
	}

	public String getSenhaSoc() {
		return senhaSoc;
	}

	public void setSenhaSoc(String senhaSoc) {
		this.senhaSoc = senhaSoc;
	}

	public String getCodigoEmpresa() {
		return codigoEmpresa;
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}

	public String getCodigoSequencialFicha() {
		return codigoSequencialFicha;
	}

	public void setCodigoSequencialFicha(String codigoSequencialFicha) {
		this.codigoSequencialFicha = codigoSequencialFicha;
	}

	public String getCodigoGed() {
		return codigoGed;
	}

	public void setCodigoGed(String codigoGed) {
		this.codigoGed = codigoGed;
	}

	public String getCodigoFuncionario() {
		return codigoFuncionario;
	}

	public void setCodigoFuncionario(String codigoFuncionario) {
		this.codigoFuncionario = codigoFuncionario;
	}

	public String getChaveAcesso() {
		return chaveAcesso;
	}

	public void setChaveAcesso(String chaveAcesso) {
		this.chaveAcesso = chaveAcesso;
	}

	public String getCodigoResponsavel() {
		return codigoResponsavel;
	}

	public void setCodigoResponsavel(String codigoResponsavel) {
		this.codigoResponsavel = codigoResponsavel;
	}

	public String getCodigoUsuario() {
		return codigoUsuario;
	}

	public void setCodigoUsuario(String codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}

}
