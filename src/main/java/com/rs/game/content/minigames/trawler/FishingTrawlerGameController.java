package com.rs.game.content.minigames.trawler;

import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.RegionUtils;

@PluginEventHandler
public class FishingTrawlerGameController extends Controller {


	@Override
	public void start() {
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		sendMessage();
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		sendMessage();
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		sendMessage();
		return false;
	}

	public void sendMessage() {
		player.getPackets().sendPlayerMessage(0, 0xFF0000, "You're too far away from shore to teleport!");
	}

	@Override
	public boolean login() {
		FishingTrawler.getInstance().removeGamePlayer(player);
		player.setNextWorldTile(FishingTrawler.SHORE.getRandomTile());
		return true;
	}

	@Override
	public boolean logout() {
		FishingTrawler.getInstance().removeGamePlayer(player);
		player.setLocation(FishingTrawler.SHORE.getRandomTile());
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		FishingTrawler trawler = FishingTrawler.getInstance();
		if(object.getId() == 255)
			player.sendMessage("It seems the winch is jammed - I can't move it.");
		else if(object.getId() == 2174 || object.getId() == 2175) {
			WorldTile tile;
			if(object.getId() == 2174)
				tile = object.getY() == 4826 ? WorldTile.of(1883, 4826, 1) : WorldTile.of(1894, 4824, 1);
			else
				tile = object.getY() == 4826 ? WorldTile.of(1885, 4826, 0) : WorldTile.of(1892, 4824, 0);
			if(trawler.isWaterShip())
				tile = tile.transform(128, 0);
			player.useLadder(tile);
			return true;
		} else if(object.getId() == 2164 || object.getId() == 2165) {
			player.setNextAnimation(new Animation(537));
			if(!trawler.isRipped()) {
				player.sendMessage("The net is not damaged.");
				return true;
			}
			if(!player.getInventory().containsItem(954)) {
				player.sendMessage("You'll need some rope to fix it.");
				return true;
			}
			player.getInventory().deleteItem(954, 1);
			trawler.setRipped(false);
			trawler.addActivity(player, 200);
		}
		return false;
	}

	public static ObjectClickHandler fillLeak = new ObjectClickHandler(false, new Object[] { FishingTrawler.LEAK }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().resetWalkSteps();
			RegionUtils.Area area = FishingTrawler.getInstance().isWaterShip() ? FishingTrawler.WATER_SHIP : FishingTrawler.NO_WATER_SHIP;
			WorldTile tile;
			if(e.getObject().getY() == area.getY()-1)
				tile = e.getObject().getTile().transform(0, 1);
			else
				tile = e.getObject().getTile().transform(0, 0);
			e.getPlayer().addWalkSteps(tile, 25, false);
			e.getPlayer().setRouteEvent(new RouteEvent(tile, () -> {
				e.getPlayer().lock(1);
				e.getPlayer().setNextFaceWorldTile(WorldTile.of(e.getObject().getTile()));
				if(!e.getPlayer().getInventory().containsItem(FishingTrawler.SWAMP_PASTE)) {
					e.getPlayer().sendMessage("You'll need some swamp paste to fill that.");
					return;
				}
				e.getPlayer().setNextAnimation(new Animation(827));
				FishingTrawler.getInstance().addActivity(e.getPlayer(), 50);
				FishingTrawler.getInstance().cheerMonty();
				FishingTrawler.getInstance().repairLeak(e.getObject());
				e.getPlayer().getInventory().deleteItem(new Item(FishingTrawler.SWAMP_PASTE, 1));
			}));
		}
	};

	public static ItemClickHandler emptyBailingBucketClick = new ItemClickHandler(new Object[] { 585 }, new String[] { "Empty" }) {
		@Override
		public void handle(ItemClickEvent e) {
			Controller controller = e.getPlayer().getControllerManager().getController();
			String message;
			if(controller instanceof FishingTrawlerGameController)
				message = "You tip the water over the side.";
			else
				message = "You tip the water out of the bucket.";
			e.getPlayer().sendMessage(message);
			e.getPlayer().getInventory().getItems().set(e.getItem().getSlot(), new Item(583, 1));
			e.getPlayer().getInventory().refresh(e.getItem().getSlot());
			FishingTrawler.getInstance().addActivity(e.getPlayer(), 20);
			e.getPlayer().setNextAnimation(new Animation(827));
			e.getPlayer().lock(1);
		}
	};

	public static ItemClickHandler bailingBucketClick = new ItemClickHandler(new Object[] { 583 }, new String[] { "Bail-with" }) {
		@Override
		public void handle(ItemClickEvent e) {
			Controller controller = e.getPlayer().getControllerManager().getController();
			if(!(controller instanceof FishingTrawlerGameController)) {
				e.getPlayer().sendMessage("I don't really need to bail yet.");
				return;
			}
			FishingTrawler trawler = FishingTrawler.getInstance();
			if(!trawler.isWaterShip()) {
				e.getPlayer().sendMessage("I don't really need to bail yet.");
				return;
			}
			trawler.setWaterLevel(trawler.getWaterLevel() - 1);
			if(trawler.getWaterLevel() < 0)
				trawler.setWaterLevel(0);
			int activity = trawler.getActivity().get(e.getPlayer().getUsername()) + 100;
			if(activity > 1000) activity = 1000;
			trawler.getActivity().put(e.getPlayer().getUsername(), activity);
			e.getPlayer().sendMessage("You fill the bucket with water.");
			e.getPlayer().getInventory().getItems().set(e.getItem().getSlot(), new Item(585, 1));
			e.getPlayer().getInventory().refresh(e.getItem().getSlot());
			FishingTrawler.getInstance().addActivity(e.getPlayer(), 20);
			e.getPlayer().setNextAnimation(new Animation(832));
			e.getPlayer().lock(1);
		}
	};
}
