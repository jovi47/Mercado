package model.services;

import java.util.List;

import model.entities.Stock;
import modelo.dao.StockDao;
import modelo.dao.DaoFactory;

public class StockService {

	private StockDao dao = DaoFactory.createStockDao();

	public List<Stock> findAll() {
    return dao.findAll();
	}
	
	public void saveOrUpdate(Stock obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
	
	public void remove(Stock obj) {
		dao.deleteById(obj.getId());
	}
	
}
