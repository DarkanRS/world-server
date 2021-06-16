package com.rs.game.player.content.minigames.sorcgarden;

import com.rs.game.player.dialogues.Dialogue;
import com.rs.lib.Constants;

public class OsmanDialogue extends Dialogue {
	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (player.getInventory().containsOneItem(10848, 10849, 10850, 10851)) {
			sendPlayerDialogue(9827, "I have some sq'irk juice for you.");
			stage = -3;
		} else
			sendPlayerDialogue(9827, "I'd like to talk about sq'irks.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -3:
			int totalXp = player.getInventory().getAmountOf(10851) * 350;
			totalXp += player.getInventory().getAmountOf(10848) * 1350;
			totalXp += player.getInventory().getAmountOf(10850) * 2350;
			totalXp += player.getInventory().getAmountOf(10849) * 3000;
			player.getInventory().deleteItem(10848, Integer.MAX_VALUE);
			player.getInventory().deleteItem(10849, Integer.MAX_VALUE);
			player.getInventory().deleteItem(10850, Integer.MAX_VALUE);
			player.getInventory().deleteItem(10851, Integer.MAX_VALUE);
			sendDialogue("Osman imparts some Thieving advice to you ( " + totalXp + " Thieving experience points ) as reward for the sq'irk juice.");
			player.getSkills().addXp(Constants.THIEVING, totalXp);
			stage = -4;
			break;
		case -4:
			sendNPCDialogue(npcId, 9827, "That you very much OH my gosh. If you get some more sq'irks be sure to come back.");
			stage = -5;
			break;
		case -5:
			sendPlayerDialogue(9827, "I will. It's been a pleasure doing business with you.");
			stage = -2;
			break;
		case -1:
			stage = 0;
			this.sendOptionsDialogue("Choose an option.", "Where do I get sq'irks?", "Why can't you get the sq'irks yourself?", "How should I squeeze the fruit?", "Is there a reward for getting these sq'irks?",
					"Whats so good about sq'irk juice then?");
			break;
		case 0:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827, "Where am I meant to find sq'irks?");
				stage = 1;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827, "Why can't you get the sq'irks yourself?");
				stage = 13;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(9827, "How should I squeeze the fruit?");
				stage = 14;
			} else if (componentId == OPTION_4) {
				sendPlayerDialogue(9827, "Is there a reward for getting these sq'irks?");
				stage = 15;
			} else if (componentId == OPTION_5) {
				sendPlayerDialogue(9827, "What's so good about sq'irk juice then?");
				stage = 19;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, 9827, "There is a sorceress near the south-eastern edge of edge of Al Kharid who grows them. Once upon a time we considered each other friends.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(9827, "What happened?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, 9827, "We fell out, and now she won't give me any more fruit.");
			stage = 4;
			break;
		case 4:
			sendPlayerDialogue(9827, "So all I have to do is ask her for some fruit for you?");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, 9827, "I doubt it will be that easy. She is not renowned for her generosity and is very secretive about her garden's location.");
			stage = 6;
			break;
		case 6:
			sendPlayerDialogue(9827, "Oh cmon, it should be easy enough to find.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, 9827, "Her garden has remained hidden even to me - the chief spy of Al Kharid. I belive her garden must be hidden by magical means.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(9827, "This should be an interesting task. How many sq'irks do you want?");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, 9827, "I'll reward you as many as you can get your hands on but could you please squeeze the fruit into a glass first?");
			stage = 10;
			break;
		case 10:
			sendOptionsDialogue("Choose an option.", "I've another question about sq'irks.", "Thanks for the information.");
			stage = 11;
			break;
		case 11:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827, "I've another question about sq'irks.");
				stage = 12;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827, "Thanks for the information.");
				stage = -2;
			}
			break;
		case 12:
			sendNPCDialogue(npcId, 9827, "What would you like to know?");
			stage = -1;
			break;
		case 13:
			sendNPCDialogue(npcId, 9827, "I may have mentioned that I had a falling out with Sorceress. WEll, unsurprisingly, she refuses to giveme any more of her garden's produce.");
			stage = 10;
			break;
		case 14:
			sendNPCDialogue(npcId, 9827, "Use a pestle and mortal to squeeze the sq'irks. Make sure you have an empty glass with you to collect the juice.");
			stage = 10;
			break;
		case 15:
			sendNPCDialogue(npcId, 9827, "Of course there is. I am a generous man. I'll teach you the art of Thieving for your troubles.");
			stage = 16;
			break;
		case 16:
			sendPlayerDialogue(9827, "How much training will you give?");
			stage = 17;
			break;
		case 17:
			sendNPCDialogue(npcId, 9827, "That depends on the quantity and ripeness of the sq'irks you put into the juice.");
			stage = 18;
			break;
		case 18:
			sendPlayerDialogue(9827, "That sounds fair enough.");
			stage = 10;
			break;
		case 19:
			sendNPCDialogue(npcId, 9827, "Ah it's sweet, sweet nectar for a thief or spy; it makes light fingers lighter, fleet fleet flightier and comes in four different colours for those who are easily amused.");
			stage = 20;
			break;
		case 20:
			sendDialogue("Osman starts salivating at the thought of sq'irk juice.");
			stage = 21;
			break;
		case 21:
			sendPlayerDialogue(9827, "It wouldn't have addictive propertie, would it?");
			stage = 22;
			break;
		case 22:
			sendNPCDialogue(npcId, 9827, "It only holds power over those with poor self-control, something which I have an abundance of.");
			stage = 23;
			break;
		case 23:
			sendPlayerDialogue(9827, "I see.");
			stage = 10;
			break;
		default:
			end();
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}