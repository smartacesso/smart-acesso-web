package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;

@Entity
@Table(name="TB_CONFIGURACAO_DESKTOP")
@NamedQueries({
	@NamedQuery(name  = "ConfiguracoesDesktopEntity.findAll", 
				query = "select obj "
				      + "from ConfiguracoesDesktopEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id desc"),
	@NamedQuery(name  = "ConfiguracoesDesktopEntity.findById", 
				query = "select obj from ConfiguracoesDesktopEntity obj "
					  + "where obj.id = :ID order by obj.id asc")
})
@SuppressWarnings("serial")
public class ConfiguracoesDesktopEntity extends ClienteBaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_CONFIGURACAO_DESKTOP", nullable=false, length=4)
	private Long id;
	
	@Type(type = "text")
	@Column(name="DEVICES", nullable=true)
	private String backupDevices;
	
	@Type(type = "text")
	@Column(name="PREFERENCES", nullable=true)
	private String backupPreferences;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBackupDevices() {
		return backupDevices;
	}

	public void setBackupDevices(String backupDevices) {
		this.backupDevices = backupDevices;
	}

	public String getBackupPreferences() {
		return backupPreferences;
	}

	public void setBackupPreferences(String backupPreferences) {
		this.backupPreferences = backupPreferences;
	}
	
	
	
	

}
