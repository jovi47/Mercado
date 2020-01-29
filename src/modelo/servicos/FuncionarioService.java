package modelo.servicos;

import java.util.List;


import modelo.dao.FabricaDao;
import modelo.dao.FuncionarioDao;
import modelo.entidades.Funcionario;

public class FuncionarioService {
	
	private FuncionarioDao dao = FabricaDao.criarFuncionarioDao();
	public List<Funcionario> findAll() {
	    return dao.findAll();
		}
		
		public void saveOrUpdate(Funcionario obj) {
			if(obj.getId()==null) {
				dao.insert(obj);
			}else {
				dao.update(obj);
			}
		}
		
		public void remove(Funcionario obj) {
			dao.deleteById(obj.getId());
		}
}
