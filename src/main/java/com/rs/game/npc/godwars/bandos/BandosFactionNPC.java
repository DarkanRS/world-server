package com.rs.game.npc.godwars.bandos;

import com.rs.game.npc.NPC;
import com.rs.game.npc.godwars.zaros.ZarosFactionNPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class BandosFactionNPC extends NPC {

	public BandosFactionNPC(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setIgnoreDocile(true);
		setCanAggroNPCs(true);
	}
	
	public boolean canAggroNPC(NPC npc) {
		return !(npc instanceof BandosFactionNPC);
	}
	
	@Override
	public boolean canAggroPlayer(Player player) {
		return !hasGodItem(player);
	}

	private boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItemsCopy()) {
			if (item == null)
				continue; // shouldn't happen
			String name = item.getDefinitions().getName().toLowerCase();

			if (name.contains("bandos") || name.contains("book of war") || name.contains("ancient mace") || name.contains("granite mace") || ZarosFactionNPC.isNexArmour(name) || item.getId() == 19612)
				return true;
		}
		return false;
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6268, 6269, 6270, 6271, 6272, 6273, 6274, 6275, 6276, 6277, 6278, 6279, 6280, 6281, 6282, 6283) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new BandosFactionNPC(npcId, tile, false);
		}
	};
}
