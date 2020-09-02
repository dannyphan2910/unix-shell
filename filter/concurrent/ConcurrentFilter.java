package filter.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

import filter.Filter;

/**
 * An abstract class that extends the Filter and implements the basic
 * functionality of all filters. Each filter should extend this class and
 * implement functionality that is specific for that filter.
 *
 */
public abstract class ConcurrentFilter extends Filter implements Runnable {
	/**
	 * The poison pill message
	 */
	protected static final String POISON_PILL_MESSAGE = "";
	/**
	 * The input queue for this filter
	 */
	protected LinkedBlockingQueue<String> input;
	/**
	 * The output queue for this filter
	 */
	protected LinkedBlockingQueue<String> output;

	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}

	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter) {
			ConcurrentFilter sequentialNext = (ConcurrentFilter) nextFilter;
			this.next = sequentialNext;
			sequentialNext.prev = this;
			if (this.output == null) {
				this.output = new LinkedBlockingQueue<String>();
			}
			sequentialNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}

	/**
	 * Gets the next filter
	 * 
	 * @return the next filter
	 */
	public Filter getNext() {
		return next;
	}

	/**
	 * processes the input queue and writes the result to the output queue
	 */
	public void process() {
		while (!isDone()) {
			try {
				String line = input.take();
				if (line.equals(POISON_PILL_MESSAGE)) {
					// makes sure the POISON PILL is not lost once this filter's processing has
					// finished
					input.add(POISON_PILL_MESSAGE);
				} else {
					String processedLine = processLine(line);
					if (processedLine != null) {
						output.add(processedLine);
					}
				}
			} catch (InterruptedException e) {
				// exit if interrupted
				return;
			}
		}
	}

	@Override
	public boolean isDone() {
		// Checks for POISON PILL flag
		if (input.peek() != null && input.peek().equals(POISON_PILL_MESSAGE)) {
			return true;
		}
		return false;
	}

	protected abstract String processLine(String line);

	/**
	 * The run method of each concurrent filter performs the process normally if the
	 * current thread is not interrupted
	 */
	@Override
	public void run() {
		// check if current thread is interrupted, else process normally
		if (!Thread.currentThread().isInterrupted()) {
			process();
			if (!Thread.currentThread().isInterrupted() && output != null) {
				// pass the POINSON PILL flag if process successfully
				output.add(POISON_PILL_MESSAGE);
			}
		}
	}
}
