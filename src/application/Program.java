package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Program extends Application {

	private static Stage stage;

	@Override
	public void start(Stage initialStage) {
		try {
			stage = initialStage;
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagens/shopping-cart.png")));
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
			AnchorPane pane = loader.load();
			Scene scene = new Scene(pane);
			configureStage(false, pane.getPrefWidth(), pane.getPrefHeight(), scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static void configureStage(Boolean resizable, Double width, Double height, Scene scene) {
		stage.setResizable(resizable);
		stage.setTitle("Mercado");
		stage.setMinWidth(width);
		stage.setMinHeight(height);
		stage.setScene(scene);
	}

	public static Stage getStage() {
		return stage;
	}
}
