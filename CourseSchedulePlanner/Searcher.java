package Indy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * The Searcher class creates top half of the course planner: the searchbar,
 * dropdown,and ImageView of the books. It takes care of searching, showing the
 * dropdown and description, and transitions associated with its components.
 * Searcher will also display a tutorial if there are no courses added to the
 * calendar when the app starts
 */

public class Searcher {

	private TextField _searchBox;
	private int[] _displayIndex;
	private Text[] _plus, _name, _number, _time;
	private Ellipse[] _circleBack;
	private static final String[] EXPECTED_HEADER = new String[] { "Name",
			"Number", "Professor", "Day", "Time", "Description" };
	private ArrayList<Course> _courses, _coursesAdded;
	private Pane _searcherPane;
	private Group _courseDisplayGroup, _textGroup, _descriptionGroup,
			_dropDownGroup, _previewGroup, _tutorialGroup;
	private boolean[][] _timeTracker;
	private boolean _descriptionShown, _timeSelected, _slidDown,
			_flashAddInstructions, _flashTimeInstructions;
	private MouseOverBox _selectedBox;
	private HashMap<String, Course> _courseMap;
	private FileProcessor _fileProcessor;

	// The constructor method reads the courses cvs file into an ArrayList,
	// sets the books background image, makes the dropdown and searchbox, clear
	// button, and flashes the start text
	public Searcher(Pane searcherPane, Group courseDisplayGroup,
			boolean[][] timeTracker, Group tutorialGroup) throws IOException {
		// Store parameters in instance variables
		_searcherPane = searcherPane;
		_courseDisplayGroup = courseDisplayGroup;
		_timeTracker = timeTracker;
		_tutorialGroup = tutorialGroup;
		_flashAddInstructions = true;
		_flashTimeInstructions = true;

		// Instantiate arraylist to track courses added
		_coursesAdded = new ArrayList<Course>();

		// Instantiate groups
		_previewGroup = new Group();
		_courseDisplayGroup.getChildren().add(_previewGroup);

		// Instantiate an array of ints to keep track of what is in the display
		_displayIndex = new int[Constants.DROPDOWN_ITEMS];

		// Creates a reader and file, and has the reader read the file into an
		// ArrayList
		CSVReader reader = new CSVReader();
		File file = adjustFilepath("courses.csv");
		_courses = reader.read(file, EXPECTED_HEADER);

		// Creates a HashMap with the course number as the key and the course as
		// the value
		_courseMap = new HashMap<String, Course>();
		for (int i = 0; i < _courses.size(); i++) {
			// Put each course in
			_courseMap.put(_courses.get(i).getNumber(), _courses.get(i));
			// Let each course know about the _coursesAdded ArrayList, so they
			// can add themselves to it
			_courses.get(i).setCoursesAdded(_coursesAdded);

			// Let each course know their index in the ArrayList
			_courses.get(i).setIndex(i);
		}

		// Insert books image, set vertical dimensions, and add to searcherPane
		ImageView imageView = new ImageView(Constants.IMAGE_BOOK);
		imageView.setFitHeight(Constants.TOP_HEIGHT);
		_searcherPane.getChildren().add(imageView);

		// Makes drop-down
		this.makeDropDown();

		// Makes searchbar
		this.makeSearchBar();

		// Load courses from previous session
		this.loadCoursesAdded();

		// Flash the welcome screen for two seconds
		this.flashInstructions(Constants.IMAGE_WELCOME,
				Constants.WELCOME_WAIT_TIME, Constants.WELCOME_DISPLAY_TIME);

		// If there are not any courses added to the calendar when the app
		// starts
		if (_coursesAdded.isEmpty()) {
			// Wait 6 seconds and flash the let's get started screen for four
			// seconds
			this.flashInstructions(Constants.IMAGE_GET_STARTED,
					Constants.GET_STARTED_WAIT_TIME,
					Constants.GET_STARTED_DISPLAY_TIME);
			// else signal that the other instructions should not be shown
			// either
		} else {
			_flashAddInstructions = false;
			_flashTimeInstructions = false;
		}
	}

	// Private inner methods used by constructor

	// flashes instructions
	private void flashInstructions(String link, double waitTime, double showTime) {
		// Creates an ImageView of the link that is passed in as a parameter,
		// make the ImageView the size of the screen, and add it to the
		// tutorialGroup
		ImageView instructions = new ImageView(link);
		instructions.setFitHeight(Constants.SCREEN_HEIGHT);
		instructions.setFitWidth(Constants.SCREEN_WIDTH);
		_tutorialGroup.getChildren().add(instructions);

		// Make the ImageView wait a specified number of seconds before fading
		// in
		PauseTransition pt = new PauseTransition(Duration.seconds(waitTime));
		FadeTransition ft = new FadeTransition(Duration.seconds(2),
				instructions);
		ft.setFromValue(0);
		ft.setToValue(Constants.TUTORIAL_OPACITY);

		// Show the ImageView for a specified number of seconds before fading
		// back out
		PauseTransition pt2 = new PauseTransition(Duration.seconds(showTime));
		FadeTransition ft2 = new FadeTransition(Duration.seconds(2),
				instructions);
		ft2.setToValue(0);

		// Move the ImageView out of the way
		TranslateTransition tt = new TranslateTransition(Duration.seconds(.1),
				instructions);
		tt.setToX(Constants.SCREEN_WIDTH);

		// Play the above in a sequential transition
		SequentialTransition st = new SequentialTransition(pt, ft, pt2, ft2, tt);
		st.play();

	}

	// Loads an ArrayList of courses from the coursesSaved text file
	private void loadCoursesAdded() throws IOException {
		// Instantiate a FileProcessor
		_fileProcessor = new FileProcessor();

		// Let each course know about the FileProcessor
		for (int i = 0; i < _courses.size(); i++) {
			_courses.get(i).setFileProcessor(_fileProcessor);
		}

		// Load the course numbers from the file into an ArrayList of Strings
		ArrayList<String> courseNumbers = _fileProcessor.loadFile();

		// Create a temporary ArrayList, and store the added courses in it
		ArrayList<Course> tempCoursesList = new ArrayList<Course>();
		for (int i = 0; i < courseNumbers.size(); i++) {
			tempCoursesList.add(_courseMap.get(courseNumbers.get(i)));
		}

		// Add each of theses courses to the calendar (they will be added to the
		// ArrayList stored in _coursesAdded within the addCourseDisplay method)
		for (int i = 0; i < tempCoursesList.size(); i++) {
			tempCoursesList.get(i).addCourseDisplay(_courseDisplayGroup,
					_timeTracker, this);
		}

	}

	// Makes the drop-down
	private void makeDropDown() {
		// Creates a new group for the drop down, and sets its location above
		// the screen so it is hidden when the app is launched
		_dropDownGroup = new Group();
		_dropDownGroup.setTranslateY(-Constants.TOP_HEIGHT);
		_searcherPane.getChildren().add(_dropDownGroup);

		// Create the background rectangle for the dropdown, setting dimensions,
		// color, rounded corners, and opacity, and adds to the group
		Rectangle dropDown = new Rectangle(Constants.DROPDOWN_WIDTH,
				Constants.DROPDOWN_HEIGHT);
		dropDown.setFill(Color.WHITE);
		dropDown.setArcHeight(Constants.COURSE_BOX_ARC);
		dropDown.setArcWidth(Constants.COURSE_BOX_ARC);
		dropDown.setOpacity(Constants.DROPDOWN_OPACITY);
		_dropDownGroup.getChildren().add(dropDown);

		// Make slide up button
		Rectangle slideUp = new Rectangle(Constants.SLIDE_UP_BUTTON_X,
				Constants.SLIDE_UP_BUTTON_Y, Constants.SLIDE_UP_BUTTON_WIDTH,
				Constants.SLIDE_UP_BUTTON_HEIGHT);
		slideUp.setFill(Constants.COLOR_BLUE.darker());
		slideUp.setOpacity(Constants.SLIDE_UP_BUTTON_OPACITY);
		slideUp.setArcHeight(Constants.SLIDE_UP_BUTTON_ARC);
		slideUp.setArcWidth(Constants.SLIDE_UP_BUTTON_ARC);
		slideUp.addEventHandler(MouseEvent.MOUSE_CLICKED, new SlideUpHandler());
		_dropDownGroup.getChildren().add(slideUp);

		// Create arrays for the items in the dropdown
		_number = new Text[Constants.DROPDOWN_ITEMS];
		_name = new Text[Constants.DROPDOWN_ITEMS];
		_time = new Text[Constants.DROPDOWN_ITEMS];
		_circleBack = new Ellipse[Constants.DROPDOWN_ITEMS];
		_plus = new Text[Constants.DROPDOWN_ITEMS];

		// Makes group for text (so the text can be made to fade in and out) and
		// adds to group
		_textGroup = new Group();
		_dropDownGroup.getChildren().add(_textGroup);

		// Make text with course number, course name, and course time, add to
		// the group, and create a box that lights up when moused over
		for (int i = 0; i < Constants.DROPDOWN_ITEMS; i++) {
			_number[i] = new Text(Constants.DROPDOWN_TEXT_X,
					Constants.DROPDOWN_TEXT_NUMBER_Y
							+ Constants.DROPDOWN_TEXT_OFFSET * i, "");
			_number[i].setFont(Font.font(Constants.MAIN_FONT, FontWeight.BOLD,
					Constants.DROPDOWN_TEXT_NUMBER_FONT_SIZE));
			_name[i] = new Text(Constants.DROPDOWN_TEXT_X,
					Constants.DROPDOWN_TEXT_NAME_Y
							+ Constants.DROPDOWN_TEXT_OFFSET * i, "");
			_name[i].setFont(Font.font(Constants.MAIN_FONT,
					Constants.DROPDOWN_TEXT_NAME_FONT_SIZE));

			_name[i].setWrappingWidth(Constants.DROPDOWN_WIDTH
					- Constants.DROPDOWN_TEXT_X);
			_time[i] = new Text(Constants.DROPDOWN_TEXT_X
					+ Constants.DROPDOWN_TEXT_TIME_X,
					Constants.DROPDOWN_TEXT_NUMBER_Y
							+ Constants.DROPDOWN_TEXT_OFFSET * i, "");
			_time[i].setFill(Constants.COLOR_BLUE.darker().darker());
			_time[i].setFont(Font.font(Constants.MAIN_FONT,
					Constants.DROPDOWN_TEXT_TIME_FONT_SIZE));

			// Adds the number, name, and time to the group
			_textGroup.getChildren().addAll(_number[i], _name[i], _time[i]);

			// Creates a box that lights up when the mouse hovers over it
			new MouseOverBox(0, Constants.DROPDOWN_MOUSEOVER_OFFSET
					+ Constants.DROPDOWN_TEXT_OFFSET * i,
					Constants.SCREEN_WIDTH, (Constants.DROPDOWN_TEXT_OFFSET),
					_dropDownGroup, false, this, i);

		}

		// Make a add button for each course in the dropdown, stored in an array
		for (int i = 0; i < Constants.DROPDOWN_ITEMS; i++) {
			// Create the plus text
			_plus[i] = new Text(Constants.DROPDOWN_PLUS_X,
					Constants.DROPDOWN_PLUS_Y + Constants.DROPDOWN_TEXT_OFFSET
							* i, "+");
			_plus[i].setTextAlignment(TextAlignment.CENTER);
			_plus[i].setWrappingWidth(Constants.WRAPPING_WIDTH);
			_plus[i].setFont(new Font(Constants.BUTTON_FONT,
					Constants.PLUS_BUTTON_FONT_SIZE));
			_plus[i].setFill(Constants.COLOR_GREEN.darker());
			_plus[i].addEventHandler(MouseEvent.MOUSE_CLICKED,
					new PlusClickHandler());

			// create the circle around the plus sign
			_circleBack[i] = new Ellipse(Constants.DROPDOWN_PLUS_X
					+ Constants.CIRCLE_BACK_OFFSET_X, Constants.DROPDOWN_PLUS_Y
					+ Constants.DROPDOWN_TEXT_OFFSET * i
					+ Constants.CIRCLE_BACK_OFFSET_Y,
					Constants.CIRCLE_BACK_RADIUS, Constants.CIRCLE_BACK_RADIUS);
			_circleBack[i].setFill(null);
			_circleBack[i].setStroke(Constants.COLOR_GREEN.darker());

			// add the plus and circle to the group
			_dropDownGroup.getChildren().addAll(_circleBack[i], _plus[i]);
		}

		// Create a group for the description
		_descriptionGroup = new Group();
		_dropDownGroup.getChildren().add(_descriptionGroup);
	}

	// Makes search bar (search box and clear bar)
	private void makeSearchBar() {
		// Makes search box, adds a ClickHandler so hitting enter triggers a
		// search, and adds it to the searcherPane
		_searchBox = new TextField();
		_searchBox.setPrefColumnCount(Constants.SEARCH_BOX_PREF_COLUMN);
		_searchBox.setPromptText(Constants.SEARCH_BOX_PROMPT);
		_searchBox.setFocusTraversable(false);
		_searchBox.setOnKeyPressed(new KeyHandler());
		_searcherPane.getChildren().add(_searchBox);

		// Make x button to clear search
		Text clear = new Text(Constants.DROPDOWN_WIDTH
				+ Constants.CLEAR_BUTTON_OFFSET_X,
				Constants.CLEAR_BUTTON_OFFSET_Y, "+");
		clear.setRotate(Constants.CLEAR_BUTTON_ROTATE);
		clear.setOpacity(Constants.CLEAR_BUTTON_OPACITY);
		clear.setFont(new Font(Constants.BUTTON_FONT,
				Constants.CLEAR_BUTTON_FONT_SIZE));
		clear.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new ClearSearchHandler());
		_searcherPane.getChildren().add(clear);

	}

	// Slides the results up with a TranslateTransition
	public void slideUpResults() {
		TranslateTransition tt = new TranslateTransition(
				Duration.seconds(Constants.SLIDE_UP_TIME), _dropDownGroup);
		tt.setToY(-Constants.TOP_HEIGHT);
		tt.play();

		// clear the preview
		_previewGroup.getChildren().clear();

		// Unselect the time on the calendar, if selected
		if (_timeSelected) {
			_timeSelected = false;
			_selectedBox.unselectTime();
		}

		// signal that the results have slid up
		_slidDown = false;
	}

	// Slides the results down with a TranslateTransition
	private void slideDownResults() {
		TranslateTransition tt2 = new TranslateTransition(
				Duration.seconds(Constants.SLIDE_DOWN_TIME), _dropDownGroup);
		tt2.setToY(0);
		tt2.play();

		// signal that the results have slid down
		_slidDown = true;
	}

	// Flashes the text in the display with a FadeTransition (used to signal
	// that search results have changed)
	private void flash(Group group) {
		FadeTransition ft1 = new FadeTransition(
				Duration.seconds(Constants.FLASH_REFRESH_TIME), group);
		ft1.setFromValue(1);

		ft1.setToValue(0);
		FadeTransition ft2 = new FadeTransition(
				Duration.seconds(Constants.FLASH_REFRESH_TIME), group);
		ft1.setFromValue(0);

		ft1.setToValue(1);
		SequentialTransition st = new SequentialTransition(ft1, ft2);
		st.play();
	}

	// Adjusts a filepath passed in as a parameter, and creates and returns a
	// file
	private File adjustFilepath(String filepath) {
		String[] path = System.getProperty("user.dir").split("/");
		File file;
		// if you're running from the cs015 directory
		if (path[path.length - 1].equals("cs015")) {
			file = new File("./Indy/" + filepath);
			// else if you're running from the Indy directory
		} else if (path[path.length - 1].equals("Indy")) {
			file = new File(filepath);
			// else it won't work.
		} else {
			file = null;
		}
		return file;
	}

	// EventHandlers

	// EventHandler that gets added to the "X" button at the right of the
	// searchbox. Clears the text and slides the results up.
	private class ClearSearchHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			Searcher.this.slideUpResults();
			_searchBox.setText("");
		}

	}

	// EventHandler that gets added to the slide up button at the bottom of the
	// dropdown. Slides the results up.
	private class SlideUpHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			Searcher.this.slideUpResults();
		}

	}

	// EventHandler that gets added to the "+/-" buttons. Adds or removes a
	// course.
	private class PlusClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			// Flash the instructions if necessary
			if (_flashTimeInstructions) {
				_flashTimeInstructions = false;
				Searcher.this.flashInstructions(
						Constants.IMAGE_CLICK_TO_SEARCH,
						Constants.CLICK_TO_SEARCH_WAIT_TIME,
						Constants.CLICK_TO_SEARCH_DISPLAY_TIME);

			}

			// Calculate the row clicked from the mouse click y
			int row = (int) ((event.getY() - Constants.DROPDOWN_PLUS_Y / 2) / Constants.DROPDOWN_TEXT_OFFSET);

			// If the button is not "!" indicating a time conflict for the
			// course
			if (_plus[row].getText() != "!") {
				// Get the course that is currently being displayed in this row
				Course course = _courses.get(_displayIndex[row]);

				// If the course is not currently selected/displayed, display
				// the course
				if (!course.getSelected()) {
					course.addCourseDisplay(_courseDisplayGroup, _timeTracker,
							Searcher.this);

					// else remove the course
				} else {
					course.removeCourseDisplay(_timeTracker);
				}
			}
		}
	}

	// Clears displayed courses from calendar
	public void clearCourses() {
		// Calculate the size of the list first since the list will shrink as
		// the courses are removed
		int listSize = _coursesAdded.size();
		for (int i = 0; i < listSize; i++) {
			_coursesAdded.get(0).removeCourseDisplay(_timeTracker);
		}
		_coursesAdded.clear();

	}

	// EventHandler that gets added to the description and makes the description
	// disappear when clicked
	private class DescriptionClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			Searcher.this.hideDescription();
		}
	}

	// EventHandler that makes the searchbox respond to enter and backspace
	// keystrokes
	private class KeyHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			// if the enter key was pressed
			if (event.getCode() == KeyCode.ENTER) {
				// Search the text in the textbox
				Searcher.this.handleSearch(_searchBox.getText());

				if (_flashAddInstructions) {
					_flashAddInstructions = false;
					Searcher.this.flashInstructions(Constants.IMAGE_ADD_COURSE,
							.3, 2);

				}
				// if the backspace key is hit
			} else if (event.getCode() == KeyCode.BACK_SPACE) {
				// if the search box is empty
				if (_searchBox.getText() == null
						|| _searchBox.getText().isEmpty()) {
					// Make the results slide up
					Searcher.this.slideUpResults();
				}
			}
		}
	}

	// Refreshes the signs of the +/- buttons in the dropdown to correspond to
	// the current state of courses being displayed
	public void refreshSigns() {
		// For each +/- button
		for (int i = 0; i < _plus.length; i++) {
			// If the course in the row is selected, set button as remove/"-"
			if (_courses.get(_displayIndex[i]).getSelected()) {
				_plus[i].setText("-");
				_plus[i].setFill(Constants.COLOR_GREEN.darker());
				_circleBack[i].setOpacity(1);
				// If the course in the row has a conflict with a selected
				// course, set button as conflict/"!"
			} else if (Searcher.this.isFilled(_courses.get(_displayIndex[i]))) {
				_plus[i].setText("!");
				_plus[i].setFill(Constants.COLOR_GRAY);
				_circleBack[i].setOpacity(0);
				// If the course in the row is not selected, set button as
				// add/"+"
			} else {
				_plus[i].setText("+");
				_circleBack[i].setOpacity(1);
				_plus[i].setFill(Constants.COLOR_GREEN.darker());
			}
		}
	}

	// calls a search for the String that is passed in as a parameter and
	// displays results
	public void handleSearch(String input) {
		// Calculates the matchstrengths of the courses according to the search
		// input
		List<Integer> matchStrengths = Searcher.this
				.calculateMatchStrengths(input);

		// Sorts the matchstrengths list using merge sort
		List<Integer> sortedMatchStrengths = Searcher.this
				.mergeSort(matchStrengths);

		// Sets the displayIndex array (which tracks which courses are displayed
		// in the dropdown) based on the matchStrengths
		this.setDisplayIndex(matchStrengths, sortedMatchStrengths);

		// Display the results
		this.displayResults();

		// if a time is selected on the calendar, unselect it
		if (_timeSelected) {
			_timeSelected = false;
			_selectedBox.unselectTime();
		}

	}

	// Set the displayIndex array (which tracks which courses are displayed
	// in the dropdown) based on the matchStrengths
	private void setDisplayIndex(List<Integer> matchStrengths,
			List<Integer> sortedMatchStrengths) {
		// Reset the display index
		for (int i = 0; i < Constants.DROPDOWN_ITEMS; i++) {
			_displayIndex[i] = 1000;
		}

		// This array can be sorted and used for binary search so that the array
		// of display indices does not need to get sorted/scrambled
		int[] tempIndex = { 1000, 1000, 1000, 1000 };

		// For each of the indices of the display (there are 4 in total)
		for (int i = 0; i < Constants.DROPDOWN_ITEMS; i++) {
			// For each element in the matchStrength
			for (int j = 0; j < sortedMatchStrengths.size(); j++) {
				// Sort the array to prep for binarySearch method
				Arrays.sort(tempIndex);
				// If this index's matchstrength is i'th element of the sorted
				// matchStrength, and this index is not already stored in the
				// displayIndex/tempIndex, store it in the
				// displayIndex/tempIndex arrays; exit loop when condition is
				// satified
				if (sortedMatchStrengths.get(i) == matchStrengths.get(j)
						&& Arrays.binarySearch(tempIndex, j) < 0) {
					_displayIndex[i] = j;
					tempIndex[i] = j;
					break;
				}
			}

		}
	}

	// Calculates a list of matchstrengths of each course from the input string
	// passed in
	public List<Integer> calculateMatchStrengths(String input) {
		// Instantiate a list of match
		List<Integer> matchStrengths = new ArrayList<Integer>();

		// Split the input into separate strings
		String[] inputs = input.split(" ");

		// For each inputed word
		for (int i = 0; i < inputs.length; i++) {
			// Make a boolean to keep track of whether a char is made
			// lowercase
			boolean lowered = false;

			// Convert input string into an array of chars
			char[] letters = inputs[i].toCharArray();
			// For each inputted letter
			for (int j = 0; j < letters.length; j++) {
				// If the char is uppercase
				if (Character.isUpperCase(letters[j])) {
					// make the letter lowercase
					letters[j] = Character.toLowerCase(letters[j]);

					// Signal that a character has been made lowercase
					lowered = true;
				}
			}
			// If a char was made lowercase
			if (lowered) {
				// Store the lower-case version of the char array in the input
				// string
				inputs[i] = new String(letters);
			}
		}

		// For every course stored in the ArrayList
		for (int i = 0; i < _courses.size(); i++) {
			// Initialize an int to keep track of the matchstrength
			int matchStrength = 0;

			// If string input is the exact course number of this course,
			// increase the matchstrength
			if (input.equals(_courses.get(i).getNumber())) {
				matchStrength++;
			}

			// For every word in the input string
			for (int j = 0; j < inputs.length; j++) {
				// If the word is in the content map of the course (which
				// contains all associated words), increase the matchstrength
				if (_courses.get(i).getContentMap().get(inputs[j]) != null) {
					matchStrength++;
				}
			}
			// Add the matchStrength to the list of matchStrengths (the order of
			// the list will be the same as the order of the courses
			matchStrengths.add(matchStrength);
		}
		return matchStrengths;
	}

	// Sorts the matchStrengths in descending order (we want the courses with
	// the highest matchStrengths (i.e. the best matches) at the front of the
	// list to be displayed!)
	private List<Integer> mergeSort(List<Integer> list) {
		// If the list is a single item, simply return it
		if (list.size() == 1) {
			return list;
		}
		// The middle of the list is half its total size
		int middle = list.size() / 2;

		// Merge sort the left and right halves of the list
		List<Integer> left = this.mergeSort(list.subList(0, middle));
		List<Integer> right = this.mergeSort(list.subList(middle, list.size()));

		// Return the two halves, merged
		return this.merge(left, right);
	}

	// Merges two sorted lists into a sorted list
	private List<Integer> merge(List<Integer> list1, List<Integer> list2) {
		List<Integer> result = new ArrayList<Integer>();
		int index1 = 0;
		int index2 = 0;

		// If we haven't reached the end of list 1 or list 2
		while (index1 < list1.size() && index2 < list2.size()) {
			// If list 1's first element is larger than list 2's first element
			if (list1.get(index1) >= list2.get(index2)) {
				// Add list 1's first element to the result list and increment
				// index1 to keep track that the element of this index has been
				// added
				result.add(list1.get(index1));
				index1++;
				// else, list 2's is larger
			} else {
				// So add list 2's first element to the result list and
				// increment index2 to keep track that the element of this index
				// has been added
				result.add(list2.get(index2));
				index2++;
			}
		}
		// Now that we've reached the end of one or both lists,
		// If some of list 1 is still not added, add it
		if (index1 < list1.size()) {
			result.addAll(list1.subList(index1, list1.size()));
		}
		// If some of list 2 is still not added, add it
		if (index2 < list2.size()) {
			result.addAll(list2.subList(index2, list2.size()));
		}
		return result;
	}

	// Displays results
	public void displayResults() {
		// For each row of the dropdown
		for (int i = 0; i < Constants.DROPDOWN_ITEMS; i++) {
			// Set the course number, course number color, course name, and
			// course time depending on what should be in the display
			_number[i].setText(_courses.get(_displayIndex[i]).getNumber());
			_number[i].setFill(_courses.get(_displayIndex[i]).getColor()
					.darker());
			_name[i].setText(_courses.get(_displayIndex[i]).getName());
			_time[i].setText(_courses.get(_displayIndex[i]).getDay() + " "
					+ _courses.get(_displayIndex[i]).getTime());
		}

		// Update the signs to match the courses
		this.refreshSigns();

		// If the description is being shown, hide it
		if (_descriptionShown) {
			this.hideDescription();
		}

		// If the dropdown is not down, slide it down
		if (!_slidDown) {
			Searcher.this.slideDownResults();
		}

		// Make the text flash
		this.flash(_textGroup);

	}

	// Handles what to do with the description depending on whether or not it is
	// currently being shown
	public void handleDescription(int index) {
		// If the description is being shown, hide the description
		if (_descriptionShown) {
			this.hideDescription();
			// else show the description
		} else {
			this.showDescription(index);
		}
	}

	// Returns true if the course's time slot(s) are already filled
	private boolean isFilled(Course course) {
		// Gets the courses coordinates on the calendar
		ArrayList<Double> coordinates = course.getCoordinates();

		// For each column coordinate, (the 0th element is the row/time, and any
		// elements after are the different columns/days)
		for (int i = 0; i < coordinates.size() - 1; i++) {
			// Cast the value as an int
			int x = (int) coordinates.get(i + 1).doubleValue();
			int y = (int) (coordinates.get(0).doubleValue() * 2);

			// For each row to check (each row on the calendar is only half an
			// hour, so classes longer than half an hour will require more than
			// one row to be checked for a given time coordinate)

			for (int j = 0; j < course.getRowsToTrack(); j++) {
				// If the time slot is already filled, return true
				if (_timeTracker[x][y + j]) {
					return true;
				}
			}
		}
		return false;
	}

	// Hides the description
	private void hideDescription() {
		_descriptionGroup.getChildren().clear();
		_descriptionShown = false;
		_previewGroup.getChildren().clear();
	}

	// Displays the description of the course of the passed in index
	public void showDescription(int index) {
		// If the course to be displayed is not the first one in the dropdown,
		// switch the course with the first course and refresh the display
		if (index != 0) {
			int temp = _displayIndex[0];
			_displayIndex[0] = _displayIndex[index];
			_displayIndex[index] = temp;
		}
		this.displayResults();

		// Create a TextArea to house the description, in the format, Professor:
		// "Description"
		TextArea description = new TextArea("Professor "
				+ _courses.get(_displayIndex[0]).getProfessor() + ": "
				+ _courses.get(_displayIndex[0]).getDescription());

		// Sets font, makes text wrap, sets dimensions and location, set as
		// uneditable, and focus away, since the user shouldn't be able to
		// interact with description
		description.setFont(Font.font(Constants.MAIN_FONT,
				FontWeight.SEMI_BOLD, Constants.DESCRIPTION_FONT_SIZE));
		description.setWrapText(true);
		description.setPrefColumnCount(Constants.DESCRIPTION_PREF_COLUMN);
		description.setPrefRowCount(Constants.DESCRIPTION_PREF_ROW);
		description.setLayoutY(Constants.DESCRIPTION_LAYOUT_Y);
		description.setEditable(false);
		description.setFocusTraversable(false);

		// Create a rectangle that stops the user from interacting with the
		// TextArea
		Rectangle shield = new Rectangle(Constants.SHIELD_X,
				Constants.SHIELD_Y, Constants.SHIELD_WIDTH,
				Constants.SHIELD_HEIGHT);
		shield.setFill(Constants.COLOR_BLUE);
		shield.setOpacity(Constants.SHIELD_OPACITY);

		// Create an ImageView of a cropped version of the books image, and
		// place it over the description, set as transparent
		ImageView imageView = new ImageView(Constants.IMAGE_BOOK_SMALL);
		imageView.setOpacity(Constants.DESCRIPTION_IMAGE_OPACITY);
		imageView.setFitHeight(Constants.DESCRIPTION_IMAGE_HEIGHT);
		imageView.setFitWidth(Constants.DESCRIPTION_IMAGE_WIDTH);
		imageView.setX(Constants.DESCRIPTION_IMAGE_X);
		imageView.setY(Constants.DESCRIPTION_IMAGE_Y);
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new DescriptionClickHandler());

		// Add the text, shield Rectangle, and ImageView to the group
		_descriptionGroup.getChildren().addAll(description, shield, imageView);

		// Signal that the description is being shown
		_descriptionShown = true;

		// Make the text of the description group flash
		this.flash(_descriptionGroup);

		// Show the preview of the course whose description is being shown
		_courses.get(_displayIndex[0]).showPreview(_previewGroup);
	}

	// accessor method for the searchbox
	public TextField getSearchBox() {
		return _searchBox;
	}

	// accessor method for the courseDisplayGroup
	public Group getDisplayGroup() {
		return _courseDisplayGroup;
	}

	// Returns the course that corresponds to the index it takes in as a
	// parameter; allows the MouseOverBox class to access course to display the
	// course preview
	public Course getCourseFromIndex(int index) {
		Course course = _courses.get(_displayIndex[index]);
		return course;
	}

	// Allows the MouseOverBox (from the calendar) to set itself as the selected
	// box after the user clicks it to search by time
	public void setSelectedBox(MouseOverBox selectedBox) {
		_selectedBox = selectedBox;
		_timeSelected = true;
	}

	// Allows course to set display index
	public void setDisplayIndex(int[] displayIndex) {
		_displayIndex = displayIndex;
	}

}
