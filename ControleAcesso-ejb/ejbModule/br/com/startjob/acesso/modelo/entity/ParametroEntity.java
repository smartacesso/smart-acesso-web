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

	@Transient
	private TipoEditorParametro tipoEditor;
	
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
		invalidarTipoEditor();
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
		if (nomeAux != null && !nomeAux.isEmpty()) {
			return nomeAux;
		}
		return nome;
	}

	public void setNomeAux(String nomeAux) {
		this.nomeAux = nomeAux;
	}

	public TipoEditorParametro getTipoEditor() {
		if (tipoEditor == null) {
			tipoEditor = TipoEditorParametroResolver.resolver(nome);
		}
		return tipoEditor;
	}

	public void setTipoEditor(TipoEditorParametro tipoEditor) {
		this.tipoEditor = tipoEditor;
	}

	public void invalidarTipoEditor() {
		this.tipoEditor = null;
	}

	public boolean isEditorBooleano() {
		return getTipoEditor() == TipoEditorParametro.BOOLEAN;
	}

	public boolean isEditorHorarioSoc() {
		return getTipoEditor() == TipoEditorParametro.HORARIO_SOC;
	}

	public boolean isEditorQtdeDigitosCartao() {
		return getTipoEditor() == TipoEditorParametro.QTDE_DIGITOS_CARTAO;
	}

	public boolean isEditorDiasValidadeLinkFacial() {
		return getTipoEditor() == TipoEditorParametro.DIAS_VALIDADE_LINK_FACIAL;
	}

	public boolean isEditorMinutosQrCodeDinamico() {
		return getTipoEditor() == TipoEditorParametro.MINUTOS_QR_CODE_DINAMICO;
	}

	public boolean isEditorHorasCadastroFacialRemoto() {
		return getTipoEditor() == TipoEditorParametro.HORAS_CADASTRO_FACIAL_REMOTO;
	}

	public boolean isEditorToleranciaEntradaSaida() {
		return getTipoEditor() == TipoEditorParametro.TOLERANCIA_ENTRADA_SAIDA;
	}

	public boolean isEditorTextoFormato() {
		return getTipoEditor() == TipoEditorParametro.TEXTO_FORMATO;
	}

	public boolean isEditorTextoLocalPadrao() {
		return getTipoEditor() == TipoEditorParametro.TEXTO_LOCAL_PADRAO;
	}

	public boolean isEditorTextoLargo() {
		return getTipoEditor() == TipoEditorParametro.TEXTO_LARGO;
	}

	public boolean isEditorSelectLimiteDigitais() {
		return getTipoEditor() == TipoEditorParametro.SELECT_LIMITE_DIGITAIS;
	}

	public boolean isEditorSelectTipoQrPadrao() {
		return getTipoEditor() == TipoEditorParametro.SELECT_TIPO_QR_PADRAO;
	}

	public boolean isEditorCamposObrigatorios() {
		return getTipoEditor() == TipoEditorParametro.CAMPOS_OBRIGATORIOS;
	}

	/** Valor booleano para checkbox (parâmetros sim/não). */
	public boolean isValorAtivo() {
		return "true".equalsIgnoreCase(valor);
	}

	public void setValorAtivo(boolean ativo) {
		this.valor = ativo ? "true" : "false";
	}

	public Integer getValorComoInteiro() {
		if (valor == null || valor.trim().isEmpty()) {
			return null;
		}
		try {
			return Integer.valueOf(valor.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void setValorComoInteiro(Integer inteiro) {
		this.valor = inteiro == null ? "" : String.valueOf(inteiro);
	}

}
