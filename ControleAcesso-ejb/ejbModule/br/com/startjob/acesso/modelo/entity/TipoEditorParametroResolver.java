package br.com.startjob.acesso.modelo.entity;

import br.com.startjob.acesso.modelo.BaseConstant;

/**
 * Mapeia o nome persistido do parâmetro ({@link BaseConstant.PARAMETERS_NAME}) para o editor da UI.
 */
public final class TipoEditorParametroResolver {

	private TipoEditorParametroResolver() {
	}

	public static TipoEditorParametro resolver(String nome) {
		if (nome == null || nome.trim().isEmpty()) {
			return TipoEditorParametro.TEXTO_FORMATO;
		}

		if (nome.equals(BaseConstant.PARAMETERS_NAME.CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE)) {
			return TipoEditorParametro.CAMPOS_OBRIGATORIOS;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.HORARIO_DISPARO_SOC)) {
			return TipoEditorParametro.HORARIO_SOC;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.ESCOLHER_QTDE_DIGITOS_CARTAO)) {
			return TipoEditorParametro.QTDE_DIGITOS_CARTAO;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.DIAS_VALIDADE_LINK_CADASTRO_FACIAL_EXTERNO)) {
			return TipoEditorParametro.DIAS_VALIDADE_LINK_FACIAL;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.TEMPO_QRCODE_DINAMICO)) {
			return TipoEditorParametro.MINUTOS_QR_CODE_DINAMICO;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.TEMPO_EXPIRACAO_CADASTRO_FACIAL)) {
			return TipoEditorParametro.HORAS_CADASTRO_FACIAL_REMOTO;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.TEMPO_TOLERANCIA_ENTRADA_E_SAIDA)) {
			return TipoEditorParametro.TOLERANCIA_ENTRADA_SAIDA;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.FORMATO_DATA_PARA_IMPORTACAO)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.FORMATO_HORA_PARA_IMPORTACAO)) {
			return TipoEditorParametro.TEXTO_FORMATO;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.LOCAL_PADRAO_PEDESTRE)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.LOCAL_PADRAO_VISITANTE)) {
			return TipoEditorParametro.TEXTO_LOCAL_PADRAO;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.CHAVE_DE_INTEGRACAO_COMTELE)) {
			return TipoEditorParametro.TEXTO_LARGO;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.LIMITE_DIGITAIS_PEDESTRE)) {
			return TipoEditorParametro.SELECT_LIMITE_DIGITAIS;
		}
		if (nome.equals(BaseConstant.PARAMETERS_NAME.TIPO_QR_CODE_PADRAO)) {
			return TipoEditorParametro.SELECT_TIPO_QR_PADRAO;
		}
		if (isParametroBooleano(nome)) {
			return TipoEditorParametro.BOOLEAN;
		}

		return TipoEditorParametro.TEXTO_FORMATO;
	}

	private static boolean isParametroBooleano(String nome) {
		return nome.equals(BaseConstant.PARAMETERS_NAME.GERAR_MATRICULA_SEQUENCIAL)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.PERMITIR_CAMPO_ADICIONAL_CRACHA)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.VALIDAR_MATRICULAS_DUPLICADAS)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.VALIDAR_CPF_DUPLICADO)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.VALIDA_CPF_VALIDO)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.VALIDAR_RG_DUPLICADO)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.VALIDAR_CARTAO_ACESSO_DUPLICADO)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.CADASTRO_EM_LOTE)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.PERMITIR_ACESSO_QR_CODE)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.EXIBE_CAMPO_SEMPRE_LIBERADO_PARA_TODOS)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.HABILITA_ACESSO_POR_RECONHECIMENTO_FACIAL)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.PREENCHER_CARTAO_COM_MATRICULA)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.PREENCHER_CARTAO_AUTO)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.ENVIO_FACIAL)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.TIPO_QR_CODE)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.HABILITA_APP_PEDESTRE)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.REMOVE_CARTAO_EXCLUIDO)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.HABILITA_RELATORIO_RONA)
				|| nome.equals(BaseConstant.PARAMETERS_NAME.HABILITA_MODO_CORRESPONDENCIA);
	}
}
