package filter.concurrent;

import filter.Message;

/**
 * The filter for grep command
 *
 */
public class GrepFilter extends ConcurrentFilter {
	/**
	 * The text to find
	 */
	private String toFind;

	/**
	 * The constructor of the grep filter
	 * 
	 * @param line the text to find
	 * @throws Exception when there is no parameter given
	 */
	public GrepFilter(String line) throws Exception {
		super();
		String[] param = line.split(" ");
		if (param.length > 1) {
			toFind = param[1];
		} else {
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), line);
			throw new Exception();
		}
	}

	/**
	 * Checks whether each line contains the text to find that we passed in the
	 * constructor
	 * 
	 * @param line the line as coming from the input
	 * @return the line, if the specified text was found, otherwise null
	 */
	public String processLine(String line) {
		if (line.contains(toFind)) {
			return line;
		} else {
			return null;
		}
	}
}
