package com.rs.game.player.content.minigames.creations;

import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class StealingCreationShop {

	private static final int[] POINTS_COST = { 20, 20, 30, 28, 24, 26, 200 };
	private static final int[] DEFAULT_PRODUCTS = { 14098, 14106, 14094, 14095, 14096, 14097, 21527 };

	public static void openInterface(final Player player) {
		refresh(player);
		player.getInterfaceManager().sendInterface(1128);
		player.getPackets().sendRunScriptBlank(5248);
		player.getPackets().setIFHidden(1128, 330, true);
		player.getPackets().setIFText(1128, 13, "" + player.scPoints);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				resetSelection(player);
			}
		});
	}
	
	public static ButtonClickHandler handleButtons = new ButtonClickHandler(1128) {
		@Override
		public void handle(ButtonClickEvent e) {
			int index = -1;
			if (e.getComponentId() == 98 || e.getComponentId() == 4)
				index = 0;
			else if (e.getComponentId() == 128 || e.getComponentId() == 106)
				index = 1;
			else if (e.getComponentId() == 144 || e.getComponentId() == 166)
				index = 2;
			else if (e.getComponentId() == 203 || e.getComponentId() == 181)
				index = 3;
			else if (e.getComponentId() == 240 || e.getComponentId() == 218)
				index = 4;
			else if (e.getComponentId() == 277 || e.getComponentId() == 255)
				index = 5;
			else if (e.getComponentId() == 292 || e.getComponentId() == 314)
				index = 6;
			if (index != -1)
				select(e.getPlayer(), index);
//			else if (componentId == 45)
//				System.out.println("componentId =" + componentId);
			purchase(e.getPlayer());
		}
	};

	public static void select(Player player, int index) {
		boolean[] selectedList = (boolean[]) player.getTemporaryAttributes().get("sc_shop_selected");
		if (selectedList == null)
			selectedList = new boolean[7];
		if (selectedList[index])
			selectedList[index] = false;
		else
			selectedList[index] = true;
		player.getTemporaryAttributes().put("sc_shop_selected", selectedList);
	}

	private static void resetSelection(Player player) {
		player.getTemporaryAttributes().remove("sc_shop_selected");
	}

	public static void purchase(Player player) {
		boolean[] requestedList = (boolean[]) player.getTemporaryAttributes().get("sc_shop_selected");
		if (requestedList == null) {
			player.sendMessage("You have nothing selected to purchase / re-charge");
			return;
		}
		int totalCost = 0;
		for (int index = 0; index < requestedList.length; index++) {
			if (requestedList[index])
				totalCost += POINTS_COST[index];
		}
		if (player.scPoints < totalCost) {
			player.sendMessage("You don't have enough points.");
			resetSelection(player);
			return;
		} else if (player.getInventory().getFreeSlots() < requestedList.length) {
			player.sendMessage("You don't have enough space for the requested items.");
			resetSelection(player);
			return;
		}
		for (int index = 0; index < requestedList.length; index++) {
			if (requestedList[index])
				player.getInventory().addItem(new Item(DEFAULT_PRODUCTS[index]));
		}
		player.scPoints -= totalCost;
		refresh(player);
	}

	private static void refresh(Player player) {
		player.getVars().setVarBit(5505, player.scPoints);
	}
}
