package br.com.startjob.acesso.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.startjob.acesso.modelo.enumeration.WebPermissao;

/**
 * Mapeia trechos de URL das páginas web para permissões exigidas.
 */
public final class WebUrlPermissaoMap {

	private static final String PATH_PESQUISA_PEDESTRE = "pedestres/pesquisapedestre";
	private static final String PATH_CADASTRO_PEDESTRE = "pedestres/cadastropedestre";
	private static final String PATH_CADASTRO_SIMPLIFICADO = "pedestres/cadastrosimplificado";

	private static final Map<String, String> URL_PARA_PERMISSAO = new HashMap<>();

	static {
		mapVer("pedestres/pesquisaPedestre", WebPermissao.PEDESTRE_VER, WebPermissao.VISITANTE_VER);
		mapEditar("pedestres/cadastroPedestre", WebPermissao.PEDESTRE_EDITAR, WebPermissao.VISITANTE_EDITAR);
		mapEditar("pedestres/cadastroSimplificado", WebPermissao.PEDESTRE_EDITAR, WebPermissao.VISITANTE_EDITAR);
		mapVer("pedestres/pesquisaCadastroErro", WebPermissao.CADASTRO_ERRO_VER);
		mapVer("pedestres/pesquisaAprovacaoTotem", WebPermissao.APROVACAO_TOTEM_VER);
		mapEditar("pedestres/cadastroAuto", WebPermissao.TOTEM_AUTO_VER);

		mapVer("responsaveis/pesquisaResponsavel", WebPermissao.PEDESTRE_VER);
		mapEditar("responsaveis/cadastroResponsavel", WebPermissao.PEDESTRE_EDITAR);

		mapVer("correspondencia/pesquisaCorrespondencia", WebPermissao.CORRESPONDENCIA_VER);
		mapEditar("correspondencia/cadastroCorrespondencia", WebPermissao.CORRESPONDENCIA_EDITAR);

		mapVer("avisoApp/pesquisaAvisoApp", WebPermissao.AVISO_APP_VER);
		mapEditar("avisoApp/cadastroAvisoApp", WebPermissao.AVISO_APP_EDITAR);

		mapVer("alteracoes/alteracoesEmMassa", WebPermissao.ALTERACAO_MASSA_VER);

		mapVer("usuarios/pesquisaUsuarios", WebPermissao.USUARIO_VER);
		mapEditar("usuarios/cadastroUsuarios", WebPermissao.USUARIO_EDITAR);

		mapVer("empresas/pesquisaEmpresa", WebPermissao.EMPRESA_VER);
		mapEditar("empresas/cadastroEmpresa", WebPermissao.EMPRESA_EDITAR);

		mapVer("relatorios/pedestres", WebPermissao.RELATORIO_PEDESTRE_VER);
		mapVer("relatorios/visitantes", WebPermissao.RELATORIO_VISITANTE_VER);
		mapVer("relatorios/ocupacao", WebPermissao.RELATORIO_OCUPACAO_VER);
		mapVer("relatorios/liberacoesManuais", WebPermissao.RELATORIO_LIBERACOES_VER);
		mapVer("relatorios/relatorioPermanencia", WebPermissao.RELATORIO_PERMANENCIA_VER);
		mapVer("relatorios/relatorioRefeicao", WebPermissao.RELATORIO_REFEICAO_VER);
		mapVer("relatorios/equipamentosConectados", WebPermissao.DISPOSITIVO_EQUIPAMENTOS_VER);

		mapVer("rhid/pesquisaRhidConfig", WebPermissao.CONFIG_RHID_VER);
		mapEditar("rhid/cadastroRhidConfig", WebPermissao.CONFIG_RHID_EDITAR);
		mapEditar("rhid/rhidConfig", WebPermissao.CONFIG_RHID_EDITAR);

		mapVer("regras/pesquisaRegra", WebPermissao.CONFIG_REGRAS_VER);
		mapEditar("regras/cadastroRegra", WebPermissao.CONFIG_REGRAS_EDITAR);

		mapVer("configuracoes/gerenciarParametros", WebPermissao.CONFIG_PARAMETROS_VER);
		mapVer("configuracoes/matrizPermissoes", WebPermissao.CONFIG_WEB_PERMISSOES_VER);

		mapVer("cameras/cameras", WebPermissao.DISPOSITIVO_CAMERAS_VER);

		mapVer("clientes/pesquisaCliente", WebPermissao.ADMIN_CLIENTES_VER);
		mapEditar("clientes/cadastroCliente", WebPermissao.ADMIN_CLIENTES_EDITAR);
	}

	private WebUrlPermissaoMap() {
	}

	private static void mapVer(String path, WebPermissao permissao) {
		URL_PARA_PERMISSAO.put(path, permissao.getCodigo());
	}

	private static void mapEditar(String path, WebPermissao permissao) {
		URL_PARA_PERMISSAO.put(path, permissao.getCodigo());
	}

	private static void mapVer(String path, WebPermissao permissaoA, WebPermissao permissaoB) {
		URL_PARA_PERMISSAO.put(path, permissaoA.getCodigo() + "|" + permissaoB.getCodigo());
	}

	private static void mapEditar(String path, WebPermissao permissaoA, WebPermissao permissaoB) {
		URL_PARA_PERMISSAO.put(path, permissaoA.getCodigo() + "|" + permissaoB.getCodigo());
	}

	public static Set<String> permissoesParaUri(String uri) {
		return permissoesParaUri(uri, null);
	}

	public static Set<String> permissoesParaUri(String uri, String tipoParam) {
		if (uri == null) {
			return Collections.emptySet();
		}
		String path = uri.toLowerCase();
		if (path.contains("/principal")) {
			return Collections.emptySet();
		}
		if (path.contains(PATH_PESQUISA_PEDESTRE)) {
			return permissoesPedestreVisitante(tipoParam, true);
		}
		if (path.contains(PATH_CADASTRO_PEDESTRE) || path.contains(PATH_CADASTRO_SIMPLIFICADO)) {
			return permissoesPedestreVisitante(tipoParam, false);
		}
		for (Map.Entry<String, String> entry : URL_PARA_PERMISSAO.entrySet()) {
			if (path.contains(entry.getKey().toLowerCase())) {
				return parseCodigos(entry.getValue());
			}
		}
		return Collections.emptySet();
	}

	public static boolean possuiPermissaoParaUri(String uri, Set<String> permissoes) {
		return possuiPermissaoParaUri(uri, null, permissoes);
	}

	public static boolean possuiPermissaoParaUri(String uri, String tipoParam, Set<String> permissoes) {
		Set<String> exigidas = permissoesParaUri(uri, tipoParam);
		if (exigidas.isEmpty()) {
			return true;
		}
		if (permissoes == null || permissoes.isEmpty()) {
			return false;
		}
		for (String codigo : exigidas) {
			if (permissoes.contains(codigo)) {
				return true;
			}
		}
		return false;
	}

	private static Set<String> permissoesPedestreVisitante(String tipoParam, boolean ver) {
		Set<String> codigos = new HashSet<>();
		if ("pe".equalsIgnoreCase(tipoParam)) {
			codigos.add(ver ? WebPermissao.PEDESTRE_VER.getCodigo() : WebPermissao.PEDESTRE_EDITAR.getCodigo());
		} else if ("vi".equalsIgnoreCase(tipoParam)) {
			codigos.add(ver ? WebPermissao.VISITANTE_VER.getCodigo() : WebPermissao.VISITANTE_EDITAR.getCodigo());
		} else {
			if (ver) {
				codigos.add(WebPermissao.PEDESTRE_VER.getCodigo());
				codigos.add(WebPermissao.VISITANTE_VER.getCodigo());
			} else {
				codigos.add(WebPermissao.PEDESTRE_EDITAR.getCodigo());
				codigos.add(WebPermissao.VISITANTE_EDITAR.getCodigo());
			}
		}
		return codigos;
	}

	private static Set<String> parseCodigos(String valor) {
		Set<String> codigos = new HashSet<>();
		if (valor == null) {
			return codigos;
		}
		for (String parte : valor.split("\\|")) {
			if (!parte.trim().isEmpty()) {
				codigos.add(parte.trim());
			}
		}
		return codigos;
	}
}
