package filter.concurrent;

import java.util.List;

/**
 * The Background Command class stores important information for each background
 * command
 *
 */
public class BackgroundCommand {
	/**
	 * the background command string - for "repl_jobs"
	 */
	private String command;
	/**
	 * the id of background command - for "repl_jobs"
	 */
	private int id;
	/**
	 * the list of sub-threads created from the filters
	 */
	private List<Thread> subThreads;

	/**
	 * Constructor of BackgroundCommand
	 * 
	 * @param command    the background command string
	 * @param id         the id of background command
	 * @param subThreads the list of sub-threads created from the filters
	 */
	public BackgroundCommand(String command, int id, List<Thread> subThreads) {
		this.command = command;
		this.id = id;
		this.subThreads = subThreads;
	}

	/**
	 * Getter for ID of background command
	 * 
	 * @return ID of background command
	 */
	public int getID() {
		return id;
	}

	/**
	 * Getter for the background command string
	 * 
	 * @return the background command string
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Getter for the list of sub-threads created from the filters
	 * 
	 * @return the list of sub-threads created from the filters
	 */
	public List<Thread> getSubThreads() {
		return subThreads;
	}

	/**
	 * Checks if the background command has yet to finish (still alive)
	 * 
	 * @return true if the background command has yet to finish (still alive)
	 */
	public boolean isAlive() {
		// if the last thread is alive, it implies that the entire background command is
		// alive (by implementation)
		return subThreads.get(subThreads.size() - 1).isAlive();
	}

	/**
	 * Checks if the background command is interrupted
	 * 
	 * @return true if the background command is interrupted
	 */
	public boolean isInterrupted() {
		// if the last thread is not interrupted, it implies that the entire background
		// command is not interrupted (by implementation)
		return subThreads.get(subThreads.size() - 1).isInterrupted();
	}
}