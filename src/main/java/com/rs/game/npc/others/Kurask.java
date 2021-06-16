package com.rs.game.npc.others;

import com.rs.game.Hit;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.combat.AmmoType;
import com.rs.game.player.content.combat.RangedWeapon;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Kurask extends NPC {

	public Kurask(int id, WorldTile tile) {
		super(id, tile);
	}

	@Override
	public void handlePreHit(Hit hit) {
		if (hit.getSource() instanceof Player) {
			Player player = (Player) hit.getSource();
			RangedWeapon weapon = RangedWeapon.forId(player.getEquipment().getWeaponId());
			AmmoType ammo = AmmoType.forId(player.getEquipment().getAmmoId());
			if (!(player.getEquipment().getWeaponId() == 13290 || player.getEquipment().getWeaponId() == 4158) && !(weapon != null && weapon.getAmmos() != null && ammo != null && (ammo == AmmoType.BROAD_ARROW || ammo == AmmoType.BROAD_TIPPED_BOLTS)))
				hit.setDamage(0);
		}
		super.handlePreHit(hit);
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(1608, 1609) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Kurask(npcId, tile);
		}
	};
}
