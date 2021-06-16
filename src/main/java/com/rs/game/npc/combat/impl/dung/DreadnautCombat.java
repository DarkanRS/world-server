package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.Dreadnaut;
import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class DreadnautCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Dreadnaut" };// GFX 2859 Poop bubbles that drain prayer
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Dreadnaut boss = (Dreadnaut) npc;

		if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
			return 0;

		if (Utils.random(5) == 0) {
			npc.setNextAnimation(new Animation(14982));
			npc.setNextSpotAnim(new SpotAnim(2865));
			int damage = getMaxHit(boss, boss.getMaxHit(), AttackStyle.MELEE, target);
			if (damage > 0) {
				target.setNextSpotAnim(new SpotAnim(2866, 75, 0));
				sendReductionEffect(boss, target, damage);
			}
			if (target instanceof Player) {
				Player player = (Player) target;
				player.sendMessage("You have been injured and are unable to use protection prayers.");
				player.setProtectionPrayBlock(12);
			}
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		} else {
			npc.setNextAnimation(new Animation(14973));
			npc.setNextSpotAnim(new SpotAnim(2856));

			for (Entity t : boss.getPossibleTargets()) {
				if (!t.withinDistance(target, 2))
					continue;
				int damage = getMaxHit(boss, boss.getMaxHit(), AttackStyle.MELEE, t);
				World.sendProjectile(boss, t, 2857, 30, 30, 25, 35, 15, 1);
				if (damage > 0) {
					sendReductionEffect(boss, t, damage);
					boss.addSpot(new WorldTile(t));
				} else
					t.setNextSpotAnim(new SpotAnim(2858, 75, 0));
				delayHit(npc, 1, t, getMeleeHit(npc, damage));
			}
		}
		return 5;
	}

	private void sendReductionEffect(Dreadnaut boss, Entity target, int damage) {
		if (!boss.canReduceMagicLevel() || !(target instanceof Player))
			return;
		Player player = (Player) target;
		player.getSkills().set(Constants.MAGIC, (int) (player.getSkills().getLevel(Constants.MAGIC) - (damage * .10)));
	}
}
