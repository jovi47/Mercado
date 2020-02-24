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
import model.services.ProductService;

import views.observer.Observer;
import views.util.Alerts;
import views.util.Utils;

public class ProductListController implements Initializable, Observer, ListController {

	private ProductService service;
	@FXML
	private TableView<Product> tableViewProduct;

	@FXML
	private TableColumn<Product, Integer> tableColumnId;

	@FXML
	private TableColumn<Product, String> tableColumnName;

	@FXML
	private TableColumn<Product, String> tableColumnDescription;

	@FXML
	private TableColumn<Product, Double> tableColumnPrice;

	@FXML
	private TableColumn<Product, Product> tableColumnEDIT;

	@FXML
	private TableColumn<Product, Product> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Product> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();

	}

	public void setProdutoService(ProductService service) {
		this.service = service;
	}

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Product obj = new Product();
		createDialogForm(obj, "/views/ProductForm.fxml", parentStage);
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		tableColumnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		Utils.formatTableColumnDouble(tableColumnPrice, 2);
		Stage stage = (Stage) Program.getStage().getScene().getWindow();
		tableViewProduct.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Servico estava nulo");
		}
		List<Product> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewProduct.setItems(obsList);
		Utils.initEditButtons(tableColumnEDIT, this, "/views/ProductForm.fxml");
		Utils.initRemoveButtons(tableColumnREMOVE, this);
	}

	@Override
	public void updateData() {
		updateTableView();
	}

	@Override
	public void createDialogForm(Entity obj, String absoluteName, Stage parentStage) {
		Product pro = (Product) obj;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ProductFormController controller = loader.getController();
			controller.setProduto(pro);
			controller.setProdutoService(new ProductService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagens/shopping-cart.png")));
			dialogStage.setTitle("Digite dados do produto");
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

	@Override
	public void removeEntity(Entity obj) {
		Product pro = (Product) obj;
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmacao", "Tem certeza que deseja deletar ?");
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Servico estava nulo");
			}
			try {
				service.remove(pro);
				updateTableView();
			} catch (Exception e) {
				Alerts.showAlert("Erro salvando objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
