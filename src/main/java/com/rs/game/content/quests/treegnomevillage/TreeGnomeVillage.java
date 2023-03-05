package com.rs.game.content.quests.treegnomevillage;

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.content.world.doors.Doors;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@QuestHandler(Quest.TREE_GNOME_VILLAGE)
@PluginEventHandler
public class TreeGnomeVillage extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int TALK_TO_MONTAI_ABOUT_WOOD = 1;
	public final static int GET_WOOD = 2;
	public final static int TALK_TO_MONTAI_ABOUT_TRACKERS = 3;
	public final static int FIRE_BALLISTA = 4;
	public final static int ORB1 = 5;
	public final static int KILL_WARLORD = 6;
	public final static int QUEST_COMPLETE = 7;

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case NOT_STARTED -> {
				lines.add("The tree gnomes are in trouble. General Khazard's forces are ");
				lines.add("hunting them to extinction. Find your way through the hedge");
				lines.add("maze to the gnomes secret treetop village. Then help the gnomes");
				lines.add("fight Khazard and retrieve the orbs of protection.");
				lines.add("");
			}
			case TALK_TO_MONTAI_ABOUT_WOOD -> {
				lines.add("I am to talk to Commander Montai to help the gnomes fight on the");
				lines.add("battlefield.");
				lines.add("");
			}
			case GET_WOOD -> {
				lines.add("I should gather wood for Commander Montai and bring them to him.");
				lines.add("");
			}
			case TALK_TO_MONTAI_ABOUT_TRACKERS -> {
				lines.add("Now that that's over let's see how else we can help on the");
				lines.add("battlefield by talking to Commander Montai again.");
				lines.add("");
			}
			case FIRE_BALLISTA -> {
				lines.add("I need to get 3 coordinates from 3 tracker gnomes. After");
				lines.add("I speak with them I can aim and fire a ballista on the");
				lines.add("battlefield toward the enemy stronghold.");
				lines.add("");
				lines.add((player.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).getB("tracker1found") ? "<str>" : "") + "I have spoken to the 1st tracker");
				lines.add((player.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).getB("tracker2found") ? "<str>" : "") + "I have spoken to the 2nd tracker");
				lines.add((player.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).getB("tracker3found") ? "<str>" : "") + "I have spoken to the 3rd tracker");
				lines.add("");
			}
			case ORB1 -> {
				lines.add("Now that the ballista was fired I can force my way");
				lines.add("into the stronghold and steal the orb.");
				lines.add("");
			}
			case KILL_WARLORD -> {
				lines.add("A Khazard Warlord has the remaining orbs. I should");
				lines.add("find him, kill him and return them.");
				lines.add("");
			}
			case QUEST_COMPLETE -> {
				lines.add("");
				lines.add("");
				lines.add("QUEST COMPLETE!");
			}
			default -> {
				lines.add("Invalid quest stage. Report this to an administrator.");
			}
		}
		return lines;
	}

	public static ObjectClickHandler handleDoorTracker2 = new ObjectClickHandler(new Object[] { 40362, 40361 }, e -> {
		Doors.handleDoor(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleWallBallistaHit = new ObjectClickHandler(new Object[] { 12762 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE) == ORB1) {
			Player p = e.getPlayer();
			WorldTasks.schedule(new WorldTask() {
				int tick = 0;
				boolean isPlayerNorth = true;
				@Override
				public void run() {
					if (tick == 0) {
						if (p.getY() == 3254) {
							p.lock(2);
							p.faceSouth();
							p.setNextAnimation(new Animation(839));
							isPlayerNorth = true;
						} else if (p.getY() == 3252) {
							p.lock(2);
							p.faceNorth();
							p.setNextAnimation(new Animation(839));
							isPlayerNorth = false;
						} else
							stop();
					} else if (tick >= 1) {
						if (isPlayerNorth)
							p.setNextTile(Tile.of(2509, 3252, 0));
						if (!isPlayerNorth) {
							p.setNextTile(Tile.of(2509, 3254, 0));
							for(NPC npc : World.getNPCsInChunkRange(p.getChunkId(), 1))
								if(npc.getId() == 478 && npc.getPlane() == 0) {//Khazard Commander
									npc.forceTalk("Hey, what are you doing in here?");
									npc.setTarget(p);
								}
						}
						stop();
					}
					tick++;
				}

			}, 0, 1);
			return;
		}
		e.getPlayer().sendMessage("The wall is too tough to cross");
	});

	public static PlayerStepHandler handleCommanderUpstairs = new PlayerStepHandler(new Tile[] { Tile.of(2503, 3254, 1), Tile.of(2504, 3254, 1), Tile.of(2502, 3254, 1) }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE) == ORB1)
			for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1))
				if(npc.getId() == 478 && npc.getPlane() == 1 && npc.getTarget() != e.getPlayer()) {//Khazard Commander
					npc.forceTalk("Hey, get out of here!");
					npc.setTarget(e.getPlayer());
				}
	});

	public static PlayerStepHandler handleCommanderDownstairs = new PlayerStepHandler(Tile.of(2505, 3256, 0), e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE) == ORB1)
			for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1))
				if(npc.getId() == 478 && npc.getPlane() == 0 && npc.getTarget() != e.getPlayer()) {//Khazard Commander
					npc.forceTalk("Get out! What are you doing here?!");
					npc.setTarget(e.getPlayer());
				}
	});

	public static NPCDeathHandler handleWarlordDrop = new NPCDeathHandler(new Object[] { 477 }, e -> {
		if(e.getKiller() instanceof Player p && p.getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE) == KILL_WARLORD && !p.getInventory().containsItem(588))
			World.addGroundItem(new Item(588, 1), Tile.of(e.getNPC().getTile()), p);
	});

	public static ObjectClickHandler handleOrb1Chest = new ObjectClickHandler(new Object[] { 2183 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE) == ORB1 && !e.getPlayer().getInventory().containsItem(587)) {
			WorldTasks.scheduleTimer(tick -> {
				if(tick == 0) {
					e.getPlayer().lock(2);
					e.getPlayer().faceObject(e.getObject());
					e.getPlayer().setNextAnimation(new Animation(536));
				}
				if(tick == 1) {
					e.getObject().setIdTemporary(2182, 2);
					e.getPlayer().getInventory().addItem(new Item(587, 1));
					return false;
				}
				return true;
			});
		}
	});

	public static ObjectClickHandler handleBallista = new ObjectClickHandler(new Object[] { 69527 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE) == FIRE_BALLISTA && has3TrackerCoordinates(e.getPlayer())) {
			Dialogue rightCoordinate = new Dialogue().addSimple("The huge spear flies through the air and screams down directly into the Khazard stronghold. " +
					"A deafening crash echoes over the battlefield as the front entrance is reduced to rubble.", ()->{
				e.getPlayer().getQuestManager().setStage(Quest.TREE_GNOME_VILLAGE, ORB1);
			});
			Dialogue wrongCoordinate = new Dialogue().addSimple("The huge spear completely misses the Khazard stronghold!");
			int coordinate = e.getPlayer().getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE).getI("tracker3coordinate");
			e.getPlayer().startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "That tracker gnome was a bit vague about the x coordinate! What could it be?")
					.addOptions("What is the X coordinate?", new Options() {
						@Override
						public void create() {
							option("0001", new Dialogue()
									.addNext(coordinate == 1 ? rightCoordinate : wrongCoordinate)
							);
							option("0002", new Dialogue()
									.addNext(coordinate == 2 ? rightCoordinate : wrongCoordinate)
							);
							option("0003", new Dialogue()
									.addNext(coordinate == 3 ? rightCoordinate : wrongCoordinate)
							);
							option("0004", new Dialogue()
									.addNext(coordinate == 4 ? rightCoordinate : wrongCoordinate)
							);
						}
					})
			);
			return;
		}
		e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.SECRETIVE, "I don't know what to do with this ballista..."));
	});

	private static boolean has3TrackerCoordinates(Player p) {
		GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.TREE_GNOME_VILLAGE);
		for(int i = 1; i <= 3; i++)
			if(!attr.getB("tracker"+i+"found"))
				return false;
		return true;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXp(Constants.ATTACK, 11450);
		player.getInventory().addItem(new Item(589, 1), true);
		getQuest().sendQuestCompleteInterface(player, 589, "11,450 Attack XP", "Spirit Tree Access");
	}

}
