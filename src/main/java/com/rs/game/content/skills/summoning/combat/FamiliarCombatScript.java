package com.rs.game.content.skills.summoning.combat;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import kotlin.Pair;

import java.util.Arrays;

public class FamiliarCombatScript extends CombatScript {
	
	protected static final int CANCEL = -50239;

	@Override
	public Object[] getKeys() {
		return Arrays.stream(Pouch.values()).map(p -> NPCDefinitions.getDefs(p.getBaseNpc()).getName()).toArray();
	}
	
	@Override
	public final int attack(NPC npc, Entity target) {
		if (npc instanceof Familiar familiar) {
			if (familiar.autoScrollMod > 0 && (familiar.attackIndex++ % familiar.autoScrollMod) == 0 && familiar.getSpecialEnergy() >= familiar.getSpecCost())
				familiar.setSpecActive(true);
			int spec = familiar.castCombatSpecial(target);
			if (spec != Familiar.CANCEL_SPECIAL)
				return spec;
		}
		int alt = alternateAttack(npc, target);
		if (alt != CANCEL)
			return alt;
		return autoAttack(npc, target);
	}
	
	public int alternateAttack(NPC npc, Entity target) {
		return CANCEL;
	}
	
	public final int autoAttack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		CombatStyle attackStyle = defs.getAttackStyle();
		if (attackStyle == CombatStyle.MELEE)
			delayHit(npc, 0, target, Hit.melee(npc, getMaxHit(npc, npc.getMaxHit(), attackStyle, target)));
		else {
			int damage = getMaxHit(npc, npc.getMaxHit(), attackStyle, target);
			WorldProjectile p = World.sendProjectile(npc, target, defs.getAttackProjectile(), new Pair<>(32, 32), 50, 5, 2);
			delayHit(npc, p.getTaskDelay(), target, attackStyle == CombatStyle.RANGE ? Hit.range(npc, damage) : Hit.magic(npc, damage));
		}
		if (defs.getAttackGfx() != -1)
			npc.setNextSpotAnim(new SpotAnim(defs.getAttackGfx()));
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		return npc.getAttackSpeed();
	}
}
