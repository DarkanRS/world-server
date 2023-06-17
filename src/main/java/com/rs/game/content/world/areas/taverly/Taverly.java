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
package com.rs.game.content.world.areas.taverly;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.wolfwhistle.WolfWhistle;
import com.rs.game.content.skills.summoning.Summoning;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PickupItemHandler;

@PluginEventHandler
public class Taverly {

	public static ObjectClickHandler handleTaverleyHouseStaircase = new ObjectClickHandler(new Object[] { 66637, 66638 }, e -> {
		Player p = e.getPlayer();
		GameObject o = e.getObject();
		Tile tile = o.getTile();
		if (e.getObjectId() == 66637) {
			if (tile.isAt(2928, 3445, 0)) {
				if (p.getQuestManager().getStage(Quest.WOLF_WHISTLE) == WolfWhistle.WOLPERTINGER_MATERIALS) {
					if (!p.getInventory().containsItem(WolfWhistle.EMBROIDERED_POUCH)
							&& !p.getBank().containsItem(WolfWhistle.EMBROIDERED_POUCH, 1)) {
						p.forceTalk("Okay, that pouch has to be here somewhere...");
					}
				}
			}
			tile = switch (o.getRotation()) {
				case 0 -> tile.transform(0, 2, 1);
				case 1 -> tile.transform(2, 0, 1);
				case 2 -> tile.transform(0, -1, 1);
				default -> tile.transform(-1, 0, 1);
			};
		} else if (e.getObjectId() == 66638) {
			tile = switch (o.getRotation()) {
				case 0 -> tile.transform(-1, -1, -1);
				case 1 -> tile.transform(-1, 1, -1);
				case 2 -> tile.transform(1, 2, -1);
				default -> tile.transform(2, -1, -1);
			};
		}
		p.useStairs(-1, tile, 0, 0);
	});

	public static ObjectClickHandler handleWell = new ObjectClickHandler(new Object[] { 67498 }, e -> {
		Player p = e.getPlayer();

		if (p.getQuestManager().getStage(Quest.WOLF_WHISTLE) == WolfWhistle.NOT_STARTED) {
			p.startConversation(new Dialogue()
					.addSimple("I'm sure there is nothing down there. It's just an old, dry well that is making funny echoes.")
				);
		} else if (p.getQuestManager().getStage(Quest.WOLF_WHISTLE) == WolfWhistle.PIKKUPSTIX_HELP) {
			/* TODO: start cutscene
			 * set up cutscene props
			 * cutscene fade to black, fade to white at bottom of well
			 * .addPlayer(HeadE.ANGRY, "All right you trolls, let the druid go!")
			 * zoom out to room
			 * .addPlayer(HeadE.WORRIED, "Wow...there certainly are a lot of you...")
			 * .addNPC(WOLF_MEAT, HeadE.LAUGH, "Hur hur hur, more food come to us!")
			 * .addPlayer(HeadE.ANGRY, "I warn you, you'd better let him go!")
			 * .addNPC(WOLF_BONES, HeadE.SHAKE, "Or what? Wolf Bones tink you're not gonna last long!")
			 * .addNPC(WOLF_MEAT, HeadE.LAUGH, "Specially not if we cut 'em into little bitty bites!")
			 * .addPlayer(HeadE.TERRIFIED, "Bowloftrix! Don't worry. I'm going to go and get help!")
			 * .addNPC(BOWLOFTRIX, HeadE.?, "Please hurry!")
			 * If player has already been in the well another different cutscene plays.
			 * .addPlayer(HeadE.ANGRY, "Alright, trolls. Time for round two!")
			 */
		} else {
			// TODO: create well instance filled with trolls
		}
	});

	public static ObjectClickHandler handleSummoningObelisk = new ObjectClickHandler(new Object[] { 67036 }, e -> {
		Player p = e.getPlayer();

		switch (e.getOption()) {
			case "Infuse-pouch" -> {
				if (p.getQuestManager().getStage(Quest.WOLF_WHISTLE) == WolfWhistle.WOLPERTINGER_CREATION) {
					if (WolfWhistle.wolfWhistleObeliskReadyToInfusePouch(p)) {
						WolfWhistle.doWolpertingerPouchCreation(p, e.getObject());
						break;
					}
				}
				Summoning.openInfusionInterface(p, false);
				break;
			}
			case "Renew-points" -> {
				int summonLevel = p.getSkills().getLevelForXp(Constants.SUMMONING);
				if (p.getSkills().getLevel(Constants.SUMMONING) < summonLevel) {
					p.lock(3);
					p.setNextAnimation(new Animation(8502));
					p.getSkills().set(Constants.SUMMONING, summonLevel);
					p.sendMessage("You have recharged your Summoning points.", true);
					break;
				}
				p.sendMessage("You already have full Summoning points.");
			}
		}
	});

	public static ObjectClickHandler handleTaverlyDungeonOddWall = new ObjectClickHandler(new Object[] { 2117 }, e -> {
		Doors.handleDoor(e.getPlayer(), e.getObject(), -1);
	});

	public static PickupItemHandler zammyWines = new PickupItemHandler(new Object[] { 245 }, new Tile[] { Tile.of(2946, 3474, 0), Tile.of(2946, 3473, 0) }, e -> {
		if (!e.isTelegrabbed()) {
			e.getPlayer().applyHit(new Hit(e.getPlayer(), 50, Hit.HitLook.TRUE_DAMAGE));
			for (NPC n : e.getPlayer().queryNearbyNPCsByTileRange(7, n -> n.getId() == 189)) {
				if (n.isDead())
					continue;
				n.forceTalk("Hands off Zamorak's wine!");
				n.setTarget(e.getPlayer());
			}
			e.cancelPickup();
		}
	});
}
