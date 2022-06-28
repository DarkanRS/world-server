package com.rs.game.content.quests.handlers.fightarena;

import static com.rs.game.content.world.doors.Doors.handleDoor;

import java.util.ArrayList;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@QuestHandler(Quest.FIGHT_ARENA)
@PluginEventHandler
public class FightArena extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int FREE_JEREMY = 1;
	public final static int GET_JAIL_KEYS = 2;
	public final static int RETURN_TO_LADY_SERVIL = 3;
	public final static int QUEST_COMPLETE = 4;


	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case NOT_STARTED -> {
				lines.add("The prosperous Servil family have been abducted by the");
				lines.add("infamous General Khazard. He plans to have the family");
				lines.add("battle for his entertainment in the Fight Arena. Can ");
				lines.add("you rescue the Servils before the tyrant has these ");
				lines.add("innocent (not to mention wealthy) civilians slain?");
				lines.add("");
				lines.add("You can start this quest by speaking to Lady Servil,");
				lines.add("South-west of the monastary that is south of Ardougne.");
				lines.add("");
			}
			case FREE_JEREMY -> {
				lines.add("Lady Servil's husband and child have been taken captive!");
				lines.add("I should find a way into the fight arena by perhaps wearing");
				lines.add("the soldier's armour.");
				lines.add("");
				lines.add("I should be able to find an armour rack or something around");
				lines.add("here....");
				lines.add("");
			}
			case GET_JAIL_KEYS -> {
				lines.add("I need to find a way to open the jail cell. Perhaps one of");
				lines.add("the gaurds has the keys. If I could get him drunk enough...");
				lines.add("");
			}
			case RETURN_TO_LADY_SERVIL -> {
				lines.add("I should return to Lady Servil.");
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

	public static ItemOnObjectHandler keyOnCell = new ItemOnObjectHandler(true, new Object[] { 80 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (e.getPlayer().getQuestManager().getStage(Quest.FIGHT_ARENA) == GET_JAIL_KEYS && e.getItem().getId() == 76) {
				e.getPlayer().startConversation(new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Jeremy! Look, I have the keys.")
						.addNPC(265, HeadE.CHILD_UNSURE, "Wow! Please set me free so we can find my dad. I overheard a guard talking. I think " +
								"they've taken him to the arena.")
						.addPlayer(HeadE.HAPPY_TALKING, "Okay, we'd better hurry.")
						.addNext(()->{e.getPlayer().getControllerManager().startController(new FightArenaFightCutsceneController());})
				);
			}
		}
	};

	public static NPCClickHandler attack = new NPCClickHandler(false, new Object[] {7552}) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().stopAll(true);
			e.getPlayer().getInteractionManager().setInteraction(new PlayerCombatInteraction(e.getPlayer(), e.getNPC()));
		}
	};

	public static ObjectClickHandler handleAreanEntrance = new ObjectClickHandler(new Object[] { 82 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			//pass
		}
	};

	public static ObjectClickHandler handleArmourStand = new ObjectClickHandler(new Object[] { 41498 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getOption().equalsIgnoreCase("borrow")) {
				if(!e.getPlayer().getInventory().containsItem(74) && e.getPlayer().getEquipment().getHatId() != 74)
					e.getPlayer().getInventory().addItem(74, 1, true);
				if(!e.getPlayer().getInventory().containsItem(75) && e.getPlayer().getEquipment().getChestId() != 75)
					e.getPlayer().getInventory().addItem(75, 1, true);
			}
		}
	};

	public static NPCClickHandler talkKhazardGaurd = new NPCClickHandler(new Object[] { 253 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), HeadE.EVIL_LAUGH, "Hail General Khazard!"));
		}
	};

	public static LoginHandler onLoginVarbits = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if(e.getPlayer().getQuestManager().getStage(Quest.FIGHT_ARENA) > NOT_STARTED)
				e.getPlayer().getVars().setVarBit(5626, 1);
			if(e.getPlayer().getQuestManager().getStage(Quest.FIGHT_ARENA) > GET_JAIL_KEYS);
				e.getPlayer().getVars().setVarBit(6163, 2);
			if(e.getPlayer().getQuestManager().getStage(Quest.FIGHT_ARENA) <= GET_JAIL_KEYS)
				e.getPlayer().getVars().setVarBit(6163, 0);
		}
	};

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
