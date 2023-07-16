// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;

public enum Skillcapes {
	Attack(9747, 9748, 9749, 25324, "a master in the fine art of attacking"),
	Defence(9753, 9754, 9755, 25326, "a master in the fine art of defence"),
	Strength(9750, 9751, 9752, 25325, "as strong as is possible"),
	Constitution(9768, 9769, 9770, 25332, "as resilient as is possible"),
	Ranging(9756, 9757, 9758, 25327, "a master in the fine art of ranging"),
	Prayer(9759, 9760, 9761, 25328, "as devoted to prayer as possible"),
	Magic(9762, 9763, 9764, 25329, "as powerful a wizard as I"),
	Cooking(9801, 9802, 9803, 25344, "a master in the culinary arts"),
	Woodcutting(9807, 9808, 9809, 25346, "a master woodsman"),
	Fletching(9783, 9784, 9785, 25337, "a master in the fine art of fletching"),
	Fishing(9798, 9799, 9800, 25343, "a master fisherman"),
	Firemaking(9804, 9805, 9806, 25345, "a master of fire"),
	Crafting(9780, 9781, 9782, 25336, "a master craftsman"),
	Smithing(9795, 9796, 9797, 25342, "a master blacksmith"),
	Mining(9792, 9793, 9794, 25341, "a master miner"),
	Herblore(9774, 9775, 9776, 25334, "a master herbalist"),
	Agility(9771, 9772, 9773, 25333, "as agile as possible"),
	Thieving(9777, 9778, 9779, 25335, "a master thief"),
	Slayer(9786, 9787, 9788, 25338, "an incredible student"),
	Farming(9810, 9811, 9812, 25347, "a master farmer"),
	Runecrafting(9765, 9766, 9767, 25330, "a master runecrafter"),
	Hunter(9948, 9949, 9950, 25339, "a master hunter"),
	Construction(9789, 9790, 9791, 25331, "a master home builder"),
	Summoning(12169, 12170, 12171, 25348, "a master summoner"),
	Dungeoneering(18508, 18509, 18510, 19709, "a master dungeon delver");

	public final int untrimmed, trimmed, hood, master;
	public final String verb;

	private Skillcapes(int untrimmed, int trimmed, int hood, int master, String verb) {
		this.untrimmed = untrimmed;
		this.trimmed = trimmed;
		this.hood = hood;
		this.verb = verb;
		this.master = master;
	}

	private Dialogue getGiveCapeDialogue(Player player, int npcId, boolean masterCape) {
		if (!player.getInventory().hasCoins(masterCape ? 120000 : 99000))
			return new Dialogue(new PlayerStatement(HeadE.SAD_MILD, "But, unfortunately, I was mistaken.")).addNext(new NPCStatement(npcId, HeadE.NO_EXPRESSION, "Well, come back and see me when you do.")).finish();
		return new Dialogue(new NPCStatement(npcId, HeadE.CHEERFUL, ordinal() == Constants.FIREMAKING ? "I'm sure you'll look hot in that cape." : "Excellent! Wear that cape with pride my friend."), () -> {
			player.getInventory().removeCoins(masterCape ? 120000 : 99000);
			if (!masterCape)
				player.getInventory().addItem(hood, 1);
			player.getInventory().addItem(masterCape ? master : player.getSkills().checkMulti99s() ? trimmed : untrimmed, 1);
		});
	}

	public Dialogue getOffer99CapeDialogue(Player player, int npcId) {
		Dialogue start = new Dialogue()
				.addNPC(npcId, HeadE.CHEERFUL, "Ah, this is a Skillcape of " + name() + ". I have mastered the art of " + name().toLowerCase()
						+ " and wear it proudly to show others.")
				.addPlayer(HeadE.SKEPTICAL, "Hmm, interesting.");
		if (player.getSkills().getLevelForXp(ordinal()) >= 99)
			start.addNPC(npcId, HeadE.NO_EXPRESSION, "Ah, but I see you are already "+verb+", perhaps you have come to me to purchase a Skillcape of "+name()+", and thus join the elite few who have mastered this exacting skill?")
			.addOptions("Choose an option", new Options() {
				@Override
				public void create() {
					option("Yes, I'd like to buy one please.", new Dialogue()
							.addNPC(npcId, HeadE.NO_EXPRESSION, "Most certainly; unfortunately being such a prestigious item, they are appropriately expensive. I'm afraid I must ask you for 99,000 gold.")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("99,000 coins? That's much too expensive.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "99,000 coins? That's much too expensive.")
											.addNPC(npcId, HeadE.CALM_TALK, "...")
									);
									option("I think I have the money right here, actually.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I think I have the money right here, actually.")
											.addNext(getGiveCapeDialogue(player, npcId, false))
									);

								}
							}));
					if (player.getSkills().is120(Skillcapes.this.ordinal()))
						option("I've mastered this skill. Is there anything else?", new Dialogue()
								.addNPC(npcId, HeadE.AMAZED, "I've been saving this master cape for someone truly " + verb + ". Is that really you?")
								.addPlayer(HeadE.CONFUSED, "I think so. I mean I really have mastered everything there is to know.")
								.addNPC(npcId, HeadE.AMAZED, "I can see that! I would be glad to offer you this cape.")
								.addOption("Buy a master cape for 120,000 coins?", "Yes, please.", "No, thanks.")
								.addNext(getGiveCapeDialogue(player, npcId, true)));
					option("Nevermind.", new Dialogue()
							.addNPC(npcId, HeadE.NO_EXPRESSION, "No problem; there are many other adventurers who would love the opportunity to purchase such a prestigious item! You can find me here if you change your mind."));
				}
			});
		else
			start.addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Please tell me more about skillcapes.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Please tell me more about skillcapes.")
							.addNPC(npcId, HeadE.CALM_TALK, "Of course. Skillcapes are a symbol of achievement. Only people who have mastered a skill and " +
									"reached level 99 can get their hands on them and gain the benefits they carry.")
					);
					option("Bye.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Bye.")
							.addNPC(npcId, HeadE.CALM_TALK, "Bye.")
					);
				}
			});
		return start.finish();
	}
}
