package br.com.startjob.acesso.modelo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;

@Entity
@Table(name = "TB_ACESSO", indexes = {
		@Index(name = "idx_pedestre_data", columnList = "ID_PEDESTRE, DATA"),
		@Index(name = "idx_pedestre_tipo", columnList = "ID_PEDESTRE, TIPO"),
		@Index(name = "idx_cliente_data", columnList = "ID_CLIENTE, DATA, SENTIDO, TIPO"),
		@Index(name = "idx_pedestre_sentido_tipo", columnList = "ID_PEDESTRE, SENTIDO, TIPO, DATA"),
		@Index(name = "idx_only_pedestre", columnList = "ID_PEDESTRE")})
@NamedQueries({
		@NamedQuery(name = "AcessoEntity.findAll", query = "select obj " + "from AcessoEntity obj "
				+ "where (obj.removido = false or obj.removido is null) " + "order by obj.id asc"),
		@NamedQuery(name = "AcessoEntity.findAllComPedestre", query = "select obj " + "from AcessoEntity obj "
				+ "join fetch obj.pedestre p " + "where (obj.removido = false or obj.removido is null) "
				+ "order by obj.data desc"),
		@NamedQuery(name = "AcessoEntity.findById", query = "select obj from AcessoEntity obj "
				+ "where obj.id = :ID order by obj.id asc"),
		@NamedQuery(name = "AcessoEntity.findAllComPedestreEmpresa", query = "select obj " + "from AcessoEntity obj "
				+ "join fetch obj.pedestre p " + "left join fetch p.empresa "
				+ "where (obj.removido = false or obj.removido is null) " + "order by obj.data desc"),
		@NamedQuery(name = "AcessoEntity.findAllComPedestreEmpresaECargo", query = "select obj "
				+ "from AcessoEntity obj " + "join fetch obj.pedestre p " + "left join fetch p.empresa "
				+ "left join fetch p.cargo " + "where (obj.removido = false or obj.removido is null) "
				+ "order by obj.data desc"),
		@NamedQuery(name = "AcessoEntity.findAllComPedestreNulo", query = "select obj " + "from AcessoEntity obj "
				+ "where obj.pedestre = null " + "and (obj.removido = false or obj.removido is null) "
				+ "order by obj.data desc"),
		@NamedQuery(name = "AcessoEntity.findOcupacaoPorHora", query = "select new br.com.startjob.acesso.modelo.entity.AcessoEntity(hour(obj.data), count(obj.id)) "
				+ "from AcessoEntity obj " + "where obj.sentido = 'ENTRADA' " + "  and obj.tipo <> 'INATIVO' "
				+ "  and obj.cliente.id = :ID_CLIENTE " + "  and date(obj.data) = :DATA "
				+ "  and (lower(obj.pedestre.nome) like lower(concat('%', :NOME_PEDESTRE, '%')) or :NOME_PEDESTRE = null) "
				+ "  and (obj.pedestre.empresa.id = :ID_EMPRESA or :ID_EMPRESA = null) "
				+ "  and (obj.equipamento = :EQUIPAMENTO or :EQUIPAMENTO = null) " + "group by hour(obj.data) "),
		@NamedQuery(name = "AcessoEntity.findOcupacaoPorHoraSqlServer", query = "select new br.com.startjob.acesso.modelo.entity.AcessoEntity(hour(obj.data), count(obj.id)) "
				+ "from AcessoEntity obj " + "where obj.sentido = 'ENTRADA' " + "  and obj.tipo <> 'INATIVO' "
				+ "  and obj.cliente.id = :ID_CLIENTE "
				+ "  and format(obj.data, 'YYYY-MM-DD') = format(:DATA, 'YYYY-MM-DD') "
				+ "  and (lower(obj.pedestre.nome) like lower(:NOME_PEDESTRE) or :NOME_PEDESTRE = null) "
				+ "  and (obj.pedestre.empresa.id = :ID_EMPRESA or :ID_EMPRESA = null) "
				+ "  and (obj.equipamento = :EQUIPAMENTO or :EQUIPAMENTO = null) " + "group by hour(obj.data) "),
		@NamedQuery(name = "AcessoEntity.findAllPedestresSemSaida", query = "select obj " + "from AcessoEntity obj "
				+ "join fetch obj.pedestre p " + "left join fetch p.empresa e " + "where p.id != null "
				+ "	and obj.sentido = 'ENTRADA' " + "	and obj.tipo = 'ATIVO' " + "	and (select count(saida.id) "
				+ "		from AcessoEntity saida " + "		join saida.pedestre pe " + "		where pe.id = p.id "
				+ "			and saida.sentido = 'SAIDA' " + "			and saida.data > obj.data) = 0 "
				+ "order by obj.data desc "),
		@NamedQuery(name = "AcessoEntity.findLastAccessIndefinidoByIdPedestre", query = "select new br.com.startjob.acesso.modelo.entity.AcessoEntity(obj.id, obj.data) "
				+ "from AcessoEntity obj " + "	join obj.pedestre p " + "where p.id = :ID_PEDESTRE "
				+ "and obj.tipo = 'INDEFINIDO' " + "order by obj.id desc"),
		@NamedQuery(name = "AcessoEntity.findLastAccessByIdAndCurrentLastAccess", query = "select new br.com.startjob.acesso.modelo.entity.AcessoEntity(obj.id) "
				+ "from AcessoEntity obj " + "	join obj.pedestre p " + "where p.id = :ID_PEDESTRE "
				+ "and obj.data = :DATA "),
		@NamedQuery(name = "AcessoEntity.findAllByIdPedestre",
				    query = "select obj from AcessoEntity obj " +
				            "where obj.pedestre.id = :ID_PEDESTRE " +
				            "AND (:DATA_INICIO IS NULL OR obj.data >= :DATA_INICIO) " +
				            "AND (:DATA_FIM IS NULL OR obj.data <= :DATA_FIM) " +
				            "and (obj.removido = false or obj.removido is null) " +
				            "order by obj.data desc")
})
@SuppressWarnings("serial")
public class AcessoEntity extends ClienteBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_ACESSO", nullable = false, length = 4)
	private Long id;

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_PEDESTRE", nullable = true)
	private PedestreEntity pedestre;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA", nullable = true, length = 11)
	private Date data;

	@Column(name = "SENTIDO", nullable = true, length = 100)
	private String sentido;

	@Column(name = "EQUIPAMENTO", nullable = true, length = 100)
	private String equipamento;

	@Column(name = "TIPO", nullable = true, length = 100)
	private String tipo;

	@Column(name = "LOCAL", nullable = true, length = 100)
	private String local;

	@Column(name = "RAZAO", nullable = true, length = 100)
	private String razao;

	@Column(name = "CARTAO_ACESSO_RECEBIDO", nullable = true, length = 100)
	private String cartaoAcessoRecebido;

	@Column(name = "IS_SINCRONIZADO", nullable = true)
	private Boolean isSincronizado;

	@Transient
	private Long qtdePedestresHora;

	@Transient
	private Integer hora;

	@Transient
	private Boolean bloquearSaida;

	@Transient
	private Long idPedestre;

	public AcessoEntity() {

	}

	public AcessoEntity(Long id) {
		this.id = id;
	}

	public AcessoEntity(Long id, Date data) {
		this.id = id;
		this.data = data;
	}

	public AcessoEntity(UsuarioEntity user, PedestreEntity pedestre, Date date, String tipo, String location,
			String reason, String sentido, String equipamento) {
		if (user != null) {
			this.cliente = user.getCliente();
		}

		this.pedestre = pedestre;
		this.data = date;
		this.tipo = tipo;
		this.local = location;
		this.razao = reason;
		this.sentido = sentido;
		this.equipamento = equipamento;
	}

	public AcessoEntity(AcessoEntity acesso, PedestreEntity pedestre) {
		this.sentido = acesso.sentido;
		this.pedestre = pedestre;
	}

	public AcessoEntity(UsuarioEntity user, PedestreEntity pedestre, Date date, String tipo, String location,
			String reason, String sentido, String equipamento, Boolean bloquearSaida) {
		if (user != null) {
			this.cliente = user.getCliente();
		}

		this.pedestre = pedestre;
		this.data = date;
		this.tipo = tipo;
		this.local = location;
		this.razao = reason;
		this.sentido = sentido;
		this.equipamento = equipamento;
		this.bloquearSaida = bloquearSaida;
	}

	public AcessoEntity(UsuarioEntity user, Long idPedestre, Date date, String tipo, String location, String reason,
			String sentido, String equipamento, Boolean bloquearSaida) {
		if (user != null) {
			this.cliente = user.getCliente();
		}

		this.idPedestre = idPedestre;
		this.data = date;
		this.tipo = tipo;
		this.local = location;
		this.razao = reason;
		this.sentido = sentido;
		this.equipamento = equipamento;
		this.bloquearSaida = bloquearSaida;
	}

	public AcessoEntity(Integer hora, Long qtdePedestresHora) {
		this.hora = hora;
		this.qtdePedestresHora = qtdePedestresHora;
	}

	public AcessoEntity(String hora, Long qtdePedestresHora) {
		this.hora = Integer.valueOf(hora);
		this.qtdePedestresHora = qtdePedestresHora;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PedestreEntity getPedestre() {
		return pedestre;
	}

	public void setPedestre(PedestreEntity pedestre) {
		this.pedestre = pedestre;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getSentido() {
		return sentido;
	}

	public void setSentido(String sentido) {
		this.sentido = sentido;
	}

	public String getEquipamento() {
		return equipamento;
	}

	public void setEquipamento(String equipamento) {
		this.equipamento = equipamento;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getRazao() {
		return razao;
	}

	public void setRazao(String razao) {
		this.razao = razao;
	}

	public Long getQtdePedestresHora() {
		return qtdePedestresHora;
	}

	public void setQtdePedestresHora(Long qtdePedestresHora) {
		this.qtdePedestresHora = qtdePedestresHora;
	}

	public Integer getHora() {
		return hora;
	}

	public void setHora(Integer hora) {
		this.hora = hora;
	}

	public Boolean getBloquearSaida() {
		return bloquearSaida;
	}

	public void setBloquearSaida(Boolean bloquearSaida) {
		this.bloquearSaida = bloquearSaida;
	}

	public Long getIdPedestre() {
		return idPedestre;
	}

	public void setIdPedestre(Long idPedestre) {
		this.idPedestre = idPedestre;
	}

	public String getCartaoAcessoRecebido() {
		return cartaoAcessoRecebido;
	}

	public void setCartaoAcessoRecebido(String cartaoAcessoRecebido) {
		this.cartaoAcessoRecebido = cartaoAcessoRecebido;
	}

	public Boolean getIsSincronizado() {
		return isSincronizado;
	}

	public void setIsSincronizado(Boolean isSincronizado) {
		this.isSincronizado = isSincronizado;
	}

}
