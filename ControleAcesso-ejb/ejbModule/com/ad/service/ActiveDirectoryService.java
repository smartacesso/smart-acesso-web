package com.ad.service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ActiveDirectoryService {
	
	private final static String url = "ldap://192.168.15.99:389";
	private static String baseDn;
	private static String usuario;
	private static String senha;

	public ActiveDirectoryService(String usuario, String senha, String baseDn) {
		this.baseDn = baseDn;
		this.usuario = usuario;
		this.senha = senha;
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
    public static List<String> buscarUsuarios(String url, String baseDn, String username, String password) throws Exception {
        List<String> usuarios = new ArrayList<>();
        DirContext context = connectToAD();

        // Configurando os critérios da busca
        // String filtroBusca = "(objectClass=user)"; // Busca apenas objetos do tipo "user"
        String filtroBusca = "(&(objectCategory=person)(objectClass=user))";
        // String filtroBusca = "(objectCategory=person)";
        String[] atributosDesejados = {"sAMAccountName", "displayName", "mail"}; // Campos que queremos buscar

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Busca em toda a árvore
        searchControls.setReturningAttributes(atributosDesejados);

        // Realiza a busca no AD
        NamingEnumeration<SearchResult> results = context.search(baseDn, filtroBusca, searchControls);
        while (results.hasMore()) {
            SearchResult result = results.next();
            Attributes attrs = result.getAttributes();

            // Obtém os atributos do usuário
            String usernameAttr = attrs.get("sAMAccountName") != null ? attrs.get("sAMAccountName").get().toString() : "N/A";
            String displayName = attrs.get("displayName") != null ? attrs.get("displayName").get().toString() : "N/A";
            String email = attrs.get("mail") != null ? attrs.get("mail").get().toString() : "N/A";

            // Adiciona ao resultado
            usuarios.add(String.format("Usuário: %s, Nome: %s, Email: %s", usernameAttr, displayName, email));
            System.out.println("nome : " + usernameAttr);
        }

        context.close(); // Fecha a conexão com o AD
        return usuarios;
    }

    public static void main(String[] args) {
        try {
            // Configurações do AD
            //String baseDn = "dc=smart,dc=local";
        	String baseDn = "cn=Users,dc=smart,dc=local";
            String username = "cn=Administrator,cn=Users,dc=smart,dc=local";
            String password = "desenvolvimento@2024";
            
            ActiveDirectoryService activeDirectoryService = new ActiveDirectoryService(baseDn,username, password);

            // Busca e exibe os usuários
            List<String> usuarios = ActiveDirectoryService.buscarUsuarios(url, baseDn, username, password);
            usuarios.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
