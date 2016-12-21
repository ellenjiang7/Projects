package Indy;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the main class of my Indy, which is a course schedule planner.
 *
 * The main method of this application calls the start method, which
 * instantiates a PaneOrganizer and sets up the scene and stage.
 *
 */

public class App extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// instantiates PaneOrganizer, the top-level object
		PaneOrganizer organizer = new PaneOrganizer();

		// sets up the scene and stage
		Scene scene = new Scene(organizer.getRoot());
		stage.setScene(scene);
		stage.setTitle("Course Schedule Planner");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args); // launch is a method inherited from Application
	}
}
