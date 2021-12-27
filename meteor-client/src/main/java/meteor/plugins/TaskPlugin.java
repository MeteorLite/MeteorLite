package meteor.plugins;

import lombok.Getter;
import meteor.PluginTask;
import meteor.task.Schedule;
import meteor.Logger;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskPlugin extends Plugin {
	private static final Logger log = Logger.Companion.getLogger(TaskPlugin.class);
	private final List<PluginTask> tasks = new ArrayList<>();
	@Getter
	private String currentTask = "Idle";

	public void submit(PluginTask... tasks) {
		this.tasks.addAll(Arrays.asList(tasks));
	}

	@Schedule(period = 10, unit = ChronoUnit.MILLIS, asynchronous = true)
	public void loop() {
		try {
			for (PluginTask task : tasks) {
				if (!task.validate()) {
					continue;
				}

				currentTask = task.getClass().getSimpleName();
				int sleep = task.execute();
				if (task.blocking()) {
					Thread.sleep(sleep);
					return;
				}
			}
		} catch (Exception e) {
			log.error("Task failed to execute {}", e);
		}
	}
}
