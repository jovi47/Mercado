package model.entities;

import java.io.Serializable;

public class Stock extends Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Product product;
	private Integer quantity;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
