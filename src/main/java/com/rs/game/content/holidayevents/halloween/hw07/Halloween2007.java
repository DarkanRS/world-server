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
package com.rs.game.content.holidayevents.halloween.hw07;

import com.rs.cache.loaders.ObjectType;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.World;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;
import com.rs.utils.spawns.ObjectSpawn;
import com.rs.utils.spawns.ObjectSpawns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@PluginEventHandler
public class Halloween2007 {

	public static String STAGE_KEY = "hw2024";
	public static boolean ENABLED = false;

	public static Tile START_LOCATION = Tile.of(1697, 4814, 0);

	public static final int GRIM_DIARY = 11780;
	public static final int GRIM_ROBE = 11781;
	public static final int GRIM_WILL = 11782;
	public static final int HUMAN_BONES = 11783;
	public static final int SERVANT_SKULL = 11784;
	public static final int HOURGLASS = 11785;
	public static final int SCYTHE_SHARPENER = 11786;
	public static final int HUMAN_EYE = 11787;
	public static final int VOD_POTION = 11788;

	public static final int[] ALL_ITEMS = { 11780, 11781, 11782, 11783, 11784, 11785, 11786, 11787, 11788 };

	private static Animation GARGOYLE_ANIM = new Animation(7285);

	private static Animation WEB_PASS_ANIM = new Animation(7280);
	private static Animation WEB_FAIL_ANIM = new Animation(7281);
	private static int WEB_PASS_PANIM = 7275;
	private static Animation WEB_FAIL_PANIM = new Animation(7276);

	private static Animation TAKE_ITEM = new Animation(833);

	public static int[] DEAD_END_WEBS = { 27955946, 28005096, 27661029, 27775726, 27726573, 27628265 };
	public static int[][] SPIDER_PATHS = {
			{ 27906795, 27939561, 27988711, 28005093 },
			{ 27595495, 27824877, 27759341, 27742955, 27644648, 27710184 },
			{ 27824877, 27857635, 27759341, 27742955, 27824870, 27759336 },
			{ 27693798, 27890410, 27710179, 27710184, 27759336 },
			{ 27890410, 27857635, 27824870 },
			{ 27808489, 27824877, 27955940, 27923173 },
			{ 27693798, 27808489, 27710179, 27808496, 27775726, 27710184, 27759336 },
			{ 27808496, 27775726, 27759341, 27742955, 27955940, 27923173, 27759336 },
	};

	private static Map<Integer, Integer> SPRINGBOARD_PAIRS = new HashMap<>();
	private static final int[] PITFALL_LOCS = { 26743532, 26743537, 26743539, 26759918, 26776300, 26776301, 26776306, 26792688, 26825456, 26825459, 26841838, 26858224, 26874611, 26890989, 26890993, 26923756, 26923759, 26923761, 26923763 };
	private static Set<Integer> PITFALLS = new HashSet<>();

	static {
		mapSpringboard(Tile.toInt(1637, 4817, 0), Tile.toInt(1637, 4820, 0));
		mapSpringboard(Tile.toInt(1630, 4824, 0), Tile.toInt(1633, 4824, 0));
		mapSpringboard(Tile.toInt(1627, 4819, 0), Tile.toInt(1630, 4819, 0));
		SPRINGBOARD_PAIRS.put(Tile.toInt(1624, 4822, 0), Tile.toInt(1624, 4828, 0));
		for (int pitfall : PITFALL_LOCS)
			PITFALLS.add(pitfall);
	}

	public static boolean isPitfall(int x, int y) {
		return PITFALLS.contains(Tile.toInt(x, y, 0));
	}

	private static void mapSpringboard(int t1, int t2) {
		SPRINGBOARD_PAIRS.put(t1, t2);
		SPRINGBOARD_PAIRS.put(t2, t1);
	}

	@ServerStartupEvent(Priority.FILE_IO)
	public static void loadPortal() {
		if (ENABLED)
			ObjectSpawns.add(new ObjectSpawn(31845, 10, 0, Tile.of(3210, 3425, 0), "Portal to enter Death's House."));
	}

	public static ItemClickHandler handleGrimDiaryRead = new ItemClickHandler(new Object[] { Halloween2007.GRIM_DIARY }, e -> e.getPlayer().openBook(new GrimsDiary()));

	public static ObjectClickHandler handleEnter = new ObjectClickHandler(new Object[] { 31845 }, e -> {
		if (!ENABLED)
			return;

		if (e.getPlayer().getI(Halloween2007.STAGE_KEY) >= 10) {
			e.getPlayer().sendMessage("You've no need to return to the Grim Reaper's house.");
			return;
		}
		e.getPlayer().startConversation(new Dialogue()
				.addPlayer(HeadE.SCARED, "I can't believe I'm doing this.. Oh well, here goes nothing!")
				.addNext(new Dialogue(() -> {
					e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
				})));
	});

	public static ObjectClickHandler handleExit = new ObjectClickHandler(new Object[] { 27254 }, e -> {
		e.getPlayer().getControllerManager().forceStop();
		e.getPlayer().useStairs(Tile.of(3211, 3424, 0));
	});

	public static ObjectClickHandler handleUpStairs = new ObjectClickHandler(new Object[] { 27242 }, e -> {
		e.getPlayer().useStairs(Tile.of(1639, 4835, 0));
		if (e.getPlayer().resizeable())
			e.getPlayer().getPackets().sendRunScript(2582, 2858, 0, 0); //Set bloom to false for upstairs resizeable
	});

	public static ObjectClickHandler handleDownStairs = new ObjectClickHandler(new Object[] { 27243 }, e -> e.getPlayer().useStairs(Tile.of(1703, 4826, 0)));

	public static ObjectClickHandler handleDoorway1 = new ObjectClickHandler(new Object[] { 27276 }, Tile.of(1705, 4817, 0), e -> {
		if (e.getPlayer().getI(Halloween2007.STAGE_KEY) >= 1)
			e.getPlayer().startConversation(new Dialogue()
					.addNPC(6389, HeadE.CAT_CALM_TALK2, "We're watching you..")
					.addNext(() -> {
						handlePassGargoyleEntry(e.getPlayer(), e.getObject());
						if (e.getPlayer().getI(Halloween2007.STAGE_KEY) == 1)
							e.getPlayer().save(Halloween2007.STAGE_KEY, 2);
					}));
		else
			handleDenyGargoyleEntry(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleSpiderWebs = new ObjectClickHandler(new Object[] { 27266 }, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (ctrl.checkWeb(e.getObject().getTile().getTileHash()))
			passWeb(e.getPlayer(), e.getObject());
		else
			failWeb(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler pickUpSkull = new ObjectClickHandler(new Object[] { 27279 }, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		e.getPlayer().lock();
		WorldTasks.schedule(new Task() {
			int stage = 0;
			@Override
			public void run() {
				if (stage == 0)
					e.getPlayer().faceObject(e.getObject());
				else if (stage == 1)
					e.getPlayer().setNextAnimation(new Animation(7270));
				else if (stage == 2)
					e.getPlayer().getInventory().addItem(SERVANT_SKULL, 1);
				else if (stage == 4) {
					e.getPlayer().sendMessage("As you pick up the skull, you feel hear the webs shift.");
					ctrl.randomizePath();
					e.getPlayer().unlock();
					stop();
				}
				stage++;
			}
		}, 0, 0);
	});

	public static ObjectClickHandler handleDoorway2 = new ObjectClickHandler(new Object[] { 27276 }, Tile.of(1701, 4832, 0), e -> {
		if (e.getPlayer().getI(Halloween2007.STAGE_KEY) >= 2) {
			if (e.getPlayer().getI(Halloween2007.STAGE_KEY) == 2)
				e.getPlayer().startConversation(new Dialogue()
						.addNPC(8867, HeadE.CALM_TALK, "You have found my garden I see.")
						.addPlayer(HeadE.NERVOUS, "Woah. You made my jump. How does your voice carry so far?")
						.addNPC(8867, HeadE.CALM_TALK, "That is not of your concern. Whilst in my domain you shall never be far from my gaze.")
						.addPlayer(HeadE.CONFUSED, "Okay... So, what exactly do you want me to do out here?")
						.addNPC(8867, HeadE.CALM_TALK, "My last servant died in here. His skull still remains and you must bring it out to prevent my next servant from getting the wrong impression.")
						.addPlayer(HeadE.NERVOUS, "And I suppose your servant was killed by some huge man-eating spider?")
						.addNPC(8867, HeadE.CALM_TALK, "That is not your business, mortal. Anyway, the webs you see are from a nest of very small spiders. Some webs you can pass, some you cannot. You will only need to use your bare hands to break through.")
						.addPlayer(HeadE.NERVOUS, "Sorry I asked. I'll go find the skull and bring it back out of the garden.")
						.addNPC(8867, HeadE.CALM_TALK, "Yes, and do not try teleporting out of the garden with the skull. The skull will remain behind and you'll have to find it again.")
						.addNext(() -> {
							handlePassGargoyleEntry(e.getPlayer(), e.getObject());
							e.getPlayer().save(Halloween2007.STAGE_KEY, 3);
						}));
			else if (e.getPlayer().getI(Halloween2007.STAGE_KEY) == 3 && e.getPlayer().getInventory().containsItem(11784))
				e.getPlayer().startConversation(new Dialogue()
						.addPlayer(HeadE.SAD_SNIFFLE, "Woah, what'd I do? Oh no. The skull's gone.")
						.addNPC(8867, HeadE.CALM_TALK, "Do not concern yourself. I had instructed the gargoyles to take it from you.")
						.addPlayer(HeadE.SAD_MILD, "You could have let me know! I almost had a heart attack.")
						.addNPC(8867, HeadE.CALM_TALK, "Please proceed to the westernmost room for your next task.")
						.addPlayer(HeadE.CALM_TALK, "You don't waste time, do you? So, the room that looks a bit like your lounge? I'm on my way.")
						.addNext(() -> {
							e.getPlayer().getInventory().deleteItem(11784, 28);
							e.getPlayer().save(Halloween2007.STAGE_KEY, 4);
							handlePassGargoyleEntry(e.getPlayer(), e.getObject());
						}));
			else
				handlePassGargoyleEntry(e.getPlayer(), e.getObject());
		} else
			handleDenyGargoyleEntry(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleNothingObjects = new ObjectClickHandler(new Object[] { 27246, 27247, 27248, 27250, 27260 }, e -> searchItem(e.getPlayer(), -1, "You find nothing of interest."));
	public static ObjectClickHandler handleTable = new ObjectClickHandler(new Object[] { 27245 }, e -> searchItem(e.getPlayer(), GRIM_DIARY, "You find a diary on the table. This should give some clues as to where items within the room should go."));
	public static ObjectClickHandler handleCabinet = new ObjectClickHandler(new Object[] { 27246 }, Tile.of(1687, 4820, 0), e -> searchItem(e.getPlayer(), GRIM_ROBE, "You found the Grim Reaper's robes."));
	public static ObjectClickHandler handleBookcase = new ObjectClickHandler(new Object[] { 27249 }, e -> searchItem(e.getPlayer(), SCYTHE_SHARPENER, "You found a scythe sharpener."));
	public static ObjectClickHandler handleShelf = new ObjectClickHandler(new Object[] { 27261 }, e -> searchItem(e.getPlayer(), HOURGLASS, "You found an hourglass."));
	public static ObjectClickHandler handleChest = new ObjectClickHandler(new Object[] { 27255 }, e -> searchItem(e.getPlayer(), GRIM_WILL, "You found someone's Last Will and Testament."));
	public static ObjectClickHandler handleFireplace = new ObjectClickHandler(new Object[] { 27251 }, e -> searchItem(e.getPlayer(), VOD_POTION, "You found a 'Voice of Doom' potion."));

	public static ItemOnObjectHandler handleSharpenerCabinet = new ItemOnObjectHandler(new Object[] { 27246 }, new Tile[] { Tile.of(1687, 4820, 0) }, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (e.getItem().getId() == SCYTHE_SHARPENER) {
			searchItem(e.getPlayer(), -1, "You recall the entry in the diary, '...put the sharpener back in the cabinet...' and place the scythe sharpener in the cabinet.");
			ctrl.returnItem(SCYTHE_SHARPENER);
		} else
			e.getPlayer().startConversation(new Dialogue().addSimple("You don't recall the diary mentioning that going in there."));
	});

	public static ItemOnObjectHandler handleVoDBookcase = new ItemOnObjectHandler(new Object[] { 27249 }, null, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (e.getItem().getId() == VOD_POTION) {
			searchItem(e.getPlayer(), -1, "You recall the entry in the diary, '...I found my old 'Voice of Doom' potion amongst some books...' and place the potion on the bookcase.");
			ctrl.returnItem(VOD_POTION);
		} else
			e.getPlayer().startConversation(new Dialogue().addSimple("You don't recall the diary mentioning that going in there."));
	});

	public static ItemOnObjectHandler handleEyeShelf = new ItemOnObjectHandler(new Object[] { 27261 }, null, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (e.getItem().getId() == HUMAN_EYE) {
			searchItem(e.getPlayer(), -1, "You recall the entry in the diary, '...I put the eye back on the shelf...' and place the eye on the shelf.");
			ctrl.returnItem(HUMAN_EYE);
		} else
			e.getPlayer().startConversation(new Dialogue().addSimple("You don't recall the diary mentioning that going in there."));
	});

	public static ItemOnObjectHandler handleBonesChest = new ItemOnObjectHandler(new Object[] { 27255 }, null, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (e.getItem().getId() == HUMAN_BONES) {
			searchItem(e.getPlayer(), -1, "You recall the entry in the diary, '...decided to lock them up in the chest...' and place the bones in the chest.");
			ctrl.returnItem(HUMAN_BONES);
		} else
			e.getPlayer().startConversation(new Dialogue().addSimple("You don't recall the diary mentioning that going in there."));
	});

	public static ItemOnObjectHandler handleRobeFireplace = new ItemOnObjectHandler(new Object[] { 27251 }, null, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (e.getItem().getId() == GRIM_ROBE) {
			searchItem(e.getPlayer(), -1, "You recall the entry in the diary, '...decided to throw them in the fireplace...' and place the robes in the fireplace.");
			ctrl.returnItem(GRIM_ROBE);
		} else
			e.getPlayer().startConversation(new Dialogue().addSimple("You don't recall the diary mentioning that going in there."));
	});

	public static ObjectClickHandler handleCouch = new ObjectClickHandler(new Object[] { 27252 }, e -> {
		e.getPlayer().sendOptionDialogue("Where do you want to search?", ops -> {
			ops.add("Under the sofa", () -> {
				e.getPlayer().lock();
				WorldTasks.scheduleTimer(stage -> {
					if (stage == 0)
						e.getPlayer().setNextAnimation(new Animation(7271));
					else if (stage == 1)
						World.sendSpotAnim(e.getPlayer().transform(-1, 0, 0), new SpotAnim(1244, 0, 0, 2));
					else if (stage == 4) {
						e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.NERVOUS, "That wasn't such a good idea."));
						e.getPlayer().unlock();
						return false;
					}
					return true;
				});
			});
			ops.add("Under the cushions", () -> searchItem(e.getPlayer(), HUMAN_EYE, "You found someone's eye."));
		});
	});

	public static ItemOnObjectHandler handleWillCouch = new ItemOnObjectHandler(new Object[] { 27252 }, null, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (e.getItem().getId() == GRIM_WILL) {
			searchItem(e.getPlayer(), -1, "You recall the entry in the diary, '...have to sit on that for a while...' and place the Last Will and Testament under the sofa cushions.");
			ctrl.returnItem(GRIM_WILL);
		} else
			e.getPlayer().startConversation(new Dialogue().addSimple("You don't recall the diary mentioning that going in there."));
	});

	public static ObjectClickHandler handleFishTank = new ObjectClickHandler(new Object[] { 27253 }, e -> {
		e.getPlayer().sendOptionDialogue("Where do you want to search?", ops -> {
			ops.add("To the left", () -> {
				e.getPlayer().lock();
				WorldTasks.scheduleTimer(stage -> {
					if (stage == 0)
						e.getPlayer().setNextAnimation(new Animation(7271));
					else if (stage == 4) {
						e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.NERVOUS, "That wasn't such a good idea."));
						e.getPlayer().unlock();
						return false;
					}
					return true;
				});
			});
			ops.add("To the right", () -> searchItem(e.getPlayer(), HUMAN_BONES, "You found some bones. They look decidedly human."));
		});
	});

	public static ItemOnObjectHandler handleHourglassFishTank = new ItemOnObjectHandler(new Object[] { 27253 }, null, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (e.getItem().getId() == HOURGLASS) {
			searchItem(e.getPlayer(), -1, "You recall the entry in the diary, '...hourglass today so have added that to the fishtank...' and place the hourglass in the fishtank.");
			ctrl.returnItem(HOURGLASS);
		} else
			e.getPlayer().startConversation(new Dialogue().addSimple("You don't recall the diary mentioning that going in there."));
	});

	public static ObjectClickHandler handleDoorway3 = new ObjectClickHandler(new Object[] { 27276 }, Tile.of(1694, 4820, 0), e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (e.getPlayer().getI(Halloween2007.STAGE_KEY) >= 4) {
			if (e.getPlayer().getI(Halloween2007.STAGE_KEY) == 4)
				e.getPlayer().startConversation(new Dialogue()
						.addNPC(8867, HeadE.CALM_TALK, "You made it.")
						.addPlayer(HeadE.CONFUSED, "You sound surprised.")
						.addNPC(8867, HeadE.CALM_TALK, "You wouldn't be the first, or last, to die in this house.")
						.addPlayer(HeadE.NO_EXPRESSION, "I'm so pleased. So, to the task at hand. What should I be doing in here?")
						.addNPC(8867, HeadE.CALM_TALK, "If you search around the room you'll find various items. Put them back where they belong. My diary is on the table - read this for some clues, but tell anyone what you read and you die.")
						.addPlayer(HeadE.NERVOUS, "*Gulp* I shall have a look.")
						.addNext(() -> {
							handlePassGargoyleEntry(e.getPlayer(), e.getObject());
							e.getPlayer().save(Halloween2007.STAGE_KEY, 5);
						}));
			else if (e.getPlayer().getI(Halloween2007.STAGE_KEY) == 5) {
				if (ctrl.isItemsCorrect())
					e.getPlayer().startConversation(new Dialogue()
							.addNPC(8867, HeadE.CALM_TALK, "Looks like you've returned everything to its proper location.")
							.addPlayer(HeadE.HAPPY_TALKING, "Woo hoo!")
							.addNPC(8867, HeadE.CALM_TALK, "You've not finished yet, mortal. Please proceed upstairs and enter the room you find there.")
							.addPlayer(HeadE.CALM_TALK, "Upstairs it is.")
							.addNext(() -> {
								e.getPlayer().save(Halloween2007.STAGE_KEY, 6);
								handlePassGargoyleEntry(e.getPlayer(), e.getObject());
							}));
				else {
					if (e.getPlayer().getX() > e.getObject().getX()) {
						handlePassGargoyleEntry(e.getPlayer(), e.getObject());
						return;
					}
					e.getPlayer().sendOptionDialogue("If you leave, the items will be returned to where you found them. Leave?", ops -> {
						ops.add("Yes", () -> {
							ctrl.removeItems();
							ctrl.resetReturnedItems();
						});
						ops.add("No, I'll finish first.");
					});
				}
			} else
				handlePassGargoyleEntry(e.getPlayer(), e.getObject());
		} else
			handleDenyGargoyleEntry(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleDoorway4 = new ObjectClickHandler(new Object[] { 27276 }, Tile.of(1641, 4829, 0), e -> {
		if (e.getPlayer().getI(Halloween2007.STAGE_KEY) >= 6) {
			if (e.getPlayer().getI(Halloween2007.STAGE_KEY) == 6)
				e.getPlayer().startConversation(new Dialogue()
						.addNPC(8867, HeadE.CALM_TALK, "The last room.")
						.addPlayer(HeadE.DIZZY, "Good job. I'm not sure my heart can take much more.")
						.addNPC(8867, HeadE.CALM_TALK, "As you can see, ahead of you is a course of a rather vicious design. I use it to keep my servants on their toes. Test it, please.")
						.addPlayer(HeadE.NERVOUS, "Test?")
						.addNPC(8867, HeadE.CALM_TALK, "This course hasn't been used for some time. I wish to make sure it all still works.")
						.addPlayer(HeadE.NERVOUS, "You've got to be kidding me.")
						.addNext(() -> {
							handlePassGargoyleEntry(e.getPlayer(), e.getObject());
							e.getPlayer().save(Halloween2007.STAGE_KEY, 7);
						}));
			else
				handlePassGargoyleEntry(e.getPlayer(), e.getObject());
		} else
			handleDenyGargoyleEntry(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleSlide = new ObjectClickHandler(new Object[] { 27218 }, e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		e.getPlayer().lock();
		WorldTasks.scheduleTimer(stage -> {
			if (stage == 0) {
				e.getPlayer().faceObject(e.getObject());
				e.getPlayer().addWalkSteps(e.getPlayer().transform(0, -1, 0), 1, false);
				Tile camTile = Tile.of(1638, 4827, 0);
				e.getPlayer().getPackets().sendCameraPos(camTile, 2000);
				e.getPlayer().getPackets().sendCameraLook(e.getPlayer().transform(-2, 0, 0), 2000);
			} else if (stage == 1)
				e.getPlayer().setNextAnimation(new Animation(7274));
			else if (stage == 9) {
				e.getPlayer().setNextTile(e.getPlayer().transform(0, -1, 0));
				e.getPlayer().forceMove(Tile.of(1642, 4819, 0), 1, 60, () -> {
					e.getPlayer().getPackets().sendResetCamera();
					ctrl.setRodeSlide(true);
				});
				return false;
			}
			return true;
		});
	});

	public static ObjectClickHandler handleSpringboards = new ObjectClickHandler(new Object[] { 27278 }, e -> {
		Tile toTile = Tile.of(SPRINGBOARD_PAIRS.get(e.getObject().getTile().getTileHash()));
		boolean toSlime = e.getObject().getTile().isAt(1624, 4822);
		e.getPlayer().lock();
		WorldTasks.scheduleTimer(stage -> {
			if (stage == 0)
				e.getPlayer().addWalkSteps(e.getObject().getTile(), 1, false);
			else if (stage == 1) {
				World.sendObjectAnimation(e.getObject(), new Animation(7268));
				if (toSlime) {
					e.getPlayer().getAppearance().setBAS(616);
					e.getPlayer().blockRun();
				}
				e.getPlayer().forceMove(toTile, toSlime ? 7269 : 7268, 1, 30);
				return false;
			}
			return true;
		});
	});

	public static ObjectClickHandler handleExitRamp = new ObjectClickHandler(new Object[] { 27211 }, e -> {
		e.getPlayer().lock();
		WorldTasks.scheduleTimer(stage -> {
			if (stage == 0)
				e.getPlayer().faceObject(e.getObject());
			else if (stage == 1) {
				e.getPlayer().forceMove(e.getPlayer().transform(0, 2, 0), 7273, 1, 150, () -> e.getPlayer().unblockRun());
				e.getPlayer().getAppearance().setBAS(-1);
				return false;
			}
			return true;
		});
	});

	public static ObjectClickHandler handleDoorway5 = new ObjectClickHandler(new Object[] { 27276 }, Tile.of(1645, 4848, 0), e -> {
		if (e.getPlayer().getControllerManager().getController() == null)
			e.getPlayer().getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) e.getPlayer().getControllerManager().getController();
		if (e.getPlayer().getI(Halloween2007.STAGE_KEY) >= 7)
			if (e.getPlayer().getI(Halloween2007.STAGE_KEY) == 7) {
				if (!ctrl.isRodeSlide()) {
					e.getPlayer().startConversation(new Dialogue().addNPC(6389, HeadE.CAT_CALM_TALK2, "You didn't test the slide!"));
					handleDenyGargoyleEntry(e.getPlayer(), e.getObject());
				} else
					handleDenyGargoyleEntry(e.getPlayer(), e.getObject(), () -> {
						e.getPlayer().setNextTile(Tile.of(1641, 4828, 0));
						e.getPlayer().save(Halloween2007.STAGE_KEY, 8);
						e.getPlayer().startConversation(new Dialogue()
								.addPlayer(HeadE.CONFUSED, "Huh? What happened there?")
								.addNPC(8867, HeadE.CALM_TALK, "Again.")
								.addPlayer(HeadE.CONFUSED, "What?")
								.addNPC(8867, HeadE.CALM_TALK, "One more time around, just to be sure all is in order."));
					});
			} else if (!ctrl.isRodeSlide()) {
				e.getPlayer().startConversation(new Dialogue().addNPC(6389, HeadE.CAT_CALM_TALK2, "You didn't test the slide!"));
				handleDenyGargoyleEntry(e.getPlayer(), e.getObject());
			} else
				handleDenyGargoyleEntry(e.getPlayer(), e.getObject(), () -> {
					e.getPlayer().setNextTile(Tile.of(1641, 4840, 0));
					e.getPlayer().save(Halloween2007.STAGE_KEY, 9);
					e.getPlayer().startConversation(new Dialogue()
							.addNPC(8867, HeadE.CALM_TALK, "That is sufficient.")
							.addPlayer(HeadE.SLEEPING, "Phew.")
							.addNPC(8867, HeadE.CALM_TALK, "Come and speak with me downstairs.")
							.addPlayer(HeadE.CHEERFUL, "I'll be right down."));
				});
	});

	public static void searchItem(Player player, int itemId, String findText) {
		if (player.getControllerManager().getController() == null)
			player.getControllerManager().startController(new Halloween2007Controller());
		Halloween2007Controller ctrl = (Halloween2007Controller) player.getControllerManager().getController();
		player.lock();
		WorldTasks.scheduleTimer(stage -> {
			if (stage == 0)
				player.setNextAnimation(TAKE_ITEM);
			else if (stage == 2) {
				player.unlock();
				if (itemId == -1 || player.getInventory().containsItem(itemId) || ctrl.isItemReturned(itemId))
					player.startConversation(new Dialogue().addSimple(findText));
				else {
					player.getInventory().addItem(itemId, 1);
					player.startConversation(new Dialogue().addItem(itemId, findText));
				}
				return false;
			}
			return true;
		});
	}

	public static void handlePassGargoyleEntry(Player player, GameObject object) {
		boolean horiz = object.getRotation() % 2 != 0;
		player.passThrough(object.getTile().transform(horiz ? player.getX() > object.getX() ? -1 : 1 : 0, horiz ? 0 : player.getY() > object.getY() ? -1 : 1, 0));
		player.sendMessage("You safely pass the gargoyles' judgement.");
	}

	public static void handleDenyGargoyleEntry(Player player, GameObject object) {
		handleDenyGargoyleEntry(player, object, null);
	}

	public static void handleDenyGargoyleEntry(Player player, GameObject object, Runnable postDeath) {
		boolean xOff = object.getRotation() % 2 == 0;
		World.sendObjectAnimation(World.getObject(object.getTile().transform(xOff ? -1 : 0, xOff ? 0 : -1, 0), ObjectType.GROUND_DECORATION), GARGOYLE_ANIM);
		World.sendObjectAnimation(World.getObject(object.getTile().transform(xOff ? 1 : 0, xOff ? 0 : 1, 0), ObjectType.GROUND_DECORATION), GARGOYLE_ANIM);
		if (postDeath == null)
			player.startConversation(new Dialogue()
					.addNPC(6389, HeadE.CAT_CALM_TALK2, "Who said you could come in here? See the Grim Reaper if you don't know where to go.")
					.addPlayer(HeadE.NERVOUS, "Oops."));
		player.lock();
		WorldTasks.schedule(new Task() {
			int stage = 0;
			@Override
			public void run() {
				if (stage == 0)
					player.faceObject(object);
				else if (stage == 2) {
					int x = 0, y = 0;
					switch(object.getRotation()) {
					case 0:
					case 1:
						x = -1;
						y = -1;
						break;
					case 2:
						x = -1;
						y = 0;
						break;
					case 3:
						x = 0;
						y = -1;
						break;
					}
					GameObject lightning = new GameObject(27277, ObjectType.SCENERY_INTERACT, object.getRotation(), object.getTile().transform(x, y, 0));
					World.spawnObjectTemporary(lightning, Ticks.fromSeconds(3));
				} else if (stage == 3) {
					player.setNextAnimation(new Animation(3170));
					player.setNextSpotAnim(new SpotAnim(560));
					player.fakeHit(new Hit(player.getHitpoints(), HitLook.TRUE_DAMAGE));
				} else if (stage == 4) {
					player.unlock();
					if (postDeath == null)
						player.sendDeath(null);
					else
						postDeath.run();
					stop();
				}
				stage++;
			}
		}, 0, 0);
	}

	public static void passWeb(Player player, GameObject object) {
		Tile toTile = object.getRotation() % 2 != 0 ? player.transform(player.getX() >= object.getX() ? -2 : 2, 0, 0) : player.transform(0, player.getY() >= object.getY() ? -2 : 2, 0);
		player.lock();
		WorldTasks.schedule(new Task() {
			int stage = 0;
			@Override
			public void run() {
				if (stage == 0)
					player.faceObject(object);
				else if (stage == 1) {
					player.forceMove(toTile, WEB_PASS_PANIM, 1, 120);
					object.animate(WEB_PASS_ANIM);
					stop();
				}
				stage++;
			}
		}, 0, 0);
	}

	public static void failWeb(Player player, GameObject object) {
		player.lock();
		WorldTasks.schedule(new Task() {
			int stage = 0;
			@Override
			public void run() {
				if (stage == 0)
					player.faceObject(object);
				else if (stage == 1) {
					player.setNextAnimation(WEB_FAIL_PANIM);
					object.animate(WEB_FAIL_ANIM);
				} else if (stage == 8) {
					player.sendMessage("You cannot pass through this particular web - try another.");
					player.unlock();
					stop();
				}
				stage++;
			}
		}, 0, 0);
	}
}
