package br.com.startjob.acesso.controller.app;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.ejb.AppEJBRemote;
import br.com.startjob.acesso.modelo.entity.AvisoAppEntity;

@Named("cadastroAvisoAppController")
@ViewScoped
@UseCase(classEntidade = AvisoAppEntity.class, funcionalidade = "Avisos do App",
		urlNovoRegistro = "/paginas/sistema/avisoApp/cadastroAvisoApp.jsf", queryEdicao = "findById")
public class CadastroAvisoAppController extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final long LIMITE_IMAGEM_BYTES = 3145728L;

	@EJB
	private AppEJBRemote appEJB;

	private boolean imagemAlterada;

	private byte[] imagemAnexo;

	private String imagemPreviewContentType = "image/jpeg";

	@PostConstruct
	@Override
	public void init() {
		super.init();
		AvisoAppEntity entidade = (AvisoAppEntity) getEntidade();
		if (entidade == null) {
			return;
		}
		if (entidade.getId() == null) {
			entidade.setDataPublicacao(new Date());
			return;
		}
		carregarImagemExistente(entidade.getId());
	}

	private void carregarImagemExistente(Long idAviso) {
		try {
			imagemAnexo = appEJB.buscarImagemAvisoApp(idAviso, getUsuarioLogado().getCliente().getId());
			imagemAlterada = false;
			atualizarContentTypePreview(imagemAnexo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String salvar() {
		AvisoAppEntity entidade = (AvisoAppEntity) getEntidade();
		entidade.setCliente(getUsuarioLogado().getCliente());

		if (entidade.getDataPublicacao() == null) {
			entidade.setDataPublicacao(new Date());
		}

		try {
			boolean novo = entidade.getId() == null;
			aplicarImagemNaEntidade(entidade, novo);
			appEJB.salvarAvisoApp(entidade);
			mensagemInfo("", novo ? "msg.generica.objeto.incluido.sucesso" : "msg.generica.objeto.alterado.sucesso");
			redirect("/paginas/sistema/avisoApp/pesquisaAvisoApp.xhtml?acao=OK");
		} catch (Exception e) {
			e.printStackTrace();
			mensagemFatal("", "msg.generica.erro.processamento");
		}
		return null;
	}

	private void aplicarImagemNaEntidade(AvisoAppEntity entidade, boolean novo) throws Exception {
		if (imagemAlterada || novo) {
			entidade.setImagem(imagemAnexo);
			return;
		}
		if (entidade.getId() != null) {
			entidade.setImagem(appEJB.buscarImagemAvisoApp(entidade.getId(),
					getUsuarioLogado().getCliente().getId()));
		}
	}

	public void onImagemUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		if (file == null || file.getContent() == null || file.getContent().length == 0) {
			mensagemErro("", "msg.generica.erro.processamento");
			return;
		}
		if (file.getSize() > LIMITE_IMAGEM_BYTES) {
			mensagemErro("", "msg.generica.erro.processamento");
			return;
		}
		imagemAnexo = file.getContent();
		imagemAlterada = true;
		atualizarContentTypePreview(file.getFileName(), imagemAnexo);
		mensagemInfo("", "msg.aviso.imagem.carregada");
	}

	public void removerImagem() {
		imagemAnexo = null;
		imagemAlterada = true;
		imagemPreviewContentType = "image/jpeg";
	}

	public boolean isTemImagemCadastrada() {
		return imagemAnexo != null && imagemAnexo.length > 0;
	}

	public String getImagemPreviewDataUrl() {
		if (!isTemImagemCadastrada()) {
			return "";
		}
		String base64 = Base64.getEncoder().encodeToString(imagemAnexo);
		return "data:" + imagemPreviewContentType + ";base64," + base64;
	}

	public boolean isImagemAlterada() {
		return imagemAlterada;
	}

	private void atualizarContentTypePreview(String fileName, byte[] content) {
		if (fileName != null && fileName.toLowerCase().endsWith(".png")) {
			imagemPreviewContentType = "image/png";
			return;
		}
		atualizarContentTypePreview(content);
	}

	private void atualizarContentTypePreview(byte[] content) {
		if (content != null && content.length >= 4
				&& (content[0] & 0xFF) == 0x89 && content[1] == 0x50 && content[2] == 0x4E && content[3] == 0x47) {
			imagemPreviewContentType = "image/png";
		} else {
			imagemPreviewContentType = "image/jpeg";
		}
	}
}
