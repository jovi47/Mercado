package controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import aplicacao.Programa;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import telas.util.Alertas;
import telas.util.Limitacoes;

public class TelaDeLoginControlador implements Initializable {

	@FXML
	private TextField txtLogin;

	@FXML
	private TextField txtSenha;

	@FXML
	private Button btLogar;

	@FXML
	public void acaoNoBtLogar() throws IOException {
		if (verificarCampo(txtLogin) && verificarCampo(txtSenha)) {
			if (txtLogin.getText().equals("Joao") && txtSenha.getText().equals("a22")) {
				carregarTela();
			} else {
				Alertas.mostrarAlerta("Error", null, "Login ou senha invalidos", AlertType.ERROR);
			}
		} else {
			Alertas.mostrarAlerta("Error", null, "Campos nao podem estar vazios", AlertType.ERROR);
		}
	}

	private boolean verificarCampo(TextField txtField) {
		return (txtField.getText() != null && !txtField.getText().equals(""));
	}

	private void carregarTela() throws IOException {
		FXMLLoader carregador = new FXMLLoader(getClass().getResource("/telas/TelaPrincipal.fxml"));
		AnchorPane pane = carregador.load();
		Scene scene = new Scene(pane);
		Programa.configureStage(false,pane.getPrefWidth(),pane.getPrefHeight(), scene);
	}

	@Override
	public void initialize(URL local, ResourceBundle recursos) {
		iniciarNos();
	}

	private void iniciarNos() {
		Limitacoes.setTextFieldTamanhoMaximo(txtLogin, 30);
		Limitacoes.setTextFieldTamanhoMaximo(txtSenha, 30);
		btLogar.setDefaultButton(true);
	}
}
