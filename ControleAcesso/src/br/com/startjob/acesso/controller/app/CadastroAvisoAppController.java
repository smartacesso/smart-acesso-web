package br.com.startjob.acesso.controller.app;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.ejb.AppEJBRemote;
import br.com.startjob.acesso.modelo.entity.AvisoAppEntity;
import br.com.startjob.acesso.services.AvisoAppImagemUploadServlet;

@Named("cadastroAvisoAppController")
@ViewScoped
@UseCase(classEntidade = AvisoAppEntity.class, funcionalidade = "Avisos do App",
		urlNovoRegistro = "/paginas/sistema/avisoApp/cadastroAvisoApp.jsf", queryEdicao = "findById")
public class CadastroAvisoAppController extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private AppEJBRemote appEJB;

	private boolean imagemAlterada;

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
			limparImagemPendente();
			imagemAlterada = false;
			imagemPreviewContentType = "image/jpeg";
			return;
		}
		carregarImagemExistente(entidade.getId());
	}

	public String getAvisoIdUpload() {
		AvisoAppEntity entidade = (AvisoAppEntity) getEntidade();
		if (entidade != null && entidade.getId() != null) {
			return String.valueOf(entidade.getId());
		}
		return "novo";
	}

	public void setAvisoIdUpload(String ignored) {
		// Campo somente leitura para o upload via servlet.
	}

	private String chaveImagemSessao() {
		AvisoAppEntity entidade = (AvisoAppEntity) getEntidade();
		Long clienteId = getUsuarioLogado().getCliente().getId();
		String idPart = entidade != null && entidade.getId() != null
				? String.valueOf(entidade.getId()) : "novo";
		return AvisoAppImagemUploadServlet.SESSION_IMAGEM_PREFIX + clienteId + "_" + idPart;
	}

	private byte[] lerImagemPendente() {
		return (byte[]) getSessionAtrribute(chaveImagemSessao());
	}

	private String lerNomeImagemPendente() {
		return (String) getSessionAtrribute(chaveImagemSessao() + AvisoAppImagemUploadServlet.SESSION_IMAGEM_NOME_SUFFIX);
	}

	private void gravarImagemPendente(byte[] imagem, String nomeArquivo) {
		setSessioAtrribute(chaveImagemSessao(), imagem);
		setSessioAtrribute(chaveImagemSessao() + AvisoAppImagemUploadServlet.SESSION_IMAGEM_NOME_SUFFIX, nomeArquivo);
	}

	private void limparImagemPendente() {
		String chave = chaveImagemSessao();
		setSessioAtrribute(chave, null);
		setSessioAtrribute(chave + AvisoAppImagemUploadServlet.SESSION_IMAGEM_NOME_SUFFIX, null);
		setSessioAtrribute(chave + AvisoAppImagemUploadServlet.SESSION_IMAGEM_ALTERADA_SUFFIX, null);
	}

	private void carregarImagemExistente(Long idAviso) {
		try {
			byte[] imagem = appEJB.buscarImagemAvisoApp(idAviso, getUsuarioLogado().getCliente().getId());
			gravarImagemPendente(imagem, null);
			imagemAlterada = false;
			atualizarContentTypePreview(imagem);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String salvar() {
		if (!validarPermissaoWeb(br.com.startjob.acesso.modelo.enumeration.WebPermissao.AVISO_APP_EDITAR)) {
			return "";
		}
		AvisoAppEntity entidade = (AvisoAppEntity) getEntidade();
		entidade.setCliente(getUsuarioLogado().getCliente());

		if (entidade.getDataPublicacao() == null) {
			entidade.setDataPublicacao(new Date());
		}

		try {
			boolean novo = entidade.getId() == null;
			aplicarImagemNaEntidade(entidade, novo);
			appEJB.salvarAvisoApp(entidade);
			limparImagemPendente();
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
			entidade.setImagem(lerImagemPendente());
			return;
		}
		if (entidade.getId() != null) {
			entidade.setImagem(appEJB.buscarImagemAvisoApp(entidade.getId(),
					getUsuarioLogado().getCliente().getId()));
		}
	}

	/** Chamado apos {@link AvisoAppImagemUploadServlet} gravar a imagem na sessao. */
	public void onImagemCarregadaExterna() {
		byte[] imagem = lerImagemPendente();
		if (imagem == null || imagem.length == 0) {
			mensagemErro("", "msg.generica.erro.processamento");
			return;
		}
		imagemAlterada = true;
		atualizarContentTypePreview(lerNomeImagemPendente(), imagem);
		mensagemInfo("", "msg.aviso.imagem.carregada");
	}

	public void removerImagem() {
		limparImagemPendente();
		imagemAlterada = true;
		imagemPreviewContentType = "image/jpeg";
	}

	public boolean isTemImagemCadastrada() {
		byte[] imagem = lerImagemPendente();
		return imagem != null && imagem.length > 0;
	}

	public String getImagemPreviewDataUrl() {
		byte[] imagem = lerImagemPendente();
		if (imagem == null || imagem.length == 0) {
			return "";
		}
		String base64 = Base64.getEncoder().encodeToString(imagem);
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
