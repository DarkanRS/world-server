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
package com.rs.game.content.quests.princealirescue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

import static com.rs.game.content.world.doors.Doors.handleDoor;

@QuestHandler(Quest.PRINCE_ALI_RESCUE)
@PluginEventHandler
public class PrinceAliRescue extends QuestOutline {

	//Quest Stages
	public final static int NOT_STARTED = 0;
	public final static int STARTED = 1;
	public final static int GEAR_CHECK = 2;

	//items
	public final static int BEER = 1917;
	public final static int PINK_SKIRT = 1013;

	//key stuff
	public final static int SOFT_CLAY = 1761;
	public final static int BRONZE_KEY = 2418;
	public final static int KEY_PRINT = 2423;
	public final static int BRONZE_BAR = 2349;

	//wig items
	public final static int BALL_WOOL = 1759;
	public final static int WIG = 2421;
	public final static int BLONDE_WIG = 2419;
	public final static int YELLOW_DYE = 1765;

	//Paste items
	public final static int ASHES = 592;
	public final static int REDBERRY = 1951;
	public final static int POT_OF_FLOUR = 1933;
	public final static int WATER_BUCKET = 1929;
	public final static int PASTE = 2424;

	public final static int ROPE = 954;

	//NPCS
	public final static int NED = 918;
	public final static int AGGIE = 922;
	public final static int HASSAN = 923;
	public final static int OSMAN = 5282;
	public final static int LEELA = 915;
	public final static int LADY_KELI = 919;
	public final static int JOE = 916;
	public final static int PRINCE_ALI1 = 920;
	public final static int PRINCE_ALI2 = 921;

	//Places
	public final static int JAIL_REGION_ID = 12338; //Update to new logic
	@Override
	public int getCompletedStage() {
		return 5;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("Prince Ali of Al Kharid has been kidnapped by ");
			lines.add("the scheming Lady Keli. You are hired to stage a rescue mission.");
			lines.add("");
			lines.add("Speak to Chancellor Hassan at the Al-Kharid palace  to start");
			lines.add("your mission.");
			lines.add("");
			break;
		case STARTED:
			lines.add("The mission has started, I must speak to Osman outside the palace");
			lines.add("to the northwest by the Alkharid Scimitar shop.");
			lines.add("");
			break;
		case GEAR_CHECK:
			lines.add("Prince Ali has been taken for randsom! To break");
			lines.add("him out of his prison we are going to need to");
			lines.add("infiltrate the jail and get him out...");
			lines.add("");
			lines.add("For this we will need a skin paste, a yellow wig, some beer,");
			lines.add("the jail cell key and a pink skirt.");
			lines.add("");
			lines.add("For the skin paste I can talk to Aggie the witch in ");
			lines.add("Draynor.");
			lines.add("");
			lines.add("For the yellow wig I can talk to Ned in Draynor.");
			lines.add("");
			lines.add("I remember a pink skirt being sold in Varrock's");
			lines.add("clothing store");
			lines.add("");
			lines.add("For the jail key I will have to be really sneaky and");
			lines.add("get a key print from Lady Keli. Afterward I can get a");
			lines.add("bronze bar turned into a key by talking to Osman");
			lines.add("in Alkharid then pick up the key delivery from Leela.");
			lines.add("");
			lines.add("Before I start the breakout I will need the following items:");
			lines.add((player.getInventory().containsItem(BEER, 3) ? "<str>":"")+"3 beer");
			lines.add((player.getInventory().containsItem(ROPE, 1) ? "<str>":"")+"Rope");
			lines.add((player.getInventory().containsItem(BRONZE_KEY, 1) ? "<str>":"")+"A jail key");
			lines.add((player.getInventory().containsItem(BLONDE_WIG, 1) ? "<str>":"")+"A yellow wig");
			lines.add((player.getInventory().containsItem(PASTE, 1) ? "<str>":"")+"Skin paste");
			lines.add((player.getInventory().containsItem(PINK_SKIRT, 1) ? "<str>":"")+"A pink skirt");
			lines.add("");
			lines.add("Once I gathered all these supplies I should talk to Leela.");
			lines.add("");
			break;
		case 4:
			lines.add("");
			lines.add("");
			break;
		case 5:
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

	public static boolean hasAllBreakOutItems(Player p) {
		return (p.getInventory().containsItem(BEER, 3) && p.getInventory().containsItem(BRONZE_KEY, 1) &&
				p.getInventory().containsItem(PASTE, 1) && p.getInventory().containsItem(BLONDE_WIG, 1) &&
				p.getInventory().containsItem(PINK_SKIRT, 1) && p.getInventory().containsItem(ROPE, 1));
	}

	public static ObjectClickHandler handleJailCellDoor = new ObjectClickHandler(new Object[] { 3436 }, e -> {
		if(e.getObject().getTile().matches(Tile.of(3128, 3243, 0))) {
			if (e.getPlayer().getInventory().containsItem(BRONZE_KEY, 1))
				handleDoor(e.getPlayer(), e.getObject());
			else
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.CALM_TALK, "It appears to need a key...");
					}
				});
			return;
		}


		if(e.getPlayer().getInventory().containsItem(BRONZE_KEY, 1)) {
			for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 2))
				if(npc.getId() == LADY_KELI) {
					e.getPlayer().sendMessage("You'd better get rid of Lady Keli before trying to go through there.");
					return;
				}
			handleDoor(e.getPlayer(), e.getObject());
		}
		else
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.CALM_TALK, "It appears to need a key...");
				}
			});
	});

	public static ItemOnItemHandler handleRedYellowDyes = new ItemOnItemHandler(YELLOW_DYE, new int[] {WIG}, e -> {
		if(e.usedWith(WIG, YELLOW_DYE)) {
			e.getPlayer().getInventory().replace(e.getItem2(), new Item(BLONDE_WIG, 1));
			e.getPlayer().getInventory().deleteItem(e.getItem1().getSlot(), e.getItem1());
		}
	});

	public static ItemOnNPCHandler handleLadyKeliRope = new ItemOnNPCHandler(LADY_KELI, e -> {
		if(e.getItem().getId() != ROPE || e.getPlayer().isQuestComplete(Quest.PRINCE_ALI_RESCUE))
			return;
		Player p = e.getPlayer();
		if(p.getQuestManager().getAttribs(Quest.PRINCE_ALI_RESCUE).getB("Joe_guard_is_drunk") && p.getInventory().containsItem(BRONZE_KEY, 1) &&
				p.getInventory().containsItem(PASTE, 1) && p.getInventory().containsItem(BLONDE_WIG, 1) &&
				p.getInventory().containsItem(PINK_SKIRT, 1) && p.getInventory().containsItem(ROPE, 1)) {
			p.getInventory().removeItems(new Item(ROPE, 1));
			e.getNPC().setRespawnTask(90);
			e.getNPC().finish();
		} else
			p.startConversation(new Conversation(p) {
				{
					addSimple("You cannot tie Keli up until you have all equipment and disabled the guard!");
					create();
				}
			});
	});




	@Override
	public void complete(Player player) {
		player.getInventory().addCoins(700);
		getQuest().sendQuestCompleteInterface(player, 6964, "700 coins");
	}


}
