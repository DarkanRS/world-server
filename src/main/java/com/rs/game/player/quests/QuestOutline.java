package com.rs.game.player.quests;

import com.rs.game.player.Player;

import java.util.ArrayList;

public abstract class QuestOutline {
	
	public final Quest getQuest() {
		return getClass().getAnnotation(QuestHandler.class).value();
	}
	
	public abstract int getCompletedStage();
	public abstract ArrayList<String> getJournalLines(Player player, int stage);
	public abstract void complete(Player player);
}
