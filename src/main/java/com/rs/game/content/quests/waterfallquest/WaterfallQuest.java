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
package com.rs.game.content.quests.waterfallquest;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.WATERFALL_QUEST)
@PluginEventHandler
public class WaterfallQuest extends QuestOutline {

	@Override
	public int getCompletedStage() {
		return 6;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
		case 0:
			lines.add("I can start this quest by speaking with Almera");
			lines.add("by the waterfall south of barbarian assault.");
			break;
		case 1:
			lines.add("Almera is worried about her son Hudon. I should find");
			lines.add("him and see what he is up to.");
			break;
		case 2:
			lines.add("Hudon mentioned some treasure he is looking for inside the");
			lines.add("waterfall.");
			lines.add("Perhaps I could find some information about this treasure");
			lines.add("somewhere nearby.");
			break;
		case 3:
			lines.add("I read that there is a gnome named Golrie that may be able");
			lines.add("to help me unlock the secrets behind the Baxtorian treasure.");
			lines.add("Last he was seen was near Yanille by the Tree Gnome Village.");
			break;
		case 4:
			lines.add("I freed Golrie from his prison underneath the Tree Gnome");
			lines.add("Village and found a small pebble that used to belong to");
			lines.add("the elf queen, Glarial.");
			lines.add("Maybe it could grant access to the waterfall somehow.");
			break;
		case 5:
			lines.add("I have located the treasure inside the waterfall.");
			lines.add("Now to find a safe way to take the treasure without");
			lines.add("triggering the water trap and being washed outside.");
			break;
		case 6:
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
		player.getSkills().addXpQuest(Constants.ATTACK, 13750);
		player.getSkills().addXpQuest(Constants.STRENGTH, 13750);
		player.getInventory().addItem(299, 40, true);
		player.getInventory().addItem(1601, 2, true);
		player.getInventory().addItem(2357, 2, true);

		getQuest().sendQuestCompleteInterface(player, 1601, "13,750 Attack XP", "13,750 Strength XP", "2 diamonds", "2 gold bars", "40 Mithril seeds");
	}

	public static ItemClickHandler handleBonesackTele = new ItemClickHandler(new Object[] { 292 }, new String[] { "Read" }, e -> {
		if (e.getPlayer().getQuestManager().getStage(Quest.WATERFALL_QUEST) == 2) {
			e.getPlayer().sendMessage("You read the book and find that a gnome named Golrie may be able to help find a way into the falls.");
			e.getPlayer().getQuestManager().setStage(Quest.WATERFALL_QUEST, 3);
		} else
			e.getPlayer().sendMessage("You have already read this book. Golrie should be able to help.");
	});

	public static NPCClickHandler handleHudon = new NPCClickHandler(false, new Object[] { "Hudon" }, e -> {
		if (e.getOpNum() == 1) {				
			e.getNPC().resetWalkSteps();
			e.getPlayer().resetWalkSteps();
			e.getPlayer().faceEntity(e.getNPC());
			e.getNPC().faceEntity(e.getPlayer());
			e.getPlayer().startConversation(new HudonD(e.getPlayer(), e.getNPC().getId()));
		}
	});

	public static NPCClickHandler handleAlmera = new NPCClickHandler(new Object[] { 304 }, e -> e.getPlayer().startConversation(new AlmeraD(e.getPlayer())));
	public static NPCClickHandler handleGolrie = new NPCClickHandler(new Object[] { 306 }, e -> e.getPlayer().startConversation(new GolrieD(e.getPlayer())));

	public static ObjectClickHandler keyCrateSearch = new ObjectClickHandler(new Object[] { 31139 }, Tile.of(2593, 9881, 0), e -> {
		e.getPlayer().sendMessage("You search the crate.");
		if (e.getPlayer().getInventory().containsItem(293, 1))
			e.getPlayer().sendMessage("You find nothing of interest.");
		else {
			e.getPlayer().getInventory().addItem(293, 1);
			e.getPlayer().sendMessage("You find an old key.");
		}
	});
	
	public static ObjectClickHandler onObjectClick = new ObjectClickHandler(new Object[] { 1987, 1990, 1757, 5251, 5250, 10283, 2020, 33047, 33066, 2022, 2014, 37247, 31139, 2002, 1991, 1989 }, e -> {
		if (e.getObjectId() == 1987) {
			e.getPlayer().sendMessage("You board the log raft and crash on a small spit of land.");
			e.getPlayer().setNextTile(Tile.of(2512, 3481, 0));
		} else if (e.getObjectId() == 1990) {
			if (e.getPlayer().getQuestManager().getStage(Quest.WATERFALL_QUEST) >= 3 && !e.getPlayer().getInventory().containsItem(298, 1)) {
				e.getPlayer().sendMessage("You find a large old key.");
				e.getPlayer().getInventory().addItem(298, 1);
			} else
				e.getPlayer().sendMessage("You find nothing interesting.");
		} else if (e.getObjectId() == 1757)
			e.getPlayer().useStairs(828, Tile.of(e.getPlayer().getX(), e.getPlayer().getY() - 6400, 0), 1, 2);
		else if (e.getObjectId() == 5251)
			e.getPlayer().useStairs(828, Tile.of(e.getPlayer().getX(), e.getPlayer().getY() - 6400, 0), 1, 2);
		else if (e.getObjectId() == 5250)
			e.getPlayer().useStairs(828, Tile.of(e.getPlayer().getX(), e.getPlayer().getY() + 6400, 0), 1, 2);
		else if (e.getObjectId() == 10283) {
			e.getPlayer().sendMessage("You try to swim down the river but you get swept away and over the waterfall.");
			e.getPlayer().setNextTile(Tile.of(2531, 3413, 0));
			e.getPlayer().applyHit(new Hit(e.getPlayer(), Utils.random(20, 56), HitLook.TRUE_DAMAGE));
		} else if (e.getObjectId() == 2020) {
			e.getPlayer().sendMessage("You slip from the tree and fall down the waterfall.");
			e.getPlayer().setNextTile(Tile.of(2531, 3413, 0));
			e.getPlayer().applyHit(new Hit(e.getPlayer(), Utils.random(20, 56), HitLook.TRUE_DAMAGE));
		} else if (e.getObjectId() == 33047) {
			if (e.getPlayer().getInventory().containsItem(295, 1))
				e.getPlayer().sendMessage("The chest is empty.");
			else {
				e.getPlayer().sendMessage("You find Glarial's amulet.");
				e.getPlayer().getInventory().addItem(295, 1);
			}
		} else if (e.getObjectId() == 33066) {
			if (e.getPlayer().getInventory().containsItem(296, 1))
				e.getPlayer().sendMessage("The tomb is empty.");
			else {
				e.getPlayer().sendMessage("You find Glarial's urn.");
				e.getPlayer().getInventory().addItem(296, 1);
			}
		} else if (e.getObjectId() == 2022) {
			e.getPlayer().sendMessage("You climb in the barrel and safely land at the bottom of the waterfall.");
			e.getPlayer().setNextTile(Tile.of(2531, 3413, 0));
		} else if (e.getObjectId() == 2014) {
			e.getPlayer().sendMessage("A powerful rush of water floods out of the cave and sweeps you down river.");
			e.getPlayer().setNextTile(Tile.of(2531, 3413, 0));
			e.getPlayer().applyHit(new Hit(e.getPlayer(), Utils.random(20, 56), HitLook.TRUE_DAMAGE));
		} else if (e.getObjectId() == 37247) {
			if (e.getPlayer().getEquipment().getAmuletId() == 295 || e.getPlayer().getInventory().containsItem(295, 1))
				e.getPlayer().setNextTile(Tile.of(2575, 9862, 0));
			else {
				e.getPlayer().sendMessage("A powerful rush of water floods out of the cave and sweeps you down river.");
				e.getPlayer().setNextTile(Tile.of(2531, 3413, 0));
				e.getPlayer().applyHit(new Hit(e.getPlayer(), Utils.random(20, 56), HitLook.TRUE_DAMAGE));
			}
		} else if (e.getObjectId() == 2002 || e.getObjectId() == 1991)
			e.getPlayer().sendMessage("The door is locked.");
		else if (e.getObjectId() == 1989)
			if (e.getPlayer().getQuestManager().getStage(Quest.WATERFALL_QUEST) >= 2 && !e.getPlayer().getInventory().containsItem(292, 1)) {
				e.getPlayer().getInventory().addItem(292, 1);
				e.getPlayer().sendMessage("You find an old book on the subject of Baxtorian!");
			} else
				e.getPlayer().sendMessage("You find nothing of interest.");
	});

	public static ItemOnObjectHandler itemOnObjectClose = new ItemOnObjectHandler(new Object[] { 1991, 1992, 2002, 2004, 2006, 2014, 2020 }, e -> {
		if (e.getItem().getId() == 954 && e.getObject().getId() == 2020) {
			e.getPlayer().sendMessage("You carefully climb down the tree using your rope.");
			e.getPlayer().setNextTile(Tile.of(2511, 3463, 0));
		} else if (e.getItem().getId() == 296 && e.getObject().getId() == 2014 && !e.getPlayer().isQuestComplete(Quest.WATERFALL_QUEST)) {
			if (e.getPlayer().getQuestManager().getStage(Quest.WATERFALL_QUEST) == 5) {
				e.getPlayer().getQuestManager().completeQuest(Quest.WATERFALL_QUEST);
				e.getPlayer().setNextTile(Tile.of(e.getPlayer().getX() - 38, e.getPlayer().getY() + 1, 0));
			} else
				e.getPlayer().sendMessage("I don't know how you got in here, but you shouldn't be.");
		} else if (e.getItem().getId() == 295 && e.getObject().getId() == 2006 && !e.getPlayer().isQuestComplete(Quest.WATERFALL_QUEST)) {
			if (e.getPlayer().getQuestManager().getAttribs(Quest.WATERFALL_QUEST).getI("wfWaterRunes") >= 6 && e.getPlayer().getQuestManager().getAttribs(Quest.WATERFALL_QUEST).getI("wfAirRunes") >= 6 && e.getPlayer().getQuestManager().getAttribs(Quest.WATERFALL_QUEST).getI("wfEarthRunes") >= 6) {
				e.getPlayer().setNextTile(Tile.of(e.getPlayer().getX() + 38, e.getPlayer().getY() - 1, 0));
				e.getPlayer().sendMessage("You place the necklace on the statue.");
				e.getPlayer().sendMessage("You hear a loud rumble beneath your feet.");
				e.getPlayer().sendMessage("The ground raises right in front of you!");
				e.getPlayer().getInventory().deleteItem(295, 1);
				e.getPlayer().getQuestManager().setStage(Quest.WATERFALL_QUEST, 5);
			} else {
				e.getPlayer().sendMessage("Water fills the cave and flushes you out and down the river.");
				e.getPlayer().setNextTile(Tile.of(2531, 3413, 0));
				e.getPlayer().applyHit(new Hit(e.getPlayer(), Utils.random(20, 56), HitLook.TRUE_DAMAGE));
			}
		} else if (e.getItem().getId() == 555 && e.getObject().getId() == 2004) {
			e.getPlayer().sendMessage("The rune stone disappears in a puff of smoke.");
			e.getPlayer().getInventory().deleteItem(555, 1);
			e.getPlayer().getQuestManager().getAttribs(Quest.WATERFALL_QUEST).incI("wfWaterRunes");
		} else if (e.getItem().getId() == 556 && e.getObject().getId() == 2004) {
			e.getPlayer().sendMessage("The rune stone disappears in a puff of smoke.");
			e.getPlayer().getInventory().deleteItem(556, 1);
			e.getPlayer().getQuestManager().getAttribs(Quest.WATERFALL_QUEST).incI("wfAirRunes");
		} else if (e.getItem().getId() == 557 && e.getObject().getId() == 2004) {
			e.getPlayer().sendMessage("The rune stone disappears in a puff of smoke.");
			e.getPlayer().getInventory().deleteItem(557, 1);
			e.getPlayer().getQuestManager().getAttribs(Quest.WATERFALL_QUEST).incI("wfEarthRunes");
		} else if (e.getItem().getId() == 294 && e.getObject().getId() == 1992) {
			if (e.getPlayer().getQuestManager().getStage(Quest.WATERFALL_QUEST) >= 4) {
				e.getPlayer().sendMessage("You place the pebble in the gravestone's small indent.");
				e.getPlayer().sendMessage("It fits perfectly.");
				e.getPlayer().sendMessage("You hear a loud creak.");
				e.getPlayer().sendMessage("The gravestone slides back to reveal a ladder going down.");
				e.getPlayer().setNextTile(Tile.of(2556, 3444 + 6400, 0));
			} else
				e.getPlayer().sendMessage("Nothing interesting happens.");
		} else if (e.getItem().getId() == 298 && e.getObject().getId() == 1991) {
			if (e.getPlayer().getX() == 2515 && e.getPlayer().getY() == 9575) {
				e.getPlayer().sendMessage("You unlock the door and go inside.");
				e.getPlayer().setNextTile(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() + 1, 0));
			} else if (e.getPlayer().getX() == 2515 && e.getPlayer().getY() == 9576) {
				e.getPlayer().sendMessage("You unlock the door and go inside.");
				e.getPlayer().setNextTile(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() - 1, 0));
			}
		} else if (e.getItem().getId() == 293 && e.getObject().getId() == 2002) {
			Doors.handleDoor(e.getPlayer(), e.getObject());
			e.getPlayer().sendMessage("You unlock the door and go inside.");
		}
	});

	public static ItemOnObjectHandler itemOnObjectFar = new ItemOnObjectHandler(false, new Object[] { 1996 }, e -> {
		if (e.getItem().getId() == 954)
			if (e.getPlayer().getX() == 2512 && e.getPlayer().getY() == 3476) {
				e.getPlayer().sendMessage("You throw the rope over the rock and carefully pull yourself safely to land.");
				e.getPlayer().setNextTile(Tile.of(2511, 3467, 0));
			} else
				e.getPlayer().sendMessage("You are too far away to do this.");
	});
}
