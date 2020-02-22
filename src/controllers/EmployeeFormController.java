package controllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Employee;
import model.exception.ValidationException;
import model.services.DepartmentService;
import model.services.EmployeeService;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import views.observer.Observer;
import views.util.Alerts;
import views.util.Constraints;
import views.util.Utils;

public class EmployeeFormController implements Initializable {

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtCPF;

	@FXML
	private TextField txtCEP;

	@FXML
	private TextField txtFone;

	@FXML
	private TextField txtSalary;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private DatePicker dpHiringDate;

	@FXML
	private DatePicker dpResignationDate;

	@FXML
	private ComboBox<Department> cbDepartment;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	private Label lblErrorName;

	@FXML
	private Label lblErrorCPF;

	@FXML
	private Label lblErrorCEP;

	@FXML
	private Label lblErrorBirthDate;

	@FXML
	private Label lblErrorFone;

	@FXML
	private Label lblErrorSalary;

	@FXML
	private Label lblErrorHiringDate;

	private Employee entity;

	private EmployeeService service;

	private ObservableList<Department> obsList;

	private DepartmentService departmentService;

	private List<Observer> observers = new ArrayList<>();

	public void setFuncionario(Employee entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observer listener) {
		observers.add(listener);
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

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void setServices(EmployeeService service, DepartmentService service1) {
		this.service = service;
		this.departmentService = service1;
	}

	private void notifyDataChangeListeners() {
		for (Observer listener :observers) {
			listener.updateData();
		}
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("Servico estava nulo");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		cbDepartment.setItems(obsList);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.cpfField(txtCPF);
		Constraints.cepField(txtCEP);
		Constraints.foneField(txtFone);
		Constraints.setTextFieldMaxLength(txtName, 255);
		Constraints.setTextFieldDouble(txtSalary);
		initializeComboBoxDepartment(); 
	}

	private Employee getFormData() {
		Employee cliente = new Employee();
		ValidationException exception = new ValidationException("Validation error");
		cliente.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("nome", "  Campo vazio");
		}
		cliente.setNome(txtName.getText());
		if (txtCPF.getText() == null || txtCPF.getText().trim().equals("")) {
			exception.addError("cpf", "  Campo vazio");
		} else if (!Utils.verificarCpf(txtCPF.getText())) {
			exception.removeError("cpf");
			exception.addError("cpfInvalido", "  CPF invalido");
		}
		cliente.setCPF(txtCPF.getText());
		if (dpBirthDate.getValue() == null) {
			exception.addError("dataNascimento", "  Campo vazio");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			Calendar x = Calendar.getInstance();
			x.setTime(Date.from(instant));
			cliente.setDataNascimento(x);
		}
		if (dpHiringDate.getValue() == null) {
			exception.addError("inicioContrato", "  Campo vazio");
		} else {
			Instant instant = Instant.from(dpHiringDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			Calendar x = Calendar.getInstance();
			x.setTime(Date.from(instant));
			cliente.setInicioContrato(x);
		}
		if (dpResignationDate.getValue() != null) {
			Instant instant = Instant.from(dpResignationDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			Calendar x = Calendar.getInstance();
			x.setTime(Date.from(instant));
			cliente.setFimContrato(x);
		}else {
			cliente.setFimContrato(null);
		}
		if (txtCEP.getText() == null || txtCEP.getText().trim().equals("")) {
			exception.addError("cep", "  Campo vazio");
		}
		cliente.setCEP(txtCEP.getText());

		if (txtFone.getText() == null || txtFone.getText().trim().equals("")) {
			exception.addError("telefone", "  Campo vazio");
		}
		cliente.setTelefone(txtFone.getText());
		if (txtSalary.getText() == null || txtSalary.getText().trim().equals("")) {
			exception.addError("salario", "  Campo vazio");
		}
		cliente.setSalario(Utils.tryParseToDouble(txtSalary.getText()));

		cliente.setDepartamento(cbDepartment.getValue());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return cliente;
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Objeto entidade estava nulo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getNome());
		txtCPF.setText(entity.getCPF());
		txtSalary.setText(String.format("%.2f", entity.getSalario()));
		Calendar x = entity.getDataNascimento();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		txtCEP.setText(entity.getCEP());
		txtFone.setText(entity.getTelefone());
		if (x != null) {
			String s = sdf.format(x.getTime());
			dpBirthDate.setValue(LOCAL_DATE(String.valueOf(s)));
		}
		x = entity.getInicioContrato();
		if (x != null) {
			String s = sdf.format(x.getTime());
			dpHiringDate.setValue(LOCAL_DATE(String.valueOf(s)));
		}
		
		if (entity.getFimContrato() != null) {
			dpResignationDate.setValue(LOCAL_DATE(String.valueOf(entity.getFimContrato())));
		}
		if (entity.getDepartamento() == null) {
			cbDepartment.getSelectionModel().selectFirst();
		} else {
			cbDepartment.setValue(entity.getDepartamento());
		}
	}

	private final LocalDate LOCAL_DATE(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		lblErrorName.setText((fields.contains("nome") ? errors.get("nome") : ""));
		lblErrorCPF.setText((fields.contains("cpf") ? errors.get("cpf") : ""));
		lblErrorCPF.setText((fields.contains("cpfInvalido") ? errors.get("cpfInvalido") : ""));
		lblErrorSalary.setText((fields.contains("salario") ? errors.get("salario") : ""));
		lblErrorBirthDate.setText((fields.contains("dataNascimento") ? errors.get("dataNascimento") : ""));
		lblErrorHiringDate.setText((fields.contains("inicioContrato") ? errors.get("inicioContrato") : ""));
		lblErrorCEP.setText((fields.contains("cep") ? errors.get("cep") : ""));
		lblErrorFone.setText((fields.contains("telefone") ? errors.get("telefone") : ""));
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());

			}
		};

		cbDepartment.setCellFactory(factory);
		cbDepartment.setButtonCell(factory.call(null));
	}
}
