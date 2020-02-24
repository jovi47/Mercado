package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Program;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Entity;
import model.entities.Product;
import model.entities.Stock;
import model.services.StockService;
import views.observer.Observer;
import views.util.Alerts;
import views.util.Utils;

public class StockListController implements Initializable, Observer, ListController {

	private StockService service;

	@FXML
	private TableView<Stock> tableViewStock;

	@FXML
	private TableColumn<Stock, Integer> tableColumnId;

	@FXML
	private TableColumn<Product, String> tableColumnIdProduct;

	@FXML
	private TableColumn<Stock, String> tableColumnQuantity;

	@FXML
	private TableColumn<Stock, Stock> tableColumnEDIT;

	@FXML
	private TableColumn<Stock, Stock> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Stock> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();

	}

	public void setService(StockService service) {
		this.service = service;
	}

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Product p = new Product();
		Stock obj = new Stock();
		obj.setProduct(p);
		createDialogForm(obj, "/views/StockForm.fxml", parentStage);
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnIdProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
		tableColumnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		Stage stage = (Stage) Program.getStage().getScene().getWindow();
		tableViewStock.prefHeightProperty().bind(stage.heightProperty());
	}

	@Override
	public void createDialogForm(Entity obj, String absoluteName, Stage parentStage) {
		Stock stock = (Stock) obj;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			StockFormController controller = loader.getController();
			controller.setEstoque(stock);
			controller.setEstoqueService(service);
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagens/shopping-cart.png")));
			dialogStage.setTitle("Digite dados do estoque");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Erro ao carregar tela", e.getMessage(), AlertType.ERROR);
		}
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Servico estava nulo");
		}
		List<Stock> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewStock.setItems(obsList);
		Utils.initEditButtons(tableColumnEDIT, this, "/views/StockForm.fxml");
		Utils.initRemoveButtons(tableColumnREMOVE, this);
	}

	@Override
	public void updateData() {
		updateTableView();
	}

	@Override
	public void removeEntity(Entity obj) {
		Stock stock = (Stock) obj;
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmacao", "Tem certeza que deseja deletar ?");
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Servico estava nulo");
			}
			try {
				service.remove(stock);
				updateTableView();
			} catch (Exception e) {
				Alerts.showAlert("Erro removendo objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
