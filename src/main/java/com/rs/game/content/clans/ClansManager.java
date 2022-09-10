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
package com.rs.game.content.clans;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.statements.SimpleStatement;
import com.rs.game.content.dialogue.statements.Statement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.NPCBodyMeshModifier;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.model.object.ObjectMeshModifier;
import com.rs.lib.model.clan.Clan;
import com.rs.lib.model.clan.ClanPermission;
import com.rs.lib.model.clan.ClanRank;
import com.rs.lib.model.clan.ClanSetting;
import com.rs.lib.model.clan.ClanVar;
import com.rs.lib.model.MemberData;
import com.rs.lib.net.packets.decoders.lobby.CCJoin;
import com.rs.lib.net.packets.decoders.lobby.CCLeave;
import com.rs.lib.net.packets.decoders.lobby.ClanAddMember;
import com.rs.lib.net.packets.decoders.lobby.ClanCheckName;
import com.rs.lib.net.packets.decoders.lobby.ClanCreate;
import com.rs.lib.net.packets.decoders.lobby.ClanLeave;
import com.rs.lib.net.packets.encoders.social.ClanSettingsFull;
import com.rs.lib.util.RSColor;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.IFOnPlayerEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.InterfaceOnPlayerHandler;

@PluginEventHandler
public class ClansManager {
	
	private static Map<String, Clan> CACHED_CLANS = new ConcurrentHashMap<>();
	
	public static Clan getClan(String name) {
		if (name == null)
			return null;
		if (CACHED_CLANS.get(name) == null) {
			try {
				LobbyCommunicator.getClan(name, clan -> CACHED_CLANS.put(name, clan));
			} catch(Throwable e) {
				System.err.println("Error communicating with clan service.");
			}
			return null;
		}
		return CACHED_CLANS.get(name);
	}
	
	public static void getClan(String name, Consumer<Clan> cb) {
		if (name == null)
			return;
		if (CACHED_CLANS.get(name) == null) {
			try {
				LobbyCommunicator.getClan(name, clan -> {
					CACHED_CLANS.put(name, clan);
					cb.accept(clan);
				});
			} catch(Throwable e) {
				cb.accept(null);
				System.err.println("Error communicating with clan service.");
			}
		} else
			cb.accept(CACHED_CLANS.get(name));
	}
	
	public static void syncClanFromLobby(Clan clan) {
		CACHED_CLANS.put(clan.getName(), clan);
		for (String username : clan.getMembers().keySet()) {
			Player player = World.getPlayerByUsername(username);
			if (player == null || player.hasFinished() || !player.hasStarted())
				continue;
			player.getAppearance().generateAppearanceData();
			for (int key : clan.getVars().keySet())
				player.getPackets().setClanVar(key, clan.getVar(key));
		}
	}
	
	public static void syncClanToLobby(Clan clan, Runnable done) {
		LobbyCommunicator.updateClan(clan, res -> {
			if (res == null) {
				done.run();
				return;
			}
			CACHED_CLANS.put(res.getName(), res);
			done.run();
		});
	}

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

			@Override
			public void close(Player player) {
				
			}
		}));
	}

	public static void create(Player player, String name) {
		player.sendOptionDialogue("The name " + name + " is available. Create the clan?", ops -> {
			ops.add("Yes, create " + name + ".", () -> LobbyCommunicator.forwardPacket(player, new ClanCreate(name), cb -> { }));
			ops.add("No, I want to pick another name.");
			return;
		});
	}

	public static void promptName(Player player) {
		player.sendInputName("Which name would you like for your clan?", name -> {
			if (player.getTempAttribs().getB("ccCreateLock")) {
				player.simpleDialogue("Your previous request to create a clan is still in progress... Please wait or relog.");
				return;
			}
			player.getTempAttribs().setB("ccCreateLock", true);
			LobbyCommunicator.forwardPacket(player, new ClanCheckName(name, false), cb -> {
				player.getTempAttribs().removeB("ccCreateLock");
			});
		});
	}
	
	public static ButtonClickHandler handleClanChatButtons = new ButtonClickHandler(1110) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().sendMessage("handleClanChatButtons: " + e.getComponentId() + " - " + e.getSlotId() + " - " + e.getPacket());
			switch(e.getComponentId()) {
			case 82 -> {
				if (e.getPlayer().getSocial().isConnectedToClan() && e.getPlayer().getSocial().getClanName() != null)
					leaveChannel(e.getPlayer(), false);
				else
					joinChannel(e.getPlayer(), null);
			}
			case 91 -> {
				if (e.getPlayer().getSocial().getGuestedClanChat() != null)
					leaveChannel(e.getPlayer(), true);
				else
					e.getPlayer().sendInputName("Which clan chat would you like to join?",
							"Please enter the name of the clan whose Clan chat you wish to join as a guest. <br><br>To talk as a guest, start  your<br>line<br>of chat with ///",
							name -> joinChannel(e.getPlayer(), name));
			}
			case 59 -> e.getPlayer().getPackets().sendRunScript(4443, -1);
			case 95 -> e.getPlayer().sendInputName("Which player would you like to ban?", name -> banPlayer(e.getPlayer(), name));
			case 78 -> openSettings(e.getPlayer());
			case 75 -> openClanDetails(e.getPlayer(), null, e.getPlayer().getClan());
			case 109 -> leaveClan(e.getPlayer());
			case 11 -> unban(e.getPlayer(), e.getSlotId());
			case 99 -> e.getPlayer().sendInputName("Which player would you like to unban?", name -> unban(e.getPlayer(), Utils.formatPlayerNameForDisplay(name)));
			}
		}
	};

	public static ButtonClickHandler handleClanFlagButtons = new ButtonClickHandler(1089) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getClan() == null)
				return;
			if (e.getComponentId() == 30)
				e.getPlayer().getTempAttribs().setI("clanflagselection", e.getSlotId());
			else if (e.getComponentId() == 26) {
				int flag = e.getPlayer().getTempAttribs().removeI("clanflagselection", -1);
				e.getPlayer().stopAll();
				if (flag != -1) {
					if (!e.getPlayer().getClan().hasPermissions(e.getPlayer().getUsername(), ClanRank.ADMIN))
						return;
					e.getPlayer().getClan().setSetting(ClanSetting.NATIONAL_FLAG, flag);
					syncClanToLobby(e.getPlayer().getClan(), () -> e.getPlayer().sendMessage("Clan settings saved."));
				}
			}
		}
	};

	public static ButtonClickHandler handleClanSettingsButtonsMain = new ButtonClickHandler(1096) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().sendMessage("handleClanSettingsButtonsMain: " + e.getComponentId() + " - " + e.getSlotId() + " - " + e.getPacket());
			if (e.getComponentId() == 41)
				viewClanmateDetails(e.getPlayer(), e.getSlotId());
//			else if (e.getComponentId() == 94)
//				switchGuestsInChatCanEnterInterface(e.getPlayer());
//			else if (e.getComponentId() == 95)
//				switchGuestsInChatCanTalkInterface(e.getPlayer());
//			else if (e.getComponentId() == 96)
//				switchRecruitingInterface(e.getPlayer());
//			else if (e.getComponentId() == 97)
//				switchClanTimeInterface(e.getPlayer());
			else if (e.getComponentId() == 124)
				openClanMotifInterface(e.getPlayer());
			else if (e.getComponentId() == 131)
				openClanMottoInterface(e.getPlayer());
//			else if (e.getComponentId() == 240)
//				setTimeZoneInterface(e.getPlayer(), -720 + e.getSlotId() * 10);
			else if (e.getComponentId() == 262)
				e.getPlayer().getTempAttribs().setI("editclanmatejob", e.getSlotId());
			else if (e.getComponentId() == 276)
				e.getPlayer().getTempAttribs().setI("editclanmaterank", e.getSlotId());
//			else if (e.getComponentId() == 309)
//				kickClanmate(e.getPlayer());
//			else if (e.getComponentId() == 318)
//				saveClanmateDetails(e.getPlayer());
//			else if (e.getComponentId() == 290)
//				setWorldIdInterface(e.getPlayer(), e.getSlotId());
			else if (e.getComponentId() == 297)
				openForumThreadInterface(e.getPlayer());
			else if (e.getComponentId() == 346)
				openNationalFlagInterface(e.getPlayer());
			if (e.getComponentId() == 113)
				showClanSettingsClanMates(e.getPlayer());
			else if (e.getComponentId() == 120)
				showClanSettingsSettings(e.getPlayer());
			else if (e.getComponentId() == 386)
				showClanSettingsPermissions(e.getPlayer());
			else if (e.getComponentId() >= 395 && e.getComponentId() <= 475) {
				int selectedRank = (e.getComponentId() - 395) / 8;
				if (selectedRank == 10)
					selectedRank = 125;
				else if (selectedRank > 5)
					selectedRank = 100 + selectedRank - 6;
				selectPermissionRank(e.getPlayer(), ClanRank.forId(selectedRank));
			} else if (e.getComponentId() == 489)
				selectPermissionTab(e.getPlayer(), 1);
			else if (e.getComponentId() == 498)
				selectPermissionTab(e.getPlayer(), 2);
			else if (e.getComponentId() == 506)
				selectPermissionTab(e.getPlayer(), 3);
			else if (e.getComponentId() == 514)
				selectPermissionTab(e.getPlayer(), 4);
			else if (e.getComponentId() == 522)
				selectPermissionTab(e.getPlayer(), 5);
		}
	};

	public static ButtonClickHandler handleMotifButtons = new ButtonClickHandler(1105) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().sendMessage("handleMotifButtons: " + e.getComponentId() + " - " + e.getSlotId() + " - " + e.getPacket());
			if (e.getComponentId() == 63 || e.getComponentId() == 66)
				ClansManager.setClanMotifTextureInterface(e.getPlayer(), e.getComponentId() == 66, e.getSlotId());
			if (e.getComponentId() == 35)
				ClansManager.openSetMotifColor(e.getPlayer(), 0);
			else if (e.getComponentId() == 80)
				ClansManager.openSetMotifColor(e.getPlayer(), 1);
			else if (e.getComponentId() == 92)
				ClansManager.openSetMotifColor(e.getPlayer(), 2);
			else if (e.getComponentId() == 104)
				ClansManager.openSetMotifColor(e.getPlayer(), 3);
			else if (e.getComponentId() == 120)
				e.getPlayer().stopAll();
		}
	};
	
	public static ButtonClickHandler handleCloseButton = new ButtonClickHandler(1079) {
		@Override
		public void handle(ButtonClickEvent e) {
			e.getPlayer().closeInterfaces();
		}
	};

	public static ButtonClickHandler handleInviteInter = new ButtonClickHandler(1095) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 33) {
				Player inviter = e.getPlayer().getTempAttribs().removeO("clanInviter");
				if (inviter == null) {
					e.getPlayer().sendMessage("There is a pending request to join a clan submitted to the clan service. Relog or try again later.");
					return;
				}
				if (inviter.hasStarted() && !inviter.hasFinished()) {
					LobbyCommunicator.forwardPacket(inviter, new ClanAddMember(e.getPlayer().getUsername()), success -> {
						if (!success)
							e.getPlayer().sendMessage("There was an error communicating with the clan service.");
					});
				}
				e.getPlayer().closeInterfaces();
			}
		}
	};
	
	public static InterfaceOnPlayerHandler handleInvite = new InterfaceOnPlayerHandler(false, new int[] { 1110 }) {
		@Override
		public void handle(IFOnPlayerEvent e) {
			if (e.getComponentId() == 87)
				e.getTarget().getPackets().sendClanInviteMessage(e.getPlayer());
		}
	};
	
	public static void viewClanmateDetails(Player player, int index) {
		if (player.getClan() == null)
			return;
		if (index >= player.getClan().getMembers().keySet().size()) {
			player.sendMessage("This is a placeholder character to unlock all clan features without needing 5 players.");
			return;
		}
		String username = new ArrayList<>(player.getClan().getMembers().keySet()).get(index);
		MemberData member = player.getClan().getMembers().get(username);
		if (member == null)
			return;
		viewClanmateDetails(player, username, member);
		player.getTempAttribs().removeO("editclanmaterank");
		player.getTempAttribs().removeO("editclanmatejob");
		if (player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN))
			player.getTempAttribs().setO("editclanmatedetails", member);
	}

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
	
	private static void joinChannel(Player player, String guestClanName) {
		if (guestClanName == null && player.getSocial().getClanName() == null) {
			createClan(player);
			return;
		}
		if (player.getTempAttribs().getB("ccJoinLock")) {
			player.sendMessage("Please wait...");
			return;
		}
		player.getTempAttribs().setB("ccJoinLock", true);
		LobbyCommunicator.forwardPacket(player, new CCJoin(guestClanName), cb -> player.getTempAttribs().removeB("ccJoinLock"));
	}

	private static void createClan(Player player) {
		player.startConversation(new Conversation(new Dialogue(new SimpleStatement("You are not currently in a clan. Would you like to create one?")))
				.addOption("Create a clan?", "Yes", "Not right now.")
				.addNext(() -> promptName(player)));
	}

	private static void leaveChannel(Player player, boolean guest) {
		if (player.getTempAttribs().getB("ccLeaveLock")) {
			player.sendMessage("Please wait...");
			return;
		}
		player.getTempAttribs().setB("ccLeaveLock", true);
		LobbyCommunicator.forwardPacket(player, new CCLeave(guest), cb -> player.getTempAttribs().removeB("ccLeaveLock"));
	}

	private static void leaveClan(Player player) {
		if (player.getSocial().getClanName() == null) {
			player.sendMessage("You aren't in a clan.");
			return;
		}
		if (player.getTempAttribs().getB("ccLeaveLock")) {
			player.sendMessage("Please wait...");
			return;
		}
		player.sendOptionDialogue("Are you sure you want to leave your clan?", conf1 -> {
			conf1.add("I am sure I want to leave the clan.").addOptions("Are you ABSOLUTELY sure you want to leave?", conf2 -> {
				conf2.add("Yes, I am absolutely sure I want to leave my clan.", () -> {
					player.getTempAttribs().setB("ccLeaveLock", true);
					LobbyCommunicator.forwardPacket(player, new ClanLeave(), cb -> player.getTempAttribs().removeB("ccLeaveLock"));
				});
				conf2.add("No, I've changed my mind.");
			});
			conf1.add("Nevermind.");
		});
	}

	private static void banPlayer(Player player, String name) {
		// TODO Auto-generated method stub
	}

	private static void unban(Player player, String displayName) {
		// TODO Auto-generated method stub
	}

	private static void unban(Player player, int slotId) {
		// TODO Auto-generated method stub
	}

	private static void openSettings(Player player) {
		if (player.getClan() == null) {
			player.sendMessage("You must be in a clan to do that.");
			return;
		}
		player.getInterfaceManager().sendInterface(1096);
		if (player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN))
			player.setCloseInterfacesEvent(() -> syncClanToLobby(player.getClan(), () -> player.sendMessage("Saved clan details successfully.")));
		showClanSettingsClanMates(player);
		selectPermissionTab(player, 1);
		player.getPackets().setIFRightClickOps(1096, 240, 0, 144, 0); // unlocks timezone setting
		player.getPackets().setIFRightClickOps(1096, 290, 0, 200, 0); // unlocks worldid setting
		player.getPackets().setIFRightClickOps(1096, 41, 0, Clan.MAX_MEMBERS, 0); // unlocks clanmates
		player.getPackets().setIFRightClickOps(1096, 276, 0, 125, 0); // set member rank
		player.getPackets().setIFRightClickOps(1096, 262, 0, 500, 0); // set member profession
	}
	
	private static void selectPermissionRank(Player player, ClanRank rank) {
		Clan clan = player.getClan();
		if (clan == null || rank == null)
			return;
		player.getTempAttribs().setO("permissionRankEditing", rank);
		player.getPackets().sendRunScript(5130);
		player.getPackets().sendVarc(1569, rank.getIconId());
		for (ClanPermission perm : ClanPermission.values())
			player.getPackets().sendVarc(perm.getVarc(), perm.hasPermission(clan, rank) ? 1 : 0);
	}
	
	private static void selectPermissionTab(Player player, int tab) {
		player.getPackets().sendRunScript(5136, tab);
		player.getPackets().setIFHidden(1096, 26, true);
		for (int i = 16;i <= 20;i++)
			if (tab == i-15)
				player.getPackets().setIFHidden(1096, i, false);
			else
				player.getPackets().setIFHidden(1096, i, true);
	}

	private static void showClanSettingsClanMates(Player player) {
		player.getPackets().setIFHidden(1096, 85, true);
		player.getPackets().setIFHidden(1096, 385, true);
		player.getPackets().sendRunScript(4298);
	}

	private static void showClanSettingsSettings(Player player) {
		player.getPackets().setIFHidden(1096, 85, true);
		player.getPackets().setIFHidden(1096, 385, true);
		player.getPackets().sendRunScript(4296);
	}

	private static void showClanSettingsPermissions(Player player) {
		player.getPackets().setIFHidden(1096, 85, false);
		player.getPackets().setIFHidden(1096, 385, false);
	}
	
	public static void openClanInvite(Player player, Player inviter, Clan clan) {
		if (clan == null || clan.getUpdateBlock() == null) {
			player.sendMessage("Error loading clan update block.");
			return;
		}
		player.getSession().write(new ClanSettingsFull(clan.getUpdateBlock(), true));
		player.getInterfaceManager().sendInterface(1095);
		player.getTempAttribs().setO("clanInviter", inviter);
		player.setCloseInterfacesEvent(() -> player.getTempAttribs().removeO("clanInviter"));
	}
	
	public static void openClanDetails(Player player, Player vexPlanter, Clan clan) {
		if (clan == null || clan.getUpdateBlock() == null) {
			player.sendMessage("Error loading clan update block.");
			return;
		}
		player.getSession().write(new ClanSettingsFull(clan.getUpdateBlock(), true));
		player.getInterfaceManager().sendInterface(1107);
		if (vexPlanter != null) {
			player.getPackets().setIFText(1107, 92, vexPlanter.getDisplayName());
			//player.getPackets().sendRunScript(4423);
		} else
			//player.getPackets().sendRunScript(4422);
		player.getPackets().sendRunScript(4413);
	}
	
	public static void openNationalFlagInterface(Player player) {
		if (player.getClan() == null)
			return;
		if (!player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN))
			return;
		player.stopAll();
		player.getInterfaceManager().sendInterface(1089);
		player.getPackets().setIFRightClickOps(1089, 30, 0, 241, 0);
	}

	public static void openForumThreadInterface(Player player) {
		if (player.getClan() == null)
			return;
		if (!player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN))
			return;
		player.getInterfaceManager().sendChatBoxInterface(1100);
		player.sendInputForumQFC(qfc -> {
			player.getInterfaceManager().closeChatBoxInterface();
			player.getClan().setSetting(ClanSetting.FORUM_QFC, Utils.stringToLong(qfc));
			syncClanToLobby(player.getClan(), () -> player.sendMessage("Saved clan forum quickfind code."));
		});
	}
	
	public static void openClanMottoInterface(Player player) {
		Clan clan = player.getClan();
		if (clan == null)
			return;
		if (!clan.hasPermissions(player.getUsername(), ClanRank.ADMIN))
			return;
		player.stopAll();
		player.sendInputLongText("Please enter your desired clan motto:", motto -> {
			clan.setSetting(ClanSetting.MOTTO, motto);
			syncClanToLobby(clan, () -> player.sendMessage("Saved clan motto: " + motto));
		});
	}

	public static void openClanMotifInterface(Player player) {
		player.stopAll();
		player.getInterfaceManager().sendInterface(1105);
		player.getPackets().sendRunScript(4400, 72417446);
		player.getPackets().setIFRightClickOps(1105, 66, 0, 116, 0); // top
		player.getPackets().setIFRightClickOps(1105, 63, 0, 116, 0); // button
		player.getVars().setVarBit(9086, player.getClan().getMotifTopIcon());
		player.getVars().setVarBit(9087, player.getClan().getMotifBottomIcon());
		for (int i = 0; i < player.getClan().getMotifColors().length; i++)
			player.getVars().setVar(2094 + i, player.getClan().getMotifColors()[i]);
		player.setCloseInterfacesEvent(() -> syncClanToLobby(player.getClan(), () -> player.sendMessage("Saved clan details successfully.")));
	}

	public static void openSetMotifColor(Player player, int part) {
		player.getInterfaceManager().sendInterface(1106);
		player.sendInputHSL(color -> {
			player.getClan().setMotifColor(part, color);
			syncClanToLobby(player.getClan(), () -> player.sendMessage("Saved clan details successfully."));
			openClanMotifInterface(player);
		});
	}
	
	public static void setClanMotifTextureInterface(Player player, boolean top, int slot) {
		if (slot > 116)
			return;
		setClanMotifTexture(player, top, slot);
	}

	public static void setClanMotifTexture(Player player, boolean top, int slot) {
		if (!player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN))
			return;
		if (top)
			player.getClan().setSetting(ClanSetting.MOTIF_TOP_ICON, slot+1);
		else
			player.getClan().setSetting(ClanSetting.MOTIF_BOTTOM_ICON, slot+1);
		player.getVars().setVarBit(9086, player.getClan().getMotifTopIcon());
		player.getVars().setVarBit(9087, player.getClan().getMotifBottomIcon());
	}
	
	public static void setVar(Clan clan, int id, Object value) {
		clan.setVar(id, value);
		syncClanToLobby(clan, () -> {});
	}
	
	public static void setVar(Clan clan, ClanVar var, Object value) {
		clan.setVar(var, value);
		syncClanToLobby(clan, () -> {});
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
	//		player.startConversation(new LeaveClan());
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
	//			player.startConversation(new ClanCreateD());
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
	//			player.startConversation(new ClanInvite(), p2);
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
	
	public static int[] generateColorGradient(Clan clan, int length, boolean hasTextures) {
		RSColor primary = RSColor.fromHSL((int) clan.getSetting(ClanSetting.MOTIF_PRIMARY_COLOR));
		RSColor secondary = RSColor.fromHSL((int) clan.getSetting(ClanSetting.MOTIF_SECONDARY_COLOR));
		
		int[] colors = new int[length];
		
		if (hasTextures) {
			colors[0] = (int) clan.getSetting(ClanSetting.MOTIF_TOP_COLOR);
			colors[1] = (int) clan.getSetting(ClanSetting.MOTIF_BOTTOM_COLOR);
		}
		int offset = hasTextures ? 2 : 0;
		int luminanceAdjustor = 40 / (colors.length - offset);
		int sizeDiff = (colors.length - offset) / 2;
		boolean initedPrimary = false, initedSecondary = false;
		for (int i = colors.length-1;i >= offset;i--) {
			if (sizeDiff-- > 0) {
				colors[i] = secondary.adjustLuminance(initedSecondary ? luminanceAdjustor : 0).getValue();
				initedSecondary = true;
			} else {
				colors[i] = primary.adjustLuminance(initedPrimary ? luminanceAdjustor : 0).getValue();
				initedPrimary = true;
			}
		}
		return colors;
	}
	
	public static void clanifyObject(Clan clan, GameObject object) {
		if (object.getDefinitions().modifiedColors == null)
			return;
		boolean hasTextures = object.getDefinitions().modifiedTextures != null;
		int[] colors = generateColorGradient(clan, object.getDefinitions().modifiedColors.length, hasTextures);
		ObjectMeshModifier modifier = object.modifyMesh().addColors(colors);
		if (hasTextures)
			modifier.addTextures(clan.getMotifTextures());
		object.refresh();
	}
	
	public static NPCBodyMeshModifier clanifyNPC(Clan clan, NPC npc) {
		if (npc.getDefinitions().modifiedColors == null)
			return null;
		boolean hasTextures = npc.getDefinitions().modifiedTextures != null;
		int[] colors = generateColorGradient(clan, npc.getDefinitions().modifiedColors.length, hasTextures);
		NPCBodyMeshModifier modifier = npc.modifyMesh().addColors(colors);
		if (hasTextures)
			modifier.addTextures(clan.getMotifTextures());
		return modifier;
	}
}
