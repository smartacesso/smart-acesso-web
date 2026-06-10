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

import br.com.startjob.acesso.modelo.entity.base.BaseEntity;

@Entity
@Table(name = "TB_DEVICE_TOKEN", indexes = {
		@Index(name = "UK_DEVICE_TOKEN_FCM", columnList = "FCM_TOKEN", unique = true),
		@Index(name = "IDX_DEVICE_TOKEN_PEDESTRE", columnList = "ID_PEDESTRE")
})
@NamedQueries({
		@NamedQuery(name = "DeviceTokenEntity.findByFcmToken", query = "SELECT obj FROM DeviceTokenEntity obj "
				+ "WHERE obj.fcmToken = :fcmToken AND (obj.removido = false OR obj.removido IS NULL)"),
		@NamedQuery(name = "DeviceTokenEntity.findByPedestreAndFcmToken", query = "SELECT obj FROM DeviceTokenEntity obj "
				+ "WHERE obj.pedestre.id = :idPedestre AND obj.fcmToken = :fcmToken "
				+ "AND (obj.removido = false OR obj.removido IS NULL)"),
		@NamedQuery(name = "DeviceTokenEntity.findAtivosByPedestre", query = "SELECT obj FROM DeviceTokenEntity obj "
				+ "WHERE obj.pedestre.id = :idPedestre AND obj.ativo = true "
				+ "AND (obj.removido = false OR obj.removido IS NULL)")
})
public class DeviceTokenEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_DEVICE_TOKEN", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_PEDESTRE", nullable = false)
	private PedestreEntity pedestre;

	@Column(name = "FCM_TOKEN", nullable = false, length = 512, unique = true)
	private String fcmToken;

	@Column(name = "PLATFORM", nullable = false, length = 20)
	private String platform;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ATUALIZADO_EM", nullable = false)
	private Date atualizadoEm = new Date();

	@Column(name = "ATIVO", nullable = false)
	private Boolean ativo = Boolean.TRUE;

	@Column(name = "APP_VERSION", nullable = true, length = 50)
	private String appVersion;

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

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Date getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(Date atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
}
