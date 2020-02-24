package model.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Employee extends Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String CPF;
	private String CEP;
	private String fone;
	private Calendar birthDate;
	private Department department;
	private Double salary;
	private Calendar hiringDate;
	private Calendar resignationDate;

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department departamento) {
		this.department = departamento;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public Calendar getHiringDate() {
		return hiringDate;
	}

	public void setHiringDate(Calendar hiringDate) {
		this.hiringDate = hiringDate;
	}

	public String getResignationDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return (resignationDate == null) ? null : sdf.format(resignationDate.getTime());
	}

	public void setResignationDate(Calendar resignationDate) {
		this.resignationDate = resignationDate;
	}

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

	public void setBirthDate(Calendar birthDate) {
		this.birthDate = birthDate;
	}

}
