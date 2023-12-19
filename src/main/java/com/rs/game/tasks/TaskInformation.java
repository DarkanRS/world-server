package com.rs.game.tasks;

public final class TaskInformation {

	String mapping;
	Task task;
	int loopDelay;
	int currDelay;

	public TaskInformation(String mapping, Task task, int startDelay, int loopDelay) {
		this.mapping = mapping;
		this.task = task;
		this.currDelay = startDelay;
		this.loopDelay = loopDelay;
		if (loopDelay == -1)
			task.needRemove = true;
	}

	public String getMapping() {
		return mapping;
	}

	public Task getTask() {
		return task;
	}

	public int getLoopDelay() {
		return loopDelay;
	}
	
	public int getCurrDelay() {
		return currDelay;
	}
}
