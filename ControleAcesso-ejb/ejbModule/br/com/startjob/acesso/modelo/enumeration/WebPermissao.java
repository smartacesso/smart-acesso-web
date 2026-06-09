package br.com.startjob.acesso.modelo.enumeration;

/**
 * Permissões exclusivas da aplicação web (não afetam desktop/API).
 */
public enum WebPermissao {

	PEDESTRE_VER("WEB_PEDESTRE_VER", "web.permissao.pedestre.ver"),
	PEDESTRE_EDITAR("WEB_PEDESTRE_EDITAR", "web.permissao.pedestre.editar"),
	PEDESTRE_EXCLUIR("WEB_PEDESTRE_EXCLUIR", "web.permissao.pedestre.excluir"),
	PEDESTRE_DADOS_SENSIVEIS_VER("WEB_PEDESTRE_DADOS_SENSIVEIS_VER", "web.permissao.pedestre.dados.sensiveis.ver"),
	PEDESTRE_LINK_FACIAL_GERAR("WEB_PEDESTRE_LINK_FACIAL_GERAR", "web.permissao.pedestre.link.facial.gerar"),

	VISITANTE_VER("WEB_VISITANTE_VER", "web.permissao.visitante.ver"),
	VISITANTE_EDITAR("WEB_VISITANTE_EDITAR", "web.permissao.visitante.editar"),
	VISITANTE_EXCLUIR("WEB_VISITANTE_EXCLUIR", "web.permissao.visitante.excluir"),

	CADASTRO_ERRO_VER("WEB_CADASTRO_ERRO_VER", "web.permissao.cadastro.erro.ver"),
	APROVACAO_TOTEM_VER("WEB_APROVACAO_TOTEM_VER", "web.permissao.aprovacao.totem.ver"),
	APROVACAO_TOTEM_EDITAR("WEB_APROVACAO_TOTEM_EDITAR", "web.permissao.aprovacao.totem.editar"),

	CORRESPONDENCIA_VER("WEB_CORRESPONDENCIA_VER", "web.permissao.correspondencia.ver"),
	CORRESPONDENCIA_EDITAR("WEB_CORRESPONDENCIA_EDITAR", "web.permissao.correspondencia.editar"),

	AVISO_APP_VER("WEB_AVISO_APP_VER", "web.permissao.aviso.app.ver"),
	AVISO_APP_EDITAR("WEB_AVISO_APP_EDITAR", "web.permissao.aviso.app.editar"),

	TOTEM_AUTO_VER("WEB_TOTEM_AUTO_VER", "web.permissao.totem.auto.ver"),
	ALTERACAO_MASSA_VER("WEB_ALTERACAO_MASSA_VER", "web.permissao.alteracao.massa.ver"),
	ALTERACAO_MASSA_EDITAR("WEB_ALTERACAO_MASSA_EDITAR", "web.permissao.alteracao.massa.editar"),

	USUARIO_VER("WEB_USUARIO_VER", "web.permissao.usuario.ver"),
	USUARIO_EDITAR("WEB_USUARIO_EDITAR", "web.permissao.usuario.editar"),

	EMPRESA_VER("WEB_EMPRESA_VER", "web.permissao.empresa.ver"),
	EMPRESA_EDITAR("WEB_EMPRESA_EDITAR", "web.permissao.empresa.editar"),

	RELATORIO_PEDESTRE_VER("WEB_RELATORIO_PEDESTRE_VER", "web.permissao.relatorio.pedestre.ver"),
	RELATORIO_VISITANTE_VER("WEB_RELATORIO_VISITANTE_VER", "web.permissao.relatorio.visitante.ver"),
	RELATORIO_OCUPACAO_VER("WEB_RELATORIO_OCUPACAO_VER", "web.permissao.relatorio.ocupacao.ver"),
	RELATORIO_LIBERACOES_VER("WEB_RELATORIO_LIBERACOES_VER", "web.permissao.relatorio.liberacoes.ver"),
	RELATORIO_PERMANENCIA_VER("WEB_RELATORIO_PERMANENCIA_VER", "web.permissao.relatorio.permanencia.ver"),
	RELATORIO_REFEICAO_VER("WEB_RELATORIO_REFEICAO_VER", "web.permissao.relatorio.refeicao.ver"),

	CONFIG_RHID_VER("WEB_CONFIG_RHID_VER", "web.permissao.config.rhid.ver"),
	CONFIG_RHID_EDITAR("WEB_CONFIG_RHID_EDITAR", "web.permissao.config.rhid.editar"),
	CONFIG_REGRAS_VER("WEB_CONFIG_REGRAS_VER", "web.permissao.config.regras.ver"),
	CONFIG_REGRAS_EDITAR("WEB_CONFIG_REGRAS_EDITAR", "web.permissao.config.regras.editar"),
	CONFIG_PARAMETROS_VER("WEB_CONFIG_PARAMETROS_VER", "web.permissao.config.parametros.ver"),
	CONFIG_PARAMETROS_EDITAR("WEB_CONFIG_PARAMETROS_EDITAR", "web.permissao.config.parametros.editar"),
	CONFIG_WEB_PERMISSOES_VER("WEB_CONFIG_WEB_PERMISSOES_VER", "web.permissao.config.permissoes.ver"),
	CONFIG_WEB_PERMISSOES_EDITAR("WEB_CONFIG_WEB_PERMISSOES_EDITAR", "web.permissao.config.permissoes.editar"),

	DISPOSITIVO_EQUIPAMENTOS_VER("WEB_DISPOSITIVO_EQUIPAMENTOS_VER", "web.permissao.dispositivo.equipamentos.ver"),
	DISPOSITIVO_CAMERAS_VER("WEB_DISPOSITIVO_CAMERAS_VER", "web.permissao.dispositivo.cameras.ver"),

	ADMIN_CLIENTES_VER("WEB_ADMIN_CLIENTES_VER", "web.permissao.admin.clientes.ver"),
	ADMIN_CLIENTES_EDITAR("WEB_ADMIN_CLIENTES_EDITAR", "web.permissao.admin.clientes.editar"),

	AJUDA_DOWNLOAD_DESKTOP_VER("WEB_AJUDA_DOWNLOAD_DESKTOP_VER", "web.permissao.ajuda.download.desktop.ver");

	private final String codigo;
	private final String messageKey;

	WebPermissao(String codigo, String messageKey) {
		this.codigo = codigo;
		this.messageKey = messageKey;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public static WebPermissao porCodigo(String codigo) {
		if (codigo == null) {
			return null;
		}
		for (WebPermissao p : values()) {
			if (p.codigo.equals(codigo)) {
				return p;
			}
		}
		return null;
	}
}
