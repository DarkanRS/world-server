package com.rs.game.player.cutscenes.actions;

import com.rs.game.player.Player;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.game.player.dialogues.SimplePlayerMessage;

public class EntityDialogueAction extends CutsceneAction {

	private int id;
	private String message;

	public EntityDialogueAction(String message) {
		this(-1, message);
	}

	public EntityDialogueAction(int id, String message) {
		super(-1, -1);
		this.id = id;
		this.message = message;
	}

	@Override
	public void process(Player player, Object[] cache) {
		if (id == -1)
			player.getDialogueManager().execute(new SimplePlayerMessage(), message);
		else
			player.getDialogueManager().execute(new SimpleNPCMessage(), id, message);
	}

}
