package br.com.startjob.acesso.modelo.ejb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;

import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.entity.WebPerfilPermissaoEntity;

@Remote
public interface WebPermissaoEJBRemote extends BaseEJBRemote {

	Set<String> resolverPermissoesWeb(UsuarioEntity usuario) throws Exception;

	boolean clientePossuiMatrizCustomizada(Long idCliente) throws Exception;

	List<WebPerfilPermissaoEntity> listarPorCliente(Long idCliente) throws Exception;

	void salvarMatrizCliente(Long idCliente, Map<PerfilAcesso, Set<String>> permissoesHabilitadas) throws Exception;

	Map<PerfilAcesso, Set<String>> carregarMatrizEfetiva(Long idCliente) throws Exception;
}
