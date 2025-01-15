
package br.com.startjob.acesso.controller.uc006;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.HorarioEntity;
import br.com.startjob.acesso.modelo.entity.RegraEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoEscala;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.enumeration.TipoRegra;

@Named("cadastroRegraAcessoController")
@ViewScoped
@UseCase(classEntidade=RegraEntity.class, funcionalidade="Cadastro de regras", 
		urlNovoRegistro="/paginas/sistema/regras/cadastroRegra.jsf", queryEdicao="findByIdComplete")
public class CadastroRegraAcessoController extends BaseController {

	private static final long serialVersionUID = 1L;
	
	private HorarioEntity horario;
	private List<HorarioEntity> listaHorarios;
	private List<HorarioEntity> listaHorariosExcluidos;
	
	private List<SelectItem> listaStatus;
	private List<SelectItem> listaTipoRegra;
	private List<SelectItem> listaTipoPedestre;
	private List<SelectItem> listaTipoEscala;
	private List<SelectItem> listaEmpresas;
	
	private TipoPedestre tipoPedestre;
	private TipoRegra tipoRegra;
	
	private Long idEmpresaSelecionada;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		
		RegraEntity regra = (RegraEntity) getEntidade();
		
		montaListaStatus();
		montaListaTipoPedestre();
		montaListaTipoRegra();
		montaListaEmpresas();
		
		if(regra != null && regra.getId() != null) {
			tipoRegra = regra.getTipo();
			tipoPedestre = regra.getTipoPedestre();
			listaHorarios = regra.getHorarios();
			
			if(regra.getEmpresa() != null)
				idEmpresaSelecionada = regra.getEmpresa().getId();
			
			montaListaTipoRegra();
			
			if(tipoRegra.equals(TipoRegra.ACESSO_ESCALA)) {
				montaListaTipoEscala();
			}
		} else {
			listaHorarios = new ArrayList<HorarioEntity>();
		}

		horario = new HorarioEntity();
	}
	
	@Override
	public String salvar() {
		RegraEntity regra = (RegraEntity) getEntidade();
		
		if(listaHorarios != null && !listaHorarios.isEmpty()) {
			boolean horariosValidos = validaHorariosAdicionados();
			
			if(!horariosValidos) {
				return "";
			} else {
				listaHorarios.forEach(horario -> {
					horario.setRegra(regra);
				});
			}
			regra.setHorarios(listaHorarios);
		}
		
		if(listaHorariosExcluidos != null && !listaHorariosExcluidos.isEmpty()) {
			if(regra.getHorarios() == null)
				regra.setHorarios(new ArrayList<HorarioEntity>());
			
			regra.getHorarios().addAll(listaHorariosExcluidos);
		}
		
		if(!TipoRegra.ACESSO_HORARIO.equals(regra.getTipo()) 
				&& !TipoRegra.ACESSO_PERIODO.equals(regra.getTipo())) {
			if(regra.getId() != null && regra.getHorarios() != null)
				regra.setHorarios(new ArrayList<HorarioEntity>());
			else
				regra.setHorarios(null);
		}
		
		regra.setEmpresa(buscaEmpresaSelecionada());
		regra.setCliente(getUsuarioLogado().getCliente());
		
		boolean valido = validaCampos(regra);
		
		if(!valido) {
			return "";
		}

		listaHorarios = null;
		
		String retorno = super.salvar();
		
		redirect("/paginas/sistema/regras/pesquisaRegra.xhtml?acao=OK");
		return retorno;
	}
	
	private boolean validaHorariosAdicionados() {
		boolean valido = true;
		
		for(HorarioEntity horario : listaHorarios) {
			if(horario.getNome() == null || "".equals(horario.getNome())) {
				mensagemFatal("", "#Nome é obrigatório.");
				valido = false;
			}
			
			if(horario.getStatus() == null) {
				mensagemFatal("", "#O status é obrigátorio.");
				valido = false;
			}

			if(horario.getDiasSemana() == null || "".equals(horario.getDiasSemana())) {
				mensagemFatal("", "#Adicione pelo menos um dia da semana.");
				valido = false;
			}

			if(horario.getHorarioInicio() == null) {
				mensagemFatal("", "#O horário de início é obrigatório");
				valido = false;
			}

			if(horario.getHorarioFim() == null) {
				mensagemFatal("", "#O horário final é obrigatório");
				valido = false;
			}
			
			if(!valido) {
				return valido;
			}
		}
		
		return valido;
	}

	public boolean validaCampos(RegraEntity regra) {
		boolean valido = true;
		
		if(TipoRegra.ACESSO_HORARIO.equals(regra.getTipo()) && 
				(regra.getHorarios() == null || regra.getHorarios().isEmpty())) {
			
			mensagemFatal("", "msg.fatal.uc006.add.horario");
			valido = false;
		
		} else if(TipoRegra.ACESSO_PERIODO.equals(regra.getTipo())){
			
			if(regra.getDataInicioPeriodo() == null) {
				mensagemFatal("", "msg.fatal.uc006.add.data.inicial#");
				valido = false;
				
			} else if(regra.getDataFimPeriodo() == null) {
				mensagemFatal("", "msg.fatal.uc006.add.data.final");
				valido = false;
			}
		
		} else if(TipoRegra.ACESSO_ESCALA.equals(regra.getTipo())) {
			if(regra.getTipoEscala() == null) {
				mensagemFatal("", "msg.fatal.uc006.add.tipo.escala");
				valido = false;
			
			} else if(regra.getHorarioInicioTurno() == null) {
				mensagemFatal("", "msg.fatal.uc006.add.add.horario.inicio.turno");
				valido = false;
			}
		
		} else if(TipoRegra.ACESSO_CREDITO.equals(regra.getTipo()) && regra.getQtdeDeCreditos() == null ) {
			mensagemFatal("", "msg.fatal.uc006.add.qtde.creditos");
			valido = false;
		
		} else if(TipoRegra.ACESSO_UNICO.equals(regra.getTipo())) {
			regra.setQtdeDeCreditos(1l);
		}
		
		return valido;
	}
	
	public EmpresaEntity buscaEmpresaSelecionada() {
		EmpresaEntity empresa = null;
		
		if(idEmpresaSelecionada != null) {
			
			try {
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("ID", idEmpresaSelecionada);

				@SuppressWarnings("unchecked")
				List<EmpresaEntity> listaEmpresa = (List<EmpresaEntity>) baseEJB.pesquisaArgFixos(EmpresaEntity.class, "findById", args);
				
				if(listaEmpresa != null && !listaEmpresa.isEmpty()) {
					empresa = listaEmpresa.get(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return empresa;
	}
	
	public void adicionarHorario() {
		listaHorarios.add(horario);
		horario = new HorarioEntity();
	}
	
	public void removeHorario(HorarioEntity horarioSelecionado) {
		if(horarioSelecionado == null)
			return;
		
		if(horarioSelecionado.getId() != null) {
			for(HorarioEntity horario : listaHorarios) {
				if(!horarioSelecionado.getId().equals(horario.getId()))
					continue;
				
				horario.setRemovido(true);
				horario.setDataRemovido(new Date());
				
				if(listaHorariosExcluidos == null)
					listaHorariosExcluidos = new ArrayList<HorarioEntity>();
				
				listaHorariosExcluidos.add(horarioSelecionado);
				listaHorarios.remove(horarioSelecionado);
				break;
			}

		} else {
			listaHorarios.remove(horarioSelecionado);
		}
		
		mensagemInfo("", "msg.uc006.horario.removido.sucesso");
	}
	
	@SuppressWarnings("unchecked")
	public void montaListaEmpresas(){
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());

		listaEmpresas = new ArrayList<SelectItem>();
		listaEmpresas.add(new SelectItem(null, "Selecione"));
		
		try {
			List<EmpresaEntity> empresas = (List<EmpresaEntity>) baseEJB.pesquisaArgFixos(EmpresaEntity.class, "findAllByIdCliente", args);
	
			if(empresas != null && !empresas.isEmpty()) {
				
				empresas.forEach(empresa -> {
					listaEmpresas.add(new SelectItem(empresa.getId(), empresa.getNome()));
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void montaListaStatus() {
		listaStatus = new ArrayList<SelectItem>();
		listaStatus.add(new SelectItem(null, "Selecione"));
		listaStatus.add(new SelectItem(Status.ATIVO, Status.ATIVO.toString()));
		listaStatus.add(new SelectItem(Status.INATIVO, Status.INATIVO.toString()));
	}
	
	private void montaListaTipoPedestre() {
		listaTipoPedestre = new ArrayList<SelectItem>();
		listaTipoPedestre.add(new SelectItem(null, "Selecione"));
		listaTipoPedestre.add(new SelectItem(TipoPedestre.PEDESTRE, TipoPedestre.PEDESTRE.toString()));
		listaTipoPedestre.add(new SelectItem(TipoPedestre.VISITANTE, TipoPedestre.VISITANTE.toString()));
		listaTipoPedestre.add(new SelectItem(TipoPedestre.AMBOS, TipoPedestre.AMBOS.toString()));
	}
	
	public void montaListaTipoRegra() {
		listaTipoRegra = new ArrayList<SelectItem>();
		listaTipoRegra.add(new SelectItem(null, "Selecione"));
		
		Arrays
			.asList(TipoRegra.values())
			.forEach(tipoRegra -> listaTipoRegra.add(new SelectItem(tipoRegra, tipoRegra.getDescricao())));
	}
	
	public void eventoAlteraListaTipoRegra(ValueChangeEvent event) {
		tipoRegra = (TipoRegra) event.getNewValue();
		
		if(tipoRegra.equals(TipoRegra.ACESSO_ESCALA)) {
			montaListaTipoEscala();
		}
	}

	public void montaListaTipoEscala() {
		listaTipoEscala = new ArrayList<SelectItem>();
		
		listaTipoEscala.add(new SelectItem(null, "Selecione"));
		listaTipoEscala.add(new SelectItem(TipoEscala.ESCALA_05_01, TipoEscala.ESCALA_05_01.getDescricao()));
		listaTipoEscala.add(new SelectItem(TipoEscala.ESCALA_06_02, TipoEscala.ESCALA_06_02.getDescricao()));
		listaTipoEscala.add(new SelectItem(TipoEscala.ESCALA_07_01, TipoEscala.ESCALA_07_01.getDescricao()));
		listaTipoEscala.add(new SelectItem(TipoEscala.ESCALA_12_36, TipoEscala.ESCALA_12_36.getDescricao()));
		listaTipoEscala.add(new SelectItem(TipoEscala.ESCALA_24_04, TipoEscala.ESCALA_24_04.getDescricao()));
	}

	public List<SelectItem> getListaStatus() {
		return listaStatus;
	}

	public void setListaStatus(List<SelectItem> listaStatus) {
		this.listaStatus = listaStatus;
	}

	public List<SelectItem> getListaTipoRegra() {
		return listaTipoRegra;
	}

	public void setListaTipoRegra(List<SelectItem> listaTipoRegra) {
		this.listaTipoRegra = listaTipoRegra;
	}

	public List<SelectItem> getListaTipoPedestre() {
		return listaTipoPedestre;
	}

	public void setListaTipoPedestre(List<SelectItem> listaTipoPedestre) {
		this.listaTipoPedestre = listaTipoPedestre;
	}

	public HorarioEntity getHorario() {
		return horario;
	}

	public void setHorario(HorarioEntity horario) {
		this.horario = horario;
	}

	public List<HorarioEntity> getListaHorarios() {
		return listaHorarios;
	}

	public List<SelectItem> getListaEmpresas() {
		return listaEmpresas;
	}

	public void setListaEmpresas(List<SelectItem> listaEmpresas) {
		this.listaEmpresas = listaEmpresas;
	}

	public Long getIdEmpresaSelecionada() {
		return idEmpresaSelecionada;
	}

	public void setIdEmpresaSelecionada(Long idEmpresaSelecionada) {
		this.idEmpresaSelecionada = idEmpresaSelecionada;
	}

	public TipoPedestre getTipoPedestre() {
		return tipoPedestre;
	}

	public void setTipoPedestre(TipoPedestre tipoPedestre) {
		this.tipoPedestre = tipoPedestre;
	}

	public TipoRegra getTipoRegra() {
		return tipoRegra;
	}

	public void setTipoRegra(TipoRegra tipoRegra) {
		this.tipoRegra = tipoRegra;
	}

	public List<SelectItem> getListaTipoEscala() {
		return listaTipoEscala;
	}

	public void setListaTipoEscala(List<SelectItem> listaTipoEscala) {
		this.listaTipoEscala = listaTipoEscala;
	}
}

