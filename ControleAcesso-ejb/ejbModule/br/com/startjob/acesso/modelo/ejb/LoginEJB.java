package br.com.startjob.acesso.modelo.ejb;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.hibernate.Hibernate;

import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.AcessoSistemaEntity;
import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.PlanoEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;

/**
 * Session Bean implementation class LoginEJB
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class LoginEJB extends BaseEJB implements LoginEJBRemote {
	
    /**
     * @see BaseEJB#BaseEJB()
     */
    public LoginEJB() {
        super();
    }
    
    /*
     * (non-Javadoc)
     * @see br.com.fitness.project.model.ejb.login.LoginEJBRemote#validaUsuario(br.com.fitness.project.model.entity.UserEntity, java.lang.String)
     */
	@Override
	public UsuarioEntity validaUsuario(String unidade, String login, String senha, String accessType,
										String deviceType) throws Exception {

		Map<String, Object> argumentos = new HashMap<String, Object>();
		argumentos.put("UNIDADE_ORGANIZACIONAL", unidade);
		int clientes = this.pesquisaArgFixosLimitadoCount(ClienteEntity.class, "findByNomeUnidadeOrganizacional", argumentos);
		
		if(clientes > 0) {
			
			argumentos = new HashMap<String, Object>();
			argumentos.put("LOGIN", login);
			argumentos.put("UNIDADE_ORGANIZACIONAL", unidade);
			List<UsuarioEntity> users = this.recuperaUsuariosArgFixos(argumentos, "findByLogin");
			
			if(users != null && users.size() == 1){
				
				//valida senha
				argumentos = new HashMap<String, Object>();
				argumentos.put("UNIDADE_ORGANIZACIONAL", unidade);
				argumentos.put("LOGIN", login);
				argumentos.put("SENHA", senha);
				users = this.recuperaUsuariosArgFixos(argumentos, "findByAllStatusLoginPass");
				
				if(users == null || users.isEmpty()) {
					throw new Exception("msgs.account.usuario.senha.invalida");					
				}
				
				UsuarioEntity user = users.get(0);
				
				validaConta(user, accessType);
				
				registraAcesso(user, accessType, deviceType);
				
				return user;
			} else{
				throw new Exception("msgs.account.usuario.nao.encontrado");
			}
			
		} else {
			throw new Exception("msgs.account.unidade.nao.encontrada");
		}
		
		//return null;
	}

	/**
	 * Registra acesso do usuário
	 * @param user - usuário logado
	 * @param accessType - tipo de acesso
	 * @param deviceType 
	 * @throws Exception e
	 */
	private void registraAcesso(UsuarioEntity user, String accessType, String deviceType) throws Exception {
		
		AcessoSistemaEntity accesso = new AcessoSistemaEntity();
		accesso.setUsuario(user);
		accesso.setData(new Date());
		accesso.setTipo(accessType);
		accesso.setDispositivo(deviceType);
		
		this.gravaObjeto(accesso);
		
	}

	/**
	 * Verificar se a conta esta vencida, se sim
	 * envia mensagem para atualização de conta.
	 * 
	 * @param user
	 * @param deviceType 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void validaConta(UsuarioEntity user, String accessType) throws Exception {
		
		//verifica se usuário esta ativo ou não e o motivo
		//de ele não estar ativo
		if(Status.INATIVO.equals(user.getStatus())) {
			throw new Exception("msgs.account.usuario.nao.encontrado");
		}
		
		//verifica se tem acesso a web
		if(BaseConstant.ACCESS_TYPES.WEB.equals(accessType) 
				&& Boolean.FALSE.equals(user.getAcessaWeb())) {
			throw new Exception("msgs.account.usuario.nao.permitido.web");
		}
		
		//carrega dados de cliente
		if(user.getCliente() != null) {
			Hibernate.initialize(user.getCliente());
			if(user.getCliente().getConfiguracoesDesktop() != null)
				Hibernate.initialize(user.getCliente().getConfiguracoesDesktop());
			if(user.getConfiguracoesDesktop() != null)
				Hibernate.initialize(user.getConfiguracoesDesktop());
			
			//busca ultimo plano ativo do cliente
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("cliente.id", user.getCliente().getId());
			args.put("status", Status.ATIVO);
			args.put("fim_maior_data", new Date());
			
			List<PlanoEntity> pAtivos = (List<PlanoEntity>) 
					pesquisaSimplesLimitado(PlanoEntity.class, "findAll", args, 0, 1);

			if(pAtivos == null || pAtivos.isEmpty()) {
				throw new Exception("msgs.account.usuario.nenhum.plano.ativo");				
			}

		}
		
	}
	
	/**
	 * Verificar se a conta esta vencida, se sim
	 * envia mensagem para atualização de conta.
	 * 
	 * @param user
	 * @param deviceType 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void validaContaPedestre(PedestreEntity user, String accessType) throws Exception {
		
		//verifica se usuário esta ativo ou não e o motivo
		//de ele não estar ativo
		if(Status.INATIVO.equals(user.getStatus()))
			throw new Exception("msgs.account.usuario.nao.encontrado");
		
		//carrega dados de cliente
		if(user.getCliente() != null) {
			Hibernate.initialize(user.getCliente());
			//busca ultimo plano ativo do cliente
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("cliente.id", user.getCliente().getId());
			args.put("status", Status.ATIVO);
			args.put("fim_maior_data", new Date());
			List<PlanoEntity> pAtivos = (List<PlanoEntity>) 
					pesquisaSimplesLimitado(PlanoEntity.class, "findAll", args, 0, 1);
			if(pAtivos == null || pAtivos.isEmpty())
				throw new Exception("msgs.account.usuario.nenhum.plano.ativo");
		}
		
	}

	@Override
	public PedestreEntity validaPedestre(String unidade, String login, String senha, String accessType,
			String deviceType) throws Exception {
		
		Map<String, Object> argumentos = new HashMap<String, Object>();
		argumentos.put("UNIDADE_ORGANIZACIONAL", unidade);
		int clientes = this.pesquisaArgFixosLimitadoCount(ClienteEntity.class, "findByNomeUnidadeOrganizacional", argumentos);
		
		if(clientes > 0) {
			
			argumentos = new HashMap<String, Object>();
			argumentos.put("LOGIN", login);
			argumentos.put("UNIDADE_ORGANIZACIONAL", unidade);
			List<PedestreEntity> users = this.recuperaPedestresArgFixos(argumentos, "findByLogin");
			
			if(users != null && users.size() == 1){
				
				//valida senha
				argumentos = new HashMap<String, Object>();
				argumentos.put("UNIDADE_ORGANIZACIONAL", unidade);
				argumentos.put("LOGIN", login);
				argumentos.put("SENHA", senha);
				users = this.recuperaPedestresArgFixos(argumentos, "findByAllStatusLoginPass");
				
				if(users == null || users.isEmpty())
					throw new Exception("msgs.account.usuario.senha.invalida");
				
				PedestreEntity user = users.get(0);
				
				validaContaPedestre(user, accessType);
				
				registraAcessoPedestre(user, accessType, deviceType);
				
				return user;
			} else{
				throw new Exception("msgs.account.usuario.nao.encontrado");
			}
			
		} else {
			throw new Exception("msgs.account.unidade.nao.encontrada");
		}
	}

	private void registraAcessoPedestre(PedestreEntity user, String accessType, String deviceType) throws Exception {
		AcessoSistemaEntity accesso = new AcessoSistemaEntity();
		accesso.setPedestre(user);
		accesso.setData(new Date());
		accesso.setTipo(accessType);
		accesso.setDispositivo(deviceType);
		
		this.gravaObjeto(accesso);
	}
}