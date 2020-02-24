package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.TextFields;

import db.DbException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Product;
import model.entities.Stock;
import model.exception.ValidationException;
import model.services.StockService;
import model.services.ProductService;
import views.observer.Observer;
import views.util.Alerts;
import views.util.Utils;

public class StockFormController implements Initializable {

	private Stock entity;

	private StockService service;
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtProduct;

	@FXML
	private TextField txtQuantity;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	private Label lblErrorProduct;

	@FXML
	private Label lblErrorQuantity;

	private List<Product> list;
	private List<Observer> observers = new ArrayList<>();

	public void setEstoqueService(StockService service) {
		this.service = service;
	}

	public void setEstoque(Stock entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observer listener) {
		observers.add(listener);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}

	private void initializeNodes() {
		ProductService service = new ProductService();
		list = service.findAll();
		TextFields.bindAutoCompletion(txtProduct, list);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Objeto entidade estava nulo");
		}
		if (service == null) {
			throw new IllegalStateException("Servico estava nulo");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro salvando objeto", e.getMessage(), e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (Observer listener : observers) {
			listener.updateData();
		}
	}

	private Stock getFormData() {
		Stock estoque = new Stock();
		estoque.setId(Utils.tryParseToInt(txtId.getText()));
		ValidationException exception = new ValidationException("Validation error");
		if (txtProduct.getText() == null || txtProduct.getText().trim().equals("")) {
			exception.addError("produto", "  Campo vazio");
		}
		if (txtQuantity.getText() == null || txtQuantity.getText().trim().equals("")) {
			exception.addError("quantidade", "  Campo vazio");
		}
		List<Product> y = list.stream().filter(x -> x.getName().equalsIgnoreCase(txtProduct.getText()))
				.collect(Collectors.toList());
		ProductService service = new ProductService();
		if (estoque.getId() != null) {

		} else {
			if (y.size() > 0) {
				estoque.setProduct(y.get(0));
				Product p = service.findById(y.get(0).getId());
				if (p != null) {
					exception.addError("produtoDb", "  Produto ja cadastrado");
				}
			}
		}
		estoque.setQuantity(Utils.tryParseToInt(txtQuantity.getText()));
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return estoque;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText((entity.getId() == null) ? "" : String.valueOf(entity.getId()));
		txtProduct.setText(entity.getProduct().getName());
		txtQuantity.setText((entity.getId() == null) ? "" : String.valueOf(entity.getQuantity()));
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		lblErrorProduct.setText((fields.contains("produto") ? errors.get("produto") : ""));
		lblErrorQuantity.setText((fields.contains("quantidade") ? errors.get("quantidade") : ""));
		lblErrorProduct.setText((fields.contains("produtoDb") ? errors.get("produtoDb") : ""));
	}

}
