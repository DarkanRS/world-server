package com.rs.game.npc.godwars.saradomin;

import com.rs.game.npc.NPC;
import com.rs.game.npc.godwars.zaros.ZarosFactionNPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class SaradominFactionNPC extends NPC {

	public SaradominFactionNPC(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setIgnoreDocile(true);
		setCanAggroNPCs(true);
	}

	public boolean canAggroNPC(NPC npc) {
		return !(npc instanceof SaradominFactionNPC);
	}
	
	@Override
	public boolean canAggroPlayer(Player player) {
		return !hasGodItem(player);
	}

	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItemsCopy()) {
			if (item == null)
				continue; // shouldn't happen
			String name = item.getDefinitions().getName().toLowerCase();
			// using else as only one item should count
			if (name.contains("saradomin") || name.contains("holy symbol") || name.contains("holy book") || name.contains("monk") || name.contains("citharede") || ZarosFactionNPC.isNexArmour(name) || item.getId() == 3839)
				return true;
		}
		return false;
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6254, 6255, 6256, 6257, 6258, 6259) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new SaradominFactionNPC(npcId, tile, false);
		}
	};
}
