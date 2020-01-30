package controladores;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import modelo.entidades.Departamento;
import modelo.entidades.Funcionario;
import modelo.excecao.ValidationException;
import modelo.servicos.DepartamentoService;
import modelo.servicos.FuncionarioService;
import telas.observador.Observador;
import telas.util.Alertas;
import telas.util.Limitacoes;
import telas.util.Utils;

public class FormularioFuncionarioControlador implements Initializable {

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtCPF;

	@FXML
	private TextField txtCEP;

	@FXML
	private TextField txtTelefone;

	@FXML
	private TextField txtSalario;

	@FXML
	private DatePicker dpDataNascimento;

	@FXML
	private DatePicker dpInicioContrato;

	@FXML
	private DatePicker dpFimContrato;

	@FXML
	private ComboBox<Departamento> cbDepartamento;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	@FXML
	private Label lblErrorNome;

	@FXML
	private Label lblErrorCPF;

	@FXML
	private Label lblErrorCEP;

	@FXML
	private Label lblErrorDataNascimento;

	@FXML
	private Label lblErrorTelefone;

	@FXML
	private Label lblErrorSalario;

	@FXML
	private Label lblErrorInicioContrato;

	private Funcionario entity;

	private FuncionarioService service;

	private ObservableList<Departamento> obsList;

	private DepartamentoService departmentService;

	private List<Observador> dataChangeListeners = new ArrayList<>();

	public void setFuncionario(Funcionario entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observador listener) {
		dataChangeListeners.add(listener);
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

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void setServices(FuncionarioService service, DepartamentoService service1) {
		this.service = service;
		this.departmentService = service1;
	}

	private void notifyDataChangeListeners() {
		for (Observador listener : dataChangeListeners) {
			listener.atualizarDados();
		}
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Departamento> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		cbDepartamento.setItems(obsList);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}

	private void initializeNodes() {
		Limitacoes.cpfField(txtCPF);
		Limitacoes.cepField(txtCEP);
		Limitacoes.foneField(txtTelefone);
		Limitacoes.setTextFieldTamanhoMaximo(txtNome, 255);
		Limitacoes.setTextFieldReal(txtSalario);
		initializeComboBoxDepartment();
	}

	private Funcionario getFormData() {
		Funcionario cliente = new Funcionario();
		ValidationException exception = new ValidationException("Validation error");
		cliente.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "  Campo vazio");
		}
		cliente.setNome(txtNome.getText());
		if (txtCPF.getText() == null || txtCPF.getText().trim().equals("")) {
			exception.addError("cpf", "  Campo vazio");
		} else if (!Utils.verificarCpf(txtCPF.getText())) {
			exception.removeError("cpf");
			exception.addError("cpfInvalido", "  CPF invalido");
		}
		cliente.setCPF(txtCPF.getText());
		if (dpDataNascimento.getValue() == null) {
			exception.addError("dataNascimento", "  Campo vazio");
		} else {
			Instant instant = Instant.from(dpDataNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			Calendar x = Calendar.getInstance();
			x.setTime(Date.from(instant));
			cliente.setDataNascimento(x);
		}
		if (dpInicioContrato.getValue() == null) {
			exception.addError("inicioContrato", "  Campo vazio");
		} else {
			Instant instant = Instant.from(dpInicioContrato.getValue().atStartOfDay(ZoneId.systemDefault()));
			Calendar x = Calendar.getInstance();
			x.setTime(Date.from(instant));
			cliente.setInicioContrato(x);
		}
		if (dpFimContrato.getValue() != null) {
			Instant instant = Instant.from(dpFimContrato.getValue().atStartOfDay(ZoneId.systemDefault()));
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

		if (txtTelefone.getText() == null || txtTelefone.getText().trim().equals("")) {
			exception.addError("telefone", "  Campo vazio");
		}
		cliente.setTelefone(txtTelefone.getText());
		if (txtSalario.getText() == null || txtSalario.getText().trim().equals("")) {
			exception.addError("salario", "  Campo vazio");
		}
		cliente.setSalario(Utils.tryParseToDouble(txtSalario.getText()));

		cliente.setDepartamento(cbDepartamento.getValue());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return cliente;
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtCPF.setText(entity.getCPF());
		txtSalario.setText(String.format("%.2f", entity.getSalario()));
		Calendar x = entity.getDataNascimento();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		txtCEP.setText(entity.getCEP());
		txtTelefone.setText(entity.getTelefone());
		if (x != null) {
			String s = sdf.format(x.getTime());
			dpDataNascimento.setValue(LOCAL_DATE(String.valueOf(s)));
		}
		x = entity.getInicioContrato();
		if (x != null) {
			String s = sdf.format(x.getTime());
			dpInicioContrato.setValue(LOCAL_DATE(String.valueOf(s)));
		}
		
		if (entity.getFimContrato() != null) {
			dpFimContrato.setValue(LOCAL_DATE(String.valueOf(entity.getFimContrato())));
		}
		if (entity.getDepartamento() == null) {
			cbDepartamento.getSelectionModel().selectFirst();
		} else {
			cbDepartamento.setValue(entity.getDepartamento());
		}
	}

	private final LocalDate LOCAL_DATE(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		lblErrorNome.setText((fields.contains("nome") ? errors.get("nome") : ""));
		lblErrorCPF.setText((fields.contains("cpf") ? errors.get("cpf") : ""));
		lblErrorCPF.setText((fields.contains("cpfInvalido") ? errors.get("cpfInvalido") : ""));
		lblErrorSalario.setText((fields.contains("salario") ? errors.get("salario") : ""));
		lblErrorDataNascimento.setText((fields.contains("dataNascimento") ? errors.get("dataNascimento") : ""));
		lblErrorInicioContrato.setText((fields.contains("inicioContrato") ? errors.get("inicioContrato") : ""));
		lblErrorCEP.setText((fields.contains("cep") ? errors.get("cep") : ""));
		lblErrorTelefone.setText((fields.contains("telefone") ? errors.get("telefone") : ""));
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());

			}
		};

		cbDepartamento.setCellFactory(factory);
		cbDepartamento.setButtonCell(factory.call(null));
	}
}
