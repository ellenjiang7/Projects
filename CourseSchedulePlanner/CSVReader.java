package Indy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.NumberFormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/*
 * This CSVReader class reads the text from the Courses csv file into an ArrayList.
 */

public class CSVReader {

	private HashMap<String, Integer> _contentMap;

	// reads the course file and returns an ArrayList of course data
	public ArrayList<Course> read(File file, String[] expectedHeader) {
		// checks to make sure file is not null
		if (file == null) {
			throw new IllegalArgumentException();
		}

		// creates an ArrayList for data
		ArrayList<Course> data = new ArrayList<Course>();

		// a try-catch block for responding to exceptions more selectively
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			if (!this.validateHeader(line, expectedHeader)) {
				return null;
			}

			// splits each line into strings, splitting at each comma
			while ((line = reader.readLine()) != null) {
				// The description which is surrounded by quotes should not be
				// split, so this parameter string makes sure the string is
				// followed by and
				// preceded by even number of quotations in order to split it
				String[] split = line
						.split(",(?=(([^\"]*\"){2})*[^\"]*$)(?![^()]*\\))");

				// Stores the resulting split chunks as the corresponding string
				// variables
				String name = split[0];
				String number = split[1];
				String professor = split[2];
				String day = split[3];
				String time = split[4];
				String description = split[5];

				// Create a HashMap with all the words in string for searching
				_contentMap = new HashMap<String, Integer>();

				// Split the line at either a space, comma, or dash
				String[] content = line.split(" |\\,|\\-");

				// Put each resulting string from the line in the HashMap
				for (int i = 0; i < content.length; i++) {
					_contentMap.put(content[i], 1);

					// Process for upper case and punctuation, and if
					// applicable, put the lower case version and/or
					// punctuation-less version in the HashMap
					this.processAndPut(content[i]);

				}

				// Create a new course, passing in all this parsed information
				Course course = new Course(name, number, professor, day, time,
						description, _contentMap);

				// Add the course to the ArrayList
				data.add(course);
			}

		} catch (IOException e) {
			System.err.println("ERROR: CSVReader.java - Read error " + e);
		} catch (NumberFormatException e) {
			System.err
					.println("ERROR: CSVReader.java - Number Format incorrect "
							+ e);
		}

		Collections.shuffle(data);
		return data;
	}

	// Checks if header is correct
	public boolean validateHeader(String line, String[] expectedHeader) {
		String[] header = line.split(",");

		if (header.length != expectedHeader.length) {
			return false;
		}

		for (int i = 0; i < header.length; i++) {
			if (!header[i].equals(expectedHeader[i])) {
				return false;
			}
		}
		return true;
	}

	// Process for upper case and punctuation, and if
	// applicable, put the lower case version and/or
	// punctuation-less version in the HashMap
	private void processAndPut(String word) {
		// initial state of boolean for checking whether a character was upper
		// case and needed to be made lowercase
		boolean lowered = false;

		// Convert the string to an array of chars
		char[] letters = word.toCharArray();

		// For each char in the array
		for (int i = 0; i < letters.length; i++) {
			// If the char is uppercase
			if (Character.isUpperCase(letters[i])) {
				// make the letter lowercase
				letters[i] = Character.toLowerCase(letters[i]);

				// Signal that a character has been made lowercase
				lowered = true;
			}
		}

		// If a character has been made lowercase
		if (lowered) {
			// put this lower-case version of the String in the contentMap
			_contentMap.put(new String(letters), 1);
		}

		// check for punctuation; remove punctuation and add to HashMap if
		// necessary
		this.checkPunctuation(letters);

	}

	// check for and remove punctuation
	private void checkPunctuation(char[] letters) {
		// For each char in the array
		for (int i = 0; i < letters.length; i++) {
			// If the char is punctuation (not a letter or digit)
			if (!Character.isLetterOrDigit(letters[i])) {
				// If the punctuation is not the first character
				if (i - 1 >= 0) {
					// Make a new char array with just the chars before the
					// punctuation
					char[] newLetters1 = new char[(i - 1)];
					newLetters1 = Arrays.copyOfRange(letters, 0, i - 1);

					// put a String converted from this char array into the
					// HashMap
					_contentMap.put(new String(newLetters1), 1);

					// check this char array again for more punctuation
					this.checkPunctuation(newLetters1);

				}

				// If the punctuation is not the last character
				if (i + 1 <= letters.length) {
					// Make a new char array with just the chars before the
					// punctuation
					char[] newLetters2 = new char[letters.length - (i + 1)];
					newLetters2 = Arrays.copyOfRange(letters, i + 1,
							letters.length);

					// put a String converted from this char array into the
					// HashMap
					_contentMap.put(new String(newLetters2), 1);

					// check this char array again for more punctuation
					this.checkPunctuation(newLetters2);
				}
			}
		}
	}
}
