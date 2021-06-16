package com.rs.game.player.managers;

import com.rs.game.item.ItemsContainer;
import com.rs.game.player.Player;
import com.rs.game.player.content.ItemConstants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.EconomyPrices;

@PluginEventHandler
public class PriceCheckManager {

	private Player player;
	private ItemsContainer<Item> pcInv;

	public PriceCheckManager(Player player) {
		this.player = player;
		pcInv = new ItemsContainer<Item>(28, false);
	}

	public void openPriceCheck() {
		player.getInterfaceManager().sendInterface(206);
		player.getInterfaceManager().sendInventoryInterface(207);
		sendInterItems();
		sendOptions();
		player.getPackets().sendVarc(728, 0);
		for (int i = 0; i < pcInv.getSize(); i++)
			player.getPackets().sendVarc(700 + i, 0);
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getInventory().getItems().addAll(pcInv);
				player.getInventory().init();
				pcInv.clear();
			}
		});
	}

	public int getSlotId(int clickSlotId) {
		return clickSlotId / 2;
	}

	public void removeItem(int clickSlotId, int amount) {
		int slot = getSlotId(clickSlotId);
		Item item = pcInv.get(slot);
		if (item == null)
			return;
		Item[] itemsBefore = pcInv.getItemsCopy();
		int maxAmount = pcInv.getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		pcInv.remove(slot, item);
		player.getInventory().addItem(item);
		refreshItems(itemsBefore);
	}

	public void addItem(int slot, int amount) {
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.sendMessage("That item isn't tradeable.");
			return;
		}
		Item[] itemsBefore = pcInv.getItemsCopy();
		int maxAmount = player.getInventory().getItems().getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		pcInv.add(item);
		player.getInventory().deleteItem(slot, item);
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int totalPrice = 0;
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = pcInv.getItems()[index];
			if (item != null)
				totalPrice += EconomyPrices.getPrice(item.getId()) * item.getAmount();
			if (itemsBefore[index] != item) {
				changedSlots[count++] = index;
				player.getPackets().sendVarc(700 + index, item == null ? 0 : EconomyPrices.getPrice(item.getId()));
			}

		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		player.getPackets().sendVarc(728, totalPrice);
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(90, pcInv, slots);
	}

	public void sendOptions() {
		player.getPackets().setIFRightClickOps(206, 15, 0, 54, 0, 1, 2, 3, 4, 5, 6);
		player.getPackets().setIFRightClickOps(207, 0, 0, 27, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(207, 0, 93, 4, 7, "Add", "Add-5", "Add-10", "Add-All", "Add-X", "Examine");
	}

	public void sendInterItems() {
		player.getPackets().sendItems(90, pcInv);
	}
	
	public static ButtonClickHandler handleButtons = new ButtonClickHandler(206, 207) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getInterfaceId() == 206) {
				if (e.getComponentId() == 15) {
					switch(e.getPacket()) {
					case IF_OP1:
						e.getPlayer().getPriceCheckManager().removeItem(e.getSlotId(), 1);
						break;
					case IF_OP2:
						e.getPlayer().getPriceCheckManager().removeItem(e.getSlotId(), 5);
						break;
					case IF_OP3:
						e.getPlayer().getPriceCheckManager().removeItem(e.getSlotId(), 10);
						break;
					case IF_OP4:
						e.getPlayer().getPriceCheckManager().removeItem(e.getSlotId(), Integer.MAX_VALUE);
						break;
					case IF_OP5:
						e.getPlayer().getTemporaryAttributes().put("pc_item_X_Slot", e.getSlotId());
						e.getPlayer().getTemporaryAttributes().put("pc_isRemove", Boolean.TRUE);
						e.getPlayer().getPackets().sendRunScriptReverse(108, new Object[] { "Enter Amount:" });
						break;
					default:
						break;
					}
				}
			} else if (e.getInterfaceId() == 207) {
				if (e.getComponentId() == 0) {
					switch(e.getPacket()) {
					case IF_OP1:
						e.getPlayer().getPriceCheckManager().addItem(e.getSlotId(), 1);
						break;
					case IF_OP2:
						e.getPlayer().getPriceCheckManager().addItem(e.getSlotId(), 5);
						break;
					case IF_OP3:
						e.getPlayer().getPriceCheckManager().addItem(e.getSlotId(), 10);
						break;
					case IF_OP4:
						e.getPlayer().getPriceCheckManager().addItem(e.getSlotId(), Integer.MAX_VALUE);
						break;
					case IF_OP5:
						e.getPlayer().getTemporaryAttributes().put("pc_item_X_Slot", e.getSlotId());
						e.getPlayer().getTemporaryAttributes().remove("pc_isRemove");
						e.getPlayer().getPackets().sendRunScriptReverse(108, new Object[] { "Enter Amount:" });
						break;
					case IF_OP6:
						e.getPlayer().getInventory().sendExamine(e.getSlotId());
						break;
					default:
						break;
					}
				}
			}
		}
	};
}
