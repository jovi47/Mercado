package controllers;

import javafx.stage.Stage;
import model.entities.Entity;

public interface ListController {
	
	 void createDialogForm(Entity obj, String absoluteName, Stage parentStage);

	void removeEntity(Entity obj);
}
