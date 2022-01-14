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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.KalGerWarmonger;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class KalGerWarmongerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Kal'Ger the Warmonger" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final KalGerWarmonger boss = (KalGerWarmonger) npc;
		final DungeonManager manager = boss.getManager();
		if (boss.getType() == 0 || boss.isMaximumPullTicks())
			return 0;
		if (boss.isUsingMelee()) {
			boolean smash = false;

			for (Player player : manager.getParty().getTeam())
				if (WorldUtil.collides(player.getX(), player.getY(), player.getSize(), boss.getX(), boss.getY(), 5)) {
					smash = true;
					break;
				}
			if (smash) {
				boss.setNextAnimation(new Animation(14968));
				boss.setNextSpotAnim(new SpotAnim(2867));
				for (Player player : manager.getParty().getTeam()) {
					if (!manager.isAtBossRoom(player))
						continue;
					player.getPackets().sendCameraShake(3, 25, 50, 25, 50);// everyone's camera shakes
					if (Utils.inCircle(player, boss, 5))// 5 square radius (imperfect circle)
						player.applyHit(new Hit(boss, Utils.random(300, boss.getMaxHit()), HitLook.TRUE_DAMAGE));
				}
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						boss.setPullTicks(0);
						for (Player player : manager.getParty().getTeam())// we obv need to reset the camera ^.^
							player.getPackets().sendStopCameraShake();
					}
				});
			} else if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
				return 0;
		}
		boss.setPullTicks(0);
		if (boss.getAnnoyanceMeter() == 8)
			// boss.playSoundEffect(2986);
			boss.setNextForceTalk(new ForceTalk("GRRRR!"));
		else if (boss.getAnnoyanceMeter() == 10)
			// boss.playSoundEffect(3012);
			boss.setNextForceTalk(new ForceTalk("ENOUGH!"));
		if (boss.getType() == 1) {// NO WEAPONS HUR
			npc.setNextAnimation(new Animation(14392));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, boss.getMaxHit(), AttackStyle.MELEE, target)));
		} else if (boss.getType() == 2) {// LONG
			npc.setNextAnimation(new Animation(14416));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, boss.getMaxHit(), AttackStyle.MELEE, target)));
		} else if (boss.getType() == 3) {// STAFF
			npc.setNextAnimation(new Animation(14996));
			npc.setNextSpotAnim(new SpotAnim(2874));
			for (Entity t : boss.getPossibleTargets()) {
				World.sendProjectile(boss, t, 2875, 65, 10, 50, 0, 5, 1);
				t.setNextSpotAnim(new SpotAnim(2873));
				delayHit(npc, 0, t, getMagicHit(npc, getMaxHit(npc, boss.getMaxHit(), AttackStyle.MAGE, t)));
			}
		} else if (boss.getType() == 4) {// 2H
			npc.setNextAnimation(new Animation(14450));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, boss.getMaxHit(), AttackStyle.MELEE, target)));
		} else if (boss.getType() == 5) {// BOW
			npc.setNextAnimation(new Animation(14537));
			npc.setNextSpotAnim(new SpotAnim(2885));
			for (Entity t : boss.getPossibleTargets()) {
				World.sendProjectile(boss, t, 2886, 75, 30, 50, 55, 2, 0);
				delayHit(npc, 2, t, getRangeHit(npc, getMaxHit(npc, boss.getMaxHit(), AttackStyle.RANGE, t)));
			}
		} else if (boss.getType() == 6) {// MAUL
			npc.setNextAnimation(new Animation(14963));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, boss.getMaxHit(), AttackStyle.MELEE, target)));
			return 3;// SUPER OP MODE!
		}
		return 4;
	}
}
