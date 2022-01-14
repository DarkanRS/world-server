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

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.combat.impl.KalphiteQueenCombat;
import com.rs.game.npc.dungeoneering.BulwarkBeast;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class BulwarkBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bulwark beast" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		((BulwarkBeast) npc).refreshBar();

		final NPCCombatDefinitions defs = npc.getCombatDefinitions();

		if (Utils.random(15) == 0) {
			List<Entity> targets = npc.getPossibleTargets();
			npc.setNextAnimation(new Animation(13007));
			for (Entity t : targets)
				if (WorldUtil.isInRange(t.getX(), t.getY(), t.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0)) {
					t.setNextSpotAnim(new SpotAnim(2400));
					delayHit(npc, 1, t, getRegularHit(npc, 1 + Utils.random((int) (npc.getMaxHit(AttackStyle.MELEE) * 0.7))));
				}
			return npc.getAttackSpeed();
		}

		// mage, range, melee
		int attackStyle = Utils.random(WorldUtil.isInRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) ? 3 : 2);
		switch (attackStyle) {
		case 0:
			npc.setNextAnimation(new Animation(13004));
			npc.setNextSpotAnim(new SpotAnim(2397));
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					KalphiteQueenCombat.attackMageTarget(new ArrayList<Player>(), npc, npc, target, 2398, 2399);
				}

			});
			break;
		case 1:
			npc.setNextAnimation(new Animation(13006));
			npc.setNextSpotAnim(new SpotAnim(2394));
			List<Entity> targets = npc.getPossibleTargets();
			for (Entity t : targets) {
				World.sendProjectile(npc, t, 2395, 35, 30, 41, 40, 0, 0);
				t.setNextSpotAnim(new SpotAnim(2396, 75, 0));
				delayHit(npc, 1, t, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, t)));
			}
			break;
		case 2:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
			break;
		}
		return npc.getAttackSpeed();
	}
}
