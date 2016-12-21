package Indy;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * 
 * This class creates the lower calendar portion of the planner. It creates the
 * rectangles and text that make up the graphical display of the calendar and
 * instantiates MouseOverBoxes
 *
 */

public class Calendar {
	private DropShadow _dropShadow;
	private Pane _calendarPane;
	private Searcher _searcher;

	// The constructor creates a drop shadow effect, and calls methods to make
	// rows, columns, and time slots.
	public Calendar(BorderPane root, Searcher searcher, Pane calendarPane) {
		// Stores parameters in instance variables
		_calendarPane = calendarPane;
		_searcher = searcher;

		// Creates drop shadow effect
		_dropShadow = new DropShadow();
		_dropShadow.setOffsetX(Constants.DROP_SHADOW_OFFSET);
		_dropShadow.setOffsetY(Constants.DROP_SHADOW_OFFSET);
		_dropShadow.setSpread(Constants.DROP_SHADOW_SPREAD);
		_dropShadow.setColor(Constants.COLOR_GRAY);

		// make rows
		this.makeRows(_calendarPane);

		// make columns
		this.makeColumns(_calendarPane);

		// make time slots
		this.makeTimeSlots(_calendarPane);

		// makes a button to clear courses
		this.makeClearCoursesButton(_calendarPane);

	}

	// makes rows and adds them to the Pane it takes in as a parameter
	private void makeRows(Pane calendarPane) {
		// Creates the specified number of rows
		for (int i = 0; i < Constants.ROW_TOTAL_NUMBER; i++) {
			// Creates a rectangle with a drop shadow
			Rectangle rectangle = new Rectangle(Constants.ROW_POS_X,
					Constants.ROW_POS_Y + Constants.ROW_SPACING * i,
					Constants.ROW_WIDTH, Constants.ROW_HEIGHT);
			rectangle.setEffect(_dropShadow);

			// rounds corners
			rectangle.setArcWidth(Constants.COURSE_BOX_ARC);
			rectangle.setArcHeight(Constants.COURSE_BOX_ARC);

			// sets color
			rectangle.setFill(Constants.COLOR_ROW);

			// adds to pane
			calendarPane.getChildren().add(rectangle);

			// figures out the hour that corresponds to the row and whether it
			// is morning/afternoon
			int time;
			String morningAfternoon;
			if (i + 9 <= 12) {
				time = i + 9;
				if (i + 9 == 12) {
					morningAfternoon = "PM";
				} else {
					morningAfternoon = "AM";
				}
			} else {
				morningAfternoon = "PM";
				time = i - 3;
			}

			// Creates text to display the number and morning/afternoon, center
			// aligned
			Text courseTime = new Text(Constants.COURSE_TIME_OFFSET_X,
					Constants.ROW_POS_Y + Constants.COURSE_TIME_OFFSET_Y
							+ Constants.ROW_SPACING * i, time
							+ morningAfternoon);
			courseTime.setTextAlignment(TextAlignment.CENTER);
			courseTime.setWrappingWidth(Constants.WRAPPING_WIDTH);

			// Sets font color, fill, rotates it, and adds it to the
			// calenderPane
			courseTime.setFont(new Font(Constants.MAIN_FONT,
					Constants.COURSE_TIME_FONT_SIZE));
			courseTime.setFill(Color.WHITE);
			courseTime.setRotate(Constants.COURSE_TIME_ROTATE);
			calendarPane.getChildren().add(courseTime);

		}
	}

	// makes columns and adds them to the Pane it takes in as a parameter
	private void makeColumns(Pane calendarPane) {
		// Creates the specified number of columns
		for (int i = 0; i < Constants.COL_TOTAL_NUMBER; i++) {
			Rectangle rectangle = new Rectangle(Constants.COL_POS_X
					+ Constants.COL_SPACING * i, Constants.COL_POS_Y,
					Constants.COL_WIDTH, Constants.COL_HEIGHT);

			// round corners
			rectangle.setArcWidth(Constants.COURSE_BOX_ARC);
			rectangle.setArcHeight(Constants.COURSE_BOX_ARC);

			// set color
			rectangle.setFill(Constants.COLOR_COL);

			// set drop shadow
			rectangle.setEffect(_dropShadow);

			// add to pane
			calendarPane.getChildren().add(rectangle);

			// Find the day (and color) that correspond to the column number
			String day = new String();
			Color color = Color.BLACK;
			switch (i) {
			case 0:
				day = "M";
				color = Constants.COLOR_GREEN;
				break;
			case 1:
				day = "T";
				color = Constants.COLOR_ORANGE;
				break;
			case 2:
				day = "W";
				color = Constants.COLOR_YELLOW;
				break;
			case 3:
				day = "R";
				color = Constants.COLOR_RED;
				break;
			case 4:
				day = "F";
				color = Constants.COLOR_TEAL;
				break;
			}

			// Creates text to display the day, center aligned
			Text courseDay = new Text(
					Constants.COL_POS_X + Constants.COL_SPACING * i
							+ Constants.COURSE_DAY_OFFSET_X,
					Constants.COL_POS_Y + Constants.COURSE_DAY_OFFSET_Y, day);
			courseDay.setTextAlignment(TextAlignment.CENTER);
			courseDay.setWrappingWidth(Constants.WRAPPING_WIDTH);

			// Sets font, color, and adds it to the calendarPane
			courseDay.setFont(new Font(Constants.MAIN_FONT,
					Constants.COURSE_DAY_FONT_SIZE));
			courseDay.setFill(color);
			calendarPane.getChildren().add(courseDay);

		}
	}

	// Creates timeslots
	private void makeTimeSlots(Pane calendarPane) {
		// Creates a group for the timeslots and adds it to the calendarPane
		Group timeSlotGroup = new Group();
		calendarPane.getChildren().add(timeSlotGroup);

		// For each column of each row on the calendar, create a timeslot
		for (int i = 0; i < Constants.COL_TOTAL_NUMBER; i++) {
			for (int j = 0; j < Constants.ROW_TOTAL_NUMBER * 2; j++) {
				// create a transparent gray rectangle with blue stroke and add
				// it to the group
				Rectangle slot = new Rectangle(Constants.COL_POS_X
						+ Constants.COL_SPACING * i, Constants.ROW_POS_Y
						+ (Constants.ROW_SPACING) * j / 2, Constants.COL_WIDTH,
						Constants.ROW_HEIGHT / 2);
				slot.setFill(Constants.COLOR_GRAY);
				slot.setOpacity(.1);
				slot.setStroke(Constants.COLOR_BLUE);
				timeSlotGroup.getChildren().add(slot);

				// create a box over the slot that lights up when the mouse is
				// over it
				new MouseOverBox(Constants.COL_POS_X + Constants.COL_SPACING
						* i, Constants.ROW_POS_Y + (Constants.ROW_SPACING) * j
						/ 2, Constants.COL_WIDTH, Constants.ROW_HEIGHT / 2,
						timeSlotGroup, true, _searcher, i);
			}
		}
	}

	// makes a button to clear courses
	public void makeClearCoursesButton(Pane pane) {
		// Create and format background rounded rectangle
		Rectangle background = new Rectangle(Constants.CALENDAR_WIDTH / 2
				- Constants.CLEAR_COURSES_BACKGROUND_WIDTH / 2,
				Constants.CALENDAR_HEIGHT
						+ Constants.CLEAR_COURSES_TEXT_OFFSET_Y
						+ Constants.CLEAR_COURSES_BACKGROUND_OFFSET_Y,
				Constants.CLEAR_COURSES_BACKGROUND_WIDTH,
				Constants.CLEAR_COURSES_BACKGROUND_HEIGHT);
		background.setFill(null);
		background.setStroke(Constants.COLOR_GREEN.darker());
		background.setArcHeight(Constants.CLEAR_COURSES_BACKGROUND_ARC);
		background.setArcWidth(Constants.CLEAR_COURSES_BACKGROUND_ARC);

		// Create and format text
		Text clear = new Text(Constants.CALENDAR_WIDTH / 2
				+ Constants.CLEAR_COURSES_TEXT_OFFSET_X,
				Constants.CALENDAR_HEIGHT
						+ Constants.CLEAR_COURSES_TEXT_OFFSET_Y,
				"CLEAR COURSES");
		clear.setFill(Constants.COLOR_GREEN.darker().darker());
		clear.setFont(Font.font(Constants.MAIN_FONT, FontWeight.BOLD,
				Constants.CLEAR_COURSES_FONT_SIZE));

		// Add to pane
		pane.getChildren().addAll(background, clear);

		// Add clickhandler to clear courses upon click
		clear.addEventHandler(MouseEvent.MOUSE_CLICKED, new ClickHandler());

	}

	// EventHandler, triggered on click, clears courses from calendar
	private class ClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			_searcher.clearCourses();
		}
	}
}
