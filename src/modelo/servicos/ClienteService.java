package modelo.servicos;

import java.util.List;

import modelo.dao.ClienteDao;
import modelo.dao.FabricaDao;
import modelo.entidades.Cliente;

public class ClienteService {

	private ClienteDao dao = FabricaDao.criarClienteDao();

	public List<Cliente> findAll() {
    return dao.findAll();
	}
	
	public void saveOrUpdate(Cliente obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
	
	public void remove(Cliente obj) {
		dao.deleteById(obj.getId());
	}
}
