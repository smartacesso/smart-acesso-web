package br.com.startjob.acesso.modelo;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Interface de constantes.
 * 
 * @author: Gustavo Diniz
 * @since 03/02/2020
 */
public interface BaseConstant {
	
	/**
	 * Mensagem que identifica concorrÃªncia
	 */
	public static final String MSG_CONCORRENCIA = "Row was updated or deleted"; 
	
	/**
	 * Defini endereço padrão do sistema
	 */
	public static final String URL_APLICACAO 		  = "/sistema";
	public static final String URL_APLICACAO_COMPLETA = "http://smartacesso.com.br/sistema";
	public static final String IP_PRINCIPAL 		  = "187.32.131.93";
	
	/**
	 * Confirma mensagem
	 */
	public static final String MSG_CADASTRO_CONFIRMADO = "c";
	
	/**
	 * define a quantidade de milisegundos por hora
	 */
	public static final int MILI_SEG_TO_HOURS = 3600000;
	/**
	 * define a quantidade de horas por dia
	 */
	public static final int HORS_TO_DAY = 24;
	
	public static final SimpleDateFormat GLOBAl_SDF = new SimpleDateFormat("dd/MM/yyyy");
	
	public static final Locale PT_BR = new Locale("pt","BR");
	
	public static final String ANDROID = "Android App";
	
	public static final String IPHONE  = "iPhone App";
	
	public static final String WEB     = "Desktop";
	
	public static final String URL_WHATSAPP = "https://web.whatsapp.com/send?1=pt_BR&phone=";
	
	public static final String NOME_REGRA_PADRAO_VISITANTE = "ACESSO_UNICO_VISITANTE";

	public static interface OPERATIONS{
		
		public static final String RESET_PASS = "R";
		public static final String RESET_PASS_PLUS = "R+";
		public static final String EDIT = "E";
		public static final String ALERT = "alert_message";
		
		
	}
	
	public static interface ACCESS_TYPES{
		
		public static final String WEB 		= "WEB";
		public static final String MOBILE 	= "MOBILE";
		public static final String TERMINAL = "TERMINAL";
		public static final String API 		= "API";
		
	}
	
	public static interface LOGIN{
		
		public static final String USER_ENTITY = "usuario";
		
	}
	
	public static interface EXPORT{
		
		/**
		 * BYTES
		 */
		public static final String BYTES = "BYTES";
		/**
		 * FILE_NAME
		 */
		public static final String FILE_NAME = "FILE_NAME";
		
	}
	
	public static interface FUNCIONALITY{
		
	}
	
	public static interface PARAMETERS_TYPE{

		
	}
	
	public static interface PARAMETERS_NAME{
		
		public static final String GERAR_MATRICULA_SEQUENCIAL = "Gerar matricula sequencial";

		public static final String ESCOLHER_QTDE_DIGITOS_CARTAO = "Escolher quantidade de digítos do cartão (até 10)";
		
		public static final String PERMITIR_CAMPO_ADICIONAL_CRACHA = "Permitir campo adicional de crachá/matricula";
		
		public static final String FORMATO_DATA_PARA_IMPORTACAO = "Formato de data para importação de dados";
		
		public static final String FORMATO_HORA_PARA_IMPORTACAO = "Formato de hora para importação de dados";
		
		public static final String CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE = "Campos obrigatórios para cadastro de pedestres";
		
		public static final String VALIDAR_MATRICULAS_DUPLICADAS = " ";
		
		public static final String VALIDAR_CPF_DUPLICADO = "Validar CPF duplicado";

		public static final String VALIDAR_RG_DUPLICADO = "Validar RG duplicado";

		public static final String VALIDAR_CARTAO_ACESSO_DUPLICADO = "Validar cartão de acesso duplicado";

		public static final String CADASTRO_EM_LOTE = "Realiza cadastros em lote (limpa tela de cadastro ao finalizar)";
		
		public static final String PERMITIR_ACESSO_QR_CODE = "Permitir acesso via QR Code";
		
		public static final String CHAVE_DE_INTEGRACAO_COMTELE = "Chave de integração Comtele";
		
		public static final String DIAS_VALIDADE_LINK_CADASTRO_FACIAL_EXTERNO = "Geração de link para cadastro facial externo: validade do link (em dias)";
		
		public static final String EXIBE_CAMPO_SEMPRE_LIBERADO_PARA_TODOS = "Exibe escolha \"Sempre liberado\" para todos os usuários";
		
		public static final String HABILITA_ACESSO_POR_RECONHECIMENTO_FACIAL = "Habilita acesso por reconhecimento facial";

		public static final String LIMITE_DIGITAIS_PEDESTRE = "Limite de cadastro de digitais";
		
		public static final String HORARIO_DISPARO_SOC = "Horário de disparo da integração SOC (-1 para inativar)";
		
		
		public static final String PREENCHER_CARTAO_COM_MATRICULA = "preencher cartao com matricula";
		
		public static final String PREENCHER_CARTAO_AUTO = "preencher cartao automaticamente (8 digitos)";

		
		public static final String TIPO_QR_CODE = "Permitir acesso via QR Code: Habilita QRCode dinâmico";

		public static final String TIPO_QR_CODE_PADRAO = "Permitir acesso via QR Code: Tipo padrão";
		
		public static final String TEMPO_QRCODE_DINAMICO = "Permitir acesso via QR Code: Tempo para renovação do tipo QRCode Dinâmico por tempo (em minutos)";

		public static final String HABILITA_APP_PEDESTRE = "Habilita App do Pedestre";
		
		public static final String REMOVE_CARTAO_EXCLUIDO = "Remove o cartão dos Pedestres removidos";
		
		public static final String TEMPO_TOLERANCIA_ENTRADA_E_SAIDA = "Tempo de tolerancia (Entrada/Saida)";

	}
	
}
