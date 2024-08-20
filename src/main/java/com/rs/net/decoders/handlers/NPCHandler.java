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
package com.rs.net.decoders.handlers;

import com.rs.Settings;
import com.rs.engine.quest.Quest;
import com.rs.game.content.Effect;
import com.rs.game.content.PlayerLook;
import com.rs.game.content.Skillcapes;
import com.rs.game.content.death.GraveStone;
import com.rs.game.content.minigames.creations.StealingCreationShop;
import com.rs.game.content.minigames.ectofuntus.Ectofuntus;
import com.rs.game.content.pets.Pet;
import com.rs.game.content.quests.piratestreasure.CustomsOfficerPiratesTreasureD;
import com.rs.game.content.quests.piratestreasure.PiratesTreasure;
import com.rs.game.content.skills.construction.EstateAgentDialogue;
import com.rs.game.content.skills.construction.ServantDialogue;
import com.rs.game.content.skills.slayer.npcs.ConditionalDeath;
import com.rs.game.content.skills.slayer.npcs.MutatedZygomite;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.skills.thieving.PickPocketAction;
import com.rs.game.content.skills.thieving.PickPocketableNPC;
import com.rs.game.content.transportation.BoatingD;
import com.rs.game.content.transportation.TravelMethods;
import com.rs.game.content.transportation.TravelMethods.Carrier;
import com.rs.game.content.world.areas.rellekka.npcs.FremennikShipmaster;
import com.rs.game.content.world.unorganized_dialogue.TanningD;
import com.rs.game.content.world.unorganized_dialogue.skillmasters.GenericSkillcapeOwnerD;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.interactions.StandardEntityInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.NPCInteractionDistanceEvent;
import com.rs.utils.NPCExamines;
import com.rs.utils.Ticks;

public class NPCHandler {

	public static void handleExamine(final Player player, final NPC npc) {
		if (player.hasRights(Rights.DEVELOPER)) {
			player.sendMessage("NPC - [id=" + npc.getId() + ", loc=[" + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane() + "]]. " + npc.getDefinitions().respawnDirection);
			player.sendMessage("HP: " + npc.getMaxHitpoints() + " Crush Def: " + npc.getDefinitions().getCrushDef() + " Slash Def: " +
					npc.getDefinitions().getSlashDef() + " Stab Def: " + npc.getDefinitions().getStabDef() + " Range Def: "+ npc.getDefinitions().getRangeDef() +
					" Mage Def: " + npc.getDefinitions().getMagicDef());
			if (npc.getDefinitions().transformTo != null)
				player.sendMessage(npc.getDefinitions().getConfigInfoString());
			player.sendMessage("Spawn tile [" + npc.getRespawnTile().getX() + ", " + npc.getRespawnTile().getY() + ", " + npc.getRespawnTile().getPlane() + "]]. ");
		}
		player.getPackets().sendNPCMessage(0, 0xFFFFFF, npc, NPCExamines.getExamine(npc, player) + " ("+npc.getId()+")");
		if (Settings.getConfig().isDebug() && (npc.getDefinitions().hasAttackOption() || npc.getDefinitions().hasOption("Investigate")))
			player.sendOptionDialogue("Would you like to check the drops on this monster?", ops -> {
				ops.add("Show drops (1,000 kills)", () -> NPC.displayDropsFor(player, npc.getId(), 1000));
				ops.add("Show drops (5,000 kills)", () -> NPC.displayDropsFor(player, npc.getId(), 5000));
				ops.add("Show drops (10,000 kills)", () -> NPC.displayDropsFor(player, npc.getId(), 10000));
				ops.add("Nevermind");
			});
		Logger.debug(NPCHandler.class, "handleExamine", "Examined NPC: index: " + npc.getIndex() + ", id: " + npc.getId());
	}

	public static void handleOption1(final Player player, final NPC npc) {
		player.stopAll(true);
		if (PluginManager.handle(new NPCClickEvent(player, npc, 1, false)))
			return;

		Object dist = PluginManager.getObj(new NPCInteractionDistanceEvent(player, npc));
		int distance = 0;
		if (dist != null)
			distance = (int) dist;

		player.getInteractionManager().setInteraction(new StandardEntityInteraction(npc, distance, () -> {
			if (!player.getControllerManager().processNPCClick1(npc))
				return;
			npc.resetWalkSteps();
			player.faceEntityTile(npc);
			npc.faceEntityTile(player);

			if (player.getTreasureTrailsManager().useNPC(npc))
				return;
			Object[] shipAttributes = BoatingD.getBoatForShip(player, npc.getId());
			if (shipAttributes != null) {
				player.startConversation(new BoatingD(player, npc.getId()));
				return;
			}
			if (npc instanceof GraveStone grave) {
				grave.sendGraveInscription(player);
				npc.resetDirection();
				return;
			}
			else if (PluginManager.handle(new NPCClickEvent(player, npc, 1, true))) {

			} else if (npc instanceof Pet pet) {
				if (pet != player.getPet()) {
					player.sendMessage("This isn't your pet.");
					return;
				}
				player.setNextAnimation(new Animation(827));
				pet.pickup();
			} else {
				player.sendMessage("Nothing interesting happens." + npc.getId());
				Logger.debug(NPCHandler.class, "handleOption1", "NPC: " + npc.getId() + ", (" + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane() + ")");
			}
		}));
	}

	public static void handleOption2(Player player, NPC npc) {
		player.stopAll(true);
		if (PluginManager.handle(new NPCClickEvent(player, npc, 2, false)))
			return;
		player.getInteractionManager().setInteraction(new StandardEntityInteraction(npc, 0, () -> PluginManager.handle(new NPCClickEvent(player, npc, 2, true))));
	}

	public static void handleOption3(final Player player, final NPC npc) {
		if (player.isLocked() && player.getActionManager().getAction() != null && player.getActionManager().getAction() instanceof PickPocketAction)
			return;
		player.stopAll(true);

		if (PluginManager.handle(new NPCClickEvent(player, npc, 3, false)))
			return;

		Object dist = PluginManager.getObj(new NPCInteractionDistanceEvent(player, npc));
		int distance = 0;
		if (dist != null)
			distance = (int) dist;

		player.getInteractionManager().setInteraction(new StandardEntityInteraction(npc, distance, () -> {
			if (!player.getControllerManager().processNPCClick2(npc))
				return;
			player.faceEntityTile(npc);
			npc.faceEntityTile(player);

			if (player.getTreasureTrailsManager().useNPC(npc))
				return;

			npc.resetWalkSteps();

			Object[] shipAttributes = BoatingD.getBoatForShip(player, npc.getId());
			if (shipAttributes != null) {
				if(npc.getId() == 380 && player.getQuestManager().getStage(Quest.PIRATES_TREASURE) == PiratesTreasure.SMUGGLE_RUM) {
					player.startConversation(new CustomsOfficerPiratesTreasureD(player).getStart());
					return;
				}
				TravelMethods.sendCarrier(player, (Carrier) shipAttributes[0], (boolean) shipAttributes[1]);
				return;
			}

			if (npc instanceof Familiar && npc.getDefinitions().hasOption("cure")) {
				if (player.getFamiliar() != npc) {
					player.sendMessage("That isn't your familiar.");
					return;
				}
				if (!player.getPoison().isPoisoned()) {
					player.sendMessage("Your arent poisoned or diseased.");
					return;
				}
				player.getFamiliar().drainSpec(2);
				player.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(2));
				return;
			}
			else if (npc instanceof GraveStone grave) {
				grave.repair(player, false);
			}
			else if (npc.getId() == 14849 && npc instanceof ConditionalDeath cd)
				cd.useHammer(player);
			else if(PluginManager.handle(new NPCClickEvent(player, npc, 3, true)));
			else {
				player.sendMessage("Nothing interesting happens." + npc.getId());
				Logger.debug(NPCHandler.class, "handleOption2", "NPC: " + npc.getId() + ", (" + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane() + ") op: " + npc.getDefinitions(player).getOption(2));
			}
		}));
	}

	public static void handleOption4(final Player player, final NPC npc) {
		player.stopAll(true);

		if (PluginManager.handle(new NPCClickEvent(player, npc, 4, false)))
			return;

		Object dist = PluginManager.getObj(new NPCInteractionDistanceEvent(player, npc));
		int distance = 0;
		if (dist != null)
			distance = (int) dist;

		player.getInteractionManager().setInteraction(new StandardEntityInteraction(npc, distance, () -> {
			if (!player.getControllerManager().processNPCClick3(npc))
				return;
			npc.resetWalkSteps();
			player.faceEntityTile(npc);
			npc.faceEntityTile(player);

			if (npc instanceof GraveStone grave) {
				grave.repair(player, true);
				npc.resetDirection();
				return;
			}
			else if (PluginManager.handle(new NPCClickEvent(player, npc, 4, true))) {
			}
			else
				player.sendMessage("Nothing interesting happens." + npc.getId());
		}));
		Logger.debug(NPCHandler.class, "handleOption4", "NPC: " + npc.getId() + ", (" + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane() + ")");
	}

	public static void handleOption5(final Player player, final NPC npc) {
		player.stopAll(true);

		PluginManager.handle(new NPCClickEvent(player, npc, 5, false));

		Object dist = PluginManager.getObj(new NPCInteractionDistanceEvent(player, npc));
		int distance = 0;
		if (dist != null)
			distance = (int) dist;

		player.getInteractionManager().setInteraction(new StandardEntityInteraction(npc, distance, () -> {
			if (!player.getControllerManager().processNPCClick3(npc))
				return;
			npc.resetWalkSteps();
			player.faceEntityTile(npc);
			npc.faceEntityTile(player);

			if (npc instanceof GraveStone grave) {
				grave.demolish(player);
				npc.resetDirection();
				return;
			}

			if (PluginManager.handle(new NPCClickEvent(player, npc, 5, true)))
				return;
			player.sendMessage("Nothing interesting happens. " + npc.getId());
			Logger.debug(NPCHandler.class, "handleOption5", "NPC: " + npc.getId() + ", (" + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane() + ")");
		}));
	}

	public static int getShopIdForNpc(int npcId) {
		return switch (npcId) {
			case 1254 -> // Razmire's General Store", Razmire Keelgan. (3488, 3296, 0)
					-1; // TODO get burgh de rott transforming npc spawns
			case 1866 -> // Pollniveach General Store", Market Seller. (3359, 2983, 0)
					-1; // TODO
			case 3166 -> // Dodgy Mike's Second-hand Clothing", Mike. (3689, 2977, 0)
					-1; // TODO
			case 2162 -> // Vermundi's Clothes Stall", Vermundia. (2887, 10189, 0)
					-1; // TODO
			case 517 -> // Shilo Village Fishing Shop", Fernahei. (2871, 2968, 0)
					-1; // TODO implemented in Karamja.java
			case 1433 -> // Solihib's food stall", Solihib. (2769, 2789, 0)
					-1; // TODO
			case 1862 -> // Ali's Discount Wares", Ali. (3301, 3211, 0)
					-1; // TODO
			case 1435 -> // Tutab's Magical Market", Tutab. (2757, 2770, 0)
					-1; // TODO
			case 1980 -> // The Spice is Right", Embalmer. (3286, 2805, 0)
					-1; // TODO

			default -> -1;
		};
	}
}