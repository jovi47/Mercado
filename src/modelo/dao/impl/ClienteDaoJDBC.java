package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import db.DB;
import db.DbException;
import modelo.dao.ClienteDao;
import modelo.entidades.Cliente;

public class ClienteDaoJDBC implements ClienteDao {

	private Connection conn;

	public ClienteDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Cliente obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO cliente (nome, cpf, cep , telefone, data_nascimento ) VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			tirarFormatacao(obj);
			st.setString(1, obj.getNome());
			st.setString(2, obj.getCPF());
			Date x = obj.getDataNascimento().getTime();
			st.setDate(5, new java.sql.Date(x.getTime()));
			st.setString(3, obj.getCEP());
			st.setString(4, obj.getTelefone());
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
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	private void tirarFormatacao(Cliente obj) {
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
	public void update(Cliente obj) {
		tirarFormatacao(obj);
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE cliente SET nome = ? , cpf = ? , cep = ? , telefone = ? , data_nascimento = ? WHERE id = ?");
			st.setString(1, obj.getNome());
			st.setString(2, obj.getCPF());

			Date x = obj.getDataNascimento().getTime();
			st.setDate(5, new java.sql.Date(x.getTime()));
			st.setString(3, obj.getCEP());
			st.setString(4, obj.getTelefone());
			st.setInt(6, obj.getId());
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
			st = conn.prepareStatement("DELETE FROM cliente Where id = ? ");
			st.setInt(1, id);
			st.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Cliente findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * from cliente where id = ? ");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Cliente obj = instanciarCliente(rs);
				return obj;
			}
			return null;
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	private Cliente instanciarCliente(ResultSet rs) throws SQLException {
		Cliente cliente = new Cliente();
		cliente.setId(rs.getInt("id"));
		cliente.setNome(rs.getString("nome"));
		cliente.setCPF(rs.getString("cpf"));
		cliente.setCEP(rs.getString("cep"));
		cliente.setTelefone(rs.getString("telefone"));
		Calendar x = Calendar.getInstance();
		x.setTimeInMillis(new java.util.Date(rs.getTimestamp("data_nascimento").getTime()).getTime());
		cliente.setDataNascimento(x);
		return cliente;
	}

	@Override
	public List<Cliente> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM cliente");
			rs = st.executeQuery();
			List<Cliente> list = new ArrayList<>();
			while (rs.next()) {
				Cliente obj = (instanciarCliente(rs));
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
