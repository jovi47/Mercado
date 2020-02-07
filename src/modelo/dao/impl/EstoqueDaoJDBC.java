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
import modelo.dao.EstoqueDao;
import modelo.entidades.Estoque;
import modelo.entidades.Produto;
import telas.util.Alertas;

public class EstoqueDaoJDBC implements EstoqueDao {

	private Connection conn;

	public EstoqueDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Estoque obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO estoque (id_produto, quantidade ) VALUES (?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, obj.getProduto().getId());
			st.setInt(2, obj.getQuantidade());
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
	public void update(Estoque obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE estoque SET id_produto = ?, quantidade = ? WHERE id = ? ");
			st.setInt(1, obj.getProduto().getId());
			st.setInt(2, obj.getQuantidade());
			st.setInt(3, obj.getId());
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
	public Estoque findById(Integer id) {
		return null;
	}

	private Produto instanciarProduto(ResultSet rs) throws SQLException {
		Produto obj = new Produto();
		obj.setId(rs.getInt("id_produto"));
		obj.setNome(rs.getString("produtoNome"));
		obj.setDescricao(rs.getString("pDescricao"));
		obj.setPreco(rs.getDouble("pPreco"));
		return obj;

	}

	private Estoque instanciarEstoque(ResultSet rs, Produto pro) throws SQLException {
		Estoque est = new Estoque();
		est.setId(rs.getInt("id"));
		est.setProduto(pro);
		est.setQuantidade(rs.getInt("quantidade"));
		return est;
	}

	@Override
	public List<Estoque> findAll() {
		PreparedStatement st = null;
		List<Estoque> estoque = new ArrayList<>();
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT estoque.*,produto.nome as produtoNome, produto.descricao as pDescricao, produto.preco as pPreco "
							+ " FROM estoque INNER JOIN " + "produto ON estoque.id_produto = "
							+ "produto.id  ORDER BY estoque.id");
			rs = st.executeQuery();
			Map<Integer, Produto> map = new HashMap<>();
			while (rs.next()) {
				Produto pro = map.get(rs.getInt("id_produto"));
				if (pro == null) {
					pro = instanciarProduto(rs);
					map.put(rs.getInt("id"), pro);
				}
				Estoque estoq = instanciarEstoque(rs, pro);
				estoque.add(estoq);
			}
			return estoque;
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

}
