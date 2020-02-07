package modelo.dao;

import java.util.List;

import modelo.entidades.Estoque;

public interface EstoqueDao {

	void insert(Estoque obj);

	void update(Estoque obj);

	void deleteById(Integer id);

	Estoque findById(Integer id);

	List<Estoque> findAll();

}
