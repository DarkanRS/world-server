package com.rs.game.tasks;

public final class WorldTaskInformation {

	WorldTask task;
	int loopDelay;
	int currDelay;

	public WorldTaskInformation(WorldTask task, int startDelay, int loopDelay) {
		this.task = task;
		this.currDelay = startDelay;
		this.loopDelay = loopDelay;
		if (loopDelay == -1)
			task.needRemove = true;
	}

	public WorldTask getTask() {
		return task;
	}

	public int getLoopDelay() {
		return loopDelay;
	}
	
	public int getCurrDelay() {
		return currDelay;
	}
}
