package com.rs.game.content.quests.handlers.blackknightsfortress;

import static com.rs.game.content.world.doors.Doors.handleDoor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@QuestHandler(Quest.BLACK_KNIGHTS_FORTRESS)
@PluginEventHandler
public class BlackKnightsFortress extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int STARTED = 1;
	public final static int HEARD_PLAN = 2;
	public final static int RUINED_CAULDRON = 3;
	public final static int QUEST_COMPLETE = 4;

	// items
	protected final static int IRON_CHAINBODY = 1101;
	protected final static int BRONZE_MED_HELM = 1139;
	protected final static int CABBAGE = 1965;

	// Npc
	protected final static int SIR_AMIK_VARZE = 608;
	protected final static int BLACK_KNIGHT_CAPTAIN = 610;
	protected final static int WITCH = 611;
	protected final static int GRELDO = 612;
	protected final static int FORTRESS_FRONT_GAURD = 4604;
	protected final static int BLACK_CAT = 4607;
	protected final static int NULL_NPC = 264;

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
	public ArrayList<String> getJournalLines(Player player, int stage) {
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
		player.getInventory().addItem(new Item(995, 2500), true);
		getQuest().sendQuestCompleteInterface(player, 9591, "2,500 coins");
	}

	public static EnterChunkHandler handleAgressiveKnights = new EnterChunkHandler() {
		@Override
		public void handle(EnterChunkEvent e) {
			if (e.getEntity() instanceof Player p && p.hasStarted() && FORTRESS_CHUNKS.contains(e.getChunkId())) {
				if (p.getQuestManager().getStage(Quest.BLACK_KNIGHTS_FORTRESS) >= STARTED && !p.isQuestComplete(Quest.BLACK_KNIGHTS_FORTRESS)) {
					for (NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId())) {
						if (npc.getName().equalsIgnoreCase("Black Knight"))
							if (npc.lineOfSightTo(p, false)) {
								npc.setTarget(p);
								if (Utils.random(0, 5) == 1)
									npc.forceTalk("Die intruder!");
							}
					}
				}
			}
		}
	};

	public static ObjectClickHandler handleMeetingDoor = new ObjectClickHandler(new Object[] { FORTRESS_MEETING_ROOM_DOOR }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if (p.getX() < e.getObject().getX())
				p.startConversation(new Conversation(p) {
					{
						addNPC(FORTRESS_FRONT_GAURD, HeadE.SKEPTICAL, "I wouldn't go in there if I were you. Those Black Knights are in an important meeting. " + "They said they'd kill anyone who went in there!");
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
		}
	};

	public static ObjectClickHandler handleFrontDoor = new ObjectClickHandler(new Object[] { FORTRESS_FRONT_DOOR }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if (p.getY() > e.getObject().getY()) {
				handleDoor(p, e.getObject());
				return;
			}
			if (p.getEquipment().getHatId() != BRONZE_MED_HELM || p.getEquipment().getChestId() != IRON_CHAINBODY) {
				p.startConversation(new Conversation(p) {
					{
						addNPC(FORTRESS_FRONT_GAURD, HeadE.SKEPTICAL, "Password?");
						addPlayer(HeadE.SKEPTICAL_THINKING, "I don't know...");
						create();
					}
				});
				return;
			}
			handleDoor(p, e.getObject());
		}
	};

	public static ObjectClickHandler handleGrill = new ObjectClickHandler(new Object[] { GRILL_LISTEN }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if (p.getQuestManager().getStage(Quest.BLACK_KNIGHTS_FORTRESS) != STARTED)
				return;
			p.setNextAnimation(new Animation(LISTEN_GRILL));
			p.startConversation(new Conversation(p) {
				{
					addNPC(BLACK_KNIGHT_CAPTAIN, HeadE.CALM_TALK, "So... how's the secret weapon coming along?");
					addNPC(WITCH, HeadE.HAPPY_TALKING, "The invincibility potion is almost ready...");
					addNPC(WITCH, HeadE.HAPPY_TALKING, "It's taken me FIVE YEARS, but it's almost ready.");
					addNPC(WITCH, HeadE.HAPPY_TALKING, "Greldo, the goblin here, is just going to fetch the last ingredient for me.");
					addNPC(WITCH, HeadE.HAPPY_TALKING, "It's a special cabbage grown by my cousin Helda, who lives in Draynor Manor.");
					addNPC(WITCH, HeadE.HAPPY_TALKING, "The soil there is slightly magical and it gives the cabbages slight magical properties...");
					addNPC(WITCH, HeadE.HAPPY_TALKING, "...not to mention the trees!");
					addNPC(WITCH, HeadE.AMAZED_MILD, "Now, remember, Greldo, only a Draynor Manor cabbage will do! Don't get lazy and bring any old cabbage. THAT" + " would ENITERELY wreck the potion!");
					addNPC(GRELDO, HeadE.CHILD_CALM_TALK, "Yeth, mithtreth");
					addNext(() -> {
						p.getQuestManager().setStage(Quest.BLACK_KNIGHTS_FORTRESS, HEARD_PLAN, true);
						p.setNextAnimation(new Animation(FINISH_LISTEN_GRILL));
					});
					create();
				}
			});
		}
	};

	public static ItemOnObjectHandler handleCabbageOnHole = new ItemOnObjectHandler(new Object[] { CABBAGE_HOLE }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			Player p = e.getPlayer();
			if (p.getQuestManager().getStage(Quest.BLACK_KNIGHTS_FORTRESS) != HEARD_PLAN)
				return;
			GameObject cauldron = World.getRegion(12086).getObjectWithId(CAULDRON, 0);

			WorldTile tileBeforeCutscene = new WorldTile(p.getX(), p.getY(), p.getPlane());
			if (e.getItem().getId() == CABBAGE) {
				p.lock();
				p.getInventory().removeItems(new Item(CABBAGE, 1));
				WorldTasks.schedule(new WorldTask() {
					int tick;
					final int WITCH_DIALOGUE1 = 10;
					final int KNIGHT_CAPTAIN_DIALOGUE1 = 12;
					final int POTION_RUINED = 20;

					@Override
					public void run() {

						if (tick == 0) {
							p.setNextAnimation(new Animation(TOSS_CABBAGE));
							World.sendProjectile(p, new WorldTile(p.getX() + 1, p.getY(), p.getPlane()), CABBAGE_PROJECTILE, 40, 0, 2, 0.1, 20, 0);
						}
						if (tick == 3)
							p.getInterfaceManager().setFadingInterface(115);

						if (tick == 6) {
							p.setNextWorldTile(cauldron);
							p.getAppearance().transformIntoNPC(NULL_NPC);
						}

						if (tick == 7)
							p.getInterfaceManager().setFadingInterface(170);

						if (tick == WITCH_DIALOGUE1)
							p.startConversation(new Conversation(p) {
								{
									addNPC(WITCH, HeadE.ANGRY, "Where has Greldo got to with that magic cabbage!");
									addNext(() -> {
										tick++;
									});
									create();
								}
							});

						if (tick == KNIGHT_CAPTAIN_DIALOGUE1)
							p.startConversation(new Conversation(p) {
								{
									addNPC(BLACK_KNIGHT_CAPTAIN, HeadE.SKEPTICAL_THINKING, "What's that noise?");
									addNPC(WITCH, HeadE.AMAZED_MILD, "Hopefully Greldo with that cabbage... yes look here it co....NOOOOOoooo!");
									addNext(() -> {
										for (NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId()))
											if (npc.getId() == WITCH || npc.getId() == BLACK_KNIGHT_CAPTAIN)
												npc.faceObject(cauldron);
										tick++;
									});
									create();
								}
							});

						if (tick == 14)
							World.sendProjectile(new WorldTile(3030, 3507, 0), cauldron, CABBAGE_PROJECTILE, 150, 0, 0, 0.1, 0, 0, proj -> {
								World.sendSpotAnim(p, new SpotAnim(CAULDRON_EXPLOSION_GFX), new WorldTile(p.getX(), p.getY(), p.getPlane()));
							});

						if (tick == POTION_RUINED) {
							p.getVars().setVarBit(CAULDRON_STATUS_VAR, 1);
							p.startConversation(new Conversation(p) {
								{
									addNPC(WITCH, HeadE.AMAZED_MILD, "My potion!");
									addNPC(BLACK_KNIGHT_CAPTAIN, HeadE.SCARED, "Oh boy, this doesn't look good!");
									addNPC(BLACK_CAT, HeadE.CAT_CALM_TALK, "Meow!");
									addNext(() -> {
										tick++;
									});
									for (NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId()))
										if (npc.getId() == WITCH)
											npc.setNextAnimation(new Animation(CRY));
									create();
								}
							});
						}

						if (tick == 22)
							p.getInterfaceManager().setFadingInterface(115);

						if (tick == 25) {
							p.setNextWorldTile(tileBeforeCutscene);
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

		}
	};

	public static LoginHandler onLogin = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if (e.getPlayer().getQuestManager().getStage(Quest.BLACK_KNIGHTS_FORTRESS) >= RUINED_CAULDRON)
				e.getPlayer().getVars().setVarBit(CAULDRON_STATUS_VAR, 1);
		}
	};

}
