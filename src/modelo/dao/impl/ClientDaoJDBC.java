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
import model.entities.Client;
import modelo.dao.ClientDao;

public class ClientDaoJDBC implements ClientDao {

	private Connection conn;

	public ClientDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Client obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO cliente (nome, cpf, cep , telefone, data_nascimento ) VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			unformat(obj);
			st.setString(1, obj.getName());
			st.setString(2, obj.getCPF());
			Date x = obj.getBirthDate().getTime();
			st.setDate(5, new java.sql.Date(x.getTime()));
			st.setString(3, obj.getCEP());
			st.setString(4, obj.getFone());
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

	private void unformat(Client obj) {
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
	public void update(Client obj) {

		unformat(obj);
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE cliente SET nome = ? , cpf = ? , cep = ? , telefone = ? , data_nascimento = ? WHERE id = ?");
			st.setString(1, obj.getName());
			st.setString(2, obj.getCPF());

			Date x = obj.getBirthDate().getTime();
			st.setDate(5, new java.sql.Date(x.getTime()));
			st.setString(3, obj.getCEP());
			st.setString(4, obj.getFone());
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
	public Client findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * from cliente where id = ? ");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Client obj = instantiateClient(rs);
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

	private Client instantiateClient(ResultSet rs) throws SQLException {
		Client cliente = new Client();
		cliente.setId(rs.getInt("id"));
		cliente.setName(rs.getString("nome"));
		cliente.setCPF(rs.getString("cpf"));
		cliente.setCEP(rs.getString("cep"));
		cliente.setFone(rs.getString("telefone"));
		Calendar x = Calendar.getInstance();
		x.setTimeInMillis(new java.util.Date(rs.getTimestamp("data_nascimento").getTime()).getTime());
		cliente.setDataNascimento(x);
		return cliente;
	}

	@Override
	public List<Client> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM cliente");
			rs = st.executeQuery();
			List<Client> list = new ArrayList<>();
			while (rs.next()) {
				Client obj = (instantiateClient(rs));
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
