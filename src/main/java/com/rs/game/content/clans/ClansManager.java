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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.rs.cache.loaders.EnumDefinitions;
import com.rs.game.World;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.SimpleStatement;
import com.rs.engine.dialogue.statements.Statement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.NPCBodyMeshModifier;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.model.object.ObjectMeshModifier;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Rights;
import com.rs.lib.model.MemberData;
import com.rs.lib.model.clan.Clan;
import com.rs.lib.model.clan.ClanPermission;
import com.rs.lib.model.clan.ClanRank;
import com.rs.lib.model.clan.ClanSetting;
import com.rs.lib.model.clan.ClanVar;
import com.rs.lib.net.packets.decoders.lobby.CCJoin;
import com.rs.lib.net.packets.decoders.lobby.CCLeave;
import com.rs.lib.net.packets.decoders.lobby.ClanAddMember;
import com.rs.lib.net.packets.decoders.lobby.ClanCheckName;
import com.rs.lib.net.packets.decoders.lobby.ClanCreate;
import com.rs.lib.net.packets.decoders.lobby.ClanKickMember;
import com.rs.lib.net.packets.decoders.lobby.ClanLeave;
import com.rs.lib.net.packets.encoders.social.ClanSettingsFull;
import com.rs.lib.util.Logger;
import com.rs.lib.util.RSColor;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;
import com.rs.plugin.annotations.PluginEventHandler;
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
				LobbyCommunicator.getClan(name, clan -> {
					if (clan == null || clan.getName() == null) {
						Logger.error(ClansManager.class, "getClan", "Clan returned from lobby server was null " + name);
						return;
					}
					CACHED_CLANS.put(name, clan);	
				});
			} catch(Throwable e) {
				Logger.error(ClansManager.class, "getClan", "Error communicating with clan service. " + name);
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
					if (clan == null || clan.getName() == null) {
						Logger.error(ClansManager.class, "getClan", "Clan returned from lobby server was null " + name);
						cb.accept(null);
						return;
					}
					CACHED_CLANS.put(name, clan);
					cb.accept(clan);
				});
			} catch(Throwable e) {
				cb.accept(null);
				Logger.error(ClansManager.class, "getClan", "Error communicating with clan service: " + name);
			}
		} else
			cb.accept(CACHED_CLANS.get(name));
	}
	
	public static void syncClanFromLobby(Clan clan) {
		if (clan == null || clan.getName() == null) {
			Logger.error(ClansManager.class, "syncClanFromLobby", "Clan returned from lobby server was null");
			return;
		}
		CACHED_CLANS.put(clan.getName(), clan);
		for (String username : clan.getMembers().keySet()) {
			Player player = World.getPlayerByUsername(username);
			if (player == null || player.hasFinished() || !player.hasStarted())
				continue;
			player.getAppearance().generateAppearanceData();
			for (int key : clan.getVars().keySet())
				player.getPackets().setClanVar(key, clan.getVar(key));
			if (player.getInterfaceManager().topOpen(1096))
				WorldTasks.delay(0, () -> player.getPackets().sendRunScript(4295));
		}
	}
	
	public static void syncClanToLobby(Clan clan) {
		syncClanToLobby(clan, null);
	}
	
	public static void syncClanToLobby(Clan clan, Runnable done) {
		LobbyCommunicator.updateClan(clan, res -> {
			if (res == null) {
				if (done != null)
					done.run();
				return;
			}
			CACHED_CLANS.put(res.getName(), res);
			if (done != null)
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
	
	public static ButtonClickHandler handleClanChatButtons = new ButtonClickHandler(1110, e -> {
		if (e.getPlayer().hasRights(Rights.DEVELOPER))
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
	});

	public static ButtonClickHandler handleClanFlagButtons = new ButtonClickHandler(1089, e -> {
		if (e.getPlayer().getClan() == null)
			return;
		switch(e.getComponentId()) {
		case 30 -> e.getPlayer().getTempAttribs().setI("clanflagselection", e.getSlotId());
		case 26 -> {
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
	});

	public static ButtonClickHandler handleClanSettingsButtonsMain = new ButtonClickHandler(1096, e -> {
		if (e.getPlayer().hasRights(Rights.DEVELOPER))
			e.getPlayer().sendMessage("handleClanSettingsButtonsMain: " + e.getComponentId() + " - " + e.getSlotId() + " - " + e.getPacket());
		switch(e.getComponentId()) {
		case 41 -> viewClanmateDetails(e.getPlayer(), e.getSlotId());
		case 94 -> setClanSetting(e.getPlayer(), ClanSetting.GUESTS_CAN_ENTER_CC, (int) e.getPlayer().getClan().getSetting(ClanSetting.GUESTS_CAN_ENTER_CC) == 0 ? 1 : 0);
		case 95 -> setClanSetting(e.getPlayer(), ClanSetting.GUESTS_CAN_TALK_CC, (int) e.getPlayer().getClan().getSetting(ClanSetting.GUESTS_CAN_TALK_CC) == 0 ? 1 : 0);
		case 96 -> {
			boolean set = ((int) e.getPlayer().getClan().getSetting(ClanSetting.IS_RECRUITING)) == 1;
			setClanSetting(e.getPlayer(), ClanSetting.IS_RECRUITING, set ? 0 : 1);
			syncClanToLobby(e.getPlayer().getClan());
		}
		case 97 -> {
			boolean set = ((int) e.getPlayer().getClan().getSetting(ClanSetting.USES_CLANTIMEZONE)) == 1;
			setClanSetting(e.getPlayer(), ClanSetting.USES_CLANTIMEZONE, set ? 0 : 1);
			syncClanToLobby(e.getPlayer().getClan());
		}
		case 113 -> showClanSettingsClanMates(e.getPlayer());
		case 120 -> showClanSettingsSettings(e.getPlayer());
		case 124 -> openClanMotifInterface(e.getPlayer());
		case 131 -> openClanMottoInterface(e.getPlayer());
		case 160 -> openKeywordEditor(e.getPlayer());
		case 222 -> {
			if (!canEditSettings(e.getPlayer()))
				return;
			setVar(e.getPlayer().getClan(), 2811, Utils.clampI(e.getSlotId(), 0, 4)); //signpost permissions
		}
		case 207 -> setClanSetting(e.getPlayer(), ClanSetting.GUEST_CITADEL_ACCESS, Utils.clampI(e.getSlotId(), 0, 3));
		case 240 -> setClanSetting(e.getPlayer(), ClanSetting.GAME_TIME, -720 + e.getSlotId() * 10);
		case 290 -> setClanSetting(e.getPlayer(), ClanSetting.HOME_WORLD, Utils.clampI(e.getSlotId(), 0, 200));
		case 297 -> openForumThreadInterface(e.getPlayer());
		case 346 -> openNationalFlagInterface(e.getPlayer());
		case 386 -> showClanSettingsPermissions(e.getPlayer());
		case 489 -> selectPermissionTab(e.getPlayer(), 1);
		case 498 -> selectPermissionTab(e.getPlayer(), 2);
		case 506 -> selectPermissionTab(e.getPlayer(), 3);
		case 514 -> selectPermissionTab(e.getPlayer(), 4);
		case 522 -> selectPermissionTab(e.getPlayer(), 5);
		
		case 262 -> e.getPlayer().getTempAttribs().setI("editClanMateJob", e.getSlotId());
		case 276 -> e.getPlayer().getTempAttribs().setI("editClanMateRank", e.getSlotId());
		case 309 -> kick(e.getPlayer());
		case 318 -> saveClanmateDetails(e.getPlayer());
		case 366 -> e.getPlayer().getPackets().sendVarc(1516, e.getSlotId());
		case 558 -> {
			ClanRank rank = e.getPlayer().getTempAttribs().getO("permissionRankEditing");
			if (rank == null) {
				e.getPlayer().sendMessage("Please select a rank before setting the chat ranking.");
				return;
			}
			e.getPlayer().getClan().setCcChatRank(rank);
		}
		case 570 -> {
			ClanRank rank = e.getPlayer().getTempAttribs().getO("permissionRankEditing");
			if (rank == null) {
				e.getPlayer().sendMessage("Please select a rank before setting the kick ranking.");
				return;
			}
			e.getPlayer().getClan().setCcKickRank(rank);
		}
		case 534, 547, 752, 739, 483, 659, 581, 592, 648, 684, 671, 
			 793, 696, 603, 614, 782, 625, 815, 720, 708, 804 -> togglePermissionSetting(e.getPlayer(), e.getComponentId());
		default -> {
			if (e.getComponentId() >= 395 && e.getComponentId() <= 475) {
				int selectedRank = (e.getComponentId() - 395) / 8;
				if (selectedRank == 10)
					selectedRank = 125;
				else if (selectedRank > 5)
					selectedRank = 100 + selectedRank - 6;
				selectPermissionRank(e.getPlayer(), ClanRank.forId(selectedRank));
			}
		}
		}
	});

	public static ButtonClickHandler handleMotifButtons = new ButtonClickHandler(1105, e -> {
		if (e.getPlayer().hasRights(Rights.DEVELOPER))
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
		else if (e.getComponentId() == 120) {
			syncClanToLobby(e.getPlayer().getClan());
			e.getPlayer().stopAll();
		}
	});
	
	private static BiMap<ClanSetting, Integer> KEYWORD_INDICES = HashBiMap.create(10);
	
	static {
		KEYWORD_INDICES.put(ClanSetting.KEYWORD0_CATEGORY, 9155);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD0_INDEX, 9156);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD1_CATEGORY, 9157);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD1_INDEX, 9158);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD2_CATEGORY, 9159);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD2_INDEX, 9160);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD3_CATEGORY, 9161);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD3_INDEX, 9162);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD4_CATEGORY, 9163);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD4_INDEX, 9164);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD5_CATEGORY, 9165);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD5_INDEX, 9166);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD6_CATEGORY, 9167);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD6_INDEX, 9168);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD7_CATEGORY, 9169);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD7_INDEX, 9170);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD8_CATEGORY, 9171);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD8_INDEX, 9172);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD9_CATEGORY, 9173);
		KEYWORD_INDICES.put(ClanSetting.KEYWORD9_INDEX, 9174);
	}
	
	public static ButtonClickHandler handleKeywordButtons = new ButtonClickHandler(1097, e -> {
		if (e.getPlayer().hasRights(Rights.DEVELOPER))
			e.getPlayer().sendMessage("handleKeywordButtons: " + e.getComponentId() + " - " + e.getSlotId() + " - " + e.getPacket());
		switch(e.getComponentId()) {
		case 193 -> e.getPlayer().getVars().setVar(2136, e.getSlotId());
		case 207 -> {
			int enumId = EnumDefinitions.getEnum(3703).getIntValue(e.getPlayer().getVars().getVar(2136));
			if (enumId == -1) {
				e.getPlayer().getTempAttribs().removeI("keywordTempVal");
				return;
			}
			EnumDefinitions infoDef = EnumDefinitions.getEnum(enumId);
			if (infoDef.getStringValue(e.getSlotId()) != null && !infoDef.getStringValue(e.getSlotId()).equals("null"))
				e.getPlayer().getTempAttribs().setI("keywordTempVal", e.getSlotId());
			else
				e.getPlayer().getTempAttribs().removeI("keywordTempVal");
		}
		case 15 -> {
			int category = e.getPlayer().getVars().getVar(2136);
			if (category <= 0)
				return;
			int index = e.getPlayer().getTempAttribs().getI("keywordTempVal");
			for (ClanSetting setting : KEYWORD_INDICES.keySet()) {
				if (setting.name().contains("INDEX"))
					continue;
				int varbit = KEYWORD_INDICES.get(setting);
				if (e.getPlayer().getVars().getVarBit(varbit) <= 0) {
					e.getPlayer().getVars().setVarBit(varbit, category);
					e.getPlayer().getVars().setVarBit(varbit+1, index);
					break;
				}
			}
		}
		case 160, 171, 172, 173, 174, 175, 176, 177, 178, 179 -> {
			int index = e.getComponentId() == 160 ? 0 : e.getComponentId() - 170;
			int baseVar = 9155 + (index * 2);
			e.getPlayer().getVars().setVarBit(baseVar, 0);
			e.getPlayer().getVars().setVarBit(baseVar+1, 0);
		}
		case 42 -> {
			for (ClanSetting setting : KEYWORD_INDICES.keySet())
				setClanSetting(e.getPlayer(), setting, e.getPlayer().getVars().getVarBit(KEYWORD_INDICES.get(setting)));
			e.getPlayer().stopAll();
			syncClanToLobby(e.getPlayer().getClan());
		}
		}
	});
	
	public static ButtonClickHandler handleCloseButton = new ButtonClickHandler(1079, e -> e.getPlayer().closeInterfaces());

	public static ButtonClickHandler handleInviteInter = new ButtonClickHandler(1095, e -> {
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
	});
	
	public static InterfaceOnPlayerHandler handleInvite = new InterfaceOnPlayerHandler(false, new int[] { 1110 }, e -> {
		if (e.getComponentId() == 87) {
			if (e.getTarget().getSocial().getClanName() != null) {
				e.getPlayer().sendMessage(e.getTarget().getDisplayName() + " is already in a clan.");
				return;
			}
			e.getPlayer().sendMessage("Sending clan invite to " + e.getTarget().getDisplayName() + ".");
			e.getTarget().getPackets().sendClanInviteMessage(e.getPlayer());
		}
	});
	
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
		LobbyCommunicator.getAccount(username, acc -> {
			if (acc == null) {
				player.sendMessage("Error finding player profile for target character.");
				return;
			}
			viewClanmateDetails(player, acc.getDisplayName(), member);
			player.getTempAttribs().removeI("editClanMateRank");
			player.getTempAttribs().removeI("editClanMateJob");
			if (player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN))
				player.getTempAttribs().setO("editClanMate", username);
		});
		player.getTempAttribs().removeI("editClanMateRank");
		player.getTempAttribs().removeI("editClanMateJob");
		if (player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN))
			player.getTempAttribs().setO("editClanMate", username);
	}
	
	protected static void togglePermissionSetting(Player player, int componentId) {
		ClanRank rank = player.getTempAttribs().getO("permissionRankEditing");
		if (rank == null) {
			player.sendMessage("Please select a rank to edit before editing permissions.");
			return;
		}
		String settingName = switch(componentId) {
		case 534 -> "PERMISSION_LOCK_KEEP";
		case 547 -> "PERMISSION_LOCK_CITADEL";
		case 739 -> "PERMISSION_ENTER_KEEP";
		case 752 -> "PERMISSION_ENTER_CITADEL";
		case 483 -> "PERMISSION_INVITE";
		case 659 -> "PERMISSION_LEAD_CLANWARS";
		case 581 -> "PERMISSION_SIGNPOST";
		case 592 -> "PERMISSION_NOTICEBOARD";
		case 648 -> "PERMISSION_START_BATTLE";
		case 671 -> "PERMISSION_CALL_VOTE";
		case 684 -> "PERMISSION_BEGIN_MEETING";
		case 793 -> "PERMISSION_THEATER";
		case 696 -> "PERMISSION_PARTY_ROOM";
		case 603 -> "PERMISSION_CITADEL_UPGRADE";
		case 614 -> "PERMISSION_CITADEL_DOWNGRADE";
		case 625 -> "PERMISSION_EDIT_BATTLEFIELD";
		case 782 -> "PERMISSION_CITADEL_LANGUAGE";
		case 815 -> "PERMISSION_CHECK_RESOURCES";
		case 720 -> "PERMISSION_BUILD_TIME";
		case 804 -> "PERMISSION_LOCK_PLOTS";
		case 708 -> "PERMISSION_RESOURCE_GATHER";
		default -> null;
		};
		if (settingName == null) {
			player.sendMessage("Unknown clan setting for component: " + componentId);
			return;
		}
		ClanSetting setting = null;
		for (ClanSetting s : ClanSetting.values()) {
			if (s.name().equals(rank.name() + "_" + settingName)) {
				setting = s;
				break;
			}
		}
		if (setting == null) {
			player.sendMessage("Cannot edit permission " + settingName + " for rank " + rank + ".");
			return;
		}
		player.getClan().setSetting(setting, (int) player.getClan().getSetting(setting) == 1 ? 0 : 1);
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
	
	private static void saveClanmateDetails(Player player) {
		int jobId = player.getTempAttribs().getI("editClanMateJob", -1);
		int rankId = player.getTempAttribs().getI("editClanMateRank", -1);
		String editingUser = player.getTempAttribs().getO("editClanMate");
		ClanRank currRank = player.getClan().getRank(editingUser);
		ClanRank newRank = ClanRank.forId(rankId);
		ClanRank playerRank = player.getClan().getRank(player.getUsername());
		if (!player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN)) {
			player.sendMessage("Only clan admins can edit ranks and jobs.");
			return;
		}
		if (newRank == ClanRank.NONE || newRank == ClanRank.OWNER)
			return;
		if (newRank != currRank) {
			if (currRank.ordinal() >= playerRank.ordinal()) {
				player.sendMessage("You can only edit the rank of players with lesser rank than you.");
				return;
			}
			if (newRank.ordinal() > playerRank.ordinal()) {
				player.sendMessage("You cannot promote someone to a rank higher than your own.");
				return;
			}
		}
		if (currRank == ClanRank.OWNER && newRank != ClanRank.OWNER) {
			String deputyOwner = null;
			for (String memberName : player.getClan().getMembers().keySet()) {
				if (player.getClan().getMembers().get(memberName).getRank() == ClanRank.DEPUTY_OWNER) {
					deputyOwner = memberName;
					break;
				}
			}
			if (deputyOwner == null) {
				if (player != null)
					player.sendMessage("You are currently the clan owner, you must at least have one deputy owner in the clan to demote yourself.");
				return;
			}
			player.getClan().setClanLeaderUsername(deputyOwner);
			player.getClan().getMembers().get(deputyOwner).setRank(ClanRank.OWNER);
		}
		player.getClan().getMembers().get(editingUser).setRank(newRank);
		if (jobId != -1 && EnumDefinitions.getEnum(3720).getStringValue(jobId) != null)
			player.getClan().getMembers().get(editingUser).setJob(jobId);
		syncClanToLobby(player.getClan());
	}

	private static void kick(Player player) {
		String editingUser = player.getTempAttribs().getO("editClanMate");
		player.sendOptionDialogue("Are you sure you would like to kick this player from the clan?", ops -> {
			ops.add("Yes, I am sure I want to kick them.", () -> LobbyCommunicator.forwardPacket(player, new ClanKickMember(editingUser), res -> {
				if (!res)
					player.sendMessage("Failed to send kick request to social server.");
			}));
			ops.add("Nevermind");
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
		player.getPackets().setIFRightClickOps(1096, 366, 0, 127, 0); // unlocks rank filters
		player.getPackets().setIFRightClickOps(1096, 222, 0, 4, 0); // unlocks signpost permissions
		player.getPackets().setIFRightClickOps(1096, 207, 0, 3, 0); // unlocks guest access to citadel settings
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
		if (!player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN)) {
			player.sendMessage("Only clan admins can edit the clan settings.");
			return;
		}
		player.stopAll();
		player.getInterfaceManager().sendInterface(1089);
		player.getPackets().setIFRightClickOps(1089, 30, 0, 241, 0);
		player.setCloseInterfacesEvent(() -> openSettings(player));
	}
	
	public static void openKeywordEditor(Player player) {
		if (player.getClan() == null)
			return;
		if (!player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN)) {
			player.sendMessage("Only clan admins can edit the clan settings.");
			return;
		}
		player.stopAll();
		player.getTempAttribs().removeI("keywordTempVal");
		player.getVars().setVar(2136, 0);
		for (int var : KEYWORD_INDICES.inverse().keySet())
			player.getVars().setVarBit(var, (int) player.getClan().getSetting(KEYWORD_INDICES.inverse().get(var)));
		player.getInterfaceManager().sendInterface(1097);
		player.getPackets().setIFRightClickOps(1097, 193, 0, 100, 0);
		player.getPackets().setIFRightClickOps(1097, 207, 0, 100, 0);
		player.setCloseInterfacesEvent(() -> openSettings(player));
	}

	public static void openForumThreadInterface(Player player) {
		if (player.getClan() == null)
			return;
		if (!player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN))
			return;
		player.getInterfaceManager().sendChatBoxInterface(1100);
		player.sendInputForumQFC(qfc -> {
			player.getInterfaceManager().closeChatBoxInterface();
			long val = -1;
			try {
				val = Long.valueOf(qfc, 36);
			} catch(NumberFormatException e) {
				player.sendMessage("Please enter a valid formatted code.");
				return;
			}
			if (setClanSetting(player, ClanSetting.FORUM_QFC, val))
				syncClanToLobby(player.getClan(), () -> {});
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
			if (setClanSetting(player, ClanSetting.MOTTO, motto))
				syncClanToLobby(player.getClan(), () -> {});
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
		player.setCloseInterfacesEvent(() -> openSettings(player));
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
			setClanSetting(player, ClanSetting.MOTIF_TOP_ICON, slot+1);
		else
			setClanSetting(player, ClanSetting.MOTIF_BOTTOM_ICON, slot+1);
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
	
	public static boolean setClanSetting(Player player, ClanSetting setting, Object value) {
		if (!canEditSettings(player))
			return false;
		if (player.getClan().getSetting(setting) == value)
			return false;
		player.getClan().setSetting(setting, value);
		return true;
	}
	
	public static boolean canEditSettings(Player player) {
		if (player.getClan() == null) {
			player.sendMessage("Error getting clan info to edit.");
			return false;
		}
		if (!player.getClan().hasPermissions(player.getUsername(), ClanRank.ADMIN)) {
			player.sendMessage("Only clan administrators can edit clan settings.");
			return false;
		}
		return true;
	}
	
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
