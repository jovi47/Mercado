package modelo.dao;

import db.DB;
import modelo.dao.impl.ClienteDaoJDBC;
import modelo.dao.impl.DepartamentoDaoJDBC;
import modelo.dao.impl.EstoqueDaoJDBC;
import modelo.dao.impl.FuncionarioDaoJDBC;
import modelo.dao.impl.ProdutoDaoJDBC;

public class FabricaDao {

	public static ClienteDao criarClienteDao() {
		return new ClienteDaoJDBC(DB.getConnection());
	}

	public static DepartamentoDao criarDepartamentoDao() {
		return new DepartamentoDaoJDBC(DB.getConnection());
	}

	public static FuncionarioDao criarFuncionarioDao() {
		return new FuncionarioDaoJDBC(DB.getConnection());
	}

	public static ProdutoDao criarProdutoDao() {
		return new ProdutoDaoJDBC(DB.getConnection());
	}

	public static EstoqueDao criarEstoqueDao() {
		return new EstoqueDaoJDBC(DB.getConnection());
	}
}
