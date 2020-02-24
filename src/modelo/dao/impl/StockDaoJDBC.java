package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import javafx.scene.control.Alert.AlertType;
import model.entities.Product;
import model.entities.Stock;
import modelo.dao.StockDao;
import views.util.Alerts;

public class StockDaoJDBC implements StockDao {

	private Connection conn;

	public StockDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Stock obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO estoque (id_produto, quantidade ) VALUES (?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, obj.getProduct().getId());
			st.setInt(2, obj.getQuantity());
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

	@Override
	public void update(Stock obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE estoque SET  quantidade = ? WHERE id = ? ");
		
			st.setInt(1, obj.getQuantity());
			st.setInt(2, obj.getId());
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
			st = conn.prepareStatement("DELETE FROM estoque Where id = ? ");
			st.setInt(1, id);
			st.executeUpdate();
		} catch (Exception e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Stock findById(Integer id) {
		return null;
	}

	private Product instantiateProduct(ResultSet rs) throws SQLException {
		Product obj = new Product();
		obj.setId(rs.getInt("id_produto"));
		obj.setName(rs.getString("produtoNome"));
		obj.setDescription(rs.getString("pDescricao"));
		obj.setPrice(rs.getDouble("pPreco"));
		return obj;

	}

	private Stock instantiateStock(ResultSet rs, Product pro) throws SQLException {
		Stock est = new Stock();
		est.setId(rs.getInt("id"));
		est.setProduct(pro);
		est.setQuantity(rs.getInt("quantidade"));
		return est;
	}

	@Override
	public List<Stock> findAll() {
		PreparedStatement st = null;
		List<Stock> estoque = new ArrayList<>();
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT estoque.*,produto.nome as produtoNome, produto.descricao as pDescricao, produto.preco as pPreco "
							+ " FROM estoque INNER JOIN " + "produto ON estoque.id_produto = "
							+ "produto.id  ORDER BY estoque.id");
			rs = st.executeQuery();
			Map<Integer, Product> map = new HashMap<>();
			while (rs.next()) {
				Product pro = map.get(rs.getInt("id_produto"));
				if (pro == null) {
					pro = instantiateProduct(rs);
					map.put(rs.getInt("id"), pro);
				}
				Stock estoq =  instantiateStock(rs, pro);
				estoque.add(estoq);
			}
			return estoque;
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

}
