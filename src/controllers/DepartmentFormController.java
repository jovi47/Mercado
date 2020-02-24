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
import model.entities.Department;
import model.exception.ValidationException;
import model.services.DepartmentService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import views.observer.Observer;
import views.util.Alerts;
import views.util.Constraints;
import views.util.Utils;

public class DepartmentFormController implements Initializable {
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label lblErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;

	private Department entity;

	private DepartmentService service;

	private List<Observer> observers = new ArrayList<>();

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observer listener) {
		observers.add(listener);
	}

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		initializeNodes();
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
			Alerts.showAlert("Erro salvando objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (Observer listener : observers) {
			listener.updateData();
		}
	}

	private Department getFormData() {
		ValidationException exception = new ValidationException("Erro de validacao");
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "  Campo vazio");
		}
		Department dep = new Department(Utils.tryParseToInt(txtId.getText()), txtName.getText());
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return dep;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 50);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Objeto entidade estava nulo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		if (fields.contains("name")) {
			lblErrorName.setText(errors.get("name"));
		}
	}
}
