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
package com.rs.game.player.content.clans;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.statements.SimpleStatement;
import com.rs.game.player.content.dialogue.statements.Statement;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.lib.game.WorldTile;
import com.rs.lib.model.MemberData;
import com.rs.lib.net.packets.decoders.lobby.CCCheckName;
import com.rs.lib.net.packets.decoders.lobby.CCCreate;
import com.rs.lib.net.packets.decoders.lobby.CCJoin;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class ClansManager {

	public static void clanMotto(Player player) {
		player.startConversation(new Dialogue(new Statement() {
			@Override
			public void send(Player player) {
				player.sendInputLongText("Enter clan motto:", motto -> player.getClan().setMotto(motto));
				player.getInterfaceManager().sendChatBoxInterface(1103);
			}

			@Override
			public int getOptionId(int componentId) {
				return 0;
			}
		}));
	}

	public static ButtonClickHandler handleClanChatButtons = new ButtonClickHandler(1110) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().sendMessage("handleClanChatButtons: " + e.getComponentId() + " - " + e.getSlotId() + " - " + e.getPacket());
			switch(e.getComponentId()) {
				case 82 -> {
					if (e.getPlayer().getSocial().isConnectedToClan())
						leaveChannel(e.getPlayer(), null);
					else
						joinChannel(e.getPlayer(), null);
				}
				case 91 -> {
					if (e.getPlayer().getSocial().getGuestedClanChat() != null)
						leaveChannel(e.getPlayer(), e.getPlayer().getSocial().getGuestedClanChat());
					else
						e.getPlayer().sendInputName("Which clan chat would you like to join?",
								"Please enter the name of the clan whose Clan chat you wish to join as a guest. <br><br>To talk as a guest, start  your<br>line<br>of chat with ///",
								name -> joinChannel(e.getPlayer(), name));
				}
				case 95 -> e.getPlayer().sendInputName("Which player would you like to ban?", name -> banPlayer(e.getPlayer(), name));
				case 78 -> openSettings(e.getPlayer());
				case 75 -> openDetails(e.getPlayer());
				case 109 -> leaveClan(e.getPlayer());
				case 11 -> unban(e.getPlayer(), e.getSlotId());
				case 99 -> e.getPlayer().sendInputName("Which player would you like to unban?", name -> unban(e.getPlayer(), Utils.formatPlayerNameForDisplay(name)));
			}
		}

		private void joinChannel(Player player, String guestClanName) {
			if (guestClanName == null && player.getSocial().getClanName() == null) {
				createClan(player);
				return;
			}
			if (player.getTempAttribs().getB("ccJoinLock")) {
				player.sendMessage("Please wait...");
				return;
			}
			player.getTempAttribs().setB("ccJoinLock", true);
			LobbyCommunicator.forwardPacket(player, new CCJoin(guestClanName), cb -> {
				player.getTempAttribs().removeB("ccJoinLock");
			});
		}

		private void createClan(Player player) {
			player.startConversation(new Conversation(new Dialogue(new SimpleStatement("You are not currently in a clan. Would you like to create one?")))
					.addOption("Create a clan?", "Yes", "Not right now.")
					.addNext(() -> promptName(player)));
		}

		private void leaveChannel(Player player, String guestClanName) {
			// TODO Auto-generated method stub
		}

		private void leaveClan(Player player) {
			// TODO Auto-generated method stub
		}

		private void banPlayer(Player player, String name) {
			// TODO Auto-generated method stub
		}

		private void unban(Player player, String displayName) {
			// TODO Auto-generated method stub
		}

		private void unban(Player player, int slotId) {
			// TODO Auto-generated method stub
		}

		private void openDetails(Player player) {
			// TODO Auto-generated method stub
		}

		private void openSettings(Player player) {
			// TODO Auto-generated method stub
		}
	};
	
	public static void create(Player player, String name) {
		player.sendOptionDialogue("The name " + name + " is available. Create the clan?", new String[] { "Yes, create " + name + ".", "No, I want to pick another name." }, new DialogueOptionEvent() {
			@Override
			public void run(Player player) {
				if (getOption() == 1) {
					LobbyCommunicator.forwardPacket(player, new CCCreate(name), cb -> {
						
					});
				} else {
					
				}
			}
		});
	}
	
	public static void promptName(Player player) {
		player.sendInputName("Which name would you like for your clan?", name -> {
			if (player.getTempAttribs().getB("ccCreateLock")) {
				player.simpleDialogue("Your previous request to create a clan is still in progress... Please wait.");
				return;
			}
			player.getTempAttribs().setB("ccCreateLock", true);
			LobbyCommunicator.forwardPacket(player, new CCCheckName(name, false), cb -> {
				player.getTempAttribs().removeB("ccCreateLock");
			});
		});
	}

	public static ButtonClickHandler handleClanFlagButtons = new ButtonClickHandler(1089) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().sendMessage("handleClanFlagButtons: " + e.getComponentId() + " - " + e.getSlotId() + " - " + e.getPacket());
			//			if (e.getComponentId() == 30)
			//				e.getPlayer().getTemporaryAttributes().put("clanflagselection", e.getSlotId());
			//			else if (e.getComponentId() == 26) {
			//				Integer flag = (Integer) e.getPlayer().getTemporaryAttributes().remove("clanflagselection");
			//				e.getPlayer().stopAll();
			//				if (flag != null)
			//					ClansManager.setClanFlagInterface(e.getPlayer(), flag);
			//			}
		}
	};

	public static ButtonClickHandler handleClanSettingsButtonsMain = new ButtonClickHandler(1096) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().sendMessage("handleClanSettingsButtonsMain: " + e.getComponentId() + " - " + e.getSlotId() + " - " + e.getPacket());
			//			if (e.getComponentId() == 41)
			//				ClansManager.viewClammateDetails(e.getPlayer(), e.getSlotId());
			//			else if (e.getComponentId() == 94)
			//				ClansManager.switchGuestsInChatCanEnterInterface(e.getPlayer());
			//			else if (e.getComponentId() == 95)
			//				ClansManager.switchGuestsInChatCanTalkInterface(e.getPlayer());
			//			else if (e.getComponentId() == 96)
			//				ClansManager.switchRecruitingInterface(e.getPlayer());
			//			else if (e.getComponentId() == 97)
			//				ClansManager.switchClanTimeInterface(e.getPlayer());
			//			else if (e.getComponentId() == 124)
			//				ClansManager.openClanMottifInterface(e.getPlayer());
			//			else if (e.getComponentId() == 131)
			//				ClansManager.openClanMottoInterface(e.getPlayer());
			//			else if (e.getComponentId() == 240)
			//				ClansManager.setTimeZoneInterface(e.getPlayer(), -720 + e.getSlotId() * 10);
			//			else if (e.getComponentId() == 262)
			//				e.getPlayer().getTemporaryAttributes().put("editclanmatejob", e.getSlotId());
			//			else if (e.getComponentId() == 276)
			//				e.getPlayer().getTemporaryAttributes().put("editclanmaterank", e.getSlotId());
			//			else if (e.getComponentId() == 309)
			//				ClansManager.kickClanmate(e.getPlayer());
			//			else if (e.getComponentId() == 318)
			//				ClansManager.saveClanmateDetails(e.getPlayer());
			//			else if (e.getComponentId() == 290)
			//				ClansManager.setWorldIdInterface(e.getPlayer(), e.getSlotId());
			//			else if (e.getComponentId() == 297)
			//				ClansManager.openForumThreadInterface(e.getPlayer());
			//			else if (e.getComponentId() == 346)
			//				ClansManager.openNationalFlagInterface(e.getPlayer());
			//			else if (e.getComponentId() == 113)
			//				ClansManager.showClanSettingsClanMates(e.getPlayer());
			//			else if (e.getComponentId() == 120)
			//				ClansManager.showClanSettingsSettings(e.getPlayer());
			//			else if (e.getComponentId() == 386)
			//				ClansManager.showClanSettingsPermissions(e.getPlayer());
			//			else if (e.getComponentId() >= 395 && e.getComponentId() <= 475) {
			//				int selectedRank = (e.getComponentId() - 395) / 8;
			//				if (selectedRank == 10)
			//					selectedRank = 125;
			//				else if (selectedRank > 5)
			//					selectedRank = 100 + selectedRank - 6;
			//				ClansManager.selectPermissionRank(e.getPlayer(), selectedRank);
			//			} else if (e.getComponentId() == 489)
			//				ClansManager.selectPermissionTab(e.getPlayer(), 1);
			//			else if (e.getComponentId() == 498)
			//				ClansManager.selectPermissionTab(e.getPlayer(), 2);
			//			else if (e.getComponentId() == 506)
			//				ClansManager.selectPermissionTab(e.getPlayer(), 3);
			//			else if (e.getComponentId() == 514)
			//				ClansManager.selectPermissionTab(e.getPlayer(), 4);
			//			else if (e.getComponentId() == 522)
			//				ClansManager.selectPermissionTab(e.getPlayer(), 5);
		}
	};

	public static ButtonClickHandler handleMottifButtons = new ButtonClickHandler(1105) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().sendMessage("handleMottifButtons: " + e.getComponentId() + " - " + e.getSlotId() + " - " + e.getPacket());
			//			if (e.getComponentId() == 63 || e.getComponentId() == 66)
			//				ClansManager.setClanMottifTextureInterface(e.getPlayer(), e.getComponentId() == 66, e.getSlotId());
			//			else if (e.getComponentId() == 35)
			//				ClansManager.openSetMottifColor(e.getPlayer(), 0);
			//			else if (e.getComponentId() == 80)
			//				ClansManager.openSetMottifColor(e.getPlayer(), 1);
			//			else if (e.getComponentId() == 92)
			//				ClansManager.openSetMottifColor(e.getPlayer(), 2);
			//			else if (e.getComponentId() == 104)
			//				ClansManager.openSetMottifColor(e.getPlayer(), 3);
			//			else if (e.getComponentId() == 120)
			//				e.getPlayer().stopAll();
		}
	};

	public static ButtonClickHandler handleCloseButton = new ButtonClickHandler(1079) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().closeInterfaces();
		}
	};

	public static ItemClickHandler handleClanVex = new ItemClickHandler(new Object[] { 20709 }, new String[] { "Teleport" }) {
		@Override
		public void handle(ItemClickEvent e) {
			Magic.sendTeleportSpell(e.getPlayer(), 7389, 7312, 537, 538, 0, 0, new WorldTile(2960, 3285, 0), 4, true, Magic.MAGIC_TELEPORT);
		}
	};

	public static void viewClanmateDetails(Player player, String username, MemberData member) {
		player.getPackets().sendVarc(1500, member.getRank().getIconId());
		player.getPackets().sendVarc(1501, member.getJob());
		player.getPackets().sendVarc(1564, 0);
		player.getPackets().sendVarc(1565, member.isBanFromKeep() ? 1 : 0);
		player.getPackets().sendVarc(1566, member.isBanFromCitadel() ? 1 : 0);
		player.getPackets().sendVarc(1567, member.isBanFromIsland() ? 1 : 0);
		player.getPackets().sendVarc(1568, member.firstWeek() ? 1 : 0);
		player.getPackets().sendVarcString(347, username);
		player.getPackets().sendRunScriptBlank(4319);
	}

	public static void unlockBanList(Player player) {
		player.getPackets().setIFRightClickOps(1110, 11, 0, 100, 0);
	}

	//	public ClansManager(Clan clan) {
	//		this.clan = clan;
	//		this.channelPlayers = new ArrayList<Player>();
	//		this.membersOnline = new ArrayList<Player>();
	//		this.bannedChannelPlayers = new HashMap<String, Long>();
	//		generateClanSettingsDataBlock();
	//		generateClanChannelDataBlock();
	//	}
	//
	//	public static void viewClammateDetails(Player player, int index) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		ClanMember member = manager.getMemberByIndex(index);
	//		if (member == null)
	//			return;
	//		viewClanmateDetails(player, member);
	//		player.getTemporaryAttributes().remove("editclanmaterank");
	//		player.getTemporaryAttributes().remove("editclanmatejob");
	//		if (manager.hasRankToEditSettings(player))
	//			player.getTemporaryAttributes().put("editclanmatedetails", member);
	//		else
	//			player.getTemporaryAttributes().remove("editclanmatedetails");
	//	}
	//
	//	public static void kickClanmate(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		ClanMember member = (ClanMember) player.getTemporaryAttributes().remove("editclanmatedetails");
	//		if (member == null) // means u not owner
	//			return;
	//		if (member.getUsername().equals(player.getUsername())) {
	//			player.sendMessage("You can't kick yourself!");
	//			return;
	//		}
	//		if (manager.hasRights(player, ClanRank.OWNER)) {
	//			player.sendMessage("You can't kick leader!");
	//			return;
	//		}
	//		manager.kickPlayer(member);
	//	}
	//
	//	public static void leaveClan(Player player) {
	//		if (player.getClanManager() == null)
	//			return;
	//		player.getDialogueManager().execute(new LeaveClan());
	//	}
	//
	//	public static void leaveClanCompletly(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		ClanMember mate = manager.getMemberByUsername(player.getUsername());
	//		if (mate == null)
	//			return;
	//		manager.kickPlayer(mate);
	//	}
	//
	//	public void kickAllChannelPlayers() {
	//		synchronized (this) {
	//			for (Player player : new ArrayList<Player>(channelPlayers))
	//				disconnect(player, !membersOnline.contains(player));
	//		}
	//	}
	//
	//	public void kickPlayer(ClanMember mate) {
	//		synchronized (cachedClans) {
	//			synchronized (this) {
	//				clan.getMembers().remove(mate);
	//				Player player = World.getPlayer(mate.getUsername());
	//				if (player != null) {
	//					player.setClanName(null); // automaticaly sets to null when
	//					// logins if logged out
	//					player.getClanManager().disconnect(player, false);
	//					player.sendMessage("You're no longer part of a clan.");
	//				}
	//				if (clan.getMembers().isEmpty()) {
	//					kickAllChannelPlayers();
	//					JsonFileManager.deleteClan(clan);
	//				} else {
	//					if (mate.getRank() == ClanRank.OWNER) {
	//						ClanMember newLeader = getHighestRank();
	//						if (newLeader != null) {
	//							newLeader.setRank(ClanRank.OWNER);
	//							clan.setClanLeaderUsername(newLeader);
	//							generateClanChannelDataBlock();
	//							refreshClanChannel();
	//							sendGlobalMessage("<col=7E2217>" + Utils.formatPlayerNameForDisplay(newLeader.getUsername()) + " has been appointed as new leader!");
	//						}
	//					}
	//					generateClanSettingsDataBlock();
	//					refreshClanSettings();
	//				}
	//			}
	//		}
	//	}
	//
	//	public static void saveClanmateDetails(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		ClanMember member = (ClanMember) player.getTemporaryAttributes().get("editclanmatedetails");
	//		if (member == null) // means u not owner
	//			return;
	//		Integer rank = (Integer) player.getTemporaryAttributes().remove("editclanmaterank");
	//		Integer job = (Integer) player.getTemporaryAttributes().remove("editclanmatejob");
	//		if (rank == null && job == null)
	//			return;
	//		synchronized (manager) {
	//			if (rank != null && member.getRank() == ClanRank.OWNER) {
	//				// sets highest rank member to leader
	//				ClanMember newLeader = manager.getDeputyOwner();
	//				if (newLeader == null) {
	//					player.sendMessage("Please select a deputy owner before changing your own rank.");
	//					return;
	//				}
	//				newLeader.setRank(ClanRank.OWNER);
	//				manager.clan.setClanLeaderUsername(newLeader);
	//				manager.sendGlobalMessage("<col=7E2217>" + Utils.formatPlayerNameForDisplay(newLeader.getUsername()) + " has been appointed as new leader!");
	//			}
	//			if (rank != null) {
	//				member.setRank(ClanRank.forId(rank));
	//				player.getPackets().sendVarc(1500, rank);
	//				manager.generateClanChannelDataBlock();
	//				manager.refreshClanChannel();
	//			}
	//			if (job != null) {
	//				member.setJob(job);
	//				player.getPackets().sendVarc(1501, job);
	//			}
	//			manager.generateClanSettingsDataBlock();
	//			manager.refreshClanSettings();
	//		}
	//	}
	//
	//	public void sendGlobalMessage(String message) {
	//		synchronized (this) {
	//			for (Player player : membersOnline)
	//				player.sendMessage(message);
	//		}
	//	}
	//
	//	public ClanMember getHighestRank() {
	//		synchronized (this) {
	//			ClanMember highest = null;
	//			for (ClanMember member : clan.getMembers())
	//				if (highest == null || member.getRank().getIconId() > highest.getRank().getIconId())
	//					highest = member;
	//			return highest;
	//		}
	//	}
	//
	//	public ClanMember getDeputyOwner() {
	//		synchronized (this) {
	//			for (ClanMember member : clan.getMembers())
	//				if (member.getRank() == ClanRank.DEPUTY_OWNER)
	//					return member;
	//			return null;
	//		}
	//	}
	//
	//	public ClanMember getMemberByIndex(int index) {
	//		synchronized (this) {
	//			if (clan.getMembers().size() <= index)
	//				return null;
	//			return clan.getMembers().get(index);
	//		}
	//	}
	//
	//	public ClanMember getMemberByUsername(String username) {
	//		synchronized (this) {
	//			for (ClanMember member : clan.getMembers())
	//				if (member.getUsername().equals(username))
	//					return member;
	//			return null;
	//		}
	//	}
	//
	//	public static void openNationalFlagInterface(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		if (!manager.hasRankToEditSettings(player))
	//			return;
	//		player.getInterfaceManager().sendInterface(1089);
	//		player.getPackets().setIFRightClickOps(1089, 30, 0, 241, 0);
	//	}
	//
	//	public static void openForumThreadInterface(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		if (!manager.hasRankToEditSettings(player))
	//			return;
	//		player.stopAll();
	//		player.getDialogueManager().execute(new ForumThreadId());
	//	}
	//
	//	public static void openClanMottoInterface(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		if (!manager.hasRankToEditSettings(player))
	//			return;
	//		player.stopAll();
	//		player.getDialogueManager().execute(new ClanMotto());
	//	}
	//
	//	public static void openClanMottifInterface(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		if (!manager.hasRankToEditSettings(player))
	//			return;
	//		player.stopAll();
	//		player.getInterfaceManager().sendInterface(1105);
	//		player.getPackets().setIFRightClickOps(1105, 66, 0, 116, 0); // top
	//		player.getPackets().setIFRightClickOps(1105, 63, 0, 116, 0); // button
	//		player.getVars().setVarBit(9086, manager.clan.getMottifTop()+1);
	//		player.getVars().setVarBit(9087, manager.clan.getMottifBottom()+1);
	//		for (int i = 0; i < manager.clan.getMottifColors().length; i++)
	//			player.getVars().setVar(2094 + i, manager.clan.getMottifColors()[i]);
	//	}
	//
	//	public static void openSetMottifColor(Player player, int part) {
	//		player.getTemporaryAttributes().put("MottifCustomize", part);
	//		player.getInterfaceManager().sendInterface(1106);
	//	}
	//
	//	public static void setMottifColor(Player player, int color) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		Integer part = (Integer) player.getTemporaryAttributes().remove("MottifCustomize");
	//		if (part == null)
	//			return;
	//		manager.setMottifColor(player, color, part);
	//	}
	//
	//	public void setMottifColor(Player player, int color, int part) {
	//		synchronized (this) {
	//			player.getVars().setVar(2094 + part, clan.getMottifColors()[part] = color);
	//			refreshAllMembersAppearence();
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//			player.getInterfaceManager().sendInterface(1105);
	//		}
	//	}
	//
	//	public void refreshAllMembersAppearence() {
	//		synchronized (this) {
	//			for (Player player : membersOnline)
	//				player.getAppearance().generateAppearanceData();
	//		}
	//	}
	//
	//	public static void setClanMottifTextureInterface(Player player, boolean top, int slot) {
	//		if (slot > 116)
	//			return;
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		if (!manager.hasRankToEditSettings(player))
	//			return;
	//		manager.setClanMottifTexture(player, top, slot);
	//	}
	//
	//	public void setClanMottifTexture(Player player, boolean top, int slot) {
	//		synchronized (this) {
	//			if (top)
	//				clan.setMottifTop(slot);
	//			else
	//				clan.setMottifBottom(slot);
	//			player.getVars().setVarBit(9086, clan.getMottifTop()+1);
	//			player.getVars().setVarBit(9087, clan.getMottifBottom()+1);
	//			refreshAllMembersAppearence();
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public static void setClanMottoInterface(Player player, String motto) {
	//		player.stopAll();
	//		player.lock(1); // fixes walking, cuz inter not cliped
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		manager.setMotto(player, motto);
	//	}
	//
	//	public void setMotto(Player player, String motto) {
	//		synchronized (this) {
	//			if (!hasRankToEditSettings(player))
	//				return;
	//			clan.setMotto(motto);
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public static void setThreadIdInterface(Player player, String id) {
	//		id = id.toLowerCase();
	//		player.stopAll();
	//		player.lock(1); // fixes walking, cuz inter not cliped
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		manager.setThreadId(player, id.isEmpty() ? null : id);
	//	}
	//
	//	public void setThreadId(Player player, String id) {
	//		synchronized (this) {
	//			if (!hasRankToEditSettings(player))
	//				return;
	//			clan.setThreadId(id);
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public static void setClanFlagInterface(Player player, int flag) {
	//		if (flag > 241)
	//			return;
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		manager.setClanFlag(player, flag);
	//	}
	//
	//	public void setClanFlag(Player player, int flag) {
	//		synchronized (this) {
	//			if (!hasRankToEditSettings(player))
	//				return;
	//			clan.setClanFlag(flag);
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public static void setTimeZoneInterface(Player player, int time) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		manager.setTimeZone(player, time);
	//	}
	//
	//	public static void switchRecruitingInterface(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		manager.switchRecruiting(player);
	//		// TODO switch flag on inter
	//	}
	//
	//	public static void switchClanTimeInterface(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		manager.switchClanTime(player);
	//		// TODO switch flag on inter
	//	}
	//
	//	public void switchClanTime(Player player) {
	//		synchronized (this) {
	//			if (!hasRankToEditSettings(player))
	//				return;
	//			clan.switchClanTime();
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public void setTimeZone(Player player, int time) {
	//		synchronized (this) {
	//			if (!hasRankToEditSettings(player))
	//				return;
	//			clan.setTimeZone(time);
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public static void setWorldIdInterface(Player player, int worldId) {
	//		if (worldId > 200)
	//			return;
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		manager.setWorldId(player, worldId);
	//	}
	//
	//	public Clan getClan() {
	//		synchronized (this) {
	//			return clan;
	//		}
	//	}
	//
	//	public void setWorldId(Player player, int worldId) {
	//		synchronized (this) {
	//			if (!hasRankToEditSettings(player))
	//				return;
	//			clan.setWorldId(worldId);
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public void switchRecruiting(Player player) {
	//		synchronized (this) {
	//			if (!hasRankToEditSettings(player))
	//				return;
	//			clan.switchRecruiting();
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public static void switchGuestsInChatCanEnterInterface(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		manager.switchGuestsInChatCanEnter(player);
	//	}
	//
	//	public static void switchGuestsInChatCanTalkInterface(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null)
	//			return;
	//		manager.switchGuestsInChatCanTalk(player);
	//	}
	//
	//	public void switchGuestsInChatCanTalk(Player player) {
	//		synchronized (this) {
	//			if (!hasRankToEditSettings(player))
	//				return;
	//			clan.switchGuestsInChatCanTalk();
	//			generateClanChannelDataBlock();
	//			refreshClanChannel();
	//		}
	//	}
	//
	//	public void sendMessage(Player player, String message) {
	//		synchronized (this) {
	//			String displayName = player.getDisplayName();
	//			int rights = player.getRights().getCrown();
	//			for (Player p2 : channelPlayers)
	//				p2.getPackets().receiveClanChatMessage(displayName, rights, message, !membersOnline.contains(p2));
	//		}
	//	}
	//
	//	public Player getPlayer(String username) {
	//		synchronized (this) {
	//			String formatedUsername = Utils.formatPlayerNameForProtocol(username);
	//			for (Player player : channelPlayers) {
	//				if (player.getUsername().equals(formatedUsername) || player.getDisplayName().equalsIgnoreCase(username))
	//					return player;
	//			}
	//			return null;
	//		}
	//	}
	//
	//	public void kickPlayerFromChat(Player player, String username) {
	//		String name = "";
	//		for (char character : username.toLowerCase().toCharArray()) {
	//			name += Utils.containsInvalidCharacter(character) ? " " : character;
	//		}
	//		synchronized (this) {
	//			Player kicked = getPlayer(name);
	//			if (kicked == null) {
	//				player.sendMessage("This player is not this channel.");
	//				return;
	//			}
	//			if (getMemberByUsername(kicked.getUsername()) != null)
	//				return;
	//			bannedChannelPlayers.put(kicked.getUsername(), System.currentTimeMillis());
	//			disconnect(kicked, true);
	//			kicked.sendMessage("You have been kicked from the guest clan chat channel.");
	//			player.sendMessage("You have kicked " + kicked.getUsername() + " from clan chat channel.");
	//
	//		}
	//	}
	//
	//	public void sendQuickMessage(Player player, QuickChatMessage message) {
	//		synchronized (this) {
	//			String displayName = player.getDisplayName();
	//			int rights = player.getRights().getCrown();
	//			for (Player p2 : channelPlayers)
	//				p2.getPackets().receiveClanChatQuickMessage(displayName, rights, message, !membersOnline.contains(p2));
	//		}
	//	}
	//
	//	public void switchGuestsInChatCanEnter(Player player) {
	//		synchronized (this) {
	//			if (!hasRankToEditSettings(player))
	//				return;
	//			clan.switchGuestsInChatCanEnter();
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public boolean isMember(Player player) {
	//		synchronized (this) {
	//			for (ClanMember member : clan.getMembers())
	//				if (member.getUsername().equals(player.getUsername()))
	//					return true;
	//			return false;
	//		}
	//	}
	//
	//	public boolean hasRankToEditSettings(Player player) {
	//		return hasRights(player, ClanRank.ADMIN);
	//	}
	//
	//	public boolean hasRankToInvite(Player player) {
	//		return hasRights(player, ClanRank.ADMIN);
	//	}
	//
	//	public ClanRank getRank(Player player) {
	//		synchronized (this) {
	//			for (ClanMember member : clan.getMembers())
	//				if (member.getUsername().equals(player.getUsername()))
	//					return member.getRank();
	//			return ClanRank.NONE;
	//		}
	//	}
	//
	//	public boolean hasRights(Player player, ClanRank rank) {
	//		return getRank(player).ordinal() >= rank.ordinal();
	//	}
	//
	//	public void refreshClanSettings() {
	//		synchronized (this) {
	//			for (Player player : membersOnline)
	//				player.getPackets().sendClanSettings(this, false);
	//		}
	//	}
	//
	//	public void refreshClanChannel() {
	//		synchronized (this) {
	//			for (Player player : channelPlayers)
	//				player.getPackets().sendClanChannel(this, !membersOnline.contains(player));
	//		}
	//	}
	//
	//	public void connect(Player player, boolean guestClan) {
	//		synchronized (this) {
	//			if (!guestClan) {
	//				membersOnline.add(player);
	//				player.getPackets().sendClanSettings(this, false);
	//			}
	//			if (guestClan || player.isConnectedClanChannel())
	//				connectChannel(player);
	//			linkClan(player, guestClan);
	//		}
	//	}
	//
	//	public void linkClan(Player player, boolean guestClan) {
	//		if (guestClan)
	//			player.setGuestClanManager(this);
	//		else
	//			player.setClanManager(this);
	//	}
	//
	//	public void unlinkClan(Player player, boolean guestClan) {
	//		if (guestClan)
	//			player.setGuestClanManager(null);
	//		else
	//			player.setClanManager(null);
	//	}
	//
	//	public void disconnect(Player player, boolean guestClan) {
	//		synchronized (cachedClans) {
	//			synchronized (this) {
	//				if (guestClan || player.isConnectedClanChannel())
	//					disconnectChannel(player);
	//				if (!guestClan)
	//					membersOnline.remove(player);
	//				unlinkClan(player, guestClan);
	//				destroyIfEmpty();
	//			}
	//		}
	//	}
	//
	//	/*
	//	 * dont use this without synchronized (cachedClans) and synchronized (this)
	//	 * else exept disconnect
	//	 */
	//	private void destroyIfEmpty() {
	//		if (empty()) {
	//			JsonFileManager.saveClan(clan);
	//			cachedClans.remove(clan.getName());
	//		}
	//	}
	//
	//	public void connectChannel(Player player) {
	//		synchronized (this) {
	//			channelPlayers.add(player);
	//			generateClanChannelDataBlock();
	//			refreshClanChannel();
	//		}
	//	}
	//
	//	/*
	//	 * only used by normal channel by itself
	//	 */
	//	private void disconnectChannel(Player player) {
	//		synchronized (this) {
	//			channelPlayers.remove(player);
	//			player.getPackets().sendClanChannel(null, !membersOnline.contains(player));
	//			generateClanChannelDataBlock();
	//			refreshClanChannel();
	//		}
	//	}
	//
	//	public boolean empty() {
	//		return membersOnline.size() == 0 && channelPlayers.size() == 0;
	//	}
	//
	//	public static void linkClanMember(Player player, String clanName) {
	//		player.setClanName(clanName);
	//		player.setConnectedClanChannel(true);
	//		player.sendMessage("You have joined the clan, so you are now part of " + clanName + ".");
	//		connectToClan(player, clanName, false);
	//	}
	//
	//	public static void createClan(Player player, String clanName) {
	//		clanName = Utils.formatPlayerNameForDisplay(clanName);
	//		if (player.getClanManager() != null)
	//			return;
	//		synchronized (cachedClans) {
	//			if (JsonFileManager.containsClan(clanName)) {
	//				player.sendMessage("The clan name you tried already exists.");
	//				return;
	//			}
	//			Clan clan = new Clan(clanName, player);
	//			JsonFileManager.saveClan(clan);
	//			linkClanMember(player, clanName);
	//		}
	//	}
	//
	//	public static void joinClan(Player player, Player inviter) {
	//		player.stopAll();
	//		if (inviter == null)
	//			return;
	//		ClansManager manager = inviter.getClanManager();
	//		if (manager == null)
	//			return;
	//		synchronized (manager) {
	//			if (player.getGuestClanManager() != null)
	//				player.getGuestClanManager().disconnect(player, true);
	//			manager.clan.addMember(player.getAccount(), ClanRank.RECRUIT);
	//			manager.generateClanSettingsDataBlock();
	//			manager.refreshClanSettings();
	//			linkClanMember(player, manager.clan.getName());
	//		}
	//	}
	//
	//	public static boolean connectToClan(Player player, String clanName, boolean guest) {
	//		clanName = Utils.formatPlayerNameForDisplay(clanName);
	//		ClansManager manager = guest ? player.getGuestClanManager() : player.getClanManager();
	//		if (manager != null || guest && player.getClanName() != null && clanName.equalsIgnoreCase(player.getClanName())) // already
	//			// connected
	//			// to
	//			// a
	//			// clan
	//			return false;
	//		synchronized (cachedClans) {
	//			manager = cachedClans.get(clanName); // grabs clan
	//			boolean created = manager != null;
	//			if (!created) { // not loaded
	//				if (!JsonFileManager.containsClan(clanName)) {
	//					player.getPackets().setIFText(1110, 70, "Could not find a clan named " + clanName + ". Please check the name and try again.");
	//					return false;
	//				}
	//				Clan clan = JsonFileManager.loadClan(clanName);
	//				if (clan == null)
	//					return false;
	//				clan.init(clanName);
	//				manager = new ClansManager(clan);
	//			} else {
	//				synchronized (manager) {
	//					if (guest) {
	//						Long bannedSince = manager.bannedChannelPlayers.get(player.getUsername());
	//						if (bannedSince != null) {
	//							if (bannedSince + 3600000 > System.currentTimeMillis()) {
	//								player.sendMessage("You have been banned from this channel.");
	//								return false;
	//							}
	//							manager.bannedChannelPlayers.remove(player.getUsername());
	//						}
	//					}
	//				}
	//			}
	//			synchronized (manager) {
	//				if (!guest && !manager.isMember(player)) {
	//					player.sendMessage("You have beem kicked from the clan.");
	//					return false;
	//				}
	//				if (guest) {
	//					if (!manager.clan.isGuestsInChatCanEnter()) {
	//						player.sendMessage("This clan only allows clanmates to join their channel.");
	//						player.getPackets().setIFText(1110, 70, "This clan only allows clanmates to join their channel.");
	//						return false;
	//					}
	//					if (manager.getClan().getBannedUsers().contains(player.getUsername())) {
	//						player.sendMessage("You have been banned from this channel.");
	//						return false;
	//					}
	//				}
	//				if (!created)
	//					cachedClans.put(clanName, manager);
	//				if (guest)
	//					player.getPackets().sendRunScriptReverse(4453);
	//				manager.connect(player, guest);
	//				return true;
	//			}
	//		}
	//	}
	//
	//	public static void openClanDetails(Player player) {
	//		if (player.getClanManager() == null) {
	//			player.sendMessage("You're not in a clan.");
	//			return;
	//		}
	//		if (player.getInterfaceManager().containsScreenInter() || player.getInterfaceManager().containsInventoryInter()) {
	//			player.sendMessage("Please close the interface you have open first.");
	//			return;
	//		}
	//		openClanDetails(player, null, player.getClanManager());
	//	}
	//
	//	/*
	//	 * can be used to see clan details when recruiting too
	//	 */
	//	public static void openClanDetails(Player player, Player p2, ClansManager manager) {
	//		player.getPackets().sendClanSettings(manager, true);
	//		player.getInterfaceManager().sendInterface(1107);
	//		if (manager.clan.getMotto() != null)
	//			player.getPackets().setIFText(1107, 88, manager.clan.getMotto());
	//		if (manager.clan.getMottifTop() != 0)
	//			player.getPackets().setIFGraphic(1107, 96, Clan.getMottifSprite(manager.clan.getMottifTop()));
	//		if (manager.clan.getMottifBottom() != 0)
	//			player.getPackets().setIFGraphic(1107, 106, Clan.getMottifSprite(manager.clan.getMottifBottom()));
	//		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
	//		Calendar cal = Calendar.getInstance();
	//		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
	//		player.getPackets().setIFText(1107, 186, dateFormat.format(cal.getTime()));
	//		cal.add(Calendar.MINUTE, manager.clan.getTimeZone());
	//		player.getPackets().setIFText(1107, 185, dateFormat.format(cal.getTime()));
	//		if (p2 != null)
	//			player.getPackets().setIFText(1107, 92, p2.getDisplayName());
	//		else
	//			player.getPackets().setIFHidden(1107, 90, true);
	//
	//	}
	//
	//	public static void showClanSettingsClanMates(Player player) {
	//		showClanSettingsClanMates(player, false);
	//		showClanSettingsSettings(player, true);
	//		showClanSettingsPermissions(player, true);
	//	}
	//
	//	public static void showClanSettingsSettings(Player player) {
	//		showClanSettingsClanMates(player, true);
	//		showClanSettingsSettings(player, false);
	//		showClanSettingsPermissions(player, true);
	//	}
	//
	//	public static void showClanSettingsPermissions(Player player) {
	//		showClanSettingsClanMates(player, true);
	//		showClanSettingsSettings(player, true);
	//		showClanSettingsPermissions(player, false);
	//	}
	//
	//	private static void showClanSettingsClanMates(Player player, boolean hide) {
	//		player.getPackets().setIFHidden(1096, 83, hide);
	//		player.getPackets().setIFHidden(1096, 110, hide);
	//	}
	//
	//	private static void showClanSettingsSettings(Player player, boolean hide) {
	//		player.getPackets().setIFHidden(1096, 84, hide);
	//		player.getPackets().setIFHidden(1096, 117, hide);
	//	}
	//
	//	private static void showClanSettingsPermissions(Player player, boolean hide) {
	//		player.getPackets().setIFHidden(1096, 85, hide);
	//		player.getPackets().setIFHidden(1096, 385, hide);
	//	}
	//
	//	public static void selectPermissionTab(Player player, int tab) {
	//		player.getPackets().sendRunScriptReverse(5136, tab);
	//	}
	//
	//	public static void selectPermissionRank(Player player, int selectedRank) {
	//		player.getPackets().sendVarc(1572, 1);
	//		player.getPackets().sendVarc(1574, 1);
	//		player.getPackets().sendVarc(1576, 1);
	//		player.getPackets().sendVarc(1577, 1);
	//		player.getPackets().sendVarc(1578, 1);
	//		player.getPackets().sendVarc(1579, 1);
	//		player.getPackets().sendVarc(1580, 1);
	//		player.getPackets().sendVarc(1581, 1);
	//		player.getPackets().sendVarc(1582, 1);
	//		player.getPackets().sendVarc(1583, 1);
	//		player.getPackets().sendVarc(1584, 1);
	//		player.getPackets().sendVarc(1585, 1);
	//		player.getPackets().sendVarc(1586, 1);
	//		player.getPackets().sendVarc(1587, 1);
	//		player.getPackets().sendVarc(1588, 1);
	//		player.getPackets().sendVarc(1589, 1);
	//
	//		player.getPackets().sendVarc(1649, 1);
	//
	//		player.getPackets().sendVarc(1590, 1);
	//		player.getPackets().sendVarc(1569, selectedRank); // selects
	//		// rank
	//		player.getPackets().sendVarc(1571, 1);
	//		player.getPackets().sendVarc(1570, 1);
	//		player.getPackets().sendVarc(1573, 1);
	//		player.getPackets().sendVarc(1575, 1);
	//		player.getPackets().sendVarc(1792, 1);
	//
	//	}
	//
	//	public static void openClanSettings(Player player) {
	//		if (player.getClanManager() == null) {
	//			player.sendMessage("You must be in a clan to do that.");
	//			return;
	//		}
	//		player.getInterfaceManager().sendInterface(1096);
	//		showClanSettingsClanMates(player);
	//		selectPermissionTab(player, 1);
	//		player.getPackets().setIFText(1096, 373, "Permissions are currently disabled and setted to default.");
	//		player.getPackets().setIFHidden(1096, 202, false); // disable
	//		// acess to
	//		// citadel
	//		// setting
	//		player.getPackets().setIFHidden(1096, 203, false); // disable
	//		// acess to
	//		// citadel
	//		// setting
	//		player.getPackets().setIFHidden(1096, 217, false); // disable
	//		// signpost
	//		// permissions
	//		// setting
	//		player.getPackets().setIFHidden(1096, 218, false); // disable
	//		// signpost
	//		// permissions
	//		// setting
	//		player.getPackets().setIFRightClickOps(1096, 240, 0, 144, 0); // unlocks
	//		// timezone
	//		// setting
	//		player.getPackets().setIFRightClickOps(1096, 290, 0, 200, 0); // unlocks
	//		// worldid
	//		// setting
	//		player.getPackets().setIFRightClickOps(1096, 41, 0, Clan.MAX_MEMBERS, 0); // unlocks
	//		// clanmates
	//		player.getPackets().setIFRightClickOps(1096, 276, 0, 125, 0); // set
	//		// member
	//		// rank
	//		player.getPackets().setIFRightClickOps(1096, 262, 0, 500, 0); // set
	//		// member
	//		// profession
	//	}
	//
	//	public static void joinGuestClanChat(Player player) {
	//		if (player.getGuestClanManager() != null)
	//			player.getGuestClanManager().disconnect(player, true);
	//		else {
	//			player.getTemporaryAttributes().put("joinguestclan", Boolean.TRUE);
	//			player.getPackets().sendInputNameScript("Please enter the name of the clan to chat in:");
	//			player.getPackets().setIFText(1110, 70, "Please enter the name of the clan whose Clan chat you wish to join as a guest. <br><br>To talk as a guest, start  your<br>line<br>of chat with ///");
	//
	//		}
	//	}
	//
	//	public static void banPlayer(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null) {
	//			player.sendMessage("You're  not in a clan.");
	//			return;
	//		}
	//		if (manager.hasRights(player, ClanRank.ADMIN)) {
	//			player.sendMessage("You must be a clan admin to do that.");
	//			return;
	//		}
	//		player.getTemporaryAttributes().put("banclanplayer", Boolean.TRUE);
	//		player.getPackets().sendInputNameScript("Enter the name of the play you wish to ban:");
	//	}
	//
	//	public static void banPlayer(Player player, String name) {
	//		name = Utils.formatPlayerNameForDisplay(name);
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null) {
	//			player.sendMessage("You're  not in a clan.");
	//			return;
	//		}
	//		if (manager.hasRights(player, ClanRank.ADMIN)) {
	//			player.sendMessage("You must be a clan admin to do that.");
	//			return;
	//		}
	//		manager.ban(player, name);
	//	}
	//
	//	public void ban(Player player, String name) {
	//		synchronized (this) {
	//			if (getMemberByUsername(name) != null) {
	//				player.sendMessage("You can't add a member from your clan to banlist.");
	//				return;
	//			}
	//			if (clan.getBannedUsers().size() >= 100) {
	//				player.sendMessage("Ban list is currently full.");
	//				return;
	//			}
	//			player.sendMessage("Attempting to ban " + name + ".");
	//			clan.getBannedUsers().add(name);
	//			generateClanSettingsDataBlock();
	//			refreshClanSettings();
	//		}
	//	}
	//
	//	public static void clanEvent(String event, Player player) {
	//		if (player.getUsername().equalsIgnoreCase("danny"))
	//			World.sendWorldMessage("<img=7><col=ff0000>[Clan Event] The clan " + player.getClanName() + " is currently hosting: " + event + "", false);
	//	}
	//
	//	public static void unbanPlayer(Player player) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null) {
	//			player.sendMessage("You must be a clan admin to do that.");
	//			return;
	//		}
	//		if (manager.hasRights(player, ClanRank.ADMIN)) {
	//			player.sendMessage("You must be a clan admin to do that.");
	//			return;
	//		}
	//		player.getTemporaryAttributes().put("unbanclanplayer", Boolean.TRUE);
	//		player.getPackets().sendInputNameScript("Enter the name of the play you wish to unban:");
	//	}
	//
	//	public static void unbanPlayer(Player player, String name) {
	//		name = Utils.formatPlayerNameForDisplay(name);
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null) {
	//			player.sendMessage("You must be a clan admin to do that.");
	//			return;
	//		}
	//		if (manager.hasRights(player, ClanRank.ADMIN)) {
	//			player.sendMessage("You must be a clan admin to do that.");
	//			return;
	//		}
	//		manager.unban(player, name);
	//	}
	//
	//	public static void unbanPlayer(Player player, int index) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null) {
	//			player.sendMessage("You must be a clan admin to do that.");
	//			return;
	//		}
	//		if (manager.hasRights(player, ClanRank.ADMIN)) {
	//			player.sendMessage("You must be a clan admin to do that.");
	//			return;
	//		}
	//		manager.unban(player, index);
	//	}
	//
	//	public void unban(Player player, int slot) {
	//		synchronized (this) {
	//			if (clan.getBannedUsers().size() <= slot)
	//				return;
	//			unban(player, clan.getBannedUsers().get(slot));
	//		}
	//	}
	//
	//	public void unban(Player player, String name) {
	//		player.sendMessage("Attempting to unban " + name + ".");
	//		synchronized (this) {
	//			if (clan.getBannedUsers().remove(name)) {
	//				generateClanSettingsDataBlock();
	//				refreshClanSettings();
	//			} else
	//				player.sendMessage("An error was encountered while applying trying to unban " + name + ". No player of that name could be found.");
	//		}
	//	}
	//
	//	public static void joinClanChatChannel(Player player) {
	//		if (player.getClanManager() == null) {
	//			player.sendMessage("You must be a member of a clan in order to join their channel.");
	//			player.getDialogueManager().execute(new ClanCreateD());
	//			return;
	//		}
	//		if (player.isConnectedClanChannel()) {
	//			player.setConnectedClanChannel(false);
	//			player.getClanManager().disconnectChannel(player);
	//		} else {
	//			player.setConnectedClanChannel(true);
	//			player.getClanManager().connectChannel(player);
	//		}
	//	}
	//
	//	public static void viewInvite(final Player player, Player p2) {
	//		if (player.getTemporaryAttributes().remove("claninvite") != null) {
	//			player.getDialogueManager().execute(new ClanInvite(), p2);
	//		}
	//	}
	//
	//	public static void invite(Player player, Player p2) {
	//		ClansManager manager = player.getClanManager();
	//		if (manager == null) {
	//			player.sendMessage("You must be in a clan to do that.");
	//			return;
	//		}
	//		synchronized (manager) {
	//			if (!manager.hasRankToInvite(player)) {
	//				player.sendMessage("You don't have permissions to invite.");
	//				return;
	//			}
	//			if (manager.getClan().getBannedUsers().contains(p2.getUsername())) {
	//				player.sendMessage("This player has been banned from this clan.");
	//				return;
	//			}
	//			if (manager.clan.getMembers().size() >= Clan.MAX_MEMBERS) {
	//				player.sendMessage("Clans can't have over 500 members.");
	//			}
	//		}
	//		if (p2.getClanManager() != null) {
	//			player.sendMessage("This player is already a member of another clan.");
	//			return;
	//		}
	//		if (p2.getInterfaceManager().containsScreenInter() || p2.getControllerManager().getController() != null) {
	//			player.sendMessage("The other player is busy.");
	//			return;
	//		}
	//		player.sendMessage("Sending " + p2.getDisplayName() + " a invitation...");
	//		p2.getPackets().sendClanInviteMessage(player);
	//		p2.getTemporaryAttributes().put("claninvite", Boolean.TRUE);
	//	}
}
