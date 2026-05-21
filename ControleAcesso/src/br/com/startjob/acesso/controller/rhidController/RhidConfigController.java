package br.com.startjob.acesso.controller.rhidController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.controller.BaseController;

@Named
@ViewScoped
public class RhidConfigController extends BaseController {

    private static final long serialVersionUID = 1L;

    // --- Configurações Globais ---
    private String emailGlobal;
    private String senhaGlobal;
    private String regraPeriodo;
    private String recorrencia;

    // --- Inserção de Novo Domínio ---
    private String novoDominio;
    private String novoIds;

    // --- Tabela de Domínios ---
    private List<DominioRhidDTO> listaDominios;

    @PostConstruct
    public void init() {
        // Inicializa a lista para evitar NullPointerException na tabela PrimeFaces
        listaDominios = new ArrayList<>();
        
        System.out.println("RhidConfigController carregado!");
        
        // TODO: Aqui você fará a busca no banco de dados para preencher as variáveis globais 
        // e a listaDominios caso o usuário já tenha salvo anteriormente.
    }

    /**
     * Salva as configurações globais de acesso e agendamento.
     */
    public void salvarConfiguracaoGlobal() {
        try {
            // TODO: Implementar a lógica de salvar as credenciais e a lista no banco de dados
            
            // Exibe mensagem de sucesso nativa do seu BaseController
            System.out.println("Configurações e domínios salvos com sucesso!");
        } catch (Exception e) {
        	System.out.println( "Falha ao salvar as configurações: ");
        }
    }

    /**
     * Adiciona um novo domínio na lista em memória (tabela).
     */
    public void adicionarDominio() {
        if (novoDominio != null && !novoDominio.trim().isEmpty() && novoIds != null && !novoIds.trim().isEmpty()) {
            
            // Adiciona na lista
            listaDominios.add(new DominioRhidDTO(novoDominio, novoIds));
            
            // Limpa os campos de input
            novoDominio = "";
            novoIds = "";
            
            System.out.println( "Domínio incluído na lista. Lembre-se de Salvar as Configurações.");
        } else {
        	System.out.println( "Preencha o nome do Domínio e o Payload (IDs).");
        }
    }

    /**
     * Remove um domínio da lista em memória (tabela).
     */
    public void removerDominio(DominioRhidDTO dominio) {
        listaDominios.remove(dominio);
        System.out.println( "Domínio removido da lista.");
    }

    public String getEmailGlobal() {
        return emailGlobal;
    }

    public void setEmailGlobal(String emailGlobal) {
        this.emailGlobal = emailGlobal;
    }

    public String getSenhaGlobal() {
        return senhaGlobal;
    }

    public void setSenhaGlobal(String senhaGlobal) {
        this.senhaGlobal = senhaGlobal;
    }

    public String getRegraPeriodo() {
        return regraPeriodo;
    }

    public void setRegraPeriodo(String regraPeriodo) {
        this.regraPeriodo = regraPeriodo;
    }

    public String getRecorrencia() {
        return recorrencia;
    }

    public void setRecorrencia(String recorrencia) {
        this.recorrencia = recorrencia;
    }

    public String getNovoDominio() {
        return novoDominio;
    }

    public void setNovoDominio(String novoDominio) {
        this.novoDominio = novoDominio;
    }

    public String getNovoIds() {
        return novoIds;
    }

    public void setNovoIds(String novoIds) {
        this.novoIds = novoIds;
    }

    public List<DominioRhidDTO> getListaDominios() {
        return listaDominios;
    }

    public void setListaDominios(List<DominioRhidDTO> listaDominios) {
        this.listaDominios = listaDominios;
    }

    // =========================================================
    // DTO Interno para alimentar a Tabela (pode ser movido para arquivo separado)
    // =========================================================
    public static class DominioRhidDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String nome;
        private String ids;

        public DominioRhidDTO() {}

        public DominioRhidDTO(String nome, String ids) {
            this.nome = nome;
            this.ids = ids;
        }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getIds() { return ids; }
        public void setIds(String ids) { this.ids = ids; }
    }
}