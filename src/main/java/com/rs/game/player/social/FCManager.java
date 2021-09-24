package com.rs.game.player.social;

import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class FCManager {
	
	public static ButtonClickHandler handle = new ButtonClickHandler(1108, 1109) {
		@Override
		public void handle(ButtonClickEvent e) {
			handleFCInterfaces(e.getInterfaceId(), e.getComponentId(), e.getPacket());
		}
	};

	public static void handleFCInterfaces(int interfaceId, int componentId, ClientPacket packet) {
		if (interfaceId == 1109) {
			if (componentId == 26) {
				if (player.getCurrentFriendChat() != null)
					player.getCurrentFriendChat().leaveChat(player, false);
			} else if (componentId == 31) {
				if (player.getInterfaceManager().containsScreenInter()) {
					player.sendMessage("Please close the interface you have opened before using Friends Chat setup.");
					return;
				}
				player.stopAll();
				openFriendChatSetup();
			} else if (componentId == 19) {
				player.toggleLootShare();
			}
		} else if (interfaceId == 1108) {
			if (componentId == 1) {
				if (packet == ClientPacket.IF_OP1) {
					player.getPackets().sendRunScriptReverse(109, new Object[] { "Enter chat prefix:" });
				} else if (packet == ClientPacket.IF_OP2) {
					if (chatName != null) {
						chatName = null;
						refreshChatName();
						FriendChatsManager.destroyChat(player);
					}
				}
			} else if (componentId == 2) {
				if (packet == ClientPacket.IF_OP1)
					whoCanEnterChat = -1;
				else if (packet == ClientPacket.IF_OP2)
					whoCanEnterChat = 0;
				else if (packet == ClientPacket.IF_OP3)
					whoCanEnterChat = 1;
				else if (packet == ClientPacket.IF_OP4)
					whoCanEnterChat = 2;
				else if (packet == ClientPacket.IF_OP5)
					whoCanEnterChat = 3;
				else if (packet == ClientPacket.IF_OP6)
					whoCanEnterChat = 4;
				else if (packet == ClientPacket.IF_OP7)
					whoCanEnterChat = 5;
				else if (packet == ClientPacket.IF_OP8)
					whoCanEnterChat = 6;
				else if (packet == ClientPacket.IF_OP9)
					whoCanEnterChat = 7;
				refreshWhoCanEnterChat();
			} else if (componentId == 3) {
				if (packet == ClientPacket.IF_OP1)
					whoCanTalkOnChat = -1;
				else if (packet == ClientPacket.IF_OP2)
					whoCanTalkOnChat = 0;
				else if (packet == ClientPacket.IF_OP3)
					whoCanTalkOnChat = 1;
				else if (packet == ClientPacket.IF_OP4)
					whoCanTalkOnChat = 2;
				else if (packet == ClientPacket.IF_OP5)
					whoCanTalkOnChat = 3;
				else if (packet == ClientPacket.IF_OP6)
					whoCanTalkOnChat = 4;
				else if (packet == ClientPacket.IF_OP7)
					whoCanTalkOnChat = 5;
				else if (packet == ClientPacket.IF_OP8)
					whoCanTalkOnChat = 6;
				else if (packet == ClientPacket.IF_OP9)
					whoCanTalkOnChat = 7;
				refreshWhoCanTalkOnChat();
			} else if (componentId == 4) {
				if (packet == ClientPacket.IF_OP1)
					whoCanKickOnChat = -1;
				else if (packet == ClientPacket.IF_OP2)
					whoCanKickOnChat = 0;
				else if (packet == ClientPacket.IF_OP3)
					whoCanKickOnChat = 1;
				else if (packet == ClientPacket.IF_OP4)
					whoCanKickOnChat = 2;
				else if (packet == ClientPacket.IF_OP5)
					whoCanKickOnChat = 3;
				else if (packet == ClientPacket.IF_OP6)
					whoCanKickOnChat = 4;
				else if (packet == ClientPacket.IF_OP7)
					whoCanKickOnChat = 5;
				else if (packet == ClientPacket.IF_OP8)
					whoCanKickOnChat = 6;
				else if (packet == ClientPacket.IF_OP9)
					whoCanKickOnChat = 7;
				refreshWhoCanKickOnChat();
				FriendChatsManager.refreshChat(player);
			} else if (componentId == 5) {
				if (packet == ClientPacket.IF_OP1)
					whoCanShareloot = -1;
				else if (packet == ClientPacket.IF_OP2)
					whoCanShareloot = 0;
				else if (packet == ClientPacket.IF_OP3)
					whoCanShareloot = 1;
				else if (packet == ClientPacket.IF_OP4)
					whoCanShareloot = 2;
				else if (packet == ClientPacket.IF_OP5)
					whoCanShareloot = 3;
				else if (packet == ClientPacket.IF_OP6)
					whoCanShareloot = 4;
				else if (packet == ClientPacket.IF_OP7)
					whoCanShareloot = 5;
				else if (packet == ClientPacket.IF_OP8)
					whoCanShareloot = 6;
				refreshWhoCanShareloot();
			}
		}
	}
}
