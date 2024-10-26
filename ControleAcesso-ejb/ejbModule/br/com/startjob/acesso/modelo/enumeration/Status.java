package br.com.startjob.acesso.modelo.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;

public enum Status {
	
	ATIVO,
	INATIVO;

	public static List<SelectItem> montaListaStatus() {
		return Arrays.asList(values())
			.stream()
			.map(status -> new SelectItem(status, status.toString()))
			.collect(Collectors.toList());
	}
}
