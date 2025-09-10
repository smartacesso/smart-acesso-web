package br.com.startjob.acesso.modelo.utils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import br.com.startjob.acesso.modelo.entity.EnderecoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.to.UsuarioADTO;

public class UsuarioAdToPedestreConverter {

	public static List<PedestreEntity> converterLista(List<UsuarioADTO> usuariosAd) {
		return usuariosAd.stream().map(UsuarioAdToPedestreConverter::converter).collect(Collectors.toList());
	}

	public static PedestreEntity converter(UsuarioADTO usuarioAd) {
		PedestreEntity pedestre = new PedestreEntity();

		pedestre.setNome(Objects.nonNull(usuarioAd.getNome()) ? usuarioAd.getNome() : usuarioAd.getNickName());
		pedestre.setCpf(Objects.nonNull(usuarioAd.getCpf()) ? usuarioAd.getCpf() : "");
		pedestre.setEmail(Objects.nonNull(usuarioAd.getEmail()) ? usuarioAd.getEmail() : "");
		pedestre.setRg(Objects.nonNull(usuarioAd.getRg()) ? usuarioAd.getRg() : "");
		pedestre.setTelefone(Objects.nonNull(usuarioAd.getTelefone()) ? usuarioAd.getTelefone() : "");
		pedestre.setCelular(Objects.nonNull(usuarioAd.getCelular()) ? usuarioAd.getCelular() : "");
		pedestre.setStatus(Status.ATIVO);
		pedestre.setTipo(TipoPedestre.PEDESTRE);
		pedestre.setSempreLiberado(Boolean.TRUE);
		pedestre.setObservacoes("Importado do Active Directory");

		if (usuarioAd.getEndereco() != null && !StringUtils.isBlank(usuarioAd.getEndereco())) {
			EnderecoEntity endereco = new EnderecoEntity();
			endereco.setLogradouro(usuarioAd.getEndereco());
			pedestre.setEndereco(endereco);
		}

		return pedestre;
	}
}
