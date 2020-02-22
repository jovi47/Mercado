package model.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Employee extends Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String nome;
	private String CPF;
	private String CEP;
	private String telefone;
	private Calendar dataNascimento;
	private Department departamento;
	private Double salario;
	private Calendar inicioContrato;
	private Calendar fimContrato;

	@Override
	public String toString() {
		return "Funcionario [id=" + id + ", nome=" + nome + ", CPF=" + CPF + ", CEP=" + CEP + ", telefone=" + telefone
				+ ", dataNascimento=" + dataNascimento + ", departamento=" + departamento + ", salario=" + salario
				+ ", inicioContrato=" + inicioContrato + ", fimContrato=" + fimContrato + "]";
	}

	public Department getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Department departamento) {
		this.departamento = departamento;
	}

	public Double getSalario() {
		return salario;
	}

	public void setSalario(Double salario) {
		this.salario = salario;
	}

	public Calendar getInicioContrato() {
		return inicioContrato;
	}

	public void setInicioContrato(Calendar inicioContrato) {
		this.inicioContrato = inicioContrato;
	}

	public String getFimContrato() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return (fimContrato == null) ? null : sdf.format(fimContrato.getTime());
	}

	public void setFimContrato(Calendar fimContrato) {
		this.fimContrato = fimContrato;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCPF() {
		return CPF;
	}

	public void setCPF(String CPF) {
		this.CPF = CPF;
	}

	public String getCEP() {
		return CEP;
	}

	public void setCEP(String CEP) {
		this.CEP = CEP;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Calendar getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Calendar dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

}
