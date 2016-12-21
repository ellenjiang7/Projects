package Indy;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/**
 * MouseOverBox creates a rectangle that can react when the mouse hovers over
 * it. It is used to create the mouse-over reaction of the rectangles in the
 * calendar and dropdown.
 */
public class MouseOverBox {

	private Rectangle _box;
	private Searcher _searcher;
	private boolean _inCalendar, _timeSelected;
	private int _index;
	private Group _previewGroup;

	// Creates the Rectangle and adds EventHandlers to detect when the mouse
	// enters and exits the node and when it is clicked; adds a ClickHandler
	// depending on whether the Rectangle is in the calendar or dropdown
	public MouseOverBox(double positionX, double positionY, double width,
			double height, Group dropDownGroup, boolean inCalendar,
			Searcher searcher, int index) {
		// store parameters in instance variables
		_searcher = searcher;
		_inCalendar = inCalendar;
		_index = index;

		// create blue a rectangle at the passed-in position with the passed-in
		// dimensions and make it invisible to start
		_box = new Rectangle(positionX, positionY, width, height);
		_box.setFill(Constants.COLOR_BLUE);
		_box.setOpacity(0);

		// Turns blue when mouse enters the node
		_box.setOnMouseEntered(new MouseEnterHandler());

		// Resets to normal color when mouse exits the node
		_box.setOnMouseExited(new MouseExitHandler());

		// Add the box to the group
		dropDownGroup.getChildren().add(_box);

		// If the box is in the calendar
		if (inCalendar) {
			// Add the Eventhandler to search by time
			_box.addEventHandler(MouseEvent.MOUSE_CLICKED,
					new CalendarClickHandler());
		} else {
			_box.addEventHandler(MouseEvent.MOUSE_CLICKED,
					new DropDownClickHandler());

		}
	}

	// Searches by time when this EventHandler is triggered by a mouse click and
	// highlight/select this time in the calendar, if this time is not currently
	// selected/being searched; unselects the time if it is currently selected
	private class CalendarClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if (!_timeSelected) {
				// Figure out row and column from mouse click location
				int column = (int) ((event.getX() - 2 * Constants.COL_POS_X) / Constants.COL_WIDTH);
				int row = (int) ((event.getY() - Constants.ROW_POS_Y) / (Constants.ROW_HEIGHT / 2));

				// Create strings for the day and time, and for morning or
				// afternoon
				String day = "day";
				String hour = "time";
				String minute = "minute";
				String morningAfternoon = " ";

				// Figure out the time and morning/afternoon from the row
				minute = Integer.toString(row % 2 * 30);
				if (minute.equals("0")) {
					minute = "00";
				}

				// Divide the number of rows by two since there are twice as
				// many rows as hours (one row is half an hour on the calendar)
				row /= 2;

				if (row == 3) {
					hour = "12";
					morningAfternoon = "pm";
				} else if (row < 3) {
					hour = Integer.toString(row + 9);
					morningAfternoon = "am";
				} else if (row > 3) {
					hour = Integer.toString(row - 3);
					morningAfternoon = "pm";
				}

				// Create a string to store the time search input
				String input = "DAY: " + day + ", TIME: " + hour;

				// Figure out the input based on the column, given the hour and
				// minute of when the course starts, and morning/afternoon.
				// Search for every combination of class days including the days
				switch (column) {
				case 0:
					day = "M";
					input = "MWF MW M " + hour + ":" + minute
							+ morningAfternoon + " " + hour + ":" + minute;
					break;

				case 1:
					day = "T";
					input = "TTh T " + hour + ":" + minute + morningAfternoon
							+ " " + hour + ":" + minute;
					break;

				case 2:
					day = "W";
					input = "MWF MW WF W " + hour + ":" + minute
							+ morningAfternoon + " " + hour + ":" + minute;
					break;

				case 3:
					day = "Th";
					input = "TTh Th " + hour + ":" + minute + morningAfternoon
							+ " " + hour + ":" + minute;
					break;

				case 4:
					day = "F";
					input = "MWF WF F " + hour + ":" + minute
							+ morningAfternoon + " " + hour + ":" + minute;
					break;
				}

				// search this time search input
				_searcher.handleSearch(input);

				// Set opacity
				_box.setOpacity(Constants.BOX_OPACITY * 2);

				// Indicated that a timeslot is selected
				_timeSelected = true;

				// Set this box as selected
				_searcher.setSelectedBox(MouseOverBox.this);
				// else
			} else {
				// unselect the time
				MouseOverBox.this.unselectTime();

				// slide the results back up
				_searcher.slideUpResults();
			}

		}
	}

	// Makes color blue when this EventHandler is activated, and make a preview
	private class MouseEnterHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			// If the time has not been selected already, make the box opaque
			if (!_timeSelected) {
				_box.setOpacity(Constants.BOX_OPACITY);
			}

			// Add a preview of the course in the calendar
			_previewGroup = new Group();
			_searcher.getDisplayGroup().getChildren().add(_previewGroup);
			if (!_inCalendar) {
				_searcher.getCourseFromIndex(_index).showPreview(_previewGroup);
			}
		}
	}

	// Resets color to original color when this EventHandler is activated and
	// removes the preview by clearing the elements added to the preview group
	private class MouseExitHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if (!_timeSelected) {
				_box.setOpacity(0);
			}

			_previewGroup.getChildren().clear();
		}

	}

	// Searches by time when this EventHandler is triggered by a mouse click
	private class DropDownClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			_searcher.handleDescription(_index);
		}
	}

	// Unselects the time in the calendar
	public void unselectTime() {
		_box.setOpacity(0);
		_timeSelected = false;
	}
}
