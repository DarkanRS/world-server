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
package com.rs.game.content.bosses.godwars.zaros;

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.Bonus;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class ZarosFactionNPC extends NPC {

	private static final int CAP_BONUS = 200;

	private static final Bonus[][] BONUSES = { { Bonus.STAB_DEF, Bonus.SLASH_DEF, Bonus.CRUSH_DEF }, { Bonus.RANGE_DEF }, {}, { Bonus.MAGIC_DEF } };

	public ZarosFactionNPC(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public List<Entity> getPossibleTargets() {
		List<Entity> targets = getPossibleTargets(true);
		ArrayList<Entity> targetsCleaned = new ArrayList<>();
		for (Entity t : targets) {
			if (t instanceof ZarosFactionNPC || t instanceof Familiar || hasSuperiourBonuses(t))
				continue;
			targetsCleaned.add(t);
		}
		return targetsCleaned;
	}

	private boolean hasSuperiourBonuses(Entity t) {
		if (!(t instanceof Player player))
			return false;
		for (Bonus bonus : BONUSES[getId() - 13456])
			if (player.getCombatDefinitions().getBonus(bonus) >= (bonus == Bonus.RANGE_DEF ? 100 : CAP_BONUS))
				return true;
		return false;
	}

	public static boolean isNexArmour(String name) {
		return name.contains("pernix") || name.contains("torva") || name.contains("virtus") || name.contains("zaryte");
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 13456, 13457, 13458, 13459 }, (npcId, tile) -> new ZarosFactionNPC(npcId, tile));
}