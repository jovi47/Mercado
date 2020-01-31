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
import modelo.entidades.Produto;
import modelo.servicos.ProdutoService;
import telas.observador.Observador;
import telas.util.Alertas;
import telas.util.Utils;

public class ListarProdutosControlador implements Initializable, Observador {

	private ProdutoService servico;
	@FXML
	private TableView<Produto> tableViewProduto;

	@FXML
	private TableColumn<Produto, Integer> tableColumnId;

	@FXML
	private TableColumn<Produto, String> tableColumnNome;

	@FXML
	private TableColumn<Produto, String> tableColumnDescricao;

	@FXML
	private TableColumn<Produto, Double> tableColumnPreco;

	@FXML
	private TableColumn<Produto, Produto> tableColumnEditar;

	@FXML
	private TableColumn<Produto, Produto> tableColumnRemover;

	private ObservableList<Produto> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();

	}

	public void setProdutoService(ProdutoService service) {
		this.servico = service;
	}

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Produto obj = new Produto();
		createDialogForm(obj, "/telas/FormularioProduto.fxml", parentStage);
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
		tableColumnPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
		Utils.formatTableColumnDouble(tableColumnPreco, 2);
		Stage stage = (Stage) Programa.getStage().getScene().getWindow();
		tableViewProduto.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (servico == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Produto> list = servico.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewProduto.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	@Override
	public void atualizarDados() {
		updateTableView();
	}

	private void createDialogForm(Produto obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FormularioProdutoControlador controller = loader.getController();
			controller.setProduto(obj);
			controller.setProdutoService(new ProdutoService());
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

	private void initEditButtons() {
		tableColumnEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEditar.setCellFactory(param -> new TableCell<Produto, Produto>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Produto obj, boolean empty) {
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
		tableColumnRemover.setCellFactory(param -> new TableCell<Produto, Produto>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Produto obj, boolean empty) {
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

	private void removeEntity(Produto obj) {
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

}