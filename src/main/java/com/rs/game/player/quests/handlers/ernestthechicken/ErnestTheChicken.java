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
package com.rs.game.player.quests.handlers.ernestthechicken;

import static com.rs.game.player.content.world.doors.Doors.handleDoor;
import static com.rs.game.player.content.world.doors.Doors.handleDoubleDoor;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestOutline;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemAddedToInventoryEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemAddedToInventoryHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@QuestHandler(Quest.ERNEST_CHICKEN)
@PluginEventHandler
public class ErnestTheChicken extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int STARTED = 1;
	public final static int KNOWS_ABOUT_CHICKEN = 2;
	public final static int NEEDS_PARTS = 3;
	public final static int ERNEST_NOT_CHICKEN=4;
	public final static int QUEST_COMPLETE = 5;

	//items
	private final static int PRESSURE_GAUGE = 271;
	private final static int FISH_FOOD = 272;
	private final static int POISON = 273;
	private final static int POISONED_FISH_FOOD = 274;
	private final static int GRIMY_KEY = 275;
	private final static int RUBBER_TUBE = 276;
	private final static int SPADE = 952;

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("Veronica is very worried. Her fiancé went into ");
			lines.add("the big spooky manor house to ask for directions.");
			lines.add("An hour later and he's still not out yet.");
			lines.add("");
			lines.add("I can start this quest by speaking to Veronica, south");
			lines.add("of the Draynor mansion's gates.");
			lines.add("");
			break;
		case STARTED:
			lines.add("I must investigate the mansion and find Ernest");
			lines.add("Veronica hinted she saw lights on the top floor.");
			lines.add("");
			break;
		case KNOWS_ABOUT_CHICKEN:
			lines.add("Ernest has been turned into a chicken!");
			lines.add("");
			break;
		case NEEDS_PARTS:
			lines.add("Professor Oddenstein needs machine parts to turn");
			lines.add("Ernest back into a human. I can find all parts inside");
			lines.add("the Draynor estate");
			lines.add("");
			break;
		case ERNEST_NOT_CHICKEN:
			lines.add("Ernest is not a chicken anymore. I ought to speak to");
			lines.add("professor Oddenstein to claim my reward");
			lines.add("");
			break;
		case QUEST_COMPLETE:
			lines.add("");
			lines.add("");
			lines.add("QUEST COMPLETE!");
			break;
		default:
			lines.add("Invalid quest stage. Report this to an administrator.");
			break;
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getInventory().addItem(new Item(995, 3000), true);
		player.getInventory().addItem(new Item(1945, 10), true);
		player.getInventory().addItem(new Item(314, 300), true);
		getQuest().sendQuestCompleteInterface(player, 314, "3,000 coins", "10 eggs", "300 feathers");
	}

	public static ObjectClickHandler handleManorFrontDoor = new ObjectClickHandler(new Object[] { 47424, 47421 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(p.getY() <= e.getObject().getY())
				if(p.getBool("EverEnteredDraynorManor")) {
					handleDoubleDoor(p, e.getObject());
					p.sendMessage("The doors slam shut behind you.");
				} else
					p.startConversation(new Conversation(p) {
						{
							addPlayer(HeadE.NERVOUS, "There's a sign on the door that says...");
							addSimple("Adventurers beware: Going in doesn't mean you'll come out again.");
							addOptions("Enter Draynor Manor?", new Options() {
								@Override
								public void create() {
									option("Yes.", new Dialogue()
											.addNext(()->{
												p.save("EverEnteredDraynorManor", true);
												handleDoubleDoor(p, e.getObject());
												p.sendMessage("The doors slam shut behind you.");
											}));
									option("No.", new Dialogue());
								}
							});
							create();
						}
					});
			else
				p.sendMessage("The doors are firmly closed.");
		}
	};

	public static ItemOnItemHandler handlePoisonOnFishFood = new ItemOnItemHandler(POISON, new int[] { FISH_FOOD }) {
		@Override
		public void handle(ItemOnItemEvent e) {
			Inventory inv = e.getPlayer().getInventory();
			inv.deleteItem(e.getItem1());
			inv.deleteItem(e.getItem2());
			inv.addItem(POISONED_FISH_FOOD, 1);
		}
	};

	public static ItemOnObjectHandler handleCompostHeapItem = new ItemOnObjectHandler(new Object[] { 152 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if(e.getPlayer().getInventory().containsItem(GRIMY_KEY, 1)) {
				e.getPlayer().sendMessage("I already have the key");
				return;
			}
			if(e.getItem().getId() == SPADE) {
				e.getPlayer().setNextAnimation(new Animation(830));
				e.getPlayer().getInventory().addItem(GRIMY_KEY, 1);
			} else
				e.getPlayer().sendMessage("I appear to need a spade.");
		}
	};

	public static ObjectClickHandler handleCompostHeapSearch = new ObjectClickHandler(new Object[] { 152 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getPlayer().getInventory().containsItem(GRIMY_KEY, 1)) {
				e.getPlayer().sendMessage("I already have the key");
				return;
			}
			if(e.getPlayer().getInventory().containsItem(SPADE, 1)) {
				e.getPlayer().setNextAnimation(new Animation(830));
				e.getPlayer().getInventory().addItem(GRIMY_KEY, 1);
			}
			else
				e.getPlayer().sendMessage("I appear to need a spade.");
		}
	};

	public static ObjectClickHandler handleFountainSearch = new ObjectClickHandler(new Object[] { 153 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(p.getInventory().containsItem(PRESSURE_GAUGE, 1)) {
                p.startConversation(new Conversation(p) {{
                    addPlayer(HeadE.CALM_TALK, "I already have the pressure gauge...");
                    create();
                }});
                return;
            }
			if(p.getTempAttribs().getB("FountainFishDead"))
				p.startConversation(new Conversation(p) {
					{
						addPlayer(HeadE.CALM_TALK, "There seems to be a preassure gauge in here...", () -> {
							p.getInventory().addItem(PRESSURE_GAUGE, 1);
						});
						addPlayer(HeadE.CALM_TALK, "... and a lot of dead fish.");
						create();
					}
				});
			else
				p.startConversation(new Conversation(p) {
					{
						addPlayer(HeadE.NERVOUS, "There appear to be deadly fish in the water...");
						create();
					}
				});
		}
	};

	public static ItemOnObjectHandler handleFountainItem = new ItemOnObjectHandler(new Object[] { 153 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			Player p = e.getPlayer();
			if(e.getItem().getId() != POISONED_FISH_FOOD) {
				p.startConversation(new Conversation(p) {{
					addPlayer(HeadE.SKEPTICAL_THINKING, "What good would that do?");
					create();
				}});
				return;
			}
			p.getInventory().deleteItem(POISONED_FISH_FOOD, 1);
			WorldTasksManager.schedule(new WorldTask() {
				int tick = 0;
				@Override
				public void run() {
					if(tick == 0)
						p.sendMessage("You pour the poisoned fish food into the fountain.");
					if(tick == 1)
						p.sendMessage("The piranhas start eating the food...");
					if(tick == 2) {
						p.sendMessage("...Then die and float to the surface.");
						p.getTempAttribs().setB("FountainFishDead", true);
						stop();
					}

					tick++;
				}
			}, 0, 1);

		}
	};

	public static ObjectClickHandler handleRubberTubeDoor = new ObjectClickHandler(new Object[] { 131 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getPlayer().getInventory().containsItem(GRIMY_KEY) || e.getPlayer().getY() <= e.getObject().getY())
				handleDoor(e.getPlayer(), e.getObject());
			else
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {{
					addPlayer(HeadE.SKEPTICAL_THINKING, "It appears to need a key");
					create();
				}});
		}
	};

	public static ItemAddedToInventoryHandler handleRubberTubePickup = new ItemAddedToInventoryHandler(RUBBER_TUBE) {
		@Override
		public void handle(ItemAddedToInventoryEvent e) {
			Player p = e.getPlayer();
			List<NPC> npcs = World.getNPCsInRegion(p.getRegionId());
			for(NPC npc : npcs)
				if(npc.getId() == 3291)
					npc.setTarget(p);
		}
	};

	public static ObjectClickHandler handleDraynorManorLadders = new ObjectClickHandler(new Object[] { 47574, 47575, 133, 32015 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			GameObject obj = e.getObject();
			if(obj.matches(new WorldTile(3105, 3363, 2)))//To 2nd floor from professor
				e.getPlayer().useLadder(new WorldTile(3105, 3364, 1));
			if(obj.matches(new WorldTile(3105, 3363, 1)))//to 3rd floor to professor
				e.getPlayer().useLadder(new WorldTile(3105, 3362, 2));
			if(obj.matches(new WorldTile(3092, 3362, 0)))//To basement from 1st floor
				e.getPlayer().useLadder(new WorldTile(3117, 9753, 0));
			if(obj.matches(new WorldTile(3117, 9754, 0)))//To 1st floor from basement
				e.getPlayer().useLadder(new WorldTile(3092, 3361, 0));
		}
	};

	public static LoginHandler onLogin = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if(e.getPlayer().getQuestManager().getStage(Quest.ERNEST_CHICKEN) >= ERNEST_NOT_CHICKEN)
				e.getPlayer().getVars().setVar(32, 3);
			else
				e.getPlayer().getVars().setVar(32, 0);
		}
	};

}
