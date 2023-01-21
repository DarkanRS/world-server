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
package com.rs.game.content.interfacehandlers;

import java.text.NumberFormat;
import java.util.Locale;

import com.rs.game.content.world.Rest;
import com.rs.game.model.entity.player.managers.InterfaceManager;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.model.entity.player.managers.PriceChecker;
import com.rs.lib.net.ClientPacket;
import com.rs.net.LobbyCommunicator;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.ReportsManager;

@PluginEventHandler
public class GameFrame {

	public static ButtonClickHandler handlePrayerOrb = new ButtonClickHandler(749, e -> {
		if (e.getComponentId() == 4)
			if (e.getPacket() == ClientPacket.IF_OP1) // activate
				e.getPlayer().getPrayer().switchQuickPrayers();
			else if (e.getPacket() == ClientPacket.IF_OP2) // switch
				e.getPlayer().getPrayer().switchSettingQuickPrayer();
	});

	public static ButtonClickHandler handleRunOrb = new ButtonClickHandler(750, e -> {
		if (e.getComponentId() == 4)
			if (e.getPacket() == ClientPacket.IF_OP1) {
				e.getPlayer().toggleRun(e.getPlayer().isResting() ? false : true);
				if (e.getPlayer().isResting())
					e.getPlayer().stopAll();
			} else if (e.getPacket() == ClientPacket.IF_OP2) {
				if (e.getPlayer().isResting()) {
					e.getPlayer().stopAll();
					return;
				}
				if (e.getPlayer().getEmotesManager().isAnimating()) {
					e.getPlayer().sendMessage("You can't rest while perfoming an emote.");
					return;
				}
				if (e.getPlayer().isLocked()) {
					e.getPlayer().sendMessage("You can't rest while perfoming an action.");
					return;
				}
				e.getPlayer().stopAll();
				e.getPlayer().getActionManager().setAction(new Rest());
			}
	});

	public static ButtonClickHandler handleAudioSettingsTab = new ButtonClickHandler(429, e -> {
		if (e.getComponentId() == 18)
			e.getPlayer().getInterfaceManager().sendSubDefault(Sub.TAB_SETTINGS);
	});

	public static ButtonClickHandler handleChatSettings = new ButtonClickHandler(982, e -> {
		if (e.getComponentId() == 5)
			e.getPlayer().getInterfaceManager().sendSubDefault(Sub.TAB_SETTINGS);
		else if (e.getComponentId() == 41)
			e.getPlayer().setPrivateChatSetup(e.getPlayer().getPrivateChatSetup() == 0 ? 1 : 0);
		else if (e.getComponentId() >= 17 && e.getComponentId() <= 36)
			e.getPlayer().setClanChatSetup(e.getComponentId() - 17);
		else if (e.getComponentId() >= 97 && e.getComponentId() <= 116)
			e.getPlayer().setGuestChatSetup(e.getComponentId() - 97);
		else if (e.getComponentId() >= 49 && e.getComponentId() <= 66)
			e.getPlayer().setPrivateChatSetup(e.getComponentId() - 48);
		else if (e.getComponentId() >= 72 && e.getComponentId() <= 91)
			e.getPlayer().setFriendChatSetup(e.getComponentId() - 72);
	});

	public static ButtonClickHandler handleSettingsTab = new ButtonClickHandler(261, e -> {
		if (e.getPlayer().getInterfaceManager().containsInventoryInter())
			return;
		if (e.getComponentId() == 22) {
			if (e.getPlayer().getInterfaceManager().containsScreenInter()) {
				e.getPlayer().sendMessage("Please close the interface you have open before setting your graphic options.");
				return;
			}
			e.getPlayer().stopAll();
			e.getPlayer().getInterfaceManager().sendInterface(742);
		} else if (e.getComponentId() == 12)
			e.getPlayer().switchAllowChatEffects();
		else if (e.getComponentId() == 13)
			e.getPlayer().getInterfaceManager().sendSub(Sub.TAB_SETTINGS, 982);
		else if (e.getComponentId() == 14)
			e.getPlayer().switchMouseButtons();
		else if (e.getComponentId() == 24) // audio options
			e.getPlayer().getInterfaceManager().sendSub(Sub.TAB_SETTINGS, 429);
		else if (e.getComponentId() == 16) // house options
			e.getPlayer().getInterfaceManager().sendSub(Sub.TAB_SETTINGS, 398);
	});

	public static ButtonClickHandler handleChatboxGameBar = new ButtonClickHandler(751, e -> {
		if (e.getComponentId() == 14)
			ReportsManager.report(e.getPlayer());
		if (e.getComponentId() == 23) {
			if (e.getPacket() == ClientPacket.IF_OP2)
				e.getPlayer().setClanStatus(0);
			else if (e.getPacket() == ClientPacket.IF_OP3)
				e.getPlayer().setClanStatus(1);
			else if (e.getPacket() == ClientPacket.IF_OP4)
				e.getPlayer().setClanStatus(2);
		} else if (e.getComponentId() == 32) {
			if (e.getPacket() == ClientPacket.IF_OP2)
				e.getPlayer().setFilterGame(false);
			else if (e.getPacket() == ClientPacket.IF_OP4)
				e.getPlayer().setFilterGame(true);
		} else if (e.getComponentId() == 0) {
			if (e.getPacket() == ClientPacket.IF_OP2) {
				e.getPlayer().getSocial().setFcStatus(0);
				LobbyCommunicator.updateSocial(e.getPlayer());
			} else if (e.getPacket() == ClientPacket.IF_OP3) {
				e.getPlayer().getSocial().setFcStatus(1);
				LobbyCommunicator.updateSocial(e.getPlayer());
			} else if (e.getPacket() == ClientPacket.IF_OP4) {
				e.getPlayer().getSocial().setFcStatus(2);
				LobbyCommunicator.updateSocial(e.getPlayer());
			}
		} else if (e.getComponentId() == 23) {
			if (e.getPacket() == ClientPacket.IF_OP2)
				e.getPlayer().setClanStatus(0);
			else if (e.getPacket() == ClientPacket.IF_OP3)
				e.getPlayer().setClanStatus(1);
			else if (e.getPacket() == ClientPacket.IF_OP4)
				e.getPlayer().setClanStatus(2);
		} else if (e.getComponentId() == 17)
			if (e.getPacket() == ClientPacket.IF_OP2)
				e.getPlayer().setAssistStatus(0);
			else if (e.getPacket() == ClientPacket.IF_OP3)
				e.getPlayer().setAssistStatus(1);
			else if (e.getPacket() == ClientPacket.IF_OP4)
				e.getPlayer().setAssistStatus(2);
			else if (e.getPacket() == ClientPacket.IF_OP6) {
				// ASSIST XP Earned/Time
			}
	});

	public static ButtonClickHandler handleWorldMap = new ButtonClickHandler(755, e -> {
		if (e.getComponentId() == 44)
			e.getPlayer().getInterfaceManager().setWindowsPane(e.getPlayer().resizeable() ? 746 : 548);
		else if (e.getComponentId() == 42) {
			e.getPlayer().getHintIconsManager().removeAll(); //TODO find hintIcon index
			e.getPlayer().getVars().setVar(1159, 1);
		}
	});

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(new Object[] { InterfaceManager.FIXED_TOP, InterfaceManager.RESIZEABLE_TOP }, e -> {
		if ((e.getInterfaceId() == 548 && e.getComponentId() == 167) || (e.getInterfaceId() == 746 && e.getComponentId() == 208)) {
			switch(e.getPacket()) {
			case IF_OP1 -> e.getPlayer().getPackets().sendRunScript(5557, 1);
			case IF_OP2 -> e.getPlayer().sendInputInteger("How much would you like to withdraw?", num -> e.getPlayer().getInventory().coinPouchToInventory(num));
			case IF_OP3 -> e.getPlayer().sendMessage("Your pouch contains " + NumberFormat.getNumberInstance(Locale.US).format(e.getPlayer().getInventory().getCoins()) + " coins.");
			case IF_OP4 -> {
				if (e.getPlayer().getInterfaceManager().containsScreenInter() || e.getPlayer().inCombat(10000) || e.getPlayer().hasBeenHit(10000)) {
					e.getPlayer().sendMessage("Please finish what you're doing before opening the price checker.");
					return;
				}
				e.getPlayer().stopAll();
				PriceChecker.openPriceCheck(e.getPlayer());
				
			}
			default -> e.getPlayer().sendMessage("Unknown coin pouch option.");
			}
			return;
		}
		if ((e.getInterfaceId() == 548 && e.getComponentId() == 157) || (e.getInterfaceId() == 746 && e.getComponentId() == 200)) {
			if (e.getPacket() == ClientPacket.IF_OP2) {
				e.getPlayer().getHintIconsManager().removeAll();
				e.getPlayer().getVars().setVar(1159, 1);
				return;
			}
			if (e.getPlayer().getInterfaceManager().containsScreenInter() || e.getPlayer().getInterfaceManager().containsInventoryInter() || e.getPlayer().inCombat(10000)) {
				e.getPlayer().sendMessage("Please finish what you're doing before opening the world map.");
				return;
			}
			e.getPlayer().getInterfaceManager().setTopInterface(755, false);
			int posHash = e.getPlayer().getX() << 14 | e.getPlayer().getY();
			e.getPlayer().getPackets().sendVarc(622, posHash); // map open center pos
			e.getPlayer().getPackets().sendVarc(674, posHash); // player position
		} else if ((e.getInterfaceId() == 548 && e.getComponentId() == 35) || (e.getInterfaceId() == 746 && e.getComponentId() == 55)) {
			if (e.getPacket() == ClientPacket.IF_OP1)
				e.getPlayer().getSkills().switchXPDisplay();
			else if (e.getPacket() == ClientPacket.IF_OP2)
				e.getPlayer().getSkills().switchXPPopup();
			else if (e.getPacket() == ClientPacket.IF_OP3) {
				if (e.getPlayer().getInterfaceManager().containsScreenInter() || e.getPlayer().getInterfaceManager().containsInventoryInter() || e.getPlayer().inCombat(10000)) {
					e.getPlayer().sendMessage("Please finish what you're doing before opening the XP counter customizer.");
					return;
				}
				e.getPlayer().getSkills().setupXPCounter();
			}
		} else if ((e.getInterfaceId() == 746 && e.getComponentId() == 207) || (e.getInterfaceId() == 548 && e.getComponentId() == 159))
			if (e.getPacket() == ClientPacket.IF_OP4) {
				if (e.getPlayer().getInterfaceManager().containsScreenInter()) {
					e.getPlayer().sendMessage("Please finish what you're doing before opening the price checker.");
					return;
				}
				e.getPlayer().stopAll();
				PriceChecker.openPriceCheck(e.getPlayer());
			}
	});

	public static ButtonClickHandler handleAudioOptionsClose = new ButtonClickHandler(743, e -> {
		if (e.getComponentId() == 20)
			e.getPlayer().stopAll();
	});

	public static ButtonClickHandler handleGraphicsSettingsClose = new ButtonClickHandler(742, e -> {
		if (e.getComponentId() == 46)
			e.getPlayer().stopAll();
	});
}
