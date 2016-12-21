package Indy;

import java.io.IOException;
import javafx.scene.layout.BorderPane;

/**
 * PaneOrganizer is my top-level class. It sets up the root node, instantiates a
 * Planner, and has an accessor method for its root node.
 */

public class PaneOrganizer {
	private BorderPane _root;

	// The constructor method sets up the root node BorderPane, and instantiates
	// a Planner
	public PaneOrganizer() throws IOException {
		// Creates root node, sets background color, and sets dimensions
		_root = new BorderPane();
		_root.setStyle("-fx-background-color: #F0F2F5;");
		_root.setPrefWidth(Constants.SCREEN_WIDTH);
		_root.setPrefHeight(Constants.SCREEN_HEIGHT);

		// Instantiate planner
		new Planner(_root);
	}

	// Accessor method, returns root node
	public BorderPane getRoot() {
		return _root;
	}
}
