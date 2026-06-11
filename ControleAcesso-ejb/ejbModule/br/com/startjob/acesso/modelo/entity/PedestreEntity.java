package br.com.startjob.acesso.modelo.entity;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
import org.hibernate.annotations.Type;

import com.senior.services.dto.FuncionarioSeniorDto;
import com.totvs.dto.FuncionarioTotvsDto;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;
import br.com.startjob.acesso.modelo.enumeration.Genero;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoQRCode;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;

@Entity
@Table(name = "TB_PEDESTRE", indexes = { @Index(name = "idx_pedestrian_nome", columnList = "NOME"),
        @Index(name = "idx_pedestrian_nome", columnList = "NOME"),
        @Index(name = "idx_pedestrian_cpf", columnList = "CPF"),
        @Index(name = "idx_pedestrian_rg", columnList = "RG"),
        @Index(name = "idx_pedestrian_matricula", columnList = "MATRICULA"),
        @Index(name = "idx_pedestrian_card_number", columnList = "CARTAO_ACESSO"),
        @Index(name = "idx_pedestrian_cliente", columnList = "ID_CLIENTE"),
        @Index(name = "idx_pedestre_cliente_removido_id", columnList = "ID_CLIENTE, REMOVIDO, ID_PEDESTRE"),
        @Index(name = "idx_pedestre_cliente_removido_id_tipo", columnList = "ID_CLIENTE, REMOVIDO, ID_PEDESTRE, TIPO_PEDESTRE"),
        @Index(name = "idx_pedestre_tipo", columnList = "ID_CLIENTE, REMOVIDO, TIPO_PEDESTRE"),
        @Index(name = "idx_pedestre_tipo_removido", columnList = "REMOVIDO, TIPO_PEDESTRE"),
        @Index(name = "idx_pedestre_empresa", columnList = "ID_EMPRESA"),
        @Index(name = "idx_pedestre_departamento", columnList = "ID_DEPARTAMENTO"),
        @Index(name = "idx_pedestre_centrocusto", columnList = "ID_CENTRO_CUSTO"),
        @Index(name = "idx_pedestre_cargo", columnList = "ID_CARGO"),
        @Index(name = "IX_PEDESTRE_CLIENTE_NOME", columnList = "ID_CLIENTE, NOME"),
        @Index(name = "idx_pedestre_cliente_cpf", columnList = "ID_CLIENTE, CPF"),
        @Index(name = "idx_pedestre_id_temp_cliente", columnList = "ID_TEMP, ID_CLIENTE"),
		@Index(name = "idx_pedestre_login_cliente_removido", columnList = "LOGIN, ID_CLIENTE, REMOVIDO")
})
@NamedQueries({
		@NamedQuery(name = "PedestreEntity.findAll", 
			query = "select obj " + "from PedestreEntity obj "
				+ "where (obj.removido = false or obj.removido is null) " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findById", 
			query = "select obj from PedestreEntity obj "
				+ "where obj.id = :ID order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByIdComplete", 
			query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.endereco en " + " left join fetch obj.empresa emp "
				+ " left join fetch obj.empresaVisitadaRef ev "
				+ " left join fetch obj.departamento dep " + " left join fetch obj.centroCusto cec "
				+ " left join fetch obj.cargo ca " + " left join fetch obj.cliente cli " + " left join obj.regras re "
				+ " left join obj.equipamentos eq " + " left join obj.documentos doc "
				+ " left join obj.biometrias bio " + " left join obj.mensagensPersonalizadas men "
				+ " left join obj.responsaveis res " + " left join fetch obj.cotas c "
				+ "where obj.id = :ID order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findAllComEmpresa", 
			query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.empresa e " + " left join fetch obj.departamento d "
				+ " left join fetch obj.centroCusto cc " + " left join fetch obj.cargo c "
				+ "where (obj.removido = false or obj.removido is null) " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByCpf", 
			query = "select obj from PedestreEntity obj "
				+ "where obj.cpf = :CPF_PEDESTRE " + " and (obj.removido = false or obj.removido is null) "
				+ " order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByRg", 
			query = "select obj from PedestreEntity obj "
				+ "where obj.rg = :RG " + " and (obj.removido = false or obj.removido is null) "
				+ " order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findUltimoPedestreCadastrado", 
			query = "select obj from PedestreEntity obj "
				+ "where obj.cliente.id = :ID_CLIENTE " + "and obj.matricula is not null " + "and obj.matricula <> '' "
				+ "order by obj.id desc"),
		@NamedQuery(name = "PedestreEntity.findById_matricula", 
			query = "select obj from PedestreEntity obj "
				+ "where obj.matricula = :MATRICULA " + "and (obj.removido = false or obj.removido is null) "
				+ "order by obj.id desc"),
		@NamedQuery(name = "PedestreEntity.findByNomePedestre", 
			query = "select obj from PedestreEntity obj "
				+ "where obj.nome = :NOME " + "and (obj.removido = false or obj.removido is null) "
				+ "order by obj.id desc"),
		@NamedQuery(name = "PedestreEntity.findByCardNumber", 
			query = "select obj from PedestreEntity obj "
				+ "where obj.codigoCartaoAcesso = :CARD_NUMBER " + "order by obj.id desc"),
		@NamedQuery(name = "PedestreEntity.findAllPedestresComEmpresa", 
			query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.empresa e " + " left join fetch obj.departamento d " + " left join fetch obj.centroCusto cc "
				+ " left join fetch obj.cargo c " + "where (obj.removido = false or obj.removido is null) "
				+ "and obj.tipo = 'PEDESTRE' " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findPedestresByIdComRegras", 
			query = "select obj from PedestreEntity obj "
				+ "left join fetch obj.endereco " + "left join fetch obj.regras " + "where obj.id = :ID "),
		@NamedQuery(name = "PedestreEntity.findByIdPedestreAndIdCliente", 
			query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.empresa e " + "where obj.id = :ID " + "and obj.cliente.id = :ID_CLIENTE "
				+ "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findAllParaAlterarEmMassa", 
			query = "select obj from PedestreEntity obj "
				+ "where obj.cliente.id = :ID_CLIENTE " + "and obj.tipo = :TIPO " + "and obj.alterarEmMassa = true "
				+ "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByIdTemp", 
			query = "select obj from PedestreEntity obj "
				+ "	left join fetch obj.cliente c " + "where obj.idTemp = :ID_TEMP "
				+ "and obj.cliente.id = :ID_CLIENTE "),
		@NamedQuery(name = "PedestreEntity.findByLogin", 
			query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.cliente c " + "where (obj.removido = false or obj.removido is null) "
				+ "	and lower(c.nomeUnidadeOrganizacional) = lower(:UNIDADE_ORGANIZACIONAL) "
				+ "	and obj.login = :LOGIN "),
		@NamedQuery(name = "PedestreEntity.findByAllStatusLoginPass", 
			query = "select obj from PedestreEntity obj "
				+ " left join fetch obj.cliente c " + "where (obj.removido = false or obj.removido is null) "
				+ "	   and lower(c.nomeUnidadeOrganizacional) = lower(:UNIDADE_ORGANIZACIONAL) "
				+ "	   and obj.login = :LOGIN and obj.senha = :SENHA "),
		@NamedQuery(name = "PedestreEntity.findByMatriculaAndIdEmpresaAndIdCliente", 
			query = "select distinct obj from PedestreEntity obj "
				+ "left join fetch obj.empresa e " + "left join fetch obj.equipamentos eq "
				+ "where obj.matricula = :MATRICULA " + "and e.id = :ID_EMPRESA "
				+ "and (obj.removido = false or obj.removido is null) " + "and obj.cliente.id = :ID_CLIENTE "),
		@NamedQuery(name = "PedestreEntity.findAllAutoAtendimentoAtivo", 
			query = "select obj "
				+ "from PedestreEntity obj "
				+ "where (obj.removido = false or obj.removido is null) and obj.cliente.id = :ID_CLIENTE and obj.autoAtendimento = true "
				+ "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByIdWithEmpRegrasAndHorarios", 
			query = "select distinct obj from PedestreEntity obj "
				+ "left join fetch obj.cliente c " + "left join fetch obj.empresa emp "
				+ "left join fetch obj.empresaVisitadaRef ev "
				+ "left join fetch obj.regras r " + "where obj.id = :ID " + "and obj.cliente.id = :ID_CLIENTE "
				+ "and (obj.removido = false or obj.removido is null) "
				+ "and (r.removido = false or r.removido is null) " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByCPFOnBlur", 
			query = "select distinct obj from PedestreEntity obj "
				+ "where obj.cpf = :CPF " + "and obj.cliente.id = :ID_CLIENTE " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByRGOnBlur", 
			query = "select distinct obj from PedestreEntity obj "
				+ "where obj.rg = :RG " + "and obj.cliente.id = :ID_CLIENTE " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByMatriculaAndIdCliente", 
			query = "select distinct obj from PedestreEntity obj "
				+ "where obj.matricula = :MATRICULA " + "and obj.cliente.id = :ID_CLIENTE "),
		@NamedQuery(name = "PedestreEntity.findByCpfAndIdCliente", 
			query = "select distinct obj from PedestreEntity obj "
				+ "left join fetch obj.cotas " + "left join fetch obj.empresa " + "where obj.cpf = :CPF "
				+ "and obj.cliente.id = :ID_CLIENTE "),
		@NamedQuery(name = "PedestreEntity.findByNomeAndIdCliente", 
			query = "select distinct obj from PedestreEntity obj "
				+ "where obj.nome = :NOME " + "and obj.cliente.id = :ID_CLIENTE "),
		@NamedQuery(name = "PedestreEntity.findByNomeAndCpfAndIdCliente", 
			query = "select distinct obj from PedestreEntity obj "
				+ "where obj.nome = :NOME " + "and obj.cpf = :CPF " + "and obj.cliente.id = :ID_CLIENTE "),
		@NamedQuery(name = "PedestreEntity.findAllAlteradoEmMassa", 
			query = "select distinct obj from PedestreEntity obj "
				+ "where obj.tipo = 'PEDESTRE' " + "and (obj.alterarEmMassa IS NULL OR obj.alterarEmMassa = 1) "
				+ "and obj.cliente.id = :ID_CLIENTE "),
		@NamedQuery(name = "PedestreEntity.findByNomeAndMatriculaAndIdCliente", 
			query = "select distinct obj from PedestreEntity obj "
				+ "where obj.nome = :NOME " + "and obj.matriculaReferencia = :MATRICULA_S "
				+ "and obj.cliente.id = :ID_CLIENTE " 
				+ "and (obj.removido = false or obj.removido is null) "),
		@NamedQuery(name = "PedestreEntity.findAllIds",
			query = "select distinct obj from PedestreEntity obj "
				+ "where obj.id in :IDS " + "and obj.cliente.id = :ID_CLIENTE "
				+ "and (obj.removido = false or obj.removido is null) "
				+ "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findAllComEmpresaOtimizado", 
			query = "select new br.com.startjob.acesso.modelo.entity.PedestreEntity("
				+ "obj.id, obj.matricula, obj.codigoCartaoAcesso, obj.nome, obj.telefone, obj.celular, "
				+ "obj.cpf, obj.rg, obj.tipo, "
				+ "COALESCE(ev.nome, obj.empresaVisitada, e.nome), d.nome, cc.nome, c.nome) "
				+ "from PedestreEntity obj "
				+ " left join  obj.empresa e "
				+ " left join  obj.empresaVisitadaRef ev "
				+ " left join  obj.departamento d "
				+ " left join  obj.centroCusto cc " 
				+ " left join  obj.cargo c "
				+ "where (obj.removido = false or obj.removido is null) " + "order by obj.id asc"),
		@NamedQuery(name = "PedestreEntity.findByNomePedestreLista",
			 query = "select obj from PedestreEntity obj "
			          + "where UPPER(obj.nome) LIKE UPPER(:NOME) "
			          + "and obj.cliente.id = :ID_CLIENTE " 
			          + "and (obj.removido = false or obj.removido is null) "
			          + "order by obj.id desc"),
		@NamedQuery(name = "PedestreEntity.findByNomePedestreAutocomplete",
			 query = "select new br.com.startjob.acesso.modelo.entity.PedestreEntity(obj.id, obj.nome, obj.cpf) "
			          + "from PedestreEntity obj "
			          + "where UPPER(obj.nome) LIKE :NOME "
			          + "and obj.cliente.id = :ID_CLIENTE "
			          + "and (obj.removido = false or obj.removido is null) "
			          + "order by obj.nome asc"),
		@NamedQuery(name = "PedestreEntity.findByLoginOtimizado",
			 query = "select new br.com.startjob.acesso.modelo.entity.PedestreEntity(" +
			            " obj.id, obj.login, obj.senha, obj.perfilApp ) " +
			            "from PedestreEntity obj " +
			            "join obj.cliente c " +
			            "where obj.removido = null " +
			            "and c.nomeUnidadeOrganizacional = :UNIDADE_ORGANIZACIONAL " +
			            "and obj.login = :LOGIN"
			),
		@NamedQuery(name = "PedestreEntity.findDistinctEmpresaVisitadaRefIdByCliente",
				query = "select distinct p.empresaVisitadaRef.id from PedestreEntity p "
						+ "where p.cliente.id = :ID_CLIENTE and p.tipo = :TIPO "
						+ "and p.empresaVisitadaRef.id is not null "
						+ "and (p.removido = false or p.removido is null)"),
		@NamedQuery(name = "PedestreEntity.findDistinctEmpresaVisitadaTextoByCliente",
				query = "select distinct p.empresaVisitada from PedestreEntity p "
						+ "where p.cliente.id = :ID_CLIENTE and p.tipo = :TIPO "
						+ "and p.empresaVisitadaRef is null "
						+ "and p.empresaVisitada is not null and p.empresaVisitada <> '' "
						+ "and (p.removido = false or p.removido is null) "
						+ "order by p.empresaVisitada asc")
})

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
	
	@Column(name = "ACESSO_LIVRE", nullable = true)
	private Boolean acessoLivre;

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
	
	@Column(name = "ID_LOCAL", nullable = true, length = 50)
	private Long idLocal;
	
	@Column(name = "UUID_LOCAL", nullable = true)
	private String uuidLocal;

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

	/** Nome da empresa/destino visitado (somente {@link TipoPedestre#VISITANTE}). */
	@Column(name = "EMPRESA_VISITADA", nullable = true, length = 200)
	private String empresaVisitada;

	/** Empresa cadastrada visitada, quando informada no catálogo do cliente. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_EMPRESA_VISITADA", nullable = true)
	private EmpresaEntity empresaVisitadaRef;

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
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity = HistoricoCotaEntity.class, mappedBy = "pedestre")
	@Fetch(FetchMode.SUBSELECT)
	private List<HistoricoCotaEntity> cotas;

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_USUARIO", nullable = true)
	protected UsuarioEntity usuario;

	@Column(name = "CODIGO_EXTERNO", nullable = true, length = 100)
	private String codigoExterno;

	@Column(name = "CODIGO_PERMISSAO", nullable = true, length = 15)
	private String codigoPermissao;

	@Column(name = "HASH_INTEGRACAO_SENIOR", nullable = true, length = 64)
	private String hashIntegracaoSenior;

	@Column(name = "HASH_ESCALA_SENIOR", nullable = true, length = 64)
	private String hashEscalaSenior;

	@Column(name = "USA_REF_SENIOR", nullable = true, length = 1)
	private String usaRefSenior;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "ESCALA_SENIOR_PENDENTE", nullable = true)
	private Boolean escalaSeniorPendente;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_ULTIMA_VERIFICACAO_ESCALA", nullable = true)
	private Date dataUltimaVerificacaoEscala;
	
	@Column(name = "AUTO_ATENDIMENTO", nullable = true)
	private Boolean autoAtendimento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "AUTO_ATENDIMENTO_AT", nullable = true)
	private Date autoAtendimentoAt;
	
	@ManyToMany
	@JoinTable(
	    name = "pedestre_responsavel",
	    joinColumns = @JoinColumn(name = "id_pedestre"),
	    inverseJoinColumns = @JoinColumn(name = "id_responsavel")
	)
	private List<PedestreEntity> responsaveis;

	@ManyToMany(mappedBy = "responsaveis")
	private List<PedestreEntity> tutorados;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INICIO_PERIODO_AGENDAMENTO", nullable = true, length = 30)
	private Date dataInicioPeriodoAgendamento;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_FIM_PERIODO_AGENDAMENTO", nullable = true, length = 30)
	private Date dataFimPeriodoAgendamento;
	
	@Column(name = "JUSTIFICATIVA_LIBERADO", nullable = true, length = 30)
	private String justificativa;
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name="AGENDAMENTO_LIBERADO", nullable=true, length=11)
	private Boolean agendamentoLiberado;
	
	@Column(name = "MATRICULA_S", nullable = true, length = 100)
	private String matriculaReferencia;
	
	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name="IMPORTADO_EDUCACIONAL", nullable=true, length=11)
	private Boolean importadoEducacional;
	
	@Transient
	private String token;

	@Transient
	private String tempoRenovacaoQRCode;

	@Transient
	private String unidade;

	@Transient
	private CadastroExternoEntity facialAtual;
	
	@Transient
	private boolean selecionadoEmMassa;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PERFIL_APP", nullable = true)
	private PerfilAcessoApp perfilApp;
	
	@Column(name = "DEVICE_KEY", nullable = true, length = 255)
	private String deviceKey;


	public PedestreEntity() {

	}
	
	public PedestreEntity(Long id, String login, String senha, PerfilAcessoApp perfilApp) {
	    this.id = id;
	    this.login = login;
	    this.senha = senha;
	    this.perfilApp = perfilApp;
	}

	/** Projeção leve para autocomplete (sem foto nem joins). */
	public PedestreEntity(Long id, String nome, String cpf) {
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
	}
	
	// CONSTRUTOR OTIMIZADO PARA A TELA (Substitui o DTO)
	public PedestreEntity(Long id, String matricula, String codigoCartaoAcesso, String nome, 
	                      String telefone, String celular, String cpf, String rg, TipoPedestre tipo, 
	                      String nomeEmpresa, String nomeDepartamento, String nomeCentroCusto, String nomeCargo) {
	    this.id = id;
	    this.matricula = matricula;
	    this.codigoCartaoAcesso = codigoCartaoAcesso;
	    this.nome = nome;
	    this.telefone = telefone;
	    this.celular = celular;
	    this.cpf = cpf;
	    this.rg = rg;
	    this.tipo = tipo;

	    // "Falsificamos" os relacionamentos apenas com o nome para o XHTML conseguir ler na grid
	    if (nomeEmpresa != null) {
	        this.empresa = new EmpresaEntity();
	        this.empresa.setNome(nomeEmpresa);
	    }
	    if (nomeDepartamento != null) {
	        this.departamento = new DepartamentoEntity();
	        this.departamento.setNome(nomeDepartamento);
	    }
	    if (nomeCentroCusto != null) {
	        this.centroCusto = new CentroCustoEntity();
	        this.centroCusto.setNome(nomeCentroCusto);
	    }
	    if (nomeCargo != null) {
	        this.cargo = new CargoEntity();
	        this.cargo.setNome(nomeCargo);
	    }
	}

	public PedestreEntity(final FuncionarioSeniorDto funcionarioSeniorDto, final EmpresaEntity empresaEntity, LocalEntity localPadrao) {
		this.nome = funcionarioSeniorDto.getNome();
		this.matricula = funcionarioSeniorDto.getNumeroMatricula() + funcionarioSeniorDto.getEmpresa()+funcionarioSeniorDto.getNumCracha();
		this.telefone = funcionarioSeniorDto.getDddtelefone() + funcionarioSeniorDto.getNumtelefone();
		this.codigoCartaoAcesso = funcionarioSeniorDto.getNumCracha() != null ? funcionarioSeniorDto.getNumCracha() : null;
		this.rg = funcionarioSeniorDto.getRg();
		this.codigoPermissao = funcionarioSeniorDto.getCodPrm(); // codigo permissao
		this.usaRefSenior = funcionarioSeniorDto.getUsaRef();
		this.cliente = empresaEntity.getCliente();
		this.empresa = empresaEntity;
		this.tipo = TipoPedestre.PEDESTRE;
		this.sempreLiberado = false;
		
		if(Objects.nonNull(localPadrao)) {
			this.uuidLocal = localPadrao.getUuid();
		}
		
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
		this.matricula = funcionarioSeniorDto.getNumeroMatricula() + funcionarioSeniorDto.getEmpresa()+funcionarioSeniorDto.getNumCracha();
		this.telefone = funcionarioSeniorDto.getDddtelefone() + funcionarioSeniorDto.getNumtelefone();
		
//		this.codigoCartaoAcesso = funcionarioSeniorDto.getNumCracha();
		
		this.rg = funcionarioSeniorDto.getRg();
		this.codigoPermissao = funcionarioSeniorDto.getCodPrm(); // codigo permissao
		this.usaRefSenior = funcionarioSeniorDto.getUsaRef();
		this.setDataAlteracao(new Date());
		this.sempreLiberado = false;

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
				this.observacoes = "STATUS : ATIVO  \nALTERACAO : " + this.getDataAlteracao();
				this.status = Status.ATIVO;
		}
	}
	
	public void updateFuncionarioTotvs(final FuncionarioTotvsDto funcionarioTotvsDto) {
		this.nome = funcionarioTotvsDto.getNome();
		this.matricula = funcionarioTotvsDto.getMatricula();

		this.tipo = TipoPedestre.PEDESTRE;
		this.setCliente(cliente);
		this.setDataAlteracao(new Date());
		this.setExistente(true);
		this.setSempreLiberado(false);
		
		System.out.println("situacaoFolha=" + funcionarioTotvsDto.getSituacaoFolha()
	    + ", horaInicial=[" + funcionarioTotvsDto.getHoraInicial() + "]"
	    + ", horaFinal=[" + funcionarioTotvsDto.getHoraFinal() + "]");
		
		if(isPermitido(funcionarioTotvsDto)) {
			this.setStatus(Status.ATIVO);
			this.observacoes = "Funcionario ATIVO com situação da folha: " + funcionarioTotvsDto.getSituacaoFolha() + ", situacao de escala: " + funcionarioTotvsDto.getStatusTrabalho() +  ", atualizado dia " + LocalDate.now().toString();
		}else {
			this.setStatus(Status.INATIVO);
			this.observacoes = "Funcionario INVATIVO com situação da folha: " + funcionarioTotvsDto.getSituacaoFolha() + ", situacao de escala: " + funcionarioTotvsDto.getStatusTrabalho() + ", atualizado dia " + LocalDate.now().toString();
		}		
	}

	private boolean isPermitido(FuncionarioTotvsDto funcionarioTotvsDto) {
	    return "ok".equalsIgnoreCase(funcionarioTotvsDto.getSituacaoFolha()) || funcionarioTotvsDto.getSituacaoFolha().isEmpty();
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
	
	public String getEmpresaVisitada() {
		return empresaVisitada;
	}

	public void setEmpresaVisitada(String empresaVisitada) {
		this.empresaVisitada = empresaVisitada;
	}

	public EmpresaEntity getEmpresaVisitadaRef() {
		return empresaVisitadaRef;
	}

	public void setEmpresaVisitadaRef(EmpresaEntity empresaVisitadaRef) {
		this.empresaVisitadaRef = empresaVisitadaRef;
	}

	/** Texto para exibição: empresa visitada ou legado em {@link #empresa}. */
	public String getEmpresaVisitadaExibicao() {
		if (empresaVisitada != null && !empresaVisitada.trim().isEmpty()) {
			return empresaVisitada.trim();
		}
		if (empresaVisitadaRef != null && empresaVisitadaRef.getNome() != null) {
			return empresaVisitadaRef.getNome();
		}
		if (isVisitante() && empresa != null && empresa.getNome() != null) {
			return empresa.getNome();
		}
		return "";
	}

	/**
	 * Copia {@link #empresa} vinculada legada para campos de visita (após migration ou ao abrir cadastro).
	 */
	public void migrarLegadoEmpresaVisitadaSeNecessario() {
		if (!isVisitante()) {
			return;
		}
		boolean semVisitada = (empresaVisitada == null || empresaVisitada.trim().isEmpty())
				&& empresaVisitadaRef == null;
		if (semVisitada && empresa != null && empresa.getId() != null) {
			empresaVisitadaRef = empresa;
			empresaVisitada = empresa.getNome();
			empresa = null;
			departamento = null;
			cargo = null;
			centroCusto = null;
		}
	}

	/**
	 * Grava empresa visitada e remove vínculo organizacional indevido em visitantes.
	 */
	public void aplicarEmpresaVisitadaInformada(String texto, EmpresaEntity refCatalogo) {
		if (!isVisitante()) {
			return;
		}
		empresa = null;
		departamento = null;
		cargo = null;
		centroCusto = null;
		if (refCatalogo != null && refCatalogo.getId() != null) {
			empresaVisitadaRef = refCatalogo;
			empresaVisitada = refCatalogo.getNome() != null ? refCatalogo.getNome().trim() : texto;
			if (empresaVisitada != null) {
				empresaVisitada = empresaVisitada.trim();
			}
			return;
		}
		empresaVisitadaRef = null;
		if (texto != null && !texto.trim().isEmpty()) {
			empresaVisitada = texto.trim();
		} else {
			empresaVisitada = null;
		}
	}

	/** Limpa campos de empresa visitada (uso em colaborador). */
	public void limparEmpresaVisitada() {
		empresaVisitada = null;
		empresaVisitadaRef = null;
	}

	public boolean isVisitante() {
		return TipoPedestre.VISITANTE.equals(this.tipo);
	}
	
	
	public boolean isPedestre() {
		return TipoPedestre.PEDESTRE.equals(this.tipo);
	}
	
	public boolean autoAtendimentoLiberado() {
		return Boolean.TRUE.equals(this.autoAtendimento);
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
		
		if(alterarEmMassa == null)
			return Boolean.FALSE;
		
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

	public String getHashIntegracaoSenior() {
		return hashIntegracaoSenior;
	}

	public void setHashIntegracaoSenior(String hashIntegracaoSenior) {
		this.hashIntegracaoSenior = hashIntegracaoSenior;
	}

	public String getHashEscalaSenior() {
		return hashEscalaSenior;
	}

	public void setHashEscalaSenior(String hashEscalaSenior) {
		this.hashEscalaSenior = hashEscalaSenior;
	}

	public String getUsaRefSenior() {
		return usaRefSenior;
	}

	public void setUsaRefSenior(String usaRefSenior) {
		this.usaRefSenior = usaRefSenior;
	}

	public Boolean getEscalaSeniorPendente() {
		return escalaSeniorPendente;
	}

	public void setEscalaSeniorPendente(Boolean escalaSeniorPendente) {
		this.escalaSeniorPendente = escalaSeniorPendente;
	}

	public Date getDataUltimaVerificacaoEscala() {
		return dataUltimaVerificacaoEscala;
	}

	public void setDataUltimaVerificacaoEscala(Date dataUltimaVerificacaoEscala) {
		this.dataUltimaVerificacaoEscala = dataUltimaVerificacaoEscala;
	}

	public List<HistoricoCotaEntity> getCotas() {
		return cotas;
	}

	public void setCotas(List<HistoricoCotaEntity> cotas) {
		this.cotas = cotas;
	}

	public List<PedestreEntity> getResponsaveis() {
	    return responsaveis;
	}

	public void setResponsaveis(List<PedestreEntity> responsaveis) {
	    this.responsaveis = responsaveis;
	}

	public Boolean getAutoAtendimento() {
		return autoAtendimento;
	}

	public void setAutoAtendimento(Boolean autoAtendimento) {
		this.autoAtendimento = autoAtendimento;
	}

	public Date getAutoAtendimentoAt() {
		return autoAtendimentoAt;
	}

	public void setAutoAtendimentoAt(Date autoAtendimentoAt) {
		this.autoAtendimentoAt = autoAtendimentoAt;
	}

	public Long getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(Long idLocal) {
		this.idLocal = idLocal;
	}

	public Boolean getAcessoLivre() {
		return acessoLivre;
	}

	public void setAcessoLivre(Boolean acessoLivre) {
		this.acessoLivre = acessoLivre;
	}

	public Date getDataInicioPeriodoAgendamento() {
		return dataInicioPeriodoAgendamento;
	}

	public void setDataInicioPeriodoAgendamento(Date dataInicioPeriodoAgendamento) {
		this.dataInicioPeriodoAgendamento = dataInicioPeriodoAgendamento;
	}

	public Date getDataFimPeriodoAgendamento() {
		return dataFimPeriodoAgendamento;
	}

	public void setDataFimPeriodoAgendamento(Date dataFimPeriodoAgendamento) {
		this.dataFimPeriodoAgendamento = dataFimPeriodoAgendamento;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Boolean getAgendamentoLiberado() {
		return agendamentoLiberado;
	}

	public void setAgendamentoLiberado(Boolean agendamentoLiberado) {
		this.agendamentoLiberado = agendamentoLiberado;
	}

	public String getUuidLocal() {
		return uuidLocal;
	}

	public void setUuidLocal(String uuidLocal) {
		this.uuidLocal = uuidLocal;
	}

	public String getMatriculaReferencia() {
		return matriculaReferencia;
	}

	public void setMatriculaReferencia(String matriculaReferencia) {
		this.matriculaReferencia = matriculaReferencia;
	}

	public Boolean getImportadoEducacional() {
		return importadoEducacional;
	}

	public void setImportadoEducacional(Boolean importadoEducacional) {
		this.importadoEducacional = importadoEducacional;
	}

	public boolean isSelecionadoEmMassa() {
	    return selecionadoEmMassa;
	}

	public void setSelecionadoEmMassa(boolean selecionadoEmMassa) {
	    this.selecionadoEmMassa = selecionadoEmMassa;
	}
	
	public PerfilAcessoApp getPerfilApp() {
	    return perfilApp;
	}

	public void setPerfilApp(PerfilAcessoApp perfilApp) {
	    this.perfilApp = perfilApp;
	}
	
	public List<PedestreEntity> getTutorados() {
	    return tutorados;
	}

	public void setTutorados(List<PedestreEntity> tutorados) {
	    this.tutorados = tutorados;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}
	
}