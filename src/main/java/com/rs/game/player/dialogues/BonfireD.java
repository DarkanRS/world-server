package com.rs.game.player.dialogues;

import com.rs.game.object.GameObject;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.skills.firemaking.Bonfire;
import com.rs.game.player.content.skills.firemaking.Bonfire.Log;

public class BonfireD extends Dialogue {

	private Log[] logs;
	private GameObject object;

	@Override
	public void start() {
		this.logs = (Log[]) parameters[0];
		this.object = (GameObject) parameters[1];
		int[] ids = new int[logs.length];
		for (int i = 0; i < ids.length; i++)
			ids[i] = logs[i].getLogId();
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "Which logs do you want to add to the bonfire?", -1, ids, null, false);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int slot = SkillsDialogue.getItemSlot(componentId);
		if (slot >= logs.length || slot < 0)
			return;
		player.getActionManager().setAction(new Bonfire(logs[slot], object));
		end();
	}

	@Override
	public void finish() {

	}

}
