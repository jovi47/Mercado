package telas.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alertas {

	public static void mostrarAlerta(String titulo, String cabecalho, String mensagem, AlertType tipo) {
		Alert alerta = new Alert(tipo);
		alerta.setTitle(titulo);
		alerta.setHeaderText(cabecalho);
		alerta.setContentText(mensagem);
		alerta.show();
	}

	public static Optional<ButtonType> mostrarConfirmacao(String titulo, String mensagem) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(null);
		alert.setContentText(mensagem);
		return alert.showAndWait();
	}
}
