package com.rs.game.content.quests.templeofikov;

import com.rs.game.content.combat.AmmoType;
import com.rs.game.content.combat.RangedWeapon;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class FireWarrior extends OwnedNPC {
	public FireWarrior(Player p, int id, Tile tile) {
		super(p, id, tile, true);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if(source instanceof Player p)
			p.getQuestManager().getAttribs(Quest.TEMPLE_OF_IKOV).setB("FireWarriorKilled", true);
	}

	@Override
	public void handlePreHit(final Hit hit) {
		super.handlePreHit(hit);
		if(hit.getSource() instanceof Player p && p.getEquipment().getAmmoId() == 78
				&& RangedWeapon.forId(p.getEquipment().getWeaponId()).getAmmos().contains(AmmoType.ICE_ARROWS))
			return;
		hit.setDamage(0);
	}

}
