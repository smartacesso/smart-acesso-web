package br.com.startjob.acesso.modelo.ejb;
import javax.ejb.Remote;

import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;

@Remote
public interface LoginEJBRemote extends BaseEJBRemote {
	
	/**
	 * Valida usuário que esta logando no sistema.
	 * 
	 * @param usuarioLogado
	 * @param accessType
	 * @param validaGestora
	 * @return
	 * @throws Exception
	 */
	public UsuarioEntity validaUsuario(String unidade, String login, String senha, 
			String accessType,String deviceType) throws Exception;
	
	/**
	 * Valida pedestre que esta logando no sistema.
	 * 
	 * @param usuarioLogado
	 * @param accessType
	 * @param validaGestora
	 * @return
	 * @throws Exception
	 */
	public PedestreEntity validaPedestre(String unidade, String login, String senha, 
			String accessType,String deviceType) throws Exception;

}
