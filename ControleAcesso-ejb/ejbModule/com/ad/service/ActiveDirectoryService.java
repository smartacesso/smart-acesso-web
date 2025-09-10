package com.ad.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import br.com.startjob.acesso.modelo.entity.IntegracaoADEntity;

public class ActiveDirectoryService {

	// private final static String url = "ldap://192.168.15.99:389"; // adicionar a
	// variavel da web
	// ldap://192.168.15.99:389
	private static String baseDn;
	private static String usuario;
	private static String senha;
	private static String url;

	public ActiveDirectoryService(String usuario, String senha, String baseDn, String url) {
		this.baseDn = baseDn;
		this.usuario = usuario;
		this.senha = senha;
		this.url = url;
	}

	public ActiveDirectoryService(IntegracaoADEntity integracaoAd) {
		this.baseDn = baseDn;
		this.usuario = integracaoAd.getUsuario();
		this.senha = integracaoAd.getSenha();
	}

	// Configuração de conexão com o AD
	private static DirContext connectToAD() throws Exception {
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url); // URL do servidor AD
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, usuario); // Usuário (e.g., cn=admin,dc=empresa,dc=com)
		env.put(Context.SECURITY_CREDENTIALS, senha); // Senha do usuário
		env.put(Context.REFERRAL, "ignore"); // Ignora referências
		return new InitialDirContext(env);
	}

	// Método para buscar usuários no AD
	public static List<String> buscarUsuarios() throws Exception {
		List<String> usuarios = new ArrayList<>();
		DirContext context = connectToAD();

		// Configurando os critérios da busca
		// String filtroBusca = "(objectClass=user)"; // Busca apenas objetos do tipo
		// "user"
		String filtroBusca = "(&(objectCategory=person)(objectClass=user))";
		// String filtroBusca = "(objectCategory=person)";
		String[] atributosDesejados = { "sAMAccountName", "displayName", "mail" }; // Campos que queremos buscar

		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Busca em toda a árvore
		searchControls.setReturningAttributes(atributosDesejados);

		// Realiza a busca no AD
		NamingEnumeration<SearchResult> results = context.search(baseDn, filtroBusca, searchControls);
		while (results.hasMore()) {
			SearchResult result = results.next();
			Attributes attrs = result.getAttributes();

			// Obtém os atributos do usuário
			String usernameAttr = attrs.get("sAMAccountName") != null ? attrs.get("sAMAccountName").get().toString()
					: "N/A";
			String displayName = attrs.get("displayName") != null ? attrs.get("displayName").get().toString() : "N/A";
			String email = attrs.get("mail") != null ? attrs.get("mail").get().toString() : "N/A";

			// Adiciona ao resultado
			usuarios.add(String.format("Usuário: %s, Nome: %s, Email: %s", usernameAttr, displayName, email));
		}

		context.close(); // Fecha a conexão com o AD
		return usuarios;
	}

}
