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
package com.rs.game.content.world;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.LoyaltyRewardDefinitions.Reward;
import com.rs.cache.loaders.LoyaltyRewardDefinitions.Tab;
import com.rs.cache.loaders.LoyaltyRewardDefinitions.Type;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.LoginHandler;

@PluginEventHandler
public class LoyaltyShop {

	public static LoginHandler sendLoyaltyPoints = new LoginHandler(e -> {
		e.getPlayer().getVars().setVar(2225, e.getPlayer().loyaltyPoints);
	});

	public static void refresh(Player player) {
		player.getVars().setVar(2225, player.loyaltyPoints);
		player.getPackets().sendVarc(1648, player.loyaltyPoints);
		refreshOwned(player);
		refreshFavorite(player);
		for (Tab tab : Tab.values()) {
			EnumDefinitions map = EnumDefinitions.getEnum(tab.getCSMapId(player.getAppearance().isMale()));
			player.getPackets().setIFRightClickOps(1143, tab.getBuyComponent(), 0, map.getSize(), 0, 1);
			player.getPackets().setIFRightClickOps(1143, tab.getFavoriteComponent(), 0, map.getSize(), 0, 1);
		}
		player.getPackets().setIFRightClickOps(1143, 40, 0, 4, 0, 1); //costume color select
	}

	public static void open(Player player) {
		player.getInterfaceManager().setFullscreenInterface(317, 1143);
		refresh(player);
	}

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(1143, e -> {
		switch (e.getComponentId()) {
		case 103:
			e.getPlayer().closeInterfaces();
			break;
		case 1:
			e.getPlayer().getVars().setVarBit(9487, -1);
			break;
		case 13:
			e.getPlayer().getVars().setVarBit(9487, 6);
			break;
		case 3:
			e.getPlayer().getVars().setVarBit(9487, 8);
			break;
		case 50:
			confirmBuy(e.getPlayer());
			break;
		case 40:
			e.getPlayer().getTempAttribs().setI("LoyaltyRewardColor", e.getSlotId());
			break;
		case 59:
		case 51:
		case 163:
			closeMessage(e.getPlayer());
			break;
		}
		if (e.getComponentId() >= 7 && e.getComponentId() <= 12) {
			Tab tab = Tab.values()[e.getComponentId() - 7];
			if (tab != null && (e.getPlayer().hasRights(Rights.DEVELOPER) || (tab != Tab.RECOLOR)))
				e.getPlayer().getVars().setVarBit(9487, tab.configId);
			else
				sendMessage(e.getPlayer(), "Tab not added", "That tab hasn't been added yet. Sorry.");
			return;
		}
		Tab tab = Tab.forId(e.getComponentId());
		if (tab != null && tab.isBuyComponent(e.getComponentId())) {
			EnumDefinitions map = EnumDefinitions.getEnum(tab.getCSMapId(e.getPlayer().getAppearance().isMale()));
			Reward reward = tab.getReward(e.getSlotId());
			if (reward != null) {
				e.getPlayer().getTempAttribs().setO("LoyaltyReward", reward);
				e.getPlayer().getPackets().sendRunScriptReverse(5355, e.getPlayer().unlockedLoyaltyReward(reward) ? 1 : 0, map.getIntValue(e.getSlotId()));
				e.getPlayer().getPackets().setIFHidden(1143, 16, false);
				e.getPlayer().getPackets().setIFHidden(1143, 56, false);
				e.getPlayer().getPackets().setIFHidden(1143, 58, true);
			} else
				e.getPlayer().getTempAttribs().removeO("LoyaltyReward");
		} else if (tab != null && tab.isFavoriteComponent(e.getComponentId())) {
			Reward reward = tab.getReward(e.getSlotId());
			if (reward != null) {
				if (e.getPlayer().favoritedLoyaltyReward(reward))
					e.getPlayer().unfavoriteLoyaltyReward(reward);
				else
					e.getPlayer().favoriteLoyaltyReward(reward);
				refreshFavorite(e.getPlayer());
			}
		}
		refresh(e.getPlayer());
		if (e.getPlayer().hasRights(Rights.DEVELOPER))
			e.getPlayer().getPackets().sendDevConsoleMessage("Loyalty click: " + e.getComponentId() + ", " + e.getSlotId() + ", " + e.getSlotId2());
	});

	public static void confirmBuy(Player player) {
		Reward reward = player.getTempAttribs().getO("LoyaltyReward");
		if (reward != null) {
			if (reward.getPreReq() <= 0 || player.unlockedLoyaltyReward(Reward.forId(reward.getPreReq()))) {
				if (player.unlockedLoyaltyReward(reward)) {
					giveReward(player, reward);
					closeMessage(player);
					sendMessage(player, "Success!", "You have reclaimed your " + reward.getItem().getName() + "! It has been sent to your bank.");
				} else if (player.loyaltyPoints >= reward.getPrice()) {
					player.unlockLoyaltyReward(reward);
					player.loyaltyPoints -= reward.getPrice();
					giveReward(player, reward);
					closeMessage(player);
					sendMessage(player, "Success!", "You have unlocked " + (reward.getType() == Type.EMOTE ? " the emote " + reward.name().toLowerCase().replace("_", " ") + "." : reward.getItem().getName() + "! It has been sent to your bank."));
				} else
					sendMessage(player, "Not Enough Points", "You need " + reward.getPrice() + " loyalty points to purchase this.");
			} else
				sendMessage(player, "Previous Reward Required", "You need to buy " + ItemDefinitions.getDefs(reward.getPreReq()).name + " before purchasing this.");
			refresh(player);
		}
	}

	private static void deletePreItems(Player player, Reward reward) {
		Reward curr = Reward.forId(reward.getPreReq());
		while(curr != null) {
			if (player.getBank().containsItem(curr.getItem().getId(), 1))
				player.getBank().removeItem(curr.getItem().getId());
			if (player.getInventory().containsItem(curr.getItem().getId(), 1))
				player.getInventory().deleteItem(curr.getItem().getId(), Integer.MAX_VALUE);
			curr = Reward.forId(curr.getPreReq());
		}
	}

	private static Reward getHighestTierUnlocked(Player player, Reward reward) {
		Reward highest = reward.getLowestTier();
		while(player.unlockedLoyaltyReward(Reward.forPreReq(highest.getItem().getId())))
			highest = Reward.forPreReq(highest.getItem().getId());
		return highest;
	}

	private static void giveReward(Player player, Reward reward) {
		switch(reward.getType()) {
		case AURA:
			reward = getHighestTierUnlocked(player, reward);
			deletePreItems(player, reward);
			player.getBank().addItem(reward.getItem(), true);
			break;
		case EFFECT:
			player.getBank().addItem(reward.getItem(), true);
			break;
		case COSTUME:
			int color = 0;
			Object colObj = player.getTempAttribs().removeI("LoyaltyRewardColor");
			if (colObj != null && colObj instanceof Integer)
				color = (int) colObj;
			for (Item item : reward.getItems(player.getAppearance().isMale() ? 0 : 1))
				if (item.getAmount() == 1)
					player.getBank().addItem(item, true);
				else if (item.getAmount() == 2)
					player.getBank().addItem(new Item(item.getId() + (color*2), 1), true);
			break;
		case TITLE:
			player.setTitle(null);
			player.setTitleColor(null);
			player.setTitleShading(null);
			player.getAppearance().setTitle(reward.getItem().getId());
			player.getAppearance().generateAppearanceData();
			break;
		case EMOTE:
			for (Emote emote : Emote.values())
				if (emote.name().equals(reward.name())) {
					player.getEmotesManager().unlockEmote(emote);
					break;
				}
			break;
		default:
			break;
		}
	}

	/*
	 * player.getPackets().sendHideIComponent(interfaceId, 56, false);
	 * https://i.imgur.com/wnU2rJr.png
	 *
	 * player.getPackets().sendHideIComponent(interfaceId, 57, false);
	 * https://i.imgur.com/50Rt2Rg.png
	 *
	 * player.getPackets().sendHideIComponent(interfaceId, 58, false);
	 * https://i.imgur.com/pqeRgOA.png
	 */

	public static void closeMessage(Player player) {
		player.getPackets().setIFHidden(1143, 16, true);
	}

	public static void sendMessage(Player player, String title, String message) {
		player.getPackets().setIFHidden(1143, 16, false);
		player.getPackets().setIFHidden(1143, 56, true);
		//player.getPackets().sendHideIComponent(1143, 57, false);
		player.getPackets().setIFHidden(1143, 58, false);
		player.getPackets().setIFText(1143, 161, title);
		player.getPackets().setIFText(1143, 162, message);
	}

	public static int[] getFavoriteFlags(Player player, Tab tab) {
		if (tab == Tab.AURAS) {
			int val1 = 0;
			int val2 = 0;
			int val3 = 0;
			for (int i = 0;i < Tab.AURAS.getRewards().length;i++)
				if (i <= 31 && player.favoritedLoyaltyReward(Tab.AURAS.getRewards()[i]))
					val1 += (1 << i);
				else if (i <= 62 && player.favoritedLoyaltyReward(Tab.AURAS.getRewards()[i]))
					val2 += (1 << (i-31));
				else if (player.favoritedLoyaltyReward(Tab.AURAS.getRewards()[i]))
					val3 += (1 << (i-62));
			return new int[] { val1, val2, val3 };
		}
		if (tab != Tab.TITLES) {
			int val = 0;
			for (Reward r : tab.getRewards())
				if (player.favoritedLoyaltyReward(r))
					val += (1 << r.getBit());
			return new int[] { val };
		}
		int val1 = 0;
		int val2 = 0;
		for (int i = 0;i < Tab.TITLES.getRewards().length;i++)
			if (i < 16 && player.favoritedLoyaltyReward(Tab.TITLES.getRewards()[i]))
				val1 += (1 << i);
			else if (player.favoritedLoyaltyReward(Tab.TITLES.getRewards()[i]))
				val2 += (1 << (i-16));
		return new int[] { val1, val2 };
	}

	public static int[] getUnlockedFlags(Player player, Tab tab) {
		if (tab == Tab.AURAS) {
			int val1 = 0;
			int val2 = 0;
			int val3 = 0;
			for (int i = 0;i < Tab.AURAS.getRewards().length;i++)
				if (i <= 31 && player.unlockedLoyaltyReward(Tab.AURAS.getRewards()[i]))
					val1 += (1 << i);
				else if (i <= 62 && player.unlockedLoyaltyReward(Tab.AURAS.getRewards()[i]))
					val2 += (1 << (i-31));
				else if (player.unlockedLoyaltyReward(Tab.AURAS.getRewards()[i]))
					val3 += (1 << (i-62));
			return new int[] { val1, val2, val3 };
		}
		if (tab != Tab.TITLES) {
			int val = 0;
			for (Reward r : tab.getRewards())
				if (player.unlockedLoyaltyReward(r))
					val += (1 << r.getBit());
			return new int[] { val };
		}
		int val1 = 0;
		int val2 = 0;
		for (int i = 0;i < Tab.TITLES.getRewards().length;i++)
			if (i < 16 && player.unlockedLoyaltyReward(Tab.TITLES.getRewards()[i]))
				val1 += (1 << i);
			else if (player.unlockedLoyaltyReward(Tab.TITLES.getRewards()[i]))
				val2 += (1 << (i-16));
		return new int[] { val1, val2 };
	}

	public static void refreshFavorite(Player player) {
		int[] auras = getFavoriteFlags(player, Tab.AURAS);
		player.getVars().setVar(2391, auras[0]);
		player.getVars().setVar(2444, auras[1]);
		player.getVars().setVar(2541, auras[2]);

		int[] titles = getFavoriteFlags(player, Tab.TITLES);
		player.getVars().setVar(2394, titles[0] + getFavoriteFlags(player, Tab.RECOLOR)[0]);
		player.getVars().setVar(2445, titles[1]);

		player.getVars().setVar(2542, getFavoriteFlags(player, Tab.EFFECTS)[0]);
		player.getVars().setVar(2392, getFavoriteFlags(player, Tab.EMOTES)[0]);
		player.getVars().setVar(2393, getFavoriteFlags(player, Tab.COSTUMES)[0]);
		player.getVars().setVar(2541, 0, true); //refresh
	}

	public static void refreshOwned(Player player) {
		int[] auras = getUnlockedFlags(player, Tab.AURAS);
		player.getVars().setVar(2229, auras[0]);
		player.getVars().setVar(2443, auras[1]);
		player.getVars().setVar(2539, auras[2]);

		int[] titles = getUnlockedFlags(player, Tab.TITLES);
		player.getVars().setVar(2232, titles[0] + getUnlockedFlags(player, Tab.RECOLOR)[0]);
		player.getVars().setVar(2447, titles[1]);

		player.getVars().setVar(2540, getUnlockedFlags(player, Tab.EFFECTS)[0]);
		player.getVars().setVar(2230, getUnlockedFlags(player, Tab.EMOTES)[0]);
		player.getVars().setVar(2231, getUnlockedFlags(player, Tab.COSTUMES)[0]);
		player.getVars().setVar(2541, 0, true); //refresh
	}
}
