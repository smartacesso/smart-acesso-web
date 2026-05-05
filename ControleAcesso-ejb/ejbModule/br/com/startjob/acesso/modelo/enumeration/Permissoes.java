package br.com.startjob.acesso.modelo.enumeration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Permissoes {

    _0(Listas.TODOS),
    _1(Listas.MATRIZ),
    _2(Listas.MATRIZ),
    _3(Listas.MATRIZ),
    _4(Listas.UBV),
    _5(Listas.UBV),
    _6(Listas.NENHUM),

    _7(Listas.TODOS),
    _8(Listas.TODOS),
    _9(Listas.TODOS),
    _10(Listas.TODOS),
    _11(Listas.MATRIZ),
    _12(Listas.MATRIZ),
    _13(Listas.TODOS),
    _14(Listas.TODOS),
    _15(Listas.TODOS),
    _16(Listas.MATRIZ),
    _17(Listas.UBV),
    _18(Listas.UBV),
    _19(Listas.TODOS),
    _20(Listas.TODOS),
    _21(Listas.TODOS),
    _22(Listas.UBV);

    private final List<String> equipamentos;

    Permissoes(List<String> equipamentos) {
        this.equipamentos = equipamentos;
    }

    public List<String> getEquipamentos() {
        return equipamentos;
    }

    public static List<String> fromCodigo(int codigo) {
        try {
            return Permissoes.valueOf("_" + codigo).getEquipamentos();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Código inválido: " + codigo);
        }
    }

    // 👇 Classe auxiliar para evitar problema de ordem
    private static class Listas {
        private static final List<String> MATRIZ = Collections.unmodifiableList(Arrays.asList(
                "MATRIZ PORTARIA",
                "PORTAO MATRIZ",
                "MATRIZ REFEITORIO ENTRADA",
                "MATRIZ REFEITORIO SAIDA"
        ));

        private static final List<String> UBV = Collections.unmodifiableList(Arrays.asList(
        		 "BELA VISTA PORTARIA",
                 "BELA VISTA REFEITORIO SAIDA",
                 "BELA VISTA REFEITORIO ENTRADA"
        ));

        private static final List<String> TODOS = Collections.unmodifiableList(Arrays.asList(
                "MATRIZ PORTARIA",
                "PORTAO MATRIZ",
                "MATRIZ REFEITORIO ENTRADA",
                "MATRIZ REFEITORIO SAIDA",
                "BELA VISTA PORTARIA",
                "BELA VISTA REFEITORIO SAIDA",
                "BELA VISTA REFEITORIO ENTRADA"
        ));

        private static final List<String> NENHUM = Collections.emptyList();
    }
}