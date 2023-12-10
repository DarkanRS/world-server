package com.rs.game.content.quests.witchshouse;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.*;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.List;

import static com.rs.game.content.world.doors.Doors.handleDoor;
import static com.rs.game.content.world.doors.Doors.handleDoubleDoor;

@QuestHandler(Quest.WITCHS_HOUSE)
@PluginEventHandler
public class WitchsHouse extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int FIND_BALL = 1;
	public final static int QUEST_COMPLETE = 2;

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

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
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
            lines.add("*Hint, you may need cheese");
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

	public static ItemClickHandler handleClickOnWitchDiary = new ItemClickHandler(new Object[] { WITCH_DIARY }, new String[] { "Read" }, e -> e.getPlayer().openBook(new WitchsDiary()));

	public static ObjectClickHandler handlePottedPlant = new ObjectClickHandler(new Object[] { 2867 }, e -> {
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
	});
	
	public static ObjectClickHandler handleFountain = new ObjectClickHandler(new Object[] { 2864 }, e -> {
		Player p = e.getPlayer();
		if(!p.getInventory().containsItem(BACKROOM_KEY, 1)) {
			if(!p.getInventory().hasFreeSlots()) {
				p.sendMessage("You don't have room...");
				return;
			}
			p.getInventory().addItem(new Item(BACKROOM_KEY, 1));
			p.sendMessage("You found a key!");
		}
	});

	public static ObjectClickHandler handleWitchHouseFrontDoor = new ObjectClickHandler(new Object[] { 2861 }, e -> {
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
	});

	public static ObjectClickHandler handleWitchHouseBackRoomDoor = new ObjectClickHandler(new Object[] { 2863 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(p.getInventory().containsItem(new Item(BACKROOM_KEY, 1))) {
			handleDoor(p, obj);
			if(!p.getQuestManager().getAttribs(Quest.WITCHS_HOUSE).getB("KILLED_EXPERIMENT")) {
				for (NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1))
					if (npc.getId() == EXPERIMENT1 || npc.getId() == EXPERIMENT2 || npc.getId() == EXPERIMENT3 || npc.getId() == EXPERIMENT4)
						return;
				World.spawnNPC(EXPERIMENT1, Tile.of(2927, 3359, 0), -1, false, true);
			}
		} else
			p.startConversation(new Conversation(e.getPlayer()) {
				{
					addSimple("It appears to need a key...");
					create();
				}
			});
	});

	public static NPCDeathHandler handleExperiment1 = new NPCDeathHandler(EXPERIMENT1, e -> {
		NPC n = World.spawnNPC(EXPERIMENT2, Tile.of(2927, 3363, 0), -1, false, true);
		n.setTarget(e.getKiller());
	});

	public static NPCDeathHandler handleExperiment2 = new NPCDeathHandler(EXPERIMENT2, e -> {
		NPC n = World.spawnNPC(EXPERIMENT3, Tile.of(2927, 3363, 0), -1, false, true);
		n.setTarget(e.getKiller());
	});

	public static NPCDeathHandler handleExperiment3 = new NPCDeathHandler(EXPERIMENT3, e -> {
		NPC n = World.spawnNPC(EXPERIMENT4, Tile.of(2927, 3363, 0), -1, false, true);
		n.setTarget(e.getKiller());
	});

	public static NPCDeathHandler handleExperiment4 = new NPCDeathHandler(EXPERIMENT4, e-> {
		if (e.killedByPlayer()) {
			Player p = (Player) e.getKiller();
			if (p.getQuestManager().getStage(Quest.WITCHS_HOUSE) == FIND_BALL)
				p.getQuestManager().getAttribs(Quest.WITCHS_HOUSE).setB("KILLED_EXPERIMENT", true);
		}
	});

	public static PickupItemHandler handleBallPickup = new PickupItemHandler(new Object[] { BALL }, Tile.of(2927, 3360, 0), e -> {
		Player p = e.getPlayer();
		if(p.getQuestManager().getStage(Quest.WITCHS_HOUSE) != FIND_BALL) {
			e.cancelPickup();
			p.startConversation(new Dialogue().addSimple("I better not touch it..."));
			return;
		}
		if(!p.getQuestManager().getAttribs(Quest.WITCHS_HOUSE).getB("KILLED_EXPERIMENT")) {
			for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1))
				if(npc.getId() == EXPERIMENT1 || npc.getId() == EXPERIMENT2 || npc.getId() == EXPERIMENT3 || npc.getId() == EXPERIMENT4)
					npc.setTarget(p);
			e.cancelPickup();
			p.sendMessage("The experiment won't let you pick up the ball");
		}
	});

	public static PlayerStepHandler handleCheesePrompt = new PlayerStepHandler(Tile.of(2894, 3367, 0), e -> {
		if (e.getPlayer().getQuestManager().getStage(Quest.WITCHS_HOUSE) == FIND_BALL && e.getPlayer().containsItem(1985))
			if (!e.getPlayer().getTempAttribs().getB("MousePuzzleKnownWitchsHouse")) {
				e.getPlayer().sendMessage("You hear a hungry mouse in this room, must be the cheese...");
				e.getPlayer().getTempAttribs().setB("MousePuzzleKnownWitchsHouse", true);
			}
	});

	public static ObjectClickHandler handleWitchHouseMouseDoor = new ObjectClickHandler(new Object[] { 2862 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(p.getQuestManager().getAttribs(Quest.WITCHS_HOUSE).getB("MOUSE_SOLVED"))
			handleDoor(p, obj);
		else
			p.startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.CALM_TALK, "It appears to be locked by some mechanism, perhaps by the mouse hole?");
					create();
				}
			});
	});

	public static ItemOnObjectHandler handleMouseHole = new ItemOnObjectHandler(new Object[] { 2870 }, null, e -> {
		GameObject obj = e.getObject();
		if(e.getPlayer().getQuestManager().getAttribs(Quest.WITCHS_HOUSE).getB("MOUSE_SOLVED")) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.CALM_TALK, "The door is already unlocked...");
					create();
				}
			});
			return;
		}
		if(e.getItem().getName().equalsIgnoreCase("Cheese")) {
			for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1))
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
			WorldTasks.schedule(new Task() {
				int tick;
				NPC mouse;
				@Override
				public void run() {
					if(tick == 0 )
						mouse = World.spawnNPC(MOUSE, Tile.of(obj.getX()-1, obj.getY(), obj.getPlane()), -1, false, true);
					if(tick == 30) {
						if(!mouse.hasFinished())
							mouse.finish();
						stop();
					}
					tick++;
				}
			}, 0, 1);
		}
	});

	public static ItemOnNPCHandler handleItemOnMouse = new ItemOnNPCHandler(MOUSE, e -> {
		if(e.getItem().getId() == MAGNET) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addSimple("You attach the magnet to the mouse's harness. The mouse finishes the cheese and runs back into its hole. You hear some" +
							" odd noises from inside the walls. There is a strange whirring noise from above the door frame.");
					create();
				}
			});
			e.getNPC().finish();
			e.getPlayer().getQuestManager().getAttribs(Quest.WITCHS_HOUSE).setB("MOUSE_SOLVED", true);
		}
	});

	public static ObjectClickHandler handleWitchHouseLadderToBasement = new ObjectClickHandler(new Object[] { 24717, 24718 }, e -> {
		if(e.getObjectId() == 24718)
			e.getPlayer().useLadder(Tile.of(2774, 9759, 0));
		else
			e.getPlayer().useLadder(Tile.of(2898, 3376, 0));
	});
	public static ObjectClickHandler handleWitchHouseElectricGate = new ObjectClickHandler(new Object[] { 2866, 2865 }, e -> {
		Player p = e.getPlayer();
		String itemName = new Item(p.getEquipment().getGlovesId(), 1).getName();
		if(p.getEquipment().wearingGloves() || (itemName.contains("gloves") && itemName.contains("Gloves")))
            handleDoubleDoor(e.getPlayer(), e.getObject());
        else
            p.startConversation(new Conversation(e.getPlayer()) {
                {
                    addSimple("As your bare hands touch the gate you feel a shock", () -> {
                        p.applyHit(new Hit(76, Hit.HitLook.TRUE_DAMAGE));
                    });
                    addPlayer(HeadE.SCARED, "I will need some gloves to stop the electric current...");
                    create();
                }
            });
	});

	public static ObjectClickHandler handleWitchsHouseCupboard = new ObjectClickHandler(new Object[] { 2868, 2869 }, e -> {
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
	});

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.HITPOINTS, 6325);
		sendQuestCompleteInterface(player, BALL);
	}

	@Override
	public String getStartLocationDescription() {
		return "Talk to Harvey, the crying boy west of Falador.";
	}

	@Override
	public String getRequiredItemsString() {
		return "Cheese or cheese wheel, leather gloves, some combat equipment and food.";
	}

	@Override
	public String getCombatInformationString() {
		return "You will need to defeat a shapeshifting enemy with forms up to level 49.";
	}

	@Override
	public String getRewardsString() {
		return "6,325 Constitution XP";
	}

}
