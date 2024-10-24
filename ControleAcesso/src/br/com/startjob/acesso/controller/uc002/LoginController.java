package br.com.startjob.acesso.controller.uc002;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.senior.services.IntegracaoSeniorService;
import com.senior.services.dto.CargoSeniorDto;
import com.senior.services.dto.CentroDeCustoSeniorDto;
import com.senior.services.dto.EmpresaSeniorDto;
import com.senior.services.dto.FuncionarioSeniorDto;

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.ejb.LoginEJBRemote;
import br.com.startjob.acesso.modelo.ejb.PedestreEJB;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.exception.AccountException;
import br.com.startjob.acesso.modelo.utils.EncryptionUtils;
import br.com.startjob.acesso.utils.MailSendUtils;
import br.com.startjob.acesso.utils.ResourceBundleUtils;

@SuppressWarnings("serial")
@Named("loginController")
@ViewScoped
public class LoginController extends BaseController {
	
	private String unidadeOrganizacional;
	private String usuario;
	private String senha;
	private String email;
	private String acao;
	
	/**
	 * EJB responsável por avaliar o usuário no sistema.
	 */
	@EJB
	private LoginEJBRemote loginEJB;
	
	@PostConstruct
	@Override
	public void init() {
		acao = getRequest().getParameter("acao");
		
	}
	
	/**
	 * 
	 * Realiza login do usuário
	 *
	 * @author: Gustavo Diniz
	 */
	@SuppressWarnings("unchecked")
	public void login() {
		
		try {
	    	IntegracaoSeniorService integracaoSeniorService = new IntegracaoSeniorService("smartwsintegra", "Sm4rt@s3n10r#");
    		List<EmpresaSeniorDto> empresas = integracaoSeniorService.buscarEmpresas();
//	    	List<FuncionarioSeniorDto> funcionarios =  integracaoSeniorService.buscarFuncionarios(empresas.get(0).getNumEmp());
//	    	List<FuncionarioSeniorDto> funcionarios =  integracaoSeniorService.buscarFuncionariosAdmitidos("1", "15/10/2024");
//	    	List<FuncionarioSeniorDto> funcionarios =  integracaoSeniorService.buscarFuncionariosDemitidos("1", "10/10/2024");
//	    	List<CargoSeniorDto> cargos =  integracaoSeniorService.buscaCargos("1");
//	    	List<CentroDeCustoSeniorDto> centrodecusto =  integracaoSeniorService.buscaCentroDeCusto("1");
	    	
			UsuarioEntity u = loginEJB.validaUsuario(unidadeOrganizacional, usuario, EncryptionUtils.encrypt(senha),
					BaseConstant.ACCESS_TYPES.WEB,
					getDeviceType(getRequest()));
			
			//se ok 
			if (u != null) {
				
				//verifica se está na estrutura principal
				//ou em outro servidor

				String urlAtual = getRequest().getRemoteHost();
				System.out.println(urlAtual);
//				if(AppAmbienteUtils.isProdution() 
//						&& !urlAtual.equals(BaseConstant.IP_PRINCIPAL)) {
//					
//					//busca usuário padrão do cliente
//					Map<String, Object> args = new HashMap<String, Object>();
//					args.put("email", u.getCliente().getEmail());
//					List<UsuarioEntity> userList = (ArrayList<UsuarioEntity>) 
//							baseEJB.pesquisaSimplesLimitado(UsuarioEntity.class, "findAll", args, 0, 1);
//					UsuarioEntity p = null;
//					if(userList != null && !userList.isEmpty()) {
//						//faz login pelo serviço antes de continuar
//						p = userList.get(0);
//						String result = ConectionUtils.get(BaseConstant.URL_APLICACAO_COMPLETA
//								+"/restful-services/login/interno?loginName="+p.getLogin()+"&passwd="+p.getSenha());
//						if(result != null) {
//							ResponseServiceTO toResult = (ResponseServiceTO) 
//									ConectionUtils.parseJSON(result, ResponseServiceTO.class);
//							if(!"OK".equals(toResult.getStatus())) {
//								mensagemFatal("", "msgs.account.usuario.nenhum.plano.ativo");
//								return;
//							}
//						}else {
//							mensagemFatal("", "msgs.account.usuario.verificar.plano");
//							return;
//						}
//					}
//				}
				
				//adiciona na sessão	
				setSessioAtrribute(BaseConstant.LOGIN.USER_ENTITY, u);
				setSessioAtrribute("horaLogin",new SimpleDateFormat("HH:mm").format(new Date()));
				setSessioAtrribute("GMT", Calendar.getInstance(new Locale("pt","BR")).getTimeZone().toString());
						
				redirect("/paginas/principal.xhtml");
				
			} else {
				
				mensagemFatal("", "msg.usuario.nao.entrado");
			}
			
		} catch (AccountException e) {
			System.out.println(e.getMessage());
			
			String msg = ResourceBundleUtils.getInstance()
					.recuperaChave(e.getMessage(),getFacesContext(),e.getParans());
			
			//mensagens normais
			mensagemFatal("", "#"+msg);
			
			return ;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			
			if(e.getMessage() != null && e.getMessage().contains("account")) {
				mensagemFatal("", e.getMessage());
			} else{
				mensagemFatal("", "#Erro ao avaliar login, favor verificar log da aplicação. Caso não tenha acesso" +
						" acione o Administrador do Sistema.");
			}
		}
		
		
	}
	
	public void alterarSenha() {
		
		if(isNullOrEmpty(email)) {
			mensagemFatal("", "msg.alteracao.senha.email.obrigatorio");
			return;
		}
		
		try {
			
			//pesquisa usuário por e-mail
			Map<String, Object> arg = new HashMap<String, Object>();
			arg.put("EMAIL", email);
			List<UsuarioEntity> u = baseEJB
					.recuperaUsuariosArgFixos(arg, "findByEmail");
			
			//envia mensagem para alterar senha
			if(u == null || u.isEmpty()) {
				mensagemFatal("", "msg.alteracao.senha.email.nao.encontrado");
				return;
			}
			
			//gera e grava token
			UsuarioEntity usuario = u.get(0);
			usuario.setToken(EncryptionUtils.getRandomPassword());
			baseEJB.alteraObjeto(usuario);
			
			MailSendUtils.enviaResetSenha(usuario, mailSession, getFacesContext(), getURLApp());
			
			email = null;
			mensagemInfo("", "msg.alteracao.senha.enviado");
			PrimeFaces.current().executeScript("PF('esqueciSenha').hide();");
			
		}catch (Exception e) {
			mensagemFatal("", "msg.alteracao.senha.email.nao.enviado");
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void exibeMensagens(ComponentSystemEvent evt) throws AbortProcessingException {
		super.exibeMensagens(evt);
		
		if("sa".equals(acao))
			mensagemInfo("", "msg.alteracao.senha.sucesso");
		
		if("invalido".equals(acao))
			mensagemFatal("", "msg.acesso.invalido");
		
		
		acao = null;
		
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUnidadeOrganizacional() {
		return unidadeOrganizacional;
	}

	public void setUnidadeOrganizacional(String unidadeOrganizacional) {
		this.unidadeOrganizacional = unidadeOrganizacional;
	}

}
