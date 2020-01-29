package modelo.dao;

import java.util.List;

import modelo.entidades.Departamento;
import modelo.entidades.Funcionario;

public interface FuncionarioDao {
	void insert(Funcionario obj);

	void update(Funcionario obj);

	void deleteById(Integer id);

	Funcionario findById(Integer id);

	List<Funcionario> findAll();
	
	List<Funcionario> findByDepartment(Departamento department);
}
