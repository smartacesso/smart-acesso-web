package br.com.startjob.acesso.modelo.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsuarioADTO {

	private Long id;

	private String nome;

	private String nickName;

	private String email;

	private String cpf;

	private String rg;

	private String telefone;

	private String celular;

	private Date dataNascimento;

	private String endereco;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public List<UsuarioADTO> convertUser(List<String> lines) {
		List<UsuarioADTO> users = new ArrayList<>();

		for (String line : lines) {
			if (line != null && line.contains("Usuário:")) {
				UsuarioADTO dto = new UsuarioADTO();

				String[] partes = line.split(",");
				for (String parte : partes) {
					parte = parte.trim();
					if (parte.startsWith("Usuário:")) {
						dto.setNickName(parte.replace("Usuário:", "").trim());
					} else if (parte.startsWith("Nome:")) {
						dto.setNome(parte.replace("Nome:", "").trim());
					} else if (parte.startsWith("Email:")) {
						String email = parte.replace("Email:", "").trim();
						dto.setEmail(email.equals("N/A") ? null : email);
					}
				}

				users.add(dto);
			}
		}

		return users;
	}
}
