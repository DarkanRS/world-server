// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content;

import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.cache.loaders.interfaces.IFTargetParams.UseFlag;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Item;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.ItemExamines;
import com.rs.utils.shop.ShopItem;

@PluginEventHandler
public class Shop {

	private static final int MAIN_STOCK_ITEMS_KEY = 0;

	private static final int MAX_SHOP_ITEMS = 40;
	public static final int COINS = 995;

	private String name;
	private ShopItem[] mainStock;
	private int[] defaultQuantity;
	private ShopItem[] generalStock;
	private boolean buyOnly;
	private int currency;

	private transient CopyOnWriteArrayList<Player> viewingPlayers;

	public Shop(String name, int money, ShopItem[] shopItems, boolean isGeneralStore, boolean buyOnly) {
		viewingPlayers = new CopyOnWriteArrayList<>();
		this.name = name;
		currency = money;
		mainStock = shopItems;
		defaultQuantity = new int[shopItems.length];
		for (int i = 0; i < defaultQuantity.length; i++)
			defaultQuantity[i] = shopItems[i].getItem().getAmount();
		if (isGeneralStore && shopItems.length < MAX_SHOP_ITEMS)
			generalStock = new ShopItem[MAX_SHOP_ITEMS - shopItems.length];
		this.buyOnly = buyOnly;
		for (ShopItem item : mainStock) {
			if (item == null)
				continue;
			item.init();
		}
	}

	public static ButtonClickHandler handleInterfaces = new ButtonClickHandler(449, 1265, 1266, 621) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getInterfaceId() == 449) {
				if (e.getComponentId() == 1) {
					Shop shop = e.getPlayer().getTempAttribs().getO("Shop");
					if (shop == null)
						return;
					shop.sendInventory(e.getPlayer());
				} else if (e.getComponentId() == 21) {
					Shop shop = e.getPlayer().getTempAttribs().getO("Shop");
					if (shop == null)
						return;
					int slot = e.getPlayer().getTempAttribs().getI("ShopSelectedSlot");
					if (slot == -1)
						return;
					if (e.getPacket() == ClientPacket.IF_OP1)
						shop.buy(e.getPlayer(), slot, 1);
					else if (e.getPacket() == ClientPacket.IF_OP2)
						shop.buy(e.getPlayer(), slot, 5);
					else if (e.getPacket() == ClientPacket.IF_OP3)
						shop.buy(e.getPlayer(), slot, 10);
					else if (e.getPacket() == ClientPacket.IF_OP4)
						shop.buy(e.getPlayer(), slot, 50);
					shop.sendCustomPrices(e.getPlayer());
				}
			} else if (e.getInterfaceId() == 1265) {
				Shop shop = e.getPlayer().getTempAttribs().getO("Shop");
				if (shop == null)
					return;
				int slot = e.getPlayer().getTempAttribs().getI("ShopSelectedSlot");
				boolean isBuying = e.getPlayer().getTempAttribs().getB("shop_buying");
				if (e.getComponentId() == 20) {
					e.getPlayer().getTempAttribs().setI("ShopSelectedSlot", e.getSlotId());
					if (e.getPacket() == ClientPacket.IF_OP1)
						shop.sendInfo(e.getPlayer(), e.getSlotId(), isBuying);
					else if (e.getPacket() == ClientPacket.IF_OP2)
						shop.handleShop(e.getPlayer(), e.getSlotId(), 1);
					else if (e.getPacket() == ClientPacket.IF_OP3)
						shop.handleShop(e.getPlayer(), e.getSlotId(), 5);
					else if (e.getPacket() == ClientPacket.IF_OP4)
						shop.handleShop(e.getPlayer(), e.getSlotId(), 10);
					else if (e.getPacket() == ClientPacket.IF_OP5)
						shop.handleShop(e.getPlayer(), e.getSlotId(), 50);
					else if (e.getPacket() == ClientPacket.IF_OP6)
						shop.handleShop(e.getPlayer(), e.getSlotId(), 500);
					else if (e.getPacket() == ClientPacket.IF_OP10)
						shop.sendExamine(e.getPlayer(), e.getSlotId());
				} else if (e.getComponentId() == 201) {
					if (slot == -1)
						return;
					if (isBuying)
						shop.buy(e.getPlayer(), slot, e.getPlayer().getTempAttribs().getI("shopAmt", 0));
					else {
						shop.sell(e.getPlayer(), slot, e.getPlayer().getTempAttribs().getI("shopAmt", 0));
						e.getPlayer().getVars().setVar(2563, 0);
						e.getPlayer().getVars().setVar(2565, 1); // this is to update the tab.
					}
				} else if (e.getComponentId() == 208) {
					e.getPlayer().getTempAttribs().setI("shopAmt", Utils.clampI(e.getPlayer().getTempAttribs().getI("shopAmt", 0) + 5, 1, 5000));
					e.getPlayer().getPackets().setIFText(1265, 67, String.valueOf(e.getPlayer().getTempAttribs().getI("shopAmt", 0)));
				} else if (e.getComponentId() == 15) {
					e.getPlayer().getTempAttribs().setI("shopAmt", Utils.clampI(e.getPlayer().getTempAttribs().getI("shopAmt", 0) + 1, 1, 5000));
					e.getPlayer().getPackets().setIFText(1265, 67, String.valueOf(e.getPlayer().getTempAttribs().getI("shopAmt", 0)));
				} else if (e.getComponentId() == 214) {
					e.getPlayer().getTempAttribs().setI("shopAmt", Utils.clampI(e.getPlayer().getTempAttribs().getI("shopAmt", 0) - 1, 1, 5000));
					e.getPlayer().getPackets().setIFText(1265, 67, String.valueOf(e.getPlayer().getTempAttribs().getI("shopAmt", 0)));
				} else if (e.getComponentId() == 217) {
					e.getPlayer().getTempAttribs().setI("shopAmt", Utils.clampI(e.getPlayer().getTempAttribs().getI("shopAmt", 0) - 5, 1, 5000));
					e.getPlayer().getPackets().setIFText(1265, 67, String.valueOf(e.getPlayer().getTempAttribs().getI("shopAmt", 0)));
				} else if (e.getComponentId() == 220) {
					e.getPlayer().getTempAttribs().setI("shopAmt", 1);
					e.getPlayer().getPackets().setIFText(1265, 67, String.valueOf(e.getPlayer().getTempAttribs().getI("shopAmt", 0)));
				} else if (e.getComponentId() == 211) {
					if ((slot == -1) || (shop.getMainStock() == null) || (slot > shop.getMainStock().length - 1) || (shop.getMainStock()[slot] == null))
						return;
					if (e.getPlayer().getInventory().getItems().getItems()[slot] == null)
						return;
					e.getPlayer().getTempAttribs().setI("shopAmt", Utils.clampI(isBuying ? shop.getMainStock()[slot].getItem().getAmount() : e.getPlayer().getInventory().getItems().getItems()[slot].getAmount(), 1, 5000));
					e.getPlayer().getPackets().setIFText(1265, 67, String.valueOf(e.getPlayer().getTempAttribs().getI("shopAmt", 0)));
				} else if (e.getComponentId() == 29) {
					e.getPlayer().getVars().setVar(2561, 93);
					e.getPlayer().getTempAttribs().removeB("shop_buying");
					e.getPlayer().getTempAttribs().setI("shopAmt", 1);
				} else if (e.getComponentId() == 28) {
					e.getPlayer().getTempAttribs().setB("shop_buying", true);
					e.getPlayer().getTempAttribs().setI("shopAmt", 1);
					e.getPlayer().getPackets().setIFText(1265, 67, String.valueOf(e.getPlayer().getTempAttribs().getI("shopAmt", 0)));
				}
				shop.sendCustomPrices(e.getPlayer());
			} else if (e.getInterfaceId() == 1266) {
				if (e.getComponentId() == 0)
					if (e.getPacket() == ClientPacket.IF_OP6)
						e.getPlayer().getInventory().sendExamine(e.getSlotId());
					else {
						Shop shop = e.getPlayer().getTempAttribs().getO("Shop");
						if (shop == null)
							return;
						e.getPlayer().getVars().setVar(2563, e.getSlotId());
						if (e.getPacket() == ClientPacket.IF_OP1)
							shop.sendValue(e.getPlayer(), e.getSlotId());
						else if (e.getPacket() == ClientPacket.IF_OP2)
							shop.sell(e.getPlayer(), e.getSlotId(), 1);
						else if (e.getPacket() == ClientPacket.IF_OP3)
							shop.sell(e.getPlayer(), e.getSlotId(), 5);
						else if (e.getPacket() == ClientPacket.IF_OP4)
							shop.sell(e.getPlayer(), e.getSlotId(), 10);
						else if (e.getPacket() == ClientPacket.IF_OP5)
							shop.sell(e.getPlayer(), e.getSlotId(), 50);
					}
			} else if (e.getInterfaceId() == 621)
				if (e.getComponentId() == 0)
					if (e.getPacket() == ClientPacket.IF_OP6)
						e.getPlayer().getInventory().sendExamine(e.getSlotId());
					else {
						Shop shop = e.getPlayer().getTempAttribs().getO("Shop");
						if (shop == null)
							return;
						if (e.getPacket() == ClientPacket.IF_OP1)
							shop.sendValue(e.getPlayer(), e.getSlotId());
						else if (e.getPacket() == ClientPacket.IF_OP2)
							shop.sell(e.getPlayer(), e.getSlotId(), 1);
						else if (e.getPacket() == ClientPacket.IF_OP3)
							shop.sell(e.getPlayer(), e.getSlotId(), 5);
						else if (e.getPacket() == ClientPacket.IF_OP4)
							shop.sell(e.getPlayer(), e.getSlotId(), 10);
						else if (e.getPacket() == ClientPacket.IF_OP5)
							shop.sell(e.getPlayer(), e.getSlotId(), 50);
					}
		}
	};

	public boolean isGeneralStore() {
		return generalStock != null;
	}

	public void addPlayer(final Player player) {
		viewingPlayers.add(player);
		player.getTempAttribs().setO("Shop", this);
		player.setCloseInterfacesEvent(() -> {
			viewingPlayers.remove(player);
			player.getTempAttribs().removeO("Shop");
		});
		player.getVars().setVar(118, MAIN_STOCK_ITEMS_KEY);
		player.getVars().setVar(1496, -1);
		player.getVars().setVar(532, currency);

		player.getVars().setVar(2561, -1);
		player.getVars().setVar(2562, -1);
		player.getVars().setVar(2563, -1);
		//player.getVars().setVar(2565, 1); //changes to sell tab
		player.getVars().syncVarsToClient();
		sendStore(player);
		player.getInterfaceManager().sendInterface(1265);
		player.getPackets().sendVarc(1876, -1);
		player.getPackets().setIFTargetParams(new IFTargetParams(1265, 20, 0, getStoreSize() * 6).enableRightClickOptions(0,1,2,3,4,5,9));
		player.getPackets().setIFTargetParams(new IFTargetParams(1265, 26, 0, getStoreSize() * 6)
				.enableUseOptions(UseFlag.ICOMPONENT)
				.enableRightClickOptions(0,2,3)
				.setDepth(4)
				.enableDrag()
				.enableBit23()
				.enableBit22());
		player.getPackets().setIFText(1265, 85, name);
		if (isGeneralStore())
			player.getPackets().setIFHidden(1265, 52, false);
		sendInventory(player);
		player.getTempAttribs().setB("shop_buying", true);
		WorldTasks.delay(1, () -> sendCustomPrices(player));
	}

	public void sendCustomPrices(Player player) {
		if (!player.getTempAttribs().getB("shop_buying"))
			return;
		for (int i = 0;i < mainStock.length;i++) {
			if (mainStock[i] == null)
				continue;
			//player.getPackets().setIFSprite(1265, 24, i, 592);
			if (mainStock[i].getCustomPrice() > 0)
				player.getPackets().setIFText(1265, 23, i, Utils.kmify(mainStock[i].getCustomPrice()));
		}
	}

	public void sendInventory(Player player) {
		player.getInterfaceManager().sendInventoryInterface(1266);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().setIFRightClickOps(1266, 0, 0, 27, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(1266, 0, 93, 4, 7, "Value", "Sell 1", "Sell 5", "Sell 10", "Sell 50", "Examine");
	}

	// cid 67
	public void buy(Player player, int slotId, int quantity) {
		if (slotId >= mainStock.length && player.isIronMan()) {
			player.sendMessage("You can't buy something another player has sold as an ironman!");
			return;
		}
		if (slotId >= getStoreSize())
			return;
		ShopItem item = slotId >= mainStock.length ? generalStock[slotId - mainStock.length] : mainStock[slotId];
		if (item == null)
			return;
		if (item.getItem().getAmount() == 0) {
			player.sendMessage("There is no stock of that item at the moment.");
			return;
		}
		int price = getBuyPrice(item);
		int amountCoins = player.getInventory().getItems().getNumberOf(currency);
		int maxQuantity = amountCoins / price;
		int buyQ = item.getItem().getAmount() > quantity ? quantity : item.getItem().getAmount();
		boolean enoughCoins = maxQuantity >= buyQ;
		if (!enoughCoins) {
			player.sendMessage("You don't have enough " + ItemDefinitions.getDefs(currency).name.toLowerCase() + ".");
			buyQ = maxQuantity;
		} else if (quantity > buyQ)
			player.sendMessage("The shop has run out of stock.");
		if (item.getItem().getDefinitions().isStackable()) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("Not enough space in your inventory.");
				return;
			}
		} else {
			int freeSlots = player.getInventory().getFreeSlots();
			if (buyQ > freeSlots) {
				buyQ = freeSlots;
				player.sendMessage("Not enough space in your inventory.");
			}
		}
		if (buyQ != 0) {
			int totalPrice = price * buyQ;
			player.getInventory().deleteItem(currency, totalPrice);
			if (item.getItem().getId() == 36 && buyQ == 5)
				player.getInventory().addItem(24170, 1);
			else
				player.getInventory().addItem(item.getItem().getId(), buyQ);
			item.getItem().setAmount(item.getItem().getAmount() - buyQ);
			if (item.getItem().getAmount() <= 0 && slotId >= mainStock.length)
				generalStock[slotId - mainStock.length] = null;
			refreshShop();
			sendInventory(player);
		}
	}

	public void restoreItems() {
		boolean needRefresh = false;
		for (ShopItem element : mainStock) {
			if (element == null)
				continue;
			if (element.tickRestock())
				needRefresh = true;
		}
		if (generalStock != null)
			for (int i = 0; i < generalStock.length; i++) {
				if (generalStock[i] == null)
					continue;
				if (generalStock[i].tickRestock())
					needRefresh = true;
				if (generalStock[i].getItem().getAmount() <= 0)
					generalStock[i] = null;
			}
		if (needRefresh) {
			refreshShop();
			for (Player player : viewingPlayers)
				WorldTasks.delay(0, () -> sendCustomPrices(player));
		}
	}

	private boolean addItem(int itemId, int quantity) {
		for (ShopItem item : mainStock)
			if (item.getItem().getId() == itemId) {
				item.getItem().setAmount(item.getItem().getAmount() + quantity);
				refreshShop();
				return true;
			}
		if (generalStock != null) {
			for (ShopItem item : generalStock) {
				if (item == null)
					continue;
				if (item.getItem().getId() == itemId) {
					item.getItem().setAmount(item.getItem().getAmount() + quantity);
					refreshShop();
					return true;
				}
			}
			for (int i = 0; i < generalStock.length; i++)
				if (generalStock[i] == null) {
					generalStock[i] = new ShopItem(itemId, quantity);
					generalStock[i].setCap(-1);
					refreshShop();
					return true;
				}
		}
		return false;
	}

	public void sell(Player player, int slotId, int quantity) {
		if (buyOnly) {
			player.sendMessage("You cannot sell items to this shop.");
			return;
		}
		if (player.getInventory().getItemsContainerSize() < slotId)
			return;
		Item item = player.getInventory().getItem(slotId);
		if (item == null)
			return;
		int originalId = item.getId();
		if (item.getDefinitions().isNoted())
			item = new Item(item.getDefinitions().getCertId(), item.getAmount());
		if (item.getDefinitions().isDestroyItem() || !ItemConstants.isTradeable(item) || item.getId() == currency) {
			player.sendMessage("You can't sell this item.");
			return;
		}
		int dq = getDefaultQuantity(item.getId());
		if (dq == -1 && generalStock == null) {
			player.sendMessage("You can't sell this item to this shop.");
			return;
		}
		int numberOff = player.getInventory().getItems().getNumberOf(originalId);
		if (quantity > numberOff)
			quantity = numberOff;
		if (!addItem(item.getId(), quantity)) {
			player.sendMessage("Shop is currently full.");
			return;
		}
		if ((player.getInventory().getNumberOf(currency) + (getSellPrice(item)*quantity)) < 0 || (player.getInventory().getNumberOf(currency)+(getSellPrice(item)*quantity)) > Integer.MAX_VALUE)
			player.sendMessage("Looks like we need an eco reset..");
		else {
			player.getInventory().deleteItem(originalId, quantity);
			player.getInventory().addItem(currency, getSellPrice(item) * quantity);
		}
	}

	public void sendValue(Player player, int slotId) {
		if (player.getInventory().getItemsContainerSize() < slotId)
			return;
		Item item = player.getInventory().getItem(slotId);
		if (item == null)
			return;
		if (item.getDefinitions().isNoted())
			item = new Item(item.getDefinitions().getCertId(), item.getAmount());
		if (item.getDefinitions().isNoted() || !ItemConstants.isTradeable(item) || item.getId() == currency) {
			player.sendMessage("You can't sell this item.");
			return;
		}
		int dq = getDefaultQuantity(item.getId());
		if (dq == -1 && generalStock == null) {
			player.sendMessage("You can't sell this item to this shop.");
			return;
		}
		player.sendMessage(item.getDefinitions().getName() + ": shop will buy for: " + getSellPrice(item) + " " + ItemDefinitions.getDefs(currency).getName().toLowerCase() + ". Right-click the item to sell.");
	}

	public int getDefaultQuantity(int itemId) {
		for (int i = 0; i < mainStock.length; i++)
			if (mainStock[i].getItem().getId() == itemId)
				return defaultQuantity[i];
		return -1;
	}

	public void sendInfo(Player player, int slotId, boolean isBuying) {
		if (slotId >= getStoreSize())
			return;
		if (isBuying) {
			ShopItem item = slotId >= mainStock.length ? generalStock[slotId - mainStock.length] : mainStock[slotId];
			if (item == null)
				return;
			int price = getBuyPrice(item);
			player.sendMessage(item.getItem().getDefinitions().getName() + ": shop will " + (isBuying ? "sell" : "buy") + " for " + price + " " + ItemDefinitions.getDefs(currency).getName().toLowerCase() + ".");
		} else {
			Item[] stock = player.getInventory().getItems().getItems();
			if (slotId >= stock.length)
				return;
			Item item = stock[slotId];
			if (item == null)
				return;
			int price = getSellPrice(item);
			player.sendMessage(item.getDefinitions().getName() + ": shop will " + (isBuying ? "sell" : "buy") + " for " + price + " " + ItemDefinitions.getDefs(currency).getName().toLowerCase() + ".");
		}
	}

	public int getSellPrice(Item item) {
		int price = item.getDefinitions().getSellPrice();
		if (price == 0)
			price = 1;
		if (currency == 6529) {
			EnumDefinitions defs = EnumDefinitions.getEnum(732);
			if (defs.getIntValue(item.getId()) != -1)
				return defs.getIntValue(item.getId());
		} else if (currency == 995) {
			EnumDefinitions defs = EnumDefinitions.getEnum(1441);
			if (defs.getIntValue(item.getId()) != -1)
				return defs.getIntValue(item.getId());
		}
		return price;
	}

	public int getBuyPrice(ShopItem item) {
		if (item.getCustomPrice() > 0)
			return item.getCustomPrice();
		int price = item.getItem().getDefinitions().getValue();
		if (price == 0)
			price = 1;
		if (currency == 6529) {
			EnumDefinitions defs = EnumDefinitions.getEnum(731);
			if (defs.getIntValue(item.getItem().getId()) != -1)
				return defs.getIntValue(item.getItem().getId());
			return (int) (price * 3.0 / 2.0);
		}
		if (currency == 995) {
			EnumDefinitions defs = EnumDefinitions.getEnum(733);
			if (defs.getIntValue(item.getItem().getId()) != -1)
				return defs.getIntValue(item.getItem().getId());
		}
		return price;
	}

	public void sendExamine(Player player, int slotId) {
		if (slotId >= getStoreSize())
			return;
		ShopItem item = slotId >= mainStock.length ? generalStock[slotId - mainStock.length] : mainStock[slotId];
		if (item == null)
			return;
		player.sendMessage(ItemExamines.getExamine(item.getItem()));
	}

	public void refreshShop() {
		for (Player player : viewingPlayers) {
			sendStore(player);
			player.getPackets().setIFTargetParams(new IFTargetParams(620, 25, 0, getStoreSize() * 6).enableRightClickOptions(0,1,2,3,4,5,9));
			sendCustomPrices(player);
		}
	}

	public int getStoreSize() {
		return mainStock.length + (generalStock != null ? generalStock.length : 0);
	}

	public void sendStore(Player player) {
		Item[] stock = new Item[mainStock.length + (generalStock != null ? generalStock.length : 0)];
		for (int i = 0;i < stock.length;i++)
			if (i >= mainStock.length) {
				ShopItem item = generalStock[i - mainStock.length];
				if (item == null)
					continue;
				stock[i] = item.getItem();
			} else {
				ShopItem item = mainStock[i];
				if (item == null)
					continue;
				stock[i] = item.getItem();
			}
		player.getPackets().sendItems(MAIN_STOCK_ITEMS_KEY, stock);
	}

	//	public void sendSellStore(Player player, Item[] inventory) {
	//		Item[] stock = new Item[inventory.length + (generalStock != null ? generalStock.length : 0)];
	//		System.arraycopy(inventory, 0, stock, 0, inventory.length);
	//		if (generalStock != null)
	//			System.arraycopy(generalStock, 0, stock, inventory.length, generalStock.length);
	//		player.getPackets().sendItems(MAIN_STOCK_ITEMS_KEY, stock);
	//	}

	/**
	 * Checks if the player is buying an item or selling it.
	 *
	 * @param player
	 *            The player
	 * @param slotId
	 *            The slot id
	 * @param amount
	 *            The amount
	 */
	public void handleShop(Player player, int slotId, int amount) {
		if (player.getTempAttribs().getB("shop_buying"))
			buy(player, slotId, amount);
		else
			sell(player, slotId, amount);
	}

	public ShopItem[] getMainStock() {
		return mainStock;
	}

	public boolean isBuyOnly() {
		return buyOnly;
	}
}