package br.com.startjob.acesso.controller.rhidController;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.services.rhid.RhidService;

@Named("rhidConfigController")
@ViewScoped
public class RhidConfigController extends BaseController implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private String senha;
    private String dominio;
    private String resultado;

    private final RhidService rhidService = new RhidService("https://rhid.com.br/v2");

    public void testarConexao() {

        try {

            this.resultado = rhidService.login(email, senha, dominio);

            System.out.println("Login realizado com sucesso!");

        } catch (Exception e) {

            e.printStackTrace();

            this.resultado = "Erro: " + e.getMessage();
        }
    }

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