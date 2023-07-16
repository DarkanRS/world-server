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
import com.rs.game.content.skills.dungeoneering.npcs.YkLagorMage;
import com.rs.game.content.skills.dungeoneering.npcs.YkLagorThunderous;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

import java.util.List;

public class YkLagorThunderousCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Yk'Lagor the Thunderous" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final YkLagorThunderous boss = (YkLagorThunderous) npc;
		boss.playMusic();
		if (boss.getNextAttack() < 2) {
			if (boss.getNextAttack() == 0) {
				boss.setNextForceTalk(new ForceTalk("Come closer!"));
				// boss.playSoundEffect(1930);
				WorldTasks.schedule(new WorldTask() {

					int cycles;

					@Override
					public void run() {
						cycles++;
						if (cycles == 3) {
							boss.setNextAnimation(new Animation(14390));
							boss.setNextSpotAnim(new SpotAnim(2768));
						} else if (cycles == 7) {
							List<Entity> targets = boss.getPossibleTargets();
							boolean recovered = false;
							for (Player player : boss.getManager().getParty().getTeam()) {
								if (player.isDead() || !boss.getManager().isAtBossRoom(player.getTile()))
									continue;
								if (targets.contains(player)) {
									sendPullAttack(boss.transform(2, 2, 0), player, true);
									player.sendMessage("Yk'Lagor sees you and pulls you closer, energising him.");
									boss.heal((int) (boss.getMaxHitpoints() * 0.15));
									if (!recovered) {
										boss.setNextForceTalk(new ForceTalk("There is no escape!"));
										// boss.playSoundEffect(1934);
										recovered = true;
									}
								} else
									player.sendMessage("Hiding behind the pillar manages to stop Yk'Lagor from pulling you in.");
							}
							stop();
							return;
						}
					}
				}, 0, 0);
			} else if (boss.getNextAttack() == 1) {// earthquake shit
				boss.setNextForceTalk(new ForceTalk("This is..."));
				// boss.playSoundEffect(1929);
				WorldTasks.schedule(new WorldTask() {

					int cycles;

					@Override
					public void run() {
						cycles++;

						if (cycles == 2) {
							boss.setNextAnimation(new Animation(14384));
							boss.setNextSpotAnim(new SpotAnim(2776));
							for (Player player : boss.getManager().getParty().getTeam()) {
								if (player.isDead() || !boss.getManager().isAtBossRoom(player.getTile()))
									continue;
								player.getPackets().sendCameraShake(3, 25, 50, 25, 50);
							}
						} else if (cycles == 5) {
							boss.setNextForceTalk(new ForceTalk("TRUE POWER!"));
							// boss.playSoundEffect(1936);
							boss.sendBrokenFloor();
						} else if (cycles == 7) {
							for (Player player : boss.getManager().getParty().getTeam()) {
								if (player.isDead() || !boss.getManager().isAtBossRoom(player.getTile()))
									continue;
								player.getPackets().sendStopCameraShake();
							}
							for (Entity t : boss.getPossibleTargets())
								t.applyHit(new Hit(boss, Utils.random(t.getMaxHitpoints()) + 1, HitLook.TRUE_DAMAGE));

							stop();
							return;
						}
					}
				}, 0, 0);
			}
			boss.increaseNextAttack((boss.getNextAttack() == 0 ? 3 : 1) + Utils.random(4, 10) * 2);
			return 10;
		}
		boss.increaseNextAttack(-2);
		boolean useMelee = false;
		boolean useMagic = false;
		if (WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
			useMelee = true;
		for (Entity t : npc.getPossibleTargets())
			if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 0))
				useMagic = true;
		int style = !useMelee ? 1 : !useMagic ? 0 : Utils.random(2);
		switch (style) {
		case 0:
			npc.setNextAnimation(new Animation(14392));
			int damage = 0;
			if (target instanceof Player player)
				if (player.getPrayer().getPoints() > 0 && damage > 0)
					player.getPrayer().drainPrayer((int) (damage * .5));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, target)));
			break;
		case 1:
			sendMagicalAttack(boss, false);
			break;
		}
		return 4;
	}

	public static void sendPullAttack(final Tile tile, final Player player, final boolean disablePrayers) {
		player.lock();
		player.resetWalkSteps();
		player.setNextSpotAnim(new SpotAnim(2767));
		player.forceMove(tile, 14388, 5, 60, () -> {
			player.getActionManager().addActionDelay(10);
			player.freeze(8);
			if (disablePrayers) {
				player.sendMessage("You've been injured and you cannot use protective " + (player.getPrayer().isCurses() ? "curses" : "protective prayers") + "!");
				player.setProtectionPrayBlock(2);
			}
		});
	}

	public static void sendMagicalAttack(YkLagorThunderous npc, boolean specialAttack) {
		npc.setNextAnimation(new Animation(14525));
		npc.setNextSpotAnim(new SpotAnim(2754));
		if (specialAttack)
			npc.setNextForceTalk(new ForceTalk("You dare steal my power?!"));
		// npc.playSoundEffect(1926);
		else if (Utils.random(5) == 0)
			npc.setNextForceTalk(new ForceTalk("Fear my wrath!"));
		///////// npc.playSoundEffect(1927);
		if (npc.getPossibleTargets().size() > 0)
			for (Player player : npc.getManager().getParty().getTeam()) {
				if (player.isDead() || !npc.getManager().isAtBossRoom(player.getTile()))
					continue;
				World.sendProjectile(npc, player, 2733, 75, 50, 20, 0, 20, 0);
				delayHit(npc, 1, player, getMagicHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, player)));
				player.setNextSpotAnim(new SpotAnim(2755, 85, 0));
			}
		if (specialAttack)
			for (YkLagorMage mage : npc.getMages()) {
				if (mage.isDead() || mage.hasFinished())
					continue;
				mage.applyHit(new Hit(npc, mage.getMaxHitpoints(), HitLook.MAGIC_DAMAGE, 60));
				// delayHit(mage, 1, mage, getMagicHit(npc, mage.getMaxHitpoints()));
				mage.setNextSpotAnim(new SpotAnim(2755, 85, 0));
			}
		// for mages kill blablalb,we dont want to kill familiars lol

	}
}
