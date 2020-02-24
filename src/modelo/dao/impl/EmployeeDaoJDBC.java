package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.entities.Employee;
import modelo.dao.EmployeeDao;
import views.util.Alerts;

public class EmployeeDaoJDBC implements EmployeeDao {

	private Connection conn;

	public EmployeeDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Employee obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO funcionario (nome, cpf, cep , telefone, data_nascimento, "
					+ "inicio_contrato, salario, departamentoId, fim_contrato) " + ""
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
			if (obj.getResignationDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date data = sdf.parse(obj.getResignationDate());
				st.setDate(9, new java.sql.Date(data.getTime()));
			} else {
				st.setNull(9, Types.DATE);
			}
			st.setString(1, obj.getName());
			st.setString(2, obj.getCPF());
			Date y = obj.getBirthDate().getTime();
			st.setDate(5, new java.sql.Date(y.getTime()));
			st.setString(3, obj.getCEP());
			st.setString(4, obj.getFone());
			Date x = obj.getHiringDate().getTime();
			st.setDate(6, new java.sql.Date(x.getTime()));
			st.setDouble(7, obj.getSalary());
			st.setInt(8, obj.getDepartment().getId());
			unformat(obj);
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected error! No rows affected.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	private void unformat(Employee obj) {
		String cpf = obj.getCPF().substring(0, 3) + obj.getCPF().substring(4, 7) + obj.getCPF().substring(8, 11)
				+ obj.getCPF().substring(12, 14);
		obj.setCPF(cpf);
		String telefone = obj.getFone().substring(1, 3) + obj.getFone().substring(4, 9)
				+ obj.getFone().substring(10, 14);
		obj.setFone(telefone);
		String cep = obj.getCEP().substring(0, 5) + obj.getCEP().substring(6, 9);
		obj.setCEP(cep);
	}

	@Override
	public void update(Employee obj) {
		unformat(obj);
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE funcionario SET nome = ? , cpf = ? , cep = ? , telefone = ? , data_nascimento = ?, inicio_contrato = ?, fim_contrato = ? , salario = ?, departamentoId = ? WHERE id = ?");
			st.setString(1, obj.getName());
			st.setString(2, obj.getCPF());
			st.setString(3, obj.getCEP());
			st.setString(4, obj.getFone());
			Date x = obj.getBirthDate().getTime();
			st.setDate(5, new java.sql.Date(x.getTime()));
			x = obj.getHiringDate().getTime();
			st.setDate(6, new java.sql.Date(x.getTime()));
			if (obj.getResignationDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date data = sdf.parse(obj.getResignationDate());
				st.setDate(7, new java.sql.Date(data.getTime()));
			} else {
				st.setNull(7, Types.DATE);
			}
			st.setDouble(8, obj.getSalary());
			st.setInt(9, obj.getDepartment().getId());
			st.setInt(10, obj.getId());
			st.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM funcionario Where id = ? ");
			st.setInt(1, id);
			st.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Employee findById(Integer id) {
//		PreparedStatement st = null;
//		ResultSet rs = null;
//		try {
//			st = conn.prepareStatement("SELECT * from funcionario where id = ? ");
//			st.setInt(1, id);
//			rs = st.executeQuery();
//			if (rs.next()) {
//				Departamento dep = instanciarDepartamento(rs);
//				Funcionario obj = instanciarFuncionario(rs,dep);
//				return obj;
//			}
//			return null;
//		} catch (Exception e) {
//			throw new DbException(e.getMessage());
//		} finally {
//			DB.closeResultSet(rs);
//			DB.closeStatement(st);
//		}
		return null;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("departamentoId"));
		dep.setName(rs.getString("depNome"));
		return dep;
	}

	private Employee instantiateEmployee(ResultSet rs, Department dep) throws SQLException {
		Employee cliente = new Employee();
		cliente.setId(rs.getInt("id"));
		cliente.setName(rs.getString("nome"));
		cliente.setCPF(rs.getString("cpf"));
		cliente.setCEP(rs.getString("cep"));
		cliente.setFone(rs.getString("telefone"));
		cliente.setSalary(rs.getDouble("salario"));
		cliente.setDepartment(dep);
		Calendar x = Calendar.getInstance();
		Date z = rs.getDate(5);
		x.setTimeInMillis(z.getTime());
		cliente.setBirthDate(x);
		Date y = rs.getDate(6);
		Calendar o = Calendar.getInstance();
		o.setTimeInMillis(y.getTime());
		cliente.setHiringDate(o);
		Date a = (Date) rs.getObject("fim_contrato");
		if (a != null) {
			Calendar u = Calendar.getInstance();
			u.setTimeInMillis(a.getTime());
			cliente.setResignationDate(u);
		} else {
			cliente.setResignationDate(null);
		}
		return cliente;
	}

	@Override
	public List<Employee> findAll() {
		PreparedStatement st = null;
		List<Employee> sellers = new ArrayList<Employee>();
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT funcionario.*,departamento.nome as depNome"
					+ " FROM funcionario INNER JOIN " + "departamento ON funcionario.departamentoId = "
					+ "departamento.id  ORDER BY funcionario.nome");
			rs = st.executeQuery();
			Map<Integer, Department> map = new HashMap<>();
			while (rs.next()) {
				Department dep = map.get(rs.getInt("id"));
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("id"), dep);
				}
				Employee seller = instantiateEmployee(rs, dep);
				sellers.add(seller);
			}
			return sellers;
		} catch (SQLException e) {
			Alerts.showAlert("Errorss", null, e.getMessage(), AlertType.ERROR);
			throw new DbException(e.getMessage());

		} catch (DbException e) {
			Alerts.showAlert("Errorss", null, e.getMessage(), AlertType.ERROR);
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}

	}

	@Override
	public List<Employee> findByDepartment(Department department) {
		//
		return null;
	}

}
