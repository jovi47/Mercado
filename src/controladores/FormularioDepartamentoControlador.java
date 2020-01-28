package controladores;

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
import modelo.entidades.Departamento;
import modelo.excecao.ValidationException;
import modelo.servicos.DepartamentoService;
import telas.observador.Observador;
import telas.util.Alertas;
import telas.util.Limitacoes;
import telas.util.Utils;

public class FormularioDepartamentoControlador implements Initializable {
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

	private Departamento entity;

	private DepartamentoService service;

	private List<Observador> dataChangeListeners = new ArrayList<>();

	public void setDepartmentService(DepartamentoService service) {
		this.service = service;
	}

	public void setDepartment(Departamento entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observador listener) {
		dataChangeListeners.add(listener);
	}

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		initializeNodes();
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (entity == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alertas.mostrarAlerta("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (Observador listener : dataChangeListeners) {
			listener.atualizarDados();
		}
	}

	private Departamento getFormData() {
		ValidationException exception = new ValidationException("Validation error");
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		Departamento dep = new Departamento(Utils.tryParseToInt(txtId.getText()), txtName.getText());
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
		Limitacoes.setTextFieldInteiro(txtId);
		Limitacoes.setTextFieldTamanhoMaximo(txtName, 50);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
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
