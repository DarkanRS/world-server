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
package com.rs.game.model.entity.npc.combat.impl.dung;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.npc.dungeoneering.DungeonBoss;
import com.rs.game.model.entity.npc.dungeoneering.IcyBones;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class IcyBonesCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Icy Bones" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		DungeonBoss boss = (DungeonBoss) npc;
		DungeonManager manager = boss.getManager();

		if (Utils.random(10) == 0) {
			npc.setNextAnimation(new Animation(13791, 20));
			npc.setNextSpotAnim(new SpotAnim(2594));
			boolean mage = Utils.random(2) == 0;
			if (mage && Utils.random(3) == 0) {
				target.setNextSpotAnim(new SpotAnim(2597));
				target.freeze(8);
			}
			if (mage)
				delayHit(npc, 2, target, getMagicHit(npc, getMaxHit(npc, AttackStyle.MAGE, target)));
			else
				delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			World.sendProjectile(npc, target, 2595, 41, 16, 41, 40, 16, 0);
			return npc.getAttackSpeed();
		}
		if (Utils.random(3) == 0 && WorldUtil.isInRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) && ((IcyBones) npc).sendSpikes()) {
			npc.setNextSpotAnim(new SpotAnim(2596));
			npc.setNextAnimation(new Animation(13790));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
			return npc.getAttackSpeed();
		}
		boolean onRange = false;
		for (Player player : manager.getParty().getTeam())
			if (WorldUtil.isInRange(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0)) {
				int damage = getMaxHit(npc, AttackStyle.MELEE, player);
				if (damage != 0 && player.getPrayer().isProtectingMelee())
					player.sendMessage("Your prayer offers only partial protection against the attack.");
				delayHit(npc, 0, player, getMeleeHit(npc, damage));
				onRange = true;
			}
		if (onRange) {
			npc.setNextAnimation(new Animation(defs.getAttackEmote(), 20));
			npc.setNextSpotAnim(new SpotAnim(defs.getAttackGfx()));
			return npc.getAttackSpeed();
		}
		return 0;
	}
}
