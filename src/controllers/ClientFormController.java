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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import model.entities.Client;
import model.exception.ValidationException;
import model.services.ClientService;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import views.observer.Observer;
import views.util.Alerts;
import views.util.Constraints;
import views.util.Utils;

public class ClientFormController implements Initializable {

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
	private DatePicker dpBirthDate;

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

	private Client entity;

	private ClientService service;

	private List<Observer> observers = new ArrayList<>();

	public void setClienteService(ClientService service) {
		this.service = service;
	}

	public void setCliente(Client entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observer listener) {
		observers.add(listener);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializerNodes();
	}

	private void initializerNodes() {
		Constraints.cpfField(txtCPF);
		Constraints.cepField(txtCEP);
		Constraints.foneField(txtFone);
		Constraints.setTextFieldMaxLength(txtName, 255);
	}

	private void notifyDataChangeListeners() {
		for (Observer listener : observers) {
			listener.updateData();
		}
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

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		lblErrorFone.setText((fields.contains("telefone") ? errors.get("telefone") : ""));
		lblErrorName.setText((fields.contains("nome") ? errors.get("nome") : ""));
		lblErrorCPF.setText((fields.contains("cpf") ? errors.get("cpf") : ""));
		lblErrorCPF.setText((fields.contains("cpfInvalido") ? errors.get("cpfInvalido") : ""));
		lblErrorCEP.setText((fields.contains("cep") ? errors.get("cep") : ""));
		lblErrorBirthDate.setText((fields.contains("dataNascimento") ? errors.get("dataNascimento") : ""));
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private Client getFormData() {
		Client cliente = new Client();
		ValidationException exception = new ValidationException("Erro de validacao");
		cliente.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("nome", "  Campo vazio");
		}
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
		if (txtCEP.getText() == null || txtCEP.getText().trim().equals("")) {
			exception.addError("cep", "  Campo vazio");
		}
		cliente.setCEP(txtCEP.getText());

		if (txtFone.getText() == null || txtFone.getText().trim().equals("")) {
			exception.addError("telefone", "  Campo vazio");
		}
		cliente.setTelefone(txtFone.getText());
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
		Locale.setDefault(Locale.US);
		txtCEP.setText(entity.getCEP());
		Calendar x = entity.getDataNascimento();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (x != null) {
			String s = sdf.format(x.getTime());
			dpBirthDate.setValue(LOCAL_DATE(String.valueOf(s)));
		}
		txtFone.setText(entity.getTelefone());
	}

	private final LocalDate LOCAL_DATE(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}
}
