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

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.npcs.SkeletalAdventurer;
import com.rs.game.content.skills.dungeoneering.npcs.YkLagorMage;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class ForgottenMage extends CombatScript {

	private static final int[][] ATTACK_TYPES = {
		{ 0, 9, 10 },
		{ 10, 11, 0, 12, 1 },
		{ 12, 13, 0, 1, 3, 2, 14 },
		{ 14, 15, 0, 3, 16 },
		{ 17, 18, 0, 1, 2, 3, 19 },
		{ 20, 21, 0, 1, 2, 22, 4 },
		{ 22, 23, 0, 1, 4, 6, 24 },
		{ 24, 25, 0, 6, 7, 5, 26 },
		{ 26, 27, 6, 7, 8, 5, 28 },
		{ 28, 29, 6, 7, 8, 5, 30 },
		{ 30, 6, 7, 8, 5 },
	};

	private static final int[] ATTACK_ANIMATIONS = { 711, 716, 724, 710, 710, 710, 729, 729, 729, 14221, 14222, 14221, 14221, 14220, 14220, 14222, 14223, 14221, 14220, 14222, 14223, 14221, 14220, 14222, 14223, 10546, 10542, 14209, 2791 };

	private static final int[] START_GRAPHICS = { 102, 105, 108, 177, 177, 177, 167, 170, 173, -1, 2701, 2713, 2728, -1, 2707, 2709, 2714, 2728, -1, 2701, 2715, 2728, -1, 2702, 2716, 2728, 457, 2701, 2717, 2728 };

	private static final int[] HIT_GRAPHICS = { 104, 107, 110, 181, 180, 179, 169, 172, 107, 2700, 2708, 2723, 2737, 2700, 2704, 2724, 2738, 2700, 2710, 2725, 2739, 2700, 2710, 2726, 2740, 2700, 2712, 2727, 2741 };

	private static final int[] PROJECTILES = { 103, 106, 109, 178, 178, 178, 168, 171, 174, 2699, 2703, 2718, 2729, 2699, 2704, 2719, 2731, 2699, 2705, 2720, 2733, 2699, 2706, 2721, 2735, 462, 2707, 2722, -1 };

	private static final int[] SKILLS = { Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE, 0, 1, 2, Constants.DEFENSE, Constants.STRENGTH, Constants.ATTACK };

	@Override
	public Object[] getKeys() {
		return new Object[] { "Forgotten mage" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		int TIER = npc instanceof YkLagorMage ? 3 : npc instanceof SkeletalAdventurer ? 8 : npc.getId() == 10560 ? 0 : ((npc.getId() - 10560) / 5) - 1;
		if (TIER < 0)
			TIER = 0;
		final int[] POSSIBLE_ATTACKS = ATTACK_TYPES[TIER];
		int attack = POSSIBLE_ATTACKS[Utils.random(POSSIBLE_ATTACKS.length)];
		if ((npc instanceof YkLagorMage || npc instanceof SkeletalAdventurer) && attack <= 8)
			return 0;
		if(!(npc instanceof YkLagorMage || npc instanceof SkeletalAdventurer) && target instanceof Player player
				&& player.getEquipment().containsOneItem(17279, 15828)
				&& !player.getTempAttribs().getB("ShadowSilkSpellDisable")) {
			sendAntiSilkHoodSpell(npc, player);
			return 5;
		}
		if (attack >= 0 && attack <= 8) {
			double drainRate = attack < 2 ? .95 : attack > 6 ? .90 : 0.0;
			sendWeaken(npc, target, ATTACK_ANIMATIONS[attack], START_GRAPHICS[attack], HIT_GRAPHICS[attack], PROJECTILES[attack], SKILLS[attack], drainRate);
		} else
			sendCombatSpell(npc, target, ATTACK_ANIMATIONS[attack], START_GRAPHICS[attack], HIT_GRAPHICS[attack], PROJECTILES[attack]);
		return 5;
	}

	private void sendCombatSpell(NPC npc, final Entity target, int attack, int start, final int hit, int projectileId) {
		npc.setNextAnimation(new Animation(attack));
		if (start != -1)
			npc.setNextSpotAnim(new SpotAnim(start, 0, 0));
		if (attack == 2722) {
			World.sendProjectile(npc, target, 2735, 18, 18, 50, 1, 3, 0);
			World.sendProjectile(npc, target, 2736, 18, 18, 50, 1, 20, 0);
			World.sendProjectile(npc, target, 2736, 18, 18, 50, 1, 110, 0);
		} else
			World.sendProjectile(npc, target, projectileId, 18, 18, 50, 1, 3, 0);
		delayHit(npc, 2, target, getMagicHit(npc, getMaxHit(npc, npc.getMaxHit(), AttackStyle.MAGE, target)));
		if (hit == -1)
			return;
		WorldTasks.schedule(2, () -> target.setNextSpotAnim(new SpotAnim(hit, 0, 85)));
	}

	private void sendWeaken(NPC npc, final Entity target, int attack, int start, final int hit, int projectileId, final int skill, final double percentDrain) {
		npc.setNextAnimation(new Animation(attack));
		npc.setNextSpotAnim(new SpotAnim(start, 0, 50));
		World.sendProjectile(npc, target, projectileId, 39, 18, 55, 1.2, 5, 0);
		if (hit > 0) {
			WorldTasks.scheduleTimer(2, (ticks) -> {
				if (target instanceof Player player)
					if (percentDrain == 0)
						player.freeze(skill == 0 ? 8 : skill == 1 ? 12 : 16, true);
					else
						if(player.getSkills().getLevel(skill) > player.getSkills().getLevelForXp(skill)*0.93)
							player.getSkills().set(skill, (int) (player.getSkills().getLevel(skill) * percentDrain));
				return false;
			});
			target.setNextSpotAnim(new SpotAnim(hit, 140, 85));
		}
	}

	private void sendAntiSilkHoodSpell(NPC npc, final Player player) {
		npc.setNextAnimation(new Animation(6293));
		npc.setNextSpotAnim(new SpotAnim(1059));
		WorldTasks.scheduleTimer(2, (ticks) -> {
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
			return false;
		});
	}
}
