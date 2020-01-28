package controladores;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import aplicacao.Programa;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.entidades.Cliente;
import modelo.servicos.ClienteService;
import telas.observador.Observador;
import telas.util.Alertas;
import telas.util.Utils;

public class ListarClientesControlador implements Initializable, Observador {

	private ClienteService servico;
	@FXML
	private TableView<Cliente> tableViewCliente;

	@FXML
	private TableColumn<Cliente, Integer> tableColumnId;

	@FXML
	private TableColumn<Cliente, String> tableColumnNome;

	@FXML
	private TableColumn<Cliente, String> tableColumnCPF;

	@FXML
	private TableColumn<Cliente, String> tableColumnCEP;

	@FXML
	private TableColumn<Cliente, String> tableColumnTelefone;

	@FXML
	private TableColumn<Cliente, Calendar> tableColumnDataNascimento;

	@FXML
	private TableColumn<Cliente, Cliente> tableColumnEditar;

	@FXML
	private TableColumn<Cliente, Cliente> tableColumnRemover;

	@FXML
	private Button btNovo;

	private ObservableList<Cliente> obsList;

	public void setClienteService(ClienteService service) {
		this.servico = service;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}

	public void updateTableView() {
		if (servico == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Cliente> list = servico.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewCliente.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnCPF.setCellValueFactory(new PropertyValueFactory<>("CPF"));
		tableColumnCEP.setCellValueFactory(new PropertyValueFactory<>("CEP"));
		tableColumnTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		tableColumnDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		Utils.formatTableColumnDate(tableColumnDataNascimento, "dd/MM/yyyy");
		Stage stage = (Stage) Programa.getStage().getScene().getWindow();
		tableViewCliente.prefHeightProperty().bind(stage.heightProperty());
	}

	private void initEditButtons() {
		tableColumnEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEditar.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setId("btEditar");
				button.setOnAction(
						event -> createDialogForm(obj, "/telas/FormularioCliente.fxml", Programa.getStage()));
			}
		});
	}
	
	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Cliente obj = new Cliente();
		createDialogForm(obj, "/telas/FormularioCliente.fxml", parentStage);
	}

	private void createDialogForm(Cliente obj, String absoluteName, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			FormularioClientesControlador controller = loader.getController();
			controller.setCliente(obj);
			controller.setClienteService(new ClienteService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagens/shopping-cart.png")));
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alertas.mostrarAlerta("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	private void initRemoveButtons() {
		tableColumnRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemover.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Cliente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setId("btRemover");
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Cliente obj) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmation", "Are you sure to delete?");
		if (result.get() == ButtonType.OK) {
			if (servico == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				servico.remove(obj);
				updateTableView();
			} catch (Exception e) {
				Alertas.mostrarAlerta("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

	@Override
	public void atualizarDados() {
		updateTableView();
	}
}
