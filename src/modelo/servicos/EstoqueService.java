package modelo.servicos;

import java.util.List;

import modelo.dao.EstoqueDao;
import modelo.dao.FabricaDao;
import modelo.entidades.Estoque;

public class EstoqueService {

	private EstoqueDao dao = FabricaDao.criarEstoqueDao();

	public List<Estoque> findAll() {
    return dao.findAll();
	}
	
	public void saveOrUpdate(Estoque obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
	
	public void remove(Estoque obj) {
		dao.deleteById(obj.getId());
	}
	
}
