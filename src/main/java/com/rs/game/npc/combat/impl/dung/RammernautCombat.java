package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.Rammernaut;
import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class RammernautCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Rammernaut" };
	}

	public static int getChargeCount(NPC npc) {

		Integer charge = (Integer) npc.getTemporaryAttributes().get("RAMMERNAUT_CHARGE");

		return charge == null ? 0 : charge;

	}

	public static void setChargeCount(NPC npc, int count) {
		npc.getTemporaryAttributes().put("RAMMERNAUT_CHARGE", count);

	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int chargeCount = getChargeCount(npc);
		
		if (!(npc instanceof Rammernaut))
			return 0;

		if (chargeCount > 1 && target instanceof Player player) {
			((Rammernaut) npc).setChargeTarget(player);
			setChargeCount(npc, 0);
			return 0;
		}

		if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
			setChargeCount(npc, chargeCount + 1);
			return 3;
		}
		setChargeCount(npc, Utils.random(10) == 0 ? 2 : 0); // 1 in 10 change charging next att

		if (Utils.random(5) == 0) {
			npc.setNextAnimation(new Animation(13705));
			for (Entity entity : npc.getPossibleTargets()) {
				if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), entity.getX(), entity.getY(), entity.getSize(), 0))
					continue;
				((Rammernaut) npc).applyStunHit(entity, npc.getMaxHit(AttackStyle.MELEE));
			}
			return npc.getAttackSpeed();
		}

		if (((Rammernaut) npc).isRequestSpecNormalAttack() && target instanceof Player player) {
			((Rammernaut) npc).setRequestSpecNormalAttack(false);
			player.sendMessage("Your prayers have been disabled.");
			player.setProtectionPrayBlock(12);
			player.sendMessage("Your defence been reduced.");
			player.getSkills().drainLevel(Constants.DEFENSE, Utils.random(3) + 1);

		}

		// default melee attack can be protected with prayer
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
		return npc.getAttackSpeed();
	}
}
