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
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.model.entity.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rs.Settings;
import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.cache.loaders.interfaces.IFEvents.UseFlag;
import com.rs.game.content.holidayevents.easter.easter22.Easter2022;
import com.rs.game.content.skills.runecrafting.Runecrafting;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.lib.game.Item;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.ServerPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.ItemConfig;

@PluginEventHandler
public class Bank {

	private static final long PIN_VALIDITY_TIME = 6 * 60 * 60 * 1000; //6 hours

	private Item[][] bankTabs;
	private short bankPin;
	private int lastX;

	private transient Player player;
	private transient int currentTab;
	private transient Item[] lastContainerCopy;
	private transient boolean withdrawNotes;
	private transient boolean insertItems;
	private transient boolean sessionPin;
	private Map<String, Long> enteredPin;

	public static final int MAX_BANK_SIZE = 800;

	public Bank() {
		bankTabs = new Item[1][0];
	}

	public static ButtonClickHandler handleDepositBox = new ButtonClickHandler(11) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 17) {
				if (e.getPacket() == ClientPacket.IF_OP1)
					e.getPlayer().getBank().depositItem(e.getSlotId(), 1, false);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().getBank().depositItem(e.getSlotId(), 5, false);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					e.getPlayer().getBank().depositItem(e.getSlotId(), 10, false);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().getBank().depositItem(e.getSlotId(), Integer.MAX_VALUE, false);
				else if (e.getPacket() == ClientPacket.IF_OP5)
					e.getPlayer().sendInputInteger("How many would you like to deposit?", (integer) -> e.getPlayer().getBank().depositItem(e.getSlotId(), integer, true));
				else if (e.getPacket() == ClientPacket.IF_OP6)
					e.getPlayer().getInventory().sendExamine(e.getSlotId());
			} else if (e.getComponentId() == 18)
				e.getPlayer().getBank().depositAllInventory(false);
			else if (e.getComponentId() == 22)
				e.getPlayer().getBank().depositAllEquipment(false);
		}
	};

	public static ButtonClickHandler handleInvButtons = new ButtonClickHandler(762) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getTempAttribs().getB("viewingOtherBank"))
				return;
			if (e.getComponentId() == 15)
				e.getPlayer().getBank().switchInsertItems();
			else if (e.getComponentId() == 19)
				e.getPlayer().getBank().switchWithdrawNotes();
			else if (e.getComponentId() == 33)
				e.getPlayer().getBank().depositAllInventory(true);
			else if (e.getComponentId() == 37)
				e.getPlayer().getBank().depositAllEquipment(true);
			else if (e.getComponentId() == 39)
				e.getPlayer().getBank().depositAllBob(true);
			else if (e.getComponentId() == 35)
				e.getPlayer().sendMessage("Coin pouch is disabled.");
			else if (e.getComponentId() == 46) {
				e.getPlayer().closeInterfaces();
				e.getPlayer().getInterfaceManager().sendInterface(767);
				e.getPlayer().setCloseInterfacesEvent(() -> e.getPlayer().getBank().open());
			} else if (e.getComponentId() >= 46 && e.getComponentId() <= 64) {
				int tabId = 9 - ((e.getComponentId() - 46) / 2);
				if (e.getPacket() == ClientPacket.IF_OP1)
					e.getPlayer().getBank().setCurrentTab(tabId);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().getBank().collapse(tabId);
			} else if (e.getComponentId() == 95) {
				if (e.getPacket() == ClientPacket.IF_OP1)
					e.getPlayer().getBank().withdrawItem(e.getSlotId(), 1);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().getBank().withdrawItem(e.getSlotId(), 5);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					e.getPlayer().getBank().withdrawItem(e.getSlotId(), 10);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().getBank().withdrawLastAmount(e.getSlotId());
				else if (e.getPacket() == ClientPacket.IF_OP5)
					e.getPlayer().sendInputInteger("How many would you like to withdraw?", amount -> {
						if (amount < 0)
							return;
						e.getPlayer().getBank().setLastX(amount);
						e.getPlayer().getBank().refreshLastX();
						e.getPlayer().getBank().withdrawItem(e.getSlotId(), amount);
					});
				else if (e.getPacket() == ClientPacket.IF_OP6)
					e.getPlayer().getBank().withdrawItem(e.getSlotId(), Integer.MAX_VALUE);
				else if (e.getPacket() == ClientPacket.IF_OP7)
					e.getPlayer().getBank().withdrawItemButOne(e.getSlotId());
				else if (e.getPacket() == ClientPacket.IF_OP10)
					e.getPlayer().getBank().sendExamine(e.getSlotId());

			} else if (e.getComponentId() == 119)
				Equipment.openEquipmentBonuses(e.getPlayer(), true);
		}
	};

	public static ButtonClickHandler handleBankButtons = new ButtonClickHandler(763) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getTempAttribs().getB("viewingOtherBank"))
				return;
			if (e.getComponentId() == 0)
				if (e.getPacket() == ClientPacket.IF_OP1)
					e.getPlayer().getBank().depositItem(e.getSlotId(), 1, true);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().getBank().depositItem(e.getSlotId(), 5, true);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					e.getPlayer().getBank().depositItem(e.getSlotId(), 10, true);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().getBank().depositLastAmount(e.getSlotId());
				else if (e.getPacket() == ClientPacket.IF_OP5)
					e.getPlayer().sendInputInteger("How many would you like to deposit?", amount -> {
						if (amount < 0)
							return;
						e.getPlayer().getBank().setLastX(amount);
						e.getPlayer().getBank().refreshLastX();
						e.getPlayer().getBank().depositItem(e.getSlotId(), amount, e.getPlayer().getInterfaceManager().topOpen(11) ? false : true);
					});
				else if (e.getPacket() == ClientPacket.IF_OP6)
					e.getPlayer().getBank().depositItem(e.getSlotId(), Integer.MAX_VALUE, true);
				else if (e.getPacket() == ClientPacket.IF_OP10)
					e.getPlayer().getInventory().sendExamine(e.getSlotId());
		}
	};

	public void removeItem(int id) {
		if (bankTabs != null)
			for (int i = 0; i < bankTabs.length; i++)
				for (int i2 = 0; i2 < bankTabs[i].length; i2++)
					if (bankTabs[i][i2].getId() == id)
						removeItem(new int[] { i, i2 }, Integer.MAX_VALUE, true, false);
	}

	public void deleteItem(int itemId, int amount) {
		if (bankTabs != null)
			for (Item[] bankTab : bankTabs)
				for (Item element : bankTab)
					if (element.getId() == itemId) {
						element.setAmount(element.getAmount() - amount);
						refreshItems();
					}
	}

	public boolean containsItem(int itemId, int amount) {
		if (bankTabs != null)
			for (Item[] bankTab : bankTabs)
				for (Item element : bankTab)
					if (element.getId() == itemId)
						if (element.getAmount() >= amount)
							return true;
		return false;
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (bankTabs == null || bankTabs.length == 0)
			bankTabs = new Item[1][0];
	}

	@SuppressWarnings("null")
	public void setItem(int slotId, int amt) {
		Item item = getItem(slotId);
		if (item == null) {
			item.setAmount(amt);
			refreshItems();
			refreshTabs();
			refreshViewingTab();
		}
	}

	public void refreshTabs(Player player) {
		for (int slot = 1; slot < 9; slot++)
			refreshTab(player, slot);
	}

	public void refreshTabs() {
		for (int slot = 1; slot < 9; slot++)
			refreshTab(slot);
	}

	public int getTabSize(int slot) {
		if (slot >= bankTabs.length)
			return 0;
		return bankTabs[slot].length;
	}

	public int getTabSize(Player other, int slot) {
		if (slot >= other.getBank().getBankTabs().length)
			return 0;
		return other.getBank().getBankTabs()[slot].length;
	}

	public Item[][] getBankTabs() {
		return bankTabs;
	}

	public void withdrawLastAmount(int bankSlot) {
		withdrawItem(bankSlot, lastX);
	}

	public void withdrawItemButOne(int fakeSlot) {
		int[] fromRealSlot = getRealSlot(fakeSlot);
		Item item = getItem(fromRealSlot);
		if (item == null)
			return;
		if (item.getAmount() <= 1) {
			player.sendMessage("You only have one of this item in your bank");
			return;
		}
		withdrawItem(fakeSlot, item.getAmount() - 1);
	}

	public void depositLastAmount(int bankSlot) {
		depositItem(bankSlot, lastX, true);
	}

	public void depositAllInventory(boolean banking) {
		if (player.getTempAttribs().getB("viewingOtherBank"))
			return;
		if (Bank.MAX_BANK_SIZE - getBankSize() < player.getInventory().getItems().getSize()) {
			player.sendMessage("Not enough space in your bank.");
			return;
		}
		for (int i = 0; i < 28; i++) {
			depositItem(i, Integer.MAX_VALUE, true);
			refreshTab(currentTab);
			refreshItems();
		}
	}

	public void depositAllBob(boolean banking) {
		if (player.getTempAttribs().getB("viewingOtherBank"))
			return;
		Familiar familiar = player.getFamiliar();
		if (familiar == null || familiar.getInventory() == null)
			return;
		int space = addItems(familiar.getInventory().array(), banking);
		if (space != 0) {
			for (int i = 0; i < space; i++)
				familiar.getInventory().set(i, null);
		}
		if (space < familiar.getInventory().getSize()) {
			player.sendMessage("Not enough space in your bank.");
			return;
		}
	}

	public void depositAllEquipment(boolean banking) {
		if (player.getTempAttribs().getB("viewingOtherBank"))
			return;
		for (int i = 0;i < Equipment.SIZE;i++) {
			Item prev = player.getEquipment().setSlot(i, null);
			if (prev == null || prev.getId() == -1)
				continue;
			if (addItems(new Item[] { prev }, banking) <= 0) {
				player.sendMessage("Not enough space in your bank.");
				break;
			}
		}
		player.getAppearance().generateAppearanceData();
	}

	public void collapse(int tabId) {
		if (tabId == 0 || tabId >= bankTabs.length)
			return;
		Item[] items = bankTabs[tabId];
		for (Item item : items)
			removeItem(getItemSlot(item.getId()), item.getAmount(), false, true);
		for (Item item : items)
			addItem(item, 0, false);
		refreshTabs();
		refreshItems();
	}

	public void switchItem(int fromSlot, int toSlot, int fromComponentId, int toComponentId) {
		if (toSlot == -1) {
			int toTab = toComponentId >= 76 ? 8 - (84 - toComponentId) : 9 - ((toComponentId - 46) / 2);
			if (toTab < 0 || toTab > 9)
				return;
			if (bankTabs.length == toTab) {
				int[] fromRealSlot = getRealSlot(fromSlot);
				if (fromRealSlot == null)
					return;
				if (toTab == fromRealSlot[0]) {
					switchItem(fromSlot, getStartSlot(toTab));
					return;
				}
				Item item = getItem(fromRealSlot);
				if (item == null)
					return;
				removeItem(fromSlot, item.getAmount(), false, true);
				createTab();
				bankTabs[bankTabs.length - 1] = new Item[] { item };
				refreshTab(fromRealSlot[0]);
				refreshTab(toTab);
				refreshItems();
			} else if (bankTabs.length > toTab) {
				int[] fromRealSlot = getRealSlot(fromSlot);
				if (fromRealSlot == null)
					return;
				if (toTab == fromRealSlot[0]) {
					switchItem(fromSlot, getStartSlot(toTab));
					return;
				}
				Item item = getItem(fromRealSlot);
				if (item == null)
					return;
				boolean removed = removeItem(fromSlot, item.getAmount(), false, true);
				if (!removed)
					refreshTab(fromRealSlot[0]);
				else if (fromRealSlot[0] != 0 && toTab >= fromRealSlot[0])
					toTab -= 1;
				refreshTab(fromRealSlot[0]);
				addItem(item, toTab, true);
			}
		} else if (!insertItems)
			switchItem(fromSlot, toSlot);
		else
			insert(fromSlot, toSlot);
	}

	public void insert(int fromSlot, int toSlot) {
		int[] fromRealSlot = getRealSlot(fromSlot);
		Item fromItem = getItem(fromRealSlot);
		if (fromItem == null)
			return;

		int[] toRealSlot = getRealSlot(toSlot);
		Item toItem = getItem(toRealSlot);
		if ((toItem == null) || (toRealSlot[0] != fromRealSlot[0]))
			return;

		if (toRealSlot[1] > fromRealSlot[1])
			for (int slot = fromRealSlot[1]; slot < toRealSlot[1]; slot++) {
				Item temp = bankTabs[toRealSlot[0]][slot];
				bankTabs[toRealSlot[0]][slot] = bankTabs[toRealSlot[0]][slot + 1];
				bankTabs[toRealSlot[0]][slot + 1] = temp;
			}
		else if (fromRealSlot[1] > toRealSlot[1])
			for (int slot = fromRealSlot[1]; slot > toRealSlot[1]; slot--) {
				Item temp = bankTabs[toRealSlot[0]][slot];
				bankTabs[toRealSlot[0]][slot] = bankTabs[toRealSlot[0]][slot - 1];
				bankTabs[toRealSlot[0]][slot - 1] = temp;
			}
		refreshItems();
	}

	public void switchItem(int fromSlot, int toSlot) {
		int[] fromRealSlot = getRealSlot(fromSlot);
		Item fromItem = getItem(fromRealSlot);
		if (fromItem == null)
			return;
		int[] toRealSlot = getRealSlot(toSlot);
		Item toItem = getItem(toRealSlot);
		if (toItem == null)
			return;
		bankTabs[fromRealSlot[0]][fromRealSlot[1]] = toItem;
		bankTabs[toRealSlot[0]][toRealSlot[1]] = fromItem;
		refreshTab(fromRealSlot[0]);
		if (fromRealSlot[0] != toRealSlot[0])
			refreshTab(toRealSlot[0]);
		refreshItems();
	}

	public void openDepositBox() {
		player.getTempAttribs().setB("viewingOtherBank", false);
		player.getTempAttribs().setB("viewingDepositBox", true);
		player.getInterfaceManager().sendInterface(11);
		player.getInterfaceManager().removeSubs(Sub.TAB_INVENTORY, Sub.TAB_EQUIPMENT);
		player.getInterfaceManager().openTab(Sub.TAB_FRIENDS);
		sendBoxInterItems();
		player.getPackets().setIFText(11, 13, "Bank Of " + Settings.getConfig().getServerName() + " - Deposit Box");
		player.setCloseInterfacesEvent(() -> {
			player.getSession().writeToQueue(ServerPacket.TRIGGER_ONDIALOGABORT);
			player.getInterfaceManager().sendSubDefaults(Sub.TAB_INVENTORY, Sub.TAB_EQUIPMENT);
			player.getInterfaceManager().openTab(Sub.TAB_INVENTORY);
			player.getTempAttribs().setB("viewingDepositBox", false);
			Familiar.sendLeftClickOption(player);
		});
	}

	public void sendBoxInterItems() {
		player.getPackets().sendInterSetItemsOptionsScript(11, 17, 93, 6, 5, "Deposit-1", "Deposit-5", "Deposit-10", "Deposit-All", "Deposit-X", "Examine");
		player.getPackets().setIFRightClickOps(11, 17, 0, 27, 0, 1, 2, 3, 4, 5);
	}

	public void setPin(byte num1, byte num2, byte num3, byte num4) {
		bankPin = (short) ((num1 << 12) + (num2 << 8) + (num3 << 4) + num4);
	}

	public boolean pinCorrect(byte num1, byte num2, byte num3, byte num4) {
		return bankPin == (short) ((num1 << 12) + (num2 << 8) + (num3 << 4) + num4);
	}

	public void openPinSettings() {
		if (!checkPin())
			return;
		player.endConversation();
		player.getTempAttribs().setB("settingPin", false);
		player.getTempAttribs().setB("cancellingPin", false);
		player.getTempAttribs().setB("changingPin", false);
		player.getPackets().sendVarc(98, bankPin == 0 ? 0 : 1);
		player.getPackets().sendVarcString(344, "Please set a secure pin.<br>0, 0, 0, 0 will be considered as no pin.");
		player.getInterfaceManager().sendInterface(14);
	}

	public void sendPinConfirmScreen(String question, String op1, String op2) {
		player.getPackets().sendVarc(98, -1);
		player.getPackets().setIFText(14, 32, question);
		player.getPackets().setIFText(14, 34, op1);
		player.getPackets().setIFText(14, 36, op2);
		player.getInterfaceManager().sendInterface(14);
	}

	public void confirmSetPin() {
		player.getTempAttribs().setB("settingPin", true);
		sendPinConfirmScreen("Do you really wish to set a PIN on your bank account?", "Yes, I really want a bank PIN. I will never forget it!", "No, I might forget it!");
	}

	public void confirmCancelPin() {
		player.getTempAttribs().setB("cancellingPin", true);
		sendPinConfirmScreen("Do you still want your PIN?", "Yes, I asked to cancel my PIN.", "No, I didn't ask to cancel my PIN.");
	}

	public void confirmChangePin() {
		player.getTempAttribs().setB("changingPin", true);
		sendPinConfirmScreen("Do you really want to change your PIN?", "Yes, I asked to change my PIN.", "No, I didn't ask to change my PIN.");
	}

	public void openPin() {
		player.getTempAttribs().setI("pinStage", 0);
		player.getTempAttribs().setI("enteredPin", 0);
		player.getTempAttribs().setI("prevPin", -1);
		player.getPackets().setIFText(13, 27, "Bank of " + Settings.getConfig().getServerName());
		player.getInterfaceManager().sendInterface(13);
		player.getInterfaceManager().sendSubSpecific(false, 13, 5, 759);
		player.getPackets().sendVarc(98, bankPin == 0 ? 0 : 1);
		player.getVars().setVarBit(1010, 1, true);
		player.getVars().syncVarsToClient();
		player.getVars().setVarBit(1010, player.getTempAttribs().getI("pinStage"), true);
		player.getVars().syncVarsToClient();
		player.getPackets().sendRunScriptBlank(1107);
	}

	public static ButtonClickHandler handlePin = new ButtonClickHandler(13, 14, 759) {
		@Override
		public void handle(ButtonClickEvent e) {
			switch(e.getInterfaceId()) {
			case 14:
				switch(e.getComponentId()) {
				case 18:
					if (e.getPlayer().getBank().bankPin == 0)
						e.getPlayer().getBank().confirmSetPin();
					else
						e.getPlayer().getBank().confirmChangePin();
					break;
				case 33:
					if (e.getPlayer().getBank().bankPin == 0)
						e.getPlayer().getBank().openPin();
					else if (e.getPlayer().getTempAttribs().getB("changingPin")) {
						e.getPlayer().getBank().bankPin = 0;
						e.getPlayer().sendMessage("Your pin has been removed. Please set a new one.");
						e.getPlayer().getBank().openPin();
					} else {
						e.getPlayer().getBank().bankPin = 0;
						e.getPlayer().sendMessage("Your pin has been removed.");
					}
					break;
				case 19:
					e.getPlayer().getBank().confirmCancelPin();
					break;
				case 35:
					e.getPlayer().getBank().openPinSettings();
					break;
				default:
					e.getPlayer().getBank().openPinSettings();
					break;
				}
				break;
			case 13:
				break;
			case 759:
				int num = (e.getComponentId() / 4) - 1;
				e.getPlayer().getTempAttribs().setI("enteredPin", e.getPlayer().getTempAttribs().getI("enteredPin") + (num << (e.getPlayer().getTempAttribs().getI("pinStage") * 4)));
				e.getPlayer().getTempAttribs().incI("pinStage");
				if (e.getPlayer().getTempAttribs().getI("pinStage") == 3)
					for (Object ifComp : EnumDefinitions.getEnum(3554).getValues().values())
						e.getPlayer().getPackets().setIFHidden(Utils.interfaceIdFromHash((int) ifComp), Utils.componentIdFromHash((int) ifComp), false);
				else if (e.getPlayer().getTempAttribs().getI("pinStage") == 4) {
					e.getPlayer().getVars().setVarBit(1010, 4, true);
					e.getPlayer().getVars().syncVarsToClient();
					if (e.getPlayer().getBank().bankPin == 0) {
						if (e.getPlayer().getTempAttribs().getI("prevPin", -1) == -1) {
							e.getPlayer().getPackets().setIFText(13, 26, "Please repeat that PIN again.");
							int prev = e.getPlayer().getTempAttribs().getI("enteredPin");
							e.getPlayer().getBank().openPin();
							e.getPlayer().getTempAttribs().setI("prevPin", prev);
							e.getPlayer().getTempAttribs().setI("enteredPin", 0);
							e.getPlayer().getTempAttribs().setI("pinStage", 0);
							e.getPlayer().getVars().setVarBit(1010, 0, true);
							e.getPlayer().getVars().syncVarsToClient();
						} else if (e.getPlayer().getTempAttribs().getI("prevPin") != e.getPlayer().getTempAttribs().getI("enteredPin")) {
							e.getPlayer().sendMessage("The PIN you entered did not match the first one.");
							e.getPlayer().closeInterfaces();
							e.getPlayer().getBank().openPinSettings();
						} else {
							e.getPlayer().getBank().bankPin = (short) e.getPlayer().getTempAttribs().getI("prevPin");
							e.getPlayer().sendMessage("Your PIN has been set.");
							e.getPlayer().closeInterfaces();
						}
					} else {
						if (e.getPlayer().getTempAttribs().getI("enteredPin") != e.getPlayer().getBank().bankPin)
							e.getPlayer().sendMessage("That PIN was incorrect.");
						else {
							e.getPlayer().sendMessage("You have entered your PIN successfully.");
							e.getPlayer().getBank().setEnteredPIN();
						}
						e.getPlayer().closeInterfaces();
					}
				}
				break;
			}
		}
	};

	public void open() {
		if (!checkPin())
			return;
		player.getTempAttribs().removeB("viewingOtherBank");
		player.getVars().setVar(638, 0);
		player.getVars().setVarBit(8348, 0);
		refreshTabs();
		refreshViewingTab();
		refreshLastX();
		refreshTab(currentTab);
		player.getVars().syncVarsToClient();
		player.getInterfaceManager().sendInterface(762);
		player.getInterfaceManager().sendInventoryInterface(763);
		player.getPackets().sendRunScript(2319);
		player.getPackets().setIFText(762, 47, "Bank of " + Settings.getConfig().getServerName());
		unlockButtons();
		sendItems();
		refreshItems();
		player.setCloseInterfacesEvent(() -> {
			player.getSession().writeToQueue(ServerPacket.TRIGGER_ONDIALOGABORT);
			Familiar.sendLeftClickOption(player);
		});
	}

	public void openBankOther(Player other) {
		player.getVars().setVarBit(8348, 0);
		player.getVars().syncVarsToClient();
		player.getInterfaceManager().sendInterface(762);
		player.getPackets().sendRunScript(2319);
		refreshTabs(other);
		sendItemsOther(other);
		unlockButtons();
		player.getPackets().sendItems(93, other.getInventory().getItems());
		player.getPackets().sendItems(94, other.getEquipment().getItemsCopy());
		player.getTempAttribs().setB("viewingOtherBank", true);
		player.setCloseInterfacesEvent(() -> {
			player.getInventory().refresh();
			player.getEquipment().refresh();
			Familiar.sendLeftClickOption(player);
		});
	}

	public void refreshLastX() {
		player.getVars().setVar(1249, lastX);
	}

	public void createTab() {
		int slot = bankTabs.length;
		Item[][] tabs = new Item[slot + 1][];
		System.arraycopy(bankTabs, 0, tabs, 0, slot);
		tabs[slot] = new Item[0];
		bankTabs = tabs;
	}

	public void destroyTab(int slot) {
		Item[][] tabs = new Item[bankTabs.length - 1][];
		System.arraycopy(bankTabs, 0, tabs, 0, slot);
		System.arraycopy(bankTabs, slot + 1, tabs, slot, bankTabs.length - slot - 1);
		bankTabs = tabs;
		if (currentTab != 0 && currentTab >= slot)
			currentTab--;
	}

	public boolean hasBankSpace() {
		return getBankSize() < MAX_BANK_SIZE;
	}

	public void withdrawItem(int bankSlot, int quantity) {
		if (player.getTempAttribs().getB("viewingOtherBank") || (quantity < 1))
			return;
		Item item = getItem(getRealSlot(bankSlot));
		if (item == null)
			return;
		if (item.getMetaData() != null)
			quantity = 1;
		if (item.getAmount() < quantity)
			item = new Item(item.getId(), item.getAmount(), item.getMetaData());
		else
			item = new Item(item.getId(), quantity, item.getMetaData());
		boolean noted = false;
		ItemDefinitions defs = item.getDefinitions();
		if (withdrawNotes)
			if (!defs.isNoted() && defs.getCertId() != -1 && item.getMetaData() == null) {
				item.setId(defs.getCertId());
				noted = true;
			} else
				player.sendMessage("You cannot withdraw this item as a note.");
		if ((noted || defs.isStackable()) && item.getMetaData() == null) {
			if (player.getInventory().getItems().containsOne(item)) {
				int slot = player.getInventory().getItems().getThisItemSlot(item);
				Item invItem = player.getInventory().getItems().get(slot);
				if (invItem.getAmount() + item.getAmount() <= 0) {
					item.setAmount(Integer.MAX_VALUE - invItem.getAmount());
					player.sendMessage("Not enough space in your inventory.");
					return;
				}
			} else if (!player.getInventory().hasFreeSlots()) {
				player.sendMessage("Not enough space in your inventory.");
				return;
			}
		} else {
			int freeSlots = player.getInventory().getFreeSlots();
			if (freeSlots == 0) {
				player.sendMessage("Not enough space in your inventory.");
				return;
			}
			if (freeSlots < item.getAmount()) {
				item.setAmount(freeSlots);
				player.sendMessage("Not enough space in your inventory.");
			}
		}
		removeItem(bankSlot, item.getAmount(), true, false);
		player.getInventory().addItem(item);
		if (item.getId() == Runecrafting.RUNE_ESS || item.getId() == Runecrafting.PURE_ESS)
			Runecrafting.fillPouchesFromBank(player, item.getId());
	}

	public void sendExamine(int fakeSlot) {
		int[] slot = getRealSlot(fakeSlot);
		if (slot == null)
			return;
		Item item = bankTabs[slot[0]][slot[1]];
		player.sendMessage(ItemConfig.get(item.getId()).getExamine(item));
		if (item.getMetaData("combatCharges") != null)
			player.sendMessage("<col=FF0000>It looks like it will last another " + Utils.ticksToTime(item.getMetaDataI("combatCharges")));
	}

	public void depositItem(int invSlot, int quantity, boolean refresh) {
		if (player.getTempAttribs().getB("viewingOtherBank") || quantity < 1 || invSlot < 0 || invSlot > 27)
			return;
		Item item = player.getInventory().getItem(invSlot);
		if (item == null)
			return;
		if (item.getMetaData() != null)
			quantity = 1;
		int amt = player.getInventory().getItems().getNumberOf(item);
		if (amt < quantity)
			item = new Item(item.getId(), amt, item.getMetaData());
		else
			item = new Item(item.getId(), quantity, item.getMetaData());
		ItemDefinitions defs = item.getDefinitions();
		int originalId = item.getId();
		if (defs.isNoted() && defs.getCertId() != -1)
			item.setId(defs.getCertId());
		Item bankedItem = getItem(item.getId());
		if (bankedItem != null) {
			if (bankedItem.getAmount() + item.getAmount() <= 0) {
				item.setAmount(Integer.MAX_VALUE - bankedItem.getAmount());
				player.sendMessage("Not enough space in your bank.");
			}
		} else if (!hasBankSpace()) {
			player.sendMessage("Not enough space in your bank.");
			return;
		}
		player.getInventory().deleteItem(invSlot, new Item(originalId, item.getAmount(), item.getMetaData()));
		addItem(item, refresh);
	}

	//	public void addItem(Item item, boolean refresh) {
	//		addItem(item.getId(), item.getAmount(), refresh);
	//	}

	public int addItems(Item[] items, boolean refresh) {
		int space = MAX_BANK_SIZE - getBankSize();
		if (space != 0) {
			space = (space < items.length ? space : items.length);
			for (int i = 0; i < space; i++) {
				if (items[i] == null)
					continue;
				if (items[i].getId() == Easter2022.EGGSTERMINATOR) {
					player.sendMessage("The banker drops the Eggsterminator as you hand it to them. You can obtain a new one from the Easter event, completing three hunts will unlock a more sturdy enchanted version.");
					continue;
				}
				if (items[i].getDefinitions().isNoted() && items[i].getDefinitions().getCertId() != -1)
					items[i].setId(items[i].getDefinitions().getCertId());
				addItem(items[i], false);
			}
			if (refresh) {
				refreshTabs();
				refreshItems();
			}
		}
		return space;
	}

	public void addItem(Item item, boolean refresh) {
		addItem(item, currentTab, refresh);
	}

	public void addItem(Item item, int creationTab, boolean refresh) {
		if (item.getId() == -1)
			return;
		int[] slotInfo = getItemSlot(item.getId());
		if (slotInfo == null || item.getMetaData() != null || bankTabs[slotInfo[0]][slotInfo[1]].getMetaData() != null) {
			if (creationTab >= bankTabs.length)
				creationTab = bankTabs.length - 1;
			if (creationTab < 0) // fixed now, alex
				creationTab = 0;
			int slot = bankTabs[creationTab].length;
			Item[] tab = new Item[slot + 1];
			System.arraycopy(bankTabs[creationTab], 0, tab, 0, slot);
			tab[slot] = item;
			bankTabs[creationTab] = tab;
			if (refresh)
				refreshTab(creationTab);
		} else {
			Item existingItem = bankTabs[slotInfo[0]][slotInfo[1]];
			bankTabs[slotInfo[0]][slotInfo[1]] = new Item(item.getId(), existingItem.getAmount() + item.getAmount(), item.getMetaData());
		}
		if (refresh)
			refreshItems();
	}

	public boolean removeItem(int fakeSlot, int quantity, boolean refresh, boolean forceDestroy) {
		return removeItem(getRealSlot(fakeSlot), quantity, refresh, forceDestroy);
	}

	public boolean removeItem(int[] slot, int quantity, boolean refresh, boolean forceDestroy) {
		if (slot == null)
			return false;
		Item item = bankTabs[slot[0]][slot[1]];
		boolean destroyed = false;
		if (quantity >= item.getAmount()) {
			if (bankTabs[slot[0]].length == 1 && (forceDestroy || bankTabs.length != 1)) {
				destroyTab(slot[0]);
				if (refresh)
					refreshTabs();
				destroyed = true;
			} else {
				Item[] tab = new Item[bankTabs[slot[0]].length - 1];
				System.arraycopy(bankTabs[slot[0]], 0, tab, 0, slot[1]);
				System.arraycopy(bankTabs[slot[0]], slot[1] + 1, tab, slot[1], bankTabs[slot[0]].length - slot[1] - 1);
				bankTabs[slot[0]] = tab;
				if (refresh)
					refreshTab(slot[0]);
			}
		} else
			bankTabs[slot[0]][slot[1]] = new Item(item.getId(), item.getAmount() - quantity, item.getMetaData());
		if (refresh)
			refreshItems();
		return destroyed;
	}

	public int[] getSlot(int itemId) {
		if (bankTabs != null)
			for (int i = 0; i < bankTabs.length; i++)
				for (int i2 = 0; i2 < bankTabs[i].length; i2++)
					if (bankTabs[i][i2].getId() == itemId)
						return new int[] { i, i2 };
		return null;
	}

	public boolean withdrawItemDel(int itemId, int amount) {
		boolean refresh = true;
		boolean forceDestroy = true;
		int[] slot = getSlot(itemId);
		if (slot == null)
			return false;
		Item item = bankTabs[slot[0]][slot[1]];
		boolean destroyed = false;
		if (amount >= item.getAmount()) {
			if (bankTabs[slot[0]].length == 1 && (forceDestroy || bankTabs.length != 1)) {
				destroyTab(slot[0]);
				if (refresh)
					refreshTabs();
				destroyed = true;
			} else {
				Item[] tab = new Item[bankTabs[slot[0]].length - 1];
				System.arraycopy(bankTabs[slot[0]], 0, tab, 0, slot[1]);
				System.arraycopy(bankTabs[slot[0]], slot[1] + 1, tab, slot[1], bankTabs[slot[0]].length - slot[1] - 1);
				bankTabs[slot[0]] = tab;
				if (refresh)
					refreshTab(slot[0]);
			}
		} else
			bankTabs[slot[0]][slot[1]] = new Item(item.getId(), item.getAmount() - amount, item.getMetaData());
		if (refresh)
			refreshItems();
		return destroyed;
	}

	public Item getItem(int id) {
		for (Item[] bankTab : bankTabs)
			for (Item item : bankTab)
				if (item.getId() == id)
					return item;
		return null;
	}

	public int[] getItemSlot(int id) {
		for (int tab = 0; tab < bankTabs.length; tab++)
			for (int slot = 0; slot < bankTabs[tab].length; slot++)
				if (bankTabs[tab][slot].getId() == id)
					return new int[] { tab, slot };
		return null;
	}

	public Item getItem(int[] slot) {
		if (slot == null)
			return null;
		return bankTabs[slot[0]][slot[1]];
	}

	public int getStartSlot(int tabId) {
		int slotId = 0;
		for (int tab = 1; tab < (tabId == 0 ? bankTabs.length : tabId); tab++)
			slotId += bankTabs[tab].length;

		return slotId;

	}

	public int[] getRealSlot(int slot) {
		for (int tab = 1; tab < bankTabs.length; tab++) {
			if (slot < bankTabs[tab].length)
				return new int[] { tab, slot };
			slot -= bankTabs[tab].length;
		}
		if (slot >= bankTabs[0].length)
			return null;
		return new int[] { 0, slot };
	}

	public void refreshViewingTab() {
		player.getVars().setVarBit(4893, currentTab + 1);
		player.getVars().syncVarsToClient();
	}

	public void refreshTab(Player other, int slot) {
		if (slot == 0)
			return;
		player.getVars().setVarBit(4885 + (slot - 1), getTabSize(other, slot));
		refreshViewingTab();
	}

	public void refreshTab(int slot) {
		if (slot == 0)
			return;
		player.getVars().setVarBit(4885 + (slot - 1), getTabSize(slot));
		refreshViewingTab();
	}

	public void sendItems() {
		player.getPackets().sendItems(95, getContainerCopy());
		player.getPackets().sendVarc(1038, 0);
		player.getPackets().sendVarc(192, getBankSize());
	}

	public void sendItemsOther(Player other) {
		player.getPackets().sendItems(95, other.getBank().getContainerCopy());
	}

	public void refreshItems(int[] slots) {
		player.getPackets().sendUpdateItems(95, getContainerCopy(), slots);
		player.getPackets().sendVarc(1038, 0);
		player.getPackets().sendVarc(192, getBankSize());
	}

	public Item[] getContainerCopy() {
		if (lastContainerCopy == null)
			lastContainerCopy = generateContainer();
		return lastContainerCopy;
	}

	public void refreshItems() {
		refreshItems(generateContainer(), getContainerCopy());
	}

	public void refreshItems(Item[] itemsAfter, Item[] itemsBefore) {
		if (itemsBefore.length != itemsAfter.length) {
			lastContainerCopy = itemsAfter;
			sendItems();
			return;
		}
		int[] changedSlots = new int[itemsAfter.length];
		int count = 0;
		for (int index = 0; index < itemsAfter.length; index++)
			if (itemsBefore[index] != itemsAfter[index])
				changedSlots[count++] = index;
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		lastContainerCopy = itemsAfter;
		refreshItems(finalChangedSlots);
	}

	public int getBankSize() {
		int size = 0;
		for (Item[] bankTab : bankTabs)
			size += bankTab.length;
		return size;
	}

	public Item[] generateContainer() {
		Item[] container = new Item[getBankSize()];
		int count = 0;
		for (int slot = 1; slot < bankTabs.length; slot++) {
			System.arraycopy(bankTabs[slot], 0, container, count, bankTabs[slot].length);
			count += bankTabs[slot].length;
		}
		System.arraycopy(bankTabs[0], 0, container, count, bankTabs[0].length);
		return container;
	}

	public void unlockButtons() {
		//		player.getPackets().sendHideIComponent(762, 42, false);
		//		player.getPackets().sendHideIComponent(762, 43, false); //unlocks bank pin shit
		//		player.getPackets().sendHideIComponent(762, 44, false);

		player.getPackets().setIFEvents(new IFEvents(762, 95, 0, MAX_BANK_SIZE)
				.enableRightClickOptions(0,1,2,3,4,5,6,9)
				.setDepth(2)
				.enableDrag());
		player.getPackets().setIFEvents(new IFEvents(763, 0, 0, 27)
				.enableUseOptions(UseFlag.ICOMPONENT)
				.enableRightClickOptions(0,1,2,3,4,5,9)
				.setDepth(1)
				.enableDrag());
	}

	public void switchWithdrawNotes() {
		withdrawNotes = !withdrawNotes;
	}

	public void switchInsertItems() {
		insertItems = !insertItems;
		player.getVars().setVar(305, insertItems ? 1 : 0);
	}

	public boolean getInsertItems() {
		return insertItems;
	}

	public boolean getWithdrawNotes() {
		return withdrawNotes;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(int currentTab) {
		if (currentTab >= bankTabs.length)
			return;
		this.currentTab = currentTab;
		player.getPackets().sendVarc(190, 1);
	}

	public int getLastX() {
		return lastX;
	}

	public void setLastX(int lastX) {
		this.lastX = lastX;
	}

	public void clear() {
		bankTabs = new Item[1][0];
	}

	public boolean enteredPIN() {
		if (bankPin == 0 || sessionPin)
			return true;
		if (enteredPin == null)
			return false;
		List<String> toRemove = new ArrayList<>();
		for (String ip : enteredPin.keySet())
			if (System.currentTimeMillis() - enteredPin.get(ip) > PIN_VALIDITY_TIME)
				toRemove.add(ip);
		for (String ip : toRemove)
			enteredPin.remove(ip);
		return enteredPin.containsKey(player.getSession().getIP());
	}

	public void setEnteredPIN() {
		if (enteredPin == null)
			enteredPin = new ConcurrentHashMap<>();
		enteredPin.put(player.getSession().getIP(), System.currentTimeMillis());
		sessionPin = true;
	}

	public boolean checkPin() {
		if (bankPin != 0 && !enteredPIN()) {
			openPin();
			return false;
		}
		return true;
	}
}
