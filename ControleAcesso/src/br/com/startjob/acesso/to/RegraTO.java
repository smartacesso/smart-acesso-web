package br.com.startjob.acesso.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import br.com.startjob.acesso.modelo.entity.HorarioEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoEscala;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;

public class RegraTO {

	private Long id;
	private String nome;
	private Date validade;
	private Long idEmpresa;
	private TipoRegra tipo;
	private TipoPedestre tipoPedestre;
	private String descricao;
	private Date dataInicioPeriodo;
	private Date dataFimPeriodo;
	private TipoEscala tipoEscala;
	private Date horarioInicioTurno;
	private Long qtdeDeCreditos;
	private Long diasValidadeCredito;
	
	private String status;
	private Boolean removed;
	private Date dataRemovido;
	
	private ArrayList<HorarioTO> horarios;
	
	public RegraTO(RegraEntity regra) {
		this.id = regra.getId();
		this.nome = regra.getNome();
		this.validade = regra.getValidade();
		this.idEmpresa = regra.getEmpresa() != null ? regra.getEmpresa().getId() : null;
		this.tipo = regra.getTipo();
		this.tipoPedestre = regra.getTipoPedestre();
		this.descricao = regra.getDescricao();
		this.dataInicioPeriodo = regra.getDataInicioPeriodo();
		this.dataFimPeriodo = regra.getDataFimPeriodo();
		this.tipoEscala = regra.getTipoEscala();
		this.horarioInicioTurno = regra.getHorarioInicioTurno();
		this.qtdeDeCreditos = regra.getQtdeDeCreditos();
		this.diasValidadeCredito = regra.getDiasValidadeCredito();
		this.status = regra.getStatus().toString();
		this.removed = regra.getRemovido();
		this.dataRemovido = regra.getDataRemovido();
		
		if(regra.getHorarios() != null && !regra.getHorarios().isEmpty()) {
			horarios = new ArrayList<>();
			regra.getHorarios().forEach(horario -> {
				this.horarios.add(new HorarioTO(horario));
			});
		}
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

	public Date getValidade() {
		return validade;
	}

	public void setValidade(Date validade) {
		this.validade = validade;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public TipoRegra getTipo() {
		return tipo;
	}

	public void setTipo(TipoRegra tipo) {
		this.tipo = tipo;
	}

	public TipoPedestre getTipoPedestre() {
		return tipoPedestre;
	}

	public void setTipoPedestre(TipoPedestre tipoPedestre) {
		this.tipoPedestre = tipoPedestre;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	public TipoEscala getTipoEscala() {
		return tipoEscala;
	}

	public void setTipoEscala(TipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}

	public Date getHorarioInicioTurno() {
		return horarioInicioTurno;
	}

	public void setHorarioInicioTurno(Date horarioInicioTurno) {
		this.horarioInicioTurno = horarioInicioTurno;
	}

	public Long getQtdeDeCreditos() {
		return qtdeDeCreditos;
	}

	public void setQtdeDeCreditos(Long qtdeDeCreditos) {
		this.qtdeDeCreditos = qtdeDeCreditos;
	}

	public Long getDiasValidadeCredito() {
		return diasValidadeCredito;
	}

	public void setDiasValidadeCredito(Long diasValidadeCredito) {
		this.diasValidadeCredito = diasValidadeCredito;
	}

	public ArrayList<HorarioTO> getHorarios() {
		return horarios;
	}

	public void setHorarios(ArrayList<HorarioTO> horarios) {
		this.horarios = horarios;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public Date getDataRemovido() {
		return dataRemovido;
	}

	public void setDataRemovido(Date dataRemovido) {
		this.dataRemovido = dataRemovido;
	}
	
}
