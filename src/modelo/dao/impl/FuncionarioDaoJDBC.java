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
import modelo.dao.FuncionarioDao;
import modelo.entidades.Departamento;
import modelo.entidades.Funcionario;
import telas.util.Alertas;

public class FuncionarioDaoJDBC implements FuncionarioDao {

	private Connection conn;

	public FuncionarioDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Funcionario obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO funcionario (nome, cpf, cep , telefone, data_nascimento, "
					+ "inicio_contrato, salario, departamentoId, fim_contrato) " + ""
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
			if (obj.getFimContrato() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date data = sdf.parse(obj.getFimContrato());
				st.setDate(9, new java.sql.Date(data.getTime()));
			} else {
				st.setNull(9, Types.DATE);
			}
			st.setString(1, obj.getNome());
			st.setString(2, obj.getCPF());
			Date y = obj.getDataNascimento().getTime();
			st.setDate(5, new java.sql.Date(y.getTime()));
			st.setString(3, obj.getCEP());
			st.setString(4, obj.getTelefone());
			Date x = obj.getInicioContrato().getTime();
			st.setDate(6, new java.sql.Date(x.getTime()));
			st.setDouble(7, obj.getSalario());
			st.setInt(8, obj.getDepartamento().getId());
			tirarFormatacao(obj);
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

	private void tirarFormatacao(Funcionario obj) {
		String cpf = obj.getCPF().substring(0, 3) + obj.getCPF().substring(4, 7) + obj.getCPF().substring(8, 11)
				+ obj.getCPF().substring(12, 14);
		obj.setCPF(cpf);
		String telefone = obj.getTelefone().substring(1, 3) + obj.getTelefone().substring(4, 9)
				+ obj.getTelefone().substring(10, 14);
		obj.setTelefone(telefone);
		String cep = obj.getCEP().substring(0, 5) + obj.getCEP().substring(6, 9);
		obj.setCEP(cep);
	}

	@Override
	public void update(Funcionario obj) {
		tirarFormatacao(obj);
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE funcionario SET nome = ? , cpf = ? , cep = ? , telefone = ? , data_nascimento = ?, inicio_contrato = ?, fim_contrato = ? , salario = ?, departamentoId = ? WHERE id = ?");
			st.setString(1, obj.getNome());
			st.setString(2, obj.getCPF());
			st.setString(3, obj.getCEP());
			st.setString(4, obj.getTelefone());
			Date x = obj.getDataNascimento().getTime();
			st.setDate(5, new java.sql.Date(x.getTime()));
			x = obj.getInicioContrato().getTime();
			st.setDate(6, new java.sql.Date(x.getTime()));
			if (obj.getFimContrato() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date data = sdf.parse(obj.getFimContrato());
				st.setDate(7, new java.sql.Date(data.getTime()));
			} else {
				st.setNull(7, Types.DATE);
			}
			st.setDouble(8, obj.getSalario());
			st.setInt(9, obj.getDepartamento().getId());
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
	public Funcionario findById(Integer id) {
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

	private Departamento instanciarDepartamento(ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();
		dep.setId(rs.getInt("departamentoId"));
		dep.setName(rs.getString("depNome"));
		return dep;
	}

	private Funcionario instanciarFuncionario(ResultSet rs, Departamento dep) throws SQLException {
		Funcionario cliente = new Funcionario();
		cliente.setId(rs.getInt("id"));
		cliente.setNome(rs.getString("nome"));
		cliente.setCPF(rs.getString("cpf"));
		cliente.setCEP(rs.getString("cep"));
		cliente.setTelefone(rs.getString("telefone"));
		cliente.setSalario(rs.getDouble("salario"));
		cliente.setDepartamento(dep);
		Calendar x = Calendar.getInstance();
		Date z = rs.getDate(5);
		x.setTimeInMillis(z.getTime());
		cliente.setDataNascimento(x);
		Date y = rs.getDate(6);
		Calendar o = Calendar.getInstance();
		o.setTimeInMillis(y.getTime());
		cliente.setInicioContrato(o);
		Date a = (Date) rs.getObject("fim_contrato");
		if (a != null) {
			Calendar u = Calendar.getInstance();
			u.setTimeInMillis(a.getTime());
			cliente.setFimContrato(u);
		} else {
			cliente.setFimContrato(null);
		}
		return cliente;
	}

	@Override
	public List<Funcionario> findAll() {
		PreparedStatement st = null;
		List<Funcionario> sellers = new ArrayList<Funcionario>();
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT funcionario.*,departamento.nome as depNome"
					+ " FROM funcionario INNER JOIN " + "departamento ON funcionario.departamentoId = "
					+ "departamento.id  ORDER BY funcionario.nome");
			rs = st.executeQuery();
			Map<Integer, Departamento> map = new HashMap<>();
			while (rs.next()) {
				Departamento dep = map.get(rs.getInt("id"));
				if (dep == null) {
					dep = instanciarDepartamento(rs);
					map.put(rs.getInt("id"), dep);
				}
				Funcionario seller = instanciarFuncionario(rs, dep);
				sellers.add(seller);
			}
			return sellers;
		} catch (SQLException e) {
			Alertas.mostrarAlerta("Errorss", null, e.getMessage(), AlertType.ERROR);
			throw new DbException(e.getMessage());

		} catch (DbException e) {
			Alertas.mostrarAlerta("Errorss", null, e.getMessage(), AlertType.ERROR);
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}

	}

	@Override
	public List<Funcionario> findByDepartment(Departamento department) {
		//
		return null;
	}

}
