package com.rhid.services;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;

/**
 * Controle de IDs RHID já sincronizados (id externo -> id RHID).
 * Persiste apenas o mapa de controle na configuração, não os dados do funcionário.
 */
public final class RhidSincronizacaoControle {

	private RhidSincronizacaoControle() {
	}

	public static Map<String, Integer> carregarMapa(ConfiguracaoRhidEntity config) {
		Map<String, Integer> mapa = new HashMap<>();
		if (config.getMapaIdsRhid() == null || config.getMapaIdsRhid().trim().isEmpty()) {
			return mapa;
		}
		try (JsonReader reader = Json.createReader(new StringReader(config.getMapaIdsRhid()))) {
			JsonObject obj = reader.readObject();
			for (String key : obj.keySet()) {
				mapa.put(key, obj.getInt(key));
			}
		} catch (Exception e) {
			// mapa inválido — reinicia vazio
		}
		return mapa;
	}

	public static Integer obterIdRhid(Map<String, Integer> mapa, String nomeDominio, String idExterno) {
		return mapa.get(chaveMapa(nomeDominio, idExterno));
	}

	public static void registrarIdRhid(Map<String, Integer> mapa, String nomeDominio, String idExterno,
			Integer idRhid) {
		mapa.put(chaveMapa(nomeDominio, idExterno), idRhid);
	}

	public static String chaveMapa(String nomeDominio, String idExterno) {
		return nomeDominio.trim().toLowerCase() + "::" + idExterno.trim();
	}
}
