package br.com.startjob.acesso.controller.uc001;

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
import br.com.startjob.acesso.controller.CadastroBaseController;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.EnderecoEntity;
import br.com.startjob.acesso.modelo.entity.IntegracaoSOCEntity;
import br.com.startjob.acesso.modelo.entity.IntegracaoSeniorEntity;
import br.com.startjob.acesso.modelo.entity.IntegracaoTotvsEntity;
import br.com.startjob.acesso.modelo.entity.PlanoEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;

@Named("cadastroClienteController")
@ViewScoped
@UseCase(classEntidade=ClienteEntity.class, funcionalidade="Cadastro de clientes", 
		urlNovoRegistro="/paginas/sistema/clientes/cadastroCliente.jsf", queryEdicao="findByIdComplete")
public class CadastroClienteController extends CadastroBaseController {
	
	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaStatus;
	private PlanoEntity plano;
	private List<PlanoEntity> listaPlanos;
	
	private String usuarioLogin;
	private String usuarioSenha;
	
	private String nomeUnidadeAnterior;
	private String emailAnterior;
	
	UsuarioEntity usuarioParaEditar;
	
	@PostConstruct
	@Override
	public void init() {
		super.init();
		
		ClienteEntity cliente = (ClienteEntity) getEntidade();
		
		if(cliente != null && cliente.getEndereco() == null) {
			cliente.setEndereco(new EnderecoEntity());
		}
		
        // Inicialize integracaoSoc se estiver nulo
        if (cliente.getIntegracaoSoc() == null) {
            cliente.setIntegracaoSoc(new IntegracaoSOCEntity()); // Substitua por sua classe específica
        }
        
        if (cliente.getIntegracaoSenior() == null) {
            cliente.setIntegracaoSenior(new IntegracaoSeniorEntity()); // Substitua por sua classe específica
        }
        
        
        // Inicialize integracaoSoc se estiver nulo
        if (cliente.getIntegracaoTotvs() == null) {
            cliente.setIntegracaoTotvs(new IntegracaoTotvsEntity()); // Substitua por sua classe específica
        }
        
		
		if(cliente.getPlanos() != null && !cliente.getPlanos().isEmpty()) {
			listaPlanos = cliente.getPlanos();
		} else {
			listaPlanos = new ArrayList<>();
		}

		
		if(cliente.getNomeUnidadeOrganizacional() != null) {
			nomeUnidadeAnterior = cliente.getNomeUnidadeOrganizacional();
		}
		
		if(cliente.getEmail() != null) {
			emailAnterior = cliente.getEmail();
		}
		
		if(cliente.getId() != null) {
			buscaDadosUsuario();
		} else {
			usuarioParaEditar = new UsuarioEntity();
			usuarioParaEditar.setLogin("");
		}
		
		plano = new PlanoEntity();
		montaListaStatus();
	}
	
	private void montaListaStatus() {
		listaStatus = new ArrayList<SelectItem>();
		listaStatus.add(new SelectItem(null, "Selecione"));
		listaStatus.add(new SelectItem(Status.ATIVO, Status.ATIVO.toString()));
		listaStatus.add(new SelectItem(Status.INATIVO, Status.INATIVO.toString()));
	}
	
	public void buscaDadosUsuario() {
		
		try {
			ClienteEntity clienteEntity = (ClienteEntity) getEntidade();
			
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("email", clienteEntity.getEmail());
			
			@SuppressWarnings("unchecked")
			ArrayList<UsuarioEntity> userList = (ArrayList<UsuarioEntity>) 
					baseEJB.pesquisaSimplesLimitado(UsuarioEntity.class, "findAll", args, 0, 1);
			
			if(userList != null && !userList.isEmpty()) {
				usuarioParaEditar = userList.get(0);
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void alterarSenhaUsuarioCliente() throws Exception {
		ClienteEntity cliente = (ClienteEntity) getEntidade();
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("email", cliente.getEmail());
		
		@SuppressWarnings("unchecked")
		ArrayList<UsuarioEntity> userList = (ArrayList<UsuarioEntity>) baseEJB.pesquisaSimplesLimitado(UsuarioEntity.class, 
				"findAll", args, 0, 1);
		
		UsuarioEntity user = new UsuarioEntity();
		
		if(userList != null && !userList.isEmpty()) {
			user = userList.get(0);
		}
		
		if("".equals(usuarioParaEditar.getLogin().trim())) {
			mensagemFatal("", "#O campo login não pode ser vazio.");
			return;
			
		} else if(!usuarioParaEditar.getLogin().equals(user.getLogin()) 
				&& verificaLoginExistente(usuarioParaEditar.getLogin(), cliente.getId())) {
			mensagemFatal("", "#Este login já está em uso.");
			return;
			
		} else if("".equals(usuarioSenha.trim())) {
			mensagemFatal("", "#O campo senha não pode ser vazio.");
			
		} else {
			user.setLogin(usuarioParaEditar.getLogin());
			user.setSenha(EncryptionUtils.encrypt(usuarioSenha));
			
			baseEJB.alteraObjeto(user);
			mensagemInfo("", "#Dados alterados com sucesso!");
		}
		usuarioLogin = "";
		usuarioSenha = "";
	}
	
	@Override
	public String salvar() {
		ClienteEntity cliente = (ClienteEntity) getEntidade();
		
		if(listaPlanos == null || listaPlanos.isEmpty()) {
			mensagemFatal("", "msg.add.um.plano");
			return "";
		}
		
		if(emailAnterior != null && !emailAnterior.equalsIgnoreCase(cliente.getEmail()) 
				&& verificaEmailExistente(cliente.getEmail())) {
			mensagemFatal("", "msg.email.existente");
			return "";
		}
		
		if(nomeUnidadeAnterior != null && !nomeUnidadeAnterior.equalsIgnoreCase(cliente.getNomeUnidadeOrganizacional()) 
				&& verificaNomeUnidadeExistente(cliente.getNomeUnidadeOrganizacional())) {
			
			mensagemFatal("", "msg.unidade.ja.existente");
			return "";
		}
		
		if(cliente.getEndereco().getCep() == null || "".equals(cliente.getEndereco().getCep())) {
			cliente.setEndereco(null);
		}
		
		cliente.setPlanos(listaPlanos);
		
		boolean criaUsuario = true;
		if(cliente.getId() != null)
			 criaUsuario = false;
		
		String retorno = super.salvar();
		
		cliente = (ClienteEntity) getEntidade();
		
		if(!"e".equals(retorno)) {
			try {
				if(criaUsuario) {
					cliente = (ClienteEntity) baseEJB.recuperaObjeto(ClienteEntity.class, cliente.getId());
					
					if(verificaLoginExistente(usuarioLogin, cliente.getId())) {
						mensagemFatal("", "msg.login.existente");
						return "";
					}
					
					UsuarioEntity user = new UsuarioEntity();
					
					preencheDadosUsuario(cliente, user);
					
					baseEJB.gravaObjeto(user);
				}else {
					UsuarioEntity user = (UsuarioEntity) baseEJB.recuperaObjeto(UsuarioEntity.class, usuarioParaEditar.getId());
					
					preencheDadosUsuario(cliente, user);

					user.setLogin(usuarioParaEditar.getLogin());

					baseEJB.alteraObjeto(user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		listaPlanos = new ArrayList<>();
		usuarioParaEditar = new UsuarioEntity();
		
		redirect("/paginas/sistema/clientes/pesquisaCliente.xhtml");
		
		return retorno;
	}
	
	private boolean verificaNomeUnidadeExistente(String nomeUnidadeOrganizacional) {
		Map<String, Object> argumentos = new HashMap<String, Object>();
		argumentos.put("UNIDADE_ORGANIZACIONAL", nomeUnidadeOrganizacional);

		try {
			int clientes = baseEJB.pesquisaArgFixosLimitadoCount(ClienteEntity.class, "findByNomeUnidadeOrganizacional", argumentos);
			
			if(clientes > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public void preencheDadosUsuario(ClienteEntity cliente, UsuarioEntity user) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		user.setNome(cliente.getNome());
		user.setStatus(cliente.getStatus());
		
		if(usuarioLogin != null)
			user.setLogin(usuarioLogin);
		if(usuarioSenha != null)
			user.setSenha(EncryptionUtils.encrypt(usuarioSenha));
		
		user.setTelefone(cliente.getTelefone());
		user.setCelular(cliente.getCelular());
		user.setEmail(cliente.getEmail());
		user.setEndereco(cliente.getEndereco());
		user.setPerfil(PerfilAcesso.ADMINISTRADOR);
		user.setCliente(cliente);
	}
	
	public void removePlano(PlanoEntity planoSelecionado) {
		if(planoSelecionado != null) {
			listaPlanos.remove(planoSelecionado);
		}
	}
	
	public void adicionarPlano() {
		if(plano != null) {
			
			boolean valido = true;
			
			if(plano.getNome() == null || "".equals(plano.getNome())) {
				mensagemFatal("", "#Nome é obrigatório!");
				valido = false;
			}
			if(plano.getStatus() == null) {
				mensagemFatal("", "#Status é obrigatório!");
				valido = false;
			}
			if(plano.getInicio() == null) {
				mensagemFatal("", "#Data de início é obrigatório!");
				valido = false;
			}
			if(plano.getFim() == null) {
				mensagemFatal("", "#Data final é obrigatório!");
				valido = false;
			}
			if(plano.getPeriodicidadeCobranca() == null) {
				mensagemFatal("", "#Periodicidade da cobrança é obrigatório!");
				valido = false;
			}
			if(plano.getDiaVencimento() == null) {
				mensagemFatal("", "#Dia do vencimento é obrigatório!");
				valido = false;
			}
			if(plano.getValor() == null) {
				mensagemFatal("", "#Valor do plano é obrigatório!");
				valido = false;
			}
			
			if(!valido) {
				return;
			}
			
			ClienteEntity cliente = (ClienteEntity) getEntidade();
			
			plano.setCliente(cliente);
			
			listaPlanos.add(plano);
		}
		plano = new PlanoEntity();
	}

	public List<SelectItem> getListaStatus() {
		return listaStatus;
	}

	public void setListaStatus(List<SelectItem> listaStatus) {
		this.listaStatus = listaStatus;
	}

	public PlanoEntity getPlano() {
		return plano;
	}

	public void setPlano(PlanoEntity plano) {
		this.plano = plano;
	}

	public List<PlanoEntity> getListaPlanos() {
		return listaPlanos;
	}

	public String getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(String usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public String getUsuarioSenha() {
		return usuarioSenha;
	}

	public void setUsuarioSenha(String usuarioSenha) {
		this.usuarioSenha = usuarioSenha;
	}

	public UsuarioEntity getUsuarioParaEditar() {
		return usuarioParaEditar;
	}

	public void setUsuarioParaEditar(UsuarioEntity usuarioParaEditar) {
		this.usuarioParaEditar = usuarioParaEditar;
	}

}
