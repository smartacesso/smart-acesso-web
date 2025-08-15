package br.com.startjob.acesso.to;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;

import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;

public class PedestrianAccessTO {
	
	private Long id;
	private Long idTemp;
	private Long idUsuario;
	private String name;
	private String cardNumber;
	private String status;
	private Date dataNascimento;
	private Date dataCriacao;
	private String tipo = TipoPedestre.PEDESTRE.toString();
	private String email;
	private String cpf;
	private String genero;
	private String rg;
	private String telefone;
	private String celular;
	private String responsavel;
	private String observacoes;
	private String matricula;
	private Boolean removido;
	private Boolean sempreLiberado;
	private Boolean acessoLivre;
	private Boolean habilitarTeclado;
	private String qrCodeParaAcesso;
	private Boolean cadastroFacialObrigatorio;

	private String cep;
	private String logradouro;
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String estado;
	
	private List<String> templates;
	
	private Long idRegra;
	private Long quantidadeCreditos;
	private Date validadeCreditos;
	private Date dataInicioPeriodo;
	private Date dataFimPeriodo;
	private String tipoTurno;
	private Date inicioTurno;
	private String luxandIdentifier;
	private Boolean bloqueado;
	
	private Boolean enviaSmsAoPassarNaCatraca;

	private Long idLocal;
	private Long idEmpresa;
	private Long idCargo;
	private Long idCentroCusto;
	private Long idDepartamento;
	
	private List<AllowedTimeTO> horariosPermitidos;

	private List<PedestrianEquipamentTO> equipamentos;
	private List<PedestrianMessagesTO> mensagens;
	
	private List<PedestreRegraTO> pedestreRegras;
	private List<DocumentoTo> documentos;
	
	private Integer qtdAcessoAntesSinc;
	
	private String login;
	private String senha;
	private String tipoAcesso;
	private String tipoQRCode;
	private Date dataCadastroFotoNaHikivision;
	private String fotoBase64;
	
	private transient SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public PedestrianAccessTO() {}
	
	public PedestrianAccessTO(Object[] objects, String version) throws ParseException {
	
		//dados básicos
		this.id 			= Long.valueOf(objects[0].toString());
		this.name 			= objects[1].toString();
		this.cardNumber 	= objects[2] == null ? null : objects[2].toString();
		this.status 		= objects[3] == null ? "ATIVO" : objects[3].toString();
		this.dataNascimento = objects[4] == null ? null : criaData(objects[4], sdf);
		this.dataCriacao 	= criaData(objects[5], sdf);
		this.tipo 			= objects[6] == null ? "VISITANTE" : objects[6].toString();
		this.email			= objects[23] == null ? null : objects[23].toString();
		this.cpf			= objects[24] == null ? null : objects[24].toString();
		this.genero			= objects[25] == null ? null : objects[25].toString();
		this.rg				= objects[26] == null ? null : objects[26].toString();
		this.telefone		= objects[27] == null ? null : objects[27].toString();
		this.celular		= objects[28] == null ? null : objects[28].toString();
		this.observacoes	= objects[29] == null ? null : objects[29].toString();
		this.matricula		= objects[37] == null ? null : objects[37].toString();
		this.removido		= objects[38] == null ? false : Boolean.valueOf(objects[38].toString());
		this.sempreLiberado   = objects[39] == null ? false : Boolean.valueOf(objects[39].toString());
		this.acessoLivre   = objects[78] == null ? false : Boolean.valueOf(objects[78].toString());
		this.habilitarTeclado = objects[40] == null ? false : Boolean.valueOf(objects[40].toString());
		this.idTemp			= objects[41] == null ? null : Long.valueOf(objects[41].toString());
		this.qrCodeParaAcesso = objects[42] == null ? null : objects[42].toString();
		this.cadastroFacialObrigatorio = objects[43] == null ? false : Boolean.valueOf(objects[43].toString());
		
		//endereco
		this.cep			  = objects[30] == null ? null : objects[30].toString();
		this.logradouro		  = objects[31] == null ? null : objects[31].toString();
		this.numero			  = objects[32] == null ? null : objects[32].toString();
		this.complemento	  = objects[33] == null ? null : objects[33].toString();
		this.bairro			  = objects[34] == null ? null : objects[34].toString();
		this.cidade			  = objects[35] == null ? null : objects[35].toString();
		this.estado			  = objects[36] == null ? null : objects[36].toString();
		
		this.idLocal 		  = objects[77] == null ? null : Long.valueOf(objects[77].toString());
		
		this.idEmpresa = objects[44] == null ? null : Long.valueOf(objects[44].toString());
		this.idCargo = objects[45] == null ? null : Long.valueOf(objects[45].toString());
		this.idCentroCusto = objects[46] == null ? null : Long.valueOf(objects[46].toString());
		this.idDepartamento = objects[47] == null ? null : Long.valueOf(objects[47].toString());
		this.luxandIdentifier = objects[49] == null ? null : objects[49].toString();
		
		this.enviaSmsAoPassarNaCatraca = objects[48] == null ? false : Boolean.valueOf(objects[48].toString());
		
		//digitais cadastradas
		adicionaBiometria(objects);
		
		this.idRegra = objects[8] == null ? null : Long.valueOf(objects[8].toString());
		this.quantidadeCreditos = objects[9] == null ? null : Long.valueOf(objects[9].toString());
		this.validadeCreditos = objects[10] == null ? null
				: montaValidadeCredito(Integer.valueOf(objects[10].toString()));
		this.tipoTurno = objects[11] == null ? null : objects[11].toString();
		this.inicioTurno = objects[12] == null ? null : criaData(objects[12], sdf);
		this.dataInicioPeriodo = objects[21] == null ? null : criaData(objects[21], sdf);
		this.dataFimPeriodo = objects[22] == null ? null : criaData(objects[22], sdf);
		try {
			this.qtdAcessoAntesSinc = objects[62] == null ? null : Integer.valueOf(objects[62].toString());
		}catch (Exception e) {}
		try {
			this.idUsuario = objects[63] == null ? null : Long.valueOf(objects[63].toString());
		}catch (Exception e) {}
//		try {
//			this.login = objects[65 - 1] == null ? null : objects[65 - 1].toString();
//		}catch (Exception e) {}
		try {
			this.senha = objects[65] == null ? null : objects[65].toString();
		}catch (Exception e) {}
		try {
			this.tipoAcesso = objects[66] == null ? null : objects[66].toString();
		}catch (Exception e) {}
		try {
			this.tipoQRCode = objects[67] == null ? null : objects[67].toString();
		}catch (Exception e) {}
		try {
			this.bloqueado = objects[64] == null ? null : Boolean.valueOf(objects[64].toString());
		}catch (Exception e) {}
		this.dataCadastroFotoNaHikivision  = objects[68] == null ? null : criaData(objects[68], sdf);
		
		//horários permitidos
		adicionaHorarios(objects);
		
		adicionaEquipamentos(objects);
		
		adicionaMensagem(objects);
		
		adicionaDocumentos(objects);
		
		adicionaPedestreRegras(objects);
		
		adicionaHorarioPedestreRegra(objects);
	}
	
	  public PedestrianAccessTO(PedestreEntity entity) {
	        this.id = entity.getId();
	        this.name = entity.getNome();
	        this.status = entity.getStatus() != null ? entity.getStatus().name() : null;
	        this.email = entity.getEmail();
	        this.cpf = entity.getCpf();
	        this.rg = entity.getRg();
	        this.telefone = entity.getTelefone();
	        this.celular = entity.getCelular();
	        this.matricula = entity.getMatricula();
	        this.cardNumber = entity.getCodigoCartaoAcesso();
	        this.observacoes = entity.getObservacoes();
	        this.tipoQRCode = entity.getTipoQRCode() != null ? entity.getTipoQRCode().name() : null;
	        this.qrCodeParaAcesso = entity.getQrCodeParaAcesso();
	        this.sempreLiberado = entity.getSempreLiberado();
	        this.acessoLivre = entity.getAcessoLivre();
	        this.habilitarTeclado = entity.getHabilitarTeclado();
	        this.cadastroFacialObrigatorio = entity.getCadastroFacialObrigatorio();
	        this.dataNascimento = entity.getDataNascimento();
	        this.tipo = entity.getTipo() != null ? entity.getTipo().name() : null;
	        this.genero = entity.getGenero() != null ? entity.getGenero().name() : null;
	        this.enviaSmsAoPassarNaCatraca = entity.getEnviaSmsAoPassarNaCatraca();
	        this.idLocal = entity.getIdLocal();
	        this.luxandIdentifier = entity.getLuxandIdentifier();
	        this.qtdAcessoAntesSinc = entity.getQtdAcessoAntesSinc();
	        this.login = entity.getLogin();
	        this.dataCadastroFotoNaHikivision = entity.getDataCadastroFotoNaHikivision();
	        this.tipoAcesso = entity.getTipoAcesso();

	        byte[] fotoBytes = entity.getFoto();
	        this.fotoBase64 = (fotoBytes != null) ? Base64.encodeBase64String(fotoBytes) : "";
	    }


	public void adicionaPedestreRegras(Object[] objects) {
		if(objects[8] == null) {
			return;
		}
		
		if(this.pedestreRegras == null) {
			this.pedestreRegras = new ArrayList<>();
		}
		
		Long id = Long.valueOf(objects[8].toString());
		Long idRegra = null;
		Date validade = null;
		Long qtdeTotalDeCreditos = null;
		try {
			idRegra = objects[59] == null ? null : Long.valueOf(objects[59].toString());
			validade = objects[60] == null ? null : criaData(objects[60], sdf);
			qtdeTotalDeCreditos = objects[61] == null ? null : Long.valueOf(objects[61].toString());
		}catch (Exception e) {
		}

		Long qtdeDeCreditos = objects[9] == null ? null : Long.valueOf(objects[9].toString());
		Long diasValidadeCredito = objects[10] == null ? null : Long.valueOf(objects[10].toString());
		Date dataInicioPeriodo = objects[21] == null ? null : criaData(objects[21], sdf);
		Date dataFimPeriodo = objects[22] == null ? null : criaData(objects[22], sdf);
		
		boolean add = true;
		
		for(PedestreRegraTO to : this.pedestreRegras) {
			if(to.getId().equals(id)) {
				add = false;
				break;
			}
		}
		
		if(add) {
			this.pedestreRegras.add(new PedestreRegraTO(id, idRegra, validade, qtdeDeCreditos, qtdeTotalDeCreditos, diasValidadeCredito, dataInicioPeriodo, dataFimPeriodo));
		}
	}
	
	public void adicionaHorarioPedestreRegra(Object[] objects) {
		if(Objects.isNull(this.pedestreRegras) || pedestreRegras.isEmpty()) {
			return;
		}
		
		
		if(Objects.isNull(objects[69])) {
			return;
		}
		
		final Long idPedestreRegra = Objects.isNull(objects[69]) ? null : Long.valueOf(objects[69].toString());
		final Long idHorario = Objects.isNull(objects[70]) ? null : Long.valueOf(objects[70].toString());
		final String diasSemana = Objects.isNull(objects[72]) ? null : objects[72].toString();
		final Date horarioInicio = Objects.isNull(objects[74]) ? null : criaData(objects[74], sdf);
		final Date horarioFim = Objects.isNull(objects[75]) ? null : criaData(objects[75], sdf);
		final String nomeRegra = Objects.isNull(objects[73]) ? null : objects[73].toString();
		final Long qtdeCreditos = Objects.isNull(objects[71]) ? null : Long.valueOf(objects[71].toString());
		final String status = Objects.isNull(objects[76]) ? null : objects[76].toString();
		
		for(PedestreRegraTO pr : this.pedestreRegras) {
			if(!pr.getId().equals(idPedestreRegra)) {
				continue;
			}
			
			if(Objects.isNull(pr.getHorarios())) {
				pr.setHorarios(new ArrayList<HorarioTO>());
			}
			
			boolean add = true;
			
			for(HorarioTO h : pr.getHorarios()) {
				if(h.getId().equals(idHorario)) {
					add = false;
					break;
				}
			}
			
			if(add) {
				pr.getHorarios().add(new HorarioTO(idHorario, nomeRegra, status, diasSemana, horarioInicio, horarioFim, qtdeCreditos, idPedestreRegra));
			}
		}
	}
	
	public void adicionaDocumentos(Object[] objects) {
		try {
			if(objects[56] == null)
				return;
		}catch (Exception e) {
			return;
		}
		
		if(this.documentos == null)
			this.documentos = new ArrayList<>();
		
		Long id = Long.valueOf(objects[56].toString());
		String nomeDoc = objects[57] == null ? null : objects[57].toString();
		Date validadeDoc = objects[58] == null ? null : criaData(objects[58], sdf);
		
		boolean add = true;
		
		for(DocumentoTo to : this.documentos) {
			if(to.getId().equals(id)) {
				add = false;
				break;
			}
		}
		
		if(add)
			this.documentos.add(new DocumentoTo(id, nomeDoc, validadeDoc));
	}

	public void adicionaMensagem(Object[] objects) {
		if(objects[18] == null)
			return;
		
		if(this.mensagens == null)
			this.mensagens = new ArrayList<PedestrianMessagesTO>();

		Long id = Long.valueOf(objects[18].toString());
		String message = objects[19] == null ? null : objects[19].toString();
		Long qtde = objects[20] == null ? null : Long.valueOf(objects[20].toString());
		String nome = objects[50] == null ? null : objects[50].toString();
		String status = objects[51] == null ? null : objects[51].toString();
		Date validade = objects[52] == null ? null : criaData(objects[52].toString(), sdf);
		
		boolean add = true;
		
		for(PedestrianMessagesTO to : this.mensagens) {
			if(to.getId().equals(id)) {
				add = false;
				break;
			}
		}
		
		if(add)
			this.mensagens.add(new PedestrianMessagesTO(id, nome, status, message, qtde, validade));
	}

	public void adicionaEquipamentos(Object[] objects) {
		if(objects[17] == null)
			return;
		
		if(this.equipamentos == null)
			this.equipamentos = new ArrayList<PedestrianEquipamentTO>();
		
		
		String idEquipamento = objects[17].toString().split(";")[0];
		Long id = null;
		Date validadeEquipamento = null;
		String nomeEquipamento = null;
		try {
			id = Long.valueOf(objects[53].toString());
			validadeEquipamento = objects[54] == null ? null : criaData(objects[54], sdf);
			nomeEquipamento = objects[55] == null ? null : objects[55].toString();
		}catch (Exception e) {
		}
		
		boolean add = true;
		
		for(PedestrianEquipamentTO to : this.equipamentos) {
			if(to == null || to.getId() == null) {
				add = false;
				break;
			}
			if(to.getId().equals(id)) {
				add = false;
				break;
			}
		}
		
		if(add)
			this.equipamentos.add(new PedestrianEquipamentTO(id, idEquipamento, validadeEquipamento, nomeEquipamento));
	}

	public void adicionaHorarios(Object[] objects) {
		if(objects[13] != null) {
			String dias = objects[14] != null ? objects[14].toString() : "1234567";
			if(this.horariosPermitidos == null) {
				this.horariosPermitidos = new ArrayList<AllowedTimeTO>();
			}
			System.out.println(objects[15].toString());
			System.out.println(objects[16].toString());
			this.horariosPermitidos.add(new AllowedTimeTO(objects[15].toString(), objects[16].toString(), dias));
		}
	}

	public void adicionaBiometria(Object[] objects) {
		if(objects[7] != null) {
			//tem outra digital
			if(this.templates == null)
				this.templates = new ArrayList<String>();
			this.templates.add(Base64.encodeBase64String((byte[]) objects[7]));
		}
	}

	private Date montaValidadeCredito(int dias) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, dias);
		return c.getTime();
	}
	
	private Date criaData(Object object, SimpleDateFormat sdf) {
		try {
			return (object instanceof String 
					? sdf.parse(object.toString()) : (Date)object);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static PedestrianAccessTO convertPedestrianAccess(PedestreEntity pedestre) {
		PedestrianAccessTO pedestreTO = new PedestrianAccessTO();
		pedestreTO.setName(pedestre.getNome());
		pedestreTO.setDataNascimento(pedestre.getDataNascimento());
		pedestreTO.setStatus(pedestre.getStatus().name());
		pedestreTO.setTipo(pedestre.getTipo().name());
		pedestreTO.setEmail(pedestre.getEmail());
		pedestreTO.setCpf(pedestre.getCpf());
		pedestreTO.setTelefone(pedestre.getTelefone());
		/*
		
		if(pedestre.getEndereco() != null) {
			pedestreTO.setCep(pedestre.getEndereco().getCep() == null ? "": pedestre.getEndereco().getCep());			
			pedestreTO.setLogradouro(pedestre.getEndereco().getLogradouro() == null ? "": pedestre.getEndereco().getLogradouro());		 
			pedestreTO.setNumero(pedestre.getEndereco().getNumero() == null ? "": pedestre.getEndereco().getNumero());			 
			pedestreTO.setComplemento(pedestre.getEndereco().getComplemento() == null ? "": pedestre.getEndereco().getComplemento());	 
			pedestreTO.setBairro(pedestre.getEndereco().getBairro() == null ? "": pedestre.getEndereco().getBairro());			  
			pedestreTO.setCidade(pedestre.getEndereco().getCidade() == null ? "": pedestre.getEndereco().getCidade());			 
			pedestreTO.setEstado(pedestre.getEndereco().getEstado() == null ? "": pedestre.getEndereco().getEstado());	
		}
		
		*/
	
		
		return pedestreTO;
		
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getTemplates() {
		return templates;
	}
	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public List<AllowedTimeTO> getHorariosPermitidos() {
		return horariosPermitidos;
	}

	public void setHorariosPermitidos(List<AllowedTimeTO> horariosPermitidos) {
		this.horariosPermitidos = horariosPermitidos;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Long getIdRegra() {
		return idRegra;
	}

	public void setIdRegra(Long idRegra) {
		this.idRegra = idRegra;
	}

	public Long getQuantidadeCreditos() {
		return quantidadeCreditos;
	}

	public void setQuantidadeCreditos(Long quantidadeCreditos) {
		this.quantidadeCreditos = quantidadeCreditos;
	}

	public Date getValidadeCreditos() {
		return validadeCreditos;
	}

	public void setValidadeCreditos(Date validadeCreditos) {
		this.validadeCreditos = validadeCreditos;
	}

	public String getTipoTurno() {
		return tipoTurno;
	}

	public void setTipoTurno(String tipoTurno) {
		this.tipoTurno = tipoTurno;
	}

	public Date getInicioTurno() {
		return inicioTurno;
	}

	public void setInicioTurno(Date inicioTruno) {
		this.inicioTurno = inicioTruno;
	}

	public List<PedestrianEquipamentTO> getEquipamentos() {
		return equipamentos;
	}

	public void setEquipamentos(List<PedestrianEquipamentTO> equipamentos) {
		this.equipamentos = equipamentos;
	}

	public List<PedestrianMessagesTO> getMensagens() {
		return mensagens;
	}

	public void setMensagens(List<PedestrianMessagesTO> mensagens) {
		this.mensagens = mensagens;
	}

	public Date getDataInicioPeriodo() {
		return dataInicioPeriodo;
	}

	public void setDataInicioPeriodo(Date dataInicioPeriodo) {
		this.dataInicioPeriodo = dataInicioPeriodo;
	}

	public Date getDataFimPeriodo() {
		return dataFimPeriodo;
	}

	public void setDataFimPeriodo(Date dataFimPeriodo) {
		this.dataFimPeriodo = dataFimPeriodo;
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

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
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

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public Boolean getRemovido() {
		return removido;
	}

	public void setRemovido(Boolean removido) {
		this.removido = removido;
	}

	public Boolean getSempreLiberado() {
		return sempreLiberado;
	}

	public void setSempreLiberado(Boolean sempreLiberado) {
		this.sempreLiberado = sempreLiberado;
	}

	public Boolean getHabilitarTeclado() {
		return habilitarTeclado;
	}

	public void setHabilitarTeclado(Boolean habilitarTeclado) {
		this.habilitarTeclado = habilitarTeclado;
	}

	public Long getIdTemp() {
		return idTemp;
	}

	public void setIdTemp(Long idTemp) {
		this.idTemp = idTemp;
	}

	public String getQrCodeParaAcesso() {
		return qrCodeParaAcesso;
	}

	public void setQrCodeParaAcesso(String qrCodeParaAcesso) {
		this.qrCodeParaAcesso = qrCodeParaAcesso;
	}

	public Boolean getCadastroFacialObrigatorio() {
		return cadastroFacialObrigatorio;
	}

	public void setCadastroFacialObrigatorio(Boolean cadastroFacialObrigatorio) {
		this.cadastroFacialObrigatorio = cadastroFacialObrigatorio;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Long getIdCargo() {
		return idCargo;
	}

	public void setIdCargo(Long idCargo) {
		this.idCargo = idCargo;
	}

	public Long getIdCentroCusto() {
		return idCentroCusto;
	}

	public void setIdCentroCusto(Long idCentroCusto) {
		this.idCentroCusto = idCentroCusto;
	}

	public Long getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(Long idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	public Boolean getEnviaSmsAoPassarNaCatraca() {
		return enviaSmsAoPassarNaCatraca;
	}

	public void setEnviaSmsAoPassarNaCatraca(Boolean enviaSmsAoPassarNaCatraca) {
		this.enviaSmsAoPassarNaCatraca = enviaSmsAoPassarNaCatraca;
	}

	public String getLuxandIdentifier() {
		return luxandIdentifier;
	}

	public void setLuxandIdentifier(String luxandIdentifier) {
		this.luxandIdentifier = luxandIdentifier;
	}
	
	public List<PedestreRegraTO> getPedestreRegras() {
		return pedestreRegras;
	}

	public void setPedestreRegras(List<PedestreRegraTO> pedestreRegras) {
		this.pedestreRegras = pedestreRegras;
	}

	public List<DocumentoTo> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocumentoTo> documentos) {
		this.documentos = documentos;
	}

	public Integer getQtdAcessoAntesSinc() {
		return qtdAcessoAntesSinc;
	}

	public void setQtdAcessoAntesSinc(Integer qtdAcessoAntesSinc) {
		this.qtdAcessoAntesSinc = qtdAcessoAntesSinc;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTipoAcesso() {
		return tipoAcesso;
	}

	public void setTipoAcesso(String tipoAcesso) {
		this.tipoAcesso = tipoAcesso;
	}

	public String getTipoQRCode() {
		return tipoQRCode;
	}

	public void setTipoQRCode(String tipoQRCode) {
		this.tipoQRCode = tipoQRCode;
	}
	
	public Boolean getBloqueado() {
			return bloqueado;
			}
		
	public void setBloqueado(Boolean bloqueado) {
		this.bloqueado = bloqueado;
		}

	public Date getDataCadastroFotoNaHikivision() {
		return dataCadastroFotoNaHikivision;
	}

	public void setDataCadastroFotoNaHikivision(Date dataCadastroFotoNaHikivision) {
		this.dataCadastroFotoNaHikivision = dataCadastroFotoNaHikivision;
	}

	public Long getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(Long idLocal) {
		this.idLocal = idLocal;
	}

	public String getFotoBase64() {
		return fotoBase64;
	}

	public void setFotoBase64(String fotoBase64) {
		this.fotoBase64 = fotoBase64;
	}

	public Boolean getAcessoLivre() {
		return acessoLivre;
	}

	public void setAcessoLivre(Boolean acessoLivre) {
		this.acessoLivre = acessoLivre;
	}
	
}


