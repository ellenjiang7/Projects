package Indy;

import javafx.scene.paint.Color;

/**
 * The Constants class contains constants that are used throughout the planner
 * to set dimensions, positioning, font size, transition time, etc.
 */

public class Constants {
	// Dimensions of screen
	public static final int SCREEN_WIDTH = 375;
	public static final int SCREEN_HEIGHT = 667;

	// Dimensions of top pane
	public static final int TOP_HEIGHT = 215;

	// Dimensions of dropdown
	public static final int DROPDOWN_WIDTH = SCREEN_WIDTH;
	public static final int DROPDOWN_HEIGHT = TOP_HEIGHT;

	// Dimensions of scroll pane
	public static final int SCROLL_WIDTH = SCREEN_WIDTH;
	public static final int SCROLL_HEIGHT = SCREEN_HEIGHT - TOP_HEIGHT;

	// Dimensions of course boxes
	public static final int COURSE_BOX_WIDTH = 54;
	public static final int COURSE_BOX_HEIGHT = 46;
	public static final int COURSE_BOX_ARC = 37;

	// dimensions/numbers regarding rows
	public static final int ROW_WIDTH = 320;
	public static final int ROW_HEIGHT = 46;
	public static final int ROW_PADDING = 0;
	public static final int ROW_SPACING = COURSE_BOX_HEIGHT + ROW_PADDING;
	public static final int ROW_POS_X = 12;
	public static final int ROW_POS_Y = 35;
	public static final int ROW_TOTAL_NUMBER = 12;

	// dimensions/numbers regarding columns
	public static final int COL_WIDTH = COURSE_BOX_WIDTH;
	public static final int COL_HEIGHT = ROW_TOTAL_NUMBER * ROW_SPACING
			+ ROW_POS_Y + 10;
	public static final int COL_PADDING = 12;
	public static final int COL_SPACING = COURSE_BOX_WIDTH + COL_PADDING;
	public static final int COL_POS_X = 33;
	public static final int COL_POS_Y = 5;
	public static final int COL_TOTAL_NUMBER = 5;

	// Dimensions of calendar
	public static final int CALENDAR_WIDTH = SCREEN_WIDTH;
	public static final int CALENDAR_HEIGHT = ROW_POS_Y * 3 + ROW_TOTAL_NUMBER
			* ROW_SPACING;

	// Colors
	public static final Color COLOR_BLUE = Color.web("#3BAFDA");
	public static final Color COLOR_MUTED_BLUE = Color.web("d2dbe4");
	public static final Color COLOR_GREEN = Color.web("#A0D468");
	public static final Color COLOR_ORANGE = Color.web("#FC6E51");
	public static final Color COLOR_YELLOW = Color.web("#FCBB42");
	public static final Color COLOR_LIGHT_YELLOW = Color.web("#ae9171");
	public static final Color COLOR_RED = Color.web("#DA4453");
	public static final Color COLOR_TEAL = Color.web("#37BC9B");
	public static final Color COLOR_GRAY = Color.web("#A0A0A0");
	public static final Color COLOR_BROWN = Color.web("#644f4c");
	public static final Color COLOR_WHITE = Color.web("#FFFFFF");

	// Colors of specific things
	public static final Color COLOR_ROW = COLOR_BLUE;
	public static final Color COLOR_COL = COLOR_WHITE;
	public static final Color COLOR_BACKGROUND_RECTANGLE = COLOR_BROWN;

	// positions/numbers regarding items in dropdown
	public static final double DROPDOWN_TEXT_X = 45;
	public static final double DROPDOWN_TEXT_Y = 45;
	public static final double DROPDOWN_TEXT_NUMBER_Y = DROPDOWN_TEXT_Y;
	public static final double DROPDOWN_TEXT_NUMBER_FONT_SIZE = 12;
	public static final double DROPDOWN_TEXT_NAME_Y = DROPDOWN_TEXT_Y + 15;
	public static final double DROPDOWN_TEXT_NAME_FONT_SIZE = 11;
	public static final double DROPDOWN_TEXT_TIME_FONT_SIZE = 12;
	public static final double DROPDOWN_TEXT_OFFSET = 45;
	public static final double DROPDOWN_PLUS_X = -6;
	public static final double DROPDOWN_PLUS_Y = DROPDOWN_TEXT_Y + 10;
	public static final double DROPDOWN_TEXT_TIME_X = 160;
	public static final double DROPDOWN_OPACITY = .8;
	public static final int DROPDOWN_ITEMS = 4;
	public static final int DROPDOWN_MOUSEOVER_OFFSET = 25;

	// plus button
	public static final int PLUS_BUTTON_FONT_SIZE = 35;
	public static final int CIRCLE_BACK_OFFSET_X = 29;
	public static final int CIRCLE_BACK_OFFSET_Y = -9;
	public static final int CIRCLE_BACK_RADIUS = 12;

	// search box
	public static final int SEARCH_BOX_PREF_COLUMN = 28;
	public static final String SEARCH_BOX_PROMPT = "Search";

	// clear button
	public static final int CLEAR_BUTTON_OFFSET_X = -20;
	public static final int CLEAR_BUTTON_OFFSET_Y = 19;
	public static final int CLEAR_BUTTON_ROTATE = 45;
	public static final double CLEAR_BUTTON_OPACITY = .50;
	public static final double CLEAR_BUTTON_FONT_SIZE = 24;

	// Overall
	public static final String MAIN_FONT = "Gillius ADF Regular";
	public static final String BUTTON_FONT = "Georgia";
	public static final int WRAPPING_WIDTH = 58;

	// slide-up button
	public static final double SLIDE_UP_BUTTON_X = Constants.DROPDOWN_WIDTH / 2 - 15;
	public static final double SLIDE_UP_BUTTON_Y = Constants.DROPDOWN_HEIGHT - 9;
	public static final double SLIDE_UP_BUTTON_WIDTH = 30;
	public static final double SLIDE_UP_BUTTON_HEIGHT = 4;
	public static final double SLIDE_UP_BUTTON_OPACITY = .4;
	public static final double SLIDE_UP_BUTTON_ARC = 5;

	// description
	public static final double DESCRIPTION_FONT_SIZE = 11;
	public static final int DESCRIPTION_PREF_COLUMN = 32;
	public static final int DESCRIPTION_PREF_ROW = 8;
	public static final int DESCRIPTION_LAYOUT_Y = 75;

	// description image
	public static final double DESCRIPTION_IMAGE_OPACITY = .1;
	public static final double DESCRIPTION_IMAGE_HEIGHT = 122;
	public static final double DESCRIPTION_IMAGE_WIDTH = Constants.DROPDOWN_WIDTH - 12;
	public static final double DESCRIPTION_IMAGE_X = 6;
	public static final double DESCRIPTION_IMAGE_Y = Constants.DROPDOWN_TEXT_OFFSET + 32;

	// shield
	public static final double SHIELD_X = 0;
	public static final double SHIELD_Y = 25 + Constants.DROPDOWN_TEXT_OFFSET;
	public static final double SHIELD_WIDTH = Constants.DROPDOWN_WIDTH - 10;
	public static final double SHIELD_HEIGHT = 3 * Constants.DROPDOWN_TEXT_OFFSET;
	public static final double SHIELD_OPACITY = .01;

	// transition times
	public static final double SLIDE_UP_TIME = .4;
	public static final double SLIDE_DOWN_TIME = .2;
	public static final double FLASH_REFRESH_TIME = .15;

	// image links
	public static final String IMAGE_BOOK = "http://ellenart.me/wp-content/uploads/2016/12/book.jpeg";
	public static final String IMAGE_WELCOME = "http://ellenart.me/wp-content/uploads/2016/12/welcome-schedule-planner.png";
	public static final String IMAGE_GET_STARTED = "http://ellenart.me/wp-content/uploads/2016/12/lets-get-started-1.png";
	public static final String IMAGE_ADD_COURSE = "http://ellenart.me/wp-content/uploads/2016/12/add-a-course.png";
	public static final String IMAGE_CLICK_TO_SEARCH = "http://ellenart.me/wp-content/uploads/2016/12/click-to-search-by-time.png";
	public static final String IMAGE_BOOK_SMALL = "http://ellenart.me/wp-content/uploads/2016/12/book2.jpg";

	// Opacity of the boxes in mouse-over
	public static final double BOX_OPACITY = .15;

	// Rate of the growing and shrinking when courses are added and removed
	public static final double GROW_SHRINK_SPEED = .2;

	// tutorial
	public static final double WELCOME_WAIT_TIME = 0;
	public static final double WELCOME_DISPLAY_TIME = 1;
	public static final double GET_STARTED_WAIT_TIME = 5.5;
	public static final double GET_STARTED_DISPLAY_TIME = 3;
	public static final double ADD_COURSE_WAIT_TIME = .3;
	public static final double ADD_COURSE_DISPLAY_TIME = 1.5;
	public static final double CLICK_TO_SEARCH_WAIT_TIME = 4;
	public static final double CLICK_TO_SEARCH_DISPLAY_TIME = 2.5;
	public static final double TUTORIAL_OPACITY = .8;

	// course display
	public static final double COURSE_DISPLAY_OPACITY = .2;
	public static final double COURSE_DISPLAY_TEXT_OFFSET = -3;

	// course text
	public static final double COURSE_TEXT_FONT_SIZE = 12;
	public static final double COURSE_TEXT_OPACITY = .9;

	// course display delete
	public static final double DELETE_BACKGROUND_OPACITY = .8;
	public static final double DELETE_OFFSET_X = 20;
	public static final double DELETE_OFFSET_Y = 10;
	public static final double DELETE_FONT_SIZE = 40;
	public static final double DELETE_CIRCLE_BACK_OFFSET_X = 28;

	// drop shadow
	public static final double DROP_SHADOW_OFFSET = .3;
	public static final double DROP_SHADOW_SPREAD = .05;

	// calendar text course time
	public static final double COURSE_TIME_OFFSET_X = -5;
	public static final double COURSE_TIME_OFFSET_Y = 27;
	public static final double COURSE_TIME_FONT_SIZE = 12;
	public static final double COURSE_TIME_ROTATE = 270;

	// calendar text course day
	public static final double COURSE_DAY_OFFSET_X = -1;
	public static final double COURSE_DAY_OFFSET_Y = 20;
	public static final double COURSE_DAY_FONT_SIZE = 18;
	public static final double COURSE_DAY_ROTATE = 270;

	// clear courses button
	public static final double CLEAR_COURSES_BACKGROUND_WIDTH = 120;
	public static final double CLEAR_COURSES_BACKGROUND_HEIGHT = 30;
	public static final double CLEAR_COURSES_BACKGROUND_ARC = 20;
	public static final double CLEAR_COURSES_BACKGROUND_OFFSET_Y = -20;
	public static final double CLEAR_COURSES_TEXT_OFFSET_X = -50;
	public static final double CLEAR_COURSES_TEXT_OFFSET_Y = -27;
	public static final double CLEAR_COURSES_FONT_SIZE = 11;

}
