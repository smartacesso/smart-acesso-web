package br.com.startjob.acesso.to.app;

import java.util.ArrayList;
import java.util.List;

public class PushStatusResponse {

	private Long pedestreId;
	private int tokensAtivos;
	private List<TokenResumo> tokens = new ArrayList<>();

	public Long getPedestreId() {
		return pedestreId;
	}

	public void setPedestreId(Long pedestreId) {
		this.pedestreId = pedestreId;
	}

	public int getTokensAtivos() {
		return tokensAtivos;
	}

	public void setTokensAtivos(int tokensAtivos) {
		this.tokensAtivos = tokensAtivos;
	}

	public List<TokenResumo> getTokens() {
		return tokens;
	}

	public void setTokens(List<TokenResumo> tokens) {
		this.tokens = tokens;
	}

	public static class TokenResumo {
		private String platform;
		private String tokenMascarado;
		private String appVersion;
		private boolean pareceExpoToken;

		public String getPlatform() {
			return platform;
		}

		public void setPlatform(String platform) {
			this.platform = platform;
		}

		public String getTokenMascarado() {
			return tokenMascarado;
		}

		public void setTokenMascarado(String tokenMascarado) {
			this.tokenMascarado = tokenMascarado;
		}

		public String getAppVersion() {
			return appVersion;
		}

		public void setAppVersion(String appVersion) {
			this.appVersion = appVersion;
		}

		public boolean isPareceExpoToken() {
			return pareceExpoToken;
		}

		public void setPareceExpoToken(boolean pareceExpoToken) {
			this.pareceExpoToken = pareceExpoToken;
		}
	}
}
