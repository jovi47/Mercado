package modelo.dao;

import java.util.List;

import model.entities.Stock;

public interface StockDao {

	void insert(Stock obj);

	void update(Stock obj);

	void deleteById(Integer id);

	Stock findById(Integer id);

	List<Stock> findAll();

}
