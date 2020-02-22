package model.entities;

import java.io.Serializable;

public class Stock extends Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Product produto;
	private Integer quantidade;

	public Product getProduto() {
		return produto;
	}

	public void setProduto(Product produto) {
		this.produto = produto;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

}
