package br.com.startjob.acesso.modelo.ejb;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.startjob.acesso.modelo.entity.ClienteEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.entity.WebPerfilPermissaoEntity;
import br.com.startjob.acesso.modelo.enumeration.PerfilAcesso;
import br.com.startjob.acesso.modelo.enumeration.WebPermissao;
import br.com.startjob.acesso.modelo.web.WebPermissaoMatriz;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class WebPermissaoEJB extends BaseEJB implements WebPermissaoEJBRemote {

	public WebPermissaoEJB() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<String> resolverPermissoesWeb(UsuarioEntity usuario) throws Exception {
		if (usuario == null || usuario.getPerfil() == null || usuario.getCliente() == null) {
			return new HashSet<>();
		}
		Long idCliente = usuario.getCliente().getId();
		if (clientePossuiMatrizCustomizada(idCliente)) {
			Map<String, Object> args = new HashMap<>();
			args.put("ID_CLIENTE", idCliente);
			args.put("PERFIL", usuario.getPerfil());
			List<WebPerfilPermissaoEntity> lista = (List<WebPerfilPermissaoEntity>) pesquisaArgFixos(
					WebPerfilPermissaoEntity.class, "findByIdClientePerfil", args);
			Set<String> codigos = new HashSet<>();
			if (lista != null) {
				for (WebPerfilPermissaoEntity row : lista) {
					if (Boolean.TRUE.equals(row.getHabilitado())) {
						codigos.add(row.getCodigoPermissao());
					}
				}
			}
			return codigos;
		}
		return WebPermissaoMatriz.codigosPadrao(usuario.getPerfil());
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean clientePossuiMatrizCustomizada(Long idCliente) throws Exception {
		if (idCliente == null) {
			return false;
		}
		Map<String, Object> args = new HashMap<>();
		args.put("ID_CLIENTE", idCliente);
		int count = pesquisaArgFixosLimitadoCount(WebPerfilPermissaoEntity.class, "findByIdCliente", args);
		return count > 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WebPerfilPermissaoEntity> listarPorCliente(Long idCliente) throws Exception {
		Map<String, Object> args = new HashMap<>();
		args.put("ID_CLIENTE", idCliente);
		return (List<WebPerfilPermissaoEntity>) pesquisaArgFixos(WebPerfilPermissaoEntity.class, "findByIdCliente",
				args);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void salvarMatrizCliente(Long idCliente, Map<PerfilAcesso, Set<String>> permissoesHabilitadas)
			throws Exception {
		if (idCliente == null || permissoesHabilitadas == null) {
			return;
		}
		ClienteEntity cliente = (ClienteEntity) recuperaObjeto(ClienteEntity.class, idCliente);
		if (cliente == null) {
			throw new Exception("msg.cliente.nao.encontrado");
		}

		Map<String, Object> args = new HashMap<>();
		args.put("ID_CLIENTE", idCliente);
		List<WebPerfilPermissaoEntity> existentes = (List<WebPerfilPermissaoEntity>) pesquisaArgFixos(
				WebPerfilPermissaoEntity.class, "findByIdCliente", args);
		if (existentes != null) {
			for (WebPerfilPermissaoEntity row : existentes) {
				excluiObjeto(row);
			}
		}

		for (Map.Entry<PerfilAcesso, Set<String>> entry : permissoesHabilitadas.entrySet()) {
			PerfilAcesso perfil = entry.getKey();
			Set<String> habilitadas = entry.getValue();
			if (perfil == null || habilitadas == null) {
				continue;
			}
			for (WebPermissao permissao : WebPermissao.values()) {
				WebPerfilPermissaoEntity row = new WebPerfilPermissaoEntity();
				row.setCliente(cliente);
				row.setPerfil(perfil);
				row.setCodigoPermissao(permissao.getCodigo());
				row.setHabilitado(habilitadas.contains(permissao.getCodigo()));
				gravaObjeto(row);
			}
		}
	}

	@Override
	public Map<PerfilAcesso, Set<String>> carregarMatrizEfetiva(Long idCliente) throws Exception {
		Map<PerfilAcesso, Set<String>> matriz = new EnumMap<>(PerfilAcesso.class);
		for (PerfilAcesso perfil : PerfilAcesso.values()) {
			matriz.put(perfil, new HashSet<>(WebPermissaoMatriz.codigosPadrao(perfil)));
		}
		if (idCliente == null || !clientePossuiMatrizCustomizada(idCliente)) {
			return matriz;
		}
		List<WebPerfilPermissaoEntity> rows = listarPorCliente(idCliente);
		for (PerfilAcesso perfil : PerfilAcesso.values()) {
			matriz.put(perfil, new HashSet<>());
		}
		if (rows != null) {
			for (WebPerfilPermissaoEntity row : rows) {
				if (row.getPerfil() == null) {
					continue;
				}
				Set<String> set = matriz.get(row.getPerfil());
				if (set == null) {
					set = new HashSet<>();
					matriz.put(row.getPerfil(), set);
				}
				if (Boolean.TRUE.equals(row.getHabilitado())) {
					set.add(row.getCodigoPermissao());
				}
			}
		}
		return matriz;
	}
}
