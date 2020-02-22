package views.util;

import javafx.application.Platform;
import javafx.scene.control.TextField;

public class Constraints {

	public static void setTextFieldInteger(TextField textField) {
		textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*")) {
				textField.setText(oldValue);
			}
		});
	}

	public static void setTextFieldMaxLength(TextField textField, int max) {
		textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				textField.setText(oldValue);
			}
		});
	}

	public static void setTextFieldDouble(TextField textField) {
		textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
				textField.setText(oldValue);
			}
		});
	}

	public static void cpfCnpjField(TextField textField) {
		setTextFieldMaxLength(textField, 18);
		textField.lengthProperty().addListener((observableValue, number1, number2) -> {
			String valor = textField.getText();
			if (number2.intValue() <= 14) {
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
			dividerPosition(textField);
		});
	}

	private static void dividerPosition(TextField textField) {
		Platform.runLater(() -> {
			if (textField.getText().length() != 0) {
				textField.positionCaret(textField.getText().length());
			}
		});
	}

	public static void cepField(TextField textField) {
		setTextFieldMaxLength(textField, 9);
		textField.lengthProperty().addListener((observableValue, number1, number2) -> {
			String value = textField.getText();
			value = value.replaceAll("[^0-9]", "");
			value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
			textField.setText(value);
			dividerPosition(textField);
		});
	}

	public static void foneField(TextField textField) {
		setTextFieldMaxLength(textField, 14);
		textField.lengthProperty().addListener((observableValue, number1, number2) -> {
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
				Constraints.dividerPosition(textField);

			} catch (Exception ex) {
			}
		});
	}

	public static void cpfField(TextField textField) {
		Constraints.setTextFieldMaxLength(textField, 14);
		textField.lengthProperty().addListener((observableValue, number1, number2) -> {
			String value = textField.getText();
			value = value.replaceAll("[^0-9]", "");
			value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
			value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
			value = value.replaceFirst("(\\d{3})(\\d)", "$1-$2");
			try {
				textField.setText(value);
				Constraints.dividerPosition(textField);
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
