package modelo.servicos;

import java.util.List;

import modelo.dao.FabricaDao;
import modelo.dao.ProdutoDao;
import modelo.entidades.Produto;

public class ProdutoService {

	
	private ProdutoDao dao = FabricaDao.criarProdutoDao();
	public List<Produto> findAll() {
	    return dao.findAll();
		}
		
		public void saveOrUpdate(Produto obj) {
			if(obj.getId()==null) {
				dao.insert(obj);
			}else {
				dao.update(obj);
			}
		}
		
		public void remove(Produto obj) {
			dao.deleteById(obj.getId());
		}
		
		public Produto findById(Integer id) {
			return dao.findById(id);
		}
}
