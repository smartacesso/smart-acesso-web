package br.com.startjob.acesso.to.TotvsEdu;

public enum PermissoesEduTotvs {

    STATUS_176("176", "LIBERADO"),
    STATUS_177("177", "LIBERADO"),
    STATUS_222("222", "LIBERADO"),
    STATUS_236("236", "LIBERADO"),
    STATUS_239("239", "LIBERADO"),
    STATUS_A("A", "LIBERADO");

    private final String codStatus;
    private final String status;

    private PermissoesEduTotvs(String codStatus, String status) {
        this.codStatus = codStatus;
        this.status = status;
    }

    // Verifica se o código está liberado
    public static boolean isPermitido(String codStatus) {
        if (codStatus == null) return false;
        for (PermissoesEduTotvs permissao : values()) {
            if (permissao.codStatus.equalsIgnoreCase(codStatus)
                    && "LIBERADO".equalsIgnoreCase(permissao.status)) {
                return true;
            }
        }
        return false;
    }

    // Retorna a descrição do status pelo código
    public static String getDescricaoStatus(String codStatus) {
        if (codStatus == null) return "DESCONHECIDO";
        for (PermissoesEduTotvs permissao : values()) {
            if (permissao.codStatus.equalsIgnoreCase(codStatus)) {
                return permissao.status;
            }
        }
        return "DESCONHECIDO";
    }

    public String getCodStatus() {
        return codStatus;
    }

    public String getStatus() {
        return status;
    }
}
