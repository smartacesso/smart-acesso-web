package br.com.startjob.acesso.repositories;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.com.startjob.acesso.to.HikivisionDeviceSimplificadoTO;

public class HikivisionEquipamentosRepository {
	// clienteId -> (deviceId -> equipamento)
	private static final Map<String, Map<String, HikivisionDeviceSimplificadoTO>> equipamentosPorCliente = new ConcurrentHashMap<>();

	public static void adicionarEquipamentos(String clienteId, Iterable<HikivisionDeviceSimplificadoTO> equipamentos) {
		equipamentosPorCliente.putIfAbsent(clienteId, new ConcurrentHashMap<>());

		Map<String, HikivisionDeviceSimplificadoTO> equipamentosCliente = equipamentosPorCliente.get(clienteId);

		for (HikivisionDeviceSimplificadoTO equipamento : equipamentos) {
			equipamentosCliente.put(equipamento.getDevIndex(), equipamento); // sobrescreve se já existir
		}
	}

	public static Map<String, HikivisionDeviceSimplificadoTO> getEquipamentos(String clienteId) {
		return equipamentosPorCliente.getOrDefault(clienteId, new ConcurrentHashMap<>());
	}
}

