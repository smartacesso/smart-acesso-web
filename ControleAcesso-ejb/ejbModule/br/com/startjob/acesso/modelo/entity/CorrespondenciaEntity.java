package br.com.startjob.acesso.modelo.entity;

import java.util.Date;
import javax.persistence.*;

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name = "TB_CORRESPONDENCIA")
@SequenceGenerator(name = "SEQ_CORRESPONDENCIA", sequenceName = "SEQ_CORRESPONDENCIA", allocationSize = 1)
@NamedQueries({
	@NamedQuery(
		    name = "CorrespondenciaEntity.findAll",
		    query = "select obj from CorrespondenciaEntity obj "
		          + "left join fetch obj.destinatario d "
		          + "where (obj.removido = false or obj.removido is null) "
		          + "order by obj.id asc")
})
public class CorrespondenciaEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_CORRESPONDENCIA")
    private Long id;

 // O morador que vai receber
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDESTRE", nullable = false)
    private PedestreEntity destinatario;

    // Quem na portaria recebeu (Usuario do sistema)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_RECEBEU")
    private UsuarioEntity usuarioRecebeu;

    @Column(name = "TIPO_VOLUME")
    private String tipo; // CARTA, PACOTE, DOCUMENTO, OUTROS

    @Column(name = "CODIGO_RASTREIO", length = 100)
    private String codigoRastreio;

    @Column(name = "ID_ARQUIVO_FOTO")
    private Long idArquivoFoto;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_RECEBIMENTO")
    private Date dataRecebimento;

    @Column(name = "CONFIRMA_RETIRADA", length = 1)
    private String confirmaRetirada = "N"; 

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_RETIRADA")
    private Date dataRetirada;

    @Column(name = "OBSERVACAO", length = 500)
    private String observacao;

    // --- NOVO CAMPO EM BYTES ---
    @Lob
    @Basic(fetch = FetchType.LAZY) // Lazy para não carregar a foto pesada em consultas de lista
    @Column(name = "FOTO_VOLUME")
    private byte[] fotoVolume;

    // Funcionalidade útil: Armazenar o formato (jpg/png) caso precise converter depois
    @Column(name = "EXTENSAO_FOTO", length = 10)
    private String extensaoFoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PedestreEntity getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(PedestreEntity destinatario) {
		this.destinatario = destinatario;
	}

	public UsuarioEntity getUsuarioRecebeu() {
		return usuarioRecebeu;
	}

	public void setUsuarioRecebeu(UsuarioEntity usuarioRecebeu) {
		this.usuarioRecebeu = usuarioRecebeu;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCodigoRastreio() {
		return codigoRastreio;
	}

	public void setCodigoRastreio(String codigoRastreio) {
		this.codigoRastreio = codigoRastreio;
	}

	public Long getIdArquivoFoto() {
		return idArquivoFoto;
	}

	public void setIdArquivoFoto(Long idArquivoFoto) {
		this.idArquivoFoto = idArquivoFoto;
	}

	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public String getConfirmaRetirada() {
		return confirmaRetirada;
	}

	public void setConfirmaRetirada(String confirmaRetirada) {
		this.confirmaRetirada = confirmaRetirada;
	}

	public Date getDataRetirada() {
		return dataRetirada;
	}

	public void setDataRetirada(Date dataRetirada) {
		this.dataRetirada = dataRetirada;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public byte[] getFotoVolume() {
		return fotoVolume;
	}

	public void setFotoVolume(byte[] fotoVolume) {
		this.fotoVolume = fotoVolume;
	}

	public String getExtensaoFoto() {
		return extensaoFoto;
	}

	public void setExtensaoFoto(String extensaoFoto) {
		this.extensaoFoto = extensaoFoto;
	}

	public ClienteEntity getCliente() {
		return cliente;
	}

	public void setCliente(ClienteEntity cliente) {
		this.cliente = cliente;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}