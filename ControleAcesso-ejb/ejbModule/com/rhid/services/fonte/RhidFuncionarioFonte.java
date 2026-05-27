package com.rhid.services.fonte;

import java.util.Date;
import java.util.List;

import com.rhid.services.dto.RhidFuncionarioExternoDTO;

/**
 * Contrato da API externa de funcionários.
 * Substituir {@link RhidFuncionarioFonteMock} pela implementação real quando disponível.
 */
public interface RhidFuncionarioFonte {

	List<RhidFuncionarioExternoDTO> buscarTodos();

	List<RhidFuncionarioExternoDTO> buscarAlteradosDesde(Date dataReferencia);
}
