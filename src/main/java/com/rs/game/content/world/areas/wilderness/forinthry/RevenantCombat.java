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
package com.rs.game.content.world.areas.wilderness.forinthry;

import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class RevenantCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13465, 13466, 13467, 13468, 13469, 13470, 13471, 13472, 13473, 13474, 13475, 13476, 13477, 13478, 13479, 13480, 13481 };
	}

	public int getMagicAnimation(NPC npc) {
        return switch (npc.getId()) {
            case 13465 -> 7500;
            case 13466, 13467, 13468, 13469 -> 7499;
            case 13470, 13471 -> 7506;
            case 13472 -> 7503;
            case 13473 -> 7507;
            case 13474 -> 7496;
            case 13475 -> 7497;
            case 13476 -> 7515;
            case 13477 -> 7498;
            case 13478 -> 7505;
            case 13479 -> 7515;
            case 13480 -> 7508;
            default ->
                // melee emote, better than 0
                    npc.getCombatDefinitions().getAttackEmote();
        };
	}

	public int getRangeAnimation(NPC npc) {
        return switch (npc.getId()) {
            case 13465 -> 7501;
            case 13466, 13467, 13468, 13469 -> 7513;
            case 13470, 13471 -> 7519;
            case 13472 -> 7516;
            case 13473 -> 7520;
            case 13474 -> 7521;
            case 13475 -> 7510;
            case 13476 -> 7501;
            case 13477 -> 7512;
            case 13478 -> 7518;
            case 13479 -> 7514;
            case 13480 -> 7522;
            default ->
                // melee emote, better than 0
                    npc.getCombatDefinitions().getAttackEmote();
        };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (npc.getHitpoints() < npc.getMaxHitpoints() / 2 && Utils.random(5) == 0)
			npc.heal(100);

		int attackStyle = Utils.random(3);
		if (attackStyle == 2) { // checks if can melee
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			if ((distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1))
				attackStyle = Utils.random(2);
		}

		if (attackStyle != 2)
			npc.soundEffect(target, 202, true);

		switch (attackStyle) {
		case 0: // magic
			int damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target);
			if (target instanceof Player player && player.hasEffect(Effect.REV_IMMUNE))
				damage = 0;
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			World.sendProjectile(npc, target, 1276, 34, 16, 30, 35, 16);
			if (damage > 0)
				WorldTasks.schedule(new Task() {

					@Override
					public void run() {
						target.setNextSpotAnim(new SpotAnim(1277, 0, 100));
						if (Utils.random(5) == 0) {
							target.setNextSpotAnim(new SpotAnim(363));
							target.freeze(Ticks.fromSeconds(5));
						}
					}

				}, 2);
			npc.setNextAnimation(new Animation(getMagicAnimation(npc)));
			break;
		case 1: // range
			int damage2 = getMaxHit(npc, defs.getMaxHit(), AttackStyle.RANGE, target);
			if (target instanceof Player player && player.hasEffect(Effect.REV_IMMUNE))
				damage = 0;
			delayHit(npc, 2, target, getRangeHit(npc, damage2));
			World.sendProjectile(npc, target, 1278, 34, 16, 30, 35, 16);
			npc.setNextAnimation(new Animation(getRangeAnimation(npc)));
			break;
		case 2: // melee
			int damage3 = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target);
			if (target instanceof Player player && player.hasEffect(Effect.REV_IMMUNE))
				damage = 0;
			delayHit(npc, 0, target, getMeleeHit(npc, damage3));
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			break;
		}
		return npc.getAttackSpeed();
	}
}
