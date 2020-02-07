package controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import aplicacao.Programa;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import modelo.servicos.ClienteService;
import modelo.servicos.DepartamentoService;
import modelo.servicos.EstoqueService;
import modelo.servicos.FuncionarioService;
import modelo.servicos.ProdutoService;
import telas.util.Alertas;

public class TelaCRUDControlador implements Initializable {

	@FXML
	private Menu menuEstoque;

	@FXML
	private Menu menuTransacoes;

	@FXML
	private Menu menuFuncionarios;

	@FXML
	private Menu menuProdutos;

	@FXML
	private Menu menuClientes;

	@FXML
	private Menu menuDepartamentos;

	@FXML
	public void onMenuEstoqueAction() {
		loadView("/telas/ListarEstoque.fxml",(ListarEstoqueControlador controller) -> {
			controller.setService(new EstoqueService());
			controller.updateTableView();
		});
	}
	@FXML
	public void onMenuProdutosAction() {
		loadView("/telas/ListarProdutos.fxml",(ListarProdutosControlador controller) -> {
			controller.setProdutoService(new ProdutoService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuClientesAction() {
		loadView("/telas/ListarClientes.fxml",(ListarClientesControlador controller) -> {
			controller.setClienteService(new ClienteService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuDepartamentosAction() {
		loadView("/telas/ListarDepartamentos.fxml",(ListarDepartamentosControlador controller) -> {
			controller.setDepartmentService(new DepartamentoService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuTransacoesAction() {
		loadView("/telas/ListarFuncionarios.fxml",(ListarFuncionariosControlador controller) -> {
			controller.setFuncionarioService(new FuncionarioService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuFuncionariosAction() {
		loadView("/telas/ListarFuncionarios.fxml",(ListarFuncionariosControlador controller) -> {
			controller.setFuncionarioService(new FuncionarioService());
			controller.updateTableView();
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	private synchronized <T> void loadView(String absoluteName, Consumer<T> initalizingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			Scene mainScene = Programa.getStage().getScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			T controller = loader.getController();
			initalizingAction.accept(controller);
		} catch (IOException e) {
			Alertas.mostrarAlerta("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
