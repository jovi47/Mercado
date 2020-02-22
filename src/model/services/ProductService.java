package model.services;

import java.util.List;

import model.entities.Product;
import modelo.dao.DaoFactory;
import modelo.dao.ProductDao;

public class ProductService {

	
	private ProductDao dao = DaoFactory.createProductDao();
	public List<Product> findAll() {
	    return dao.findAll();
		}
		
		public void saveOrUpdate(Product obj) {
			if(obj.getId()==null) {
				dao.insert(obj);
			}else {
				dao.update(obj);
			}
		}
		
		public void remove(Product obj) {
			dao.deleteById(obj.getId());
		}
		
		public Product findById(Integer id) {
			return dao.findById(id);
		}
}
