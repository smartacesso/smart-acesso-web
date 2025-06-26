package br.com.startjob.acesso.controller.uc016;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
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
	    boolean isNovo = (responsavel.getId() == null);

	    String passwordEncrypted = null;

	    // Checa se login já existe em novo cadastro
	    if (isNovo && responsavelExiste(responsavel.getLogin())) {
	        mensagemFatal("", "Login já existe");
	        return "e";
	    }

	    try {
	        if (responsavel.getPassword() != null && !responsavel.getPassword().trim().isEmpty()) {
	            // Criptografa nova senha se foi digitada
	            passwordEncrypted = EncryptionUtils.encrypt(responsavel.getPassword());
	            responsavel.setPassword(passwordEncrypted);
	        } else if (!isNovo) {
	            // Em edição: mantém senha atual
	            ResponsibleEntity existente = (ResponsibleEntity) baseEJB.recuperaObjeto(ResponsibleEntity.class, responsavel.getId());
	            responsavel.setPassword(existente.getPassword());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        mensagemFatal("", "Erro ao criptografar a senha");
	        return "e";
	    }

	    responsavel.setCliente(getUsuarioLogado().getCliente());

	    String retorno = super.salvar();

	    if (!"e".equals(retorno)) {
	        try {
//	            UsuarioEntity usuario = getUsuarioPorLogin(responsavel.getLogin());

//	            if (usuario == null) {
	        	UsuarioEntity  usuario = new UsuarioEntity();
	                usuario.setLogin(responsavel.getLogin());
	                usuario.setPerfil(PerfilAcesso.RESPONSAVEL);
	                usuario.setCliente(responsavel.getCliente());
	                usuario.setStatus(Status.ATIVO);
//	            }

	            usuario.setNome(responsavel.getNome());
	            usuario.setEmail(responsavel.getEmail());
	            usuario.setCpf(responsavel.getCpf());
	            usuario.setRg(responsavel.getRg());
	            usuario.setCelular(responsavel.getCelular());
	            usuario.setDataNascimento(responsavel.getDataNascimento());

	            if (passwordEncrypted != null) {
	                usuario.setSenha(passwordEncrypted);
	            }

	            baseEJB.gravaObjeto(usuario);

	        } catch (Exception e) {
	            e.printStackTrace();
	            mensagemFatal("", "Erro ao salvar usuário vinculado");
	            return "e";
	        }

	        redirect("/paginas/sistema/responsaveis/pesquisaResponsavel.xhtml?acao=OK");
	    }

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
	
	@SuppressWarnings("unchecked")
	private boolean responsavelExiste(String login) {
	    Map<String, Object> args = new HashMap<>();
	    args.put("LOGIN", login.trim().toLowerCase());

	    List<ResponsibleEntity> encontrados = new ArrayList<>();

	    try {
	        encontrados = (List<ResponsibleEntity>) baseEJB
	                .pesquisaArgFixosLimitado(ResponsibleEntity.class, "findByLoginGeral", args, 0, 1);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false; // ou true, se quiser barrar o cadastro em caso de erro
	    }

	    if (encontrados == null || encontrados.isEmpty()) {
	        return false;
	    }

	    ResponsibleEntity responsavelAtual = (ResponsibleEntity) getEntidade();

	    for (ResponsibleEntity r : encontrados) {
	        // Em edição, ignora o próprio registro
	        if (responsavelAtual.getId() != null && responsavelAtual.getId().equals(r.getId())) {
	            continue;
	        }
	        return true; // Existe outro com o mesmo login
	    }

	    return false;
	}




}
