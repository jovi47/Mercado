package controladores;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import aplicacao.Programa;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.util.Callback;
import modelo.entidades.Departamento;
import modelo.entidades.Funcionario;
import modelo.servicos.DepartamentoService;
import modelo.servicos.FuncionarioService;
import telas.observador.Observador;
import telas.util.Alertas;
import telas.util.Utils;

public class ListarFuncionariosControlador implements Initializable, Observador {

	private FuncionarioService service;
	@FXML
	private TableView<Funcionario> tableViewFuncionario;

	@FXML
	private TableColumn<Funcionario, Integer> tableColumnId;

	@FXML
	private TableColumn<Funcionario, String> tableColumnNome;

	@FXML
	private TableColumn<Funcionario, String> tableColumnCPF;

	@FXML
	private TableColumn<Funcionario, String> tableColumnCEP;

	@FXML
	private TableColumn<Funcionario, String> tableColumnTelefone;

	@FXML
	private TableColumn<Funcionario, Calendar> tableColumnDataNascimento;

	@FXML
	private TableColumn<Funcionario, Calendar> tableColumnInicioContrato;

	@FXML
	private TableColumn<Funcionario, Calendar> tableColumnFimContrato;

	@FXML
	private TableColumn<Funcionario, Double> tableColumnSalario;

	@FXML
	private TableColumn<Funcionario, Funcionario> tableColumnEditar;

	@FXML
	private TableColumn<Funcionario, Funcionario> tableColumnRemover;

	@FXML
	private TableColumn<Departamento, String> tableColumnDepartamento;

	@FXML
	private Button btNovo;

	private ObservableList<Funcionario> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();

	}

	public void setFuncionarioService(FuncionarioService service) {
		this.service = service;
	}
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Funcionario obj = new Funcionario();
		createDialogForm(obj, "/telas/FormularioFuncionario.fxml", parentStage);
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnCPF.setCellValueFactory(new PropertyValueFactory<>("CPF"));
		tableColumnCEP.setCellValueFactory(new PropertyValueFactory<>("CEP"));
		tableColumnTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		tableColumnDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		tableColumnFimContrato.setCellValueFactory(new PropertyValueFactory<>("fimContrato"));
		tableColumnInicioContrato.setCellValueFactory(new PropertyValueFactory<>("inicioContrato"));
		tableColumnSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));
		Utils.formatTableColumnDouble(tableColumnSalario, 2);
		tableColumnDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));
		Utils.formatTableColumnDate(tableColumnDataNascimento, "dd/MM/yyyy");
		Utils.formatTableColumnDate(tableColumnFimContrato, "dd/MM/yyyy");
		Utils.formatTableColumnDate(tableColumnInicioContrato, "dd/MM/yyyy");
		Stage stage = (Stage) Programa.getStage().getScene().getWindow();
		tableViewFuncionario.prefHeightProperty().bind(stage.heightProperty());
	}

	private void createDialogForm(Funcionario obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			FormularioFuncionarioControlador controller = loader.getController();
			controller.setFuncionario(obj);
			controller.setServices(new FuncionarioService(), new DepartamentoService());
			controller.subscribeDataChangeListener(this);
			controller.loadAssociatedObjects();
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagens/shopping-cart.png")));
			dialogStage.setTitle("Enter Department data");
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

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Funcionario> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewFuncionario.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	@Override
	public void atualizarDados() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEditar.setCellFactory(param -> new TableCell<Funcionario, Funcionario>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Funcionario obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setId("btEditar");
				button.setOnAction(
						event -> createDialogForm(obj, "/telas/FormularioFuncionario.fxml", Programa.getStage()));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemover.setCellFactory(param -> new TableCell<Funcionario, Funcionario>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Funcionario obj, boolean empty) {
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

	private void removeEntity(Funcionario obj) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmation", "Are you sure to delete?");
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (Exception e) {
				Alertas.mostrarAlerta("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
