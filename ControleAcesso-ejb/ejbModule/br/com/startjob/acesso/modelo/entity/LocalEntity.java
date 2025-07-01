package br.com.startjob.acesso.modelo.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;

@Entity
@Table(name="TB_LOCAL")
@NamedQueries({
	@NamedQuery(name  = "LocalEntity.findAll", 
				query = "select obj "
				      + "from LocalEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	@NamedQuery(name  = "LocalEntity.findById", 
				query = "select obj from LocalEntity obj "
					  + "where obj.id = :ID order by obj.id asc"),
	@NamedQuery(name  = "LocalEntity.findByNameAndLocal", 
				query = "select obj from LocalEntity obj "
					  + "where obj.nome = :NOME and obj.cliente.id = :ID_CLIENTE "
					  + "and (obj.removido = false OR obj.removido IS NULL) "  	
					  + "order by obj.id asc"),
	@NamedQuery(name = "LocalEntity.findAllByIdCliente", 
			    query = "select distinct obj FROM LocalEntity obj "
			          + "left join fetch obj.hikivisionDeviceNames "
			          + "where obj.cliente.id = :ID_CLIENTE "
			          + "and (obj.removido = false OR obj.removido IS NULL) "
			          + "order BY obj.id ASC")

})
public class LocalEntity extends ClienteBaseEntity {
	
	  private static final long serialVersionUID = 1L;

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "ID_LOCAL", nullable = false)
	    private Long id;
	    
	    @Column(name = "NOME", nullable = true, length = 255)
	    private String nome;
	    
	    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	    @CollectionTable(name = "tb_hikivision_devices_name", joinColumns = @JoinColumn(name = "ID_LOCAL"))
	    @Column(name = "DEVICE_NAME", nullable = false)
	    private List<String> hikivisionDeviceNames = new ArrayList<>();

	    public Long getId() {
	        return this.id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    @Override
	    public String toString() {
	        return "LocalEntity [id=" + id + ", nome=" + nome + "]";
	    }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof LocalEntity)) return false;
	        LocalEntity that = (LocalEntity) o;
	        return id != null && id.equals(that.id);
	    }

	    @Override
	    public int hashCode() {
	        return getClass().hashCode();
	    }

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public List<String> getHikivisionDeviceNames() {
			return hikivisionDeviceNames;
		}

		public void setHikivisionDeviceNames(List<String> hikivisionDeviceNames) {
			this.hikivisionDeviceNames = hikivisionDeviceNames;
		}
}
