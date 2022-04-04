package com.rs.game.content.quests.handlers.fightarena;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;

import static com.rs.game.content.world.doors.Doors.handleDoor;

@QuestHandler(Quest.FIGHT_ARENA)
@PluginEventHandler
public class FightArena extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int FREE_JEREMY = 1;
	public final static int GET_JAIL_KEYS = 2;
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
			}
			case FREE_JEREMY -> {
				lines.add("");
				lines.add("");
			}
			case HELP_CEDRIC -> {
				lines.add("");
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

	public static ObjectClickHandler handleJailEntrance = new ObjectClickHandler(new Object[] { 81 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getObject().getRotation() == 2) {
				if(e.getPlayer().getX() > e.getObject().getX()) {
					handleDoor(e.getPlayer(), e.getObject());
					return;
				}
				if(e.getPlayer().getEquipment().getHatId() == 74 && e.getPlayer().getEquipment().getChestId() == 75) {
					e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.FRUSTRATED, "This door appears to be locked.")
							.addNPC(253, HeadE.CALM_TALK, "Nice observation, guard. You could have asked to be let in like any normal person.")
							.addNext(()->{handleDoor(e.getPlayer(), e.getObject());})
					);
					return;
				}
				e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.FRUSTRATED, "This door appears to be locked.")
						.addNPC(253, HeadE.CALM_TALK, "Nice observation")
				);
			}
			if(e.getObject().getRotation() == 3) {
				if(e.getPlayer().getY() < e.getObject().getY()) {
					handleDoor(e.getPlayer(), e.getObject());
					return;
				}
				if(e.getPlayer().getEquipment().getHatId() == 74 && e.getPlayer().getEquipment().getChestId() == 75) {
					e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.FRUSTRATED, "This door appears to be locked.")
							.addNPC(253, HeadE.CALM_TALK, "Nice observation, guard. You could have asked to be let in like any normal person.")
							.addNext(()->{handleDoor(e.getPlayer(), e.getObject());})
					);
					return;
				}
				e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.FRUSTRATED, "This door appears to be locked.")
						.addNPC(253, HeadE.CALM_TALK, "Nice observation")
				);
			}
		}
	};

	@Override
	public void complete(Player player) {
		player.getInventory().addItem(995, 1000, true);
		player.getSkills().addXpQuest(Constants.ATTACK, 12_175);
		player.getSkills().addXpQuest(Constants.THIEVING, 2_175);
		getQuest().sendQuestCompleteInterface(player, 75, "12,175 Attack XP", "2,175 Thieving XP", "1,000 Coins");
	}

}
