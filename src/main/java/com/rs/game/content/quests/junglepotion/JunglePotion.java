package com.rs.game.content.quests.junglepotion;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(
		quest = Quest.JUNGLE_POTION,
		startText = "775 Herblore XP",
		itemsText = "None.",
		combatText = "None.",
		rewardsText = "775 Herblore XP",
		completedStage = 6
)
@PluginEventHandler
public class JunglePotion extends QuestOutline {

	// Quest Stages
	public static final int NOT_STARTED = 0;
	public static final int FIND_SNAKE_WEED = 1;
	public static final int FIND_ARDRIGAL = 2;
	public static final int FIND_SITO_FOIL = 3;
	public static final int FIND_VOLENCIA_MOSS = 4;
	public static final int FIND_ROGUES_PURSE = 5;
	public static final int QUEST_COMPLETE = 6;

	// Items IDs
	public static final int GRIMY_SNAKE_WEED = 1525;
	public static final int GRIMY_ARDRIGAL = 1527;
	public static final int GRIMY_SITO_FOIL = 1529;
	public static final int GRIMY_VOLENCIA_MOSS = 1531;
	public static final int GRIMY_ROGUES_PURSE = 1533;

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		// Keeping this commented for future updates
//		if (stage == QUEST_COMPLETE) {
//			lines.add("I collected five jungle herbs for him and he was");
//			lines.add("able to commune with his gods.");
//			lines.add("As a reward he showed me some herblore techniques.");
//			return lines;
//		}
		if (stage == player.getQuestStage(Quest.JUNGLE_POTION)) {
			lines.add("Trufitus Shakaya of the Tai Bwo Wannai village");
			lines.add("needed some jungle herbs in order to make a potion");
			lines.add("to help him commune with his gods.");
			return lines;
		}
		return lines;
	}

	private static final int HERB = 0; // TODO: I am unsure the herb used in the iface
	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.HERBLORE, 775);
		sendQuestCompleteInterface(player, HERB);
	}

	public static ObjectClickHandler handleMarshyVines = new ObjectClickHandler(new Object[] { 2575, 32106 }, (e) -> {
		Player player = e.getPlayer();
		int item;
		Animation searchAnim;
		switch (e.getObject().getId()) {
			case 2575 -> {
				item = GRIMY_SNAKE_WEED;
				searchAnim = new Animation(2094);
				player.sendMessage("You search the vine...");
			}
			case 32106 -> {
				item = GRIMY_ROGUES_PURSE;
				player.sendMessage("You search the wall...");
				player.setNextAnimation(new Animation(2096));
				searchAnim = new Animation(2097);
			}
			default -> {
				item = -1;
				searchAnim = new Animation(-1);
			}
		};
		player.repeatAction(5, (ticks) -> {
			if (e.getObject().getId() != 2575 && e.getObject().getId() != 32106)
				return false;
			int rand = Utils.random(0, 10);
			if (rand == 0) {
				player.getInventory().addItemDrop(item, 1);
				player.setNextAnimation(new Animation(-1));
				player.itemDialogue(item, "You find a grimy herb.");
				e.getObject().setIdTemporary(e.getObject().getId() + 1, Ticks.fromSeconds(90));
				return false;
			}
			if (ticks > 0)
				player.setNextAnimation(searchAnim);
			return true;
		});
	});

	public static ObjectClickHandler handlePalmTree = new ObjectClickHandler(new Object[] { 2577, 2579, 2581 }, (e) -> {
		Player player = e.getPlayer();
		// Is actually supposed to reject stage by stage
		if (!player.isQuestStarted(Quest.JUNGLE_POTION)) {
			player.sendMessage("You find nothing of significance...");
			return;
		}
		if (e.getOption().equals("Search")) {
			int item = switch (e.getObject().getId()) {
				case 2577 -> GRIMY_ARDRIGAL;
				case 2579 -> GRIMY_SITO_FOIL;
				case 2581 -> GRIMY_VOLENCIA_MOSS;
				default -> -1;
			};
			e.getObject().setIdTemporary(e.getObject().getId() + 1, Ticks.fromSeconds(60));
			player.getInventory().addItem(item, 1);
			player.itemDialogue(item, "You find a grimy herb.");
		}
	});

}
