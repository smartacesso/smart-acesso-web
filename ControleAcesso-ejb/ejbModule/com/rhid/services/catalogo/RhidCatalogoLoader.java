package com.rhid.services.catalogo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.rhid.services.RhidService;

/**
 * Carrega catálogos RHID (DataTables) após login no domínio.
 */
public final class RhidCatalogoLoader {

	private static final int TAMANHO_PAGINA = 500;

	private RhidCatalogoLoader() {
	}

	public static RhidCatalogoDominio carregar(RhidService rhidService) {
		RhidCatalogoDominio catalogo = new RhidCatalogoDominio();
		catalogo.getEmpresas().addAll(listar(rhidService, "/customerdb/company.svc/a", true));
		catalogo.getDepartamentos().addAll(listar(rhidService, "/customerdb/department.svc/a", false));
		catalogo.getCargos().addAll(listar(rhidService, "/customerdb/personrole.svc/a", false));
		catalogo.getHorarios().addAll(listar(rhidService, "/customerdb/shift.svc/a", false));
		catalogo.getCentrosCusto().addAll(listar(rhidService, "/customerdb/costcenter.svc/a", false));
		return catalogo;
	}

	private static List<RhidItemCatalogo> listar(RhidService rhidService, String endpoint, boolean incluirCnpj) {
		List<RhidItemCatalogo> itens = new ArrayList<>();
		int start = 0;
		int total = Integer.MAX_VALUE;

		while (start < total) {
			String json = rhidService.listarCustomerDb(endpoint, start, TAMANHO_PAGINA);
			JsonObject resposta = parseObjeto(json);
			JsonArray data = resposta.containsKey("data") && !resposta.isNull("data")
					? resposta.getJsonArray("data")
					: Json.createArrayBuilder().build();

			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getValueType() != JsonValue.ValueType.OBJECT) {
					continue;
				}
				JsonObject row = data.getJsonObject(i);
				RhidItemCatalogo item = new RhidItemCatalogo();
				item.setId(lerInteiro(row, "id"));
				item.setName(lerTexto(row, "name"));
				if (incluirCnpj) {
					item.setTradingName(lerTexto(row, "tradingName"));
					item.setCnpj(lerTexto(row, "cnpj"));
				}
				if (item.getId() != null) {
					itens.add(item);
				}
			}

			total = resposta.containsKey("recordsFiltered") && !resposta.isNull("recordsFiltered")
					? resposta.getInt("recordsFiltered")
					: resposta.containsKey("recordsTotal") && !resposta.isNull("recordsTotal")
							? resposta.getInt("recordsTotal")
							: start + data.size();

			if (data.isEmpty()) {
				break;
			}
			start += TAMANHO_PAGINA;
		}
		return itens;
	}

	private static JsonObject parseObjeto(String json) {
		try (JsonReader reader = Json.createReader(new StringReader(json))) {
			return reader.readObject();
		}
	}

	private static Integer lerInteiro(JsonObject row, String campo) {
		if (!row.containsKey(campo) || row.isNull(campo)) {
			return null;
		}
		try {
			if (row.get(campo).getValueType() == JsonValue.ValueType.NUMBER) {
				return row.getInt(campo);
			}
			String texto = row.getString(campo, null);
			if (texto == null || texto.trim().isEmpty()) {
				return null;
			}
			return Integer.parseInt(texto.trim());
		} catch (Exception e) {
			return null;
		}
	}

	private static String lerTexto(JsonObject row, String campo) {
		if (!row.containsKey(campo) || row.isNull(campo)) {
			return null;
		}
		try {
			return row.getString(campo);
		} catch (Exception e) {
			return String.valueOf(row.get(campo));
		}
	}
}
