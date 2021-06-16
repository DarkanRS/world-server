package com.rs.game.player.content.combat;

import com.rs.cache.loaders.Bonus;
import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

public enum AttackType {
	STAB(Bonus.STAB_ATT, Bonus.STAB_DEF),
	SLASH(Bonus.SLASH_ATT, Bonus.SLASH_DEF),
	CRUSH(Bonus.CRUSH_ATT, Bonus.CRUSH_DEF),
	
	ACCURATE(Bonus.RANGE_ATT, Bonus.RANGE_DEF),
	RAPID(Bonus.RANGE_ATT, Bonus.RANGE_DEF),
	LONG_RANGE(Bonus.RANGE_ATT, Bonus.RANGE_DEF),
	
	POLYPORE_ACCURATE(Bonus.MAGIC_ATT, Bonus.MAGIC_DEF),
	POLYPORE_LONGRANGE(Bonus.MAGIC_ATT, Bonus.MAGIC_DEF),
	
	MAGIC(Bonus.MAGIC_ATT, Bonus.MAGIC_DEF);
	
	private Bonus attBonus;
	private Bonus defBonus;
	
	private AttackType(Bonus attBonus, Bonus defBonus) {
		this.attBonus = attBonus;
		this.defBonus = defBonus;
	}

	public int getAttackBonus(Player player) {
		return player.getCombatDefinitions().getBonus(attBonus);
	}
	
	public int getDefenseBonus(Entity entity) {
		if (entity instanceof Player)
			return ((Player) entity).getCombatDefinitions().getBonus(defBonus);
		else
			return ((NPC) entity).getBonus(defBonus);
	}
}
