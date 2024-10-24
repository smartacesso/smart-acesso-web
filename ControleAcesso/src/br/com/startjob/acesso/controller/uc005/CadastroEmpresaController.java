package br.com.startjob.acesso.controller.uc005;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.CadastroBaseController;
import br.com.startjob.acesso.modelo.ejb.EmpresaEJBRemote;
import br.com.startjob.acesso.modelo.entity.CargoEntity;
import br.com.startjob.acesso.modelo.entity.CentroCustoEntity;
import br.com.startjob.acesso.modelo.entity.DepartamentoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;
import br.com.startjob.acesso.modelo.entity.EnderecoEntity;
import br.com.startjob.acesso.modelo.enumeration.Status;

@Named("cadastroEmpresaController")
@ViewScoped
@UseCase(classEntidade=EmpresaEntity.class, funcionalidade="Cadastro de empresas", 
		urlNovoRegistro="/paginas/sistema/empresas/cadastroEmpresa.jsf", queryEdicao="findByIdComplete")
public class CadastroEmpresaController extends CadastroBaseController {

	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> listaStatus;
	
	private DepartamentoEntity departamento;
	private List<DepartamentoEntity> listaDepartamentos;
	private List<DepartamentoEntity> listaDepartamentosExcluidos;
	
	private CentroCustoEntity centroCusto;
	private List<CentroCustoEntity> listaCentroCustos;
	private List<CentroCustoEntity> listaCentroCustosExcluidos;
	
	private CargoEntity cargo;
	private List<CargoEntity> listaCargos;
	private List<CargoEntity> listaCargosExcluidos;
	
	@EJB
	protected EmpresaEJBRemote empresaEJB;

	public List<SelectItem> getListaStatus() {
		return listaStatus;
	}

	public void setListaStatus(List<SelectItem> listaStatus) {
		this.listaStatus = listaStatus;
	}
	
	@PostConstruct
	@Override
	public void init() {
		baseEJB = empresaEJB;
		
		super.init();
		inicializaVariaveis();
		
		montaListaStatus();
	}
	
	public void inicializaVariaveis() {
		EmpresaEntity empresa = (EmpresaEntity) getEntidade();
		
		if(empresa != null && empresa.getEndereco() == null)
			empresa.setEndereco(new EnderecoEntity());
		
		if(empresa.getDepartamentos() != null && !empresa.getDepartamentos().isEmpty())
			listaDepartamentos = empresa.getDepartamentos();
		
		if(empresa.getCentros() != null && !empresa.getCentros().isEmpty())
			listaCentroCustos = empresa.getCentros();
		
		if(empresa.getCargos() != null && !empresa.getCargos().isEmpty())
			listaCargos = empresa.getCargos();
		
		departamento = new DepartamentoEntity();
		centroCusto = new CentroCustoEntity();
		cargo = new CargoEntity();
	}
	
	@Override
	public String salvar() {
		EmpresaEntity empresa = (EmpresaEntity) getEntidade();

		if(empresa.getTelefone() == null && empresa.getCelular() == null) {
			mensagemFatal("", "msg.add.telefone");
			return "";
		}
		
		if(empresa.getEndereco().getCep() == null || empresa.getEndereco().getCep().equals(""))
			empresa.setEndereco(null);
		
		if(listaCargos != null || listaCargosExcluidos != null)
			empresa.setCargos(new ArrayList<>());
		if(listaCargos != null && !listaCargos.isEmpty())
			empresa.getCargos().addAll(listaCargos);
		if(listaCargosExcluidos != null && !listaCargosExcluidos.isEmpty())
			empresa.getCargos().addAll(listaCargosExcluidos);

		if(listaDepartamentos != null || listaDepartamentosExcluidos != null)
			empresa.setDepartamentos(new ArrayList<>());
		if(listaDepartamentos != null && !listaDepartamentos.isEmpty())
			empresa.getDepartamentos().addAll(listaDepartamentos);
		if(listaDepartamentosExcluidos != null && !listaDepartamentosExcluidos.isEmpty())
			empresa.getDepartamentos().addAll(listaDepartamentosExcluidos);
		
		if(listaCentroCustos != null || listaCentroCustosExcluidos != null)
			empresa.setCentros(new ArrayList<>());
		if(listaCentroCustos != null && !listaCentroCustos.isEmpty())
			empresa.getCentros().addAll(listaCentroCustos);
		if(listaCentroCustosExcluidos != null && !listaCentroCustosExcluidos.isEmpty())
			empresa.getCentros().addAll(listaCentroCustosExcluidos);
		
		empresa.setCliente(getUsuarioLogado().getCliente());
		
		String retorno = super.salvar();
		
		listaCargos = new ArrayList<>();
		listaDepartamentos = new ArrayList<>();
		listaCentroCustos = new ArrayList<>();
		
		redirect("/paginas/sistema/empresas/pesquisaEmpresa.xhtml?acao=OK");
		
		return retorno;
	}
	
	public void adicionarDepartamento() {
		if(departamento == null) {
			departamento = new DepartamentoEntity();
			return;
		}
		
		boolean valido = true;
		
		if(departamento.getNome() == null || "".equals(departamento.getNome())) {
			mensagemFatal("", "#Nome é obrigatório!");
			valido = false;
		}
		
		if(departamento.getStatus() == null) {
			mensagemFatal("", "#Status é obrigatório!");
			valido = false;
		}
		
		if(!valido)
			return;
		
		EmpresaEntity empresa = (EmpresaEntity) getEntidade();
		departamento.setEmpresa(empresa);
		
		if(listaDepartamentos == null)
			listaDepartamentos = new ArrayList<DepartamentoEntity>();

		listaDepartamentos.add(departamento);
		
		departamento = new DepartamentoEntity();
	}
	
	public void adicionarCentroDeCusto() {
		if(centroCusto == null) {
			centroCusto = new CentroCustoEntity();
			return;
		}
		
		boolean valido = true;
		
		if(centroCusto.getNome() == null || "".equals(centroCusto.getNome())) {
			mensagemFatal("", "#Nome é obrigatório!");
			valido = false;
		}
		
		if(centroCusto.getStatus() == null) {
			mensagemFatal("", "#Status é obrigatório!");
			valido = false;
		}
		
		if(!valido)
			return;
		
		EmpresaEntity empresa = (EmpresaEntity) getEntidade();
		centroCusto.setEmpresa(empresa);
		
		if(listaCentroCustos == null)
			listaCentroCustos = new ArrayList<>();
		
		listaCentroCustos.add(centroCusto);
		
		centroCusto = new CentroCustoEntity();
	}
	
	public void adicionarCargo() {
		if(cargo == null) {
			cargo = new CargoEntity();
			return;
		}
		
		boolean valido = true;
		
		if(cargo.getNome() == null || "".equals(cargo.getNome())) {
			mensagemFatal("", "#Nome é obrigatório!");
			valido = false;
		}
		
		if(cargo.getStatus() == null) {
			mensagemFatal("", "#Status é obrigatório!");
			valido = false;
		}
		
		if(!valido)
			return;
		
		EmpresaEntity empresa = (EmpresaEntity) getEntidade();
		cargo.setEmpresa(empresa);
		
		if(listaCargos == null)
			listaCargos = new ArrayList<>();
		
		listaCargos.add(cargo);
		
		cargo = new CargoEntity();
	}
	
	public void removeDepartamento(DepartamentoEntity departamentoSelecionado) {
		if(departamentoSelecionado == null)
			return;
		
		if(departamentoSelecionado.getId() != null) {
			for(DepartamentoEntity d : listaDepartamentos) {
				if(!departamentoSelecionado.getId().equals(d.getId()))
					continue;
				
				d.setRemovido(true);
				d.setDataRemovido(new Date());
				
				if(listaDepartamentosExcluidos == null)
					listaDepartamentosExcluidos = new ArrayList<>();
				
				listaDepartamentosExcluidos.add(departamentoSelecionado);
				listaDepartamentos.remove(departamentoSelecionado);
				break;
			}
			
		} else {
			listaDepartamentos.remove(departamentoSelecionado);
		}
		
		mensagemInfo("", "msg.uc005.departamento.removido.sucesso");
	}
	
	public void removeCentroDeCusto(CentroCustoEntity centroDeCustoSelecionado) {
		if(centroDeCustoSelecionado == null)
			return;
		
		if(centroDeCustoSelecionado.getId() != null) {
			for(CentroCustoEntity c : listaCentroCustos) {
				if(!centroDeCustoSelecionado.getId().equals(c.getId()))
					continue;
				
				c.setRemovido(true);
				c.setDataRemovido(new Date());
				
				if(listaCentroCustosExcluidos == null)
					listaCentroCustosExcluidos = new ArrayList<>();
				
				listaCentroCustosExcluidos.add(centroDeCustoSelecionado);
				listaCentroCustos.remove(centroDeCustoSelecionado);
				break;
			}

		} else {
			listaCentroCustos.remove(centroDeCustoSelecionado);
		}
		
		mensagemInfo("", "msg.uc005.centro.removido.sucesso");
	}
	
	public void removeCargo(CargoEntity cargoSelecionado) {
		if(cargoSelecionado == null)
			return;
		
		if(cargoSelecionado.getId() != null) {
			for(CargoEntity c : listaCargos) {
				if(!cargoSelecionado.getId().equals(c.getId()))
					continue;
				
				c.setRemovido(true);
				c.setDataRemovido(new Date());
				
				if(listaCargosExcluidos == null)
					listaCargosExcluidos = new ArrayList<>();
				
				listaCargosExcluidos.add(cargoSelecionado);
				listaCargos.remove(cargoSelecionado);
				break;
			}
		} else {
			listaCargos.remove(cargoSelecionado);
		}
		
		mensagemInfo("", "msg.uc005.cargo.removido.sucesso");
	}
	
	private void montaListaStatus() {
		listaStatus = new ArrayList<SelectItem>();
		listaStatus.add(new SelectItem(null, "Selecione"));
		listaStatus.add(new SelectItem(Status.ATIVO, Status.ATIVO.toString()));
		listaStatus.add(new SelectItem(Status.INATIVO, Status.INATIVO.toString()));
	}

	public DepartamentoEntity getDepartamento() {
		return departamento;
	}

	public void setDepartamento(DepartamentoEntity departamento) {
		this.departamento = departamento;
	}

	public List<DepartamentoEntity> getListaDepartamentos() {
		return listaDepartamentos;
	}

	public void setListaDepartamentos(List<DepartamentoEntity> listaDepartamentos) {
		this.listaDepartamentos = listaDepartamentos;
	}

	public CentroCustoEntity getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(CentroCustoEntity centroCusto) {
		this.centroCusto = centroCusto;
	}

	public List<CentroCustoEntity> getListaCentroCustos() {
		return listaCentroCustos;
	}

	public void setListaCentroCustos(List<CentroCustoEntity> listaCentroCustos) {
		this.listaCentroCustos = listaCentroCustos;
	}

	public CargoEntity getCargo() {
		return cargo;
	}

	public void setCargo(CargoEntity cargo) {
		this.cargo = cargo;
	}

	public List<CargoEntity> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<CargoEntity> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public List<DepartamentoEntity> getListaDepartamentosExcluidos() {
		return listaDepartamentosExcluidos;
	}

	public void setListaDepartamentosExcluidos(List<DepartamentoEntity> listaDepartamentosExcluidos) {
		this.listaDepartamentosExcluidos = listaDepartamentosExcluidos;
	}

	public List<CentroCustoEntity> getListaCentroCustosExcluidos() {
		return listaCentroCustosExcluidos;
	}

	public void setListaCentroCustosExcluidos(List<CentroCustoEntity> listaCentroCustosExcluidos) {
		this.listaCentroCustosExcluidos = listaCentroCustosExcluidos;
	}

	public List<CargoEntity> getListaCargosExcluidos() {
		return listaCargosExcluidos;
	}

	public void setListaCargosExcluidos(List<CargoEntity> listaCargosExcluidos) {
		this.listaCargosExcluidos = listaCargosExcluidos;
	}
	
}
