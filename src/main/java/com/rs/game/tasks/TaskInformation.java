package com.rs.game.tasks;

public final class TaskInformation {

	Task task;
	int loopDelay;
	int currDelay;

	public TaskInformation(Task task, int startDelay, int loopDelay) {
		this.task = task;
		this.currDelay = startDelay;
		this.loopDelay = loopDelay;
		if (loopDelay == -1)
			task.needRemove = true;
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
