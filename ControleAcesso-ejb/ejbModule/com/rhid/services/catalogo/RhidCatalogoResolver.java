package com.rhid.services.catalogo;

import java.util.List;

import com.rhid.services.dto.RhidFuncionarioExternoDTO;

/**
 * Resolve IDs internos RHID a partir de catálogos carregados no domínio.
 */
public final class RhidCatalogoResolver {

	private RhidCatalogoResolver() {
	}

	public static void aplicarIdsInternos(RhidFuncionarioExternoDTO funcionario, RhidCatalogoDominio catalogo) {
		if (funcionario.getIdCompany() == null) {
			funcionario.setIdCompany(resolverEmpresaPorCnpj(funcionario, catalogo));
		}
		if (funcionario.getIdShift() == null) {
			funcionario.setIdShift(resolverHorario(funcionario, catalogo));
		}
		if (funcionario.getIdDepartment() == null && funcionario.getNomeDepartamento() != null) {
			funcionario.setIdDepartment(
					resolverPorNomeOpcional(catalogo.getDepartamentos(), funcionario.getNomeDepartamento()));
		}
		if (funcionario.getIdCostCenter() == null && funcionario.getNomeCentroCusto() != null) {
			funcionario.setIdCostCenter(
					resolverPorNomeOpcional(catalogo.getCentrosCusto(), funcionario.getNomeCentroCusto()));
		}
		if (funcionario.getIdPersonRole() == null && funcionario.getNomeCargo() != null) {
			funcionario.setIdPersonRole(resolverPorNomeOpcional(catalogo.getCargos(), funcionario.getNomeCargo()));
		}
	}

	private static Integer resolverEmpresaPorCnpj(RhidFuncionarioExternoDTO funcionario, RhidCatalogoDominio catalogo) {
		String cnpj = normalizarCnpj(funcionario.getCnpjEmpresa());
		if (cnpj == null) {
			throw new IllegalStateException("Informe idCompany ou cnpjEmpresa (funcionário "
					+ funcionario.getIdExterno() + "). A busca de empresa no RHID é feita somente por CNPJ.");
		}

		RhidItemCatalogo encontrada = null;
		for (RhidItemCatalogo empresa : catalogo.getEmpresas()) {
			if (cnpj.equals(normalizarCnpj(empresa.getCnpj()))) {
				if (encontrada != null) {
					throw new IllegalStateException("Mais de uma empresa RHID com CNPJ " + funcionario.getCnpjEmpresa()
							+ " (funcionário " + funcionario.getIdExterno() + ").");
				}
				encontrada = empresa;
			}
		}
		if (encontrada != null) {
			return encontrada.getId();
		}
		throw new IllegalStateException("Empresa não encontrada no RHID para CNPJ " + funcionario.getCnpjEmpresa()
				+ " (funcionário " + funcionario.getIdExterno() + ").");
	}

	private static Integer resolverHorario(RhidFuncionarioExternoDTO funcionario, RhidCatalogoDominio catalogo) {
		List<RhidItemCatalogo> horarios = catalogo.getHorarios();
		String nome = textoNormalizado(funcionario.getNomeHorario());
		if (nome != null) {
			for (RhidItemCatalogo item : horarios) {
				if (nome.equals(textoNormalizado(item.getName()))) {
					return item.getId();
				}
			}
		}
		if (horarios.isEmpty()) {
			throw new IllegalStateException("Nenhum horário cadastrado no RHID (funcionário "
					+ funcionario.getIdExterno() + ").");
		}
		return horarios.get(0).getId();
	}

	/** Campos opcionais: se não houver match único no catálogo, não vincula (retorna null). */
	private static Integer resolverPorNomeOpcional(List<RhidItemCatalogo> itens, String alvo) {
		String nome = textoNormalizado(alvo);
		if (nome == null) {
			return null;
		}
		RhidItemCatalogo encontrado = null;
		for (RhidItemCatalogo item : itens) {
			if (nome.equals(textoNormalizado(item.getName()))) {
				if (encontrado != null) {
					return null;
				}
				encontrado = item;
			}
		}
		return encontrado != null ? encontrado.getId() : null;
	}

	private static String normalizarCnpj(String valor) {
		String numeros = normalizarNumeros(valor);
		if (numeros == null || numeros.isEmpty()) {
			return null;
		}
		if (numeros.length() > 14) {
			numeros = numeros.substring(numeros.length() - 14);
		}
		while (numeros.length() < 14) {
			numeros = "0" + numeros;
		}
		return numeros;
	}

	private static String textoNormalizado(String valor) {
		if (valor == null) {
			return null;
		}
		String t = valor.trim();
		return t.isEmpty() ? null : t.toLowerCase();
	}

	private static String normalizarNumeros(String valor) {
		if (valor == null) {
			return null;
		}
		return valor.replaceAll("\\D", "");
	}
}
