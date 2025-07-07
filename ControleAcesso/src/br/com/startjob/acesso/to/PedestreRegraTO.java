package br.com.startjob.acesso.to;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;

public class PedestreRegraTO {

	private Long id;
	private Long idRegra;
	private Date validade;
	private Long qtdeDeCreditos;
	private Long qtdeTotalDeCreditos;
	private Long diasValidadeCredito;
	private Date dataInicioPeriodo;
	private Date dataFimPeriodo;
	private List<HorarioTO> horarios;
	
	public PedestreRegraTO() {}
	
	public PedestreRegraTO(Long id, Long idRegra, Date validade, Long qtdeDeCreditos, Long qtdeTotalDeCreditos,
			Long diasValidadeCredito, Date dataInicioPeriodo, Date dataFimPeriodo) {
		this.id = id;
		this.idRegra = idRegra;
		this.validade = validade;
		this.qtdeDeCreditos = qtdeDeCreditos;
		this.qtdeTotalDeCreditos = qtdeTotalDeCreditos;
		this.diasValidadeCredito = diasValidadeCredito;
		this.dataInicioPeriodo = dataInicioPeriodo;
		this.dataFimPeriodo = dataFimPeriodo;
	}
	
	public static PedestreRegraTO fromDomain(PedestreRegraEntity pedestreRegraEntity) {
		final PedestreRegraTO pedestreRegraTO = new PedestreRegraTO();
		pedestreRegraTO.id = pedestreRegraEntity.getId();
		pedestreRegraTO.idRegra = pedestreRegraEntity.getRegra().getId();
		pedestreRegraTO.validade = pedestreRegraEntity.getValidade();
		pedestreRegraTO.qtdeDeCreditos = pedestreRegraEntity.getQtdeDeCreditos();
		pedestreRegraTO.qtdeTotalDeCreditos = pedestreRegraEntity.getQtdeTotalDeCreditos();
		pedestreRegraTO.diasValidadeCredito = pedestreRegraEntity.getDiasValidadeCredito();
		pedestreRegraTO.dataInicioPeriodo = pedestreRegraEntity.getDataInicioPeriodo();
		pedestreRegraTO.dataFimPeriodo = pedestreRegraEntity.getDataFimPeriodo();
		
		if(Objects.nonNull(pedestreRegraEntity.getHorarios())) {
			pedestreRegraTO.horarios = pedestreRegraEntity.getHorarios().stream()
				.map(HorarioTO::new)
				.collect(Collectors.toList());
		}
		
		return pedestreRegraTO;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getIdRegra() {
		return idRegra;
	}
	public void setIdRegra(Long idRegra) {
		this.idRegra = idRegra;
	}
	public Date getValidade() {
		return validade;
	}
	public void setValidade(Date validade) {
		this.validade = validade;
	}
	public Long getQtdeDeCreditos() {
		return qtdeDeCreditos;
	}
	public void setQtdeDeCreditos(Long qtdeDeCreditos) {
		this.qtdeDeCreditos = qtdeDeCreditos;
	}
	public Long getQtdeTotalDeCreditos() {
		return qtdeTotalDeCreditos;
	}
	public void setQtdeTotalDeCreditos(Long qtdeTotalDeCreditos) {
		this.qtdeTotalDeCreditos = qtdeTotalDeCreditos;
	}
	public Long getDiasValidadeCredito() {
		return diasValidadeCredito;
	}
	public void setDiasValidadeCredito(Long diasValidadeCredito) {
		this.diasValidadeCredito = diasValidadeCredito;
	}
	public Date getDataInicioPeriodo() {
		return dataInicioPeriodo;
	}
	public void setDataInicioPeriodo(Date dataInicioPeriodo) {
		this.dataInicioPeriodo = dataInicioPeriodo;
	}
	public Date getDataFimPeriodo() {
		return dataFimPeriodo;
	}
	public void setDataFimPeriodo(Date dataFimPeriodo) {
		this.dataFimPeriodo = dataFimPeriodo;
	}

	public List<HorarioTO> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<HorarioTO> horarios) {
		this.horarios = horarios;
	}
}
