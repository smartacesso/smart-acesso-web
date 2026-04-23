package br.com.startjob.acesso.controller.uc018;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.CaptureEvent;
import org.primefaces.model.StreamedContent;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.CadastroBaseController;
import br.com.startjob.acesso.modelo.ejb.PedestreEJB;
import br.com.startjob.acesso.modelo.ejb.PedestreEJBRemote;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;

@Named("cadastroCorrespondenciaController")
@ViewScoped
@UseCase(classEntidade=CorrespondenciaEntity.class, funcionalidade="Cadastro de Correspondências", 
		urlNovoRegistro="/paginas/sistema/correspondencia/cadastroCorrespondencia.jsf", queryEdicao="findByIdComplete")
public class CadastroCorrespondenciaController extends CadastroBaseController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaTipos;
	
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

	/**
	 * Lógica de salvamento personalizada
	 */
	@Override
	public String salvar() {
		CorrespondenciaEntity entidade = (CorrespondenciaEntity) getEntidade();
		
		// Garante o vínculo com o cliente (condomínio) logado
		entidade.setCliente(getUsuarioLogado().getCliente());

		// Lógica para quando marcar a retirada na tela de edição
		if ("S".equals(entidade.getConfirmaRetirada()) && entidade.getDataRetirada() == null) {
			entidade.setDataRetirada(new Date());
		}

		String retorno = super.salvar();
		
		// Redireciona para a consulta com parâmetro de sucesso
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