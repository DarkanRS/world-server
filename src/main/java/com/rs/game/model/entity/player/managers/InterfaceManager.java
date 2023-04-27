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
package com.rs.game.model.entity.player.managers;

import com.rs.game.content.achievements.Achievement;
import com.rs.game.content.achievements.AchievementInterface;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class InterfaceManager {

	public static final int FIXED_TOP = 548;
	public static final int RESIZEABLE_TOP = 746;
	public static final int CHATBOX_TOP = 752;
	public static final int CHATBOX_SUB = 13;
	
	private Player player;
	private int top;

	private final Map<Integer, Integer> openedInterfaces = new ConcurrentHashMap<>();
	
	public enum ScreenMode {
		FULLSCREEN,
		FIXED,
		RESIZEABLE,
		FULLSCREEN2MAYBE;
		
		public static ScreenMode forId(int id) {
			if (id < 0 || id >= ScreenMode.values().length)
				return null;
			return ScreenMode.values()[id];
		}
		
		public boolean resizeable() {
			return this == RESIZEABLE || this == FULLSCREEN;
		}
	}
	
	public enum Sub {
						/* GAME WINDOWS */
						RENDER_SPACE(48, 0),
						CENTRAL(29, 44),
						//CENTRAL_SMALL(10, 44),
						
						FULL_GAMESPACE_BG(2, 28),
						FULL_GAMESPACE_BG2(11, 29),
						MINIGAME_HUD(12, 47),
						
						FADING(13, 48),
						
						ABOVE_CHATBOX(27, 43),
						
						LEVEL_UP(35, 46),
						ACHIEVEMENT_NOTIF(41, 271),
						
						FULLSCREEN_BG(42, 15),
						FULLSCREEN_BG2(44, 17),
						
						FULLSCREEN_FG(45, 18),
						FULLSCREEN_FG2(46, 204),
								
						/* HUD */
						CHATBOX(22, 168, CHATBOX_TOP),
						CHATBAR_SETTINGS(23, 53, 751, p -> p.getInterfaceManager().sendChatBoxInterface(9, 137)),
						MULTICOMBAT_SIGN(16, 40, 745),
						SYSTEM_UPDATE(25, 42, 754),
						LOGOUT(130, 194, 182),
						XP_DROPS(39, 16, 1213, p -> p.getPackets().setIFHidden(p.resizeable() ? RESIZEABLE_TOP : FIXED_TOP, p.resizeable() ? 39 : 16, !p.getSkills().xpDropsActive())),
						XP_COUNTER(202, 50, 1215),
		
						/* TABS */
		/* CONFIRMED */	ORB_HP(196, 160, 748),
		/* CONFIRMED */	ORB_PRAYER(197, 161, 749),
		/* CONFIRMED */	ORB_SUMMONING(199, 164, 747),
		/* CONFIRMED */	ORB_RUN(198, 162, 750, p -> p.sendRunButtonConfig()),
		/* CONFIRMED */	ORB_MONEYPOUCH(208, 167),
		
		/* CONFIRMED */ INVENTORY_OVERLAY(109, 172),
		
		/* CONFIRMED */	TAB_COMBAT(112, 176, 884, p -> p.getCombatDefinitions().sendUnlockAttackStylesButtons()),
		/* CONFIRMED */	TAB_ACHIEVEMENT(113, 177, 1056, p -> AchievementInterface.init(p)),
		/* CONFIRMED */	TAB_SKILLS(114, 178, 320),
		/* CONFIRMED */	TAB_QUEST(115, 179, 190, p -> p.getQuestManager().unlockQuestTabOptions()),
		/* CONFIRMED */	TAB_INVENTORY(116, 180, Inventory.INVENTORY_INTERFACE, p -> p.getInventory().unlockInventoryOptions()),
		/* CONFIRMED */	TAB_EQUIPMENT(117, 181, 387),
		/* CONFIRMED */	TAB_PRAYER(118, 182, 271, p -> p.getPrayer().unlockPrayerBookButtons()),
		/* CONFIRMED */	TAB_MAGIC(119, 183) {
							@Override
							public int getDefaultInterfaceId(Player p) {
								return p.getCombatDefinitions().getSpellbook().getInterfaceId();
							}
						},
		/* CONFIRMED */	TAB_FOLLOWER(120, 184),
		/* CONFIRMED */	TAB_FRIENDS(121, 185, 550),
		/* CONFIRMED */	TAB_FRIENDS_CHAT(122, 186, 1109),
		/* CONFIRMED */	TAB_CLAN_CHAT(123, 187, 1110),
		/* CONFIRMED */	TAB_SETTINGS(124, 188, 261),
		/* CONFIRMED */	TAB_EMOTES(125, 189, 590, p -> p.getEmotesManager().unlockEmotesBook()),
		/* CONFIRMED */	TAB_MUSIC(126, 190, 187, p -> p.getMusicsManager().unlockMusicPlayer()),
		/* CONFIRMED */	TAB_NOTES(127, 191, 34);
		
		public static final Sub[] ALL_GAME_TABS = { Sub.TAB_COMBAT, Sub.TAB_ACHIEVEMENT, Sub.TAB_SKILLS, Sub.TAB_QUEST, Sub.TAB_INVENTORY, Sub.TAB_EQUIPMENT, Sub.TAB_PRAYER, Sub.TAB_MAGIC, Sub.TAB_FOLLOWER, Sub.TAB_FRIENDS, Sub.TAB_FRIENDS_CHAT, Sub.TAB_CLAN_CHAT, Sub.TAB_SETTINGS, Sub.TAB_EMOTES, Sub.TAB_MUSIC, Sub.TAB_NOTES, Sub.ORB_RUN };
		private static Map<Integer, Sub> BY_HASH = new HashMap<>();
		
		static {
			for (Sub sub : Sub.values()) {
				if (BY_HASH.put(Utils.toInterfaceHash(FIXED_TOP, sub.fixed), sub) != null)
					System.err.println("Duplicate fixed hash for: " + sub + " - " + sub.fixed);
				if (BY_HASH.put(Utils.toInterfaceHash(RESIZEABLE_TOP, sub.resizeable), sub) != null)
					System.err.println("Duplicate resizeable hash for: " + sub + " - " + sub.resizeable);
			}
		}
		
		public static Sub forHash(int hash) {
			return BY_HASH.get(hash);
		}
		
		private int resizeable, fixed;
		private int defaultInter = -1;
		private Consumer<Player> defaultProcedure = p -> {};
		
		Sub(int resizeable, int fixed) {
			this.resizeable = resizeable;
			this.fixed = fixed;
		}
		
		Sub(int resizeable, int fixed, int defaultInter) {
			this(resizeable, fixed);
			this.defaultInter = defaultInter;
		}
		
		Sub(int resizeable, int fixed, int defaultInterfaceId, Consumer<Player> defaultProcedure) {
			this(resizeable, fixed, defaultInterfaceId);
			this.defaultProcedure = defaultProcedure;
		}
		
		public int getComponent(boolean resizeableScreen) {
			return resizeableScreen ? resizeable : fixed;
		}
		
		public boolean isClicked(int interfaceId, int componentId) {
			return (interfaceId == RESIZEABLE_TOP && componentId == resizeable) || (interfaceId == FIXED_TOP && componentId == fixed);
		}

		public int getDefaultInterfaceId(Player player) {
			return defaultInter;
		}

		public int getHash(ScreenMode oldMode) {
			return Utils.toInterfaceHash(oldMode.resizeable() ? RESIZEABLE_TOP : FIXED_TOP, oldMode.resizeable() ? resizeable : fixed);
		}
	}

	public InterfaceManager(Player player) {
		this.player = player;
	}
	
	public void sendGameWindowSub(int resizable, int fixed, int interfaceId, boolean clickThrough) {
		sendSubSpecific(clickThrough, player.resizeable() ? RESIZEABLE_TOP : FIXED_TOP, player.resizeable() ? resizable : fixed, interfaceId);
	}

	public void removeGameWindowSub(int resizableComponentId, int fixedComponentId) {
		removeSubSpecific(player.resizeable() ? RESIZEABLE_TOP : FIXED_TOP, player.resizeable() ? resizableComponentId : fixedComponentId);
	}

	public void sendChatBoxInterface(int interfaceId) {
		sendSubSpecific(true, CHATBOX_TOP, CHATBOX_SUB, interfaceId);
	}

	public void sendChatBoxInterface(int componentId, int interfaceId) {
		sendSubSpecific(true, CHATBOX_TOP, componentId, interfaceId);
	}

	public void closeChatBoxInterface() {
		removeSubSpecific(CHATBOX_TOP, CHATBOX_SUB);
	}

	public boolean containsChatBoxInter() {
		return isSubOpen(CHATBOX_TOP, CHATBOX_SUB);
	}

	public boolean isSubOpen(int parentInterfaceId, int parentInterfaceComponentId) {
		return openedInterfaces.containsKey(getComponentUId(parentInterfaceId, parentInterfaceComponentId));
	}
	
	public boolean isSubOpenWindow(int componentId) {
		return isSubOpen(player.resizeable() ? RESIZEABLE_TOP : FIXED_TOP, componentId);
	}
	
	public void sendSubSpecific(boolean clickThrough, int parentInterfaceId, int parentInterfaceComponentId, int interfaceId) {
		//Logger.debug(parentInterfaceId + " - " + parentInterfaceComponentId + " - " + interfaceId + " - " + clickThrough);
		int parentComponentUID = getComponentUId(parentInterfaceId, parentInterfaceComponentId);
		//int parentId = getInterfaceParentId(interfaceId);

		Integer oldInterface = openedInterfaces.get(parentComponentUID);
		if (oldInterface != null)
			clearChilds(oldInterface);

		openedInterfaces.put(parentComponentUID, interfaceId);
		player.getPackets().sendInterface(clickThrough, parentInterfaceId, parentInterfaceComponentId, interfaceId);
	}
	
	public void removeSubSpecific(int parentInterfaceId, int parentInterfaceComponentId) {
		removeInterfaceByParent(getComponentUId(parentInterfaceId, parentInterfaceComponentId));
	}

	public void removeInterfaceByParent(int parentUID) {
		Integer removedInterface = openedInterfaces.remove(parentUID);
		if (removedInterface != null) {
			clearChilds(removedInterface);
			player.getPackets().closeInterface(parentUID);
//			if (player.getFamiliar() != null)
//				Familiar.sendLeftClickOption(player);
		}
	}
	
	public static int getComponentUId(int interfaceId, int componentId) {
		return interfaceId << 16 | componentId;
	}

	public int getInterfaceParentId(int interfaceId) {
		if (interfaceId == top)
			return -1;
		for (int key : openedInterfaces.keySet()) {
			int value = openedInterfaces.get(key);
			if (value == interfaceId)
				return key;
		}
		return -1;
	}

	private void clearChilds(int parentInterfaceId) {
		for (int key : openedInterfaces.keySet())
			if (key >> 16 == parentInterfaceId)
				openedInterfaces.remove(key);
	}

	public void removeInterface(int interfaceId) {
		int parentUID = getInterfaceParentId(interfaceId);
		if (parentUID == -1)
			return;
		removeInterfaceByParent(parentUID);
	}

	public boolean topOpen(int interfaceId) {
		if (interfaceId == top)
			return true;
		for (int value : openedInterfaces.values())
			if (value == interfaceId)
				return true;
		return false;
	}

	public void sendSub(Sub sub, int interfaceId, boolean clickThrough) {
		if (interfaceId == -1)
			return;
		sendGameWindowSub(sub.resizeable, sub.fixed, interfaceId, clickThrough);
	}
	
	public void sendSub(Sub sub, int interfaceId) {
		sendSub(sub, interfaceId, true);
	}

	public void removeSubs(Sub... subs) {
		for (Sub tab : subs)
			removeSub(tab);
	}

	public void removeSub(Sub sub) {
		removeGameWindowSub(sub.resizeable, sub.fixed);
	}

	public void sendSubDefault(Sub sub) {
		sendSub(sub, sub.getDefaultInterfaceId(player), true);
		if (sub.defaultProcedure != null)
			sub.defaultProcedure.accept(player);
	}
	
	public void sendSubDefaults(Sub... subs) {
		for (Sub sub : subs)
			sendSubDefault(sub);
	}
	
	public boolean isSubClick(Sub sub, int interfaceId, int componentId) {
		return sub.isClicked(interfaceId, componentId);
	}

	public void flashTab(Sub tab) {
		int tabId = switch(tab) {
			case TAB_COMBAT -> 1;
			case TAB_ACHIEVEMENT -> 2;
			case TAB_SKILLS -> 3;
			case TAB_QUEST -> 4;
			case TAB_INVENTORY -> 5;
			case TAB_EQUIPMENT -> 6;
			case TAB_PRAYER -> 7;
			case TAB_MAGIC -> 8;
			case TAB_FOLLOWER -> 9;
			case TAB_FRIENDS -> 10;
			case TAB_FRIENDS_CHAT -> 11;
			case TAB_CLAN_CHAT -> 12;
			case TAB_SETTINGS -> 13;
			case TAB_EMOTES -> 14;
			case TAB_MUSIC -> 15;
			case TAB_NOTES -> 16;
			case ORB_RUN -> 17;
			default -> 0;
		};
		player.getVars().setVar(1021, tabId);
	}
	
	public void openTab(Sub tab) {
		int tabId = switch(tab) {
			case TAB_COMBAT -> 0;
			case TAB_ACHIEVEMENT -> 1;
			case TAB_SKILLS -> 2;
			case TAB_QUEST -> 3;
			case TAB_INVENTORY -> 4;
			case TAB_EQUIPMENT -> 5;
			case TAB_PRAYER -> 6;
			case TAB_MAGIC -> 7;
			case TAB_FOLLOWER -> 8;
			case TAB_FRIENDS -> 9;
			case TAB_FRIENDS_CHAT -> 10;
			case TAB_CLAN_CHAT -> 11;
			case TAB_SETTINGS -> 12;
			case TAB_EMOTES -> 13;
			case TAB_MUSIC -> 14;
			case TAB_NOTES -> 15;
			case ORB_RUN -> 16;
			default -> 0;
		};
		player.getPackets().sendVarc(168, tabId);
	}
	
	public void flashTabOff() {
		player.getVars().setVar(1021, 0);
	}
	
	public final void sendInterfaces() {
		if (openedInterfaces != null)
			openedInterfaces.clear();
		setDefaultTopInterface();
		if (player.getFamiliar() != null && player.isRunning())
			player.getFamiliar().sendMainConfigs();
		for (Sub sub : Sub.values())
			sendSubDefault(sub);
		player.getControllerManager().sendInterfaces();
	}
	
	public final void switchDisplayModes(ScreenMode screenMode) {
		if (player.getScreenMode() == screenMode)
			return;
		Map<Integer, Integer> old = new HashMap<>(openedInterfaces);
		for (int parentUid : old.keySet()) {
			Sub sub = Sub.forHash(parentUid);
			if (sub != null)
				removeSub(sub);
		}
		ScreenMode oldMode = player.getScreenMode();
		player.setScreenMode(screenMode);
		openedInterfaces.clear();
		setDefaultTopInterface();
		if (player.getFamiliar() != null && player.isRunning())
			player.getFamiliar().sendMainConfigs();
		for (Sub sub : Sub.values()) {
		//	sendSubDefault(sub);
			Integer prev = old.get(sub.getHash(oldMode));
			if (prev != null) {
				sendSub(sub, prev, sub == Sub.CENTRAL ? false : true);
				if (sub.defaultProcedure != null)
					sub.defaultProcedure.accept(player);
			}
		}
	}

	public void sendInterface(int interfaceId) {
		if (interfaceId > Utils.getInterfaceDefinitionsSize())
			return;
		sendSub(Sub.CENTRAL, interfaceId, false);
	}

	public void sendInterface(int interfaceId, boolean clickThrough) {
		if (interfaceId > Utils.getInterfaceDefinitionsSize())
			return;
		sendSub(Sub.CENTRAL, interfaceId, clickThrough);
	}

	public void sendInventoryInterface(int interfaceId) {
		sendSub(Sub.INVENTORY_OVERLAY, interfaceId, false);
	}

	public boolean containsReplacedChatBoxInter() {
		return isSubOpen(CHATBOX_TOP, 11);
	}

	public void replaceRealChatBoxInterface(int interfaceId) {
		sendSubSpecific(true, CHATBOX_TOP, 11, interfaceId);
	}

	public void closeReplacedRealChatBoxInterface() {
		removeSubSpecific(CHATBOX_TOP, 11);
	}

	public void setDefaultTopInterface() {
		setTopInterface(player.resizeable() ? RESIZEABLE_TOP : FIXED_TOP, false);
	}

	public void setTopInterface(int rootInterface, boolean gc) {
		top = rootInterface;
		player.getPackets().sendWindowsPane(rootInterface, gc ? 3 : 0);
	}

	public void setWindowsPane(int windowsPane) {
		top = windowsPane;
		player.getPackets().sendWindowsPane(top, 2);
	}

	public void sendAchievementComplete(Achievement achievement) {
		if (achievement == null)
			return;
		sendSub(Sub.ACHIEVEMENT_NOTIF, 1055);
		player.getPackets().sendVarc(1425, achievement.getId());
	}

	public boolean containsScreenInter() {
		return isSubOpenWindow(player.resizeable() ? Sub.CENTRAL.resizeable : Sub.CENTRAL.fixed);
	}

	public boolean containsInventoryInter() {
		return isSubOpenWindow(Sub.INVENTORY_OVERLAY.getComponent(player.resizeable()));
	}

	public void removeInventoryInterface() {
		this.removeSub(Sub.INVENTORY_OVERLAY);
	}

	public void setFadingInterface(int interfaceId) {
		sendSub(Sub.FADING, interfaceId);
	}

	public void closeFadingInterface() {
		removeSub(Sub.FADING);
	}

	public void removeCentralInterface() {
		removeSub(Sub.CENTRAL);
	}

	public void setFullscreenInterface(int backgroundInterface, int interfaceId) {
		removeCentralInterface();
		sendSub(Sub.FULLSCREEN_BG, backgroundInterface, false);
		sendSub(Sub.FULLSCREEN_BG2, interfaceId, false);
		player.setCloseInterfacesEvent(() -> removeSubs(Sub.FULLSCREEN_BG, Sub.FULLSCREEN_BG2));
	}

	public int getTopInterface() {
		return top;
	}

	public void gazeOrbOfOculus() {
		setTopInterface(475, false);
		sendSubSpecific(true, 475, 57, 751);
		sendSubSpecific(true, 475, 55, 752);
		player.setCloseInterfacesEvent(() -> {
			setDefaultTopInterface();
			player.getPackets().sendResetCamera();
		});
	}

	public void fadeIn() {
		setFadingInterface(115);
	}

	public void fadeOut() {
		setFadingInterface(170);
	}
	
	public void fadeInBG() {
		sendBackgroundInterfaceOverGameWindow(115);
	}

	public void fadeOutBG() {
		sendBackgroundInterfaceOverGameWindow(170);
	}
	
	public void sendBackgroundInterfaceOverGameWindow(int id) {
		sendSub(Sub.FULLSCREEN_BG, id);
	}

	public void sendForegroundInterfaceOverGameWindow(int id) {
		sendSub(Sub.FULLSCREEN_BG2, id);
	}
	
	public void closeInterfacesOverGameWindow() {
		removeSubs(Sub.FULLSCREEN_BG, Sub.FULLSCREEN_BG2);
	}

	public void removeOverlay(boolean fullGameWindow) {
		removeSub(fullGameWindow ? Sub.FULL_GAMESPACE_BG2 : Sub.MINIGAME_HUD);
	}

	public void removeOverlay() {
		removeOverlay(false);
	}

	public void sendOverlay(int id, boolean fullGameWindow) {
		sendSub(fullGameWindow ? Sub.FULL_GAMESPACE_BG2 : Sub.MINIGAME_HUD, id);
	}

	public void sendOverlay(int id) {
		sendOverlay(id, false);
	}
}
