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

import com.rs.engine.quest.Quest;
import com.rs.game.content.Effect;
import com.rs.game.content.PlayerLook;
import com.rs.game.content.Skillcapes;
import com.rs.game.content.death.GraveStone;
import com.rs.game.content.minigames.creations.StealingCreationShop;
import com.rs.game.content.minigames.ectofuntus.Ectofuntus;
import com.rs.game.content.minigames.pest.CommendationExchange;
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
import com.rs.game.content.world.unorganized_dialogue.FremennikShipmaster;
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
		}
		player.getPackets().sendNPCMessage(0, 0xFFFFFF, npc, NPCExamines.getExamine(npc, player) + " ("+npc.getId()+")");
		if (npc.getDefinitions().hasAttackOption() || npc.getDefinitions().hasOption("Investigate"))
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
			player.faceEntity(npc);
			npc.faceEntity(player);

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
			if (player.getTreasureTrailsManager().useNPC(npc))
				return;
			if (npc.getId() == 2825)
				player.sendOptionDialogue("Would you like to travel to Braindeath Island?", ops -> {
					ops.add("Yes", () -> player.setNextTile(Tile.of(2163, 5112, 1)));
					ops.add("No");
				});
			else if (npc.getId() == 2826)
				player.sendOptionDialogue("Would you like to travel back to Port Phasmatys?", ops -> {
					ops.add("Yes", () -> player.setNextTile(Tile.of(3680, 3536, 0)));
					ops.add("No");
				});
			else if (npc.getId() == 9707)
				player.startConversation(new FremennikShipmaster(player, npc.getId(), true));
			else if (npc.getId() == 8269)
				player.startConversation(new GenericSkillcapeOwnerD(player, 8269, Skillcapes.Strength));
			else if (npc.getId() == 705)
				player.startConversation(new GenericSkillcapeOwnerD(player, 705, Skillcapes.Defence));
			else if (npc.getId() == 961)
				player.startConversation(new GenericSkillcapeOwnerD(player, 961, Skillcapes.Constitution));
			else if (npc.getId() == 682)
				player.startConversation(new GenericSkillcapeOwnerD(player, 682, Skillcapes.Ranging));
			else if (npc.getId() == 802)
				player.startConversation(new GenericSkillcapeOwnerD(player, 802, Skillcapes.Prayer));
			else if (npc.getId() == 1658)
				player.startConversation(new GenericSkillcapeOwnerD(player, 1658, Skillcapes.Magic));
			else if (npc.getId() == 847)
				player.startConversation(new GenericSkillcapeOwnerD(player, 847, Skillcapes.Cooking));
			else if (npc.getId() == 4906)
				player.startConversation(new GenericSkillcapeOwnerD(player, 4906, Skillcapes.Woodcutting));
			else if (npc.getId() == 575)
				player.startConversation(new GenericSkillcapeOwnerD(player, 575, Skillcapes.Fletching));
			else if (npc.getId() == 308)
				player.startConversation(new GenericSkillcapeOwnerD(player, 308, Skillcapes.Fishing));
			else if (npc.getId() == 4946)
				player.startConversation(new GenericSkillcapeOwnerD(player, 4946, Skillcapes.Firemaking));
			else if (npc.getId() == 805)
				player.startConversation(new GenericSkillcapeOwnerD(player, 805, Skillcapes.Crafting));
			else if (npc.getId() == 3295)
				player.startConversation(new GenericSkillcapeOwnerD(player, 3295, Skillcapes.Mining));
			else if (npc.getId() == 437)
				player.startConversation(new GenericSkillcapeOwnerD(player, 437, Skillcapes.Agility));
			else if (npc.getId() == 2270)
				player.startConversation(new GenericSkillcapeOwnerD(player, 2270, Skillcapes.Thieving));
			else if (npc.getId() == 3299)
				player.startConversation(new GenericSkillcapeOwnerD(player, 3299, Skillcapes.Farming));
			else if (npc.getId() == 13632)
				player.startConversation(new GenericSkillcapeOwnerD(player, 13632, Skillcapes.Runecrafting));
			else if (npc.getId() == 5113)
				player.startConversation(new GenericSkillcapeOwnerD(player, 5113, Skillcapes.Hunter));
			else if (npc.getId() == 9713)
				player.startConversation(new GenericSkillcapeOwnerD(player, 9713, Skillcapes.Dungeoneering));
			else if (npc.getId() == 9708 || npc.getId() == 14847)
				player.startConversation(new FremennikShipmaster(player, npc.getId(), false));
			else if (npc.getId() == 6715 || npc.getId() == 14862)
				player.startConversation(new EstateAgentDialogue(player, npc.getId()));
			else if (npc.getId() == 3344 || npc.getId() == 3345)
				MutatedZygomite.transform(player, npc);
			else if (npc.getId() == 4236 || npc.getId() == 4238 || npc.getId() == 4240 || npc.getId() == 4242 || npc.getId() == 4244)
				player.startConversation(new ServantDialogue(player, npc));
			else if (npc.getId() == 2824 || npc.getId() == 1041 || npc.getId() == 804)
				player.startConversation(new TanningD(player, npc.getId() == 1041, npc.getId()));
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
		player.getInteractionManager().setInteraction(new StandardEntityInteraction(npc, 0, () -> {
			PluginManager.handle(new NPCClickEvent(player, npc, 2, true));
		}));
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
			player.faceEntity(npc);
			npc.faceEntity(player);

			if (player.getTreasureTrailsManager().useNPC(npc))
				return;

			PickPocketableNPC pocket = PickPocketableNPC.get(npc.getId());
			if (pocket != null) {
				npc.resetDirection();
				player.getActionManager().setAction(new PickPocketAction(npc, pocket));
				return;
			}

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

			if (npc.getId() == 9707)
				FremennikShipmaster.sail(player, true);
			else if (npc.getId() == 1686) {
				if (player.getInventory().hasFreeSlots() && player.unclaimedEctoTokens > 0) {
					player.getInventory().addItem(Ectofuntus.ECTO_TOKEN, player.unclaimedEctoTokens);
					player.unclaimedEctoTokens = 0;
				}
			} else if (npc.getId() == 9708 || npc.getId() == 14847)
				FremennikShipmaster.sail(player, false);
			else if (npc instanceof GraveStone grave) {
				grave.repair(player, false);
				return;
			} else if (npc.getId() == 11267) {
				int[] noteableFish = { 377, 371, 359, 317, 345, 327 };
				for (Item item : player.getInventory().getItems().array()) {
					if (item == null)
						continue;
					for (int id : noteableFish)
						if (item.getId() == id) {
							player.getInventory().deleteItem(item.getId(), 1);
							player.getInventory().addItem(item.getDefinitions().getCertId(), 1);
						}
				}
			} else if (npc.getId() == 8228)
				StealingCreationShop.openInterface(player);
			else if (npc.getId() == 14849 && npc instanceof ConditionalDeath cd)
				cd.useHammer(player);
			else if (npc.getId() == 2824 || npc.getId() == 1041)
				player.startConversation(new TanningD(player, npc.getId() == 1041, npc.getId()));
			else if (npc.getId() == 1843)
				player.setNextTile(Tile.of(2836, 10142, 0));
			else if (npc.getId() == 1844)
				player.setNextTile(Tile.of(2839, 10131, 0));
			else if (npc.getId() == 1419)
				GE.open(player);
			else if (npc.getId() == 2676 || npc.getId() == 599)
				PlayerLook.openMageMakeOver(player);
			else if (npc.getId() == 598)
				PlayerLook.openHairdresserSalon(player);
			else if (PluginManager.handle(new NPCClickEvent(player, npc, 3, true)))
				;
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
			player.faceEntity(npc);
			npc.faceEntity(player);

			if (npc instanceof GraveStone grave) {
				grave.repair(player, true);
				npc.resetDirection();
				return;
			}
			if (npc.getId() == 548)
				PlayerLook.openThessaliasMakeOver(player);
			else if (npc.getId() == 1526)
				player.getInterfaceManager().sendInterface(60);
			else if (PluginManager.handle(new NPCClickEvent(player, npc, 4, true)))
				return;
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
			player.faceEntity(npc);
			npc.faceEntity(player);

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
		switch (npcId) {
		case 1254: // Razmire's General Store", Razmire Keelgan. (3488, 3296, 0)
			return -1; // TODO get burgh de rott transforming npc spawns
		case 1866: // Pollniveach General Store", Market Seller. (3359, 2983, 0)
			return -1; // TODO
		case 3166: // Dodgy Mike's Second-hand Clothing", Mike. (3689, 2977, 0)
			return -1; // TODO
		case 2162: // Vermundi's Clothes Stall", Vermundia. (2887, 10189, 0)
			return -1; // TODO
		case 517: // Shilo Village Fishing Shop", Fernahei. (2871, 2968, 0)
			return -1; // TODO
		case 1433: // Solihib's food stall", Solihib. (2769, 2789, 0)
			return -1; // TODO
		case 1862: // Ali's Discount Wares", Ali. (3301, 3211, 0)
			return -1; // TODO
		case 1435: // Tutab's Magical Market", Tutab. (2757, 2770, 0)
			return -1; // TODO
		case 1980: // The Spice is Right", Embalmer. (3286, 2805, 0)
			return -1; // TODO

		default:
			return -1;
		}
	}
}