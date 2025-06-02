package br.com.startjob.acesso.modelo.ejb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.startjob.acesso.modelo.entity.HorarioEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;

public class HorarioSeniorDto {
	
	private String idEscala;
	private String idHorario;
	private String diaSemana;
	private String inicio;
	private String fim;
	private String nome;
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			
	public HorarioSeniorDto() {
		
	}
	
	public String getIdEscala() {
		return idEscala;
	}


	public void setIdEscala(String idEscala) {
		this.idEscala = idEscala;
	}


	public String getIdHorario() {
		return idHorario;
	}


	public void setIdHorario(String idHorario) {
		this.idHorario = idHorario;
	}


	public Date getInicio() {
		return converterHorarioParaDate(inicio);
	}


	public void setInicio(String inicio) {
		this.inicio = inicio;
	}


	public Date getFim() {
		return converterHorarioParaDate(fim);
	}


	public void setFim(String fim) {
		this.fim = fim;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}
	

	public String getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(String diaSemana) {
		this.diaSemana = diaSemana;
	}
	
	private Date converterHorarioParaDate(String hora) {
        try {
            return sdf.parse(hora);
        } catch (ParseException e) {
            e.printStackTrace(); // Melhor trocar por log
            return null;
        }
	}
	
	public RegraEntity toRegraEntity() {
		RegraEntity regra =  new RegraEntity();
		regra.setDataAlteracao(new Date());
		regra.setDataCriacao(new Date());
		regra.setNome("Escala " + getIdEscala());
		regra.setIdEscala(Integer.parseInt(getIdEscala()));
		regra.setStatus(Status.ATIVO);
		regra.setTipoPedestre(TipoPedestre.PEDESTRE);
		regra.setTipo(TipoRegra.ACESSO_HORARIO);
		return regra;
	}
	
	public HorarioEntity toHorarioEntity() {
		if(this.nome.contains("Lanche")) {
			return null;
		}
		
		HorarioEntity horario = new HorarioEntity();
		horario.setDiasSemana(diaSemana);
		horario.setHorarioInicio(converterHorarioParaDate(inicio));
		horario.setHorarioFim(converterHorarioParaDate(fim));
		horario.setNome(nome);
		horario.setIdHorarioSenior(Integer.parseInt(idHorario));
		horario.setStatus(Status.ATIVO);
		if(this.nome.equalsIgnoreCase("Refeicao") ||this.nome.equalsIgnoreCase("inicio")) {
			horario.setQtdeDeCreditos(1L);
		}
		return horario;
	}
	
	public HorarioEntity toHorarioPedestre() {
		if(this.nome.contains("Lanche")) {
			return null;
		}
		
		HorarioEntity horario = new HorarioEntity();
		horario.setDiasSemana(diaSemana);
		horario.setHorarioInicio(converterHorarioParaDate(inicio));
		horario.setHorarioFim(converterHorarioParaDate(fim));
		horario.setNome(nome + " pedestre");
		horario.setIdHorarioSenior(Integer.parseInt(idHorario));
		horario.setStatus(Status.ATIVO);
		if(this.nome.equalsIgnoreCase("Refeicao") ||this.nome.equalsIgnoreCase("inicio")) {
			horario.setQtdeDeCreditos(1L);
		}

		return horario;
	}
	
	public static HorarioSeniorDto criaHorarioPadrao() {
		HorarioSeniorDto horarioPadrao = new HorarioSeniorDto();
		horarioPadrao.setIdEscala("99991");
		horarioPadrao.setIdHorario("99991");
		horarioPadrao.setDiaSemana("1234567");
		horarioPadrao.setInicio("00:00");
		horarioPadrao.setFim("23:59");
		horarioPadrao.setNome("periodo");
		
		return horarioPadrao;
	}
	
	public static HorarioSeniorDto criarHorarioFolga() {
		HorarioSeniorDto horarioPadrao = new HorarioSeniorDto();
		horarioPadrao.setIdEscala("99992");
		horarioPadrao.setIdHorario("99992");
		horarioPadrao.setDiaSemana("7");
		horarioPadrao.setInicio("00:00");
		horarioPadrao.setFim("00:01");
		horarioPadrao.setNome("periodo");
		
		return horarioPadrao;
	}

}
