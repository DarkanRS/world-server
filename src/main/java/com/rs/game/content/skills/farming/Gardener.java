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
package com.rs.game.content.skills.farming;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public enum Gardener {

	ELSTAN(2323, PatchLocation.Falador_allotment_north, PatchLocation.Falador_allotment_south),
	LYRA(2326, PatchLocation.Canifis_allotment_north, PatchLocation.Canifis_allotment_south),
	DANTAERA(2324, PatchLocation.Catherby_allotment_north, PatchLocation.Catherby_allotment_south),
	KRAGEN(2325, PatchLocation.Ardougne_allotment_north, PatchLocation.Ardougne_allotment_south),

	VASQUEN(2333, PatchLocation.Lumbridge_hops),
	RHONEN(2334, PatchLocation.Seers_Village_hops),
	SELENA(2332, PatchLocation.Yanille_hops),
	FRANCIS(2327, PatchLocation.Entrana_hops),

	DREVEN(2335, PatchLocation.Champions_Guild_bush),
	TARIA(2336, PatchLocation.Rimmington_bush),
	TORRELL(2338, PatchLocation.Ardougne_bush),
	RHAZIEN(2337, PatchLocation.Etceteria_bush),

	FAYETH(2342, PatchLocation.Lumbridge_tree),
	TREZNOR(2341, PatchLocation.Varrock_tree),
	HESKEL(2340, PatchLocation.Falador_tree),
	ALAIN(2339, PatchLocation.Taverly_tree),
	PRISSY_SCILLA(1037, PatchLocation.Gnome_Stronghold_tree),

	BOLONGO(2343, PatchLocation.Gnome_Stronghold_fruit_tree),
	GILETH(2344, PatchLocation.Tree_Gnome_Village_fruit_tree),
	GARTH(2330, PatchLocation.Brimhaven_fruit_tree),
	ELLENA(2331, PatchLocation.Catherby_fruit_tree),
	AMAETHWR(2860, PatchLocation.Lletya_fruit_tree),

	YULF_SQUECKS(4561, PatchLocation.Etceteria_spirit_tree),
	FRIZZY_SKERNIP(4560, PatchLocation.Port_Sarim_spirit_tree),
	PRAISTAN_EBOLA(4562, PatchLocation.Brimhaven_spirit_tree),

	IMIAGO(871, PatchLocation.Karamja_calquat),

	ZOMBIE_FARMER(13101, PatchLocation.Herblore_Habitat_fruit_tree, PatchLocation.Herblore_Habitat_bush, PatchLocation.Herblore_Habitat_west_herbs);

	static Map<Integer, Gardener> MAP = new HashMap<>();

	static {
		for (Gardener g : Gardener.values())
			MAP.put(g.npcId, g);
	}

	private static Gardener forNPC(int npcId) {
		return MAP.get(npcId);
	}

	private int npcId;
	private PatchLocation[] locations;

	private Gardener(int npcId, PatchLocation... locations) {
		this.npcId = npcId;
		this.locations = locations;
	}

	public static NPCClickHandler handleGardeners = new NPCClickHandler(new Object[] {  871, 1037, 2323, 2324, 2325, 2326, 2327, 2330, 2331, 2332, 2333, 2334, 2335, 2336, 2337, 2338, 2339, 2340, 2341, 2342, 2343, 2344, 2860, 4560, 4561, 4562, 13101 }, e -> {
		Gardener gardener = Gardener.forNPC(e.getNPCId());
		if (e.getOption().contains("Pay")) {
			int patchIdx = e.getOpNum()-3;
			FarmPatch patch = e.getPlayer().getPatch(gardener.locations[patchIdx]);
			protectPatch(e.getPlayer(), e.getNPC(), patch);
			return;
		}
		if (e.getOption().contains("Talk"))
			e.getPlayer().startConversation(new Conversation(e.getPlayer()).addOptions("What would you like to say?", new Options() {
				@Override
				public void create() {
					option("Would you look after my crops for me?", new Dialogue()
							.addNPC(e.getNPCId(), HeadE.CALM, "Of course, what would you like me to protect?")
							.addOptions("What would you like to protect?", new Options() {
								@Override
								public void create() {
									for (PatchLocation loc : gardener.locations)
										option(loc.name().replace("_", " "), new Dialogue().addNext(() -> protectPatch(e.getPlayer(), e.getNPC(), e.getPlayer().getPatch(loc))));
									option("Nevermind.");
								}
							}));
					option("Can you sell me something?", new Dialogue().addOptions(new Options() {
						@Override
						public void create() {
							option("Plant cure (25 gold)", buyItem(e.getPlayer(), e.getNPCId(), 6036, 25));
							option("Compost (35 gold)", buyItem(e.getPlayer(), e.getNPCId(), 6032, 35));
							option("Rake (15 gold)", buyItem(e.getPlayer(), e.getNPCId(), 5341, 15));
							option("Watering can (25 gold)", buyItem(e.getPlayer(), e.getNPCId(), 5331, 25));
							option("Gardening trowel (15 gold)", buyItem(e.getPlayer(), e.getNPCId(), 5325, 15));
							option("Seed dibber (15 gold)", buyItem(e.getPlayer(), e.getNPCId(), 5343, 15));
						}
					}));
					option("That's all, thanks.");
				}
			}));
	});

	private static void protectPatch(Player player, NPC npc, FarmPatch patch) {
		if (patch == null || patch.seed == null) {
			player.startConversation(new Dialogue().addNPC(npc.getId(), HeadE.CHEERFUL, "You don't have anything planted in that patch. Plant something and I might agree to look after it for you."));
			return;
		}
		if (patch.isDiseaseProtected(player)) {
			player.sendMessage("That patch is already being adequately protected.");
			return;
		}
		if (patch.dead) {
			player.startConversation(new Dialogue().addNPC(npc.getId(), HeadE.CALM_TALK, "It looks a little too late for that, huh?"));
			return;
		}
		Item cost = patch.seed.protection;
		if (cost == null) {
			player.startConversation(new Dialogue().addNPC(npc.getId(), HeadE.CALM_TALK, "I'm sorry, but I can't help you with that crop."));
			return;
		}
		if (!player.getInventory().containsItemNoted(cost)) {
			player.startConversation(new Dialogue().addNPC(npc.getId(), HeadE.CHEERFUL, "I'd protect that crop for " + cost.getAmount() + " " + cost.getName() + "."));
			return;
		}
		player.getInventory().deleteItemNoted(cost);
		patch.diseaseProtected = true;
		player.sendMessage(npc.getName(player) + " takes " + cost.getAmount() + " " + cost.getName() + " and agrees to protect the patch for you." );
	}

	private static Dialogue buyItem(Player player, int npcId, int itemId, int cost) {
		return new Dialogue()
				.addPlayer(HeadE.CHEERFUL, "I'd like to buy a " + ItemDefinitions.getDefs(itemId).name.toLowerCase() + " please.")
				.addNPC(npcId, HeadE.CHEERFUL, "Alright, I can sell you one of those for " + cost + " gold.")
				.addOption("Buy a " + ItemDefinitions.getDefs(itemId).name.toLowerCase() + " for " + cost + " gold?", "Yes, please.", "No thanks, that's too much.")
				.addNext(() -> {
					if (!player.getInventory().hasFreeSlots()) {
						player.sendMessage("You don't have enough inventory space.");
						return;
					}
					if (player.getInventory().hasCoins(cost)) {
						player.getInventory().removeCoins(cost);
						player.getInventory().addItem(itemId);
					} else
						player.sendMessage("You don't have enough coins for that.");
				});
	}
}
