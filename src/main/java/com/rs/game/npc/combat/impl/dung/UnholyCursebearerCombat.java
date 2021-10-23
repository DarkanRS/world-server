package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.DungeonBoss;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class UnholyCursebearerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Unholy cursebearer" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = WorldUtil.isInRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) ? Utils.random(2) : 0;
		if (target instanceof Player player && target.getTempAttribs().getI("UNHOLY_CURSEBEARER_ROT") == -1) {
			target.getTempAttribs().setI("UNHOLY_CURSEBEARER_ROT", 1);
			player.sendMessage("An undead rot starts to work at your body.");
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					int value = target.getTempAttribs().getI("UNHOLY_CURSEBEARER_ROT");
					if (player.hasFinished() || npc.hasFinished() || !((DungeonBoss) npc).getManager().isAtBossRoom(player) || value == -1) {
						target.getTempAttribs().removeI("UNHOLY_CURSEBEARER_ROT");
						stop();
						return;
					}
					int damage = 20 * value;
					for (int stat = 0; stat < 7; stat++) {
						if (stat == Constants.HITPOINTS)
							continue;
						int drain = Utils.random(5) + 1;
						if (stat == Constants.PRAYER)
							player.getPrayer().drainPrayer(drain * 10);
						player.getSkills().drainLevel(stat, drain);
					}
					int maxDamage = player.getMaxHitpoints() / 10;
					if (damage > maxDamage)
						damage = maxDamage;
					if (value == 6)
						player.sendMessage("The undead rot can now be cleansed by the unholy font.");
					player.applyHit(new Hit(npc, damage, HitLook.TRUE_DAMAGE));
					player.setNextSpotAnim(new SpotAnim(2440));
					target.getTempAttribs().incI("UNHOLY_CURSEBEARER_ROT");
				}

			}, 0, 12);
		}
		switch (attackStyle) {
		case 0:
			boolean multiTarget = Utils.random(2) == 0;
			npc.setNextAnimation(new Animation(multiTarget ? 13176 : 13175));
			if (multiTarget) {
				npc.setNextSpotAnim(new SpotAnim(2441));
				for (Entity t : npc.getPossibleTargets()) {
					World.sendProjectile(npc, t, 88, 50, 30, 41, 40, 0, 0);
					delayHit(npc, 1, t, getMagicHit(npc, getMaxHit(npc, (int) (npc.getMaxHit(AttackStyle.MAGE) * 0.6), AttackStyle.MAGE, t)));
				}
			} else {
				World.sendProjectile(npc, target, 88, 50, 30, 41, 30, 0, 0);
				delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, AttackStyle.MAGE, target)));
			}
			break;
		case 1:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
			break;
		}
		return npc.getAttackSpeed();
	}
}
