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
package com.rs.game.player.managers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.achievements.Achievement;
import com.rs.game.player.content.achievements.AchievementInterface;
import com.rs.lib.util.Utils;

public class InterfaceManager {

	public static final int FIXED_TOP = 548;
	public static final int FIXED_CENTRAL_SUB = 44;
	public static final int FIXED_INVENTORY_SUB = 172;
	public static final int FIXED_OVERLAY_SUB = 3;

	public static final int RESIZEABLE_TOP = 746;
	public static final int RESIZEABLE_CENTRAL_SUB = 29;
	public static final int RESIZEABLE_OVERLAY_SUB = 12;
	public static final int FULL_SCREEN_OVERLAY_SUB = 11;
	public static final int RESIZEABLE_INVENTORY_SUB = 109;

	public static final int CHATBOX_WINDOW = 752;
	public static final int REAL_CHATBOX_TAB = 11;
	public static final int CHATBOX_TAB = 13;

	private Player player;

	private final ConcurrentHashMap<Integer, Integer> openedInterfaces = new ConcurrentHashMap<>();

	private boolean resizableScreen;
	private int top;

	public enum Tab {
		COMBAT(112, 176, 884, p -> p.getCombatDefinitions().sendUnlockAttackStylesButtons()),
		ACHIEVEMENT(113, 177, 1056, p -> AchievementInterface.init(p)),
		SKILLS(114, 178, 320),
		QUEST(115, 179, 190, p -> p.getQuestManager().unlockQuestTabOptions()),
		INVENTORY(116, 180, Inventory.INVENTORY_INTERFACE, p -> p.getInventory().unlockInventoryOptions()),
		EQUIPMENT(117, 181, 387),
		PRAYER(118, 182, 271, p -> p.getPrayer().unlockPrayerBookButtons()),
		MAGIC(119, 183, -1, p -> {
			p.getInterfaceManager().setWindowInterface(119, 183, p.getCombatDefinitions().getSpellBook());
		}),
		MISC(120, 184, -1),
		FRIENDS(121, 185, 550),
		FRIENDS_CHAT(122, 186, 1109),
		CLAN_CHAT(123, 187, 1110),
		SETTINGS(124, 188, 261),
		EMOTES(125, 189, 590, p -> p.getEmotesManager().unlockEmotesBook()),
		MUSIC(126, 190, 187, p -> p.getMusicsManager().unlockMusicPlayer()),
		NOTES(127, 191, 34),
		RUN(198, 162, 750, p -> p.sendRunButtonConfig()),
		NONE(-1, -1, -1);

		private int resizable;
		private int fixed;
		private int defaultInterfaceId;
		private Consumer<Player> defaultProcedure;

		Tab(int resizable, int fixed, int defaultInterfaceId, Consumer<Player> defaultProcedure) {
			this.resizable = resizable;
			this.fixed = fixed;
			this.defaultInterfaceId = defaultInterfaceId;
			this.defaultProcedure = defaultProcedure;
		}

		Tab(int resizable, int fixed, int defaultInterfaceId) {
			this(resizable, fixed, defaultInterfaceId, (p) -> {});
		}

		public boolean isClicked(int interfaceId, int componentId) {
			if (interfaceId == RESIZEABLE_TOP && (componentId-75) == ordinal())
				return true;
			else if (interfaceId == FIXED_TOP) {
				if (componentId >= 112 && componentId <= 119)
					return (componentId-112) == ordinal();
				else if (componentId >= 83 && componentId <= 90)
					return (componentId-75) == ordinal();
			}
			return false;
		}

		public int getComponent(InterfaceManager mgr) {
			return mgr.hasRezizableScreen() ? resizable : fixed;
		}
	}

	public InterfaceManager(Player player) {
		this.player = player;
	}

	public boolean containsInterfaceAtParent(int parentInterfaceId, int parentInterfaceComponentId) {
		return openedInterfaces.containsKey(getComponentUId(parentInterfaceId, parentInterfaceComponentId));
	}
	
	public void closeTabs(Tab... tabs) {
		for (Tab tab : tabs)
			closeTab(tab);
	}
	
	public void closeTab(Tab tab) {
		removeWindowInterface(tab.resizable, tab.fixed);
	}
	
	public void flashTab(Tab tab) {
		player.getVars().setVar(1021, tab == Tab.NONE ? 0 : (tab.ordinal()+1));
	}

	public boolean isTabClick(Tab tab, int interfaceId, int componentId) {
		return tab.isClicked(interfaceId, componentId);
	}

	public void sendTab(Tab tab) {
		sendTab(tab, tab.defaultInterfaceId);
		tab.defaultProcedure.accept(player);;
	}
	
	public void sendTab(Tab tab, int interfaceId) {
		if (interfaceId == -1)
			return;
		setWindowInterface(tab.resizable, tab.fixed, interfaceId);
	}
	
	public void sendTabs(Tab... tabs) {
		for (Tab tab : tabs)
			sendTab(tab);
	}
	
	public void setWindowInterface(int resizable, int fixed, int interfaceId) {
		setInterface(true, resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, resizableScreen ? resizable : fixed, interfaceId);
	}

	public void setWindowInterface(int componentId, int interfaceId) {
		setInterface(true, resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, componentId, interfaceId);
	}
	
	public void setWindowInterfaceNoOverlay(int componentId, int interfaceId) {
		setInterface(false, resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, componentId, interfaceId);
	}
	
    public void removeWindowInterface(int resizableComponentId, int fixedComponentId) {
    	removeInterfaceByParent(resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, resizableScreen ? resizableComponentId : fixedComponentId);
    }
	
    public void removeWindowInterface(int componentId) {
    	removeInterfaceByParent(resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, componentId);
    }

    public void sendChatBoxInterface(int interfaceId) {
		setInterface(true, CHATBOX_WINDOW, CHATBOX_TAB, interfaceId);
	}
    
    public void sendChatBoxInterface(int componentId, int interfaceId) {
		setInterface(true, CHATBOX_WINDOW, componentId, interfaceId);
	}

	public void closeChatBoxInterface() {
		removeInterfaceByParent(CHATBOX_WINDOW, CHATBOX_TAB);
	}

	public boolean containsChatBoxInter() {
		return containsInterfaceAtParent(CHATBOX_WINDOW, CHATBOX_TAB);
	}
	
	public void setOverlay(int interfaceId) {
		setOverlay(interfaceId, false);
	}

	public void setOverlay(int interfaceId, boolean fullScreen) {
		setWindowInterface(resizableScreen ? fullScreen ? FULL_SCREEN_OVERLAY_SUB : RESIZEABLE_OVERLAY_SUB : FIXED_OVERLAY_SUB, interfaceId);
	}

    /**
     * Plays over the window, despite screen type.
     * @param interfaceId
     */
    public void sendBackgroundInterfaceOverGameWindow(int interfaceId) {
        if(player.getInterfaceManager().hasRezizableScreen())
            setFadingInterface(interfaceId);
        else
            sendInterface(interfaceId);
    }

    /**
     * Plays over the window, despite screen type.
     * @param interfaceId
     */
    public void sendForegroundInterfaceOverGameWindow(int interfaceId) {
        if(player.getInterfaceManager().hasRezizableScreen())
            sendInterface(interfaceId);
        else
            setOverlay(interfaceId);
    }

    /**
     * Closes over the window, despite screen type.
     */
    public void closeInterfacesOverGameWindow() {
        if(player.getInterfaceManager().hasRezizableScreen()) {
            player.closeInterfaces();
            closeFadingInterface();
        }
        else {
            removeOverlay();
            removeWindowInterface(44);
            removeWindowInterface(3);
        }
    }

	public void removeOverlay() {
		removeOverlay(false);
	}

	public void removeOverlay(boolean fullScreen) {
		removeWindowInterface(resizableScreen ? fullScreen ? FULL_SCREEN_OVERLAY_SUB : RESIZEABLE_OVERLAY_SUB : FIXED_OVERLAY_SUB);
	}

	public void sendInterface(int interfaceId) {
		if (interfaceId > Utils.getInterfaceDefinitionsSize())
			return;
		setInterface(false, resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, resizableScreen ? RESIZEABLE_CENTRAL_SUB : FIXED_CENTRAL_SUB, interfaceId);
	}

    public void sendInterface(int interfaceId, boolean overlay) {
        if (interfaceId > Utils.getInterfaceDefinitionsSize())
            return;
        setInterface(overlay, resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, resizableScreen ? RESIZEABLE_CENTRAL_SUB : FIXED_CENTRAL_SUB, interfaceId);
    }

	public void sendInventoryInterface(int interfaceId) {
		setInterface(false, resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, resizableScreen ? RESIZEABLE_INVENTORY_SUB : FIXED_INVENTORY_SUB, interfaceId);
	}

	public final void sendInterfaces() {
		if (player.getDisplayMode() == 2 || player.getDisplayMode() == 3) {
			resizableScreen = true;
			sendResizeableInterfaces();
		} else {
			resizableScreen = false;
			sendFixedInterfaces();
		}
		sendChatBoxInterface(9, 137);
		player.getSkills().sendInterfaces();
		if (player.getFamiliar() != null && player.isRunning())
			player.getFamiliar().unlock();
		
		for (Tab tab : Tab.values()) {
			sendTab(tab);
		}
		player.getControllerManager().sendInterfaces();
	}

    public boolean containsReplacedChatBoxInter() {
	return containsInterfaceAtParent(752, 11);
    }
    
    public void replaceRealChatBoxInterface(int interfaceId) {
    	setInterface(true, 752, 11, interfaceId);
    }

    public void closeReplacedRealChatBoxInterface() {
    	removeInterfaceByParent(752, 11);
    }

    public void setDefaultTopInterface() {
    	setTopInterface(resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, false);
    }

    /**
     * On top of even inventory
     */
    public void setTopInterface(int rootInterface, boolean gc) {
		this.top = rootInterface;
		player.getPackets().sendWindowsPane(rootInterface, gc ? 3 : 0);
	}

	public void setWindowsPane(int windowsPane) {
		this.top = windowsPane;
		player.getPackets().sendWindowsPane(this.top, 2);
	}
	
	public void sendAchievementComplete(Achievement achievement) {
		if (achievement == null)
			return;
		if (resizableScreen)
			setWindowInterface(13, 1055);
		else
			setWindowInterface(271, 1055);
		player.getPackets().sendVarc(1425, achievement.getId());
	}

	public void sendResizeableInterfaces() {
		setDefaultTopInterface();
		setWindowInterface(22, CHATBOX_WINDOW);
		setWindowInterface(23, 751);
		setWindowInterface(16, 745);
		setWindowInterface(25, 754);
		setWindowInterface(196, 748);
		setWindowInterface(197, 749);
		setWindowInterface(199, 747);
		setWindowInterface(130, 182); //logout

		player.getPackets().setIFHidden(RESIZEABLE_TOP, 208, true); //money pouch
	}

	public void sendFixedInterfaces() {
		setDefaultTopInterface();
		setWindowInterface(168, CHATBOX_WINDOW);
		setWindowInterface(53, 751);
		setWindowInterface(40, 745); //multicombat indicator
		setWindowInterface(42, 754); //system update
		setWindowInterface(164, 747);
		setWindowInterface(160, 748);
		setWindowInterface(161, 749);
		setWindowInterface(194, 182); //logout

		player.getPackets().setIFHidden(FIXED_TOP, 167, true); //money pouch
	}

	public void sendXPPopup() {
		setWindowInterface(resizableScreen ? 39 : 16, 1213); // xp
	}

	public void sendXPDisplay() {
		sendXPDisplay(1215); // xp counter
	}

	public void sendXPDisplay(int interfaceId) {
		setWindowInterface(resizableScreen ? 28 : 29, interfaceId); // xp counter
	}

	public void closeXPPopup() {
		removeWindowInterface(39, 16);
	}

	public void closeXPDisplay() {
		removeWindowInterface(28, 29);
	}
	
	public void setInterface(boolean overlay, int parentInterfaceId, int parentInterfaceComponentId, int interfaceId) {
		int parentComponentUID = getComponentUId(parentInterfaceId, parentInterfaceComponentId);
		getInterfaceParentId(interfaceId);

		Integer oldInterface = openedInterfaces.get(parentComponentUID);
        if (oldInterface != null)
			clearChilds(oldInterface);

		openedInterfaces.put(parentComponentUID, interfaceId);
		player.getPackets().sendInterface(overlay, parentInterfaceId, parentInterfaceComponentId, interfaceId);
	}

	public boolean containsInterface(int interfaceId) {
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
	
    public boolean containsWindowInterfaceAtParent(int componentId) {
    	return containsInterfaceAtParent(resizableScreen ? RESIZEABLE_TOP : FIXED_TOP, componentId);
    }

    public boolean containsScreenInter() {
    	return containsWindowInterfaceAtParent(resizableScreen ? RESIZEABLE_CENTRAL_SUB : FIXED_CENTRAL_SUB);
    }

	public boolean containsInventoryInter() {
		return containsWindowInterfaceAtParent(resizableScreen ? RESIZEABLE_INVENTORY_SUB : FIXED_INVENTORY_SUB);
	}

	public void removeInventoryInterface() {
		removeWindowInterface(RESIZEABLE_INVENTORY_SUB, FIXED_INVENTORY_SUB);
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

	public void removeInterfaceByParent(int parentInterfaceId, int parentInterfaceComponentId) {
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
		for (int key : openedInterfaces.keySet()) {
			if (key >> 16 == parentInterfaceId)
				openedInterfaces.remove(key);
		}
	}

	public void removeInterface(int interfaceId) {
		int parentUID = getInterfaceParentId(interfaceId);
		if (parentUID == -1)
			return;
		removeInterfaceByParent(parentUID);
	}

    /**
     * Fading interface is a misnomer, just means under inventory/chat/map
     */
	public void setFadingInterface(int backgroundInterface) {
		setWindowInterface(hasRezizableScreen() ? 13 : 15, backgroundInterface);
	}

	public void closeFadingInterface() {
		removeWindowInterface(hasRezizableScreen() ? 13 : 15);
	}
	
	public void removeScreenInterface() {
		removeWindowInterface(resizableScreen ? RESIZEABLE_CENTRAL_SUB : FIXED_CENTRAL_SUB);
	}

	public void setScreenInterface(int backgroundInterface, int interfaceId) {
		removeScreenInterface();
		setInterface(false, hasRezizableScreen() ? RESIZEABLE_TOP : FIXED_TOP, hasRezizableScreen() ? 44 : 249, backgroundInterface);
		setInterface(false, hasRezizableScreen() ? RESIZEABLE_TOP : FIXED_TOP, hasRezizableScreen() ? 45 : 204, interfaceId);

		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				removeWindowInterface(hasRezizableScreen() ? 44 : 249);
				removeWindowInterface(hasRezizableScreen() ? 45 : 204);
			}
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
		setInterface(true, 475, 57, 751);
		setInterface(true, 475, 55, 752);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				setDefaultTopInterface();
				player.getPackets().sendResetCamera();
			}

		});
	}

	public void openGameTab(Tab tab) {
		if (tab == null)
			return;
		player.getPackets().sendVarc(168, tab.ordinal());
	}

}
