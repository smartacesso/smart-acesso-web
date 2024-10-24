package br.com.startjob.acesso.controller.uc002;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.controller.BaseController;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.EquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.PedestreEntity;
import br.com.startjob.acesso.modelo.entity.UsuarioEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;
import br.com.startjob.acesso.modelo.utils.DateUtils;

@SuppressWarnings("serial")
@Named("principalController")
@ViewScoped
public class PrincipalController extends BaseController {
	
	private String acessos;
	private String visitantes;
	private String equipamentos;
	private String liberacoes;
	
	private List<AcessoEntity> ultimas;
	private List<EquipamentoEntity> equipamentosDisponiveis;
	
	private Long pedestre;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		ultimas = new ArrayList<AcessoEntity>();
		
		try {
			UsuarioEntity usuarioLogado = getUsuarioLogado();
			if(usuarioLogado != null && usuarioLogado.getId() != null) {
				
				Map<String, Object> argBase = new HashMap<String, Object>();
				argBase.put("cliente.id", usuarioLogado.getCliente().getId());
				argBase.put("dataCriacao_ini_data", DateUtils.getInstance().ajustaDataIni(Calendar.getInstance()).getTime());
				argBase.put("dataCriacao_fim_data", DateUtils.getInstance().ajustaDataFim(Calendar.getInstance()).getTime());
				
				//busca quantidade de acessos hoje
				Map<String, Object> argAcessos = new HashMap<String, Object>();
				argAcessos.putAll(argBase);
				argAcessos.put("pedestre_dif", "null");
				Integer qtdAcessos = baseEJB
						.pesquisaSimplesCount(AcessoEntity.class, "findAll", argAcessos);
				if(qtdAcessos != null)
					acessos = qtdAcessos + "";
				
				//busca quantidade de visitantes hoje
				Map<String, Object> argVisitante = new HashMap<String, Object>();
				argVisitante.putAll(argBase);
				argVisitante.put("tipo", TipoPedestre.VISITANTE);
				Integer qtdVisitantes = baseEJB
						.pesquisaSimplesCount(PedestreEntity.class, "findAll", argVisitante);
				if(qtdVisitantes != null)
					visitantes = qtdVisitantes + "";
				
				//busca liberações justificadas hoje
				Map<String, Object> argLiberacoes = new HashMap<String, Object>();
				argLiberacoes.putAll(argBase);
				argLiberacoes.put("pedestre", "null");
				Integer qtdLiberacoes = baseEJB
						.pesquisaSimplesCount(AcessoEntity.class, "findAll", argLiberacoes);
				if(qtdLiberacoes != null)
					liberacoes = qtdLiberacoes + "";
				
				//busca equipamentos
				Map<String, Object> argEquipamentos = new HashMap<String, Object>();
				argEquipamentos.put("cliente.id", usuarioLogado.getCliente().getId());
				Integer qtdEquipamentos = baseEJB
						.pesquisaSimplesCount(EquipamentoEntity.class, "findAll", argEquipamentos);
				if(qtdEquipamentos != null)
					equipamentos = qtdEquipamentos + "";
				
				//busca ultimos 5 acessos
				Map<String, Object> argUltimos = new HashMap<String, Object>();
				argUltimos.put("cliente.id", usuarioLogado.getCliente().getId());
				ultimas = (List<AcessoEntity>) baseEJB
						.pesquisaSimplesLimitado(AcessoEntity.class, "findAllComPedestre", argUltimos, 0, 5);
				
				//busca até 5 equipamentos
				equipamentosDisponiveis = (List<EquipamentoEntity>) baseEJB
						.pesquisaSimplesLimitado(EquipamentoEntity.class, "findAll", argUltimos, 0, 5);
				
				
				//deu tudo certo, retorna
				return;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
			
		//indica ausencia de dados
		acessos      = "--";
		visitantes   = "--";
		equipamentos = "--";
		liberacoes   = "--";
			
		
	}
	
	@SuppressWarnings("unchecked")
	public List<PedestreEntity> buscarPedestreComplete(String nome) {
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("nome", nome);
			args.put("cliente.id", getUsuarioLogado().getCliente().getId());
			
			return (List<PedestreEntity>) baseEJB.pesquisaSimplesLimitado(
					PedestreEntity.class, "findAll", args, 0, 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void selecionaPedestre() {
		if(pedestre != null)
			redirect("/paginas/sistema/pedestres/cadastroPedestre.xhtml?id="+pedestre);
		else
			mensagemFatal("", "#Pedestre não selecionado");
		
	}

	public String getAcessos() {
		return acessos;
	}

	public void setAcessos(String acessos) {
		this.acessos = acessos;
	}

	public String getVisitantes() {
		return visitantes;
	}

	public void setVisitantes(String visitantes) {
		this.visitantes = visitantes;
	}

	public String getEquipamentos() {
		return equipamentos;
	}

	public void setEquipamentos(String equipamentos) {
		this.equipamentos = equipamentos;
	}

	public String getLiberacoes() {
		return liberacoes;
	}

	public void setLiberacoes(String liberacoes) {
		this.liberacoes = liberacoes;
	}

	public List<AcessoEntity> getUltimas() {
		return ultimas;
	}

	public void setUltimas(List<AcessoEntity> ultimas) {
		this.ultimas = ultimas;
	}


	public List<EquipamentoEntity> getEquipamentosDisponiveis() {
		return equipamentosDisponiveis;
	}

	public void setEquipamentosDisponiveis(List<EquipamentoEntity> equipamentosDisponiveis) {
		this.equipamentosDisponiveis = equipamentosDisponiveis;
	}

	public Long getPedestre() {
		return pedestre;
	}

	public void setPedestre(Long pedestre) {
		this.pedestre = pedestre;
	}
	
	


}
