package filter.concurrent;

import main.ConcurrentREPL;

/**
 * The filter for pwd command
 *
 */
public class PwdFilter extends ConcurrentFilter {
	public PwdFilter() {
		super();
	}

	public void process() {
		output.add(processLine(""));
	}

	public String processLine(String line) {
		return ConcurrentREPL.currentWorkingDirectory;
	}
}
