package com.rs.game.player.content.minigames.pyramidplunder;

import java.util.Arrays;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.ForceMovement;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.others.OwnedNPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.controllers.PyramidPlunderController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.DropSets;
import com.rs.utils.Ticks;
import com.rs.utils.drop.DropTable;

//Scepter of the gods = 1/650 (1/480 with ring of wealth) from engraved sarcophagus
//Black ibis = 1/2300 from everything increased to 1/1150 with scepter of the gods
//black ibis also reduced drop rate with ring of wealth 3%

//pharaohs sceptre
//1/650 floor 5+
//1/750 floor 4
//1/1250 floor 3
//1/2250 floor 2
//1/3500 floor 1

/** sarcophagus/chest success rates 99 thieving
Object:	Sarcophagus:		Chest:
Room 1:	73.33%				83.33% / 75% @ 75 thieving
Room 2:	73.33%				83.33%
Room 3:	73.33%				83.33%
Room 4:	46.66%				83.33%
Room 5:	40%					83.33%
Room 6:	33.33%				83.33%
Room 7:	26.66%				83.33%
Room 8:	6.66%				76.66%
 */

@PluginEventHandler
public class PyramidPlunder {

	public static final WorldTile EXIT_TILE = new WorldTile(3288, 2801, 0);
	public static final Integer[] DOORS = { 16539, 16540, 16541, 16542 };

	public static ObjectClickHandler handlePyramidExits = new ObjectClickHandler(new Object[] { 16458 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addOptions("Would you like to exit?", new Options() {
						@Override
						public void create() {
							option("Yes", new Dialogue()
									.addNext(()-> {
										teleAndResetRoom(e.getPlayer(), new WorldTile(3288, 2801, 0));
										e.getPlayer().getControllerManager().forceStop();
									}));
							option("No", new Dialogue());
						}
					});
					create();
				}
			});
		}
	};

	public static ObjectClickHandler handlePlunderUrns = new ObjectClickHandler(new Object[] { 16518, 16519, 16520, 16521, 16522, 16523, 16524, 16525, 16526, 16527, 16528, 16529, 16530, 16531, 16532 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			PyramidPlunderController ctrl = e.getPlayer().getControllerManager().getController(PyramidPlunderController.class);
			if (ctrl == null) {
				e.getPlayer().setNextWorldTile(EXIT_TILE);
				e.getPlayer().sendMessage("No idea how you got in here. But get out bad boy.");
				return;
			}
			e.getPlayer().lock();
			int varbitValue = e.getPlayer().getVars().getVarBit(e.getObject().getDefinitions().varpBit);
			if (varbitValue == 1) {
				e.getPlayer().sendMessage("The urn is empty.");
				return;
			}
			WorldTasks.scheduleTimer(i -> {
				switch(i) {
				case 1 -> e.getPlayer().faceObject(e.getObject());
				case 2 -> e.getPlayer().setNextAnimation(new Animation(4340));
				case 3 -> {
					switch(e.getOption()) {
					case "Search" -> {
						//if (varbitValue == 2)
							//snake bite?
						e.getPlayer().getSkills().addXp(Constants.THIEVING, getRoomBaseXP(ctrl.getCurrentRoom() * (varbitValue == 0 ? 3 : 2)));
					}
					case "Check for Snakes" -> {
						e.getPlayer().setNextAnimation(new Animation(4340));
						e.getPlayer().getSkills().addXp(Constants.THIEVING, getRoomBaseXP(ctrl.getCurrentRoom()));
						//if has snake
						//ctrl.updateUrn(e.getObject(), 2)
						//else
						//ctrl.updateUrn(e.getObject(), 1)
					}
					case "Charm Snake" -> {
						if (e.getPlayer().getInventory().containsItem(4605, 1))
							ctrl.updateUrn(e.getObject(), 3); //TODO anim
						else
							e.getPlayer().sendMessage("You need a snake charm flute for that!");
					}
					}
				}
				case 5 -> e.getPlayer().unlock();
				}
				return true;
			});
		}
	};

	private static boolean checkUrn(ObjectClickEvent e) {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		int varNum = obj.getDefinitions().varpBit;
		if (p.getVars().getVarBit(varNum) == 0) { //Untouched urn
			if (e.getOption().equalsIgnoreCase("check for snakes")) {
				p.lock(1);
				p.setNextAnimation(new Animation(4340));
				p.getVars().setVarBit(varNum, 2);
				giveCheckUrnXP(p);
			} else { //search option
				p.lock(3);
				WorldTasks.scheduleTimer(tick -> {
					if (tick == 0)
						p.setNextAnimation(new Animation(4340));//check urn anim
					if (tick == 1)
						if (urnSnakeBiteChance(p)) {
							p.setNextAnimation(new Animation(4341));//snake bite anim
							p.applyHit(new Hit(p.getSkills().getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE));
							p.forceTalk("Ow!");
							p.getVars().setVarBit(varNum, 2);
						} else {
							p.setNextAnimation(new Animation(4342));//loot urn anim
							urnLoot(p);
							giveSearchBlindUrnXP(p);
							p.getVars().setVarBit(varNum, 1);
						}
					if (tick == 3)
						return false;
					return true;
				});
			}
		} else if (p.getVars().getVarBit(varNum) == 1)//empty urn
			return;
		else if (p.getVars().getVarBit(varNum) == 2) {//Snake active
			if (e.getOption().equalsIgnoreCase("charm snake")) {
				if (p.getInventory().containsItem(4605, 1))
					p.getVars().setVarBit(varNum, 3);
				else
					p.sendMessage("You need a snake charm flute for that!");
			} else if (urnSnakeBiteChance(p)) {
				p.applyHit(new Hit(p.getSkills().getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE));
				p.forceTalk("Ow!");
			} else {
				urnLoot(p);
				giveSearchCheckedUrnXP(p);
				p.getVars().setVarBit(varNum, 1);
			}
		} else if (p.getVars().getVarBit(varNum) == 3)//snake charmed
			if (urnSnakeBiteChance(p, true)) {
				p.applyHit(new Hit(p.getSkills().getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE));
				p.forceTalk("Ow!");
			} else {
				urnLoot(p);
				giveSearchCheckedUrnXP(p);
				p.getVars().setVarBit(varNum, 1);
			}
	}

	private static int getRoomBaseXP(int roomId) {		
		return switch(roomId) {
		case 1 -> 20;
		case 2 -> 30;
		case 3 -> 50;
		case 4 -> 70;
		case 5 -> 100;
		case 6 -> 150;
		case 7 -> 225;
		case 8 -> 275;
		default -> 0;
		};
	}

	private static boolean urnSnakeBiteChance(Player p) {
		return urnSnakeBiteChance(p, false);
	}

	private static boolean urnSnakeBiteChance(Player p, boolean hasCharm) {
		double ratio = (p.getSkills().getLevel(Constants.THIEVING))/100.0*(hasCharm ? 6.0 : 3.0); //level 1 is ~0, level 99 is ~3
		if(ratio < 1.0)
			ratio = 1.0;
		if (isIn21Room(p))
			return Utils.random(0.0, 1.0) < (0.25/ratio);
		else if (isIn31Room(p))
			return Utils.random(0.0, 1.0) < (0.3/ratio);
		else if (isIn41Room(p))
			return Utils.random(0.0, 1.0) < (0.4/ratio);
		else if (isIn51Room(p))
			return Utils.random(0.0, 1.0) < (0.5/ratio);
		else if (isIn61Room(p))
			return Utils.random(0.0, 1.0) < (0.6/ratio);
		else if (isIn71Room(p))
			return Utils.random(0.0, 1.0) < (0.7/ratio);
		else if (isIn81Room(p))
			return Utils.random(0.0, 1.0) < (0.8/ratio);
		else if (isIn91Room(p))
			return Utils.random(0.0, 1.0) < (0.9/ratio);
		else
			return true;
	}

	public static ObjectClickHandler handleGrandChest = new ObjectClickHandler(new Object[] { 16537 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			PyramidPlunderController ctrl = e.getPlayer().getControllerManager().getController(PyramidPlunderController.class);
			if (ctrl == null) {
				e.getPlayer().setNextWorldTile(EXIT_TILE);
				e.getPlayer().sendMessage("No idea how you got in here. But get out bad boy.");
				return;
			}
			e.getPlayer().getVars().setVarBit(2363, 1);
			if(Utils.randomInclusive(0, 4) == 1) {
				OwnedNPC swarm = new OwnedNPC(e.getPlayer(), 2001, e.getPlayer(), false);
				swarm.setTarget(e.getPlayer());
			}
			//TODO loot for chests
			e.getPlayer().getSkills().addXp(Constants.THIEVING, getRoomBaseXP(ctrl.getCurrentRoom()) * 2);
		}
	};

	public static ObjectClickHandler handleSarcophagus = new ObjectClickHandler(new Object[] { 16547 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			PyramidPlunderController ctrl = e.getPlayer().getControllerManager().getController(PyramidPlunderController.class);
			if (ctrl == null) {
				e.getPlayer().setNextWorldTile(EXIT_TILE);
				e.getPlayer().sendMessage("No idea how you got in here. But get out bad boy.");
				return;
			}
			switch(e.getPlayer().getVars().getVarBit(2362)) {
			case 0 -> {
				//TODO success rate/animation/delay
				int lvlReq = (ctrl.getCurrentRoom()+1) * 10 + 1;
				if (e.getPlayer().getSkills().getLevel(Constants.STRENGTH) < lvlReq) {
					e.getPlayer().sendMessage("You need " + lvlReq + " strength...");
					return;
				}
				e.getPlayer().getVars().setVarBit(2362, 1);
				e.getPlayer().getSkills().addXp(Constants.STRENGTH, getRoomBaseXP(ctrl.getCurrentRoom()));
				if (Utils.randomInclusive(0, 4) == 1) {
					OwnedNPC mummy = new OwnedNPC(e.getPlayer(), 2015, e.getPlayer(), false);
					mummy.setTarget(e.getPlayer());
				}
			}
			case 1 -> {
				//TODO success rate/animation/delay
				e.getPlayer().getVars().setVarBit(2362, 2);
				//TODO roll for pharaoh's scepter and kick the player out if they get it
				//else do this:
				Item[] drops = DropTable.calculateDrops(DropSets.getDropSet("pp_sarcophagus_" + ctrl.getCurrentRoom()));
				for (Item item : drops) {
					if (item == null)
						continue;
					e.getPlayer().getInventory().addItemDrop(item);
				}
			}
			}
		}
	};

	public static ObjectClickHandler handleEngravedSarcophagus = new ObjectClickHandler(new Object[] { 59795 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			PyramidPlunderController ctrl = e.getPlayer().getControllerManager().getController(PyramidPlunderController.class);
			if (ctrl == null) {
				e.getPlayer().setNextWorldTile(EXIT_TILE);
				e.getPlayer().sendMessage("No idea how you got in here. But get out bad boy.");
				return;
			}
			if (p.getVars().getVarBit(3422) == 0) {
				if (p.getSkills().getLevel(Constants.STRENGTH) < 91 || p.getSkills().getLevel(Skills.RUNECRAFTING) < 75) {
					p.sendMessage("You need 91 Strength and 75 Runecrafting to open this sarcophagus.");
					return;
				}
				p.getVars().setVarBit(3422, 1);
				if (Utils.randomInclusive(0, 4) == 1) {
					OwnedNPC mummy = new OwnedNPC(p, 2015, p, false);
					mummy.setTarget(p);
				}
			} else if (p.getVars().getVarBit(3422) == 1) {
				p.getVars().setVarBit(3422, 2);
				p.getInventory().addItem(rollItem(7, 1.2, true), true);
			}
		}
	};

	private static final int PHARAOH_SCEPTRE = 9044; //Only golden chest and sarcophagus incremental chanves

	public static ObjectClickHandler handlePyramidTombDoors = new ObjectClickHandler((Object[]) DOORS) {
		@Override
		public void handle(ObjectClickEvent e) {
			PyramidPlunderController ctrl = e.getPlayer().getControllerManager().getController(PyramidPlunderController.class);
			if (ctrl == null) {
				e.getPlayer().setNextWorldTile(EXIT_TILE);
				e.getPlayer().sendMessage("No idea how you got in here. But get out bad boy.");
				return;
			}
			if (e.getOption().equals("Pick-lock")) {
				
			} else if (e.getOption().equals("Enter")) {
				if (e.getObjectId() == ctrl.getCorrectDoor()) {
					ctrl.nextRoom();
					return;
				} else
					e.getPlayer().sendMessage("This doesn't look like the right door.");
			}
		}
	};

	public static ObjectClickHandler handleSpearTrap = new ObjectClickHandler(new Object[] { 16517 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			PyramidPlunderController ctrl = e.getPlayer().getControllerManager().getController(PyramidPlunderController.class);
			if (ctrl == null) {
				e.getPlayer().setNextWorldTile(EXIT_TILE);
				e.getPlayer().sendMessage("No idea how you got in here. But get out bad boy.");
				return;
			}
			int lvlReq = (ctrl.getCurrentRoom()+1) * 10 + 1;
			if (e.getPlayer().getSkills().getLevel(Constants.THIEVING) < lvlReq) {
				e.getPlayer().sendMessage("You need a thieving level of " + lvlReq + " or higher...");
				return;
			}
			passTrap(e);
		}
	};

	private static void passTrap(ObjectClickEvent e) {
		Player p = e.getPlayer();
		WorldTile tile = e.getObject();
		WorldTile[] nearbyTiles = {
				new WorldTile(tile.getX(), tile.getY()+1, tile.getPlane()), new WorldTile(tile.getX()+1, tile.getY(), tile.getPlane()),
				new WorldTile(tile.getX(), tile.getY()-1, tile.getPlane()), new WorldTile(tile.getX()-1, tile.getY(), tile.getPlane())
		};
		WorldTile[] farTiles = {
				new WorldTile(p.getX(), p.getY()+3, p.getPlane()), new WorldTile(p.getX()+3, p.getY(), p.getPlane()),
				new WorldTile(p.getX(), p.getY()-3, p.getPlane()), new WorldTile(p.getX()-3, p.getY(), p.getPlane())
		};
		int i = 0;
		for(WorldTile nearbyTile : nearbyTiles) {
			GameObject obj2 = World.getObject(nearbyTile, ObjectType.SCENERY_INTERACT);
			if (obj2 != null && obj2.getId() == 16517) {
				p.lock(3);
				boolean hasRun = p.getRun();
				p.setRun(false);
				p.addWalkSteps(farTiles[i], 4, false);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						p.setRun(hasRun);
						p.getSkills().addXp(Skills.THIEVING, 10);
					}
				}, 1);
				return;
			}
			i++;
		}
	}

	final static WorldTile[] rightHandSpearTraps = {
			new WorldTile(1927, 4473, 0), new WorldTile(1928, 4473, 0),
			new WorldTile(1930, 4452, 0), new WorldTile(1930, 4453, 0),
			new WorldTile(1955, 4474, 0), new WorldTile(1954, 4474, 0),
			new WorldTile(1961, 4444, 0), new WorldTile(1961, 4445, 0),
			new WorldTile(1927, 4428, 0), new WorldTile(1926, 4428, 0),
			new WorldTile(1944, 4425, 0), new WorldTile(1945, 4425, 0),
			new WorldTile(1974, 4424, 0), new WorldTile(1975, 4424, 0)
	};
	final static WorldTile[] leftHandSpearTraps = {
			new WorldTile(1927, 4472, 0), new WorldTile(1928, 4472, 0),
			new WorldTile(1931, 4452, 0), new WorldTile(1931, 4453, 0),
			new WorldTile(1955, 4473, 0), new WorldTile(1954, 4473, 0),
			new WorldTile(1962, 4444, 0), new WorldTile(1962, 4445, 0),
			new WorldTile(1927, 4427, 0), new WorldTile(1926, 4427, 0),
			new WorldTile(1944, 4424, 0), new WorldTile(1945, 4424, 0),
			new WorldTile(1974, 4423, 0), new WorldTile(1975, 4423, 0)
	};

	public static PlayerStepHandler handleRightHandSpearTraps = new PlayerStepHandler(rightHandSpearTraps) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getPlayer().isLocked())
				return;
			Direction rightHandTrap = Direction.rotateClockwise(e.getStep().getDir(), 2);//90 degree turn
			activateTrap(e, rightHandTrap);
			hitPlayer(e);
		}
	};

	public static PlayerStepHandler handleLeftHandSpearTraps = new PlayerStepHandler(leftHandSpearTraps) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getPlayer().isLocked())
				return;
			Direction leftHandTrap = Direction.rotateClockwise(e.getStep().getDir(), 6);//270 degree turn
			activateTrap(e, leftHandTrap);
			hitPlayer(e);
		}
	};

	private static void activateTrap(PlayerStepEvent e, Direction trapDir) {
		WorldTile trapTile = e.getTile();
		for(GameObject obj : World.getRegion(trapTile.getRegionId()).getObjects())
			if(obj.getId() == 16517) {
				if(trapTile.matches(obj) || (obj.getX() - trapDir.getDx() == trapTile.getX() && obj.getY() - trapDir.getDy() == trapTile.getY())) {
					obj.animate(new Animation(463));
					break;
				}
			}
	}

	private static void hitPlayer(PlayerStepEvent e) {
		Player p = e.getPlayer();
		p.applyHit(new Hit(30, Hit.HitLook.POISON_DAMAGE));
		Direction oppositeDir = Direction.rotateClockwise(e.getStep().getDir(), 4);//180 degree turn
		int dX = oppositeDir.getDx();
		int dY = oppositeDir.getDy();
		WorldTile prevTile = new WorldTile(e.getTile().getX() + dX, e.getTile().getY() + dY, e.getTile().getPlane());
		p.lock(3);
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;
			@Override
			public void run() {
				if(ticks == 0) {
					p.setNextAnimation(new Animation(1832));
					p.setNextForceMovement(new ForceMovement(prevTile, 1, e.getStep().getDir()));
				}
				else if (ticks == 1) {
					p.setNextWorldTile(prevTile);
					p.forceTalk("Ouch!");
				}
				else if (ticks == 2)
					stop();
				ticks++;
			}
		}, 0, 1);
	}

}
