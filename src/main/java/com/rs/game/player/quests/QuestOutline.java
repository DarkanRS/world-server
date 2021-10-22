package com.rs.game.player.quests;

import java.util.ArrayList;

import com.rs.game.player.Player;

public abstract class QuestOutline {
	
	public final Quest getQuest() {
		return getClass().getAnnotation(QuestHandler.class).value();
	}
	
	public abstract int getCompletedStage();
	public abstract ArrayList<String> getJournalLines(Player player, int stage);
	public abstract void complete(Player player);
}
