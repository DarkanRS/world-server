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
package com.rs.game.content.world.areas.keldagrim;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Keldagrim {

	public static NPCClickHandler handleHirko = new NPCClickHandler(new Object[] { 4558 }, e -> {
		int option = e.getOpNum();
		if (option == 1)
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Can I help you at all?");
					addNext(() -> {
						ShopsHandler.openShop(e.getPlayer(), "keldagrim_crossbow_shop");
					});
					create();
				}
			});
		if (option == 3)
			ShopsHandler.openShop(e.getPlayer(), "keldagrim_crossbow_shop");
	});

	public static NPCClickHandler handleNolar = new NPCClickHandler(new Object[] { 2158 }, e -> {
		int option = e.getOpNum();
		if (option == 1)
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Can I help you at all?");
					addNext(() -> {
						ShopsHandler.openShop(e.getPlayer(), "carefree_crafting_stall");
					});
					create();
				}
			});
		if (option == 3)
			ShopsHandler.openShop(e.getPlayer(), "carefree_crafting_stall");
	});

	public static ObjectClickHandler handleRellekkaEntrance = new ObjectClickHandler(new Object[] { 5973 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2838, 10124, 0));
	});

	public static ObjectClickHandler handleRellekkaExit = new ObjectClickHandler(new Object[] { 5998 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2780, 10161, 0));
	});

	public static ObjectClickHandler handleChaosDwarfBattlefieldEnter = new ObjectClickHandler(new Object[] { 45060 }, e -> {
		e.getPlayer().setNextTile(Tile.of(1520, 4704, 0));
	});

	public static ObjectClickHandler handleChaosDwarfBattlefieldExit = new ObjectClickHandler(new Object[] { 45008 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2817, 10155, 0));
	});

	public static ObjectClickHandler handleBlastFurnaceEntrances = new ObjectClickHandler(new Object[] { 9084, 9138 }, e -> {
		e.getPlayer().useStairs(e.getObjectId() == 9084 ? Tile.of(1939, 4958, 0) : Tile.of(2931, 10196, 0));
	});

}
