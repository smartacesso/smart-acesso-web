package br.com.startjob.acesso.to.TotvsEdu;

public enum NivelDeEnsino {
    EDUCACAO_BASICA("Educação Básica");
	
    private final String nivel;

    NivelDeEnsino(String nivel) {
        this.nivel = nivel;
    }

    public String getNivel() {
        return nivel;
    }
}
