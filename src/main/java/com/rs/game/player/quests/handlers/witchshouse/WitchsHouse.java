package com.rs.game.player.quests.handlers.witchshouse;

import static com.rs.game.player.content.world.doors.Doors.handleDoor;
import static com.rs.game.player.content.world.doors.Doors.handleDoubleDoor;

import java.util.ArrayList;

import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestOutline;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemAddedToInventoryEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.NPCDeathEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemAddedToInventoryHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@QuestHandler(Quest.WITCHS_HOUSE)
@PluginEventHandler
public class WitchsHouse extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int FIND_BALL = 1;
	public final static int QUEST_COMPLETE = 2;

	//Attributes
	protected final static String MOUSE_SOLVED_ATTR = "MOUSE_SOLVED";
	protected final static String KILLED_EXPERIMENT_ATTR = "KILLED_EXPERIMENT";

	//items
	protected final static int DOOR_KEY = 2409;
	protected final static int BACKROOM_KEY = 2411;
	protected final static int MAGNET = 2410;
	protected final static int WITCH_DIARY = 2408;
	protected final static int BALL = 2407;

	//NPCs
	protected final static int BOY = 895;
	protected final static int WITCH = 896;
	protected final static int MOUSE = 901;
	protected final static int EXPERIMENT1 = 897;
	protected final static int EXPERIMENT2 = 898;
	protected final static int EXPERIMENT3 = 899;
	protected final static int EXPERIMENT4 = 900;

	//Objects


	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("A young boy who lives in Taverley has kicked his ball into");
			lines.add("the garden of a scary old lady. He asks you to get it back");
			lines.add("for him. This proves more difficult than it first sounds.");
			lines.add("");
			break;
		case FIND_BALL:
			lines.add("I need to find this boy's ball. Perhaps the witch left");
			lines.add("clues lying around her house?");
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

	public static ItemClickHandler handleClickOnWitchDiary = new ItemClickHandler(WITCH_DIARY) {
		@Override
		public void handle(ItemClickEvent e) {
			if(e.getOption().equalsIgnoreCase("read"))
				e.getPlayer().openBook(new WitchsDiary());;
				if(e.getOption().equalsIgnoreCase("drop")) {
					e.getPlayer().getInventory().deleteItem(e.getSlotId(), e.getItem());
					World.addGroundItem(e.getItem(), new WorldTile(e.getPlayer()), e.getPlayer());
					e.getPlayer().getPackets().sendSound(2739, 0, 1);
				}
		}
	};

	public static ObjectClickHandler handlePottedPlant = new ObjectClickHandler(new Object[] { 2867 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(p.getQuestManager().getStage(Quest.WITCHS_HOUSE) != FIND_BALL) {
				p.startConversation(new Conversation(e.getPlayer()) {
					{
						addSimple("I have no reason to look under there.");
						create();
					}
				});
				return;
			}
			if(!p.getInventory().containsItem(DOOR_KEY, 1)) {
				if(!p.getInventory().hasFreeSlots()) {
					p.sendMessage("You don't have room...");
					return;
				}
				p.getInventory().addItem(new Item(DOOR_KEY, 1));
				p.sendMessage("You found a key!");
			}

		}
	};
	public static ObjectClickHandler handleFountain = new ObjectClickHandler(new Object[] { 2864 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(!p.getInventory().containsItem(BACKROOM_KEY, 1)) {
				if(!p.getInventory().hasFreeSlots()) {
					p.sendMessage("You don't have room...");
					return;
				}
				p.getInventory().addItem(new Item(BACKROOM_KEY, 1));
				p.sendMessage("You found a key!");
			}

		}
	};

	public static ObjectClickHandler handleWitchHouseFrontDoor = new ObjectClickHandler(new Object[] { 2861 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(p.getInventory().containsItem(new Item(DOOR_KEY, 1)))
				handleDoor(p, obj);
			else
				p.startConversation(new Conversation(e.getPlayer()) {
					{
						addSimple("It appears to need a key...");
						create();
					}
				});
		}
	};

	public static ObjectClickHandler handleWitchHouseBackRoomDoor = new ObjectClickHandler(new Object[] { 2863 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(p.getInventory().containsItem(new Item(BACKROOM_KEY, 1))) {
				handleDoor(p, obj);
				if(!p.getQuestManager().getAttribs(Quest.WITCHS_HOUSE).getB(KILLED_EXPERIMENT_ATTR)) {
					for (NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId()))
						if (npc.getId() == EXPERIMENT1 || npc.getId() == EXPERIMENT2 || npc.getId() == EXPERIMENT3 || npc.getId() == EXPERIMENT4)
							return;
					World.spawnNPC(EXPERIMENT1, new WorldTile(2927, 3359, 0), -1, false, true);
				}
			}
			else
				p.startConversation(new Conversation(e.getPlayer()) {
					{
						addSimple("It appears to need a key...");
						create();
					}
				});
		}
	};

	public static NPCDeathHandler handleExperiment1 = new NPCDeathHandler(EXPERIMENT1) {
		@Override
		public void handle(NPCDeathEvent e) {
			NPC n = World.spawnNPC(EXPERIMENT2, new WorldTile(2927, 3363, 0), -1, false, true);
			n.setTarget(e.getKiller());
		}
	};

	public static NPCDeathHandler handleExperiment2 = new NPCDeathHandler(EXPERIMENT2) {
		@Override
		public void handle(NPCDeathEvent e) {
			NPC n = World.spawnNPC(EXPERIMENT3, new WorldTile(2927, 3363, 0), -1, false, true);
			n.setTarget(e.getKiller());
		}
	};

	public static NPCDeathHandler handleExperiment3 = new NPCDeathHandler(EXPERIMENT3) {
		@Override
		public void handle(NPCDeathEvent e) {
			NPC n = World.spawnNPC(EXPERIMENT4, new WorldTile(2927, 3363, 0), -1, false, true);
			n.setTarget(e.getKiller());
		}
	};

	public static NPCDeathHandler handleExperiment4 = new NPCDeathHandler(EXPERIMENT4) {
		@Override
		public void handle(NPCDeathEvent e) {
			if(e.killedByPlayer()) {
				Player p = (Player)e.getKiller();
				if(p.getQuestManager().getStage(Quest.WITCHS_HOUSE) == FIND_BALL)
					p.getQuestManager().getAttribs(Quest.WITCHS_HOUSE).setB(KILLED_EXPERIMENT_ATTR, true);
			}
		}
	};

	public static ItemAddedToInventoryHandler handleBallPickup = new ItemAddedToInventoryHandler(BALL) {
		@Override
		public void handle(ItemAddedToInventoryEvent e) {
			Player p = e.getPlayer();
			if(!p.withinDistance(new WorldTile(2927, 3361, 0), 2))
				return;
			if(p.getQuestManager().getStage(Quest.WITCHS_HOUSE) != FIND_BALL) {
				p.getInventory().removeItems(new Item(BALL, 1));
				World.addGroundItem(e.getItem(), new WorldTile(2927, 3360, 0));
				p.startConversation(new Conversation(e.getPlayer()) {
					{
						addSimple("I better not touch it...");
						create();
					}
				});
			}
			if(p.getQuestManager().getStage(Quest.WITCHS_HOUSE) == FIND_BALL)
				if(!p.getQuestManager().getAttribs(Quest.WITCHS_HOUSE).getB(KILLED_EXPERIMENT_ATTR)) {
					for(NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId()))
						if(npc.getId() == EXPERIMENT1 || npc.getId() == EXPERIMENT2 || npc.getId() == EXPERIMENT3 || npc.getId() == EXPERIMENT4)
							npc.setTarget(p);
					p.getInventory().removeItems(new Item(BALL, 1));
					World.addGroundItem(e.getItem(), new WorldTile(2927, 3360, 0));
				}
		}
	};

	public static ObjectClickHandler handleWitchHouseMouseDoor = new ObjectClickHandler(new Object[] { 2862 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(p.getQuestManager().getAttribs(Quest.WITCHS_HOUSE).getB(MOUSE_SOLVED_ATTR))
				handleDoor(p, obj);
			else
				p.startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.CALM_TALK, "It appears to be locked by some mechanism, perhaps by the mouse hole?");
						create();
					}
				});
		}
	};

	public static ItemOnObjectHandler handleMouseHole = new ItemOnObjectHandler(new Object[] { 2870 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			GameObject obj = e.getObject();
			if(e.getPlayer().getQuestManager().getAttribs(Quest.WITCHS_HOUSE).getB(MOUSE_SOLVED_ATTR)) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.CALM_TALK, "The door is already unlocked...");
						create();
					}
				});
				return;
			}
			if(e.getItem().getName().equalsIgnoreCase("Cheese")) {
				for(NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId()))
					if(npc.getId() == MOUSE) {
						e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
							{
								addPlayer(HeadE.CALM_TALK, "The mouse is right there...");
								create();
							}
						});
						return;
					}
				e.getPlayer().getInventory().removeItems(new Item(1985, 1));
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addSimple("A mouse exits the hole...");
						create();
					}
				});
				WorldTasks.schedule(new WorldTask() {
					int tick;
					NPC mouse;
					@Override
					public void run() {
						if(tick == 0 )
							mouse = World.spawnNPC(MOUSE, new WorldTile(obj.getX()-1, obj.getY(), obj.getPlane()), -1, false, true);
						if(tick == 30) {
							if(!mouse.hasFinished())
								mouse.finish();
							stop();
						}
						tick++;
					}
				}, 0, 1);
			}
		}
	};

	public static ItemOnNPCHandler handleItemOnMouse = new ItemOnNPCHandler(MOUSE) {
		@Override
		public void handle(ItemOnNPCEvent e) {
			if(e.getItem().getId() == MAGNET) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addSimple("You attach the magnet to the mouse's harness. The mouse finishes the cheese and runs back into its hole. You hear some" +
								" odd noises from inside the walls. There is a strange whirring noise from above the door frame.");
						create();
					}
				});
				e.getNPC().finish();
				e.getPlayer().getQuestManager().getAttribs(Quest.WITCHS_HOUSE).setB(MOUSE_SOLVED_ATTR, true);
			}

		}
	};



	public static ObjectClickHandler handleWitchHouseLadderToBasement = new ObjectClickHandler(new Object[] { 24717, 24718 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getObjectId() == 24718)
				e.getPlayer().useLadder(new WorldTile(2774, 9759, 0));
			else
				e.getPlayer().useLadder(new WorldTile(2898, 3376, 0));
		}
	};
	public static ObjectClickHandler handleWitchHouseElectricGate = new ObjectClickHandler(new Object[] { 2866 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			String itemName = new Item(p.getEquipment().getGlovesId(), 1).getName();
			if(!p.getEquipment().wearingGloves() || (!itemName.contains("gloves") && !itemName.contains("Gloves"))) {
				p.startConversation(new Conversation(e.getPlayer()) {
					{
						addSimple("As your bare hands touch the gate you feel a shock", () -> {
							p.applyHit(new Hit(76, Hit.HitLook.TRUE_DAMAGE));
						});
						addPlayer(HeadE.SCARED, "I will need some gloves to stop the electric current...");
						create();
					}
				});
				return;
			}
			handleDoubleDoor(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleWitchsHouseCupboard = new ObjectClickHandler(new Object[] { 2868, 2869 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.getOption().equalsIgnoreCase("open")) {
				p.setNextAnimation(new Animation(536));
				p.lock(2);
				GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
				p.faceObject(openedChest);
				World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
			}
			if(e.getOption().equalsIgnoreCase("shut")) {
				p.setNextAnimation(new Animation(536));
				p.lock(2);
				GameObject openedChest = new GameObject(obj.getId() - 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
				p.faceObject(openedChest);
				World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
			}
			if(e.getOption().equalsIgnoreCase("search"))
				if(p.getInventory().containsItem(MAGNET, 1))
					p.sendMessage("The cupboard is empty.");
				else if(p.getInventory().hasFreeSlots())
					p.getInventory().addItem(new Item(MAGNET, 1));
				else
					p.sendMessage("You need more inventory space.");

		}
	};

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.HITPOINTS, 6325);
		getQuest().sendQuestCompleteInterface(player, BALL, "6,325 Constitution XP");
	}

}
