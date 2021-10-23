package com.rs.game.player.content.world;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.game.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class GraveStoneSelection {

	public static void openSelectionInterface(Player player) {
		player.getInterfaceManager().sendInterface(652);
		player.getPackets().setIFRightClickOps(652, 31, 0, 78, 0, 1);
		player.getPackets().setIFRightClickOps(652, 34, 0, 13, 0, 1);
		player.getVars().setVar(1146, player.getGraveStone() | 262112);
	}
	
	public static ButtonClickHandler handleSelectionInterface = new ButtonClickHandler(652) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 31)
				e.getPlayer().getTempAttribs().setI("graveSelection", e.getSlotId());
			else if (e.getComponentId() == 34)
				confirmSelection(e.getPlayer());
		}
	};
	
	private static String getStoneName(int slot) {
		return EnumDefinitions.getEnum(1099).getStringValue(slot);
	}
	
	private static int getStonePrice(int slot) {
		return EnumDefinitions.getEnum(1101).getIntValue(slot);
	}

	public static void confirmSelection(Player player) {
		int slot = player.getTempAttribs().getI("graveSelection", -1) / 6;
		int price = getStonePrice(slot);
		String name = getStoneName(slot);
		if (slot != -1) {
			if (player.getInventory().getAmountOf(995) < price) {
				player.sendMessage("You need " + Utils.formatNumber(price) + " coins to purchase " + Utils.addArticle(name) + ".");
				return;
			}
			player.getInventory().deleteItem(995, price);
			player.setGraveStone(slot);
			player.closeInterfaces();
		}
	}
}