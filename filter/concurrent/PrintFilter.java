package filter.concurrent;

/**
 * The filter for printing in the console
 *
 */
public class PrintFilter extends ConcurrentFilter {
	public PrintFilter() {
		super();
	}

	public void process() {
		while (!isDone()) {
			try {
				String line = input.take();
				// ends when POINSON PILL is seen
				if (!line.equals(POISON_PILL_MESSAGE)) {
					processLine(line);
				} else {
					break;
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public String processLine(String line) {
		System.out.println(line);
		return null;
	}
}
