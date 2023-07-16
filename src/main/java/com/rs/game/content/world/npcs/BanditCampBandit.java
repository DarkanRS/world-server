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
package com.rs.game.content.world.npcs;

import com.rs.game.content.bosses.godwars.saradomin.SaradominFactionNPC;
import com.rs.game.content.bosses.godwars.zamorak.ZamorakFactionNPC;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class BanditCampBandit extends NPC {

	public BanditCampBandit(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setForceAgressive(true); // to ignore combat lvl
		setIgnoreDocile(true);
	}

	@Override
	public List<Entity> getPossibleTargets() {
		List<Entity> targets = super.getPossibleTargets();
		ArrayList<Entity> targetsCleaned = new ArrayList<>();
		for (Entity t : targets) {
			if (!(t instanceof Player) || (!ZamorakFactionNPC.hasGodItem((Player) t) && !SaradominFactionNPC.hasGodItem((Player) t)))
				continue;
			targetsCleaned.add(t);
		}
		return targetsCleaned;
	}

	@Override
	public void setTarget(Entity entity) {
		if (entity instanceof Player && (ZamorakFactionNPC.hasGodItem((Player) entity) || SaradominFactionNPC.hasGodItem((Player) entity)))
			setNextForceTalk(new ForceTalk(ZamorakFactionNPC.hasGodItem((Player) entity) ? "Prepare to suffer, Zamorakian scum!" : "Time to die, Saradominist filth!"));
		super.setTarget(entity);
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 1926, 1931 }, (npcId, tile) -> new BanditCampBandit(npcId, tile, false));

}
