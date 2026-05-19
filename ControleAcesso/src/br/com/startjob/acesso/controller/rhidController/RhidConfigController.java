package br.com.startjob.acesso.controller.rhidController;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped; // IMPORTANTE: O ViewScoped correto para CDI

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.services.rhid.RhidService;

@Named
@ViewScoped
public class RhidConfigController extends BaseController{

    private static final long serialVersionUID = 1L;

    private String email;
    private String senha;
    private String dominio;
    private String resultado;
    private RhidService rhidService;
    
    
    @PostConstruct
    public void init() {
        rhidService = new RhidService("https://rhid.com.br/v2");
        System.out.println("Bean carregado - Se o escopo estiver certo, isso aparece só 1x por tela!");
    }

    public void testarConexao() {
        try {
            // Inicializa o serviço com o que o usuário digitou nos inputs da tela
            rhidService.inicializarCredenciais(email, senha, dominio);
            
            // Força a autenticação para validar se as credenciais estão certas
            this.resultado = rhidService.loginAutenticar(); 
        } catch (Exception e) {
            this.resultado = "Erro: " + e.getMessage();
        }
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}