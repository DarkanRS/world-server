package com.rs.game.player.content.transportation;

import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.dialogues.QuickCharter;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class TravelMethods {

	private static final int TRAVEL_INTERFACE = 299, CHARTER_INTERFACE = 95;
	private static final int[] REGIONS = new int[] { 8496, 14646, 11061, 11823, 11825, 11058, 10545, 12081, 14637, -1, 10284 };

	public enum Carrier {
		PORT_TYRAS(new int[] { -1, 3200, 3200, 3200, 3200, 3200, 3200, 3200, 1600, -1, 3200 }, new WorldTile(0,33,48,30,50)),
		PORT_PHASMATYS(new int[] { 3200, -1, 3500, -1, 2900, 2900, 4100, 1300, -1, -1, 2800 }, new WorldTile(0,57,54,54,47)),
		CATHERBY(new int[] { 3200, 2500, -1, 1600, 480, 480, 1600, 1000, 1750, -1, 3400 }, new WorldTile(0,43,53,40,22)),
		SHIP_YARD(new int[] { 3200, -1, 1600, -1, 200, 400, 720, 400, 225, -1, 900 }, new WorldTile(0,46,47,57,24)),
		KARAMJA(new int[] { 3200, 2900, 480, 200, -1, 200, 400, -1, 225, -1, 2000 }, new WorldTile(0,46,49,10,22)),
		BRIMHAVEN(new int[] { 3200, 2900, 480, 400, 200, -1, 400, 1600, 975, -1, 3800 }, new WorldTile(0,43,50,8,38)),
		PORT_KHAZARD(new int[] { 3200, 4100, 1600, 1600, 1600, 1600, -1, 1280, 1025, -1, 5000 }, new WorldTile(0,41,49,50,8)),
		PORT_SARIM(new int[] { 3200, 1300, 1000, 400, -1, 1600, 1280, -1, 325, -1, 1400 }, new WorldTile(0,47,49,35,55)),
		MOS_LE_HARMLESS(new int[] { 1600, -1, 625, 275, 1025, 725, 1025, 325, -1, -1, 500 }, new WorldTile(0,57,45,23,51)),	
		CRANDOR(new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, new WorldTile(0,40,44,63,41)), //disabled
		OO_GLOG(new int[] { 3200, 2800, 3400, 900, 2000, 3800, 5000, 1400, 550, -1, -1 }, new WorldTile(0,40,44,63,41)),
		ENTRANA_FARE(null, "Port Sarim", new WorldTile(2834, 3335, 0), new WorldTile(3048, 3234, 0)),
		CRANDOR_FARE(null, "Port Sarim", new WorldTile(2853, 3238, 0), new WorldTile(3047, 3204, 0)),
		KARAMJA_FARE(new int[] { 30 }, "Port Sarim", new WorldTile(2956, 3146, 0), new WorldTile(3029, 3217, 0)),
		BRIMHAVEN_FARE(new int[] { 30 }, "Ardougne", new WorldTile(2772, 3234, 0), new WorldTile(2683, 3271, 0)),
		UNUSED(null, null, null, null),
		PORT_KHAZARD_FARE(null, "Ship yard", new WorldTile(2981, 3052, 0), new WorldTile(2676, 3170, 0)), // 15
		CARIN_ISLAND_FARE(null, "Ship yard", new WorldTile(2995, 3052, 0), new WorldTile(2763, 2956, 1)),
		VOID_OUTPOST_FARE(null, "Port Sarim", new WorldTile(2659, 2676, 0), new WorldTile(3041, 3202, 0)),
		JATIZO(null, "Relleka", new WorldTile(2422, 3781, 0), new WorldTile(2643, 3710, 0)),
		NEITZNOT(null, "Relleka", new WorldTile(2311, 3781, 0), new WorldTile(2643, 3710, 0)),
		WATERBIRTH(null, "Relleka", new WorldTile(2551, 3756, 0), new WorldTile(2620, 3686, 0)),
		MISCELLENIA(null, "Relleka", new WorldTile(2581, 3847, 0), new WorldTile(2629, 3693, 0)),
		PIRATES_COVE(null, "Relleka", new WorldTile(2213, 3794, 0), new WorldTile(2620, 3686, 0)),
		LUNAR_ISLE(null, "Pirate's Cove", new WorldTile(2138, 3900, 2), new WorldTile(2223, 3797, 2)),
		TEACH_MOS_LE_HARMLESS(null, "Relleka", new WorldTile(3682, 2948, 1), new WorldTile(3714, 3499, 1)),
		LUMBRIDGE_CANOE(null, null, new WorldTile(3233, 3249, 0), null),
		CHAMPIONS_GUILD_CANOE(null, null, new WorldTile(3199, 3343, 0), null),
		BARBARIAN_VILLAGE_CANOE(null, null, new WorldTile(3113, 3406, 0), null),
		EDGEVILLE_CANOE(null, null, new WorldTile(3130, 3505, 0), null),
		WILDERNESS_CANOE(null, null, new WorldTile(3147, 3799, 0), null);

		private int[] fares;
		private WorldTile destination, origin;
		private String secondDest;

		private Carrier(int[] fare, String secondDest, WorldTile destination, WorldTile origin) {
			this.fares = fare;
			this.destination = destination;
			this.secondDest = secondDest;
			this.origin = origin;
		}

		private Carrier(int[] fares, WorldTile destination) {
			this(fares, null, destination, null);
		}

		public int[] getFares() {
			return fares;
		}

		public WorldTile getDestination() {
			return destination;
		}

		public WorldTile getOrigon() {
			return origin;
		}

		public String getFixedName(boolean returning) {
			return (returning ? secondDest : toString().toLowerCase().replace("_FARE", "").replace("_", " "));
		}
	}
	
	public static NPCClickHandler handleCharterShips = new NPCClickHandler("Trader Crewmember", "Trader crewmember") {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 3)
				ShopsHandler.openShop(e.getPlayer(), "trader_stans_trading_post");
			else if (e.getOpNum() == 4)
				TravelMethods.openCharterInterface(e.getPlayer());
		}
	};
	
	public static void openCharterInterface(Player player) {
		player.getInterfaceManager().sendInterface(CHARTER_INTERFACE);
		int shipIndex = getOriginIndex(player.getRegionId());
		if (shipIndex == -1)
			return;
		Carrier ship = Carrier.values()[shipIndex];
		for (int index = 0; index < ship.fares.length; index++) {
			if (ship.getFares()[index] == -1)
				player.getPackets().setIFHidden(CHARTER_INTERFACE, 23 + index, true);
		}
	}
	
	public static ButtonClickHandler handleButtons = new ButtonClickHandler(95) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 23 && e.getComponentId() <= 33)
				handleCharterOptions(e.getPlayer(), e.getComponentId());
		}
	};
	
	public static void handleCharterOptions(Player player, int componentId) {
		int index = componentId - 23;
		if (index < 0 || index > 10)
			return;
		Carrier ship = Carrier.values()[index];
		player.closeInterfaces();

		int costIndex = getOriginIndex(player.getRegionId());
		player.getDialogueManager().execute(new QuickCharter(), ship, costIndex);
	}

	private static int getOriginIndex(int regionId) {
		for (int index = 0; index < REGIONS.length; index++) {
			if (REGIONS[index] == regionId)
				return index;
		}
		return -1;
	}

	public static boolean sendCarrier(final Player player, final Carrier ship, boolean returning) {
		return sendCarrier(player, ship, 0, returning);
	}

	public static boolean sendCarrier(final Player player, final Carrier ship, int shipIndex, boolean returning) {
		if (player.getTemporaryAttributes().get("using_carrier") != null)
			return false;
		int cost = -1;
		if (ship.getFares() != null)
			cost = ship.getFares()[shipIndex];
		if (cost != -1) {
			if (player.getInventory().getAmountOf(995) < cost) {
				player.sendMessage("You don't have enough money for that.");
				return false;
			}
			player.getInventory().deleteItem(new Item(995, cost));
			player.sendMessage("You pay the fare and sail to " + ship.getFixedName(returning) + ".");
		}
		final boolean isFare = ship.toString().contains("Fare");
		if (isFare) {
			if (ship.ordinal() == 10 && !returning) {
				boolean hasEquip = false;
				for (Item item : player.getInventory().getItems().getItems()) {
					if (item == null)
						continue;
					if (Equipment.getItemSlot(item.getId()) != -1) {
						hasEquip = true;
						break;
					}
				}
				if (player.getEquipment().wearingArmour() || hasEquip) {
					player.sendMessage("The monk refuses to let you board. Please bank all your equippable items.");
					return false;
				}
			}
			player.getInterfaceManager().sendInterface(TRAVEL_INTERFACE);
			int configValue = 1 + (((ship.ordinal() - 10) * 2) + (ship.ordinal() >= 17 ? returning ? -1 : 0 : returning ? 1 : 0));
			player.getVars().setVar(75, configValue);
		}
		final WorldTile tile = returning ? ship.getOrigon() : ship.getDestination();
		player.lock();
		player.getMusicsManager().playMusic(550);
		player.getTemporaryAttributes().put("using_carrier", true);
		FadingScreen.fade(player, new Runnable() {

			@Override
			public void run() {// 9
				player.setNextWorldTile(tile);
				player.lock(1);
				player.closeInterfaces();
				if (isFare)
					player.getVars().setVar(75, 0);
				player.getTemporaryAttributes().remove("using_carrier");
			}
		});
		return true;
	}
}
