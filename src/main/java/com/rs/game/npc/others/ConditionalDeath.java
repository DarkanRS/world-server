package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class ConditionalDeath extends NPC {

	private int requiredItem;
	private String deathMessage;
	private boolean remove;

	public ConditionalDeath(int requiredItem, String deathMessage, boolean remove, int id, WorldTile tile) {
		super(id, tile);
		this.requiredItem = requiredItem;
		this.deathMessage = deathMessage;
		this.remove = remove;
	}

	public int getRequiredItem() {
		return requiredItem;
	}

	private boolean removeItem(Player player) {
		if (this.getHitpoints() < (getMaxHitpoints() * 0.1) && (player.getEquipment().getWeaponId() == requiredItem || player.getInventory().containsItem(requiredItem, 1))) {
			if (remove)
				player.getInventory().deleteItem(requiredItem, 1);
			return true;
		}
		return false;
	}

	public boolean useHammer(Player player) {
		if (removeItem(player)) {
			if (deathMessage != null)
				player.sendMessage(deathMessage);
			// missing emote
			if (getId() == 14849)
				player.setNextAnimation(new Animation(15845));
			setHitpoints(0);
			super.sendDeath(player);
			return true;
		}
		return false;
	}

	@Override
	public void sendDeath(Entity source) {
		if (source instanceof Player) {
			Player player = (Player) source;
			if ((player.hasLearnedQuickBlows() || player.getEquipment().getWeaponId() == requiredItem || player.getEquipment().getGlovesId() == requiredItem) && useHammer(player))
				return;
			player.sendMessage("The " + getName() + " is on its last legs! Finish it quickly!");
		}
		setHitpoints(1);
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(1610, 1631, 1632, 2803, 2804, 2805, 2806, 2807, 2808, 14849) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			if (npcId == 1631 || npcId == 1632)
				return new ConditionalDeath(4161, "The rockslug shrivels and dies.", true, npcId, tile);
			else if (npcId == 1610)
				return new ConditionalDeath(4162, "The gargoyle breaks into peices as you slam the hammer onto its head.", false, npcId, tile);
			else if (npcId == 14849)
				return new ConditionalDeath(23035, null, false, npcId, tile);
			else
				return new ConditionalDeath(6696, null, true, npcId, tile);
		}
	};
}