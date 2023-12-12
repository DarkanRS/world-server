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
package com.rs.game.content.quests.priestinperil;

import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class MonkOfZamorak extends NPC {
	public MonkOfZamorak(int id, Tile tile, boolean permaDeath) {
		super(id, tile, permaDeath);

	}

	@Override
	public void sendDeath(final Entity source) {
		if(source instanceof Player player && player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 4) {
			super.sendDeath(source);
			Tile tile = getTile();
			World.addGroundItem(new Item(2944), tile, player);
			return;
		}
		if(source instanceof Player player)
			super.sendDeath(source);
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[]{ 1044, 1045, 1046 }, (npcId, tile) -> new MonkOfZamorak(npcId, tile, false));


}
