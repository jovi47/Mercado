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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.entidades.Cliente;
import modelo.excecao.ValidationException;
import modelo.servicos.ClienteService;
import telas.observador.Observador;
import telas.util.Alertas;
import telas.util.Limitacoes;
import telas.util.Utils;

public class FormularioClientesControlador implements Initializable {

	public TextField getTxtId() {
		return txtId;
	}

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
	private DatePicker dpDataNascimento;

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

	private Cliente entity;

	private ClienteService service;

	private List<Observador> dataChangeListeners = new ArrayList<>();

	public void setClienteService(ClienteService service) {
		this.service = service;
	}

	public void setCliente(Cliente entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observador listener) {
		dataChangeListeners.add(listener);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializerNodes();
	}

	private void initializerNodes() {
		Limitacoes.cpfField(txtCPF);
		Limitacoes.cepField(txtCEP);
		Limitacoes.foneField(txtTelefone);
		Limitacoes.setTextFieldTamanhoMaximo(txtNome, 255);
	}

	private void notifyDataChangeListeners() {
		for (Observador listener : dataChangeListeners) {
			listener.atualizarDados();
		}
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

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		lblErrorTelefone.setText((fields.contains("telefone") ? errors.get("telefone") : ""));
		lblErrorNome.setText((fields.contains("nome") ? errors.get("nome") : ""));
		lblErrorCPF.setText((fields.contains("cpf") ? errors.get("cpf") : ""));
		lblErrorCPF.setText((fields.contains("cpfInvalido") ? errors.get("cpfInvalido") : ""));
		lblErrorCEP.setText((fields.contains("cep") ? errors.get("cep") : ""));
		lblErrorDataNascimento.setText((fields.contains("dataNascimento") ? errors.get("dataNascimento") : ""));
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private Cliente getFormData() {
		Cliente cliente = new Cliente();
		ValidationException exception = new ValidationException("Validation error");
		cliente.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "  Campo vazio");
		}
		cliente.setNome(txtNome.getText());
		if (txtCPF.getText() == null || txtCPF.getText().trim().equals("")) {
			exception.addError("cpf", "  Campo vazio");
		} else if (!Utils.verificarCpf(txtCPF.getText())) {
			exception.addError("cpfInvalido", "  CPF invalido");
			exception.removeError("cpf");
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
		if (txtCEP.getText() == null || txtCEP.getText().trim().equals("")) {
			exception.addError("cep", "  Campo vazio");
		}
		cliente.setCEP(txtCEP.getText());

		if (txtTelefone.getText() == null || txtTelefone.getText().trim().equals("")) {
			exception.addError("telefone", "  Campo vazio");
		}
		cliente.setTelefone(txtTelefone.getText());
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
		Locale.setDefault(Locale.US);
		txtCEP.setText(entity.getCEP());
		Calendar x = entity.getDataNascimento();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (x != null) {
			String s = sdf.format(x.getTime());
			dpDataNascimento.setValue(LOCAL_DATE(String.valueOf(s)));
		}
		txtTelefone.setText(entity.getTelefone());
	}

	private final LocalDate LOCAL_DATE(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}
}
