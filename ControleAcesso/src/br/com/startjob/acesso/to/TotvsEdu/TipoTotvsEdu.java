package br.com.startjob.acesso.to.TotvsEdu;

public enum TipoTotvsEdu {
	
	ALUNO("EDUCACIONAL", "ALUNO"),
	FUNCIONARIO("FOLHA", "FUNCIONARIO"),
	RESPONSAVEL("RESPONSAVEL_EDUCACIONAL", "RESPONSAVEL"),
	RESPONSAVEL_FIN("RESPONSAVEL_FINANCEIRO", "RESPONSAVEL");
	
	private String tabelaRm;
	private String desc;
	
	private TipoTotvsEdu(String tabelaRm , String desc) {
		this.tabelaRm = tabelaRm;
		this.desc = desc;
	}

	public String getTabelaRm() {
		return tabelaRm;
	}

	public void setTabelaRm(String tabelaRm) {
		this.tabelaRm = tabelaRm;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
    public static TipoTotvsEdu fromTabelaRm(String tabelaRm) {
        for (TipoTotvsEdu tipo : TipoTotvsEdu.values()) {
            if (tipo.getTabelaRm().equalsIgnoreCase(tabelaRm)) {
                return tipo;
            }
        }
        return null;
    }

}
