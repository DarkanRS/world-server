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
package com.rs.game.content.skills.dungeoneering.npcs.combat;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.Default;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.rs.game.content.skills.dungeoneering.DungeonConstants.GuardianMonster.NECROMANCER;
import static com.rs.game.content.skills.dungeoneering.DungeonConstants.GuardianMonster.REBORN_MAGE;

public class AntiSilkHoodMages extends Default {//default combat script

	@Override
	public Object[] getKeys() {//Get necromancer/reborn mages as Object array of ints
		List<Integer> antiSilkHoodMages1 = Arrays.stream(NECROMANCER.getNPCIds()).boxed().collect(Collectors.toList());
		List<Integer> antiSilkHoodMages2 = Arrays.stream(REBORN_MAGE.getNPCIds()).boxed().collect(Collectors.toList());
		List<Integer> antiSilkHoodMages = new ArrayList<>(antiSilkHoodMages1);
		antiSilkHoodMages.addAll(antiSilkHoodMages2);

		Object[] mages = new Object[antiSilkHoodMages.size()];
		int i = 0;
		for(Object o : antiSilkHoodMages)
			mages[i++] = o;
		return mages;
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if(target instanceof Player player && player.getEquipment().containsOneItem(17279, 15828)
				&& !player.getTempAttribs().getB("ShadowSilkSpellDisable")){
			sendAntiSilkHoodSpell(npc, player);
			return 5;//delay 5 ticks for spell
		}
		return super.attack(npc, target);
	}
	private void sendAntiSilkHoodSpell(NPC npc, final Player player) {
		int animation = 6293;
		if(Arrays.stream(REBORN_MAGE.getNPCIds()).anyMatch(i -> i == npc.getId()))
			animation = 11130;
		npc.setNextAnimation(new Animation(animation));
		npc.setNextSpotAnim(new SpotAnim(1059));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextSpotAnim(new SpotAnim(736, 0, 50));
				player.getTempAttribs().setB("ShadowSilkSpellDisable", true);
				player.sendMessage("<col=ff6f69>Your shadow silk hood loses its power...");
				WorldTasks.delay(Ticks.fromMinutes(2), () -> {
					if(player.hasStarted()) {
						if(player.getTempAttribs().getB("ShadowSilkSpellDisable"))
							player.sendMessage("<col=96ceb4>Your shadow silk hood returns its power...");
						player.getTempAttribs().setB("ShadowSilkSpellDisable", false);
					}
				});
			}
		}, 2);
	}
}
