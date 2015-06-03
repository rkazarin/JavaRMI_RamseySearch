package system;

import java.util.Comparator;

import api.Task;

/**
 * Compares two tasks based on priority
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
@SuppressWarnings("rawtypes")
public class TaskComparator implements Comparator<Task> {

	@Override
	public int compare(Task task1, Task task2) {
		return task1.getPriority() - task2.getPriority();
	}

}
