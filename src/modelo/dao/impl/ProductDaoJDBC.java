package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.entities.Product;
import modelo.dao.ProductDao;

public class ProductDaoJDBC implements ProductDao{

	private Connection conn;

	public ProductDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public void insert(Product obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO produto (nome, descricao, preco) " + ""
					+ "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getNome());
			st.setString(2, obj.getDescricao());
			st.setDouble(3, obj.getPreco());
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

	@Override
	public void update(Product obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE produto SET nome = ?, descricao = ?, preco = ?  WHERE id = ? ");
			st.setString(1, obj.getNome());
			st.setString(2,obj.getDescricao());
			st.setDouble(3, obj.getPreco());
			st.setInt(4, obj.getId());
			st.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
	
		
	}

	@Override
	public Product findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * from produto where id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Product obj = new Product();
				obj.setId(rs.getInt("id"));
				obj.setNome(rs.getString("nome"));
				obj.setDescricao(rs.getString("descricao"));
				obj.setPreco(rs.getDouble("preco"));
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

	@Override
	public List<Product> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT * FROM produto ORDER BY nome");
			rs = st.executeQuery();

			List<Product> list = new ArrayList<>();

			while (rs.next()) {
				Product obj = new Product();
				obj.setId(rs.getInt("id"));
				obj.setNome(rs.getString("nome"));
				obj.setDescricao(rs.getString("descricao"));
				obj.setPreco(rs.getDouble("preco"));
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
