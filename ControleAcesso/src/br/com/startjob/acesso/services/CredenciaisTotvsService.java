package br.com.startjob.acesso.services;

import br.com.startjob.acesso.modelo.entity.CredenciaisIntegracaoTotvsEntity;
import br.com.startjob.acesso.repositories.CredencialTotvsRepository;

public class CredenciaisTotvsService {

	private CredencialTotvsRepository credencialTotvsRepository;

	public boolean existeCredencialValida(String usuario, String senha, String token) {
		credencialTotvsRepository = new CredencialTotvsRepository();

		CredenciaisIntegracaoTotvsEntity credencial = null;

		try {
			credencial = credencialTotvsRepository.findByUsuario(usuario);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (credencial == null)
			return false;

		return credencial.getSenha().equals(senha) && credencial.getToken().equals(token);
	}
}
