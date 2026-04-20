package br.com.startjob.acesso.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.primefaces.model.StreamedContent;

import br.com.startjob.acesso.modelo.ejb.EmpresaEJBRemote;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.EquipamentoEntity;
import br.com.startjob.acesso.modelo.entity.base.BaseEntity;
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
					if(equipamento.getMarca().contains("TopData")) {
					} else {
					}
					
					listaEquipamentos.add(new SelectItem(equipamento.getNome(), equipamento.getNome()));
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
	
	public void exportarExcelCustomizado(List<AcessoEntity> registrosTela) {
	    System.out.println("--- INICIANDO EXPORTAÇÃO DA PÁGINA ATUAL ---");

	    if (registrosTela == null || registrosTela.isEmpty()) {
	        System.out.println("Nenhum dado na tela para exportar.");
	        mensagemAviso("", "Não há dados na tela para exportar.");
	        return;
	    }

	    try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
	        workbook.setCompressTempFiles(true);
	        Sheet sheet = workbook.createSheet("Relatório de Acessos");
	        int rowNum = 0;
	        
	        criarCabecalho(sheet.createRow(rowNum++));

	        // Faz o loop direto na lista da tela
	        for (BaseEntity baseObj : registrosTela) {
	            
	            // Faz o cast (conversão) para a sua Entidade real
	            AcessoEntity acesso = (AcessoEntity) baseObj;

	            Row row = sheet.createRow(rowNum++);
	            
	            // Lógica de proteção contra NullPointerException ao navegar nos relacionamentos
	            String matricula = acesso.getPedestre() != null ? safe(acesso.getPedestre().getMatricula()) : "";
	            String nome = acesso.getPedestre() != null ? safe(acesso.getPedestre().getNome()) : "";
	            
	            String empresa = (acesso.getPedestre() != null && acesso.getPedestre().getEmpresa() != null) 
	                             ? safe(acesso.getPedestre().getEmpresa().getNome()) : "";
	                             
	            String cargo = (acesso.getPedestre() != null && acesso.getPedestre().getCargo() != null) 
	                           ? safe(acesso.getPedestre().getCargo().getNome()) : "";

	            // Preenchimento chamando os GETTERS da entidade, na mesma ordem do seu cabeçalho
	            row.createCell(0).setCellValue(matricula); 
	            row.createCell(1).setCellValue(safe(acesso.getCartaoAcessoRecebido())); 
	            row.createCell(2).setCellValue(nome); 
	            row.createCell(3).setCellValue(empresa); 
	            row.createCell(4).setCellValue(cargo); 
	            
	            // Formatação da Data (pode usar Date nativo ou converter para String)
	            row.createCell(5).setCellValue(safe(acesso.getData())); 
	            
	            row.createCell(6).setCellValue(safe(acesso.getEquipamento())); 
	            row.createCell(7).setCellValue(traduzTipo(safe(acesso.getTipo()))); 
	            row.createCell(8).setCellValue(safe(acesso.getSentido())); 
	        }

	        // Fluxo de envio do arquivo único para o navegador
	        FacesContext fc = FacesContext.getCurrentInstance();
	        ExternalContext ec = fc.getExternalContext();
	        ec.responseReset(); 
	        ec.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"relatorio_pagina.xlsx\"");

	        java.io.OutputStream out = ec.getResponseOutputStream();
	        workbook.write(out);
	        out.flush();
	        workbook.dispose(); 
	        out.close(); 
	        fc.responseComplete();

	        System.out.println("--- SUCESSO! ARQUIVO DA PÁGINA GERADO ---");

	    } catch (Throwable e) { 
	        System.err.println(">>>> ERRO NA GERAÇÃO DO ARQUIVO: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	// Métodos auxiliares
	private void criarCabecalho(Row headerRow) {
	    String[] colunas = {"Matrícula", "Cartão", "Nome", "Empresa", "Cargo", "Data Acesso", "Equipamento", "Tipo Acesso", "Sentido"};
	    for (int i = 0; i < colunas.length; i++) {
	        headerRow.createCell(i).setCellValue(colunas[i]);
	    }
	}

	private String safe(Object value) {
	    return value != null ? value.toString() : "";
	}

	private String traduzTipo(String tipo) {
	    if (tipo == null) return "";
	    switch (tipo) {
	        case "ATIVO": return "LIBERADO";
	        case "INATIVO": return "BLOQUEADO";
	        case "INDEFINIDO": return "NÃO GIROU";
	        default: return tipo;
	    }
	}
}
