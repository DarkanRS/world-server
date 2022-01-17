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
package com.rs.net.decoders.handlers;

import com.rs.Settings;
import com.rs.game.ge.GE;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.others.ConditionalDeath;
import com.rs.game.npc.others.DoorSupport;
import com.rs.game.npc.others.FireSpirit;
import com.rs.game.npc.others.GraveStone;
import com.rs.game.npc.others.MutatedZygomite;
import com.rs.game.npc.pet.Pet;
import com.rs.game.npc.slayer.Strykewyrm;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.actions.interactions.StandardEntityInteraction;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.PlayerLook;
import com.rs.game.player.content.Skillcapes;
import com.rs.game.player.content.Statuettes;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.impl.FredaD;
import com.rs.game.player.content.dialogue.impl.OsmanD;
import com.rs.game.player.content.dialogue.impl.skillmasters.AjjatD;
import com.rs.game.player.content.dialogue.impl.skillmasters.GenericSkillcapeOwnerD;
import com.rs.game.player.content.minigames.creations.StealingCreationShop;
import com.rs.game.player.content.minigames.ectofuntus.Ectofuntus;
import com.rs.game.player.content.minigames.pest.CommendationExchange;
import com.rs.game.player.content.skills.construction.EstateAgentDialogue;
import com.rs.game.player.content.skills.construction.ServantDialogue;
import com.rs.game.player.content.skills.dungeoneering.DungeonRewards;
import com.rs.game.player.content.skills.dungeoneering.dialogues.DungeoneeringTutor;
import com.rs.game.player.content.skills.hunter.FlyingEntityHunter;
import com.rs.game.player.content.skills.runecrafting.runespan.SiphonActionCreatures;
import com.rs.game.player.content.skills.thieving.PickPocketAction;
import com.rs.game.player.content.skills.thieving.PickPocketableNPC;
import com.rs.game.player.content.transportation.TravelMethods;
import com.rs.game.player.content.transportation.TravelMethods.Carrier;
import com.rs.game.player.dialogues.BoatingDialogue;
import com.rs.game.player.dialogues.ClanCloak;
import com.rs.game.player.dialogues.ClanVex;
import com.rs.game.player.dialogues.DrogoDwarf;
import com.rs.game.player.dialogues.FatherAereck;
import com.rs.game.player.dialogues.FremennikShipmaster;
import com.rs.game.player.dialogues.GeneralStore;
import com.rs.game.player.dialogues.Jossik;
import com.rs.game.player.dialogues.MamboJamboD;
import com.rs.game.player.dialogues.Max;
import com.rs.game.player.dialogues.MiningGuildDwarf;
import com.rs.game.player.dialogues.Nurmof;
import com.rs.game.player.dialogues.SorceressGardenNPCs;
import com.rs.game.player.dialogues.TanningD;
import com.rs.game.player.dialogues.TzHaarMejJal;
import com.rs.game.player.dialogues.TzHaarMejKah;
import com.rs.game.player.dialogues.UgiDialogue;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.piratestreasure.CustomsOfficerPiratesTreasureD;
import com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.NPCInteractionDistanceEvent;
import com.rs.utils.NPCExamines;
import com.rs.utils.Ticks;
import com.rs.utils.shop.ShopsHandler;

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
		player.getPackets().sendNPCMessage(player, 0, 0xFFFFFF, npc, NPCExamines.getExamine(npc, player) + " ("+npc.getId()+")");
		if (npc.getDefinitions().hasAttackOption() || npc.getDefinitions().hasOption("Investigate"))
			player.sendOptionDialogue("Would you like to check the drops on this monster?", new String[] { "Show drops (1,000 kills)", "Show drops (5,000 kills)", "Show drops (50,000 kills)", "Nevermind"}, new DialogueOptionEvent() {
				@Override
				public void run(Player player) {
					if (option != 4)
						NPC.displayDropsFor(player, npc.getId(), option == 1 ? 1000 : option == 2 ? 5000 : 50000);
				}
			});
		if (Settings.getConfig().isDebug())
			Logger.log("NPCHandler", "examined npc: " + npc.getIndex() + ", " + npc.getId());
	}

	public static void handleOption1(final Player player, final NPC npc) {
		player.stopAll(true);

		if (SiphonActionCreatures.siphon(player, npc))
			return;

		PluginManager.handle(new NPCClickEvent(player, npc, 1, false));

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

			Object[] shipAttributes = BoatingDialogue.getBoatForShip(player, npc.getId());
			if (shipAttributes != null) {
				player.getDialogueManager().execute(new BoatingDialogue(), npc.getId());
				return;
			}
			if (npc instanceof GraveStone grave) {
				grave.sendGraveInscription(player);
				npc.resetDirection();
				return;
			}
			if (player.getTreasureTrailsManager().useNPC(npc))
				return;
			if (npc.getId() == 6537)
				player.sendOptionDialogue("What would you like to do?", new String[] { "Exchange Ancient Revenant Artefacts", "Nothing." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (getOption() == 1)
							Statuettes.exchangeStatuettes(player);
					}
				});
			else if (npc.getId() == 5282)
				player.startConversation(new OsmanD(player, npc.getId()));
			else if (npc.getId() == 15099)
				player.startConversation(new FredaD(player, npc.getId()));
			else if (npc.getId() == 5532)
				player.getDialogueManager().execute(new SorceressGardenNPCs(), npc);
			else if (npc.getId() == 5141)
				player.getDialogueManager().execute(new UgiDialogue(), npc);
			else if (npc.getId() == 9712)
				player.getDialogueManager().execute(new DungeoneeringTutor());
			else if (npc.getId() == 5563)
				player.getDialogueManager().execute(new SorceressGardenNPCs(), npc);
			else if (npc.getId() == 3373 || npc.getId() == 3705)
				player.getDialogueManager().execute(new Max(), npc.getId());
			else if (npc.getId() == 15451 && npc instanceof FireSpirit spirit)
				spirit.giveReward(player);
			else if (npc.getId() == 9462 || npc.getId() == 9464 || npc.getId() == 9466)
				Strykewyrm.handleStomping(player, npc);
			else if (npc.getId() == 2825)
				player.sendOptionDialogue("Would you like to travel to Braindeath Island?", new String[] {"Yes", "No"}, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(new WorldTile(2163, 5112, 1));
					}
				});
			else if (npc.getId() == 2826)
				player.sendOptionDialogue("Would you like to travel back to Port Phasmatys?", new String[] {"Yes", "No"}, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(new WorldTile(3680, 3536, 0));
					}
				});
			else if (npc.getId() == 9707)
				player.getDialogueManager().execute(new FremennikShipmaster(), npc.getId(), true);
			else if (npc.getId() == 4288)
				player.startConversation(new AjjatD(player));
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
			//			else if (npc.getId() == 455)
			//				player.startConversation(new GenericSkillcapeOwnerD(player, 455, Skillcapes.Herblore));
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
			else if (npc.getId() == 8649)
				player.startConversation(new Conversation(new Dialogue()
						.addNPC(8649, HeadE.CHEERFUL, "Hello! What do you think of my apiary? Nice, isn't it?")
						.addPlayer(HeadE.SKEPTICAL, "You mean all these beehives?")
						.addNPC(8649, HeadE.CHEERFUL, "Yup! They're filled with bees. Also wax, and delicious honey too!")
						.addNPC(8649, HeadE.CHEERFUL, "You're welcome to help yourself to as much wax and honey as you like.")
						.addNPC(8649, HeadE.SKEPTICAL, "Oh, but you'll need some insect repellant - here.")
						.addItemToInv(player, new Item(28, 1), "The beekeeper hands you some insect repellant.")
						.addPlayer(HeadE.CHEERFUL, "Thank you!")
						.addNPC(8649, HeadE.ANGRY, "Leave the bees, though. The bees are mine!")
						.addNPC(8649, HeadE.CHEERFUL_EXPOSITION, "I love bees!")
						.finish()));
			else if (npc.getId() == 9708 || npc.getId() == 14847)
				player.getDialogueManager().execute(new FremennikShipmaster(), npc.getId(), false);
			else if (npc.getId() == 579)
				player.getDialogueManager().execute(new DrogoDwarf(), npc.getId());
			else if (npc.getId() == 528 || npc.getId() == 529)
				player.getDialogueManager().execute(new GeneralStore(), npc.getId(), "edgeville_general_store");
			else if (npc.getId() == 522 || npc.getId() == 523)
				player.getDialogueManager().execute(new GeneralStore(), npc.getId(), "varrock_general_store");
			else if (npc.getId() == 520 || npc.getId() == 521)
				player.getDialogueManager().execute(new GeneralStore(), npc.getId(), "lumbridge_general_store");
			else if (npc.getId() == 594)
				player.getDialogueManager().execute(new Nurmof(), npc);
			else if (npc.getId() == 3122)
				player.getDialogueManager().execute(new MamboJamboD(), npc.getId());
			else if (npc.getId() == 382 || npc.getId() == 3294 || npc.getId() == 4316)
				player.getDialogueManager().execute(new MiningGuildDwarf(), npc.getId(), false);
			else if (npc.getId() == 2617)
				player.getDialogueManager().execute(new TzHaarMejJal(), npc.getId());
			else if (npc.getId() == 2618)
				player.getDialogueManager().execute(new TzHaarMejKah(), npc.getId());
			else if (npc.getId() == 6715 || npc.getId() == 14862)
				player.getDialogueManager().execute(new EstateAgentDialogue(), npc.getId());
			else if (npc.getId() == 3344 || npc.getId() == 3345)
				MutatedZygomite.transform(player, npc);
			else if (npc.getId() == 4236 || npc.getId() == 4238 || npc.getId() == 4240 || npc.getId() == 4242 || npc.getId() == 4244)
				player.getDialogueManager().execute(new ServantDialogue(), npc.getId());
			else if (npc.getId() == 1334)
				player.getDialogueManager().execute(new Jossik(), npc.getId());
			else if (npc.getId() == 456)
				player.getDialogueManager().execute(new FatherAereck(), npc.getId());
			else if (npc.getId() == 13633)
				player.getDialogueManager().execute(new ClanCloak(), false);
			else if (npc.getId() == 5915)
				player.getDialogueManager().execute(new ClanVex(), false);
			else if (npc.getId() == 2824 || npc.getId() == 1041 || npc.getId() == 804)
				player.getDialogueManager().execute(new TanningD(), npc.getId());
			else if (npc.getName().toLowerCase().contains("impling"))
				FlyingEntityHunter.captureFlyingEntity(player, npc);
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
				if (Settings.getConfig().isDebug())
					System.out.println("cliked 1 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
			}
		}));
	}

	public static void handleOption2(Player player, NPC npc) {
		if (!npc.getDefinitions().hasAttackOption())
			return;
		if (npc.getId() == 7891) {
			player.getInteractionManager().setInteraction(new StandardEntityInteraction(npc, 0, () -> {
				if (!player.getControllerManager().canAttack(npc))
					return;
				npc.resetWalkSteps();
				player.faceEntity(npc);
				npc.faceEntity(player);
				if (player.getSkills().getLevelForXp(Constants.ATTACK) < 5) {
					if (player.getActionManager().getActionDelay() < 1) {
						player.getActionManager().setActionDelay(4);
						player.setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle())));
						player.getSkills().addXp(Constants.ATTACK, 15);
					}
				} else
					player.sendMessage("You have nothing more you can learn from this.");
			}));
			return;
		}
		if (npc instanceof Familiar familiar) {
			if (familiar == player.getFamiliar()) {
				player.sendMessage("You can't attack your own familiar.");
				return;
			}
			if (!familiar.canAttack(player)) {
				player.sendMessage("You can't attack this npc.");
				return;
			}
		}  else if (npc instanceof DoorSupport door) {
			if (!door.canDestroy(player)) {
				player.sendMessage("You cannot see a way to open this door...");
				return;
			}
		} else if (!npc.isForceMultiAttacked())
			if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
				if (player.getAttackedBy() != npc && player.inCombat()) {
					player.sendMessage("You are already in combat.");
					return;
				}
				if (npc.getAttackedBy() != player && npc.inCombat()) {
					player.sendMessage("This npc is already in combat.");
					return;
				}
			}
		player.setLastNpcInteractedName(npc.getDefinitions().getName());
		player.stopAll(true);
		player.getActionManager().setAction(new PlayerCombat(npc));
		PluginManager.handle(new NPCClickEvent(player, npc, 2, false));
	}

	public static void handleOption3(final Player player, final NPC npc) {
		if (player.isLocked() && player.getActionManager().getAction() != null && player.getActionManager().getAction() instanceof PickPocketAction)
			return;
		player.stopAll(true);

		PluginManager.handle(new NPCClickEvent(player, npc, 3, false));

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

			Object[] shipAttributes = BoatingDialogue.getBoatForShip(player, npc.getId());
			if (shipAttributes != null) {
				if(npc.getId() == 380 && player.getQuestManager().getStage(Quest.PIRATES_TREASURE) == PiratesTreasure.SMUGGLE_RUM) {
					player.startConversation(new CustomsOfficerPiratesTreasureD(player).getStart());
					return;
				}
				TravelMethods.sendCarrier(player, (Carrier) shipAttributes[0], (boolean) shipAttributes[1]);
				return;
			}
			if (npc instanceof Familiar) {
				npc.resetDirection();
				if (npc.getDefinitions().hasOption("store")) {
					if (player.getFamiliar() != npc) {
						player.sendMessage("That isn't your familiar.");
						return;
					}
					player.getFamiliar().store();
				} else if (npc.getDefinitions().hasOption("cure")) {
					if (player.getFamiliar() != npc) {
						player.sendMessage("That isn't your familiar.");
						return;
					}
					if (!player.getPoison().isPoisoned()) {
						player.sendMessage("Your arent poisoned or diseased.");
						return;
					}
					player.getFamiliar().drainSpecial(2);
					player.addEffect(Effect.ANTIPOISON, Ticks.fromMinutes(2));
				}
				return;
			}

			if (npc.getDefinitions().getName(player.getVars()).toLowerCase().equals("void knight")) {
				CommendationExchange.openExchangeShop(player);
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
				for (Item item : player.getInventory().getItems().getItems()) {
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
			else if (npc.getId() == 13633)
				player.getDialogueManager().execute(new ClanCloak(), true);
			else if (npc.getId() == 5915)
				player.getDialogueManager().execute(new ClanVex(), true);
			else if (npc.getId() == 2824 || npc.getId() == 1041)
				player.getDialogueManager().execute(new TanningD(), npc.getId());
			else if (npc.getId() == 1843)
				player.setNextWorldTile(new WorldTile(2836, 10142, 0));
			else if (npc.getId() == 1844)
				player.setNextWorldTile(new WorldTile(2839, 10131, 0));
			else if (npc.getId() == 1419)
				GE.open(player);
			else if (npc.getId() == 2676 || npc.getId() == 599)
				PlayerLook.openMageMakeOver(player);
			else if (npc.getId() == 598)
				PlayerLook.openHairdresserSalon(player);
			else if (npc instanceof Pet) {
				if (npc != player.getPet()) {
					player.sendMessage("This isn't your pet!");
					return;
				}
				Pet pet = player.getPet();
				player.getPackets().sendDevConsoleMessage("Pet [id=" + pet.getId() + ", hunger=" + pet.getDetails().getHunger() + ", growth=" + pet.getDetails().getGrowth() + ", stage=" + pet.getDetails().getStage() + "].");
			} else if (PluginManager.handle(new NPCClickEvent(player, npc, 3, true)))
				return;
			else {
				player.sendMessage("Nothing interesting happens." + npc.getId());
				if (Settings.getConfig().isDebug())
					System.out.println("cliked 2 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
			}
		}));
	}

	public static void handleOption4(final Player player, final NPC npc) {
		player.stopAll(true);

		PluginManager.handle(new NPCClickEvent(player, npc, 4, false));

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
			else if (npc.getId() == 1334)
				ShopsHandler.openShop(player, "book_shop");
			else if (PluginManager.handle(new NPCClickEvent(player, npc, 4, true)))
				return;
			else
				player.sendMessage("Nothing interesting happens." + npc.getId());
		}));

		if (Settings.getConfig().isDebug())
			System.out.println("cliked 3 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
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

			if (Settings.getConfig().isDebug())
				System.out.println("cliked 4 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
		}));
	}

	public static int getShopIdForNpc(int npcId) {
		switch (npcId) {
		case 1436: // Ape Toll General Store", Ifaba. (2752, 2774, 0)
			return -1; // TODO
		case 590: // East Ardougne General Store", Aemad. (2614, 3293, 0)
			return -1; // TODO
		case 2154: // Gunslik's General Store "Gunslik's Assorted Items",
			// Gunslik. (2868, 10190, 0)
			return -1; // TODO
		case 1254: // Razmire's General Store", Razmire Keelgan. (3488, 3296, 0)
			return -1; // TODO get burgh de rott transforming npc spawns
		case 1866: // Pollniveach General Store", Market Seller. (3359, 2983, 0)
			return -1; // TODO
		case 1699: // Port Phasmatys General Store", Ghostly Shopkeeper. (3659,
			// 3473, 0) (Ghostly Amulet Needed to talk to him, run by
			// one of the ghostly denizens).
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
