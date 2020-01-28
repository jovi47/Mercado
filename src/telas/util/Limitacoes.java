package telas.util;

import javafx.application.Platform;
import javafx.scene.control.TextField;

public class Limitacoes {

	public static void setTextFieldInteiro(TextField texto) {
		texto.textProperty().addListener((observavel, antigoValor, novoValor) -> {
			if (novoValor != null && !novoValor.matches("\\d*")) {
				texto.setText(antigoValor);
			}
		});
	}

	public static void setTextFieldTamanhoMaximo(TextField texto, int maximo) {
		texto.textProperty().addListener((observavel, antigoValor, novoValor) -> {
			if (novoValor != null && novoValor.length() > maximo) {
				texto.setText(antigoValor);
			}
		});
	}

	public static void setTextFieldReal(TextField texto) {
		texto.textProperty().addListener((observavel, antigoValor, novoValor) -> {
			if (novoValor != null && !novoValor.matches("\\d*([\\.]\\d*)?")) {
				texto.setText(antigoValor);
			}
		});
	}

	public static void cpfCnpjField(TextField textField) {
		setTextFieldTamanhoMaximo(textField, 18);
		textField.lengthProperty().addListener((observavel, numero, numero2) -> {
			String valor = textField.getText();
			if (numero2.intValue() <= 14) {
				valor = valor.replaceAll("[^0-9]", "");
				valor = valor.replaceFirst("(\\d{3})(\\d)", "$1.$2");
				valor = valor.replaceFirst("(\\d{3})(\\d)", "$1.$2");
				valor = valor.replaceFirst("(\\d{3})(\\d)", "$1-$2");
			} else {
				valor = valor.replaceAll("[^0-9]", "");
				valor = valor.replaceFirst("(\\d{2})(\\d)", "$1.$2");
				valor = valor.replaceFirst("(\\d{3})(\\d)", "$1.$2");
				valor = valor.replaceFirst("(\\d{3})(\\d)", "$1/$2");
				valor = valor.replaceFirst("(\\d{4})(\\d)", "$1-$2");
			}
			textField.setText(valor);
			posicionarDivisor(textField);
		});
	}

	private static void posicionarDivisor(TextField textField) {
		Platform.runLater(() -> {
			if (textField.getText().length() != 0) {
				textField.positionCaret(textField.getText().length());
			}
		});
	}

	public static void cepField(TextField textField) {
		setTextFieldTamanhoMaximo(textField, 9);
		textField.lengthProperty().addListener((observableValue, number, number2) -> {
			String value = textField.getText();
			value = value.replaceAll("[^0-9]", "");
			value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
			textField.setText(value);
			posicionarDivisor(textField);
		});
	}

	public static void foneField(TextField textField) {
		setTextFieldTamanhoMaximo(textField, 14);
		textField.lengthProperty().addListener((observableValue, number, number2) -> {
			try {
				String value = textField.getText();
				value = value.replaceAll("[^0-9]", "");
				int tam = value.length();
				value = value.replaceFirst("(\\d{2})(\\d)", "($1)$2");
				value = value.replaceFirst("(\\d{4})(\\d)", "$1-$2");
				if (tam > 10) {
					value = value.replaceAll("-", "");
					value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
				}
				textField.setText(value);
				Limitacoes.posicionarDivisor(textField);

			} catch (Exception ex) {
			}
		});
	}

	public static void cpfField(TextField textField) {
		Limitacoes.setTextFieldTamanhoMaximo(textField, 14);
		textField.lengthProperty().addListener((observableValue, number, number2) -> {
			String value = textField.getText();
			value = value.replaceAll("[^0-9]", "");
			value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
			value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
			value = value.replaceFirst("(\\d{3})(\\d)", "$1-$2");
			try {
				textField.setText(value);
				Limitacoes.posicionarDivisor(textField);
			} catch (Exception ex) {
			}
		});
	}

	public static String cpfField(String cpf) {
		String value = cpf;
		value = value.replaceAll("[^0-9]", "");
		value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
		value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
		value = value.replaceFirst("(\\d{3})(\\d)", "$1-$2");
		return cpf;
	}
}
