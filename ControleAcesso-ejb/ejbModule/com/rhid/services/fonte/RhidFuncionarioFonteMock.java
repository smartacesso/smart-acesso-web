package com.rhid.services.fonte;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.rhid.services.dto.RhidFuncionarioExternoDTO;

/**
 * Fonte mockada até a API externa estar disponível.
 * IDs RHID são resolvidos via catálogos (cnpjEmpresa, nomeHorario, etc.).
 */
public class RhidFuncionarioFonteMock implements RhidFuncionarioFonte {

	@Override
	public List<RhidFuncionarioExternoDTO> buscarTodos() {
		return criarFuncionariosMock();
	}

	@Override
	public List<RhidFuncionarioExternoDTO> buscarAlteradosDesde(Date dataReferencia) {
		if (dataReferencia == null) {
			return buscarTodos();
		}
		java.time.LocalDate ref = com.rhid.services.fonte.totvs.RhidTotvsDataUtil.toLocalDate(dataReferencia);
		return criarFuncionariosMock().stream()
				.filter(f -> {
					if (f.getDataAlteracao() == null || ref == null) {
						return false;
					}
					java.time.LocalDate alteracao = com.rhid.services.fonte.totvs.RhidTotvsDataUtil
							.toLocalDate(f.getDataAlteracao());
					return alteracao != null && ref != null && !alteracao.isBefore(ref);
				})
				.collect(Collectors.toList());
	}

	private List<RhidFuncionarioExternoDTO> criarFuncionariosMock() {
		List<RhidFuncionarioExternoDTO> lista = new ArrayList<>();

		// Ajuste cnpjEmpresa/nomeHorario para bater com os cadastros do domínio RHID de teste
		lista.add(criar("EXT001", "João da Silva", "22900423668", "922900423668", "MAT001",
				"direcional1", null, 0, "16614075000879", "Horário Comercial", "Engenharia", null, null,
				diasAtras(30), diasAtras(30)));

		lista.add(criar("EXT002", "Maria Oliveira", "13045678901", "913045678901", "MAT002",
				"direcional1", null, 0, "16614075000879", "Horário ", "Engenharia", "CC Administrativo", null,
				diasAtras(60), diasAtras(60)));

		lista.add(criar("EXT003", "Carlos Souza", "20345678901", "920345678901", "MAT003",
				"direcional1", null, 0, "16614075000879", "Horário Comercial", null, null, null,
				diasAtras(15), diasAtras(1)));

		lista.add(criar("EXT004", "Ana Paula Costa", "30456789012", "930456789012", "MAT004",
				"direcional2", null, 0, "16614075000100", "Horário Comercial", null, null, null,
				diasAtras(10), diasAtras(0)));

		lista.add(criar("EXT005", "Pedro Sem Domínio", "11144477735", "911144477735", "MAT005",
				null, null, 0, "16614075000100", "Horário Comercial", null, null, null,
				diasAtras(5), diasAtras(0)));

		return lista;
	}

	private RhidFuncionarioExternoDTO criar(String idExterno, String nome, String cpf, String pis, String matricula,
			String nomeDominio, String email, int status, String cnpjEmpresa, String nomeHorario,
			String nomeDepartamento, String nomeCentroCusto, String nomeCargo, Date admissao, Date alteracao) {
		RhidFuncionarioExternoDTO dto = new RhidFuncionarioExternoDTO();
		dto.setIdExterno(idExterno);
		dto.setNome(nome.toUpperCase());
		dto.setCpf(cpf);
		dto.setPis(pis);
		dto.setMatricula(matricula);
		dto.setNomeDominio(nomeDominio);
		dto.setEmail(email);
		dto.setStatus(status);
		dto.setCnpjEmpresa(cnpjEmpresa);
		dto.setNomeHorario(nomeHorario);
		dto.setNomeDepartamento(nomeDepartamento);
		dto.setNomeCentroCusto(nomeCentroCusto);
		dto.setNomeCargo(nomeCargo);
		dto.setDataAdmissao(admissao);
		dto.setDataAlteracao(alteracao);
		return dto;
	}

	private Date diasAtras(int dias) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -dias);
		return cal.getTime();
	}
}
