package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Program;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import views.util.Alerts;
import views.util.Constraints;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginViewController implements Initializable {

	@FXML
	private TextField txtLogin;

	@FXML
	private TextField txtPassword;

	@FXML
	private Button btLogar;

	@FXML
	public void acaoNoBtLogar() throws IOException {
		if (verificarCampo(txtLogin) && verificarCampo(txtPassword)) {
			if (txtLogin.getText().equals("Joao") && txtPassword.getText().equals("a22")) {
				carregarTela();
			} else {
				Alerts.showAlert("Error", null, "Login ou senha invalidos", AlertType.ERROR);
			}
		} else {
			Alerts.showAlert("Error", null, "Campos nao podem estar vazios", AlertType.ERROR);
		}
	}

	private boolean verificarCampo(TextField txtField) {
		return (txtField.getText() != null && !txtField.getText().equals(""));
	}

	private void carregarTela() throws IOException {
		FXMLLoader carregador = new FXMLLoader(getClass().getResource("/views/ChoiceView.fxml"));
		AnchorPane pane = carregador.load();
		Scene scene = new Scene(pane);
		Program.configureStage(false,pane.getPrefWidth(),pane.getPrefHeight(), scene);
	}

	@Override
	public void initialize(URL local, ResourceBundle recursos) {
		iniciarNos();
	}

	private void iniciarNos() {
		Constraints.setTextFieldMaxLength(txtLogin, 30);
		Constraints.setTextFieldMaxLength(txtPassword, 30);
		btLogar.setDefaultButton(true);
	}
}
