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
package com.rs.game.player.content.world.regions;

import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Tirannwn {

	//	public static void unlockPrisonerOfGlouphrieEntrance(LoginEvent e) {
	//		e.getPlayer().getVars().setVarBit(5332, 1); //remove boulder
	//		e.getPlayer().getVars().setVarBit(8749, 2); //expose vent and tie rope
	//		return false;
	//	}

	public static ObjectClickHandler handleStickTraps = new ObjectClickHandler(new Object[] { 3922 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			/*
            3 = S -> N
            2 = W -> E
            1 = N- > S
            0 = E -> W
			 */
			Player p = e.getPlayer();
			WorldTile objTile = new WorldTile(e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane());
			if(p.withinDistance(objTile, 3) && (e.getObject().getRotation() == 3 || e.getObject().getRotation()== 1))
				if(p.getY() > objTile.getY())
					p.addWalkSteps(new WorldTile(objTile.getX(), objTile.getY()-1, objTile.getPlane()), 5, false);
				else if(p.getY() <= objTile.getY())
					p.addWalkSteps(new WorldTile(objTile.getX(), objTile.getY()+2, objTile.getPlane()), 5, false);
			if(p.withinDistance(objTile, 3) && (e.getObject().getRotation() == 0 || e.getObject().getRotation()== 2))
				if(p.getX() > objTile.getX())
					p.addWalkSteps(new WorldTile(objTile.getX()-1, objTile.getY(), objTile.getPlane()), 5, false);
				else if(p.getX() <= objTile.getX())
					p.addWalkSteps(new WorldTile(objTile.getX()+2, objTile.getY(), objTile.getPlane()), 5, false);
		}
	};

	public static ObjectClickHandler handleTripWire = new ObjectClickHandler(new Object[] { 3921 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			/*
            2 = S -> N
            3 = W -> E
            0 = N- > S
            1 = E -> W
			 */
			Player p = e.getPlayer();
			WorldTile objTile = new WorldTile(e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane());
			if(p.withinDistance(objTile, 3) && (e.getObject().getRotation() == 0 || e.getObject().getRotation()== 2))
				if(p.getY() > objTile.getY())
					p.addWalkSteps(new WorldTile(objTile.getX(), objTile.getY()-1, objTile.getPlane()), 5, false);
				else if(p.getY() <= objTile.getY())
					p.addWalkSteps(new WorldTile(objTile.getX(), objTile.getY()+2, objTile.getPlane()), 5, false);
			if(p.withinDistance(objTile, 3) && (e.getObject().getRotation() == 1 || e.getObject().getRotation()== 3))
				if(p.getX() > objTile.getX())
					p.addWalkSteps(new WorldTile(objTile.getX()-1, objTile.getY(), objTile.getPlane()), 5, false);
				else if(p.getX() <= objTile.getX())
					p.addWalkSteps(new WorldTile(objTile.getX()+2, objTile.getY(), objTile.getPlane()), 5, false);
		}
	};

	public static ObjectClickHandler handleLeafTrap = new ObjectClickHandler(new Object[] { 3923, 3925 }) {
		@Override
		public void handle(ObjectClickEvent e) { //Hard coded
			Player p = e.getPlayer();
			WorldTile objTile = new WorldTile(e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane());
			if(p.withinDistance(new WorldTile(2208, 3204, 0), 4))
				if(p.getY() > objTile.getY())
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2209, 3201, 0), 10963, 1, 0, Direction.SOUTH);
				else if(p.getY() < objTile.getY())
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2209, 3205, 0), 10963, 1, 0, Direction.NORTH);
			if(p.withinDistance(new WorldTile(2267, 3202, 0), 4))
				if(p.getY() > objTile.getY())
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2267, 3201, 0), 10963, 1, 0, Direction.SOUTH);
				else if(p.getY() < objTile.getY())
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2267, 3205, 0), 10963, 1, 0, Direction.NORTH);
			if(p.withinDistance(new WorldTile(2274, 3174, 0), 4))
				if(p.getY() > objTile.getY())
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2274, 3172, 0), 10963, 1, 0, Direction.SOUTH);
				else if(p.getY() < objTile.getY())
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2274, 3176, 0), 10963, 1, 0, Direction.NORTH);
			if(p.withinDistance(new WorldTile(2278, 3262, 0), 4))
				if(p.getX() > objTile.getX())
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2275, 3262, 0), 10963, 1, 0, Direction.WEST);
				else if(p.getX() < objTile.getX())
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2279, 3262, 0), 10963, 1, 0, Direction.EAST);
		}
	};


	public static ObjectClickHandler handleGlouphrieCave = new ObjectClickHandler(new Object[] { 20750, 20753 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 20753)
				e.getPlayer().useStairs(new WorldTile(2389, 3193, 0));
			else
				e.getPlayer().useStairs(new WorldTile(3577, 4400, 0));
		}
	};

	public static ObjectClickHandler handleGlouphrieCaveStairs1 = new ObjectClickHandler(new Object[] { 20652, 20653 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 20652)
				e.getPlayer().useStairs(new WorldTile(3546, 4581, 0));
			else
				e.getPlayer().useStairs(new WorldTile(3619, 4582, 0));
		}
	};

	public static ObjectClickHandler handleGlouphrieCaveStairs2 = new ObjectClickHandler(new Object[] { 20655, 20631 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 20655)
				e.getPlayer().useStairs(new WorldTile(3545, 4577, 0));
			else
				e.getPlayer().useStairs(new WorldTile(3540, 4512, 0));
		}
	};

	public static ObjectClickHandler handleGlouphrieVent = new ObjectClickHandler(new Object[] { 20719, 20659 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 20719 && e.getOption().equals("Climb-down"))
				e.getPlayer().useStairs(new WorldTile(3541, 4571, 0));
			else
				e.getPlayer().useStairs(new WorldTile(2375, 3181, 0));
		}
	};

	public static ObjectClickHandler handleGrenwallLogBalance = new ObjectClickHandler(new Object[] { 3931, 3932 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 45)) {
				e.getPlayer().sendMessage("You need 45 agility");
				return;
			}
			AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() > e.getObject().getX() ? -6 : 6, 0, 0), 4);
		}
	};

	public static ObjectClickHandler handleArandarLogBalance = new ObjectClickHandler(new Object[] { 3933 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 45)) {
				e.getPlayer().sendMessage("You need 45 agility");
				return;
			}
			AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() > e.getObject().getY() ? -7 : 7, 0), 6);
		}
	};

	public static ObjectClickHandler handleEnterUndergroundPass = new ObjectClickHandler(new Object[] { 4006 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2438, 3315, 0));
		}
	};

	public static ObjectClickHandler handleLletyaTreePass = new ObjectClickHandler(new Object[] { 8742 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Agility.handleObstacle(e.getPlayer(), 3303, 1, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 2 : -2, 0, 0), 0);
		}
	};

	public static ObjectClickHandler handleDenseForest = new ObjectClickHandler(new Object[] { "Dense forest" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 56)) {
				e.getPlayer().sendMessage("You need 56 agility");
				return;
			}
			if (e.getObject().getRotation() == 3 || e.getObject().getRotation() == 1)
				Agility.handleObstacle(e.getPlayer(), 3303, 1, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 3 : -3, 0, 0), 0);
			else
				Agility.handleObstacle(e.getPlayer(), 3303, 1, e.getPlayer().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 3 : -3, 0), 0);
		}
	};

	public static ObjectClickHandler handleHeavyGateToArandar = new ObjectClickHandler(new Object[] { 3945, 3944 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			WorldObject obj = e.getObject();

			if(p.getY() > obj.getY())
				p.setNextWorldTile(new WorldTile(obj.getX(), obj.getY()-1, obj.getPlane()));
			if(p.getY() < obj.getY())
				p.setNextWorldTile(new WorldTile(obj.getX(), obj.getY()+1, obj.getPlane()));
		}
	};

	public static ObjectClickHandler handleAdvancedElvenCliffside = new ObjectClickHandler(new Object[] { 9297, 9296 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			WorldObject obj = e.getObject();

			if(obj.getId() == 9296) {//above
				if(obj.matches(new WorldTile(2333,3252, 0))) {
					if (!Agility.hasLevel(p, 85)) {
						p.getPackets().sendGameMessage("You need level 85 agility to use this shortcut.");
						return;
					}
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2338, 3253, 0), 2050, 1, 1, Direction.WEST);
				}
				if(obj.matches(new WorldTile(2338,3282, 0))) {
					if (!Agility.hasLevel(p, 68)) {
						p.getPackets().sendGameMessage("You need level 68 agility to use this shortcut.");
						return;
					}
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2338, 3286, 0), 2050, 1, 1, Direction.SOUTH);
				}
				if(obj.matches(new WorldTile(2346,3299, 0))) {
					if (!Agility.hasLevel(p, 59)) {
						p.getPackets().sendGameMessage("You need level 59 agility to use this shortcut.");
						return;
					}
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2344, 3294, 0), 2050, 1, 1, Direction.NORTH);
				}
			}

			if(obj.getId() == 9297) {//below
				if(obj.matches(new WorldTile(2337,3253, 0))) {
					if (!Agility.hasLevel(p, 85)) {
						p.getPackets().sendGameMessage("You need level 85 agility to use this shortcut.");
						return;
					}
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2332, 3252, 0), 2049, 1, 1, Direction.WEST);
				}
				if(obj.matches(new WorldTile(2338,3285, 0))) {
					if (!Agility.hasLevel(p, 68)) {
						p.getPackets().sendGameMessage("You need level 68 agility to use this shortcut.");
						return;
					}
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2338, 3281, 0), 2049, 1, 1, Direction.SOUTH);
				}
				if(obj.matches(new WorldTile(2344,3295, 0))) {
					if (!Agility.hasLevel(p, 59)) {
						p.getPackets().sendGameMessage("You need level 59 agility to use this shortcut.");
						return;
					}
					AgilityShortcuts.forceMovementInstant(p, new WorldTile(2346, 3300, 0), 2049, 1, 1, Direction.NORTH);
				}
			}


		}
	};

	public static NPCClickHandler handleArianwynCampTalk = new NPCClickHandler("Arianwyn") {
		@Override
		public void handle(NPCClickEvent e) {
			if (!Quest.REGICIDE.meetsRequirements(e.getPlayer(), "to talk to Arianwyn about teleport seeds and crystal weaponry."))
				return;
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello " + e.getPlayer().getDisplayName() + ". What is it you need?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("Could you repair some seeds for me?", new Dialogue().addNPC(e.getNPCId(), HeadE.CHEERFUL, "Of course! If you have a seed you would like me to repair, use it on me."));
							option("Could I buy a crystal bow?", new Dialogue()
									.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Yes, but it will cost you 1,000,000 coins.")
									.addOption("Buy a crystal bow for 1,000,000 coins?", "Yes, I'd like to buy one.", "No thanks, that's way too much for me.")
									.addNext(() -> {
										if (player.getInventory().containsItem(995, 1000000)) {
											player.getInventory().deleteItem(995, 1000000);
											player.getInventory().addItem(4212, 1);
										} else
											player.sendMessage("You don't have enough money.");
									}));
							option("Could I buy a crystal shield?", new Dialogue()
									.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Yes, but it will cost you 750,000 coins.")
									.addOption("Buy a crystal shield for 750,000 coins?", "Yes, I'd like to buy one.", "No thanks, that's way too much for me.")
									.addNext(() -> {
										if (player.getInventory().containsItem(995, 750000)) {
											player.getInventory().deleteItem(995, 750000);
											player.getInventory().addItem(4224, 1);
										} else
											player.sendMessage("You don't have enough money.");
									}));
						}
					});
				}
			});
		}
	};

	public static ItemOnNPCHandler handleArianwynCamp = new ItemOnNPCHandler("Arianwyn") {
		@Override
		public void handle(ItemOnNPCEvent e) {
			if (e.getItem().getId() == 4207) {
				int cost = 1000000;
				if (e.getPlayer().getCrystalSeedRepairs() >= 0) {
					int repairs = e.getPlayer().getCrystalSeedRepairs();
					if (repairs <= 5)
						cost -= repairs * 200000;
					else
						cost = 200000;
				}

				e.getPlayer().sendOptionDialogue("Would you like to attune your crystal seed for " + cost + " gold?", new String[] { "Yes, I'll pay " + cost + " gold for a bow.", "Yes, I'll pay " + cost + " gold for a shield.", "No, that's too much." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						int cost = 1000000;
						if (e.getPlayer().getCrystalSeedRepairs() >= 0) {
							int repairs = e.getPlayer().getCrystalSeedRepairs();
							if (repairs < 5)
								cost -= repairs * 200000;
							else
								cost = 200000;
						}
						if (option == 1)
							if (player.getInventory().containsItem(995, cost) && player.getInventory().containsItem(4207, 1)) {
								player.getInventory().deleteItem(4207, 1);
								player.getInventory().deleteItem(995, cost);
								player.getInventory().addItem(4212, 1);
								player.incrementCrystalSeedRepair();
							} else
								player.sendMessage("You don't have enough money.");

						if (option == 2)
							if (player.getInventory().containsItem(995, cost) && player.getInventory().containsItem(4207, 1)) {
								player.getInventory().deleteItem(4207, 1);
								player.getInventory().deleteItem(995, cost);
								player.getInventory().addItem(4224, 1);
								player.incrementCrystalSeedRepair();
							} else
								player.sendMessage("You don't have enough money.");
					}
				});
			} else if (e.getItem().getId() == 6103) {
				int numSeeds = e.getPlayer().getInventory().getNumberOf(6103);
				int cost = 750;
				if (e.getPlayer().getTinyCrystalSeedRepairs() >= 0) {
					int repairs = e.getPlayer().getTinyCrystalSeedRepairs();
					if (repairs < 5)
						cost -= repairs * 120;
					else
						cost = 150;
				}
				int totalCost = cost * numSeeds;
				e.getPlayer().sendOptionDialogue("Repair all your teleport seeds for " + totalCost + "?", new String[] { "Yes please.", "No, thanks." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							if (player.getInventory().containsItem(995, totalCost) && player.getInventory().containsItem(6103, numSeeds)) {
								player.getInventory().deleteItem(6103, numSeeds);
								player.getInventory().addItem(6099, numSeeds);
								for (int i = 0;i < numSeeds;i++)
									player.incrementTinyCrystalSeedRepair();
							} else
								player.sendMessage("You don't have enough money.");
					}
				});
			}
		}
	};

	public static ItemClickHandler handleTeleportCrystal = new ItemClickHandler(new Object[] { 6099, 6100, 6101, 6102 }, new String[] { "Activate" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (Magic.sendNormalTeleportSpell(e.getPlayer(), new WorldTile(2340, 3172, 0))) {
				e.getItem().setId(e.getItem().getId()+1);
				e.getPlayer().getInventory().refresh(e.getItem().getSlot());
			}
		}
	};


}
