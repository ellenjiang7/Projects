package Indy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * 
 * The Course class models a course, containing instance variables regarding its
 * name, number, professor, time, day, etc. It contains methods to figure out
 * some of its own properties (like coordinates) which are called when the
 * course is instantiated, and also contains methods to display itself as well
 * as accessor and mutator methods.
 *
 */

public class Course {
	private final String _name, _number, _professor, _day, _time, _description;
	private HashMap<String, Integer> _contentMap;
	private boolean _selected;
	private Color _color;
	private ArrayList<Double> _coordinates;
	private Group _displayGroup, _thisDisplay, _previewGroup;
	private double _elapsedTime;
	private int _rowsToTrack, _index;
	private boolean[][] _timeTracker;
	private Searcher _searcher;
	private ArrayList<Course> _coursesAdded;
	private FileProcessor _fileProcessor;

	public Course(String name, String number, String professor, String day,
			String time, String description, HashMap<String, Integer> contentMap) {
		// Store and set (or instantiate) instance variables
		_name = name;
		_number = number;
		_professor = professor;
		_day = day;
		_time = time;
		_description = description;
		_contentMap = contentMap;
		_color = Constants.COLOR_BLUE;
		_selected = false;
		_previewGroup = new Group();

		// Figures out where it should be displayed
		this.figureOutDisplayCoordinates();

		// Figures out which rows it encompasses when displayed in the calendar
		this.figureOutRowsToTrack();

		// Add a few additional searchable times to the ContentMap so a search
		// for any time within a course's time span to come up)
		this.addToContent();

		// Sets its color based on department
		this.setColor();
	}

	// Figures out which rows it encompasses when displayed in the calendar
	private void figureOutRowsToTrack() {
		// The elapsed time is the number of hours (could be a decimal like
		// 2.5). We can figure out the rows to track by dividing it by .5 since
		// each row is only .5 hours.
		_rowsToTrack = (int) (_elapsedTime / .5);

		// If the time went over the half hour (like if a class ended at 3:50),
		// add an additional row to track
		if (_elapsedTime % .5 != 0) {
			_rowsToTrack++;
		}
	}

	// Add a few additional searchable times to the ContentMap so a search for
	// any time within a course's time span to come up)
	private void addToContent() {
		// Split the time at the hyphen to separate out the start time
		String[] times = _time.split("-");
		String startTime = times[0];

		// Split the start time at the colon into hour and minute
		String[] startNums = startTime.split(":");
		int hour = Integer.parseInt(startNums[0]);
		int minute = Integer.parseInt(startNums[1].substring(0, 2));

		// For each time row the course occupies
		for (int i = 0; i < _rowsToTrack; i++) {
			// Put the time in the ContentMap
			_contentMap.put(hour + ":" + minute, 1);

			// Add a half-hour to the time
			minute += 30;
			if (minute >= 60) {
				minute -= 60;
				hour += 1;
			}
		}
	}

	// Sets the color of the course according to the department
	private void setColor() {
		String[] split = _number.split(" ");
		switch (split[0]) {
		case "CSCI":
			_color = Constants.COLOR_GREEN;
			break;
		case "VISA":
			_color = Constants.COLOR_BLUE;
			break;
		case "MATH":
			_color = Constants.COLOR_ORANGE;
			break;
		case "HIAA":
			_color = Constants.COLOR_YELLOW;
			break;
		case "LITR":
			_color = Constants.COLOR_BROWN;
			break;
		case "PHIL":
			_color = Constants.COLOR_TEAL;
			break;
		}

	}

	// Figures out where it should be displayed
	private void figureOutDisplayCoordinates() {
		// Split the days string into an array of chars (so M, W, F, etc. will
		// be separated)
		char[] days = _day.toCharArray();

		// Instantiate an ArrayList of doubles
		_coordinates = new ArrayList<Double>();

		// Split time at hyphen into start and end time
		String[] times = _time.split("-");

		// Split start time at colon into hour and minute
		String startTime = times[0];
		String[] startNums = startTime.split(":");
		int startHour = Integer.parseInt(startNums[0]);
		int startMinute = Integer.parseInt(startNums[1].substring(0, 2));

		// Split end time at colon into hour and minute
		String endTime = times[1];
		String[] endNums = endTime.split(":");
		int endHour = Integer.parseInt(endNums[0]);
		int endMinute = Integer.parseInt(endNums[1].substring(0, 2));

		// Calculate the amount of elapsed time
		this.calculateElapsed(startHour, startMinute, endHour, endMinute);

		// Determine the row from the hour
		double row = 0;
		if (startHour == 12) {
			row = 3;
		} else if (startHour >= 9 && startHour < 12) {
			row = startHour - 9;
		} else if (startHour < 9) {
			row = startHour + 3;
		}

		// Add on the time in minutes converted to half-hour rows
		row += Double.parseDouble(startNums[1].substring(0, 2)) / 60;
		// if(Double.parseDouble(startNums[1].substring(0, 2)) > 0){

		// Add the row as the first element of the coordinates
		_coordinates.add(row);

		// Sort the days array to make ready for binarySearch
		Arrays.sort(days);

		// If the days array contains the given letter representing a day of the
		// week, add corresponding row to the coordinates
		if (Arrays.binarySearch(days, 'M') >= 0) {
			_coordinates.add(0.0);
		}
		if (Arrays.binarySearch(days, 'T') >= 0) {
			_coordinates.add(1.0);
		}
		if (Arrays.binarySearch(days, 'W') >= 0) {
			_coordinates.add(2.0);
		}
		if (Arrays.binarySearch(days, 'h') >= 0) {
			_coordinates.add(3.0);
		}
		if (Arrays.binarySearch(days, 'F') >= 0) {
			_coordinates.add(4.0);
		}
	}

	// Calculate the elapsed time, given a start hour/minute and end hour/minute
	private void calculateElapsed(int startHour, int startMinute, int endHour,
			int endMinute) {
		// Calculate elapsed hours
		int hours = endHour - startHour;

		// If the result is negative, add 12 (with an 11a-1p class, 1-11 would
		// be -10, but what we really want is 2 hours, so we can add 12)
		if (hours < 0) {
			hours += 12;
		}

		// Calculate the elapsed minutes
		int minutes = endMinute - startMinute;

		// If the result is negative, carry the 1 (For instance, with a
		// 1:30-2:20 class, 20-30 would yield 1 hour, -10 minutes; so we would
		// add 60, yielding 50, and subtract 1 from the hour, yielding 0 hours
		// 50 minutes
		if (minutes < 0) {
			minutes += 60;
			hours -= 1;
		}

		// Store the result in the elapsed time instance variable
		_elapsedTime = (double) hours + (double) (minutes) / 60;
	}

	// accessor method for course name
	public String getName() {
		return _name;
	}

	// accessor method for course number
	public String getNumber() {
		return _number;
	}

	// accessor method for course professor
	public String getProfessor() {
		return _professor;
	}

	// accessor method for day
	public String getDay() {
		return _day;
	}

	// accessor method for course time
	public String getTime() {
		return _time;
	}

	// accessor method for course description
	public String getDescription() {
		return _description;
	}

	// accessor method for course content
	public HashMap<String, Integer> getContentMap() {
		return _contentMap;
	}

	// accessor method for whether course is selected
	public boolean getSelected() {
		return _selected;
	}

	// mutator method for setting whether course is selected
	public void setSelected(boolean selected) {
		_selected = selected;
	}

	// accessor method for whether course's time coordinates for the calendar
	public ArrayList<Double> getCoordinates() {
		return _coordinates;
	}

	// accessor method for the course color
	public Color getColor() {
		return _color;
	}

	// shows the preview, adding it to a group passed in as a parameter
	public void showPreview(Group group) {
		// Store group in instance variable (so the hidePreview method can
		// access it)
		_previewGroup = group;

		// For each coordinate/time
		for (int i = 0; i < _coordinates.size() - 1; i++) {
			// Calculate the position
			double positionX = Constants.COL_SPACING * _coordinates.get(i + 1)
					+ Constants.COL_POS_X;
			double positionY = Constants.ROW_SPACING * _coordinates.get(0)
					+ Constants.ROW_POS_Y;

			// Create a transparent preview rectangle and add it to the preview
			// group
			Rectangle background = new Rectangle(positionX, positionY,
					Constants.COURSE_BOX_WIDTH, Constants.COURSE_BOX_HEIGHT
							* _elapsedTime);
			background.setFill(this.getColor());
			background.setOpacity(.3);
			_previewGroup.getChildren().add(background);
		}
	}

	// Add a course display to the calendar
	public void addCourseDisplay(Group group, boolean[][] timeTracker,
			Searcher searcher) {
		// Store parameters in instance variables
		_displayGroup = group;
		_timeTracker = timeTracker;
		_searcher = searcher;

		// Indicate that course is selected
		this.setSelected(true);

		// Add the course to the coursesAdded ArrayList and update the file (to
		// save the session)
		_coursesAdded.add(this);
		this.updateFile();

		// Instantiate a group for this course display and add it to the display
		// group
		_thisDisplay = new Group();
		_displayGroup.getChildren().add(_thisDisplay);

		// Display the course in each time slot and updates time tracker that
		// the time slots for the course's coordinates
		// are now filled
		ArrayList<Double> coordinates = this.getCoordinates();
		for (int i = 0; i < coordinates.size() - 1; i++) {
			this.displayCourse(coordinates.get(i + 1), coordinates.get(0));
			int x = (int) coordinates.get(i + 1).doubleValue();
			int y = (int) (coordinates.get(0).doubleValue() * 2);
			for (int j = 0; j < _rowsToTrack; j++) {
				_timeTracker[x][y + j] = true;
			}
		}

		// Refresh the signs in the drop down
		_searcher.refreshSigns();

	}

	// displays the course in the calendar
	private void displayCourse(double column, double row) {
		// Display background rectangle at the correct position, setting
		// dimensions
		double positionX = Constants.COL_SPACING * column + Constants.COL_POS_X;
		double positionY = Constants.ROW_SPACING * row + Constants.ROW_POS_Y;
		Rectangle background = new Rectangle(positionX, positionY,
				Constants.COURSE_BOX_WIDTH, Constants.COURSE_BOX_HEIGHT
						* _elapsedTime);
		background.setFill(this.getColor());
		background.setOpacity(Constants.COURSE_DISPLAY_OPACITY);

		// Create a second background rectangle for the stroke (I wanted the
		// stroke to have a full opacity while the fill rectangle is
		// transparent)
		Rectangle backgroundStroke = new Rectangle(positionX, positionY,
				Constants.COURSE_BOX_WIDTH, Constants.COURSE_BOX_HEIGHT
						* _elapsedTime);
		backgroundStroke.setStroke(this.getColor());
		backgroundStroke.setFill(null);

		// Add rectangles to display
		_thisDisplay.getChildren().addAll(background, backgroundStroke);

		// Create the text
		new MouseOverText(positionX, positionY
				+ (Constants.COURSE_BOX_HEIGHT * _elapsedTime) / 2
				+ Constants.COURSE_DISPLAY_TEXT_OFFSET, this.getNumber(), this
				.getColor().darker(), _thisDisplay, this, _elapsedTime);

		// Make the display scale up vertically
		ScaleTransition st = new ScaleTransition(
				Duration.seconds(Constants.GROW_SHRINK_SPEED), _thisDisplay);
		st.setFromY(0);
		st.setToY(1);
		st.play();
	}

	// Removes course display from calendar
	public void removeCourseDisplay(boolean[][] timeTracker) {
		// Scales the course down vertically
		ScaleTransition st = new ScaleTransition(
				Duration.seconds(Constants.GROW_SHRINK_SPEED), _thisDisplay);
		st.setFromY(1);
		st.setToY(0);
		st.play();

		// Waits .3 seconds before removing the course graphically
		KeyFrame kf = new KeyFrame(Duration.seconds(.3), new TimeHandler());
		Timeline timeline = new Timeline(kf);
		timeline.setCycleCount(1);
		timeline.play();

		// Updates time tracker that the time slots for the course's coordinates
		// are now empty
		ArrayList<Double> coordinates = this.getCoordinates();
		for (int i = 0; i < coordinates.size() - 1; i++) {
			int x = (int) coordinates.get(i + 1).doubleValue();
			int y = (int) (coordinates.get(0).doubleValue() * 2);

			for (int j = 0; j < _rowsToTrack; j++) {
				_timeTracker[x][y + j] = false;
			}
		}

		// Indicate the course is no longer selected
		this.setSelected(false);

		// Removes course from tracker arraylist
		_coursesAdded.remove(this);

		// Updates the file (autosaves)
		this.updateFile();

		// Hide the preview
		_previewGroup.getChildren().clear();

		// Refresh the signs in the dropdown
		_searcher.refreshSigns();
	}

	// updates the file to match the courses currently added to the calendar
	private void updateFile() {
		try {
			_fileProcessor.saveToFile(_coursesAdded);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// mutator method for coursesAdded ArrayList
	public void setCoursesAdded(ArrayList<Course> coursesAdded) {
		_coursesAdded = coursesAdded;
	}

	// mutator method for FileProcessor
	public void setFileProcessor(FileProcessor fileProcessor) {
		_fileProcessor = fileProcessor;
	}

	// Accessor method for the number of rows to track
	public int getRowsToTrack() {
		return _rowsToTrack;
	}

	// TimeHandler for timeline, tells what to do at the end of each keyframe
	private class TimeHandler implements EventHandler<ActionEvent> {

		// Handles what happens at the end of each KeyFrame
		@Override
		public void handle(ActionEvent event) {
			_displayGroup.getChildren().remove(_thisDisplay);

		}
	}

	// sets course index
	public void setIndex(int index) {
		_index = index;
	}

	// shows the course description
	public void showDescription() {
		// sets the displayIndex array with the index of this course as the
		// first element with three other placeholder indices
		int[] results = { _index, 1, 2, 3 };
		_searcher.setDisplayIndex(results);

		// Display the results
		_searcher.displayResults();

		// Show the description of the first index (which is now the index for
		// this course)
		_searcher.showDescription(0);
	}
}
