package views.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Locale;

import application.Program;
import controllers.ListController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.entities.Entity;

public class Utils {
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Double tryParseToDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static <T> void formatTableColumnDate(TableColumn<T, Calendar> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Calendar> cell = new TableCell<T, Calendar>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);

				@Override
				protected void updateItem(Calendar item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(sdf.format(item.getTime()));
					}
				}
			};
			return cell;
		});

	}

	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						Locale.setDefault(Locale.US);
						setText(String.format("%." + decimalPlaces + "f", item));
					}
				}
			};
			return cell;
		});
	}

	public static void formatDatePicker(DatePicker datePicker, String format) {
		datePicker.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);

			{
				datePicker.setPromptText(format.toLowerCase());
			}

			@Override
			public String toString(LocalDate object) {
				if (object != null) {
					return dateFormatter.format(object);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});
	}

	public static void addIcon(Alert alert) {
		final Image icone = new Image("/imagens/shopping-cart.png");
		Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(icone);
	}

	public static Boolean verifyCPF(String cpfWithFormat) {
		String cpf = cpfWithFormat.substring(0, 3) + cpfWithFormat.substring(4, 7) + cpfWithFormat.substring(8, 11)
				+ cpfWithFormat.substring(12, 14);
		if (cpf.equals("00000000000") || cpf.equals("11111111111") || cpf.equals("22222222222")
				|| cpf.equals("33333333333") || cpf.equals("44444444444") || cpf.equals("55555555555")
				|| cpf.equals("66666666666") || cpf.equals("77777777777") || cpf.equals("88888888888")
				|| cpf.equals("99999999999") || (cpf.length() != 11))
			return (false);
		char digito10, digito11;
		int somaDigitos, i, r, numero, peso;
		try {
			somaDigitos = 0;
			peso = 10;
			for (i = 0; i < 9; i++) {
				numero = (int) (cpf.charAt(i) - 48);
				somaDigitos += (numero * peso);
				peso += -1;
			}
			r = 11 - (somaDigitos % 11);
			if ((r == 10) || (r == 11))
				digito10 = '0';
			else
				digito10 = (char) (r + 48);
			somaDigitos = 0;
			peso = 11;
			for (i = 0; i < 10; i++) {
				numero = (int) (cpf.charAt(i) - 48);
				somaDigitos += (numero * peso);
				peso += -1;
			}
			r = 11 - (somaDigitos % 11);
			if ((r == 10) || (r == 11))
				digito11 = '0';
			else
				digito11 = (char) (r + 48);
			return (digito10 == cpf.charAt(9)) && (digito11 == cpf.charAt(10));
		} catch (InputMismatchException error) {
			return (false);
		}
	}

	public static <T> void initRemoveButtons(TableColumn<T, T> column, ListController controller) {
		column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		column.setCellFactory(param -> new TableCell<T, T>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(T obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setId("btRemover");
				button.setOnAction(event -> controller.removeEntity((Entity) obj));
			}
		});
	}

	public static <T> void initEditButtons(TableColumn<T, T> column, ListController controller, String path) {
		column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		column.setCellFactory(param -> new TableCell<T, T>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(T obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setId("btEditar");
				button.setOnAction(event -> controller.createDialogForm((Entity) obj, path, Program.getStage()));
			}
		});
	}
}
