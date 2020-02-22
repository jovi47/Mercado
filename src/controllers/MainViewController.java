package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Program;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.ClientService;
import model.services.DepartmentService;
import model.services.StockService;
import model.services.EmployeeService;
import model.services.ProductService;
import views.util.Alerts;

public class MainViewController implements Initializable {

	@FXML
	private Menu menuStock;

	@FXML
	private Menu menuTransaction;

	@FXML
	private Menu menuEmployee;

	@FXML
	private Menu menuProduct;

	@FXML
	private Menu menuClient;

	@FXML
	private Menu menuDepartment;

	@FXML
	public void onMenuStockAction() {
		loadView("/views/StockList.fxml", (StockListController controller) -> {
			controller.setService(new StockService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuProductAction() {
		loadView("/views/ProductList.fxml", (ProductListController controller) -> {
			controller.setProdutoService(new ProductService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuClientAction() {
		loadView("/views/ClientList.fxml", (ClientListController controller) -> {
			controller.setClienteService(new ClientService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuDepartmentAction() {
		loadView("/views/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuTransactionAction() {
		loadView("/views/TransactionList.fxml", (EmployeeListController controller) -> {
			controller.setFuncionarioService(new EmployeeService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuEmployeeAction() {
		loadView("/views/EmployeeList.fxml", (EmployeeListController controller) -> {
			controller.setFuncionarioService(new EmployeeService());
			controller.updateTableView();
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	private synchronized <T> void loadView(String absoluteName, Consumer<T> initalizingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			Scene mainScene = Program.getStage().getScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			T controller = loader.getController();
			initalizingAction.accept(controller);
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao carregar tela", e.getMessage(), AlertType.ERROR);
		}
	}
}
