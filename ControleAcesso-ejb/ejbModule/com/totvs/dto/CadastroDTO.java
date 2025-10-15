package com.totvs.dto;

import java.util.Date;

public class CadastroDTO {

	private int codColigada;         // numerico
    private String nomeColigada;     // string
    private String cnpjColigada;     // string
    private int codFilial;           // numerico
    private String nomeFilial;       // string
    private String cnpjFilial;       // string
    private String nome;             // string
    private String matricula;        // string
    private String cpf;              // string
    private String codStatus;        // string (alguns casos pode ser letra, ex: "A")
    private String status;           // string
    private String nivelEnsino;      // string (pode vir vazio ou `<NIVELENSINO />`)
    private int idHabilitacaoFilial; // numerico (pode não vir em todos os resultados)
    private Date dataAlteracao;      // Date
    private int idPerLet;            // numerico (pode não vir em todos os resultados)
    private String origem;           // string (EDUCACIONAL, FOLHA, etc.)

    // Getters e setters

    public int getCodColigada() { return codColigada; }
    public void setCodColigada(int codColigada) { this.codColigada = codColigada; }

    public String getNomeColigada() { return nomeColigada; }
    public void setNomeColigada(String nomeColigada) { this.nomeColigada = nomeColigada; }

    public String getCnpjColigada() { return cnpjColigada; }
    public void setCnpjColigada(String cnpjColigada) { this.cnpjColigada = cnpjColigada; }

    public int getCodFilial() { return codFilial; }
    public void setCodFilial(int codFilial) { this.codFilial = codFilial; }

    public String getNomeFilial() { return nomeFilial; }
    public void setNomeFilial(String nomeFilial) { this.nomeFilial = nomeFilial; }

    public String getCnpjFilial() { return cnpjFilial; }
    public void setCnpjFilial(String cnpjFilial) { this.cnpjFilial = cnpjFilial; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCodStatus() { return codStatus; }
    public void setCodStatus(String codStatus) { this.codStatus = codStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNivelEnsino() { return nivelEnsino; }
    public void setNivelEnsino(String nivelEnsino) { this.nivelEnsino = nivelEnsino; }

    public int getIdHabilitacaoFilial() { return idHabilitacaoFilial; }
    public void setIdHabilitacaoFilial(int idHabilitacaoFilial) { this.idHabilitacaoFilial = idHabilitacaoFilial; }

    public Date getDataAlteracao() { return dataAlteracao; }
    public void setDataAlteracao(Date dataAlteracao) { this.dataAlteracao = dataAlteracao; }

    public int getIdPerLet() { return idPerLet; }
    public void setIdPerLet(int idPerLet) { this.idPerLet = idPerLet; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
}
