package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.game.World;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.skills.firemaking.Firemaking;
import com.rs.game.player.content.skills.firemaking.Firemaking.Fire;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.skills.magic.Rune;
import com.rs.game.player.content.skills.magic.RuneSet;
import com.rs.game.player.managers.TreasureTrailsManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnGroundItem;
import com.rs.lib.util.Utils;

public class IFOnGroundItemHandler implements PacketHandler<Player, IFOnGroundItem> {

	@Override
	public void handle(Player player, IFOnGroundItem packet) {
		if (Utils.getInterfaceDefinitionsSize() <= packet.getInterfaceId()) {
			return;
		}
		if (player.isDead() || !player.getInterfaceManager().containsInterface(packet.getInterfaceId()))
			return;

		if (packet.getComponentId() != 65535 && Utils.getInterfaceDefinitionsComponentsSize(packet.getInterfaceId()) <= packet.getComponentId()) {
			return;
		}
		final WorldTile tile = new WorldTile(packet.getX(), packet.getY(), player.getPlane());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId))
			return;
		GroundItem groundItem = World.getRegion(regionId).getGroundItem(packet.getItemId(), tile, player);
		if (groundItem == null)
			return;
		player.stopAll();
		if (packet.isForceRun())
			player.setRun(packet.isForceRun());
		if (packet.getInterfaceId() == 679) {
			Item item = player.getInventory().getItem(packet.getSlotId());
			if (item == null)
				return;
			
			player.setRouteEvent(new RouteEvent(groundItem, new Runnable() {
				@Override
				public void run() {
					if (item.getId() == 590) {
						Fire fire = Fire.forId(groundItem.getId());
						if (fire != null) {
							player.getActionManager().setAction(new Firemaking(fire, groundItem));
						}
					}
				}
			}));
		} else if (packet.getInterfaceId() == 192 && packet.getComponentId() == 44) {
			player.getActionManager().setAction(new Action() {
				@Override
				public boolean start(Player player) {
					return true;
				}
				
				public boolean process() {
					if (player.isDead() || player.hasFinished())
						return false;
					final GroundItem item = World.getRegion(regionId).getGroundItem(packet.getItemId(), tile, player);
					if (item == null)
						return false;
					if (player.getPlane() != tile.getPlane())
						return false;
					if (player.isFrozen())
						return true;
					if (!player.lineOfSightTo(tile, false) || Utils.getDistance(player, tile) > 8) {
						if (player.hasWalkSteps())
							player.resetWalkSteps();
						player.calcFollow(tile, 25, true, true);
						return true;
					}
					if (TreasureTrailsManager.isScroll(item.getId())) {
						if (player.getTreasureTrailsManager().hasClueScrollItem()) {
							player.sendMessage("You should finish the clue you are currently doing first.");
							return false;
						}
					}
					if (Magic.checkMagicAndRunes(player, 33, true, new RuneSet(Rune.AIR, 1, Rune.LAW, 1))) {
						player.getActionManager().setActionDelay(3);
						player.resetWalkSteps();
						player.setNextFaceWorldTile(tile);
						player.setNextAnimation(new Animation(711));
						player.getSkills().addXp(Constants.MAGIC, 43);
						player.setNextSpotAnim(new SpotAnim(142, 2, 50, Utils.getAngleTo(tile.getX() - player.getX(), tile.getY() - player.getY())));
						World.sendProjectile(player, tile, 143, 35, 0, 60, 1, 0, 0, () -> {
							final GroundItem gItem = World.getRegion(regionId).getGroundItem(packet.getItemId(), tile, player);
							if (gItem == null) {
								player.sendMessage("Too late. It's gone!");
								return;
							}
							World.sendSpotAnim(null, new SpotAnim(144), tile);
							World.removeGroundItem(player, gItem, true);
						});
					}
					return false;
				}

				@Override
				public boolean process(Player player) {
					return process();
				}

				@Override
				public int processWithDelay(Player player) {
					return 0;
				}

				@Override
				public void stop(Player player) {
					
				}
			});
		}
	}
}
