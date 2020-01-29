package modelo.dao;

import db.DB;
import modelo.dao.impl.ClienteDaoJDBC;
import modelo.dao.impl.DepartamentoDaoJDBC;
import modelo.dao.impl.FuncionarioDaoJDBC;

public class FabricaDao {

	
	public static ClienteDao criarClienteDao() {
		return  new ClienteDaoJDBC(DB.getConnection());
	}
	
	public static DepartamentoDao criarDepartamentoDao() {
		return new DepartamentoDaoJDBC(DB.getConnection());
	}
	
	public static FuncionarioDao criarFuncionarioDao() {
		return new FuncionarioDaoJDBC(DB.getConnection());
	}
}
