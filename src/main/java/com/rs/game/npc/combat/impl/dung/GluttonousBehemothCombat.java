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
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.DungeonBoss;
import com.rs.game.npc.dungeoneering.GluttonousBehemoth;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class GluttonousBehemothCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Gluttonous behemoth" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		DungeonBoss boss = (DungeonBoss) npc;
		DungeonManager manager = boss.getManager();
		boolean lessThanHalf = npc.getHitpoints() < npc.getMaxHitpoints() * .5;
		if (lessThanHalf && !npc.getTempAttribs().getB("GLUTTONOUS_HEALING")) {
			RoomReference reference = manager.getCurrentRoomReference(npc);
			GameObject food1 = manager.getObject(reference, 49283, 0, 11);
			GameObject food2 = manager.getParty().getTeam().size() <= 1 ? null : manager.getObject(reference, 49283, 11, 11);
			GameObject food = food1;
			if (food1 != null)
				for (Player player : manager.getParty().getTeam())
					if (player.withinDistance(food1, food1.getDefinitions().getSizeX() + 1)) {
						food = null;
						break;
					}
			if (food == null && food2 != null) {
				food = food2;
				for (Player player : manager.getParty().getTeam())
					if (player.withinDistance(food2, food1.getDefinitions().getSizeX() + 1)) {
						food = null;
						break;
					}
			}
			if (food != null) {
				npc.getTempAttribs().setB("GLUTTONOUS_HEALING", true);
				((GluttonousBehemoth) npc).setHeal(food);
				return 0;
			}
		}
		boolean stomp = false;
		for (Player player : manager.getParty().getTeam())
			if (WorldUtil.collides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				stomp = true;
				delayHit(npc, 0, player, getRegularHit(npc, getMaxHit(npc, AttackStyle.MELEE, player)));
			}
		if (stomp) {
			npc.setNextAnimation(new Animation(13718));
			return npc.getAttackSpeed();
		}
		int attackStyle = Utils.getRandomInclusive(2);
		if (attackStyle == 2) {
			if (WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
			attackStyle = Utils.getRandomInclusive(1);
		}
		if (attackStyle == 0) {
			npc.setNextAnimation(new Animation(13719));
			World.sendProjectile(npc, target, 2612, 41, 16, 41, 35, 16, 0);
			int damage = getMaxHit(npc, AttackStyle.MAGE, target);
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			if (damage != 0)
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						target.setNextSpotAnim(new SpotAnim(2613));
					}
				}, 1);
		} else if (attackStyle == 1) {
			npc.setNextAnimation(new Animation(13721));
			World.sendProjectile(npc, target, 2610, 41, 16, 41, 35, 16, 0);
			delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					target.setNextSpotAnim(new SpotAnim(2611));
				}
			}, 1);
		}
		return npc.getAttackSpeed();
	}
}
