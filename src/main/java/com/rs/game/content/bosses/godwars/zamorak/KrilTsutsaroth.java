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
package com.rs.game.content.bosses.godwars.zamorak;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class KrilTsutsaroth extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6203 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (Utils.getRandomInclusive(4) == 0)
			switch (Utils.getRandomInclusive(8)) {
			case 0:
				npc.setNextForceTalk(new ForceTalk("Attack them, you dogs!"));
				break;
			case 1:
				npc.setNextForceTalk(new ForceTalk("Forward!"));
				break;
			case 2:
				npc.setNextForceTalk(new ForceTalk("Death to Saradomin's dogs!"));
				break;
			case 3:
				npc.setNextForceTalk(new ForceTalk("Kill them, you cowards!"));
				break;
			case 4:
				npc.setNextForceTalk(new ForceTalk("The Dark One will have their souls!"));
				npc.voiceEffect(3229);
				break;
			case 5:
				npc.setNextForceTalk(new ForceTalk("Zamorak curse them!"));
				break;
			case 6:
				npc.setNextForceTalk(new ForceTalk("Rend them limb from limb!"));
				break;
			case 7:
				npc.setNextForceTalk(new ForceTalk("No retreat!"));
				break;
			case 8:
				npc.setNextForceTalk(new ForceTalk("Flay them all!"));
				break;
			}
		int attackStyle = Utils.getRandomInclusive(2);
		switch (attackStyle) {
		case 0:// magic flame attack
			npc.setNextAnimation(new Animation(14962));
			npc.setNextSpotAnim(new SpotAnim(1210));
			for (Entity t : npc.getPossibleTargets()) {
				delayHit(npc, 1, t, getMagicHit(npc, getMaxHit(npc, 300, AttackStyle.MAGE, t)));
				World.sendProjectile(npc, t, 1211, 41, 16, 41, 35, 16, 0);
				if (Utils.getRandomInclusive(4) == 0)
					t.getPoison().makePoisoned(168);
			}
			break;
		case 1:// main attack
		case 2:// melee attack
			int damage = 300;// normal
			for (Entity e : npc.getPossibleTargets()) {
				if (e instanceof Player player && ((Player) e).getPrayer().isProtectingMelee() && Utils.random(10) == 0) {
					damage = 497;
					npc.setNextForceTalk(new ForceTalk("YARRRRRRR!"));
					player.getPrayer().drainPrayer(player.getPrayer().getPoints()/2);
					player.sendMessage("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
				}
				npc.setNextAnimation(new Animation(damage <= 463 ? 14963 : 14968));
				delayHit(npc, 0, e, getMeleeHit(npc, getMaxHit(npc, damage, AttackStyle.MELEE, e)));
			}
			break;
		}
		return npc.getAttackSpeed();
	}
}
