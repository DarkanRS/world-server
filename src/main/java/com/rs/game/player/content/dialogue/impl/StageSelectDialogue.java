package com.rs.game.player.content.dialogue.impl;

import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;

public class StageSelectDialogue extends Dialogue {
	
	private String stageName;
	private Conversation conversation;

	public StageSelectDialogue(String stageName, Conversation conversation) {
		this.stageName = stageName;
		this.conversation = conversation;
	}

	public String getStageName() {
		return stageName;
	}

	public Conversation getConversation() {
		return conversation;
	}

}
