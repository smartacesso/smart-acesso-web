package br.com.startjob.acesso.controller.uc008;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.controller.MenuController;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.CadastroExternoEntity;
import br.com.startjob.acesso.service.CadastroErroService;
import br.com.startjob.acesso.utils.Utils;

@SuppressWarnings("serial")
@Named("consultaCadastroErroController")
@ViewScoped
public class ConsultaCadastroErroController extends BaseController {

	private static final Logger LOG = Logger.getLogger(ConsultaCadastroErroController.class.getName());

	private static final String SCRIPT_ATUALIZAR_BADGE_ERROS =
			"if (typeof rcAtualizarBadgeCadastrosErro === 'function') { rcAtualizarBadgeCadastrosErro(); }";

	@EJB
	private PedestreEJBRemote pedestreEJB;

	@Inject
	private MenuController menuController;

	private CadastroErroService erroService;
	private List<CadastroExternoEntity> erros = new ArrayList<>();
	private CadastroExternoEntity selecionado;

	@PostConstruct
	public void init() {
		baseEJB = pedestreEJB;
		erroService = new CadastroErroService(baseEJB);
		carregarErros();
	}

	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		String acaoReq = getRequest().getParameter("acao");
		if ("registrado".equalsIgnoreCase(acaoReq)) {
			mensagemInfo("", "msg.cadastro.erro.registrado.lista");
		}
	}

	public void carregarErros() {
		try {
			erros = erroService.listarErros(getUsuarioLogado().getCliente().getId());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao listar cadastros com falha", e);
			erros = new ArrayList<>();
			mensagemFatal("", "msg.cadastro.erro.lista.falha");
		}
	}

	public void prepararDescarte(CadastroExternoEntity item) {
		selecionado = item;
	}

	public void descartar() {
		if (selecionado == null || selecionado.getId() == null) {
			return;
		}
		try {
			erroService.descartar(selecionado.getId(), getUsuarioLogado().getCliente().getId());
			mensagemInfo("", "msg.cadastro.erro.descartado");
			carregarErros();
			atualizarIndicadorMenu();
			PrimeFaces.current().executeScript(
					"PF('dlgDescartarCadastroErro').hide(); " + SCRIPT_ATUALIZAR_BADGE_ERROS);
		} catch (IllegalStateException e) {
			mensagemAviso("", e.getMessage());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Erro ao descartar cadastro", e);
			mensagemFatal("", "msg.cadastro.erro.descartar.falha");
		}
	}

	public void refazer(CadastroExternoEntity item) {
		if (item == null || !erroService.podeRefazer(item)) {
			mensagemAviso("", "msg.cadastro.erro.refazer.sem.pedestre");
			return;
		}
		String url = erroService.montarUrlRefazer(item);
		if (url == null || url.isEmpty()) {
			mensagemAviso("", "msg.cadastro.erro.refazer.sem.pedestre");
			return;
		}
		redirect(url);
	}

	private void atualizarIndicadorMenu() {
		if (menuController != null) {
			try {
				menuController.recarregarMenuCadastrosErroAjax();
			} catch (Exception e) {
				menuController.invalidarMenuCadastrosErro();
			}
		}
	}

	public StreamedContent fotoItem(CadastroExternoEntity item) {
		if (item == null) {
			return null;
		}
		byte[] foto = item.getPrimeiraFoto();
		if (foto == null && item.getPedestre() != null) {
			foto = item.getPedestre().getFoto();
		}
		if (foto == null || foto.length == 0) {
			return null;
		}
		return Utils.getStreamedContent(foto);
	}

	public String nomeExibicao(CadastroExternoEntity item) {
		if (item == null) {
			return "";
		}
		if (item.getNomeVisitante() != null && !item.getNomeVisitante().isEmpty()) {
			return item.getNomeVisitante();
		}
		if (item.getPedestre() != null && item.getPedestre().getNome() != null) {
			return item.getPedestre().getNome();
		}
		return "—";
	}

	public String cpfExibicao(CadastroExternoEntity item) {
		if (item == null) {
			return "";
		}
		String cpf = item.getCpfVisitante();
		if ((cpf == null || cpf.isEmpty()) && item.getPedestre() != null) {
			cpf = item.getPedestre().getCpf();
		}
		return cpfExibicaoPedestre(cpf);
	}

	public String formatarCpf(String cpf) {
		if (cpf == null) {
			return "";
		}
		String v = cpf.replaceAll("\\D", "");
		if (v.length() != 11) {
			return cpf;
		}
		return v.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
	}

	public String labelOrigem(CadastroExternoEntity item) {
		if (item == null || item.getTipo() == null) {
			return "";
		}
		return item.getTipo().getNomeFormated();
	}

	public List<CadastroExternoEntity> getErros() {
		return erros;
	}

	public CadastroExternoEntity getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(CadastroExternoEntity selecionado) {
		this.selecionado = selecionado;
	}

}
