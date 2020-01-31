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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import modelo.entidades.Departamento;
import modelo.entidades.Produto;
import modelo.excecao.ValidationException;
import modelo.servicos.DepartamentoService;
import modelo.servicos.ProdutoService;
import telas.observador.Observador;
import telas.util.Alertas;
import telas.util.Limitacoes;
import telas.util.Utils;

public class FormularioProdutoControlador implements Initializable {

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtDescricao;

	@FXML
	private TextField txtPreco;

	@FXML
	private Label lblErrorNome;

	@FXML
	private Label lblErrorDescricao;

	@FXML
	private Label lblErrorPreco;

	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;

	private Produto entity;

	private ProdutoService service;

	private List<Observador> dataChangeListeners = new ArrayList<>();

	public void setProdutoService(ProdutoService service) {
		this.service = service;
	}

	public void setProduto(Produto entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observador listener) {
		dataChangeListeners.add(listener);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
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

	private Produto getFormData() {
		Produto produto = new Produto();
		ValidationException exception = new ValidationException("Validation error");
		produto.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Field can't be empty");
		}
		produto.setNome(txtNome.getText());
		if (txtDescricao.getText() == null || txtDescricao.getText().trim().equals("")) {
			exception.addError("descricao", "Field can't be empty");
		}
		produto.setDescricao(txtDescricao.getText());
		if (txtPreco.getText() == null || txtPreco.getText().trim().equals("")) {
			exception.addError("preco", "Field can't be empty");
		}
		produto.setPreco(Utils.tryParseToDouble(txtPreco.getText()));

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
		Limitacoes.setTextFieldInteiro(txtId);
		Limitacoes.setTextFieldTamanhoMaximo(txtNome, 50);
		Limitacoes.setTextFieldReal(txtPreco);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtDescricao.setText(entity.getDescricao());
		txtPreco.setText(String.valueOf(entity.getPreco()));
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		lblErrorNome.setText((fields.contains("nome") ? errors.get("nome") : ""));
		lblErrorDescricao.setText((fields.contains("descricao") ? errors.get("descricao") : ""));
		lblErrorPreco.setText((fields.contains("preco") ? errors.get("preco") : ""));
	}

}
