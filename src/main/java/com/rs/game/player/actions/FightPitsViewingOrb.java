package com.rs.game.player.actions;

import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;

public class FightPitsViewingOrb extends Action {

	public static final WorldTile[] ORB_TELEPORTS = { new WorldTile(4571, 5092, 0), new WorldTile(4571, 5107, 0), new WorldTile(4590, 5092, 0), new WorldTile(4571, 5077, 0), new WorldTile(4557, 5092, 0) };

	private WorldTile tile;

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		tile = new WorldTile(player);
		player.getAppearance().switchHidden();
		player.getPackets().setBlockMinimapState(5);
		player.setNextWorldTile(ORB_TELEPORTS[0]);
		player.getInterfaceManager().sendInventoryInterface(374);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getPoison().isPoisoned()) {
			player.sendMessage("You can't use orb while you're poisoned.");
			return false;
		}
		if (player.getFamiliar() != null) {
			player.sendMessage("You can't use orb with a familiar.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(final Player player) {
		player.lock(2);
		player.getInterfaceManager().removeInventoryInterface();
		player.getAppearance().switchHidden();
		player.getPackets().setBlockMinimapState(0);
		player.setNextWorldTile(tile);
	}

}
