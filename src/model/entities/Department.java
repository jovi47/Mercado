package model.entities;

import java.io.Serializable;


public class Department extends Entity implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;

	public Department() {
	}
	public Department(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return ""+name;
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

	
}
