package Indy;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * MouseOverText creates text that can reacts when the mouse hovers over it. It
 * is used to create the text in the course display.
 */

public class MouseOverText {
	private Text _courseText, _delete;
	private Color _color;
	private Pane _dismissPane;
	private boolean[][] _timeTracker;
	private Rectangle _background;
	private Course _course;
	private Group _group;
	private double _elapsedTime;

	// Creates the text and adds EventHandlers to detect when the mouse enters
	// and exits the node and when it is clicked
	public MouseOverText(double positionX, double positionY, String number,
			Color color, Group group, Course course, double elapsedTime) {

		// store parameters in instance variables
		_color = color;
		_course = course;
		_group = group;
		_elapsedTime = elapsedTime;

		// Create text with course number, center aligned, with text wrapping to
		// keep the text within the display box
		_courseText = new Text(positionX, positionY, number);
		_courseText.setTextAlignment(TextAlignment.CENTER);
		_courseText.setWrappingWidth(Constants.WRAPPING_WIDTH);

		// Sets font, opacity, and color
		_courseText.setFont(Font.font(Constants.MAIN_FONT, FontWeight.BOLD,
				Constants.COURSE_TEXT_FONT_SIZE));
		_courseText.setOpacity(Constants.COURSE_TEXT_OPACITY);
		_courseText.setFill(color);

		// Creates delete display when clicked
		_courseText.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new CourseClickHandler());

		// Turns blue when mouse enters the node
		_courseText.setOnMouseEntered(new MouseEnterHandler());

		// Resets to normal color when mouse exits the node
		_courseText.setOnMouseExited(new MouseExitHandler());

		// Add the courseText to the group that was passed in as a parameter
		group.getChildren().add(_courseText);
	}

	// Makes color blue when this EventHandler is activated
	private class MouseEnterHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			_courseText.setFill(Constants.COLOR_BLUE);
		}
	}

	// Resets color to original color when this EventHandler is activated
	private class MouseExitHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			_courseText.setFill(_color);
		}
	}

	// Creates the delete display when this EventHandler is activated
	private class CourseClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			// Create a Pane that goes over the entire screen; when clicked it
			// will remove the delete display
			_dismissPane = new Pane();
			_dismissPane.setPrefWidth(Constants.SCREEN_WIDTH);
			_dismissPane.setPrefHeight(Constants.SCREEN_HEIGHT);
			_dismissPane.addEventHandler(MouseEvent.MOUSE_CLICKED,
					new DismissClickHandler());
			_group.getChildren().add(_dismissPane);

			// Display a delete display over each course display
			ArrayList<Double> coordinates = _course.getCoordinates();
			for (int i = 0; i < coordinates.size() - 1; i++) {
				// Figure out where to display the delete panes from the
				// coordinates
				double column = coordinates.get(i + 1).doubleValue();
				double row = coordinates.get(0).doubleValue();

				double positionX = Constants.COL_SPACING * column
						+ Constants.COL_POS_X;
				double positionY = Constants.ROW_SPACING * row
						+ Constants.ROW_POS_Y;

				// Create a white, transparent rectangle over course display
				_background = new Rectangle(positionX, positionY,
						Constants.COURSE_BOX_WIDTH, Constants.COURSE_BOX_HEIGHT
								* _elapsedTime);
				_background.setFill(Color.WHITE);
				_background.setOpacity(Constants.DELETE_BACKGROUND_OPACITY);

				// Create a delete text (minus sign), center aligned, set its
				// font and color, and add a EventHandler to delete the course
				// when clicked
				_delete = new Text(positionX + Constants.DELETE_OFFSET_X,
						positionY
								+ (Constants.COURSE_BOX_HEIGHT * _elapsedTime)
								/ 2 + Constants.DELETE_OFFSET_Y, "-");
				_delete.setTextAlignment(TextAlignment.CENTER);
				_delete.setFont(new Font(Constants.BUTTON_FONT,
						Constants.DELETE_FONT_SIZE));
				_delete.setFill(Constants.COLOR_GREEN.darker());
				_delete.addEventHandler(MouseEvent.MOUSE_CLICKED,
						new DeleteClickHandler());

				// Makes a circle around the delete sign
				Ellipse circleBack = new Ellipse(positionX
						+ Constants.DELETE_CIRCLE_BACK_OFFSET_X, positionY
						+ (Constants.COURSE_BOX_HEIGHT * _elapsedTime) / 2,
						Constants.CIRCLE_BACK_RADIUS,
						Constants.CIRCLE_BACK_RADIUS);
				circleBack.setFill(null);
				circleBack.setStroke(Constants.COLOR_GREEN.darker());

				// Add both the display background and text to the dismissPane
				// created in this method
				_dismissPane.getChildren().addAll(_background, _delete,
						circleBack);

				// Show description
				_course.showDescription();
			}
		}
	}

	// Removes the course when this EventHandler is activated
	private class DeleteClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			_course.removeCourseDisplay(_timeTracker);

		}
	}

	// Removes the delete display when this EventHandler is activated
	private class DismissClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			_group.getChildren().remove(_dismissPane);
		}
	}

}
