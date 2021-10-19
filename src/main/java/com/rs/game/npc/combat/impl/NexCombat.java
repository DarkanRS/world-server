package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.godwars.zaros.Nex.Phase;
import com.rs.game.npc.godwars.zaros.attack.BloodSacrifice;
import com.rs.game.npc.godwars.zaros.attack.ContainThis;
import com.rs.game.npc.godwars.zaros.attack.Drag;
import com.rs.game.npc.godwars.zaros.attack.EmbraceDarkness;
import com.rs.game.npc.godwars.zaros.attack.IcePrison;
import com.rs.game.npc.godwars.zaros.attack.NoEscape;
import com.rs.game.npc.godwars.zaros.attack.ShadowTraps;
import com.rs.game.npc.godwars.zaros.attack.Siphon;
import com.rs.game.npc.godwars.zaros.attack.Virus;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class NexCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Nex" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		if (!(npc instanceof Nex))
			return notNexAttack(npc, target);
		Nex nex = (Nex) npc;
		if (nex.getTempB("siphoning"))
			return 0;
		switch(nex.getPhase()) {
		case SMOKE:
			if (nex.getAttackCount() % 12 == 0)
				return nex.performAttack(target, Utils.random(4) == 0 ? new Drag() : new NoEscape());
			else if (nex.getAttackCount() % 6 == 0)
				return nex.performAttack(target, new Virus());
			break;
		case SHADOW:
			if (nex.getAttackCount() == 0)
				return nex.performAttack(target, new EmbraceDarkness());
			else if (nex.getAttackCount() % 5 == 0)
				return nex.performAttack(target, new ShadowTraps());
			break;
		case BLOOD:
			if (nex.getAttackCount() % 8 == 0)
				return nex.performAttack(target, new Siphon());
			else if (nex.getAttackCount() % 4 == 0)
				return nex.performAttack(target, new BloodSacrifice());
			break;
		case ICE:
			if (nex.getAttackCount() % 8 == 0)
				return nex.performAttack(target, new ContainThis());
			else if (nex.getAttackCount() % 4 == 0)
				return nex.performAttack(target, new IcePrison());
			break;
		default:
			break;
		}
		return autoAttack(nex, target);
	}
	
	public int autoAttack(Nex nex, Entity target) {
		if (nex.isFollowTarget()) {
			if (!nex.inMeleeRange(target)) {
				nex.calcFollow(target, true);
				if (nex.shouldStopMeleeing())
					nex.setFollowTarget(false);
				return 0;
			}
			nex.setFollowTarget(Utils.random(2) == 0);
			int damage = getMaxHit(nex, 360, AttackStyle.MELEE, target);
			delayHit(nex, 0, target, getMeleeHit(nex, damage));
			nex.setNextAnimation(new Animation(6354));
		} else {
			nex.setFollowTarget(Utils.random(2) == 0);
			switch (nex.getPhase()) {
			case SMOKE:
				nex.setNextAnimation(new Animation(6987));
				nex.setNextSpotAnim(new SpotAnim(1214));
				for (Entity t : nex.getPossibleTargets()) {
					int damage = getMaxHit(nex, 250, AttackStyle.MAGE, t);
					delayHit(nex, World.sendProjectile(nex, t, 306, 41, 16, 41, 1.6, 16, 0, () -> t.setNextSpotAnim(new SpotAnim(471))).getTaskDelay(), t, getMagicHit(nex, damage));
					if (damage > 0 && Utils.getRandomInclusive(5) == 0)
						t.getPoison().makePoisoned(88);
				}
				break;
			case SHADOW:
				nex.setNextAnimation(new Animation(6987));
				for (final Entity t : nex.getPossibleTargets()) {
					int distance = (int) Utils.getDistance(t.getX(), t.getY(), nex.getX(), nex.getY());
					if (distance <= 10) {
						int damage = 800 - (distance * 800 / 11);
						delayHit(nex, World.sendProjectile(nex, t, 380, 41, 16, 41, 1.6, 16, 0, () -> t.setNextSpotAnim(new SpotAnim(471))).getTaskDelay(), t, getRangeHit(nex, getMaxHit(nex, damage, AttackStyle.RANGE, t)));
					}
				}
				break;
			case BLOOD:
				nex.setNextAnimation(new Animation(6986));
				delayHit(nex, World.sendProjectile(nex, target, 374, 41, 16, 41, 1.6, 16, 0).getTaskDelay(), target, getMagicHit(nex, getMaxHit(nex, 250, AttackStyle.MAGE, target)));
				break;
			case ICE:
				nex.setNextAnimation(new Animation(6986));
				for (final Entity t : nex.getPossibleTargets()) {
					int damage = getMaxHit(nex, 250, AttackStyle.MAGE, t);
					delayHit(nex, World.sendProjectile(nex, t, 362, 41, 16, 41, 35, 16, 0).getTaskDelay(), t, getMagicHit(nex, damage));
					if (damage > 0 && Utils.getRandomInclusive(5) == 0) {
						if (t instanceof Player player)
							t.freeze(Ticks.fromSeconds(player.getPrayer().isProtectingMage() ? 3 : 18), true);
						t.setNextSpotAnim(new SpotAnim(369));
					}
				}
				break;
			case ZAROS:
				nex.setNextAnimation(new Animation(6987));
				for (Entity t : nex.getPossibleTargets()) {
					int damage = getMaxHit(nex, 350, AttackStyle.MAGE, t);
					delayHit(nex, World.sendProjectile(nex, t, 306, 41, 16, 41, 1.6, 16, 0, () -> t.setNextSpotAnim(new SpotAnim(471))).getTaskDelay(), t, getMagicHit(nex, damage));
				}
				break;
			}
		}
		if (nex.getPhase() == Phase.ZAROS && nex.getAttackCount() % 3 == 0)
			nex.switchPrayers();
		nex.incrementAttack();
		return nex.getAttackSpeed();
	}
	
	public int notNexAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(6987));
		npc.setNextSpotAnim(new SpotAnim(1214));
		for (Entity t : npc.getPossibleTargets()) {
			int damage = getMaxHit(npc, 250, AttackStyle.MAGE, t);
			delayHit(npc, World.sendProjectile(npc, t, 306, 41, 16, 41, 1.6, 16, 0, () -> t.setNextSpotAnim(new SpotAnim(471))).getTaskDelay(), t, getMagicHit(npc, damage));
			if (damage > 0 && Utils.getRandomInclusive(5) == 0)
				t.getPoison().makePoisoned(88);
		}
		return npc.getAttackSpeed();
	}
}
