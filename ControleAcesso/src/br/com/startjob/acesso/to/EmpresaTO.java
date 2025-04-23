package br.com.startjob.acesso.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import br.com.startjob.acesso.modelo.entity.CargoEntity;
import br.com.startjob.acesso.modelo.entity.CentroCustoEntity;
import br.com.startjob.acesso.modelo.entity.DepartamentoEntity;
import br.com.startjob.acesso.modelo.entity.EmpresaEntity;

public class EmpresaTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nome;
	private String cnpj;
	private String email;
	private String telefone;
	private String celular;
	private String status;
	
	private Boolean removed;
	private Boolean autoAtendimentoLiberado;
	private Date dataRemovido;

	private ArrayList<CargoTO> cargos;
	private ArrayList<CentroTO> centros;
	private ArrayList<DepartamentoTO> departamentos;
	
	public EmpresaTO(EmpresaEntity empresa) {
		this.id = empresa.getId();
		this.nome = empresa.getNome();
		this.cnpj = empresa.getCnpj();
		this.email = empresa.getEmail();
		this.celular = empresa.getCelular();
		this.telefone = empresa.getTelefone();
		this.status = empresa.getStatus().toString();
		this.removed = empresa.getRemovido();
		this.dataRemovido = empresa.getDataRemovido();
		this.autoAtendimentoLiberado = empresa.getAutoAtendimentoLiberado();
		
		if(empresa.getCargos() != null) {
			this.cargos = new ArrayList<>();
			for(CargoEntity cargo : empresa.getCargos()) {
				this.cargos.add(new CargoTO(cargo));
			}
		}
		
		if(empresa.getCentros() != null) {
			this.centros = new ArrayList<>();
			for(CentroCustoEntity centro : empresa.getCentros()) {
				this.centros.add(new CentroTO(centro));
			}
		}
		
		if(empresa.getDepartamentos() != null) {
			this.departamentos = new ArrayList<>();
			for(DepartamentoEntity departamento : empresa.getDepartamentos()) {
				this.departamentos.add(new DepartamentoTO(departamento));
			}
		}
	}

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

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public ArrayList<CargoTO> getCargos() {
		return cargos;
	}

	public void setCargos(ArrayList<CargoTO> cargos) {
		this.cargos = cargos;
	}

	public ArrayList<CentroTO> getCentros() {
		return centros;
	}

	public void setCentros(ArrayList<CentroTO> centros) {
		this.centros = centros;
	}

	public ArrayList<DepartamentoTO> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(ArrayList<DepartamentoTO> departamentos) {
		this.departamentos = departamentos;
	}

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public Date getDataRemovido() {
		return dataRemovido;
	}

	public void setDataRemovido(Date dataRemovido) {
		this.dataRemovido = dataRemovido;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getAutoAtendimentoLiberado() {
		return autoAtendimentoLiberado;
	}

	public void setAutoAtendimentoLiberado(Boolean autoAtendimentoLiberado) {
		this.autoAtendimentoLiberado = autoAtendimentoLiberado;
	}
}
