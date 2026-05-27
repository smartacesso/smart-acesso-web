package com.rhid.services.catalogo;

import java.util.ArrayList;
import java.util.List;

/**
 * Catálogos RHID carregados por domínio (IDs internos da plataforma).
 */
public class RhidCatalogoDominio {

	private final List<RhidItemCatalogo> empresas = new ArrayList<>();
	private final List<RhidItemCatalogo> departamentos = new ArrayList<>();
	private final List<RhidItemCatalogo> cargos = new ArrayList<>();
	private final List<RhidItemCatalogo> horarios = new ArrayList<>();
	private final List<RhidItemCatalogo> centrosCusto = new ArrayList<>();

	public List<RhidItemCatalogo> getEmpresas() {
		return empresas;
	}

	public List<RhidItemCatalogo> getDepartamentos() {
		return departamentos;
	}

	public List<RhidItemCatalogo> getCargos() {
		return cargos;
	}

	public List<RhidItemCatalogo> getHorarios() {
		return horarios;
	}

	public List<RhidItemCatalogo> getCentrosCusto() {
		return centrosCusto;
	}

	public String resumo() {
		return "empresas=" + empresas.size() + ", departamentos=" + departamentos.size()
				+ ", cargos=" + cargos.size() + ", horários=" + horarios.size()
				+ ", centros de custo=" + centrosCusto.size();
	}
}
