package br.com.startjob.acesso.modelo.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Permissoes {
	_0(new ArrayList<>(Arrays.asList())),
	_1(new ArrayList<>(Arrays.asList("ADMINISTRATIVO"))),
	_2(new ArrayList<>(Arrays.asList("PRODUÇÃO"))),
	_3(new ArrayList<>(Arrays.asList("EXPEDIÇÃO"))),
	_4(new ArrayList<>(Arrays.asList("UBV EXPEDIÇÃO"))),
	_5(new ArrayList<>(Arrays.asList("UBV FATURAMENTO"))),
	_6(new ArrayList<>(Arrays.asList("CD JUIZ DE FORA"))),
	_18(new ArrayList<>(Arrays.asList("PORTARIA UBV"))),
	
	_7(new ArrayList<>(Arrays.asList("ADMINISTRATIVO", "UBV EXPEDIÇÃO"))),
	_8(new ArrayList<>(Arrays.asList("ADMINISTRATIVO", "PRODUÇÃO"))),
	_9(new ArrayList<>(Arrays.asList("ADMINISTRATIVO", "PRODUÇÃO","EXPEDIÇÃO","UBV EXPEDIÇÃO","UBV FATURAMENTO"))),
	_10(new ArrayList<>(Arrays.asList("ADMINISTRATIVO", "PRODUÇÃO","EXPEDIÇÃO","UBV EXPEDIÇÃO","UBV FATURAMENTO","CD JUIZ DE FORA"))),
	_11(new ArrayList<>(Arrays.asList("PRODUÇÃO","EXPEDIÇÃO"))),
	_12(new ArrayList<>(Arrays.asList("PORTARIA"))),
	_13(new ArrayList<>(Arrays.asList("EXPEDIÇÃO", "UBV EXPEDIÇÃO"))),
	_14(new ArrayList<>(Arrays.asList("UBV EXPEDIÇÃO", "UBV FATURAMENTO","EXPEDIÇÃO"))),
	_15(new ArrayList<>(Arrays.asList("UBV EXPEDIÇÃO", "UBV FATURAMENTO","PRODUÇÃO"))),
	_16(new ArrayList<>(Arrays.asList("ADMINISTRATIVO", "EXPEDIÇÃO"))),
	_17(new ArrayList<>(Arrays.asList("UBV EXPEDIÇÃO", "UBV FATURAMENTO"))),
	_19(new ArrayList<>(Arrays.asList("PORTARIA", "PORTARIA UBV"))), //ESTA UBV PORTARIA 1 MAS NAO TEM ESSE INDIVIDUAL
	_20(new ArrayList<>(Arrays.asList("UBV FATURAMENTO", "UBV EXPEDIÇÃO","ADMINISTRATIVO")));
	
	
	private List<String> equipamentos;
	
	Permissoes(List<String> equipamentos){
		this.equipamentos = equipamentos;
	}

	public List<String> getEquipamentos() {
		return equipamentos;
	}

	public void setEquipamentos(List<String> equipamentos) {
		this.equipamentos = equipamentos;
	}
	
	 public static List<String> fromCodigo(int codigo) {
	        switch (codigo) {
//	        	case 0: return _0.getEquipamentos();
	            case 1: return _1.getEquipamentos();
	            case 2: return _2.getEquipamentos();
	            case 3: return _3.getEquipamentos();
	            case 4: return _4.getEquipamentos();
	            case 5: return _5.getEquipamentos();
	            case 6: return _6.getEquipamentos();
	            case 7: return _7.getEquipamentos();
	            case 8: return _8.getEquipamentos();
	            case 9: return _9.getEquipamentos();
	            case 10: return _10.getEquipamentos();
	            case 11: return _11.getEquipamentos();
	            case 12: return _12.getEquipamentos();
	            case 13: return _13.getEquipamentos();
	            case 14: return _14.getEquipamentos();
	            case 15: return _15.getEquipamentos();
	            case 16: return _16.getEquipamentos();
	            case 17: return _17.getEquipamentos();
	            case 18: return _18.getEquipamentos();
	            case 19: return _19.getEquipamentos();
	            case 20: return _20.getEquipamentos();
	            default: throw new IllegalArgumentException("Código de permissão inválido: " + codigo);
	        }
	 }
	
}
