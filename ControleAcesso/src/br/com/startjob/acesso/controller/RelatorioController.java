package br.com.startjob.acesso.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import br.com.startjob.acesso.modelo.ejb.EmpresaEJBRemote;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.EquipamentoEntity;
import br.com.startjob.acesso.utils.Utils;

@SuppressWarnings("serial")
public abstract class RelatorioController extends BaseController {
	
	@EJB
	protected EmpresaEJBRemote empresaEJB;
	
	protected Date formatarDataInicioDia(Date data) {
		if(data == null)
			data = new Date();
		
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		
		return c.getTime();
	}
	
	protected Date formatarDataFimDia(Date data) {
		if(data == null)
			data = new Date();
		
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		
		return c.getTime();
	}
	
	public void redirecionaPaginaEditarPedetre(Long idPedestreSelecionado, String origem) {
		if(idPedestreSelecionado != null) {
			redirect("/paginas/sistema/pedestres/cadastroPedestre.xhtml?id=" + idPedestreSelecionado +"&origem=" + origem);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<SelectItem> montaListaEmpresas() {
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());
		
		List<SelectItem> listaEmpresas = new ArrayList<SelectItem>();
		listaEmpresas.add(new SelectItem(null, "Filtrar por empresa"));
		
		try {
			List<EmpresaEntity> empresas = (List<EmpresaEntity>) baseEJB.pesquisaArgFixos(EmpresaEntity.class,
					"findAllByIdCliente2", args);

			if (empresas != null && !empresas.isEmpty()) {
				empresas.forEach(empresa -> {
					listaEmpresas.add(new SelectItem(empresa.getId(), empresa.getNome()));
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return listaEmpresas;
	}
	
	@SuppressWarnings("unchecked")
	public EmpresaEntity buscaEmpresaPeloId(Long id) {
		baseEJB = empresaEJB;

		EmpresaEntity empresa = null;
		
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID", id);

			List<EmpresaEntity> empresas = (List<EmpresaEntity>) 
					baseEJB.pesquisaArgFixos(EmpresaEntity.class, "findByIdComplete", args);
					
			if(empresas != null && !empresas.isEmpty())
				empresa = empresas.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empresa;
	}
	
	protected List<SelectItem> montaListaDepartamentos(EmpresaEntity empresa) {
		List<SelectItem> listaDepartamentos = new ArrayList<SelectItem>();
		listaDepartamentos.add(new SelectItem(null, "Selecione"));
		
		empresa.getDepartamentos().forEach(departamento -> {
			listaDepartamentos.add(new SelectItem(departamento.getId(), departamento.getNome()));
		});
		
		return listaDepartamentos;
	}
	
	protected List<SelectItem> montaListaCentroDeCusto(EmpresaEntity empresa) {
		List<SelectItem> listaCentrosDeCusto = new ArrayList<SelectItem>();
		listaCentrosDeCusto.add(new SelectItem(null, "Selecione"));
		
		empresa.getCentros().forEach(centro -> {
			listaCentrosDeCusto.add(new SelectItem(centro.getId(), centro.getNome()));
		});
		
		return listaCentrosDeCusto;
	}
	
	protected List<SelectItem> montaListaCargo(EmpresaEntity empresa) {
		List<SelectItem> listaCargos = new ArrayList<SelectItem>();
		listaCargos.add(new SelectItem(null, "Selecione"));
		
		empresa.getCargos().forEach(cargo -> {
			listaCargos.add(new SelectItem(cargo.getId(), cargo.getNome()));
		});
		
		return listaCargos;
	}
	
	@SuppressWarnings("unchecked")
	protected List<SelectItem> montaListaEquipamentos(){
		List<SelectItem> listaEquipamentos = new ArrayList<SelectItem>();
		listaEquipamentos.add(new SelectItem(null, "Filtrar por equipamento"));
		
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("ID_CLIENTE", getUsuarioLogado().getCliente().getId());
			
			List<EquipamentoEntity> equipamentos = (List<EquipamentoEntity>) 
					baseEJB.pesquisaArgFixos(EquipamentoEntity.class, "findAllByIdCliente", args);
			
			if(equipamentos != null && !equipamentos.isEmpty()) {
				equipamentos.forEach(equipamento -> {
					String identificador = null;
					if(equipamento.getMarca().contains("TopData"))
						identificador = "Inner " + equipamento.getIdentificador().split(";")[0];
					else
						identificador = equipamento.getIdentificador();
					
					listaEquipamentos.add(new SelectItem(identificador, equipamento.getNome()));
				});
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return listaEquipamentos;
	}
	
	public void alteraDataInicio(ValueChangeEvent event) {
		getParans().put("data_maior_data", event.getNewValue());
	}
	
	public void alteraDataFim(ValueChangeEvent event) {
		getParans().put("data_menor_data", event.getNewValue());
	}
	
	public StreamedContent getStreamedContent(byte[] foto) {
		return Utils.getStreamedContent(foto);
	}
}
