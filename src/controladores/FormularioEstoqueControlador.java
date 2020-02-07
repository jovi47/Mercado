package controladores;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
import modelo.entidades.Estoque;
import modelo.entidades.Produto;
import modelo.excecao.ValidationException;
import modelo.servicos.EstoqueService;
import modelo.servicos.ProdutoService;
import telas.observador.Observador;
import telas.util.Alertas;
import telas.util.Utils;

public class FormularioEstoqueControlador implements Initializable {

	private Estoque entity;

	private EstoqueService service;
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtProduto;

	@FXML
	private TextField txtQuantidade;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	@FXML
	private Label lblErrorProduto;

	@FXML
	private Label lblErrorQuantidade;

	private List<Produto> list;
	private List<Observador> dataChangeListeners = new ArrayList<>();

	public void setEstoqueService(EstoqueService service) {
		this.service = service;
	}

	public void setEstoque(Estoque entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(Observador listener) {
		dataChangeListeners.add(listener);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}

	private void initializeNodes() {
		ProdutoService service = new ProdutoService();
		list = service.findAll();
		TextFields.bindAutoCompletion(txtProduto, list);
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
			Alertas.mostrarAlerta("Error saving object", e.getMessage(), e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (Observador listener : dataChangeListeners) {
			listener.atualizarDados();
		}
	}

	private Estoque getFormData() {
		Estoque estoque = new Estoque();
		estoque.setId(Utils.tryParseToInt(txtId.getText()));
		ValidationException exception = new ValidationException("Validation error");
		if (txtProduto.getText() == null || txtProduto.getText().trim().equals("")) {
			exception.addError("produto", "Field can't be empty");
		}
		if (txtQuantidade.getText() == null || txtQuantidade.getText().trim().equals("")) {
			exception.addError("quantidade", "Field can't be empty");
		}
		List<Produto> y = list.stream().filter(x -> x.getNome().equalsIgnoreCase(txtProduto.getText()))
				.collect(Collectors.toList());
		ProdutoService service = new ProdutoService();
		if(y.size()>0) {
			estoque.setProduto(y.get(0));			
			Produto p = service.findById(y.get(0).getId());
			if(p!=null) {
				exception.addError("produtoDb", "Produto ja cadastrado");
			}
		}
		estoque.setQuantidade(Utils.tryParseToInt(txtQuantidade.getText()));
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
		txtProduto.setText(entity.getProduto().getNome());
		txtQuantidade.setText((entity.getId() == null) ? "" : String.valueOf(entity.getQuantidade()));
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		lblErrorProduto.setText((fields.contains("produto") ? errors.get("produto") : ""));
		lblErrorQuantidade.setText((fields.contains("quantidade") ? errors.get("quantidade") : ""));
		lblErrorProduto.setText((fields.contains("produtoDb") ? errors.get("produtoDb") : ""));
	}

}
