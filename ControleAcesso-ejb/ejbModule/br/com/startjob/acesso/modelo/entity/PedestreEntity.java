package br.com.startjob.acesso.modelo.entity;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase;
import com.senior.services.dto.FuncionarioSeniorDto;
import com.totvs.dto.FuncionarioTotvsDto;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Genero;
import br.com.startjob.acesso.modelo.enumeration.Permissoes;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoQRCode;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;

@Entity
@Table(name = "TB_PEDESTRE")
@NamedQueries({
		@NamedQuery(name = "PedestreEntity.findAll", query = "select obj " + "from PedestreEntity obj "
				+ "where (obj.removido = false or obj.removido is null) " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findById", query = "select obj from PedestreEntity obj "
				+ "where obj.id = :ID order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByIdComplete", query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.endereco en " + " left join fetch obj.empresa emp "
				+ " left join fetch obj.departamento dep " + " left join fetch obj.centroCusto cec "
				+ " left join fetch obj.cargo ca " + " left join fetch obj.cliente cli " + " left join obj.regras re "
				+ " left join obj.equipamentos eq " + " left join obj.documentos doc "
				+ " left join obj.biometrias bio " + " left join obj.mensagensPersonalizadas men "
				+ " left join obj.responsavel res " + "where obj.id = :ID order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findAllComEmpresa", query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.empresa e " + "where (obj.removido = false or obj.removido is null) "
				+ "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByCpf", query = "select obj from PedestreEntity obj "
				+ "where obj.cpf = :CPF_PEDESTRE " + " and (obj.removido = false or obj.removido is null) "
				+ " order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findUltimoPedestreCadastrado", query = "select obj from PedestreEntity obj "
				+ "where obj.cliente.id = :ID_CLIENTE " + "and obj.matricula is not null " + "and obj.matricula <> '' "
				+ "order by obj.id desc"),
		@NamedQuery(name = "PedestreEntity.findById_matricula", query = "select obj from PedestreEntity obj "
				+ "where obj.matricula = :MATRICULA " + "order by obj.id desc"),
		@NamedQuery(name = "PedestreEntity.findByNomePedestre", query = "select obj from PedestreEntity obj "
				+ "where obj.nome = :NOME " + "order by obj.id desc"),
		@NamedQuery(name = "PedestreEntity.findByCardNumber", query = "select obj from PedestreEntity obj "
				+ "where obj.codigoCartaoAcesso = :CARD_NUMBER " + "order by obj.id desc"),
		@NamedQuery(name = "PedestreEntity.findAllPedestresComEmpresa", query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.empresa e " + "where (obj.removido = false or obj.removido is null) "
				+ "and obj.tipo = 'PEDESTRE' " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findPedestresByIdComRegras", query = "select obj from PedestreEntity obj "
				+ "left join fetch obj.endereco " + "left join fetch obj.regras " + "where obj.id = :ID "),
		@NamedQuery(name = "PedestreEntity.findByIdPedestreAndIdCliente", query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.empresa e " + "where obj.id = :ID " + "and obj.cliente.id = :ID_CLIENTE "
				+ "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findAllParaAlterarEmMassa", query = "select obj from PedestreEntity obj "
				+ "where obj.cliente.id = :ID_CLIENTE " + "and obj.tipo = :TIPO "
				+ "and (obj.alterarEmMassa is null or obj.alterarEmMassa = true) " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByIdTemp", query = "select obj from PedestreEntity obj "
				+ "	left join fetch obj.cliente c " + "where obj.idTemp = :ID_TEMP "
				+ "and obj.cliente.id = :ID_CLIENTE "),
		@NamedQuery(name = "PedestreEntity.findByLogin", query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.cliente c " + "where (obj.removido = false or obj.removido is null) "
				+ "	and lower(c.nomeUnidadeOrganizacional) = lower(:UNIDADE_ORGANIZACIONAL) "
				+ "	and obj.login = :LOGIN "),
		@NamedQuery(name = "PedestreEntity.findByAllStatusLoginPass", query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.cliente c " + "where (obj.removido = false or obj.removido is null) "
				+ "	   and lower(c.nomeUnidadeOrganizacional) = lower(:UNIDADE_ORGANIZACIONAL) "
				+ "	   and obj.login = :LOGIN and obj.senha = :SENHA "),
		@NamedQuery(name = "PedestreEntity.findByMatriculaAndIdEmpresaAndIdCliente", query = "select obj from PedestreEntity obj "
				+ "left join fetch obj.empresa e " + "left join fetch obj.equipamentos eq "
				+ "where obj.matricula = :MATRICULA " + "and e.id = :ID_EMPRESA "
				+ "and obj.cliente.id = :ID_CLIENTE ") })

@SuppressWarnings("serial")
public class PedestreEntity extends ClienteBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PEDESTRE", nullable = false, length = 4)
	private Long id;

	@Column(name = "ID_TEMP", nullable = true)
	private Long idTemp;

	@Column(name = "NOME", nullable = true, length = 255)
	private String nome;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = true, length = 10)
	private Status status = Status.ATIVO;

	@Column(name = "EMAIL", nullable = true, length = 100)
	private String email;

	@Column(name = "CPF", nullable = true, length = 20)
	private String cpf;

	@Column(name = "RG", nullable = true, length = 20)
	private String rg;

	@Column(name = "TELEFONE", nullable = true, length = 20)
	private String telefone;

	@Column(name = "CELULAR", nullable = true, length = 20)
	private String celular;

	@Column(name = "MATRICULA", nullable = true, length = 200)
	private String matricula;

	@Column(name = "CARTAO_ACESSO", nullable = true, length = 50)
	private String codigoCartaoAcesso;

	@Column(name = "OBSERVACOES", nullable = true, length = 300)
	private String observacoes;

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_QRCODE", nullable = true, length = 100)
	private TipoQRCode tipoQRCode = null;

	@Column(name = "QR_CODE_PARA_ACESSO", nullable = true, length = 100)
	private String qrCodeParaAcesso;

	@Column(name = "SEMPRE_LIBERADO", nullable = true)
	private Boolean sempreLiberado;

	@Column(name = "HABILITAR_TECLADO", nullable = true)
	private Boolean habilitarTeclado;

	@Column(name = "CADASTRO_FACIAL_OBRIGATORIO", nullable = true)
	private Boolean cadastroFacialObrigatorio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ULTIMAS_FOTOS_TIRADAS", nullable = true, length = 30)
	private Date ultimasFotosTiradas;

	@Column(name = "FOTOS_FORAM_EXCLUIDAS", nullable = true)
	private Boolean fotosForamExcluidas;

	@Lob
	@Column(name = "FOTO", nullable = true)
	private byte[] foto;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ALTERACAO_FOTO", nullable = true, length = 10)
	private Date dataAlteracaoFoto = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_NASCIMENTO", nullable = true, length = 10)
	private Date dataNascimento;

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_PEDESTRE", nullable = true, length = 15)
	private TipoPedestre tipo;

	@Enumerated(EnumType.STRING)
	@Column(name = "GENERO", nullable = true, length = 15)
	private Genero genero;

	@Column(name = "ENVIAR_SMS_AO_PASSAR_NA_CATRACA", nullable = true)
	private Boolean enviaSmsAoPassarNaCatraca = false;

	@Column(name = "ALTERAR_EM_MASSA", nullable = true)
	private Boolean alterarEmMassa = null;

	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_ENDERECO", nullable = true)
	private EnderecoEntity endereco;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_EMPRESA", nullable = true)
	private EmpresaEntity empresa;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_DEPARTAMENTO", nullable = true)
	private DepartamentoEntity departamento;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_CENTRO_CUSTO", nullable = true)
	private CentroCustoEntity centroCusto;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_CARGO", nullable = true)
	private CargoEntity cargo;

	@Column(name = "LUXAND_IDENTIFIER", nullable = true)
	private String luxandIdentifier;

	@Column(name = "QUANTIDADE_ACESSO_ANTES_SINC", nullable = true, length = 15)
	private Integer qtdAcessoAntesSinc;

	@Column(name = "LOGIN", nullable = true, length = 100, unique = true)
	private String login;

	@Column(name = "SENHA", nullable = true, length = 255)
	private String senha;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CADASTRO_FOTO_HIKIVISION", nullable = true, length = 30)
	private Date dataCadastroFotoNaHikivision;

	@Transient
	private String senhaLivre;

	@Column(name = "TIPO_ACESSO", nullable = true, length = 255)
	private String tipoAcesso;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity = PedestreRegraEntity.class, mappedBy = "pedestre")
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("dataCriacao desc")
	private List<PedestreRegraEntity> regras;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity = PedestreEquipamentoEntity.class, mappedBy = "pedestre")
	@Fetch(FetchMode.SUBSELECT)
	private List<PedestreEquipamentoEntity> equipamentos;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity = DocumentoEntity.class, mappedBy = "pedestre")
	@Fetch(FetchMode.SUBSELECT)
	private List<DocumentoEntity> documentos;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity = BiometriaEntity.class, mappedBy = "pedestre")
	@Fetch(FetchMode.SUBSELECT)
	private List<BiometriaEntity> biometrias;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity = MensagemEquipamentoEntity.class, mappedBy = "pedestre")
	@Fetch(FetchMode.SUBSELECT)
	private List<MensagemEquipamentoEntity> mensagensPersonalizadas;

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_USUARIO", nullable = true)
	protected UsuarioEntity usuario;

	@Column(name = "CODIGO_EXTERNO", nullable = true, length = 100)
	private String codigoExterno;

	@Column(name = "CODIGO_PERMISSAO", nullable = true, length = 15)
	private String codigoPermissao;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_RESPONSAVEL", nullable = true)
	private ResponsibleEntity responsavel;

	@Transient
	private String token;

	@Transient
	private String tempoRenovacaoQRCode;

	@Transient
	private String unidade;

	@Transient
	private CadastroExternoEntity facialAtual;

	public PedestreEntity() {

	}

	public PedestreEntity(final FuncionarioSeniorDto funcionarioSeniorDto, final EmpresaEntity empresaEntity) {
		LocalDate hoje = LocalDate.now();

		this.nome = funcionarioSeniorDto.getNome();
		this.matricula = funcionarioSeniorDto.getNumeroMatricula();
		this.telefone = funcionarioSeniorDto.getDddtelefone() + funcionarioSeniorDto.getNumtelefone();
		this.codigoCartaoAcesso = funcionarioSeniorDto.getNumCracha();
		this.rg = funcionarioSeniorDto.getRg();
		this.codigoPermissao = funcionarioSeniorDto.getCodPrm(); // codigo permissao
		this.cliente = empresaEntity.getCliente();
		this.empresa = empresaEntity;
		this.tipo = TipoPedestre.PEDESTRE;

		if (Objects.nonNull(funcionarioSeniorDto.getDatDem())) {
			this.observacoes = "DATA DEMISSAO : " + funcionarioSeniorDto.getDatDem() + " | MOTIVO : DEMISSAO";
			this.status = Status.INATIVO;
		} else if (Objects.nonNull(funcionarioSeniorDto.getDatAfa())) {
			this.observacoes = "DATA AFASTAMENTO: " + funcionarioSeniorDto.getDatAfa() + " | MOTIVO: afastamento";
			if (Objects.nonNull(funcionarioSeniorDto.getDesAfa())) {
				this.observacoes += " | DESCRICAO: " + funcionarioSeniorDto.getDesAfa();
			}
			this.status = Status.INATIVO;
		} else {
			this.status = Status.ATIVO;
		}
	}

	public void updateFuncionarioSenior(final FuncionarioSeniorDto funcionarioSeniorDto,
			final EmpresaEntity empresaEntity) {

		this.nome = funcionarioSeniorDto.getNome();
		this.matricula = funcionarioSeniorDto.getNumeroMatricula();
		this.telefone = funcionarioSeniorDto.getDddtelefone() + funcionarioSeniorDto.getNumtelefone();
		this.codigoCartaoAcesso = funcionarioSeniorDto.getNumCracha();
		this.rg = funcionarioSeniorDto.getRg();
		this.codigoPermissao = funcionarioSeniorDto.getCodPrm(); // codigo permissao
		this.setDataAlteracao(new Date());

		if (Objects.nonNull(funcionarioSeniorDto.getDatDem())) {
			this.observacoes = "DATA DEMISSAO : " + funcionarioSeniorDto.getDatDem() + " | MOTIVO : DEMISSAO";
			this.status = Status.INATIVO;
		} else if (Objects.nonNull(funcionarioSeniorDto.getDatAfa())) {
			this.observacoes = "DATA AFASTAMENTO: " + funcionarioSeniorDto.getDatAfa() + " | MOTIVO: afastamento";
			if (Objects.nonNull(funcionarioSeniorDto.getDesAfa())) {
				this.observacoes += " | DESCRICAO: " + funcionarioSeniorDto.getDesAfa();
			}
			this.status = Status.INATIVO;
		} else {
			// Atualiza para ativo somente se o status atual não for INATIVO
			if (this.status != Status.INATIVO) {
				this.observacoes = "STATUS : ATIVO";
				this.status = Status.ATIVO;
			}
		}

	}
	
	public void updateFuncionarioTotvs(final FuncionarioTotvsDto funcionarioTotvsDto) {
		this.nome = funcionarioTotvsDto.getName();
		this.matricula = funcionarioTotvsDto.getCode();
		this.email = funcionarioTotvsDto.getEmail();
		this.cpf = funcionarioTotvsDto.getEmployeeCpf();
		
		
		if(funcionarioTotvsDto.getEmployeeSituation().trim().equals("")) {
			this.sempreLiberado = true;
			this.status =  Status.ATIVO;
			this.observacoes =  "Importado dia " + LocalDate.now().toString();

		}else {
			this.sempreLiberado = false;
			this.status =  Status.INATIVO;
			this.observacoes = "Funcionario com situação : " + funcionarioTotvsDto.getEmployeeSituation();
		}		
	}

	public String getAllPhonesFormatted() {
		String tel = getTelefone();
		String cel = getCelular();

		if (isNullOrEmpty(tel) && isNullOrEmpty(cel)) {
			return "--";
		}
		if (isNullOrEmpty(tel))
			return cel;

		if (isNullOrEmpty(cel))
			return tel;

		return tel + " / " + cel;
	}

	private boolean isNullOrEmpty(String string) {
		if (string == null)
			return true;
		if (string.isEmpty())
			return true;
		return false;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getCodigoCartaoAcesso() {
		return codigoCartaoAcesso;
	}

	public void setCodigoCartaoAcesso(String codigoCartaoAcesso) {
		this.codigoCartaoAcesso = codigoCartaoAcesso;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public byte[] getFoto() {
		return foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public TipoPedestre getTipo() {
		return tipo;
	}

	public void setTipo(TipoPedestre tipo) {
		this.tipo = tipo;
	}

	public EnderecoEntity getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoEntity endereco) {
		this.endereco = endereco;
	}

	public EmpresaEntity getEmpresa() {
		return empresa;
	}

	public void setEmpresa(EmpresaEntity empresa) {
		this.empresa = empresa;
	}

	public DepartamentoEntity getDepartamento() {
		return departamento;
	}

	public void setDepartamento(DepartamentoEntity departamento) {
		this.departamento = departamento;
	}

	public CentroCustoEntity getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(CentroCustoEntity centroCusto) {
		this.centroCusto = centroCusto;
	}

	public CargoEntity getCargo() {
		return cargo;
	}

	public void setCargo(CargoEntity cargo) {
		this.cargo = cargo;
	}

	public List<PedestreRegraEntity> getRegras() {
		return regras;
	}

	public void setRegras(List<PedestreRegraEntity> regras) {
		this.regras = regras;
	}

	public List<PedestreEquipamentoEntity> getEquipamentos() {
		return equipamentos;
	}

	public void setEquipamentos(List<PedestreEquipamentoEntity> equipamentos) {
		this.equipamentos = equipamentos;
	}

	public List<DocumentoEntity> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocumentoEntity> documentos) {
		this.documentos = documentos;
	}

	public List<BiometriaEntity> getBiometrias() {
		return biometrias;
	}

	public void setBiometrias(List<BiometriaEntity> biometrias) {
		this.biometrias = biometrias;
	}

	public List<MensagemEquipamentoEntity> getMensagensPersonalizadas() {
		return mensagensPersonalizadas;
	}

	public void setMensagensPersonalizadas(List<MensagemEquipamentoEntity> mensagensPersonalizadas) {
		this.mensagensPersonalizadas = mensagensPersonalizadas;
	}

	public Genero getGenero() {
		return genero;
	}

	public void setGenero(Genero genero) {
		this.genero = genero;
	}

	public Date getDataAlteracaoFoto() {
		return dataAlteracaoFoto;
	}

	public void setDataAlteracaoFoto(Date dataAlteracaoFoto) {
		this.dataAlteracaoFoto = dataAlteracaoFoto;
	}

	public Boolean getAlterarEmMassa() {

		if (alterarEmMassa == null)
			return Boolean.TRUE;

		return alterarEmMassa;
	}

	public void setAlterarEmMassa(Boolean alterarEmMassa) {
		this.alterarEmMassa = alterarEmMassa;
	}

	public Boolean getSempreLiberado() {
		return sempreLiberado;
	}

	public void setSempreLiberado(Boolean sempreLiberado) {
		this.sempreLiberado = sempreLiberado;
	}

	public Boolean getHabilitarTeclado() {
		return habilitarTeclado;
	}

	public void setHabilitarTeclado(Boolean habilitarTeclado) {
		this.habilitarTeclado = habilitarTeclado;
	}

	public Date getUltimasFotosTiradas() {
		return ultimasFotosTiradas;
	}

	public void setUltimasFotosTiradas(Date ultimasFotosTiradas) {
		this.ultimasFotosTiradas = ultimasFotosTiradas;
	}

	public Boolean getFotosForamExcluidas() {
		return fotosForamExcluidas;
	}

	public void setFotosForamExcluidas(Boolean fotosForamExcluidas) {
		this.fotosForamExcluidas = fotosForamExcluidas;
	}

	public Long getIdTemp() {
		return idTemp;
	}

	public void setIdTemp(Long idTemp) {
		this.idTemp = idTemp;
	}

	public String getQrCodeParaAcesso() {
		return qrCodeParaAcesso;
	}

	public void setQrCodeParaAcesso(String qrCodeParaAcesso) {
		this.qrCodeParaAcesso = qrCodeParaAcesso;
	}

	public Boolean getCadastroFacialObrigatorio() {
		return cadastroFacialObrigatorio;
	}

	public void setCadastroFacialObrigatorio(Boolean cadastroFacialObrigatorio) {
		this.cadastroFacialObrigatorio = cadastroFacialObrigatorio;
	}

	public Boolean getEnviaSmsAoPassarNaCatraca() {
		return enviaSmsAoPassarNaCatraca;
	}

	public void setEnviaSmsAoPassarNaCatraca(Boolean enviaSmsAoPassarNaCatraca) {
		this.enviaSmsAoPassarNaCatraca = enviaSmsAoPassarNaCatraca;
	}

	public String getLuxandIdentifier() {
		return luxandIdentifier;
	}

	public void setLuxandIdentifier(String luxandIdentifier) {
		this.luxandIdentifier = luxandIdentifier;
	}

	public Integer getQtdAcessoAntesSinc() {
		return qtdAcessoAntesSinc;
	}

	public void setQtdAcessoAntesSinc(Integer qtdAcessoAntesSinc) {
		this.qtdAcessoAntesSinc = qtdAcessoAntesSinc;
	}

	public UsuarioEntity getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioEntity usuario) {
		this.usuario = usuario;
	}

	public String getCodigoExterno() {
		return codigoExterno;
	}

	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}

	public TipoQRCode getTipoQRCode() {
		return tipoQRCode;
	}

	public void setTipoQRCode(TipoQRCode tipoQRCode) {
		this.tipoQRCode = tipoQRCode;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTipoAcesso() {
		return tipoAcesso;
	}

	public void setTipoAcesso(String tipoAcesso) {
		this.tipoAcesso = tipoAcesso;
	}

	public String getSenhaLivre() {
		return senhaLivre;
	}

	public void setSenhaLivre(String senhaLivre) {

		if (senhaLivre != null) {
			try {
				senha = EncryptionUtils.encrypt(senhaLivre);
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.senhaLivre = senhaLivre;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTempoRenovacaoQRCode() {
		return tempoRenovacaoQRCode;
	}

	public void setTempoRenovacaoQRCode(String tempoRenovacaoQRCode) {
		this.tempoRenovacaoQRCode = tempoRenovacaoQRCode;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public CadastroExternoEntity getFacialAtual() {
		return facialAtual;
	}

	public void setFacialAtual(CadastroExternoEntity facialAtual) {
		this.facialAtual = facialAtual;
	}

	public Date getDataCadastroFotoNaHikivision() {
		return dataCadastroFotoNaHikivision;
	}

	public void setDataCadastroFotoNaHikivision(Date dataCadastroFotoNaHikivision) {
		this.dataCadastroFotoNaHikivision = dataCadastroFotoNaHikivision;
	}

	public String getCodigoPermissao() {
		return codigoPermissao;
	}

	public void setCodigoPermissao(String codigoPermissao) {
		this.codigoPermissao = codigoPermissao;
	}

	public ResponsibleEntity getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(ResponsibleEntity responsavel) {
		this.responsavel = responsavel;

	}

}