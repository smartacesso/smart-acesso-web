package br.com.startjob.acesso.controller.uc018;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.Session;

import org.primefaces.event.CaptureEvent;
import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.utils.MailSendUtils;

@Named("cadastroCorrespondenciaController")
@ViewScoped
@UseCase(classEntidade=CorrespondenciaEntity.class, funcionalidade="Cadastro de Correspondências", 
		urlNovoRegistro="/paginas/sistema/correspondencia/cadastroCorrespondencia.jsf", queryEdicao="findByIdComplete")
public class CadastroCorrespondenciaController extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaTipos;
	
	@Resource(mappedName = "java:/mail/suporte")
	private Session mailSession;

	
	@EJB
	private PedestreEJBRemote pedestreEJB;

	@PostConstruct
	@Override
	public void init() {
		super.init();
		montaListaTipos();
		
		CorrespondenciaEntity entidade = (CorrespondenciaEntity) getEntidade();
		
		// Se for um novo registro, preenche dados iniciais
		if (entidade != null && entidade.getId() == null) {
			entidade.setDataRecebimento(new Date());
			entidade.setConfirmaRetirada("N");
			entidade.setUsuarioRecebeu(getUsuarioLogado());
		}
	}

	public String salvar() {
		CorrespondenciaEntity entidade = (CorrespondenciaEntity) getEntidade();

		entidade.setCliente(getUsuarioLogado().getCliente());

		// Flag para saber se é um NOVO registro (para não mandar e-mail se for só uma
		// edição)
		boolean isNovoRegistro = (entidade.getId() == null);

		if ("S".equals(entidade.getConfirmaRetirada()) && entidade.getDataRetirada() == null) {
			entidade.setDataRetirada(new java.util.Date());
		}

		// Salva no banco primeiro
		String retorno = super.salvar();

		// Dispara o e-mail em background APÓS o salvamento com sucesso
		if (isNovoRegistro) {
			try {
				MailSendUtils.enviaNotificacaoCorrespondencia(entidade, mailSession);
			} catch (Exception e) {
				System.err.println("Erro ao tentar enviar e-mail de correspondência: " + e.getMessage());
				e.printStackTrace();
				// O e-mail falhou, mas não quebramos a tela do porteiro. Apenas logamos.
			}
		}

		redirect("/paginas/sistema/correspondencia/pesquisaCorrespondencia.xhtml?acao=OK");
		return retorno;
	}

	/**
	 * Captura a foto da webcam (p:photoCam)
	 */
	public void onCapture(CaptureEvent captureEvent) {
		byte[] data = captureEvent.getData();
		((CorrespondenciaEntity) getEntidade()).setFotoVolume(data);
		((CorrespondenciaEntity) getEntidade()).setExtensaoFoto("png");
	}

	/**
	 * AutoComplete para buscar moradores (Pedestres)
	 */
	public List<PedestreEntity> completePedestre(String nome) {
		// Busca baseada no cliente logado para não vazar dados entre condomínios
		return pedestreEJB.buscaPedestrePorNomeAndIdCliente(nome, getUsuarioLogado().getCliente().getId());
	}

	private void montaListaTipos() {
		listaTipos = new ArrayList<>();
		listaTipos.add(new SelectItem(null, "Selecione o tipo"));
		listaTipos.add(new SelectItem("CARTA", "Carta"));
		listaTipos.add(new SelectItem("PACOTE", "Pacote/Encomenda"));
		listaTipos.add(new SelectItem("DOCUMENTO", "Documento"));
		listaTipos.add(new SelectItem("OUTROS", "Outros"));
	}

	// Getters e Setters
	public List<SelectItem> getListaTipos() {
		return listaTipos;
	}
}