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
package com.rs.game.content.world.areas.yanille;

import com.rs.game.content.quests.treegnomevillage.dialogues.ElkoyTreeGnomeVillageD;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Yanille {

	public static ObjectClickHandler handleUnderwallTunnelShortcut = new ObjectClickHandler(new Object[] { 9301, 9302 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 14))
			return;
		AgilityShortcuts.crawlUnder(e.getPlayer(), e.getPlayer().transform(0, e.getObjectId() == 9302 ? -5 : 5, 0));
	});

	public static ObjectClickHandler handleMagicGuildStairs = new ObjectClickHandler(new Object[] { 1722, 1723 }, e -> {
		e.getPlayer().setNextTile(e.getPlayer().transform(0, e.getObjectId() == 1722 ? 4 : -4, e.getObjectId() == 1722 ? 1 : -1));
	});

	public static ObjectClickHandler handleTreeGnomeVillageGateSqueeze = new ObjectClickHandler(new Object[] { 2186 }, e -> {
		AgilityShortcuts.sidestep(e.getPlayer(), e.getPlayer().getY() < e.getObject().getY() ? e.getPlayer().transform(0, 1, 0) : e.getPlayer().transform(0, -1, 0));
	});

	public static NPCClickHandler handleMagicGuildArmorShop = new NPCClickHandler(new Object[] { 1658 }, e -> {
		if (e.getOpNum() == 3)
			ShopsHandler.openShop(e.getPlayer(), "magic_guild_store_robes");
	});

	public static NPCClickHandler handleElkoy = new NPCClickHandler(new Object[] { 473, 474 }, e -> {
		if(e.getOption().equalsIgnoreCase("Talk-to")) {
			e.getPlayer().startConversation(new ElkoyTreeGnomeVillageD(e.getPlayer()).getStart());
		}
		if (e.getOpNum() == 3)
			e.getPlayer().fadeScreen(() -> {
				e.getPlayer().sendMessage("Elkoy leads you through the maze...");
				e.getPlayer().setNextTile(e.getNPC().getId() == 473 ? Tile.of(2515, 3160, 0) : Tile.of(2502, 3193, 0));
			});
	});

	public static ObjectClickHandler handleMagicGuildPortals = new ObjectClickHandler(new Object[] { 2518, 2156, 2157 }, e -> {
		switch(e.getObjectId()) {
		case 2518:
			e.getPlayer().sendOptionDialogue("Teleport to Thormac's Tower?", ops -> {
				ops.add("Yes, teleport me to Thormac's Tower.", () -> e.getPlayer().setNextTile(Tile.of(2702, 3403, 0)));
				ops.add("Not right now.");
			});
			break;
		case 2156:
			e.getPlayer().sendOptionDialogue("Teleport to the Wizard's Tower?", ops -> {
				ops.add("Yes, teleport me to the Wizard's Tower.", () -> e.getPlayer().setNextTile(Tile.of(3109, 3164, 0)));
				ops.add("Not right now.");
			});
			break;
		case 2157:
			e.getPlayer().sendOptionDialogue("Teleport to the Dark Wizard's Tower?", ops -> {
				ops.add("Yes, teleport me to the Dark Wizard's Tower.", () -> e.getPlayer().setNextTile(Tile.of(2906, 3334, 0)));
				ops.add("Not right now.");
			});
			break;
		}
	});

	public static ObjectClickHandler handleMagicGuildEnter = new ObjectClickHandler(new Object[] { "Magic guild door" }, e -> {
		if (e.getPlayer().getSkills().getLevel(Skills.MAGIC) < 66) {
			e.getPlayer().sendMessage("You require 66 magic to enter the Magic Guild.");
			return;
		}
		Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleGrottoBridge = new ObjectClickHandler(new Object[] { 2830, 2831 }, e -> {
		Tile endLoc;
		if (e.getObjectId() == 2830)
			endLoc = Tile.of(2530, 3029, 0);
		else
			endLoc = Tile.of(2531, 3026, 0);

		e.getPlayer().lock();
		e.getPlayer().setNextFaceTile(endLoc);
		e.getPlayer().setNextAnimation(new Animation(769));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				e.getPlayer().unlockNextTick();
				e.getPlayer().setNextTile(endLoc);
				e.getPlayer().setNextAnimation(new Animation(-1));
			}
		}, 0);
	});

	public static ObjectClickHandler handleTrellis = new ObjectClickHandler(new Object[] { 20056 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 18)) {
			e.getPlayer().sendMessage("You need 18 Agility to climb that.");
			return;
		}

		Player p = e.getPlayer();
		WorldObject obj = e.getObject();

		if(obj.getId() == 20056) {
			p.useLadder(Tile.of(2548, 3118, 1));
			p.getSkills().addXp(Skills.AGILITY, 31);
		}
	});
}
