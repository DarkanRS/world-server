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
import com.rs.game.npc.dungeoneering.FrozenAdventurer;
import com.rs.game.npc.dungeoneering.ToKashBloodChiller;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class ToKashBloodChillerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "To'Kash the Bloodchiller" };
	}

	@Override
	public int attack(final NPC npc, Entity target) {
		final ToKashBloodChiller boss = (ToKashBloodChiller) npc;
		final DungeonManager manager = boss.getManager();

		boolean perfectDamage = false;

		if (target instanceof Player player)
			if (player.getAppearance().isNPC())
				perfectDamage = true;

		if (perfectDamage) {
			((Player) target).getAppearance().transformIntoNPC(-1);
			target.applyHit(new Hit(npc, (int) Utils.random(boss.getMaxHit() * .90, boss.getMaxHit()), HitLook.MAGIC_DAMAGE));
		}

		boolean special = boss.canSpecialAttack() && Utils.random(10) == 0;

		if (!special) {
			boolean meleeAttack = perfectDamage || Utils.random(3) == 0;

			if (meleeAttack) {
				npc.setNextAnimation(new Animation(14392));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, 200, AttackStyle.MELEE, target)));
			} else {
				npc.setNextAnimation(new Animation(14398));
				World.sendProjectile(npc, target, 2546, 16, 16, 41, 30, 0, 0);
				delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, 200, AttackStyle.MAGE, target)));
			}
			return meleeAttack ? 4 : 5;
		}
		npc.setNextForceTalk(new ForceTalk("Sleep now, in the bitter cold..."));
		// npc.playSoundEffect(2896);
		boss.setSpecialAttack(true);
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				npc.setNextForceTalk(new ForceTalk("DEEP FREEZE!"));
				npc.setNextAnimation(new Animation(14396));
				npc.setNextSpotAnim(new SpotAnim(2544));
				for (Entity t : boss.getPossibleTargets())
					setSpecialFreeze((Player) t, boss, manager);
			}
		}, 3);
		return 8;
	}

	public static void setSpecialFreeze(final Player player, final ToKashBloodChiller boss, DungeonManager dungManager) {
		player.resetWalkSteps();
		player.stopAll();
		player.lock();
		player.setNextSpotAnim(new SpotAnim(2545));
		player.getAppearance().transformIntoNPC(10022);
		FrozenAdventurer npc = new FrozenAdventurer(10023, player, -1, false);
		npc.setPlayer(player);
		player.sendMessage("You have been frozen solid!");
		WorldTasks.schedule(new WorldTask() {

			int counter = 0;

			@Override
			public void run() {
				boss.setSpecialAttack(false);
				for (Entity t : boss.getPossibleTargets()) {
					Player player = (Player) t;
					if (player.isLocked()) {
						counter++;
						player.getAppearance().transformIntoNPC(-1);
					}
				}
				if (counter == 0)
					return;
				boss.setNextForceTalk(new ForceTalk("I will shatter your soul!"));
				boss.setNextSpotAnim(new SpotAnim(2549, 5, 100));
			}
		}, 5 * dungManager.getParty().getTeam().size());
	}

	public static void removeSpecialFreeze(Player player) {
		player.unlock();
		player.getAppearance().transformIntoNPC(-1);
		player.setNextSpotAnim(new SpotAnim(2548));
		player.sendMessage("The ice encasing you shatters violently.");
	}
}
