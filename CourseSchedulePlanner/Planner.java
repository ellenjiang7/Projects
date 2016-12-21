package Indy;

import java.io.IOException;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * The Planner class does some of the big picture set up and organization of
 * Panes and Groups, creates an ImageView for the books image, creates a time
 * tracker array, and instantiates a Searcher and Calendar.
 * 
 */

public class Planner {

	// The constructor creates Panes for the searcher and calendar and sets them
	// in the BorderPane, sets up Panes for scrolling and a group for the
	// tutorial images, creates a
	// time tracker array, creates other nodes for the Calendar, and
	// instantiates a Searcher and Calendar.
	public Planner(BorderPane root) throws IOException {

		// Make pane for searchbox and dropdown and add to top of root
		// BorderPane
		Pane searcherPane = new Pane();
		root.setTop(searcherPane);

		// Make panes to hold the scrollPane and calendarPane, and set to center
		// of root BorderPane
		Pane calendarBack = new Pane();
		root.setCenter(calendarBack);

		// Make group for tutorial images, and add it to the root node to let
		// them fill the entire screen
		Group tutorialGroup = new Group();
		root.getChildren().add(tutorialGroup);

		// Make calendar pane and set dimensions
		Pane calendarPane = new Pane();
		calendarPane.setPrefSize(Constants.CALENDAR_WIDTH,
				Constants.CALENDAR_HEIGHT);

		// Make scroll pane, set dimensions, set calendarPane as content
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setPrefSize(Constants.SCROLL_WIDTH, Constants.SCROLL_HEIGHT);
		scrollPane.setContent(calendarPane);

		// Make scrollPane the same width as its content
		scrollPane.setFitToWidth(true);

		// Make scrollPane pannable (scrolls when mouse is clicked and dragged)
		scrollPane.setPannable(true);

		// Add scrollPane and calendarPane to calendarBack
		calendarBack.getChildren().addAll(scrollPane, calendarPane);

		// Make two nodes, one for the background of the calendar, and one for
		// the courses to be displayed, and add them with the background behind
		// the display to keep the correct nodes in front
		Pane backgroundPane = new Pane();
		Group courseDisplayGroup = new Group();
		calendarPane.getChildren().addAll(backgroundPane, courseDisplayGroup);

		// Create an array of booleans to keep track of which time slots are
		// filled (for the rows, we want twice the number of hour rows so as to
		// account for every half an hour of time). Set all booleans as false,
		// as no courses are stored initially
		boolean[][] timeTracker = new boolean[Constants.COL_TOTAL_NUMBER][Constants.ROW_TOTAL_NUMBER * 2];
		for (int i = 0; i < Constants.COL_TOTAL_NUMBER; i++) {
			for (int j = 0; j < Constants.ROW_TOTAL_NUMBER * 2; j++) {
				timeTracker[i][j] = false;
			}
		}

		// Instantiate a searcher
		Searcher searcher = new Searcher(searcherPane, courseDisplayGroup,
				timeTracker, tutorialGroup);

		// Instantiate a calendar
		new Calendar(root, searcher, backgroundPane);
	}
}
