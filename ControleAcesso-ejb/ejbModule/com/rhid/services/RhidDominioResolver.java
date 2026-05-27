package com.rhid.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rhid.services.dto.RhidFuncionarioExternoDTO;

import br.com.startjob.acesso.modelo.entity.DominioRhidEntity;

/**
 * Define em qual(is) domínio(s) RHID cada funcionário deve ser exportado.
 * O roteamento usa apenas {@code nomeDominio} do funcionário (parâmetro domain do login).
 * {@code idCompany} identifica a empresa dentro do domínio, não o domínio em si.
 */
public final class RhidDominioResolver {

	private RhidDominioResolver() {
	}

	public static Map<DominioRhidEntity, List<RhidFuncionarioExternoDTO>> agruparPorDominio(
			List<RhidFuncionarioExternoDTO> funcionarios, List<DominioRhidEntity> dominios,
			RhidSemDominioAcaoEnum semDominioAcao) {

		Map<DominioRhidEntity, List<RhidFuncionarioExternoDTO>> agrupados = new LinkedHashMap<>();
		for (DominioRhidEntity dominio : dominios) {
			agrupados.put(dominio, new ArrayList<>());
		}

		RhidSemDominioAcaoEnum acao = semDominioAcao != null ? semDominioAcao : RhidSemDominioAcaoEnum.IGNORAR;

		for (RhidFuncionarioExternoDTO funcionario : funcionarios) {
			List<DominioRhidEntity> destinos = resolverDestinos(funcionario, dominios, acao);
			for (DominioRhidEntity dominio : destinos) {
				agrupados.get(dominio).add(funcionario);
			}
		}
		return agrupados;
	}

	/**
	 * @return lista vazia quando o funcionário deve ser ignorado (sem nomeDominio e config IGNORAR)
	 */
	public static List<DominioRhidEntity> resolverDestinos(RhidFuncionarioExternoDTO funcionario,
			List<DominioRhidEntity> dominios, RhidSemDominioAcaoEnum semDominioAcao) {
		if (dominios == null || dominios.isEmpty()) {
			throw new IllegalStateException("Nenhum domínio RHID configurado.");
		}

		if (funcionario.getNomeDominio() != null && !funcionario.getNomeDominio().trim().isEmpty()) {
			String alvo = funcionario.getNomeDominio().trim();
			for (DominioRhidEntity dominio : dominios) {
				if (alvo.equalsIgnoreCase(dominio.getNomeDominio())) {
					return Collections.singletonList(dominio);
				}
			}
			throw new IllegalStateException("Domínio '" + alvo + "' não está cadastrado na configuração RHID.");
		}

		RhidSemDominioAcaoEnum acao = semDominioAcao != null ? semDominioAcao : RhidSemDominioAcaoEnum.IGNORAR;
		if (acao == RhidSemDominioAcaoEnum.ENVIAR_TODOS) {
			return new ArrayList<>(dominios);
		}
		return Collections.emptyList();
	}

	public static String descreverAcaoSemDominio(RhidSemDominioAcaoEnum semDominioAcao) {
		RhidSemDominioAcaoEnum acao = semDominioAcao != null ? semDominioAcao : RhidSemDominioAcaoEnum.IGNORAR;
		return acao.getLabel();
	}
}
