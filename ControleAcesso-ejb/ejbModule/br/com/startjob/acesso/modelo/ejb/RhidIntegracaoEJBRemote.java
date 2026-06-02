package br.com.startjob.acesso.modelo.ejb;

import br.com.startjob.acesso.modelo.entity.ConfiguracaoRhidEntity;

import com.rhid.services.dto.RhidOperacaoResultDTO;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface RhidIntegracaoEJBRemote extends BaseEJBRemote {

	RhidOperacaoResultDTO exportarRhid(boolean completa) throws Exception;

	RhidOperacaoResultDTO exportarRhidPorId(Long configId, boolean completa) throws Exception;

	RhidOperacaoResultDTO exportarRhidAutomatico() throws Exception;

	RhidOperacaoResultDTO exportarRhidAutomaticoPorId(Long configId) throws Exception;

	ConfiguracaoRhidEntity salvarConfiguracao(ConfiguracaoRhidEntity configuracao) throws Exception;

	ConfiguracaoRhidEntity buscarConfiguracaoPorId(Long id) throws Exception;

	List<ConfiguracaoRhidEntity> listarConfiguracoes() throws Exception;

	void excluirConfiguracao(Long id) throws Exception;
}
