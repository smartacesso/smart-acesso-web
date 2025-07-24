package br.com.startjob.acesso.to.TotvsEdu;

public enum PermissoesEduTotvs {
	
    STATUS_176(176, "LIBERADO"),
    STATUS_177(177, "LIBERADO"),
    STATUS_222(222, "LIBERADO"),
    STATUS_236(236, "LIBERADO"),
    STATUS_239(239, "LIBERADO");

    private final int codStatus;
    private final String status;

    private PermissoesEduTotvs(int codStatus, String status) {
        this.codStatus = codStatus;
        this.status = status;
    }

    public int getCodStatus() {
        return codStatus;
    }

    public String getStatus() {
        return status;
    }

    // Verifica se o código está liberado
    public static boolean isPermitido(int codStatus) {
        for (PermissoesEduTotvs permissao : values()) {
            if (permissao.codStatus == codStatus && "LIBERADO".equalsIgnoreCase(permissao.status)) {
                return true;
            }
        }
        return false;
    }

    // Retorna a descrição do status pelo código
    public static String getDescricaoStatus(int codStatus) {
        for (PermissoesEduTotvs permissao : values()) {
            if (permissao.codStatus == codStatus) {
                return permissao.status;
            }
        }
        return "DESCONHECIDO";
    }
}