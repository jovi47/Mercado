package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
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
import model.entities.Client;
import model.entities.Entity;
import model.services.ClientService;
import views.observer.Observer;
import views.util.Alerts;
import views.util.Utils;

public class ClientListController implements Initializable, Observer, ListController {

	private ClientService service;
	@FXML
	private TableView<Client> tableViewCliente;

	@FXML
	private TableColumn<Client, Integer> tableColumnId;

	@FXML
	private TableColumn<Client, String> tableColumnName;

	@FXML
	private TableColumn<Client, String> tableColumnCPF;

	@FXML
	private TableColumn<Client, String> tableColumnCEP;

	@FXML
	private TableColumn<Client, String> tableColumnFone;

	@FXML
	private TableColumn<Client, Calendar> tableColumnBirthDate;

	@FXML
	private TableColumn<Client, Client> tableColumnEDIT;

	@FXML
	private TableColumn<Client, Client> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Client> obsList;

	public void setClienteService(ClientService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Servico estava nulo");
		}
		List<Client> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewCliente.setItems(obsList);
		Utils.initEditButtons(tableColumnEDIT, this, "/views/ClientForm.fxml");
		Utils.initRemoveButtons(tableColumnREMOVE, this);
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnCPF.setCellValueFactory(new PropertyValueFactory<>("CPF"));
		tableColumnCEP.setCellValueFactory(new PropertyValueFactory<>("CEP"));
		tableColumnFone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		Stage stage = (Stage) Program.getStage().getScene().getWindow();
		tableViewCliente.prefHeightProperty().bind(stage.heightProperty());
	}

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Client obj = new Client();
		createDialogForm(obj, "/views/ClientForm.fxml", parentStage);
	}

	@Override
	public void updateData() {
		updateTableView();
	}

	@Override
	public void createDialogForm(Entity obj, String absoluteName, Stage parentStage) {
		Client cliente = (Client) obj;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			ClientFormController controller = loader.getController();
			controller.setCliente(cliente);
			controller.setClienteService(new ClientService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagens/shopping-cart.png")));
			dialogStage.setTitle("Digite dados do cliente");
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
		Client client = (Client) obj;
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmacao", "Tem certeza que deseja deletar ?");
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Servico estava nulo");
			}
			try {
				service.remove(client);
				updateTableView();
			} catch (Exception e) {
				Alerts.showAlert("Erro removendo objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}

	}
}
