package br.com.startjob.acesso.to.TotvsEdu;
public enum OrigemStatusTotvs {
	
    RESPONSAVEL_MAE(3, "RESPONSAVEL_MAE"),
    RESPONSAVEL_PAI(4, "RESPONSAVEL_PAI"),
    RESPONSAVEL_TEMPORARIO(5, "RESPONSAVEL_TEMPORARIO"),
    RESPONSAVEL_FIN(6, "RESPONSAVEL_FINANCEIRO");

    private final int codigo;
    private final String origem;

    OrigemStatusTotvs(int codigo, String origem) {
        this.codigo = codigo;
        this.origem = origem;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getOrigem() {
        return origem;
    }


    // 🔥 MÉTODO PARA RETORNAR O CÓDIGO A PARTIR DO TIPO
    public static int getCodigoPorTipo(String origem) {
        for (OrigemStatusTotvs o : values()) {
            if (o.origem.equalsIgnoreCase(origem)) {
                return o.codigo;
            }
        }
        throw new IllegalArgumentException("Tipo inválido: " + origem);
    }
}
