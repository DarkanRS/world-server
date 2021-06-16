package com.rs.game.player.content.world.regions;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.player.quests.Quest;
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
			if (!Agility.hasLevel(e.getPlayer(), 45))
				return;
			AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() > e.getObject().getX() ? -6 : 6, 0, 0), 4);
		}
	};
	
	public static ObjectClickHandler handleArandarLogBalance = new ObjectClickHandler(new Object[] { 3933 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 45))
				return;
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
			if (e.getObject().getRotation() == 3 || e.getObject().getRotation() == 1)
				Agility.handleObstacle(e.getPlayer(), 3303, 1, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 3 : -3, 0, 0), 0);
			else
				Agility.handleObstacle(e.getPlayer(), 3303, 1, e.getPlayer().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 3 : -3, 0), 0);
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
										} else {
											player.sendMessage("You don't have enough money.");
										}
									}));
							option("Could I buy a crystal shield?", new Dialogue()
									.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Yes, but it will cost you 750,000 coins.")
									.addOption("Buy a crystal shield for 750,000 coins?", "Yes, I'd like to buy one.", "No thanks, that's way too much for me.")
									.addNext(() -> {
										if (player.getInventory().containsItem(995, 750000)) {
											player.getInventory().deleteItem(995, 750000);
											player.getInventory().addItem(4224, 1);
										} else {
											player.sendMessage("You don't have enough money.");
										}
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
			    	if (repairs <= 5) {
			    		cost -= repairs * 200000;
			    	} else {
			    		cost = 200000;
			    	}
			    }  

				e.getPlayer().sendOptionDialogue("Would you like to attune your crystal seed for " + cost + " gold?", new String[] { "Yes, I'll pay " + cost + " gold for a bow.", "Yes, I'll pay " + cost + " gold for a shield.", "No, that's too much." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
					    int cost = 1000000;
					    if (e.getPlayer().getCrystalSeedRepairs() >= 0) {
					    	int repairs = e.getPlayer().getCrystalSeedRepairs();
					    	if (repairs < 5) {
					    		cost -= repairs * 200000;
					    	} else {
					    		cost = 200000;
					    	}
					    }
						if (option == 1) {
							if (player.getInventory().containsItem(995, cost) && player.getInventory().containsItem(4207, 1)) {
								player.getInventory().deleteItem(4207, 1);
								player.getInventory().deleteItem(995, cost);
								player.getInventory().addItem(4212, 1);
								player.incrementCrystalSeedRepair();
							} else {
								player.sendMessage("You don't have enough money.");
							}
						}
						
						if (option == 2) {
							if (player.getInventory().containsItem(995, cost) && player.getInventory().containsItem(4207, 1)) {
								player.getInventory().deleteItem(4207, 1);
								player.getInventory().deleteItem(995, cost);
								player.getInventory().addItem(4224, 1);
								player.incrementCrystalSeedRepair();
							} else {
								player.sendMessage("You don't have enough money.");
							}
						}
					}
				});
			} else if (e.getItem().getId() == 6103) {
				int numSeeds = e.getPlayer().getInventory().getNumberOf(6103);
				int cost = 750;
			    if (e.getPlayer().getTinyCrystalSeedRepairs() >= 0) {
			    	int repairs = e.getPlayer().getTinyCrystalSeedRepairs();
			    	if (repairs < 5) {
			    		cost -= repairs * 120;
			    	} else {
			    		cost = 150;
			    	}
			    }
			    int totalCost = cost * numSeeds;
			    e.getPlayer().sendOptionDialogue("Repair all your teleport seeds for " + totalCost + "?", new String[] { "Yes please.", "No, thanks." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1) {
							if (player.getInventory().containsItem(995, totalCost) && player.getInventory().containsItem(6103, numSeeds)) {
								player.getInventory().deleteItem(6103, numSeeds);
								player.getInventory().addItem(6099, numSeeds);
								for (int i = 0;i < numSeeds;i++)
									player.incrementTinyCrystalSeedRepair();
							} else
								player.sendMessage("You don't have enough money.");
						}
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
