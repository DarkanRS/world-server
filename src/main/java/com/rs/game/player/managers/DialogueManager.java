package com.rs.game.player.managers;

import com.rs.game.player.Player;
import com.rs.game.player.dialogues.Dialogue;

public class DialogueManager {

	private Player player;
	private Dialogue lastDialogue;

	public DialogueManager(Player player) {
		this.player = player;
	}

	public void continueDialogue(int interfaceId, int componentId) {
		if (player.getTempAttribs().getB("staticDialogue"))
			finishDialogue();
		if (lastDialogue == null)
			return;
		lastDialogue.run(interfaceId, componentId);
	}

	public void finishDialogue() {
		player.getTempAttribs().removeB("staticDialogue");
		if (player.getInterfaceManager().containsChatBoxInter())
			player.getInterfaceManager().closeChatBoxInterface();
		if (lastDialogue == null)
			return;
		lastDialogue.finish();
		lastDialogue = null;
	}

	public boolean execute(Dialogue dialogue, Object... params) {
		if (!player.getControllerManager().useDialogueScript(dialogue.getClass().getName()))
			return false;
		if (lastDialogue != null)
			lastDialogue.finish();
		lastDialogue = dialogue;
		if (lastDialogue == null)
			return false;
		lastDialogue.parameters = params;
		lastDialogue.setPlayer(player);
		lastDialogue.start();
		return true;
	}

}
