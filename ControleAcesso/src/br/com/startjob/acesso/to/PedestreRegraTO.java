package br.com.startjob.acesso.to;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.startjob.acesso.modelo.entity.PedestreRegraEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;

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
		// 1. Proteção contra elemento nulo vindo do Stream
		if (pedestreRegraEntity == null) {
			return null;
		}

		final PedestreRegraTO pedestreRegraTO = new PedestreRegraTO();
		pedestreRegraTO.id = pedestreRegraEntity.getId();
		
		// 2. Proteção caso a entidade não tenha uma regra associada
		if (pedestreRegraEntity.getRegra() != null) {
			pedestreRegraTO.idRegra = pedestreRegraEntity.getRegra().getId();
		}
		
		pedestreRegraTO.validade = pedestreRegraEntity.getValidade();
		pedestreRegraTO.qtdeDeCreditos = pedestreRegraEntity.getQtdeDeCreditos();
		pedestreRegraTO.qtdeTotalDeCreditos = pedestreRegraEntity.getQtdeTotalDeCreditos();
		pedestreRegraTO.diasValidadeCredito = pedestreRegraEntity.getDiasValidadeCredito();
		pedestreRegraTO.dataInicioPeriodo = pedestreRegraEntity.getDataInicioPeriodo();
		pedestreRegraTO.dataFimPeriodo = pedestreRegraEntity.getDataFimPeriodo();
		
		// 3. Verifica se os horários e a regra existem antes de validar o tipo
		if (Objects.nonNull(pedestreRegraEntity.getHorarios()) && pedestreRegraEntity.getRegra() != null) {
			if (pedestreRegraEntity.getRegra().getTipo() == TipoRegra.ACESSO_HORARIO) {
				pedestreRegraTO.horarios = pedestreRegraEntity.getHorarios().stream()
						.filter(Objects::nonNull) // Proteção extra caso exista horário nulo na lista
						.map(HorarioTO::new)
						.collect(Collectors.toList());
			}
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
