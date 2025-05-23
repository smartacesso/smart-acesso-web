package br.com.startjob.acesso.controller.uc016;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.ResponsibleEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;

@Named("cadastroResponsavelController")
@ViewScoped
@UseCase(classEntidade=ResponsibleEntity.class, funcionalidade="Cadastro de responsaveis", 
		urlNovoRegistro="/paginas/sistema/regras/cadastroResponsavel.jsf", queryEdicao="findByIdComplete")
public class CadastroResponsavelController extends BaseController {

	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaStatus;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		
		ResponsibleEntity responsavel = (ResponsibleEntity) getEntidade();
		
		montaListaStatus();
	}
	
	@Override
	public String salvar() {
		ResponsibleEntity responsavel = (ResponsibleEntity) getEntidade();
		String passwordEncrypted = "";

		try {
			passwordEncrypted = EncryptionUtils.encrypt(responsavel.getPassword());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		responsavel.setPassword(passwordEncrypted);
		responsavel.setCliente(getUsuarioLogado().getCliente());

		String retorno = super.salvar();
		
		
		if (!"e".equals(retorno)) { // Verifica se não houve erro ao salvar o responsável
			UsuarioEntity usuario = new UsuarioEntity();
			usuario.setNome(responsavel.getNome());
			usuario.setLogin(responsavel.getLogin());
			usuario.setSenha(passwordEncrypted);
			usuario.setEmail(responsavel.getEmail());
			usuario.setCpf(responsavel.getCpf());
			usuario.setRg(responsavel.getRg());
			usuario.setCelular(responsavel.getCelular());
			usuario.setDataNascimento(responsavel.getDataNascimento());
			usuario.setPerfil(PerfilAcesso.RESPONSAVEL);
			usuario.setCliente(responsavel.getCliente());
			usuario.setStatus(Status.ATIVO);

			try {
				baseEJB.gravaObjeto(usuario); // Persistindo o usuário usando a mesma lógica
			} catch (Exception e) {
				e.printStackTrace();
				return "e";
			}
		}
		
		redirect("/paginas/sistema/responsaveis/pesquisaResponsavel.xhtml?acao=OK");
		return retorno;
	}
	
	private void montaListaStatus() {
		listaStatus = new ArrayList<SelectItem>();
		listaStatus.add(new SelectItem(null, "Selecione"));
		
		listaStatus.addAll(Status.montaListaStatus());
	}

	public List<SelectItem> getListaStatus() {
		return listaStatus;
	}

	public void setListaStatus(List<SelectItem> listaStatus) {
		this.listaStatus = listaStatus;
	}

}
