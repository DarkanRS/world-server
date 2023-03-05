package com.rs.game.content.quests.monksfriend;

import java.util.ArrayList;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@QuestHandler(Quest.MONKS_FRIEND)
@PluginEventHandler
public class MonksFriend extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int GET_BLANKET = 1;
	public final static int ASK_ABOUT_PARTY = 2;
	public final static int HELP_CEDRIC = 3;
	public final static int RETURN_TO_OMAD = 4;
	public final static int QUEST_COMPLETE = 5;


	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case NOT_STARTED -> {
				lines.add("A child has had their blanket stolen! Find the thieves'");
				lines.add("den and return the blanket, then help Brother Omad ");
				lines.add("organise the drinks for the child's birthday party.");
				lines.add("");
				lines.add("I can start this quest by speaking to Brother Omad");
				lines.add("in the Monastery south of Ardougne");
				lines.add("");
			}
			case GET_BLANKET -> {
				lines.add("I need to find the thief's hideout who took a blanket");
				lines.add("from a poor child. I will need to look around to find it.");
				lines.add("");
			}
			case HELP_CEDRIC -> {
				lines.add("Cedric is somewhere outside the Monastary. He needs");
				lines.add("help before returning back to brother Omad.");
				lines.add("");
			}
			case QUEST_COMPLETE -> {
				lines.add("");
				lines.add("");
				lines.add("QUEST COMPLETE!");
			}
			default -> {
				lines.add("Invalid quest stage. Report this to an administrator.");
			}
		}
		return lines;
	}

	//It is a circle around a null ladder. A varbit makes it visible.
	private static Tile[] ladderTilesInACircle = new Tile[]{
			Tile.of(2561, 3220, 0),
			Tile.of(2560, 3220, 0),
			Tile.of(2559, 3220, 0),
			Tile.of(2559, 3221, 0),
			Tile.of(2559, 3222, 0),
			Tile.of(2559, 3223, 0),
			Tile.of(2560, 3223, 0),
			Tile.of(2560, 3224, 0),
			Tile.of(2561, 3224, 0),
			Tile.of(2562, 3224, 0),
			Tile.of(2562, 3223, 0),
			Tile.of(2563, 3223, 0),
			Tile.of(2563, 3222, 0),
			Tile.of(2563, 3221, 0),
			Tile.of(2562, 3221, 0),
			Tile.of(2562, 3220, 0)
	};

	public static PlayerStepHandler handleInvisibleLadder = new PlayerStepHandler(ladderTilesInACircle, e -> {
		Player p = e.getPlayer();
		p.getVars().setVarBit(4833, 1);
	});

	public static ObjectClickHandler handleThiefLadder = new ObjectClickHandler(new Object[]{42, 32015}, e -> {
		Player p = e.getPlayer();
		if (e.getObjectId() == 42)
			p.useLadder(Tile.of(2561, 9621, 0));
		else if (e.getObject().getTile().matches(Tile.of(2561, 9622, 0)))
			p.useLadder(Tile.of(2560, 3222, 0));
	});

	@Override
	public void complete(Player player) {
		player.getInventory().addItem(563, 8, true);
		player.getSkills().addXpQuest(Constants.WOODCUTTING, 2000);
		getQuest().sendQuestCompleteInterface(player, 563, "8 Law Runes", "2,000 Woodcutting XP");
	}

}
