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
import model.entities.Department;
import model.entities.Employee;
import model.entities.Entity;
import model.services.DepartmentService;
import model.services.EmployeeService;
import views.observer.Observer;
import views.util.Alerts;
import views.util.Utils;

public class EmployeeListController implements Initializable, Observer, ListController {

	private EmployeeService service;
	@FXML
	private TableView<Employee> tableViewEmployee;

	@FXML
	private TableColumn<Employee, Integer> tableColumnId;

	@FXML
	private TableColumn<Employee, String> tableColumnName;

	@FXML
	private TableColumn<Employee, String> tableColumnCPF;

	@FXML
	private TableColumn<Employee, String> tableColumnCEP;

	@FXML
	private TableColumn<Employee, String> tableColumnFone;

	@FXML
	private TableColumn<Employee, Calendar> tableColumnBirthDate;

	@FXML
	private TableColumn<Employee, Calendar> tableColumnHiringDate;

	@FXML
	private TableColumn<Employee, String> tableColumnResignationDate;

	@FXML
	private TableColumn<Employee, Double> tableColumnSalary;

	@FXML
	private TableColumn<Employee, Employee> tableColumnEDIT;

	@FXML
	private TableColumn<Employee, Employee> tableColumnREMOVE;

	@FXML
	private TableColumn<Department, String> tableColumnDepartment;

	@FXML
	private Button btNew;

	private ObservableList<Employee> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();

	}

	public void setFuncionarioService(EmployeeService service) {
		this.service = service;
	}

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Employee obj = new Employee();
		createDialogForm(obj, "/views/EmployeeForm.fxml", parentStage);
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnCPF.setCellValueFactory(new PropertyValueFactory<>("CPF"));
		tableColumnCEP.setCellValueFactory(new PropertyValueFactory<>("CEP"));
		tableColumnFone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		tableColumnResignationDate.setCellValueFactory(new PropertyValueFactory<>("fimContrato"));
		tableColumnHiringDate.setCellValueFactory(new PropertyValueFactory<>("inicioContrato"));
		tableColumnSalary.setCellValueFactory(new PropertyValueFactory<>("salario"));
		Utils.formatTableColumnDouble(tableColumnSalary, 2);
		tableColumnDepartment.setCellValueFactory(new PropertyValueFactory<>("departamento"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		Utils.formatTableColumnDate(tableColumnHiringDate, "dd/MM/yyyy");
		Stage stage = (Stage) Program.getStage().getScene().getWindow();
		tableViewEmployee.prefHeightProperty().bind(stage.heightProperty());
	}

	@Override
	public void createDialogForm(Entity obj, String absoluteName, Stage parentStage) {
		Employee fun = (Employee) obj;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			EmployeeFormController controller = loader.getController();
			controller.setFuncionario(fun);
			controller.setServices(new EmployeeService(), new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.loadAssociatedObjects();
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagens/shopping-cart.png")));
			dialogStage.setTitle("Digites dados do funcionario");
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
		List<Employee> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewEmployee.setItems(obsList);
		Utils.initEditButtons(tableColumnEDIT, this, "/views/EmployeeForm.fxml");
		Utils.initRemoveButtons(tableColumnREMOVE, this);
	}

	@Override
	public void updateData() {
		updateTableView();
	}

	@Override
	public void removeEntity(Entity obj) {
		Employee emp = (Employee) obj;
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmacao ", "Tem certeza que deseja deletar ?");
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Servico estava nulo");
			}
			try {
				service.remove(emp);
				updateTableView();
			} catch (Exception e) {
				Alerts.showAlert("Erro removendo objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
