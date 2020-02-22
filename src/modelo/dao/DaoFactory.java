package modelo.dao;

import db.DB;
import modelo.dao.impl.ClientDaoJDBC;
import modelo.dao.impl.DepartmentDaoJDBC;
import modelo.dao.impl.StockDaoJDBC;
import modelo.dao.impl.EmployeeDaoJDBC;
import modelo.dao.impl.ProductDaoJDBC;

public class DaoFactory {

	public static ClientDao createClientDao() {
		return new ClientDaoJDBC(DB.getConnection());
	}

	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
	}

	public static EmployeeDao createEmployeeDao() {
		return new EmployeeDaoJDBC(DB.getConnection());
	}

	public static ProductDao createProductDao() {
		return new ProductDaoJDBC(DB.getConnection());
	}

	public static StockDao createStockDao() {
		return new StockDaoJDBC(DB.getConnection());
	}
}
