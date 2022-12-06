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
package com.rs.game.content.quests.handlers.dwarfcannon;

import java.util.ArrayList;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.statements.PlayerStatement;
import com.rs.game.content.dialogue.statements.SimpleStatement;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@QuestHandler(Quest.DWARF_CANNON)
@PluginEventHandler
public class DwarfCannon extends QuestOutline {

	public static final int RAILINGS = 14;
	public static final int DWARF_REMAINS = 0;
	public static final int TOOLKIT = 1;
	public static final int NULODIONS_NOTES = 3;
	public static final int AMMO_MOULD = 4;

	@Override
	public int getCompletedStage() {
		return 11;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
		case 0:
			lines.add("I can start this quest by speaking to Lawgof the Dwarven");
			lines.add("Captain of the Black Watch. He is defending an area north-");
			lines.add("west of the Fishing Guild against goblin attack.");
			break;
		case 1:
			lines.add("I have spoken to Captain Lawgof, he recruited me into the");
			lines.add("Black Guard and asked me to help the dwarves.");
			lines.add("My first task is to fix the broken railings");
			lines.add("in the dwarves defensive parameter.");
			break;
		case 2:
			lines.add("I have repaired all the broken railings,");
			lines.add("I should report back to Captain lawgof.");
			break;
		case 3:
			lines.add("Captain Lawgof has asked me to check up on his guards at");
			lines.add("the watchtower to the South of his camp.");
			if (player.getVars().getVar(0) == 1) {
				lines.add("I went to the watchtower where I found the remains of");
				lines.add("Gilob.");
				lines.add("I should take them back to Captain Lawgof.");
			} else if (player.getVars().getVar(0) == 2) {
				lines.add("I went to the watchtower but no one was there.");
				lines.add("I haven't found Gilob, I wonder what happened to him.");
			}
			break;
		case 4:
			lines.add("I gave the remains to Captain Lawgof.");
			lines.add("He sent me to find the Goblin base, South-east of the camp.");
			break;
		case 5:
			lines.add("I found the Goblin's base.");
			lines.add("Next I need to find the Dwarf child, Lollk.");
			break;
		case 6:
			lines.add("I have rescued Lollk and sent him back to the Captain.");
			lines.add("I need to speak to Captain Lawgof again.");
			break;
		case 7:
			lines.add("Now that Lollk has returned safely to the Dwarves.");
			lines.add("Captain Lawgof requested that I help repair the sabotaged cannon.");
			break;
		case 8:
			lines.add("After some tinkering and lucky guess work. I think I have successfully");
			lines.add("repaired the cannon. I should tell Captain Lawgof.");
			break;
		case 9:
			lines.add("I have been asked to perform one last favour and retreive the");
			lines.add("instructions for the cannon. The Dwarf Cannon engineer Nulodion");
			lines.add("should be at the Black Guard camp south of ice mountain.");
			break;
		case 10:
			lines.add("Nulodion has provided me with an ammo mould and instructions.");
			lines.add("I should return to Captain Lawgof.");
			break;
		case 11:
			lines.add("I can now buy a multicannon from Nulodion as a reward.");
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
		player.getSkills().addXpQuest(Constants.CRAFTING, 750);
		getQuest().sendQuestCompleteInterface(player, 1, "750 Crafting XP", "Permission to purchase and use a dwarf multicannon", "Ability to add the ammo mould to your tool belt", "Ability to smith cannonballs");
	}

	public static LoginHandler login = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			updateVars(e.getPlayer());
		}
	};

	public static void updateVars(Player player) {
		if (player.getQuestManager().getStage(Quest.DWARF_CANNON) == 3 && !player.getInventory().containsItem(DWARF_REMAINS))
			player.getVars().setVar(0, 3);
		else if (player.getQuestManager().getStage(Quest.DWARF_CANNON) >= 8 && !player.isQuestComplete(Quest.DWARF_CANNON))
			player.getVars().setVar(0, 8);
	}

	public static ButtonClickHandler handleToolkit = new ButtonClickHandler(409) {
		@Override
		public void handle(ButtonClickEvent e) {
		}
	};

	public static ItemOnObjectHandler handleItemOnRailings = new ItemOnObjectHandler(new Object[] { 15590, 15591, 15592, 15593, 15594, 15595 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (e.getPlayer().getQuestManager().getStage(Quest.DWARF_CANNON) == 1)
				if (e.getItem().getId() == 14) {
					int varbit = e.getObject().getDefinitions().varpBit;
					if ((varbit != -1) && (e.getPlayer().getVars().getVarBit(varbit) == 0)) {
						e.getPlayer().setNextAnimation(new Animation(4190));
						if (Utils.random(4) == 0) {
							e.getPlayer().getVars().saveVarBit(varbit, 1);
							e.getPlayer().getInventory().deleteItem(14, 1);
							e.getPlayer().sendMessage("This railing is now fixed.");
							if (checkRemainingRepairs(e.getPlayer()) == 0)
								e.getPlayer().startConversation(new Dialogue(new PlayerStatement(HeadE.CALM, "I've fixed all these railings now.")));
							e.getPlayer().getQuestManager().setStage(Quest.DWARF_CANNON, 2);
						} else
							failedRepair(e.getPlayer());
					} else
						e.getPlayer().sendMessage("That railing does not need to be repaired.");
				}
		}
	};

	public static ObjectClickHandler handleRailingClick = new ObjectClickHandler(new Object[] { 15590, 15591, 15592, 15593, 15594, 15595 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOption().equals("Inspect"))
				if (e.getPlayer().getVars().getVarBit(e.getObject().getDefinitions().varpBit) == 1)
					e.getPlayer().startConversation(new Dialogue(new SimpleStatement("That railing does not need to be replaced.")));
				else {
					e.getPlayer().startConversation(new Dialogue(new SimpleStatement("This railing is broken and needs to be replaced.")));
					if (e.getPlayer().getInventory().containsItem(14)) {
						e.getPlayer().setNextAnimation(new Animation(4190));
						if (Utils.random(4) == 0) {
							e.getPlayer().getVars().saveVarBit(e.getObject().getDefinitions().varpBit, 1);
							e.getPlayer().getInventory().deleteItem(14, 1);
							if (checkRemainingRepairs(e.getPlayer()) == 0)
								e.getPlayer().getQuestManager().setStage(Quest.DWARF_CANNON, 2);
						} else
							failedRepair(e.getPlayer());
					}
				}
		}
	};

	public static ObjectClickHandler handleLadderClimbUp = new ObjectClickHandler(new Object[] { 11 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getPlayer().transform(0, 0, 1));
			updateVars(e.getPlayer());
		}
	};

	public static ObjectClickHandler handleEnterCaveEntrance = new ObjectClickHandler(new Object[] { 2 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(WorldTile.of(2620, 9796, 0));
			if (e.getPlayer().getQuestManager().getStage(Quest.DWARF_CANNON) == 4)
				e.getPlayer().getQuestManager().setStage(Quest.DWARF_CANNON, 5);
		}
	};

	public static ObjectClickHandler handleClimbMudPile = new ObjectClickHandler(new Object[] { 13 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(WorldTile.of(2627, 3391, 0));
		}
	};

	public static ObjectClickHandler handleDwarfRemains = new ObjectClickHandler(new Object[] { 0 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOption().equals("Take"))
				if ((e.getPlayer().getQuestManager().getStage(Quest.DWARF_CANNON) == 3) && !e.getPlayer().getInventory().containsItem(0)) {
					e.getPlayer().startConversation(new Dialogue(new SimpleStatement("I had better take these remains.")));
					e.getPlayer().getVars().saveVar(0, 0);
					e.getPlayer().getInventory().addItem(0, 1);
				}
		}
	};

	public static ObjectClickHandler handleSearchCrate = new ObjectClickHandler(new Object[] { 1 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOption().equals("Search"))
				if ((e.getPlayer().getQuestManager().getStage(Quest.DWARF_CANNON) == 5))
					e.getPlayer().startConversation(new LollkD(e.getPlayer()));
		}
	};

	public static ObjectClickHandler handleCannonRepair = new ObjectClickHandler(new Object[] { 15597 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOption().equals("Inspect")) {
				if ((e.getPlayer().getQuestManager().getStage(Quest.DWARF_CANNON) == 7)) {
					if (e.getPlayer().containsItem(TOOLKIT)) {
						e.getPlayer().getQuestManager().setStage(Quest.DWARF_CANNON, 8);
						e.getPlayer().sendMessage("You repair the cannon using the toolkit Lawgof gave you.");
						updateVars(e.getPlayer());
					} else
						e.getPlayer().sendMessage("You need a toolkit to repair the cannon.");
				} else
					e.getPlayer().sendMessage("It looks pretty broken.");
			} else if (e.getOption().equals("Fire"))
				e.getPlayer().sendMessage("I should leave that up to the mercenaries.");
			else if (e.getOption().equals("Pick-up"))
				e.getPlayer().sendMessage("That isn't your cannon!");
		}
	};

	public static int checkRemainingRepairs(Player p) {
		int count = 6;
		for (int i = 0; i <= 5; i++)
			if (p.getVars().getVarBit(2240 + i) == 1)
				count--;
		return count;
	}

	public static void failedRepair(Player p) {
		String[] chatMessages = { "You cut your hand on the rusty old railing.", "You strain your back trying to handle the railings.", "You accidentally crush your hand in the railing." };
		String[] playerMessages = { "Ow!", "Urrrgh!", "Gah!", "Oooch!" };
		p.sendMessage(chatMessages[Utils.getRandomInclusive(2)]);
		p.forceTalk(playerMessages[Utils.getRandomInclusive(3)]);
		p.applyHit(new Hit(2, HitLook.TRUE_DAMAGE));

	}
}
