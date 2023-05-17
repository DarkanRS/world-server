package com.rs.game.content.quests.dragonslayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.*;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.DRAGON_SLAYER)
@PluginEventHandler
public class DragonSlayer extends QuestOutline {
	//	public final static int
	public final static int NOT_STARTED = 0;
	public final static int TALK_TO_OZIACH = 1;
	public final static int TALK_TO_GUILDMASTER = 2;//ask all dialogue, get key
	public final static int PREPARE_FOR_CRANDOR = 3;//Melzars maze, chest, wormbrain, then get a boat, then ned.
	public final static int REPORT_TO_OZIACH = 4;
	public final static int QUEST_COMPLETE = 5;

	//Items
	public final static int MAP_PART1 = 1535;
	public final static int MAP_PART2 = 1536;
	public final static int MAP_PART3 = 1537;
	public final static int CRANDOR_MAP = 1538;
	public final static int LAW_RUNE = 563;
	public final static int AIR_RUNE = 556;
	public final static int HAMMER = 2347;
	public final static int ANTI_DRAGON_SHIELD = 1540;
	public final static int STEEL_NAILS = 1539;
	public final static int PLANKS = 960;
	public final static int RED_KEY = 1543;//keys
	public final static int ORANGE_KEY = 1544;
	public final static int YELLOW_KEY = 1545;
	public final static int BLUE_KEY = 1546;
	public final static int MAGENTA_KEY = 1547;
	public final static int GREEN_KEY = 1548;
	public final static int ELVARG_HEAD = 11279;
	public final static int MELZAR_MAZE_KEY = 1542;

	//Game Objects
	public final static int MAP_CHEST2 = 2587;

	//NPCs
	public final static int WORM_BRAIN = 745;
	public final static int NED = 918;
	public final static int CAPTAIN_NED = 6082;//Ned on Lady Lumbridge boat
	public final static int GUILD_MASTER = 198;
	public final static int OZIACH = 747;
	public final static int KLARENSE = 744;
	public final static int JENKINS = 748;
	public final static int ORACLE = 746;

	//Magenta Room
	public final static int MELZAR_THE_MAD_MEGENTA_KEY = 753;
	//Green Room
	public final static int LESSER_DEMON_GREEN_KEY = 4694;
	//Blue Room
	public final static int ZOMBIE_BLUE_KEY = 75;
	//Orange Room
	public final static int GHOST_ORANGE_KEY = 6094;
	//Red Room
	public final static int RAT_RED_KEY = 6088;
	//Yellow Room
	public final static int SKELETON_YELLOW_KEY = 6091;

	//Vars & attribs
	public final static String NED_IS_CAPTAIN_ATTR = "NED_IS_CAPTAIN";
	public final static int NED_BOAT_VISIBILITY_VAR = 176;//7 for visible
	public final static int BOAT_HULL_FIXED_VAR = 1837;//1 for fix, 0 for broken
	//GuildMaster dialogue attributes
	public final static String STARTED_DIALOGUE_GUILDMASTER_ATTR = "STARTED_BRIEFING_FROM_GUILDMASTER";
	public final static String FINISHED_DIALOGUE_GUILDMASTER_ATTR = "FINISHED_BRIEFING_FROM_GUILDMASTER";
	public final static String KNOWS_MAP_EXISTS_ATTR = "KNOWS_MAP_EXISTS";
	public final static String KNOWS_ABOUT_THALZAR_MAP_ATTR = "KNOWS_ABOUT_THALZAR";
	public final static String KNOWS_ABOUT_LOZAR_MAP_ATTR = "KNOWS_ABOUT_LOZAR";
	public final static String KNOWS_ABOUT_MELZAR_MAP_ATTR = "KNOWS_ABOUT_MELZAR_MAP";
	public final static String KNOWS_ABOUT_SHIP_ATTR = "KNOWS_ABOUT_SHIP";
	public final static String KNOWS_ABOUT_DRAGON_BREATH_ATTR = "KNOWS_ABOUT_DRAGON_BREATH";

	//Other attributes
	public final static String OWNS_BOAT_ATTR = "PLAYER_OWNS_BOAT";
	public final static String IS_BOAT_FIXED_ATTR = "IS_BOAT_FIXED";
	public final static String BOAT_FIX_NUM_ATTR = "BOAT_FIX_STAGE";
	public final static String ORACLE_DOOR_KNOWLEDGE_ATTR = "ORACLE_DOOR";
	public final static String DOOR_BOWL_ATTR = "DOOR_BOWL";
	public final static String DOOR_BOMB_ATTR = "DOOR_BOMB";
	public final static String DOOR_SILK_ATTR = "DOOR_SILK";
	public final static String DOOR_CAGE_ATTR = "DOOR_CAGE";
	public final static String FINISHED_BOAT_SCENE_ATTR = "FINISHED_BOAT_SCENE";
	public final static String INTRODUCED_ELVARG_ATTR = "INTRODUCED_ELVARG";

	//Other
	public final static Tile MELZAR_BASEMENT = Tile.of(2933, 9641, 0);
	public final static Tile MELZAR_MAZE = Tile.of(2931, 3250, 0);
	private final static int HAMMER_HITTING_REPAIR_ANIM = 3676;

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("Prove yourself a true champion. Kill the mighty");
			lines.add("dragon Elvarg of Crandor and earn the right to");
			lines.add("buy and wear the Rune platebody.");
			lines.add("");
			lines.add("Talk to the Guildmaster in the Champions Guild in");
			lines.add("South Varrock. You will need 32 quest points.");
			lines.add("");
			break;
		case TALK_TO_OZIACH:
			lines.add("The Guildmaster from the Champion's guild has");
			lines.add("sent me to Oziach in Edgeville to get a quest.");
			lines.add("He also says Oziach can make rune armour,");
			lines.add("");
			lines.add("Maybe I can get a quest and rune armour? I");
			lines.add("should go ask Oziach.");
			lines.add("");
			break;
		case TALK_TO_GUILDMASTER:
			lines.add("Oziach says I cannot wield rune platebodies until");
			lines.add("I prove myself by defeating Elvarg, the dragon of");
			lines.add("Crandor.");
			lines.add("");
			lines.add("I should report to the Guildmaster and get more");
			lines.add("information as to how I should prepare.");
			lines.add("");
			lines.add("You will need to complete all the dialogue to unlock");
			lines.add("the rest of the quest. Dialogue below describing where");
			lines.add("the rest of the quest items will be, will be below:");
			lines.add("");
			break;
		case PREPARE_FOR_CRANDOR:
			lines.add("The guildmaster says I need:");
			lines.add("---A boat---");
			lines.add("I should go to port sarim and ask around to find a");
			lines.add("Boat that is not being used.");
			lines.add("");
			lines.add("---A boat captain---");
			lines.add("Perhaps Ned can help in Draynor village");
			lines.add("");
			lines.add("---A map to Crandor---");
			lines.add("There are 3 pieces I need to gather.");
			lines.add("1. I need to search Melzars Maze, I better ask the");
			lines.add("Guildmaster for a key");
			if(player.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(ORACLE_DOOR_KNOWLEDGE_ATTR)) {
				lines.add("2. I need to use a crayfish cage, an unfired bowl,");
				lines.add("Silk, and a Wizard Mind Bomb on a door in the dwarven");
				lines.add("mine. Inside the door I should find a map piece.");
				lines.add("I can get a Wizard Mind Bomb at the Lighthouse store,");
				lines.add("owned by a guy named Jossik.");
			} else {
				lines.add("2. I should talk to the Oracle at White Wolf Mountain.");
				lines.add("The map piece is somewhere in the dwarven mine.");
			}
			lines.add("3. I hear one of the prisoners in Port Sarim prison has");
			lines.add("a map piece");
			lines.add("");
			break;
		case REPORT_TO_OZIACH:
			lines.add("I have defeated Evlarg! Now I just have to tell Oziach");
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




	public static ItemClickHandler handleClickOnCrandorMap = new ItemClickHandler(new Object[] { CRANDOR_MAP }, e -> {
		if(e.getOption().equalsIgnoreCase("study"))
			e.getPlayer().sendMessage("The map shows a sea path to Crandor...");
		if(e.getOption().equalsIgnoreCase("drop")) {
			e.getPlayer().getInventory().deleteItem(e.getSlotId(), e.getItem());
			World.addGroundItem(e.getItem(), Tile.of(e.getPlayer().getTile()), e.getPlayer());
			e.getPlayer().soundEffect(2739);
		}
	});

	public static ItemClickHandler handleClickOnMapPart = new ItemClickHandler(new Object[] { MAP_PART1, MAP_PART2, MAP_PART3 }, e -> {
		if(e.getOption().equalsIgnoreCase("study"))
			e.getPlayer().sendMessage("The map shows part of a sea path to Crandor...");
		if(e.getOption().equalsIgnoreCase("drop")) {
			e.getPlayer().getInventory().deleteItem(e.getSlotId(), e.getItem());
			World.addGroundItem(e.getItem(), Tile.of(e.getPlayer().getTile()), e.getPlayer());
			e.getPlayer().soundEffect(2739);
		}
	});

	public static ItemOnItemHandler createMapFromParts = new ItemOnItemHandler(new int[]{MAP_PART1, MAP_PART2, MAP_PART3}, new int[]{MAP_PART1, MAP_PART2, MAP_PART3}, e -> {
		if (e.getPlayer().getInventory().containsItem(MAP_PART1, 1))
			if (e.getPlayer().getInventory().containsItem(MAP_PART2, 1))
				if (e.getPlayer().getInventory().containsItem(MAP_PART3, 1)) {
					e.getPlayer().getInventory().removeItems(new Item(MAP_PART1, 1), new Item(MAP_PART2, 1), new Item(MAP_PART3, 1));
					e.getPlayer().getInventory().addItem(new Item(CRANDOR_MAP, 1), true);
				}
	});

	public static LoginHandler onLogin = new LoginHandler(e -> {
		if(e.getPlayer().getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(NED_IS_CAPTAIN_ATTR))
			e.getPlayer().getVars().setVar(NED_BOAT_VISIBILITY_VAR, 7);
		else
			;
		if(e.getPlayer().getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getI(BOAT_FIX_NUM_ATTR) >= 3) {
			e.getPlayer().getVars().setVarBit(BOAT_HULL_FIXED_VAR, 1);
			e.getPlayer().getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(IS_BOAT_FIXED_ATTR, true);
		}
	});

	public static ObjectClickHandler handleBoatFix = new ObjectClickHandler(new Object[] { 25036 }, e -> {
		Player p = e.getPlayer();
		if (p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(OWNS_BOAT_ATTR) || p.isQuestComplete(Quest.DRAGON_SLAYER)) {
			if(p.getInventory().containsItem(new Item(HAMMER, 1)) || p.containsTool(HAMMER)) {
				if (p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getI(BOAT_FIX_NUM_ATTR) <= 2)
					if (p.getInventory().containsItem(STEEL_NAILS, 30) && p.getInventory().containsItem(PLANKS, 1)) {
						p.setNextAnimation(new Animation(HAMMER_HITTING_REPAIR_ANIM));
						p.getInventory().removeItems(new Item(STEEL_NAILS, 30), new Item(PLANKS, 1));
						p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setI(BOAT_FIX_NUM_ATTR, p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getI(BOAT_FIX_NUM_ATTR) + 1);
						if (p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getI(BOAT_FIX_NUM_ATTR) >= 3) {
							p.getVars().setVarBit(BOAT_HULL_FIXED_VAR, 1);
							p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(IS_BOAT_FIXED_ATTR, true);
							p.startConversation(new Conversation(e.getPlayer()) {
								{
									addPlayer(HeadE.HAPPY_TALKING, "There, all done...");
									create();
								}
							});
						}
					} else
						p.startConversation(new Conversation(e.getPlayer()) {
							{
								addPlayer(HeadE.HAPPY_TALKING, "I am going to need a hammer, planks and some steel nails...");
								addPlayer(HeadE.HAPPY_TALKING, (90-p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getI(BOAT_FIX_NUM_ATTR) * 30) + " steel nails and " + (3-p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getI(BOAT_FIX_NUM_ATTR)) + " planks.");
								create();
							}
						});
			} else
				p.startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.HAPPY_TALKING, "I am going to need a hammer, planks and some steel nails....");
						addPlayer(HeadE.HAPPY_TALKING, (90-p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getI(BOAT_FIX_NUM_ATTR) * 30) + " steel nails and " + (3-p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getI(BOAT_FIX_NUM_ATTR)) + " planks.");
						create();
					}
				});
		} else
			p.startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.HAPPY_TALKING, "This isn't my boat, better not touch it...");
					create();
				}
			});
	});

	public static ObjectClickHandler handleMagicDoor = new ObjectClickHandler(new Object[] { 25115 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER);

		if(p.getX() > obj.getX()) {
			p.startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.SCARED, "It appears to be locked from the outside, I am trapped!");
					create();
				}
			});
			return;
		}

		if(p.getQuestManager().getStage(Quest.DRAGON_SLAYER) != PREPARE_FOR_CRANDOR)
			return;

		if(attr.getB(DOOR_BOMB_ATTR) && attr.getB(DOOR_BOWL_ATTR) && attr.getB(DOOR_SILK_ATTR) && attr.getB(DOOR_CAGE_ATTR)) {
			obj.animate(new Animation(6636));
			WorldTasks.schedule(new WorldTask() {
				int tick;
				@Override
				public void run() {
					if(tick == 0)
						p.lock();
					if(tick == 1)
						p.walkToAndExecute(Tile.of(3050, 9840, 0), ()-> {
							p.faceEast();
							tick++;
						});
					if(tick==2) {
						p.sendMessage("The magic door opens...");
						obj.animate(new Animation(6636));
					}
					if(tick==3)
						p.addWalkSteps(Tile.of(3051, 9840, 0), 3, false);
					if(tick==5)
						obj.animate(new Animation(6637));
					if(tick==7) {
						p.unlock();
						stop();
					}

					if(tick != 1)
						tick++;
				}
			}, 0, 1);
		} else
			p.startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.SKEPTICAL_THINKING, "It appears to be magically locked...");
					create();
				}
			});
	});

	public static ObjectClickHandler handleMagicChest = new ObjectClickHandler(new Object[] { MAP_CHEST2 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(p.getQuestManager().getStage(Quest.DRAGON_SLAYER) != PREPARE_FOR_CRANDOR)
			return;
		if(p.getInventory().containsItem(MAP_PART3, 1)) {
			p.startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.SKEPTICAL_THINKING, "The chest is empty...");
					create();
				}
			});
			return;
		}
		p.startConversation(new Conversation(e.getPlayer()) {
			{
				addSimple("As you open the chest you notice an inscription on the lid.");
				addSimple("Here, I rest the map of my beloved home. To whoever finds it, I beg of you, let it be. I was honour-bound not to destroy the " +
						"map piece, but I have used all of my magical skill to keep it from being recovered.");
				addSimple("But revenge would not benefit me now, and to disturb this beast is to risk bringing its wrath down upon another land. " +
						"This map leads to the lair of the beast that destroyed my home, devoured my family, and burned to a cinder all that I love.");
				addSimple("I cannot stop you from taking this map piece now, but think on this: if you can slay the Dragon of Crandor, you are a " +
						"greater hero than my land ever produced. There is no shame in backing out now.");
				addNext(()-> {
					p.setNextAnimation(new Animation(536));
					p.lock(2);
					GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
					p.faceObject(openedChest);
					World.spawnObjectTemporary(openedChest, Ticks.fromSeconds(4));
					p.getInventory().addItem(new Item(MAP_PART3, 1), true);
				});
				create();
			}
		});
	});
	
	static final int SILK = 950;
	static final int BOMB = 1907;
	static final int BOWL = 1791;
	static final int CAGE = 13431;

	public static ItemOnObjectHandler itemOnMagicDoor = new ItemOnObjectHandler(true, new Object[] { 25115 }, e -> {
		Player p = e.getPlayer();
		GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER);
		if (e.getItem().getId() == SILK)
			if(attr.getB(DOOR_SILK_ATTR))
				p.sendMessage("Silk seems to have been used on the door...");
			else {
				attr.setB(DOOR_SILK_ATTR, true);
				p.getInventory().removeItems(new Item(SILK, 1));
				p.sendMessage("The door consumes the silk...");
			}
		if (e.getItem().getId() == BOMB)
			if(attr.getB(DOOR_BOMB_ATTR))
				p.sendMessage("A wizard mind bomb seems to have been used on the door...");
			else {
				attr.setB(DOOR_BOMB_ATTR, true);
				p.getInventory().removeItems(new Item(BOMB, 1));
				p.sendMessage("You pour the wizard mind bomb on the door...");
			}
		if (e.getItem().getId() == BOWL)
			if(attr.getB(DOOR_BOWL_ATTR))
				p.sendMessage("A bowl seems to have been used on the door...");
			else {
				attr.setB(DOOR_BOWL_ATTR, true);
				p.getInventory().removeItems(new Item(BOWL, 1));
				p.sendMessage("You place the unfired bowl on the door...");
			}
		if (e.getItem().getId() == CAGE || e.getItem().getId() == 301)
			if(attr.getB(DOOR_CAGE_ATTR))
				p.sendMessage("The cage seems to have been used on the door...");
			else {
				attr.setB(DOOR_CAGE_ATTR, true);
				p.getInventory().removeItems(new Item(CAGE, 1));
				p.sendMessage("The door consumes the cage...");
			}
	});

	public static void introduceElvarg(Player p) {
		WorldTasks.schedule(new WorldTask() {
			int tick;
			@Override
			public void run() {
				if (tick == 0) { // setup p1
					p.lock();
					p.getInterfaceManager().setFadingInterface(115);
					p.getPackets().setBlockMinimapState(2);
				}
				if (tick == 3) {// setup p2, move p
					p.getAppearance().transformIntoNPC(266);
					p.setNextTile(Tile.of(2845, 9636, 0));
				}
				if (tick == 5) {// setup p3, camera
					p.getPackets().sendCameraPos(p.getXInScene(p.getSceneBaseChunkId()), p.getYInScene(p.getSceneBaseChunkId()), 1300);
					p.getPackets().sendCameraLook(p.getXInScene(p.getSceneBaseChunkId()) + 4, p.getYInScene(p.getSceneBaseChunkId()) - 4, 50);
				}
				if (tick == 6) {// start scene
					p.getInterfaceManager().setFadingInterface(170);
					p.getPackets().sendCameraPos(p.getXInScene(p.getSceneBaseChunkId())+10, p.getYInScene(p.getSceneBaseChunkId())+7, 1400);
					p.getPackets().sendCameraLook(p.getXInScene(p.getSceneBaseChunkId()) + 10, p.getYInScene(p.getSceneBaseChunkId()) +12, 1300);
					p.getPackets().sendCameraPos(p.getXInScene(p.getSceneBaseChunkId())+10, p.getYInScene(p.getSceneBaseChunkId())+3, 1400, 0, 4);
				}
				if(tick == 10) {
					p.getPackets().sendCameraPos(p.getXInScene(p.getSceneBaseChunkId())+14, p.getYInScene(p.getSceneBaseChunkId())+9, 3000);
					p.getPackets().sendCameraLook(p.getXInScene(p.getSceneBaseChunkId()) + 11, p.getYInScene(p.getSceneBaseChunkId()) +4, 1200);
				}

				if(tick == 15)
					p.getInterfaceManager().setFadingInterface(115);
				if(tick==18) {
					p.setNextTile(Tile.of(2834, 9657, 0));
					p.getPackets().sendResetCamera();
				}
				if (tick == 21) {// closing p2
					p.getAppearance().transformIntoNPC(-1);
					p.getInterfaceManager().setFadingInterface(170);
					p.getPackets().setBlockMinimapState(0);
					p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(INTRODUCED_ELVARG_ATTR, true);
					p.unlock();
					stop();
				}
				tick++;
			}
		}, 0, 1);
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.STRENGTH, 18650);
		player.getSkills().addXpQuest(Constants.DEFENSE, 18650);
		getQuest().sendQuestCompleteInterface(player, 11279, "18,650 Strength XP", "18,650 Defence XP");
	}



}

