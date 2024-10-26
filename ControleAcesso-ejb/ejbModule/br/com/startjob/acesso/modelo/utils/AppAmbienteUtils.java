package br.com.startjob.acesso.modelo.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Classe utilitária para configuração de 
 * ambientes (produção e demais ambientes)
 * 
 * @author gustavo
 *
 */
public class AppAmbienteUtils {
	
	public static final String IND_AMBIENTE_TESTE = "test"; 
	public static final String IND_AMBIENTE_PROD  = "prod";
	
	//definições padrão
	private static final String CONFIG_FILE 					= "/ambiente-config.properties";
	
	
	//configuraçĩoes do arquivo do ambiente
	public static final String CONFIG_AMBIENTE			     = "config.ambiente";
	public static final String CONFIG_AMBIENTE_SERVICES      = "config.ambiente.servicos";
	public static final String CONFIG_AMBIENTE_FACEBOOK 	 = "config.ambiente.facebook";
	public static final String CONFIG_AMBIENTE_TWITTER 		 = "config.ambiente.twitter";
	public static final String CONFIG_AMBIENTE_GPLUS 		 = "config.ambiente.gplus";
	public static final String CONFIG_AMBIENTE_INTAGRAM 	 = "config.ambiente.instagram";
	public static final String CONFIG_AMBIENTE_MAIN_SITE     = "config.ambiente.main.site";
	public static final String CONFIG_AMBIENTE_MAIN_LINK     = "config.ambiente.main.link";
	public static final String CONFIG_AMBIENTE_SITE     	 = "config.ambiente.site";
	public static final String CONFIG_AMBIENTE_SUPPORT_EMAIL = "config.ambiente.support.email";
	public static final String CONFIG_AMBIENTE_NOME_APP 	 = "config.ambiente.nome.app";
	public static final String CONFIG_AMBIENTE_NOREPLY_EMAIL = "config.ambiente.noreply.email";
	public static final String CONFIG_AMBIENTE_BLOG 		 = "config.ambiente.blog";
	public static final String CONFIG_AMBIENTE_SLOGAN 		 = "config.ambiente.slogan";
	
	//configuração de academia gestora
	public static final String CONFIG_MANAGEMENT_PATH        = "config.management.academy.path";
	public static final String CONFIG_MANAGEMENT_ID  	     = "config.management.academy.include.id";
	public static final String CONFIG_MANAGEMENT_ID_SUB	     = "config.management.academy.include.id.sub";
	public static final String CONFIG_MANAGEMENT_ID_EXT		 = "config.management.academy.include.id.ext";
	
	
	public static final String CONFIG_MANAGEMENT_EXCLUDE_IDS = "config.management.academy.exclude.ids";
	
	//configuração das pastas de resources
	private static final String CONFIG_RESOURCE_FOLDER_PROD = "config.resources.folder.prod";
	private static final String CONFIG_RESOURCE_FOLDER_DEV  = "config.resources.folder.dev";
	
	public static final String CONFIG_TEMPO_DISPOSITIVO = "config.tempo.dispositivo";
	public static final String CONFIG_AMBIENTE_SGDB = "config.sgdb.utilizado";
	public static final String CONFIG_AMBIENTE_SCHEMA = "config.schema.name";
	public static final String CONFIG_AMBIENTE_SQL_SERVER_VESION = "config.sqlserver.version";
	
	/**
	 * Retorna se ambiente configurado é de produção
	 * ou de teste
	 * @return true para ambiente de produção
	 */
	public static boolean isProdution(){
		
		//esta em produção
		if(IND_AMBIENTE_PROD.equals(getConfig(CONFIG_AMBIENTE))) 
			return true;
		
		return false;
	}
	
	/**
	 * Retorna se ambiente configurado é de produção
	 * ou de teste
	 * @return true para ambiente de produção
	 */
	public static boolean isServices(){
		
		//esta em produção
		if("S".equals(getConfig(CONFIG_AMBIENTE_SERVICES))) 
			return true;
		
		return false;
	}
	
	public static String getResourcesFolder(){
		if(isProdution())
			return getConfig(CONFIG_RESOURCE_FOLDER_PROD);
		else
			return getConfig(CONFIG_RESOURCE_FOLDER_DEV);
	}
	
	/**
	 * Recupera configuração disponível no arquivo
	 * de configurações do ambiente
	 * 
	 * @param key - chave da configuração
	 * @return configuração
	 * 
	 */
	public static String getConfig(String key){
		
		try{
			//verifica existencia do arquivo
			InputStream is = AppAmbienteUtils.class.getResourceAsStream(CONFIG_FILE);  
			if(is != null){
				Properties properties = new Properties(); 
				properties.load(is);
				return properties.getProperty(key);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
