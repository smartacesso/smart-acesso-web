package com.senior.services.dto;

import java.util.Objects;

public class FuncionarioSeniorDto {
	
    String codPrm;   // cod permissão
    String datAdm;
    String datAfa;
    String datTer;
    String desAfa;
	String datDem;
    String datAlt;
    String datNas;
    String dddtelefone;
    String numtelefone;
    String emailComercial; //email corporativo
    String emailPessoal; //email pessoal
//    <fotEmp xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
    String Nome;
    String empresa;
    String numeroMatricula; //<numCad>376</numCad>
    String rg;  // <numCid>6157492</numCid> rg
    String numCracha; // xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
    String numFisicoCracha; // >0</numFis>
    String numEmpresa; // 1</numEmp>
    String tipCol; // <tipCol>Empregado</tipCol>
    String cargo; // <titRed>DIRETOR</titRed>
    String obs; // <titRed>DIRETOR</titRed>
    String usaRef; //<usaRef>S</usaRef>

	public String getCodPrm() {
		return codPrm;
	}
	public void setCodPrm(String codPrm) {
		this.codPrm = codPrm;
	}
	public String getDatAdm() {
		return datAdm;
	}
	public void setDatAdm(String datAdm) {
		this.datAdm = datAdm;
	}
	public String getDatDem() {
		return datDem;
	}
	public void setDatDem(String datDem) {
		this.datDem = datDem;
	}
	public String getDatNas() {
		return datNas;
	}
	public void setDatNas(String datNas) {
		this.datNas = datNas;
	}
	public String getEmailComercial() {
		return emailComercial;
	}
	public void setEmailComercial(String emailComercial) {
		this.emailComercial = emailComercial;
	}
	public String getEmailPessoal() {
		return emailPessoal;
	}
	public void setEmailPessoal(String emailPessoal) {
		this.emailPessoal = emailPessoal;
	}
	public String getNome() {
		return Nome;
	}
	public void setNome(String nome) {
		Nome = nome;
	}
	public String getEmpresa() {
		return empresa;
	}
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	public String getRg() {
		return rg;
	}
	public void setRg(String rg) {
		this.rg = rg;
	}
	public String getNumCracha() {
		return numCracha;
	}
	public void setNumCracha(String numCracha) {
		this.numCracha = numCracha;
	}
	public String getNumEmpresa() {
		return numEmpresa;
	}
	public void setNumEmpresa(String numEmpresa) {
		this.numEmpresa = numEmpresa;
	}
	public String getTipCol() {
		return tipCol;
	}
	public void setTipCol(String tipCol) {
		this.tipCol = tipCol;
	}
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	public String getDddtelefone() {
		return dddtelefone;
	}
	public void setDddtelefone(String dddtelefone) {
		this.dddtelefone = dddtelefone;
	}
	public String getNumtelefone() {
		return numtelefone;
	}
	public void setNumtelefone(String numtelefone) {
		this.numtelefone = numtelefone;
	}
	public String getNumeroMatricula() {
		return numeroMatricula;
	}
	public void setNumeroMatricula(String numeroMatricula) {
		this.numeroMatricula = numeroMatricula;
	}
	public String getNumFisicoCracha() {
		return numFisicoCracha;
	}
	public void setNumFisicoCracha(String numFisicoCracha) {
		this.numFisicoCracha = numFisicoCracha;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public String getDatAfa() {
		return datAfa;
	}
	public void setDatAfa(String datAfa) {
		this.datAfa = datAfa;
	}
	public String getDatAlt() {
		return datAlt;
	}
	public void setDatAlt(String datAlt) {
		this.datAlt = datAlt;
	}
    public String getDatTer() {
		return datTer;
	}
	public void setDatTer(String datTer) {
		this.datTer = datTer;
	}
	public String getDesAfa() {
		return desAfa;
	}
	public void setDesAfa(String desAfa) {
		this.desAfa = desAfa;
	}
	public String getUsaRef() {
		return usaRef;
	}
	public void setUsaRef(String usaRef) {
		this.usaRef = usaRef;
	}
	
	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof FuncionarioSeniorDto)) return false;
	        FuncionarioSeniorDto that = (FuncionarioSeniorDto) o;

	        // sempre compara matrícula + empresa
	        boolean baseEquals = Objects.equals(numeroMatricula, that.numeroMatricula)
	                && Objects.equals(numEmpresa, that.numEmpresa);

	        // se ambos tiverem cartão preenchido, também compara cartão
	        if (this.numCracha != null && that.numCracha != null) {
	            return baseEquals && Objects.equals(numCracha, that.numCracha);
	        }

	        // senão, usa só matrícula + empresa
	        return baseEquals;
	    }

	    @Override
	    public int hashCode() {
	        // hash sempre de matrícula + empresa
	        int result = Objects.hash(numeroMatricula, numEmpresa);

	        // se tiver cartão, inclui no hash
	        if (numCracha != null) {
	            result = 31 * result + numCracha.hashCode();
	        }
	        return result;
	    }

	    @Override
	    public String toString() {
	        return "FuncionarioSeniorDto{" +
	                "matricula='" + numeroMatricula + '\'' +
	                ", empresa='" + numEmpresa + '\'' +
	                ", cracha='" + numCracha + '\'' +
	                ", nome='" + Nome + '\'' +
	                '}';
	    }
}
