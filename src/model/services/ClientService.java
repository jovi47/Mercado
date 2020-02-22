package model.services;

import java.util.List;

import model.entities.Client;
import modelo.dao.ClientDao;
import modelo.dao.DaoFactory;

public class ClientService {

	private ClientDao dao = DaoFactory.createClientDao();

	public List<Client> findAll() {
    return dao.findAll();
	}
	
	public void saveOrUpdate(Client obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
	
	public void remove(Client obj) {
		dao.deleteById(obj.getId());
	}
}
