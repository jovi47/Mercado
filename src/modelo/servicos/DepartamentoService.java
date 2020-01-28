package modelo.servicos;

import java.util.List;

import modelo.dao.DepartamentoDao;
import modelo.dao.FabricaDao;
import modelo.entidades.Departamento;

public class DepartamentoService {

	private DepartamentoDao dao = FabricaDao.criarDepartamentoDao();

	public List<Departamento> findAll() {
    return dao.findAll();
	}
	
	public void saveOrUpdate(Departamento obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
	
	public void remove(Departamento obj) {
		dao.deleteById(obj.getId());
	}
}
