package com.rs.game.npc.dungeoneering;

import java.util.List;

import com.rs.game.Entity;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

public class DungeonBoss extends DungeonNPC {

	private RoomReference reference;

	public DungeonBoss(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager);
		this.setReference(reference);
		this.resetBonuses();
		setForceAgressive(true);
		setIntelligentRouteFinder(true);
		setLureDelay(3000);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		getManager().openStairs(getReference());
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
		this.resetBonuses();
	}

	@Override
	public void drop() {
		DropTable[] drops = DropSets.getDropSet(getId()).getTables();
		if (drops == null || drops.length == 0)
			return;
		DropTable drop;
		if (getManager().getParty().getSize() == DungeonConstants.LARGE_DUNGEON)
			drop = drops[Utils.random(100) < 90 ? drops.length - 1 : Utils.random(drops.length)];
		else if (getManager().getParty().getSize() == DungeonConstants.LARGE_DUNGEON)
			drop = drops[Utils.random(100) < 60 ? drops.length - 1 : Utils.random(drops.length)];
		else
			drop = drops[Utils.random(drops.length)];
		List<Player> players = getManager().getParty().getTeam();
		if (players.size() == 0)
			return;
		Player killer = players.get(Utils.random(players.size()));
		if (drop != null) {
			for (Item item : drop.toItemArr())
				sendDrop(killer, item);
		}
	}

	
	@Override
	public void sendDrop(Player player, Item item) {
		List<Player> players = getManager().getParty().getTeam();
		if (players.size() == 0)
			return;
		player.getInventory().addItemDrop(item);
		player.sendMessage("<col=D2691E>You received: " + item.getAmount() + " " + item.getName() + ".");
		for (Player p2 : players) {
			if (p2 == player)
				continue;
			p2.sendMessage("<col=D2691E>" + player.getDisplayName() + " received: " + item.getAmount() + " " + item.getName() + ".");
		}
	}

	public RoomReference getReference() {
		return reference;
	}

	public void setReference(RoomReference reference) {
		this.reference = reference;
	}
}
