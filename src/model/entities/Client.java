package model.entities;

import java.io.Serializable;
import java.util.Calendar;

public class Client extends Entity implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String CPF;
	private String CEP;
	private String fone;
	private Calendar birthDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getFone() {
		return fone;
	}

	public void setFone(String fone) {
		this.fone = fone;
	}

	public Calendar getBirthDate() {
		return birthDate;
	}

	public void setDataNascimento(Calendar dataNascimento) {
		this.birthDate = dataNascimento;
	}

	@Override
	public String toString() {
		return "Cliente [id=" + id + ", nome=" + name + ", CPF=" + CPF + ", CEP=" + CEP + ", telefone=" + fone
				+ ", dataNascimento=" + birthDate + "]";
	}
}
