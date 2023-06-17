package com.rs.game.content.quests.blackknightsfortress;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.*;

import static com.rs.game.content.world.doors.Doors.handleDoor;

@QuestHandler(Quest.BLACK_KNIGHTS_FORTRESS)
@PluginEventHandler
public class BlackKnightsFortress extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int STARTED = 1;
	public final static int HEARD_PLAN = 2;
	public final static int RUINED_CAULDRON = 3;
	public final static int QUEST_COMPLETE = 4;

	// Animations
	public final static int TOSS_CABBAGE = 9705;
	public final static int CRY = 860;
	public final static int LISTEN_GRILL = 4195;
	public final static int FINISH_LISTEN_GRILL = 4197;
	public final static int CAULDRON_EXPLOSION_GFX = 773;
	public final static int CABBAGE_PROJECTILE = 772;

	// Varbits
	protected final static int CAULDRON_STATUS_VAR = 2494;

	// Objects
	protected final static int GRILL_LISTEN = 2342;
	protected final static int CAULDRON = 17163;
	protected final static int CABBAGE_HOLE = 2336;
	protected final static int FORTRESS_FRONT_DOOR = 2337;
	protected final static int FORTRESS_MEETING_ROOM_DOOR = 2338;

	// Black knight fortress chunks
	protected final static Set<Integer> FORTRESS_CHUNKS = new HashSet<>(Arrays.asList(6180280, 6163896, 6196656, 6196664));

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
		case NOT_STARTED:
			lines.add("The Black Knights are up to no good. You are ");
			lines.add("hired by the White Knights to spy on them and");
			lines.add("uncover their evil scheme.");
			lines.add("");
			lines.add("Talk to Sir Amik Varze, located on the 3rd floor");
			lines.add("in the western tower of the White Knights' Castle");
			lines.add("in Falador to get started.");
			lines.add("");
			break;
		case STARTED:
			lines.add("I should infiltrate the Black Knights fortress and");
			lines.add("find out their plans...");
			lines.add("");
			lines.add("I will need an Iron Chain body & Bronze Med Helm");
			lines.add("equipped to enter...");
			lines.add("");
			lines.add("Perhaps I could listen in through a wall or something");
			lines.add("");
			break;
		case HEARD_PLAN:
			lines.add("Oh no! The Black Knights have a witch who can make an");
			lines.add("invincibility potion. I need to find a way to ruin her");
			lines.add("potion...");
			lines.add("");
			lines.add("Maybe I can toss a cabbage through a vent above it?");
			lines.add("");
			break;
		case RUINED_CAULDRON:
			lines.add("Aha! I have ruined the witch's plan, Time to report to");
			lines.add("Sir Amik Varze to update him Falador is safe!");
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
		player.getInventory().addCoins(2500);
		getQuest().sendQuestCompleteInterface(player, 9591, "2,500 coins");
	}

	public static EnterChunkHandler handleAgressiveKnights = new EnterChunkHandler(e -> {
		if (e.getEntity() instanceof Player p && p.hasStarted() && FORTRESS_CHUNKS.contains(e.getChunkId())) {
			if (p.getQuestManager().getStage(Quest.BLACK_KNIGHTS_FORTRESS) >= STARTED && !p.isQuestComplete(Quest.BLACK_KNIGHTS_FORTRESS)) {
				for (NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1)) {
					if (npc.getName().equalsIgnoreCase("Black Knight"))
						if (npc.lineOfSightTo(p, false)) {
							npc.setTarget(p);
							if (Utils.random(0, 5) == 1)
								npc.forceTalk("Die intruder!");
						}
				}
			}
		}
	});

	public static ObjectClickHandler handleMeetingDoor = new ObjectClickHandler(new Object[] { FORTRESS_MEETING_ROOM_DOOR }, e -> {
		Player p = e.getPlayer();
		if (p.getX() < e.getObject().getX())
			p.startConversation(new Conversation(p) {
				{
					addNPC(4604, HeadE.SKEPTICAL, "I wouldn't go in there if I were you. Those Black Knights are in an important meeting. " + "They said they'd kill anyone who went in there!");
					addOptions("Select an option", new Options() {
						@Override
						public void create() {
							option("Okay, I won't", new Dialogue().addPlayer(HeadE.SCARED, "Okay, I won't."));
							option("I don't care: i'm going in anyway", new Dialogue().addPlayer(HeadE.LAUGH, "I am going in anyway").addNext(() -> {
								handleDoor(p, e.getObject());
							}));
						}
					});
					addPlayer(HeadE.SKEPTICAL_THINKING, "I don't know...");
					create();
				}
			});
		else
			handleDoor(p, e.getObject());
	});

	public static ObjectClickHandler handleFrontDoor = new ObjectClickHandler(new Object[] { FORTRESS_FRONT_DOOR }, e -> {
		Player p = e.getPlayer();
		if (p.getY() > e.getObject().getY()) {
			handleDoor(p, e.getObject());
			return;
		}
		if (p.getEquipment().getHatId() != 1139 || p.getEquipment().getChestId() != 1101) {//bronze med iron chainbody
			p.startConversation(new Conversation(p) {
				{
					addNPC(4604, HeadE.SKEPTICAL, "Password?");
					addPlayer(HeadE.SKEPTICAL_THINKING, "I don't know...");
					create();
				}
			});
			return;
		}
		handleDoor(p, e.getObject());
	});

	public static ObjectClickHandler handleGrill = new ObjectClickHandler(new Object[] { GRILL_LISTEN }, e -> {
		Player p = e.getPlayer();
		if (p.getQuestManager().getStage(Quest.BLACK_KNIGHTS_FORTRESS) != STARTED)
			return;
		p.setNextAnimation(new Animation(LISTEN_GRILL));
		p.startConversation(new Conversation(p) {//610 black knight, 611 witch
			{
				addNPC(610, HeadE.CALM_TALK, "So... how's the secret weapon coming along?");
				addNPC(611, HeadE.HAPPY_TALKING, "The invincibility potion is almost ready...");
				addNPC(611, HeadE.HAPPY_TALKING, "It's taken me FIVE YEARS, but it's almost ready.");
				addNPC(611, HeadE.HAPPY_TALKING, "Greldo, the goblin here, is just going to fetch the last ingredient for me.");
				addNPC(611, HeadE.HAPPY_TALKING, "It's a special cabbage grown by my cousin Helda, who lives in Draynor Manor.");
				addNPC(611, HeadE.HAPPY_TALKING, "The soil there is slightly magical and it gives the cabbages slight magical properties...");
				addNPC(611, HeadE.HAPPY_TALKING, "...not to mention the trees!");
				addNPC(611, HeadE.AMAZED_MILD, "Now, remember, Greldo, only a Draynor Manor cabbage will do! Don't get lazy and bring any old cabbage. THAT" + " would ENITERELY wreck the potion!");
				addNPC(612, HeadE.CHILD_CALM_TALK, "Yeth, mithtreth");//random goblin
				addNext(() -> {
					p.getQuestManager().setStage(Quest.BLACK_KNIGHTS_FORTRESS, HEARD_PLAN, true);
					p.setNextAnimation(new Animation(FINISH_LISTEN_GRILL));
				});
				create();
			}
		});
	});

	public static ItemOnObjectHandler handleCabbageOnHole = new ItemOnObjectHandler(new Object[] { CABBAGE_HOLE }, e -> {
		Player p = e.getPlayer();
		if (p.getQuestManager().getStage(Quest.BLACK_KNIGHTS_FORTRESS) != HEARD_PLAN)
			return;
		GameObject cauldron = World.getObjectWithId(Tile.of(3031, 3507, 0), CAULDRON);

		Tile tileBeforeCutscene = Tile.of(p.getX(), p.getY(), p.getPlane());
		if (e.getItem().getId() == 1965) { //Cabbage
			p.lock();
			p.getInventory().removeItems(new Item(1965, 1));
			WorldTasks.schedule(new WorldTask() {
				int tick;
				final int WITCH_DIALOGUE1 = 10;
				final int KNIGHT_CAPTAIN_DIALOGUE1 = 12;
				final int POTION_RUINED = 20;

				@Override
				public void run() {

					if (tick == 0) {
						p.setNextAnimation(new Animation(TOSS_CABBAGE));
						World.sendProjectile(p, Tile.of(p.getX() + 1, p.getY(), p.getPlane()), CABBAGE_PROJECTILE, 40, 0, 2, 0.1, 20, 0);
					}
					if (tick == 3)
						p.getInterfaceManager().setFadingInterface(115);

					if (tick == 6) {
						p.setNextTile(cauldron.getTile());
						p.getAppearance().transformIntoNPC(264);
					}

					if (tick == 7)
						p.getInterfaceManager().setFadingInterface(170);

					if (tick == WITCH_DIALOGUE1)
						p.startConversation(new Conversation(p) {
							{
								addNPC(611, HeadE.ANGRY, "Where has Greldo got to with that magic cabbage!");
								addNext(() -> {
									tick++;
								});
								create();
							}
						});

					if (tick == KNIGHT_CAPTAIN_DIALOGUE1)
						p.startConversation(new Conversation(p) {//610: black knight captain
							{
								addNPC(610, HeadE.SKEPTICAL_THINKING, "What's that noise?");
								addNPC(611, HeadE.AMAZED_MILD, "Hopefully Greldo with that cabbage... yes look here it co....NOOOOOoooo!");
								addNext(() -> {
									for (NPC npc : World.getNPCsInChunkRange(e.getPlayer().getRegionId(), 2))
										if (npc.getId() == 611 || npc.getId() == 610)
											npc.faceObject(cauldron);
									tick++;
								});
								create();
							}
						});

					if (tick == 14)
						World.sendProjectile(Tile.of(3030, 3507, 0), cauldron, CABBAGE_PROJECTILE, 150, 0, 0, 0.1, 0, 0, proj -> {
							World.sendSpotAnim(Tile.of(p.getX(), p.getY(), p.getPlane()), new SpotAnim(CAULDRON_EXPLOSION_GFX));
						});

					if (tick == POTION_RUINED) {
						p.getVars().setVarBit(CAULDRON_STATUS_VAR, 1);
						p.startConversation(new Conversation(p) {//610 black knight captain, 611 witch
							{
								addNPC(611, HeadE.AMAZED_MILD, "My potion!");
								addNPC(610, HeadE.SCARED, "Oh boy, this doesn't look good!");
								addNPC(4607, HeadE.CAT_CALM_TALK, "Meow!");
								addNext(() -> {
									tick++;
								});
								for (NPC npc : World.getNPCsInChunkRange(e.getPlayer().getRegionId(), 2))
									if (npc.getId() == 611)
										npc.setNextAnimation(new Animation(CRY));
								create();
							}
						});
					}

					if (tick == 22)
						p.getInterfaceManager().setFadingInterface(115);

					if (tick == 25) {
						p.setNextTile(tileBeforeCutscene);
						p.getAppearance().transformIntoNPC(-1);
					}

					if (tick == 26)
						p.getInterfaceManager().setFadingInterface(170);

					if (tick == 27) {
						p.getQuestManager().setStage(Quest.BLACK_KNIGHTS_FORTRESS, RUINED_CAULDRON, true);
						p.unlock();
						stop();
					}

					if (tick != WITCH_DIALOGUE1 + 1 && tick != KNIGHT_CAPTAIN_DIALOGUE1 + 1 && tick != POTION_RUINED + 1)
						tick++;
				}
			}, 0, 1);

		}
	});

	public static LoginHandler onLogin = new LoginHandler(e -> {
		if (e.getPlayer().getQuestManager().getStage(Quest.BLACK_KNIGHTS_FORTRESS) >= RUINED_CAULDRON)
			e.getPlayer().getVars().setVarBit(CAULDRON_STATUS_VAR, 1);
	});

}
