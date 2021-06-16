package com.rs.game.npc.godwars.zammorak;

import com.rs.game.npc.NPC;
import com.rs.game.npc.godwars.zaros.ZarosFactionNPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class ZamorakFactionNPC extends NPC {

	public ZamorakFactionNPC(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setIgnoreDocile(true);
		setCanAggroNPCs(true);
	}

	public boolean canAggroNPC(NPC npc) {
		return !(npc instanceof ZamorakFactionNPC);
	}
	
	@Override
	public boolean canAggroPlayer(Player player) {
		return !hasGodItem(player);
	}

	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItemsCopy()) {
			if (item == null || item.getId() == -1)
				continue; // shouldn't happen
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("zamorak") || name.contains("unholy") || ZarosFactionNPC.isNexArmour(name) || item.getId() == 3841)
				return true;
		}
		return false;
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6210, 6211, 6212, 6213, 6214, 6215, 6216, 6217, 6218, 6219, 6220, 6221) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new ZamorakFactionNPC(npcId, tile, false);
		}
	};
}
