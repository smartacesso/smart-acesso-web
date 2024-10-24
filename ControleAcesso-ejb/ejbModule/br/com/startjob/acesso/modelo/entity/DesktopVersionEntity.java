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
@Table(name="TB_DESKTOP_VERSION")
@NamedQueries({
	@NamedQuery(name = "DesktopVersionEntity.findAll", query = "select obj from DesktopVersionEntity obj order by obj.version desc"),
	@NamedQuery(name = "DesktopVersionEntity.findById", query = "select obj from DesktopVersionEntity obj where obj.id = :ID order by obj.version desc")
})
@SuppressWarnings("serial")
public class DesktopVersionEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_SERVICES_VERSION", nullable=false, length=4)
	private Long id;
	
	@Column(name="VERSION", nullable=false, length=6, precision=2)
	private Double version;
	
	@Column(name="DOWNLOAD_URL", nullable=false, length=250)
	private String downloadUrl;
	
	@Column(name="DOWNLOAD_UPDATE_WORKER_URL", nullable=false, length=250)
	private String downloadUpdateWorkerUrl;
	
	@Column(name="DESCRIPTION", nullable=false, length=250)
	private String description;
	
	public DesktopVersionEntity() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDownloadUpdateWorkerUrl() {
		return downloadUpdateWorkerUrl;
	}

	public void setDownloadUpdateWorkerUrl(String downloadUpdateWorkerUrl) {
		this.downloadUpdateWorkerUrl = downloadUpdateWorkerUrl;
	}

}
