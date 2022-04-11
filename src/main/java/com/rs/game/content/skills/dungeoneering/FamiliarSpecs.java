package com.rs.game.content.skills.dungeoneering;

import static com.rs.game.model.entity.npc.combat.CombatScript.*;

import com.rs.game.World;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class FamiliarSpecs {
	public static int snaringWave(Player owner, Familiar familiar, Entity target, int tier) {
		target.freeze(8, true);
		familiar.setNextSpotAnim(new SpotAnim(2591));
		familiar.setNextAnimation(new Animation(13620));
		delayHit(familiar, World.sendProjectile(familiar, target, 2592, 41, 16, 41, 2.0, 16, 0).getTaskDelay(), target, getMagicHit(familiar, getMaxHit(familiar, (int) (familiar.getMaxHit() * (1.05 * tier)), AttackStyle.MAGE, target, 1.5)), () -> target.setNextSpotAnim(new SpotAnim(2593)));
		return Familiar.DEFAULT_ATTACK_SPEED;
	}

	public static int poisonousShot(Player owner, Familiar familiar, Entity target, int tier) {
		familiar.setNextAnimation(new Animation(13203));
		familiar.setNextSpotAnim(new SpotAnim(2447));
		delayHit(familiar, World.sendProjectile(familiar, target, 2448, 41, 16, 41, 2.0, 16, 0).getTaskDelay(), target, getRangeHit(familiar, getMaxHit(familiar, (int) (familiar.getMaxHit() * (1.05 * tier)), AttackStyle.RANGE, target, 1.5)), () -> target.getPoison().makePoisoned(18+tier));
		return Familiar.DEFAULT_ATTACK_SPEED;
	}

	public static int sunderingStrike(Player owner, Familiar familiar, Entity target, int tier) {
		familiar.setNextAnimation(new Animation(13198));
		familiar.setNextSpotAnim(new SpotAnim(2444));
		Hit hit = getMeleeHit(familiar, getMaxHit(familiar, (int) (familiar.getMaxHit() * (1.05 * tier)), AttackStyle.MELEE, target, 1.5));
		delayHit(familiar, 1, target, hit);
		if (hit.getDamage() > 0 && target instanceof NPC n)
			n.lowerDefense((hit.getDamage() / 20) * tier);
		return Familiar.DEFAULT_ATTACK_SPEED;
	}

	public static boolean aptitude(Player player, Familiar familiar, int boost) {
		//TODO
		return false;
	}
	
	public static boolean secondWind(Player player, Familiar familiar, int amount) {
		//TODO
		return false;
	}
	
	public static boolean glimmer(Player player, Familiar familiar, int amount) {
		//TODO
		//13620
		return false;
	}
}
