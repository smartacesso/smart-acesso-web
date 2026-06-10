package br.com.startjob.acesso.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.startjob.acesso.modelo.ejb.AppEJBRemote;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.AvisoAppEntity;
import br.com.startjob.acesso.modelo.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.modelo.entity.DeviceTokenEntity;
import br.com.startjob.acesso.modelo.to.app.EncomendaListItem;

/**
 * Disparo de push FCM para eventos do app mobile.
 *
 * Matriz:
 * - encomenda → destinatário → /entregas
 * - aviso → todos os pedestres com perfil app do cliente → /avisos
 * - acesso → app novo: RESPONSAVEL (tutorados) + GERENCIAL (mesma empresa) → /historico
 *   (gatilho: catraca POST /registerlog; espelha regras de POST /app/acessos)
 */
public class AppPushNotificationService {

	private static final Logger LOG = Logger.getLogger(AppPushNotificationService.class.getName());

	private final AppEJBRemote appEjb;

	public AppPushNotificationService(AppEJBRemote appEjb) {
		this.appEjb = appEjb;
	}

	public void notificarRetiradaEncomenda(EncomendaListItem encomenda) {
		if (encomenda == null || encomenda.getDestinatarioId() == null) {
			return;
		}
		try {
			String title = "Retirada confirmada";
			String body = "Sua encomenda foi retirada na portaria";
			if (encomenda.getNomeQuemRetirou() != null && !encomenda.getNomeQuemRetirou().trim().isEmpty()) {
				body = "Retirada por " + encomenda.getNomeQuemRetirou().trim();
			}
			Map<String, String> data = montarPayload("retirada_encomenda", "/entregas", encomenda.getId(), title, body);
			enviarParaPedestres(java.util.Collections.singletonList(encomenda.getDestinatarioId()), title, body, data);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Falha ao enviar push de retirada de encomenda", e);
		}
	}

	public void notificarEncomenda(CorrespondenciaEntity correspondencia) {
		if (correspondencia == null || correspondencia.getDestinatario() == null
				|| correspondencia.getDestinatario().getId() == null) {
			LOG.info("Push encomenda ignorado: correspondencia ou destinatario sem ID");
			return;
		}
		try {
			Long idDestinatario = correspondencia.getDestinatario().getId();
			LOG.info("Push encomenda: correspondenciaId=" + correspondencia.getId() + ", destinatarioId="
					+ idDestinatario);
			String title = "Nova encomenda";
			String body = "Você recebeu uma correspondência na portaria";
			if (correspondencia.getTipo() != null && !correspondencia.getTipo().trim().isEmpty()) {
				body = "Nova correspondência: " + correspondencia.getTipo().trim();
			}
			Map<String, String> data = montarPayload("encomenda", "/entregas", correspondencia.getId(), title, body);
			enviarParaPedestres(java.util.Collections.singletonList(idDestinatario), title, body, data);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Falha ao enviar push de encomenda", e);
		}
	}

	public void notificarAviso(AvisoAppEntity aviso, boolean novo) {
		if (!novo || aviso == null || aviso.getCliente() == null || aviso.getCliente().getId() == null) {
			return;
		}
		try {
			String title = aviso.getTitulo() != null ? aviso.getTitulo().trim() : "Novo aviso";
			String body = resumirTexto(aviso.getDescricao(), "Confira o aviso no app");
			Map<String, String> data = montarPayload("aviso", "/avisos", aviso.getId(), title, body);
			List<Long> destinatarios = appEjb.buscarIdsPedestresNotificaveisAppPorCliente(aviso.getCliente().getId());
			enviarParaPedestres(destinatarios, title, body, data);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Falha ao enviar push de aviso", e);
		}
	}

	public void notificarAcesso(AcessoEntity acesso) {
		if (acesso == null) {
			return;
		}
		try {
			Long idPedestre = acesso.getIdPedestre();
			if (idPedestre == null && acesso.getPedestre() != null) {
				idPedestre = acesso.getPedestre().getId();
			}
			if (idPedestre == null) {
				LOG.info("Push acesso ignorado: pedestre sem ID no log");
				return;
			}

			String nome = appEjb.buscarNomePedestre(idPedestre);
			if (nome == null || nome.trim().isEmpty()) {
				nome = "Pedestre";
			}
			String sentido = acesso.getSentido() != null ? acesso.getSentido().trim() : "acesso";
			String title = "Smart Acesso";
			String body = String.format("Acesso de %s: %s", nome, sentido);
			Map<String, String> data = montarPayload("acesso", "/historico", acesso.getId(), title, body);

			List<Long> destinatarios = resolverDestinatariosPushAcessoApp(idPedestre);
			if (destinatarios.isEmpty()) {
				LOG.info("Push acesso ignorado para pedestreId=" + idPedestre
						+ ": nenhum RESPONSAVEL/GERENCIAL do app novo vinculado (perfilApp + token FCM)");
				return;
			}
			LOG.info("Push acesso: pedestreId=" + idPedestre + ", destinatariosApp=" + destinatarios.size()
					+ " (RESPONSAVEL tutorados + GERENCIAL empresa)");
			enviarParaPedestres(destinatarios, title, body, data);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Falha ao enviar push de acesso", e);
		}
	}

	/**
	 * Espelha {@code idsPermitidosPorPerfil} do AppRequestService (histórico), invertido:
	 * quem deve ser notificado quando o pedestre {@code idPedestreAcessou} registra acesso.
	 */
	private List<Long> resolverDestinatariosPushAcessoApp(Long idPedestreAcessou) {
		Set<Long> unicos = new HashSet<>();
		List<Long> responsaveisApp = appEjb.buscarIdsResponsaveisAppPorPedestre(idPedestreAcessou);
		if (responsaveisApp != null) {
			unicos.addAll(responsaveisApp);
		}
		List<Long> gerenciaisApp = appEjb.buscarIdsGerenciaisAppPorPedestre(idPedestreAcessou);
		if (gerenciaisApp != null) {
			unicos.addAll(gerenciaisApp);
		}
		unicos.remove(idPedestreAcessou);
		return new ArrayList<>(unicos);
	}

	private void enviarParaPedestres(List<Long> idsPedestre, String title, String body, Map<String, String> data) {
		if (idsPedestre == null || idsPedestre.isEmpty()) {
			LOG.info("Push ignorado: nenhum destinatario informado");
			return;
		}
		Set<Long> unicos = new HashSet<>(idsPedestre);
		for (Long idPedestre : unicos) {
			if (idPedestre == null) {
				continue;
			}
			List<DeviceTokenEntity> tokens = appEjb.buscarDeviceTokensAtivos(idPedestre);
			if (tokens == null || tokens.isEmpty()) {
				LOG.info("Push ignorado para pedestreId=" + idPedestre
						+ ": nenhum token FCM ativo em TB_DEVICE_TOKEN (app precisa registrar POST /device-token apos login)");
				continue;
			}
			LOG.info("Push para pedestreId=" + idPedestre + ": " + tokens.size() + " token(s) ativo(s)");
			for (DeviceTokenEntity deviceToken : tokens) {
				if (deviceToken.getFcmToken() == null || deviceToken.getFcmToken().trim().isEmpty()) {
					continue;
				}
				String fcmToken = deviceToken.getFcmToken().trim();
				if (pareceTokenExpo(fcmToken)) {
					LOG.warning("Token Expo detectado para pedestreId=" + idPedestre
							+ ". Use getDevicePushTokenAsync() no app (build nativo), nao getExpoPushTokenAsync().");
					continue;
				}
				FirebasePushResult result = FirebasePushService.send(appEjb, fcmToken, title, body, data);
				if (!result.isSuccess()) {
					LOG.warning("Push falhou para pedestreId=" + idPedestre + ": "
							+ (result.getError() != null ? result.getError() : "erro desconhecido"));
				}
			}
		}
	}

	public static boolean pareceTokenExpo(String token) {
		if (token == null) {
			return false;
		}
		String t = token.trim();
		return t.startsWith("ExponentPushToken[") || t.startsWith("ExpoPushToken");
	}

	public static Map<String, String> montarPayload(String type, String screen, Long id, String title, String body) {
		Map<String, String> data = new HashMap<>();
		data.put("type", type);
		data.put("screen", screen);
		if (id != null) {
			data.put("id", String.valueOf(id));
		}
		if (title != null) {
			data.put("title", title);
		}
		if (body != null) {
			data.put("body", body);
		}
		return data;
	}

	private static String resumirTexto(String texto, String fallback) {
		if (texto == null || texto.trim().isEmpty()) {
			return fallback;
		}
		String t = texto.trim().replaceAll("\\s+", " ");
		return t.length() > 180 ? t.substring(0, 177) + "..." : t;
	}
}
