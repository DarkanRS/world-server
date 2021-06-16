package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.DungeonBoss;
import com.rs.game.npc.dungeoneering.IcyBones;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class IcyBonesCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Icy Bones" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		DungeonBoss boss = (DungeonBoss) npc;
		DungeonManager manager = boss.getManager();

		if (Utils.random(10) == 0) {
			npc.setNextAnimation(new Animation(13791, 20));
			npc.setNextSpotAnim(new SpotAnim(2594));
			boolean mage = Utils.random(2) == 0;
			if (mage && Utils.random(3) == 0) {
				target.setNextSpotAnim(new SpotAnim(2597));
				target.freeze(8);
			}
			if (mage)
				delayHit(npc, 2, target, getMagicHit(npc, getMaxHit(npc, AttackStyle.MAGE, target)));
			else
				delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			World.sendProjectile(npc, target, 2595, 41, 16, 41, 40, 16, 0);
			return npc.getAttackSpeed();
		}
		if (Utils.random(3) == 0 && WorldUtil.isInRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) && ((IcyBones) npc).sendSpikes()) {
			npc.setNextSpotAnim(new SpotAnim(2596));
			npc.setNextAnimation(new Animation(13790));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
			return npc.getAttackSpeed();
		}
		boolean onRange = false;
		for (Player player : manager.getParty().getTeam()) {
			if (WorldUtil.isInRange(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0)) {
				int damage = getMaxHit(npc, AttackStyle.MELEE, player);
				if (damage != 0 && player.getPrayer().isProtectingMelee())
					player.sendMessage("Your prayer offers only partial protection against the attack.");
				delayHit(npc, 0, player, getMeleeHit(npc, damage));
				onRange = true;
			}
		}
		if (onRange) {
			npc.setNextAnimation(new Animation(defs.getAttackEmote(), 20));
			npc.setNextSpotAnim(new SpotAnim(defs.getAttackGfx()));
			return npc.getAttackSpeed();
		}
		return 0;
	}
}
