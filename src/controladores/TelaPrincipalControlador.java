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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import modelo.servicos.ClienteService;
import modelo.servicos.DepartamentoService;
import modelo.servicos.EstoqueService;
import modelo.servicos.FuncionarioService;
import modelo.servicos.ProdutoService;
import telas.util.Alertas;

public class TelaPrincipalControlador implements Initializable {
	
	@FXML
	private Button btClientes;

	@FXML
	private Button btFuncionarios;

	@FXML
	private Button btTransacoes;

	@FXML
	private Button btEstoque;

	@FXML
	private Button btDepartamentos;

	@FXML
	private Button btProdutos;

	@FXML 
	public void onBtClientesAction() {
		
		carregarTela("/telas/ListarClientes.fxml",(ListarClientesControlador controller) -> {
			controller.setClienteService(new ClienteService());
			controller.updateTableView();
		});
	}
	
	@FXML 
	public void onBtFuncionariosAction() {
		carregarTela("/telas/ListarFuncionarios.fxml",(ListarFuncionariosControlador controller) -> {
			controller.setFuncionarioService(new FuncionarioService());
			controller.updateTableView();
		});
	}
	
	@FXML 
	public void onBtTransacoesAction() {
		carregarTela("/telas/ListarTransacoes.fxml",x -> {
		});
	}
	
	@FXML 
	public void onBtEstoqueAction() {
		carregarTela("/telas/ListarEstoque.fxml",(ListarEstoqueControlador controller) -> {
			controller.setService(new EstoqueService());
			controller.updateTableView();
		});
	}
	
	@FXML 
	public void onBtDepartamentosAction() {
		carregarTela("/telas/ListarDepartamentos.fxml",(ListarDepartamentosControlador controller) -> {
			controller.setDepartmentService(new DepartamentoService());
			controller.updateTableView();
		});
	}
	
	@FXML 
	public void onBtProdutosAction() {
		carregarTela("/telas/ListarProdutos.fxml",(ListarProdutosControlador controller) -> {
			controller.setProdutoService(new ProdutoService());
			controller.updateTableView();
		});
	}
	
	private <T> void carregarTela(String caminho,Consumer<T> initializingAction)  {
		if(Programa.getStage().getScene().getRoot() instanceof ScrollPane) {
			carregarVbox(caminho,initializingAction);
		}else {
			try {
				FXMLLoader carregador = new FXMLLoader(getClass().getResource("/telas/TelaCRUD.fxml"));
				ScrollPane pane = carregador.load();
				pane.setFitToHeight(true);
				pane.setFitToWidth(true);
				Scene scene = new Scene(pane);
				Programa.configureStage(true,pane.getPrefWidth(),pane.getPrefHeight(), scene);	
				carregarVbox(caminho,initializingAction);
			}catch(Exception e) {
				
			}
			
		}
	}
	
	private synchronized <T> void carregarVbox(String caminho, Consumer<T> initalizingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(caminho));
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
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
