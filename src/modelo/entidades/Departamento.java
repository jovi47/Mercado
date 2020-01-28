package modelo.entidades;

import java.io.Serializable;


public class Departamento implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String nome;

	public Departamento() {
	}
	public Departamento(Integer id, String name) {
		this.id = id;
		this.nome = name;
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return nome;
	}

	public void setName(String name) {
		this.nome = name;
	}

	
}
