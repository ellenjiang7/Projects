package Indy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * The file processor takes care of saving and loading. It will write course
 * numbers coursesSaved text file to save a session and read course numbers from
 * the text file to load a session.
 *
 */

public class FileProcessor {
	private File _file;

	// Constructor loads the text file into an instance variable
	public FileProcessor() {
		// Load file
		_file = adjustFilepath("coursesSaved.txt");

	}

	// This method will read the lines of the file into an ArrayList of Strings
	// that is returned
	public ArrayList<String> loadFile() throws IOException {
		// Make an ArrayList to store the courseNumbers that get read from the
		// file
		ArrayList<String> courseNumbers = new ArrayList<String>();

		// Make file reader
		BufferedReader bufferedReader = new BufferedReader(
				new FileReader(_file));

		// Read every line that contains text into the courseNumbers ArrayList,
		// and close the reader
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			courseNumbers.add(line);
		}
		bufferedReader.close();

		return courseNumbers;
	}

	// This method takes in an ArrayList of courses and writes the courses'
	// numbers into the coursesSaved text file
	public void saveToFile(ArrayList<Course> courses) throws IOException {
		// Instantiate a writer to clear the file
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(
				new FileWriter(_file)));
		printWriter.print("");
		printWriter.close();

		// Instantiate a writer to write the course numbers of each course in
		// the ArrayList into the coursesAdded text file
		PrintWriter printWriter1 = new PrintWriter(new BufferedWriter(
				new FileWriter(_file)));
		for (int i = 0; i < courses.size(); i++) {
			printWriter1.println(courses.get(i).getNumber());
		}
		printWriter1.close();
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
}
