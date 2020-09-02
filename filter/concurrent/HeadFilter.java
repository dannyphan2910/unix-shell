package filter.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import filter.Filter;
import filter.Message;
import main.ConcurrentREPL;

public class HeadFilter extends ConcurrentFilter {
	private Scanner reader;
	private int count;
	private int total;

	/**
	 * The constructor of the head filter
	 * 
	 * @param line the parameters for head
	 * @throws Exception throws exception when there is an error with the given
	 *                   parameters, or when the file is not found
	 */
	public HeadFilter(String line) throws Exception {
		super();
		count = 0;

		// parsing the head options
		String[] args = line.split(" ");
		String filename;
		// obviously incorrect number of parameters
		if (args.length == 1) {
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), line);
			throw new Exception();
		} else if (args[1].charAt(0) == '-') { // check to see if length option is used
			try {
				total = Integer.parseInt(args[1].substring(1));
			} catch (Exception e) {
				System.out.printf(Message.INVALID_PARAMETER.toString(), line);
				throw new Exception();
			}
			if (args.length > 2) {
				filename = args[2];
			} else { // check to see if filename is given
				System.out.printf(Message.REQUIRES_PARAMETER.toString(), line);
				throw new Exception();
			}
		} else { // no options, should just be "head filename.txt"
			total = 10;
			filename = args[1];
		}
		try {
			reader = new Scanner(new File(ConcurrentREPL.currentWorkingDirectory + Filter.FILE_SEPARATOR + filename));
		} catch (FileNotFoundException e) {
			System.out.printf(Message.FILE_NOT_FOUND.toString(), line);
			throw new Exception();
		}
	}

	/**
	 * Overrides the process() method of ConcurrentFilter to check whether the file
	 * has more lines (through the reader object) and calls processLine() for each
	 * line until the limit (in variable total) is reached
	 */
	public void process() {
		while (count < total) {
			String processedLine = processLine("");
			if (processedLine == null) {
				break;
			}
			output.add(processedLine);
		}
		reader.close();
	}

	/**
	 * Processes each line by reading from the reader object and adding the result
	 * to the output queue
	 * 
	 * @param line the line to be processed
	 */
	public String processLine(String line) {
		if (reader.hasNextLine()) {
			count++;
			return reader.nextLine();
		} else {
			return null;
		}
	}

	/**
	 * Closes the input file reader whenever the filter is created but not executed
	 * properly (for example due to error in linking filters)
	 */
	public void terminate() {
		reader.close();
	}
}
