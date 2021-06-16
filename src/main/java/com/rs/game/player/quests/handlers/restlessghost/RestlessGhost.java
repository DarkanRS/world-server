package com.rs.game.player.quests.handlers.restlessghost;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestOutline;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@QuestHandler(Quest.RESTLESS_GHOST)
@PluginEventHandler
public class RestlessGhost extends QuestOutline {

	public static int SKULL_CONF = 2130;
	
	@Override
	public int getCompletedStage() {
		return 4;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<String>();

		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking to Father Aereck");
			lines.add("in the lumbridge chapel.");
			break;
		case 1:
			lines.add("Aereck told me there is a ghost haunting his graveyard.");
			lines.add("He told me I should speak with Father Urhney about what");
			lines.add("to do next.");
			lines.add("");
			lines.add("I was told he can be found in southern Lumbridge swamp.");
			break;
		case 2:
			lines.add("I was given an amulet of ghostspeak by Father Urhney.");
			lines.add("The amulet should let me communicate with the ghost somehow.");
			lines.add("");
			lines.add("I should find the ghost and try to figure out what is");
			lines.add("causing it to haunt the church graveyard.");
			break;
		case 3:
			lines.add("I found the ghost and he told me that he has lost his skull.");
			lines.add("");
			lines.add("He said he lost it somewhere near the swamp mines.");
			break;
		case 4:
			lines.add("");
			lines.add("");
			lines.add("QUEST COMPLETE!");
			break;
		default:
			lines.add("Invalid quest stage. Report this to an administrator.");
			break;
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.PRAYER, 1125);
		getQuest().sendQuestCompleteInterface(player, 553, "1,125 Prayer XP");
	}

	static class RGhostD extends Dialogue {

		@Override
		public void start() {
			stage = 0;
			if (player.getEquipment().getAmuletId() == 552) {
				sendNPCDialogue(457, Dialogue.CALM_TALK, "Hello mortal.");
			} else {
				sendNPCDialogue(457, Dialogue.CALM_TALK, "Woooo woooo wooo woo!");
				stage = -1;
			}
		}

		@Override
		public void run(int interfaceId, int componentId) {
			if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 3) {
				if (stage == 0) {
					if (player.getInventory().containsItem(553, 1)) {
						stage++;
						sendPlayerDialogue(Dialogue.CALM_TALK, "I found this skull outside.");
					} else {
						stage++;
						sendPlayerDialogue(Dialogue.HAPPY_TALKING, "Hello.");
					}
				} else if (stage == 1) {
					stage++;
					if (player.getInventory().containsItem(553, 1)) {
						sendNPCDialogue(457, Dialogue.HAPPY_TALKING, "That's it! That's my head! Thank you adventurer.<br>Finally I can be at peace.");
					} else {
						sendNPCDialogue(457, Dialogue.SAD, "I seem to have lost my skull. Could you go<br>find it for me please? I want to be released.<br>Last I saw it was a bit south of here by the mining site.");
					}
				} else if (stage == 2) {
					stage++;
					if (player.getInventory().containsItem(553, 1)) {
						sendPlayerDialogue(Dialogue.LAUGH_EXCITED, "You're very welcome. Farewell.");
						player.getInventory().deleteItem(553, 1);
						player.getQuestManager().completeQuest(Quest.RESTLESS_GHOST);
					} else {
						sendPlayerDialogue(Dialogue.HAPPY_TALKING, "I think I can handle that that.");
					}
				} else {
					end();
				}
			}
		}

		@Override
		public void finish() {

		}

	}

	static class UrhneyD extends Dialogue {

		@Override
		public void start() {
			stage = 0;
			if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 0) {
				sendNPCDialogue(458, Dialogue.MEAN_FACE, "Get out of my house!");
				stage = -1;
			} else if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 1) {
				sendNPCDialogue(458, Dialogue.MEAN_FACE, "Get out of my house!");
			} else {
				sendNPCDialogue(458, Dialogue.MEAN_FACE, "What do you need now?");
			}
		}

		@Override
		public void run(int interfaceId, int componentId) {
			if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) > 0) {
				if (stage == 0) {
					if (!player.getInventory().containsItem(552, 1)) {
						stage++;
						if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 1) {
							sendPlayerDialogue(Dialogue.CALM_TALK, "Father Aereck told me to come talk to you about a ghost<br>haunting his graveyard.");
						} else {
							sendPlayerDialogue(Dialogue.CALM_TALK, "I've lost my amulet of ghostspeak.");
						}
					} else {
						stage = -1;
						sendPlayerDialogue(Dialogue.HAPPY_TALKING, "I don't need anything right now.<br>I just wanted to have a chat.");
					}
				} else if (stage == 1) {
					stage++;
					if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 1) {
						sendNPCDialogue(458, Dialogue.MEAN_FACE, "Oh the silly old fool. Here, take this amulet<br>and see if you can communicate with the spectre.");
					} else {
						sendNPCDialogue(458, Dialogue.MEAN_FACE, "Have another one then. But be more careful next time!");
					}
					player.getInventory().addItem(552, 1);
					if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 1)
						player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 2);
				} else if (stage == 2) {
					stage++;
					sendPlayerDialogue(Dialogue.LAUGH_EXCITED, "Thank you. I'll try.");
				} else {
					end();
				}
			}
		}

		@Override
		public void finish() {

		}

	}

	private static boolean hasSkull(Player player) {
		if (player.getInventory().containsItem(553, 1) || player.getQuestManager().isComplete(Quest.RESTLESS_GHOST))
			return true;
		return false;
	}

	private static void refreshSkull(Player player) {
		player.getVars().setVarBit(SKULL_CONF, hasSkull(player) ? 1 : 0);
	}

	public static LoginHandler onLogin = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			refreshSkull(e.getPlayer());
		}
	};
	
	public static ObjectClickHandler handleSkullRock = new ObjectClickHandler(new Object[] { 47713 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getQuestManager().getStage(Quest.RESTLESS_GHOST) == 3) {
				e.getPlayer().sendMessage("You take the skull.");
				e.getPlayer().getInventory().addItem(553, 1);
				refreshSkull(e.getPlayer());
			}
		}
	};

	public static ObjectClickHandler handleCoffin = new ObjectClickHandler(new Object[] { 2145 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getQuestManager().getStage(Quest.RESTLESS_GHOST) == 2) {
				e.getPlayer().getQuestManager().setStage(Quest.RESTLESS_GHOST, 3);
				e.getPlayer().sendMessage("A ghost appears nearby!");
			}
		}
	};
	
	public static NPCClickHandler talkToNpcs = new NPCClickHandler(457, 458) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 1) {
				if (e.getNPC().getId() == 458) {
					e.getPlayer().getDialogueManager().execute(new UrhneyD());
					return;
				} else if (e.getNPC().getId() == 457) {
					if (e.getPlayer().getQuestManager().getStage(Quest.RESTLESS_GHOST) == 3)
						e.getPlayer().getDialogueManager().execute(new RGhostD());
					return;
				}
			}
		}
	};
}