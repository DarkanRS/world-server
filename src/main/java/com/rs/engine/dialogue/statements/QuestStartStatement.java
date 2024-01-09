package com.rs.engine.dialogue.statements;

import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class QuestStartStatement implements Statement {

	private Quest quest;

	public QuestStartStatement(Quest quest) {
		this.quest = quest;
	}

	@Override
	public void send(Player player) {
		quest.openQuestInfo(player, true);
	}

	@Override
	public int getOptionId(int componentId) {
		return switch(componentId) {
			case 46 -> 0;
			default -> 1;
		};
	}

	@Override
	public void close(Player player) {
		if (player.getInterfaceManager().containsScreenInter())
			player.getInterfaceManager().removeCentralInterface();
	}
}
