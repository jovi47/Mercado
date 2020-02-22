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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.ClientService;
import model.services.DepartmentService;
import model.services.StockService;
import model.services.EmployeeService;
import model.services.ProductService;
import views.util.Alerts;

public class ChoiceViewController implements Initializable {

	@FXML
	private Button btClient;

	@FXML
	private Button btEmployee;

	@FXML
	private Button btTransaction;

	@FXML
	private Button btStock;

	@FXML
	private Button btDepartment;

	@FXML
	private Button btProduct;

	@FXML
	public void onBtClientAction() {
		loadView("/views/ClientList.fxml", (ClientListController controller) -> {
			controller.setClienteService(new ClientService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onBtEmployeeAction() {
		loadView("/views/EmployeeList.fxml", (EmployeeListController controller) -> {
			controller.setFuncionarioService(new EmployeeService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onBtTransactionAction() {
		loadView("/views/TransactionList.fxml", x -> {
		});
	}

	@FXML
	public void onBtStockAction() {
		loadView("/views/StockList.fxml", (StockListController controller) -> {
			controller.setService(new StockService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onBtDepartmentAction() {
		loadView("/views/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onBtProductAction() {
		loadView("/views/ProductList.fxml", (ProductListController controller) -> {
			controller.setProdutoService(new ProductService());
			controller.updateTableView();
		});
	}

	private <T> void loadView(String path, Consumer<T> initializingAction) {
		if (Program.getStage().getScene().getRoot() instanceof ScrollPane) {
			loadVBox(path, initializingAction);
		} else {
			try {
				FXMLLoader carregador = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
				ScrollPane pane = carregador.load();
				pane.setFitToHeight(true);
				pane.setFitToWidth(true);
				Scene scene = new Scene(pane);
				Program.configureStage(true, pane.getPrefWidth(), pane.getPrefHeight(), scene);
				loadVBox(path, initializingAction);
			} catch (Exception e) {

			}

		}
	}

	private synchronized <T> void loadVBox(String path, Consumer<T> initalizingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
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
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
