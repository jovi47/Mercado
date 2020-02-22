package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Product;
import model.exception.ValidationException;
import model.services.ProductService;
import views.observer.Observer;
import views.util.Alerts;
import views.util.Constraints;
import views.util.Utils;

public class ProductFormController implements Initializable {

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtDescription;

	@FXML
	private TextField txtPrice;

	@FXML
	private Label lblErrorName;

	@FXML
	private Label lblErrorDescription;

	@FXML
	private Label lblErrorPrice;

	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;

	private Product entity;

	private ProductService service;

	private List<Observer> observers = new ArrayList<>();

	public void setProdutoService(ProductService service) {
		this.service = service;
	}

	public void setProduto(Product entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observer listener) {
		observers.add(listener);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();

	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entidade estava nulo");
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
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (Observer listener : observers) {
			listener.updateData();
		}
	}

	private Product getFormData() {
		Product produto = new Product();
		ValidationException exception = new ValidationException("Validation error");
		produto.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("nome", " Campo vazio");
		}
		produto.setNome(txtName.getText());
		if (txtDescription.getText() == null || txtDescription.getText().trim().equals("")) {
			exception.addError("descricao", "Campo vazio");
		}
		produto.setDescricao(txtDescription.getText());
		if (txtPrice.getText() == null || txtPrice.getText().trim().equals("")) {
			exception.addError("preco", "Campo vazio");
		}
		produto.setPreco(Utils.tryParseToDouble(txtPrice.getText()));
		System.out.println(exception.getErrors().size());
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return produto;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 50);
		Constraints.setTextFieldDouble(txtPrice);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Objeto entidade estava nulo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getNome());
		txtDescription.setText(entity.getDescricao());
		txtPrice.setText(String.valueOf(entity.getPreco()));
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		lblErrorName.setText((fields.contains("nome") ? errors.get("nome") : ""));
		lblErrorDescription.setText((fields.contains("descricao") ? errors.get("descricao") : ""));
		lblErrorPrice.setText((fields.contains("preco") ? errors.get("preco") : ""));
	}

}
