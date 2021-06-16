package com.rs.game.npc.godwars.armadyl;

import com.rs.game.npc.NPC;
import com.rs.game.npc.godwars.zaros.ZarosFactionNPC;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class ArmadylFactionNPC extends NPC {

	public ArmadylFactionNPC(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setIgnoreDocile(true);
		setCanAggroNPCs(true);
	}
	
	@Override
	public boolean canBeAttackedBy(Player player) {
		if (!PlayerCombat.isRanging(player)) {
			player.sendMessage("The aviansie is flying too high for you to attack using melee.");
			return false;
		}
		return true;
	}
	
	public boolean canAggroNPC(NPC npc) {
		return !(npc instanceof ArmadylFactionNPC);
	}
	
	@Override
	public boolean canAggroPlayer(Player player) {
		return !hasGodItem(player);
	}

	private boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItemsCopy()) {
			if (item == null)
				continue;
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("armadyl") || name.contains("book of law") || ZarosFactionNPC.isNexArmour(name) || item.getId() == 19614)
				return true;
		}
		return false;
	}
	
	public static NPCInstanceHandler toAbyssalDemon = new NPCInstanceHandler(6228, 6229, 6230, 6231, 6232, 6233, 6234, 6235, 6236, 6237, 6238, 6239, 6240, 6241, 6242, 6243, 6244, 6245, 6246) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new ArmadylFactionNPC(npcId, tile, false);
		}
	};
}
