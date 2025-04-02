package br.com.startjob.acesso.modelo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.com.startjob.acesso.modelo.entity.base.ClienteBaseEntity;
@NamedQueries({
	@NamedQuery(name  = "HistoricoCotaEntity.findAll", 
				query = "select obj "
				      + "from HistoricoCotaEntity obj "
					  + "where (obj.removido = false or obj.removido is null) "
					  + "order by obj.id asc"),
	
	 @NamedQuery(name = "HistoricoCotaEntity.findCotaByPedestreAndMesAno",
			     query = "SELECT hc.cotaMensal FROM HistoricoCotaEntity hc " +
			             "WHERE hc.pedestre.id = :idPedestre " +
			             "AND hc.mes = :mes " +
			             "AND hc.ano = :ano")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "TB_COTAS")
public class HistoricoCotaEntity extends ClienteBaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedestre_id", nullable = false)
    private PedestreEntity pedestre;

    private Integer mes;
    private Integer ano;
    private Long cotaMensal; // Em segundos (para evitar cálculos extras depois)

    public HistoricoCotaEntity() {}

    public HistoricoCotaEntity(PedestreEntity pedestre, int mes, int ano, Long cotaMensal) {
        this.pedestre = pedestre;
        this.mes = mes;
        this.ano = ano;
        this.cotaMensal = cotaMensal;
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

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Long getCotaMensal() {
		return cotaMensal;
	}

	public void setCotaMensal(Long cotaMensal) {
		this.cotaMensal = cotaMensal;
	}

    // Getters e Setters
}

