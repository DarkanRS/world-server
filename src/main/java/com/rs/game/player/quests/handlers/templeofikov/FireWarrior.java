package com.rs.game.player.quests.handlers.templeofikov;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.others.OwnedNPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.combat.AmmoType;
import com.rs.game.player.content.combat.RangedWeapon;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class FireWarrior extends OwnedNPC {
	public FireWarrior(Player p, int id, WorldTile tile) {
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
