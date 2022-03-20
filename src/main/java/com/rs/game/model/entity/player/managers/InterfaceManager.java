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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.rs.game.content.achievements.Achievement;
import com.rs.game.content.achievements.AchievementInterface;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;

public class InterfaceManager {

	public static final int FIXED_TOP = 548;
	public static final int RESIZEABLE_TOP = 746;
	public static final int CHATBOX_TOP = 752;
	public static final int CHATBOX_SUB = 13;
	
	private Player player;

	private final Map<Integer, Integer> openedInterfaces = new ConcurrentHashMap<>();

	private boolean resizableScreen;
	private int top;
	
	public enum Sub {
						/* GAME WINDOWS */
						CENTRAL(29, 44),
						FULL_GAMESPACE2(1, 3),
						FULL_GAMESPACE(12, 3), //TODO conflict FULL_GAMESPACE
						FADING(13, 15), 
						
						ABOVE_CHATBOX(25, 42), //Tutorial island
						
						LEVEL_UP(44, 28),
						FULLSCREEN_BG(44, 249), //TODO conflict LEVEL_UP
						FULLSCREEN_BG_FG(45, 204),
						
						
						ACHIEVEMENT_NOTIF(13, 271), //TODO conflict FADING
		
						/* HUD */
						CHATBOX(168, 22, CHATBOX_TOP),
						CHATBAR_SETTINGS(53, 23, 751),
						MULTICOMBAT_SIGN(40, 16, 745),
						SYSTEM_UPDATE(42, 25, 754),
						LOGOUT(194, 130, 182),
						XP_DROPS(39, 16) {
							@Override
							public int getDefaultInterfaceId(Player p) {
								return p.getSkills().xpDropsActive() ? 1213 : -1;
							}
						},
						XP_COUNTER(28, 29) {
							@Override
							public int getDefaultInterfaceId(Player p) {
								return p.getSkills().xpCounterOpen() ? 1215 : -1;
							}
						},
		
						/* TABS */
		/* CONFIRMED */	ORB_HP(160, 196, 748),
		/* CONFIRMED */	ORB_PRAYER(161, 197, 749),
		/* CONFIRMED */	ORB_SUMMONING(164, 199, 747),
		/* CONFIRMED */	ORB_RUN(198, 162, 750, p -> p.sendRunButtonConfig()),
		/* CONFIRMED */	ORB_MONEYPOUCH(167, 208),
		
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
								return p.getCombatDefinitions().getSpellBook();
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
		
		public int getComponent(InterfaceManager mgr) {
			return mgr.hasRezizableScreen() ? resizeable : fixed;
		}
		
		public boolean isClicked(int interfaceId, int componentId) {
			return (interfaceId == RESIZEABLE_TOP && componentId == resizeable) || (interfaceId == FIXED_TOP && componentId == fixed);
		}

		public int getDefaultInterfaceId(Player player) {
			return defaultInter;
		}
	}

	public InterfaceManager(Player player) {
		this.player = player;
	}
	
	public void sendGameWindowSub(int resizable, int fixed, int interfaceId, boolean clickThrough) {
		sendSubSpecific(clickThrough, resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, resizableScreen ? resizable : fixed, interfaceId);
	}

	public void removeGameWindowSub(int resizableComponentId, int fixedComponentId) {
		removeSubSpecific(resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, resizableScreen ? resizableComponentId : fixedComponentId);
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
		return isSubOpen(resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, componentId);
	}
	
	private void sendSubSpecific(boolean clickThrough, int parentInterfaceId, int parentInterfaceComponentId, int interfaceId) {
		int parentComponentUID = getComponentUId(parentInterfaceId, parentInterfaceComponentId);
		getInterfaceParentId(interfaceId);

		Integer oldInterface = openedInterfaces.get(parentComponentUID);
		if (oldInterface != null)
			clearChilds(oldInterface);

		openedInterfaces.put(parentComponentUID, interfaceId);
		player.getPackets().sendInterface(clickThrough, parentInterfaceId, parentInterfaceComponentId, interfaceId);
	}

	public boolean topOpen(int interfaceId) {
		if (interfaceId == top)
			return true;
		for (int value : openedInterfaces.values())
			if (value == interfaceId)
				return true;
		return false;
	}

	public void removeAll() {
		openedInterfaces.clear();
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
		setDefaultTopInterface();
		sendChatBoxInterface(9, 137);
		if (player.getFamiliar() != null && player.isRunning())
			player.getFamiliar().unlock();
		for (Sub sub : Sub.values())
			sendSubDefault(sub);
		player.getControllerManager().sendInterfaces();
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
		sendSub(Sub.TAB_INVENTORY, interfaceId, false);
	}

	public boolean containsReplacedChatBoxInter() {
		return isSubOpen(752, 11);
	}

	public void replaceRealChatBoxInterface(int interfaceId) {
		sendSubSpecific(true, 752, 11, interfaceId);
	}

	public void closeReplacedRealChatBoxInterface() {
		removeSubSpecific(752, 11);
	}

	public void setDefaultTopInterface() {
		setTopInterface(resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, false);
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
		return isSubOpenWindow(resizableScreen ? Sub.CENTRAL.resizeable : Sub.CENTRAL.fixed);
	}

	public boolean containsInventoryInter() {
		return isSubOpenWindow(Sub.TAB_INVENTORY.getComponent(this));
	}

	public void removeInventoryInterface() {
		this.removeSub(Sub.TAB_INVENTORY);
	}

	public boolean removeTab(int tabId) {
		return openedInterfaces.remove(tabId) != null;
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

	public void removeSubSpecific(int parentInterfaceId, int parentInterfaceComponentId) {
		removeInterfaceByParent(getComponentUId(parentInterfaceId, parentInterfaceComponentId));
	}

	public void removeInterfaceByParent(int parentUID) {
		Integer removedInterface = openedInterfaces.remove(parentUID);
		if (removedInterface != null) {
			clearChilds(removedInterface);
			player.getPackets().closeInterface(parentUID);
		}
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

	public void setFadingInterface(int backgroundInterface) {
		setSubSpecific(hasRezizableScreen() ? RESIZEABLE_FADING_INTERFACE : FIXED_FADING_INTERFACE, backgroundInterface);
	}

	public void closeFadingInterface() {
		removeSubSpecific(hasRezizableScreen() ? RESIZEABLE_FADING_INTERFACE : FIXED_FADING_INTERFACE);
	}

	public void removeScreenInterface() {
		removeSubSpecific(resizableScreen ? RESIZEABLE_CENTRAL_SUB : FIXED_CENTRAL_SUB);
	}

	public void setScreenInterface(int backgroundInterface, int interfaceId) {
		removeScreenInterface();
		sendSubSpecific(false, hasRezizableScreen() ? RESIZEABLE_TOP : FIXED_TOP, hasRezizableScreen() ? 44 : 249, backgroundInterface);
		sendSubSpecific(false, hasRezizableScreen() ? RESIZEABLE_TOP : FIXED_TOP, hasRezizableScreen() ? 45 : 204, interfaceId);

		player.setCloseInterfacesEvent(() -> {
			removeSubSpecific(hasRezizableScreen() ? 44 : 249);
			removeSubSpecific(hasRezizableScreen() ? 45 : 204);
		});
	}

	public boolean hasRezizableScreen() {
		return resizableScreen;
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

}
