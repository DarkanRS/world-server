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
package com.rs.game.content.skills.agility;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.List;

@PluginEventHandler
public class WerewolfAgility {
	public static class WolfAgilityTrainer extends NPC {
		public WolfAgilityTrainer(int id, Tile tile) {
			super(id, tile);
			if(id == 1661)
				setRandomWalk(false);

		}

		@Override
		public void processNPC() {
			super.processNPC();
			int option = Utils.random(400);
			switch (option) {
				case 0 -> setNextForceTalk(new ForceTalk("Let the bloodlust take you!!"));
				case 1 -> setNextForceTalk(new ForceTalk("You're the slowest wolf I've ever had the misfortune to witness"));
				case 2 -> setNextForceTalk(new ForceTalk("I never really wanted to be an agility trainer"));
				case 3 -> setNextForceTalk(new ForceTalk("Claws first - think later."));
				case 4 -> setNextForceTalk(new ForceTalk("When you're done there's a human with your name on it!!"));
				case 5 -> setNextForceTalk(new ForceTalk("Get on with it - you need your shiskers plucking!!!!"));
				case 6 -> setNextForceTalk(new ForceTalk("Remember - a slow wolf is a hungry wolf!!"));
				case 7 -> setNextForceTalk(new ForceTalk("It'll be worth it when you hunt!!"));
				case 8 -> setNextForceTalk(new ForceTalk("Let's see those powerful backlegs at work!!"));
				case 9 -> setNextForceTalk(new ForceTalk("Imagine the smell of blood in your nostrils!!!"));
			}
		}
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { "Agility Trainer", "Agility Boss" }, (npcId, tile) -> new WolfAgilityTrainer(npcId, tile));
	public static NPCClickHandler handleAgilityTrainer = new NPCClickHandler(new Object[]{1664}, e -> {
		if(e.getPlayer().getInventory().containsItem(4179)) {
			int stickCount = e.getPlayer().getInventory().getAmountOf(4179);
			e.getPlayer().getInventory().removeItems(new Item(4179, stickCount));
			e.getPlayer().getSkills().addXp(Skills.AGILITY, 380*stickCount);
			e.getPlayer().incrementCount("Werewolf agility sticks fetched");
		}
	});

	public static ObjectClickHandler handleZipLine = new ObjectClickHandler(false, new Object[] { 5139, 5140, 5141 }, e -> {
		Tile endTile = Tile.of(3528, 9873, 0);
		e.getPlayer().walkToAndExecute(Tile.of(3528, 9910, 0), () -> {
			e.getPlayer().lock();
			e.getPlayer().getTasks().scheduleTimer(1, 0, ticks -> {
				switch(ticks) {
					default -> { return true; }
					case 0 -> {
						e.getPlayer().lock();
						e.getPlayer().faceSouth();
						List<NPC> npcs = e.getPlayer().queryNearbyNPCsByTileRange(1, (npc -> npc.getId() == 1663 && npc.withinDistance(e.getPlayer().getTile(), 4)));
						if(npcs.size() >= 1) {
							switch(Utils.random(3)) {
								case 0 -> npcs.get(0).forceTalk("Now a true test of teeth...");
								case 1 -> npcs.get(0).forceTalk("Don't let the spikes or the blood put you off...");
							}
						}
						return true;
					}
					case 1 -> { e.getPlayer().setNextAnimation(new Animation(1601)); return true; }
					case 2 -> {
						e.getPlayer().forceTalk("WAAAAAARRRGGGHHH!!!!!!");
						e.getPlayer().forceMove(endTile, 1602, 0, 240, () -> {
							e.getPlayer().setNextAnimation(new Animation(-1));
							e.getPlayer().getSkills().addXp(Skills.AGILITY, 200);
							e.getPlayer().incrementCount("Werewolf agility laps completed");
						});
						return true;
					}
					case 11 -> {
						List<NPC> npcs = e.getPlayer().queryNearbyNPCsByTileRange(10, (npc -> npc.getId() == 1664));
						if(npcs.size() >= 1)
							switch(Utils.random(3)) {
								case 0 -> npcs.get(0).forceTalk("Remember - no stick, no agility bonus!");
								case 1 -> npcs.get(0).forceTalk("Don't forget to give me the stick when you're done.");
							}
						return false;
					}
				}
			});
		});
	});

	public static ObjectClickHandler handleStepStones = new ObjectClickHandler(false, new Object[] { 35996 }, e -> {
		if(!Agility.hasLevel(e.getPlayer(), 60) || e.getPlayer().getTile().getY() > 9880)
			return;
		if(e.getObject().getTile().matches(e.getPlayer().getTile()))
			return;
		if (!e.getObject().getTile().withinDistance(e.getPlayer().getTile(), 2))
			return;
		if(e.getObject().getTile().isAt(3538, 9875)) {
			e.getPlayer().walkToAndExecute(Tile.of(3538, 9873, 0), jumpRock(e));
		} else
			jumpRock(e).run();
	});

	private static Runnable jumpRock(ObjectClickEvent e) {
		return () -> {
			if(e.getObject().getTile().isAt(3538, 9875))
				yellFetch(e);
			e.getPlayer().getSkills().addXp(Skills.AGILITY, 10);
			e.getPlayer().forceMove(e.getObject().getTile(), 741, 0, 25);
			e.getPlayer().lock(0);
		};
	}

	private static void yellFetch(ObjectClickEvent e) {
		List<NPC> npcs = e.getPlayer().queryNearbyNPCsByTileRange(8, (npc -> npc.getId() == 1661));
		if (npcs.size() >= 1) {
			npcs.get(0).faceNorth();
			npcs.get(0).forceTalk("FETCH!!!!!");
			WorldTasks.schedule(2, () -> {
				npcs.get(0).setNextAnimation(new Animation(6547));
				World.sendProjectile(npcs.get(0), Tile.of(3540, 9911, 0), 1158, 35, 0, 20, 0.6, 20, 50, p -> World.addGroundItem(new Item(4179), Tile.of(3540, 9911, 0), e.getPlayer()));
			});
		}
	}

	public static ObjectClickHandler handleHurdles = new ObjectClickHandler(new Object[] { 5134, 5133, 5135 }, e -> {
		if(e.getPlayer().getTile().getY() > e.getObject().getY())
			return;
		e.getPlayer().getSkills().addXp(Skills.AGILITY, 20);
		e.getPlayer().forceMove(Tile.of(e.getObject().getX()+(e.getObjectId() == 5134 ? 0 : 1), e.getObject().getY() + 1, 0), 1603, 0, 45);
		e.getPlayer().lock(1);
	});

	public static ObjectClickHandler handleObstaclePipes = new ObjectClickHandler(new Object[] { 5152 }, e -> {
		if(e.getPlayer().getTile().getY() > e.getObject().getY())
			return;
		final Tile toTile = Tile.of(e.getObject().getX(), e.getObject().getY()+4, e.getObject().getPlane());
		e.getPlayer().forceMove(toTile, 10580, 0, 60, () -> e.getPlayer().getSkills().addXp(Skills.AGILITY, 15));
	});

	public static ObjectClickHandler handleRockSlope = new ObjectClickHandler(new Object[] { 5136 }, e -> {
		if(e.getPlayer().getX() < e.getObject().getX())
			return;
		e.getPlayer().forceMove(Tile.of(e.getObject().getX()-2, e.getObject().getY(), 0), 2049, 30, 60, () -> e.getPlayer().getSkills().addXp(Skills.AGILITY, 25));
	});

	public static ObjectClickHandler courseExitLadder = new ObjectClickHandler(new Object[] { 5130 }, e -> e.getPlayer().useLadder(Tile.of(3543, 3463, 0)));
	public static ObjectClickHandler courseEntranceLadder = new ObjectClickHandler(new Object[] { 5132 }, e -> e.getPlayer().useLadder(Tile.of(3549, 9865, 0)));
}

