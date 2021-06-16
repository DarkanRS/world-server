package com.rs.game.player.dialogues;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.PlayerLook;

public class MakeOverMage extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		int v = (Integer) parameters[1];
		if (v == 0) {
			sendEntityDialogue(SEND_3_TEXT_CHAT,
					new String[] { NPCDefinitions.getDefs(npcId).getName(), "Hello there! I am know as the Makeover Mage! I have", "spent many years researching magicks that can change", "your physical appearence." }, IS_NPC, npcId, 9827);
		} else if (v == 1) {
			stage = -2;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "That is no different from what you already have. I guess I", "shouldn't charge you if I'm not changing anything." }, IS_NPC, npcId, 9827);
		} else if (v == 2) {
			stage = 19;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "Whew! That was lucky." }, IS_NPC, npcId, 9827);
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "I call it a 'makeover'.", "Would you like to perform my magicks on you?" }, IS_NPC, npcId, 9827);
		} else if (stage == 0) {
			stage = 1;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Tell me more about this 'makeover'.", "Sure, do it.", "No thanks.", "Cool amulet! Can I have one?");
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				stage = 2;
				sendPlayerDialogue(9827, "Tell me more about this 'makeover'.");
			} else if (componentId == OPTION_2) {
				stage = 11;
				sendPlayerDialogue(9827, "Sure, do it.");
			} else if (componentId == OPTION_3) {
				stage = 13;
				sendPlayerDialogue(9827, "No thanks. I'm happy as Saradomin made me.");
			} else {
				stage = 14;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Cool amulet! Can I have one?" }, IS_PLAYER, player.getIndex(), 9827);
			}
		} else if (stage == 2) {
			stage = 3;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "Why, of course! Basically, and I will explain so that", "you understand it correctly," }, IS_NPC, npcId, 9827);
		} else if (stage == 3) {
			stage = 4;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "I use my secret magical technique to melt your body down", "into a puddle of its elements" }, IS_NPC, npcId, 9827);

		} else if (stage == 4) {
			stage = 5;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "When I have broken down all components of your body, I", "then rebuilt it into the form I am thinking of." }, IS_NPC, npcId, 9827);

		} else if (stage == 5) {
			stage = 6;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "Or, you know, something vaguely close enough, anyway." }, IS_NPC, npcId, 9827);
		} else if (stage == 6) {
			stage = 7;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Uh... that doesn't sound particualry safe to me." }, IS_PLAYER, player.getIndex(), 9827);
		} else if (stage == 7) {
			stage = 8;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "It's as safe as houses. Why, I have only had thirty-six", "major accidents this month!" }, IS_NPC, npcId, 9827);

		} else if (stage == 8) {
			stage = 9;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "So what do you say? Feel like a change?" }, IS_NPC, npcId, 9827);
		} else if (stage == 9) {
			stage = 10;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Sure do it.", "No thanks.");
		} else if (stage == 10) {
			if (componentId == OPTION_1) {
				stage = 11;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Sure, do it." }, IS_PLAYER, player.getIndex(), 9827);
			} else {
				stage = 13;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "No thanks. I'm happy as Saradomin made me." }, IS_PLAYER, player.getIndex(), 9827);
			}
		} else if (stage == 11) {
			stage = 12;
			sendEntityDialogue(SEND_3_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "You, of course, agree that if by some accident you are", "turned into a frog you have no rights for compensation or", "refund." }, IS_NPC,
					npcId, 9827);
		} else if (stage == 12) {
			PlayerLook.openMageMakeOver(player);
			end();
		} else if (stage == 13) {
			stage = -2;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "Ehhh...suit yourself." }, IS_NPC, npcId, 9827);
		} else if (stage == 14) {
			stage = 15;
			sendEntityDialogue(SEND_3_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "No problem, but please remember that the amulet I will", "sell you is only a copy of my own. It contains no magical",
					"powers and, as such, it will only cost you 100 coins." }, IS_NPC, npcId, 9827);
		} else if (stage == 15) {
			stage = 16;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Sure, here you go.", "No way! That's too expensive.");
		} else if (stage == 16) {
			if (componentId == OPTION_1) {
				if (!player.getInventory().containsItem(995, 100))
					end();
				else {
					stage = 17;
					sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Sure, here you go." }, IS_PLAYER, player.getIndex(), 9827);
				}
			} else {
				stage = -2;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "No way! That's too expensive." }, IS_PLAYER, player.getIndex(), 9827);
			}
		} else if (stage == 17) {
			stage = 18;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { "", "You receive an amulet in exchange for 100 coins." }, IS_ITEM, 7803, SEND_NO_EMOTE);
			player.getInventory().deleteItem(995, 100);
			player.getInventory().addItem(7803, 1);
		} else if (stage == 18) {
			stage = 0;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "I can alter you physical form if you wish. Would you like", " me to perform my magicks on you?" }, IS_NPC, npcId, 9827);
		} else if (stage == 19) {
			stage = 20;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "What was?" }, IS_PLAYER, player.getIndex(), 9827);
		} else if (stage == 20) {
			stage = 21;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Nothing! It's all fine you seem alive anyway." }, IS_NPC, npcId, 9827);
		} else if (stage == 21) {
			stage = -2;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "	Uh, thanks, I guess." }, IS_PLAYER, player.getIndex(), 9827);
		} else
			end();

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
