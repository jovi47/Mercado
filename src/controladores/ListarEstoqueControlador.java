package controladores;

import java.io.IOException;
import java.net.URL;
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
import modelo.entidades.Estoque;
import modelo.entidades.Produto;
import modelo.servicos.EstoqueService;
import telas.observador.Observador;
import telas.util.Alertas;
import telas.util.Utils;

public class ListarEstoqueControlador implements Initializable, Observador {

	private EstoqueService service;

	@FXML
	private TableView<Estoque> tableViewEstoque;

	@FXML
	private TableColumn<Estoque, Integer> tableColumnId;

	@FXML
	private TableColumn<Produto, String> tableColumnIdProduto;

	@FXML
	private TableColumn<Estoque, String> tableColumnQuantidade;

	@FXML
	private TableColumn<Estoque, Estoque> tableColumnEDIT;

	@FXML
	private TableColumn<Estoque, Estoque> tableColumnREMOVE;

	@FXML
	private Button btNovo;

	private ObservableList<Estoque> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();

	}

	public void setService(EstoqueService service) {
		this.service = service;
	}

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Produto p = new Produto();
		Estoque obj = new Estoque();
		obj.setProduto(p);
		createDialogForm(obj, "/telas/FormularioEstoque.fxml", parentStage);
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnIdProduto.setCellValueFactory(new PropertyValueFactory<>("produto"));
		tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		Stage stage = (Stage) Programa.getStage().getScene().getWindow();
		tableViewEstoque.prefHeightProperty().bind(stage.heightProperty());
	}

	private void createDialogForm(Estoque obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			FormularioEstoqueControlador controller = loader.getController();
			controller.setEstoque(obj);
			controller.setEstoqueService(service);
			controller.subscribeDataChangeListener(this);
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
		List<Estoque> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewEstoque.setItems(obsList);
		initEditButtons();
		 initRemoveButtons();
	}

	@Override
	public void atualizarDados() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Estoque, Estoque>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Estoque obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setId("btEditar");
				button.setOnAction(
						event -> createDialogForm(obj, "/telas/FormularioEstoque.fxml", Programa.getStage()));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Estoque, Estoque>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Estoque obj, boolean empty) {
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

	private void removeEntity(Estoque obj) {
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
