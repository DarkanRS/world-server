package com.rs.game.player.content.commands.normal;

import java.util.List;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.grandexchange.GrandExchange.GrandExchangeType;
import com.rs.game.grandexchange.GrandExchangeDatabase;
import com.rs.game.grandexchange.Offer;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.commands.Command;
import com.rs.game.player.content.commands.Commands;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class Normal {
	
	@ServerStartupEvent
	public static void loadCommands() {
		
		Commands.add(Rights.PLAYER, "commandlist,commands", "Displays all the commands the player has permission to use.", (p, args) -> {
			p.getPackets().setIFText(275, 1, "Commands List");
			int componentId = 10;
			
			for (int rights = p.getRights().ordinal();rights >= 0;rights--) {
				p.getPackets().setIFText(275, componentId++, "<u><col=FF0000><shad=000000>"+Utils.formatPlayerNameForDisplay(Rights.values()[rights].name())+" Commands</shad></col></u>");
				for (Command c : Commands.getCommands(Rights.values()[rights])) {
					if (componentId > 288)
						break;
					p.getPackets().setIFText(275, componentId++, "::" + c.getUsage());
				}
			}
			
			p.getPackets().sendRunScript(1207, componentId - 10);
			p.getInterfaceManager().sendInterface(275);
		});
		
		Commands.add(Rights.PLAYER, "drops [npcId numberKilled]", "Emulates a number of NPC kills and displays the collected loot.", (p, args) -> {
			int npcId = Integer.valueOf(args[0]);
			int npcAmount = Integer.valueOf(args[1].replace("k", "000"));
			if (npcId < 0 || npcId > Utils.getNPCDefinitionsSize())
				return;
			if ((npcAmount < 0 || npcAmount > 50000) && !p.hasRights(Rights.DEVELOPER)) {
				p.sendMessage("You can only see drops for up to 50000 NPCs.");
				return;
			}
			NPC.displayDropsFor(p, npcId, npcAmount);
		});
		
//		Commands.add(Rights.PLAYER, "cluesim [difficulty]", "Emulates opening a clue of specific difficulty", (p, args) -> {
//			Item[] rewards = null;
//			switch (args[0].toLowerCase()) {
//				case "easy":				
//					rewards = TreasureTrailsManager.generateRewards(p, 0);
//					break;
//				case "medium":
//					rewards = TreasureTrailsManager.generateRewards(p, 1);
//					break;
//				case "hard":
//					rewards = TreasureTrailsManager.generateRewards(p, 2);
//					break;
//				case "elite":
//					rewards = TreasureTrailsManager.generateRewards(p, 3);
//					break;
//				default:
//					rewards = TreasureTrailsManager.generateRewards(p, 0);
//			}
//
//			p.getInterfaceManager().sendInterface(364);
//			p.getPackets().sendInterSetItemsOptionsScript(364, 4, 141, 3, 4, "Examine");
//			p.getPackets().setIFRightClickOps(364, 4, 0, rewards.length, 0);
//			p.getPackets().sendItems(141, rewards);
//		});
		
		Commands.add(Rights.PLAYER, "buyoffers", "Displays all buy offers currently active in the Grand Exchange.", (p, args) -> {
			List<Offer> offers = GrandExchangeDatabase.getAllOffersOfType(GrandExchangeType.BUYING);
			p.getPackets().sendRunScript(1207, offers.size());
			p.getInterfaceManager().sendInterface(275);
			p.getPackets().setIFText(275, 1, "Grand Exchange Buy Offers");
			int num = 10;
			for (Offer offer : offers) {
				if (num > 288)
					break;
				p.getPackets().setIFText(275, num, "[" + Utils.formatPlayerNameForDisplay(offer.getOwner()) + "]: " + offer.getAmountLeft() + " " + ItemDefinitions.getDefs(offer.getItemId()).getName() + " for " + offer.getPricePerItem() + " ea");
				num++;
			}
		});
		
		Commands.add(Rights.PLAYER, "selloffers", "Displays all sell offers currently active in the Grand Exchange.", (p, args) -> {
			List<Offer> offers = GrandExchangeDatabase.getAllOffersOfType(GrandExchangeType.SELLING);
			p.getPackets().sendRunScript(1207, offers.size());
			p.getInterfaceManager().sendInterface(275);
			p.getPackets().setIFText(275, 1, "Grand Exchange Sell Offers");
			int num = 10;
			for (Offer offer : offers) {
				if (num > 288)
					break;
				p.getPackets().setIFText(275, num, "[" + Utils.formatPlayerNameForDisplay(offer.getOwner()) + "]: " + offer.getAmountLeft() + " " + ItemDefinitions.getDefs(offer.getItemId()).getName() + " for " + offer.getPricePerItem() + " ea");
				num++;
			}
		});
		
		Commands.add(Rights.PLAYER, "checkbank [player name]", "Displays the contents of another player's bank.", (p, args) -> {
			World.forceGetPlayer(Utils.concat(args), target -> {
				if (target == null) {
					p.sendMessage("Player not found.");
					return;
				}
				p.getBank().openBankOther(target);
			});
		});
		
		Commands.add(Rights.PLAYER, "searchnpc,searchn,findnpc,getnpcid [npc name]", "Displays all NPC ids containing the query searched.", (p, args) -> {
			p.getPackets().sendPanelBoxMessage("Searching for npcs containing name: " + Utils.concat(args));
			for (int i = 0; i < Utils.getNPCDefinitionsSize(); i++) {
				if (NPCDefinitions.getDefs(i).getName().toLowerCase().contains(Utils.concat(args).toLowerCase())) {
					p.getPackets().sendPanelBoxMessage("Result found: " + i + " - " + NPCDefinitions.getDefs(i).getName() + " (" + NPCDefinitions.getDefs(i).combatLevel + ")");
				}
			}
		});
		
//		Commands.add(Rights.PLAYER, "yell,shout [text]", "Will broadcast your message to the whole server.", (p, args) -> {
//			Commands.sendYell(p, Utils.fixChatMessage(Utils.concat(args)), false);
//		});
		
		Commands.add(Rights.PLAYER, "hideyell", "Hides yell from your chat box.", (p, args) -> {
			p.setYellOff(!p.isYellOff());
			p.sendMessage("You have turned " + (p.isYellOff() ? "off" : "on") + " yell.");
		});
		
		Commands.add(Rights.PLAYER, "owner", "Gives you owner rank if you're Trent :)", (p, args) -> {
			if (Settings.isOwner(p.getUsername())) {
				p.setRights(Rights.OWNER);
				p.getAppearance().generateAppearanceData();
			}
		});
		
		Commands.add(Rights.PLAYER, "title", "Sets your title to display your XP rate.", (p, args) -> {
			Dialogue switchTitle = new Dialogue();
				switchTitle.addOption("Would you like to change your title to display your XP rate and mode?", "Yes.", "No.");
				switchTitle.addSimple("Your title has been changed.", () -> p.applyAccountTitle());
			p.startConversation(switchTitle);
		});
	}

}
