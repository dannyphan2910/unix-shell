package main;


import filter.Message;
import filter.concurrent.BackgroundCommand;
import filter.concurrent.ConcurrentCommandBuilder;
import filter.concurrent.ConcurrentFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * The main implementation of the REPL loop (read-eval-print loop). It reads
 * commands from the user, parses them, executes them and displays the result.
 *
 */
public class ConcurrentREPL {
	/**
	 * the path of the current working directory
	 */
	public static String currentWorkingDirectory;

	/**
	 * the list of running (alive and non-interrupted) background commands
	 */
	static List<BackgroundCommand> backgroundCommands;

	/**
	 * The main method that will execute the REPL loop
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		currentWorkingDirectory = System.getProperty("user.dir");
		backgroundCommands = new ArrayList<BackgroundCommand>();

		Scanner s = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		String command;
		while (true) {
			// obtaining the command from the user
			System.out.print(Message.NEWCOMMAND);
			command = s.nextLine();
			if (command.equals("exit")) {
				break;
			} else {
				command = command.trim();
				String[] commandArr = command.split("\\s+");

				// BACKGROUND COMMAND (denoted by the '&' ending)
				if (command.endsWith("&")) {
					executeBG(command);
					// REPL_JOBS COMMAND
				} else if (command.startsWith("repl_jobs")) {
					executeREPL_JOBS(command, commandArr.length);
					// KILL COMMAND
				} else if (command.startsWith("kill")) {
					executeKill(command, commandArr);
					// NORMAL (NON-BACKGROUND) COMMANDS
				} else if (!command.trim().equals("")) {
					processFilters(command);
				}
			}
		}
		s.close();
		System.out.print(Message.GOODBYE);
	}

	/**
	 * This method processes the filters from the non-background command by creating
	 * the filters, making and starting a thread from each filter
	 * 
	 * @param command the non-background command string
	 */
	private static void processFilters(String command) {
		Thread lastElement = null; // keep track of the last thread
		// build the filters list from the command
		ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
		while (filterlist != null) {
			// create and start a thread from each filter
			Thread thisFilter = new Thread(filterlist);
			thisFilter.start();
			filterlist = (ConcurrentFilter) filterlist.getNext();
			// store the last thread
			if (filterlist == null) {
				lastElement = thisFilter;
			}
		}
		// join the thread to the MAIN thread - the MAIN thread waits the threads to
		// finish before getting another input/printing ">"
		try {
			if (lastElement != null) {
				lastElement.join();
			}
		} catch (InterruptedException e) {
			return;
		}
	}

	/**
	 * This method executes the background command (denoted by the ampersand ending)
	 * by adding a BackgroundCommand built with threads of filters to the list
	 * 
	 * @param command the background command string
	 */
	private static void executeBG(String command) {
		List<Thread> subThreads = new LinkedList<>(); // the list of threads from filters of this command
		// building the filters list from the command
		ConcurrentFilter filterlist = ConcurrentCommandBuilder
				.createFiltersFromCommand(command.trim().substring(0, command.trim().length() - 1));
		while (filterlist != null) {
			Thread thisFilter = new Thread(filterlist);
			thisFilter.start();
			subThreads.add(thisFilter);
			filterlist = (ConcurrentFilter) filterlist.getNext();
		}
		// create and add to the list a background command
		BackgroundCommand bg = new BackgroundCommand(command, backgroundCommands.size() + 1, subThreads);
		backgroundCommands.add(bg);
	}

	/**
	 * This method executes the "repl_jobs" command by getting the id and the
	 * command string of background commands in the list
	 * 
	 * @param command       the command string
	 * @param commandLength the length of the command string array split by
	 *                      whitespace
	 */
	private static void executeREPL_JOBS(String command, int commandLength) {
		cleanBackgroundCommands(); // clean finished (not alive) and interrupted background commands
		// checks for INVALID_PARAMTER exception
		if (commandLength > 1) {
			System.out.print(Message.INVALID_PARAMETER.with_parameter(command));
		} else {
			// loop through the list of background commands to print accordingly
			for (BackgroundCommand bg : backgroundCommands) {
				System.out.println("\t" + bg.getID() + ". " + bg.getCommand());
			}
		}
	}

	/**
	 * This method checks the kill command and calls the execution of the "kill"
	 * command
	 * 
	 * @param command the command string
	 * @param arr     the command string array split by whitespace
	 */
	private static void executeKill(String command, String[] arr) {
		int commandLength = arr.length;
		// checks for INVALID_PARAMETER exception (too many parameters or non-numerical
		// parameter)
		if (commandLength > 2 || commandLength == 2 && !arr[1].matches("^[0-9]+$")) {
			System.out.print(Message.INVALID_PARAMETER.with_parameter(command));
			// checks for REQUIRES_PARAMETER exception (no parameter)
		} else if (commandLength == 1) {
			System.out.print(Message.REQUIRES_PARAMETER.with_parameter(command));
		} else {
			// parse the id to kill the corresponding background command
			int numToKill = Integer.valueOf(arr[1]);
			executeKillMain(numToKill, command);
		}
	}

	/**
	 * This method executes the "kill" command by looping through the list and kills
	 * the one with the right id
	 * 
	 * @param numToKill the id of background command
	 * @param command   the "kill" command string
	 */
	private static void executeKillMain(int numToKill, String command) {
		boolean successful = false;
		// loop through the list and kills the one with the right id
		for (int i = 0; i < backgroundCommands.size(); i++) {
			BackgroundCommand bg = backgroundCommands.get(i);
			if (bg.getID() == numToKill) {
				interruptSubThreads(bg.getSubThreads());
				backgroundCommands.remove(i);
				successful = true;
				break;
			}
		}
		// cannot find to kill the background command with that id
		if (!successful) {
			System.out.print(Message.INVALID_PARAMETER.with_parameter(command));
		}
	}

	/**
	 * Interrupt this background command by interrupting all the threads (of
	 * filters) in the list
	 * 
	 * @param subThreads the list of threads (of filters)
	 */
	private static void interruptSubThreads(List<Thread> subThreads) {
		for (Thread subThread : subThreads) {
			subThread.interrupt();
		}
	}

	/**
	 * This method clean the list of background commands by removing finished
	 * (non-alive) and interrupted ones
	 */
	private static void cleanBackgroundCommands() {
		int i = 0;
		while (i < backgroundCommands.size()) {
			BackgroundCommand thisBg = backgroundCommands.get(i);
			// checks if this background command is not alive or interrupted
			if (!thisBg.isAlive() || thisBg.isInterrupted()) {
				backgroundCommands.remove(i);
			} else {
				i++;
			}
		}
	}

}
