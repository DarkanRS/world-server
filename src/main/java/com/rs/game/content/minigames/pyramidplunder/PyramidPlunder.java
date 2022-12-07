package com.rs.game.content.minigames.pyramidplunder;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class PyramidPlunder {

	public static final WorldTile EXIT_TILE = WorldTile.of(3288, 2801, 0);
	public static final Integer[] DOORS = { 16539, 16540, 16541, 16542 };
	private static final int PHARAOHS_SCEPTRE = 9044;
	private static final int SCEPTRE_OF_THE_GODS = 21536;
	private static final int[] BLACK_IBIS = { 21532, 21533, 21534, 21535 };

	public static ObjectClickHandler handlePyramidExits = new ObjectClickHandler(new Object[] { 16458 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			PyramidPlunderController ctrl = e.getPlayer().getControllerManager().getController(PyramidPlunderController.class);
			if (ctrl == null) {
				e.getPlayer().setNextWorldTile(EXIT_TILE);
				e.getPlayer().sendMessage("No idea how you got in here. But get out bad boy.");
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addOptions("Would you like to exit?", new Options() {
				@Override
				public void create() {
					option("Yes", new Dialogue().addNext(() -> ctrl.exitMinigame()));
					option("No", new Dialogue());
				}
			}));
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
				e.getPlayer().unlock();
				e.getPlayer().sendMessage("The urn is empty.");
				return;
			}
			switch(e.getOption()) {
			case "Check for Snakes" -> {
				e.getPlayer().unlock();
				e.getPlayer().getSkills().addXp(Constants.THIEVING, getRoomBaseXP(ctrl.getCurrentRoom()));
				ctrl.updateObject(e.getObject(), 2);
				return;
			}
			case "Charm Snake" -> {
				e.getPlayer().unlock();
				if (e.getPlayer().getInventory().containsItem(4605, 1)) {
					e.getPlayer().setNextAnimation(new Animation(1877));
					ctrl.updateObject(e.getObject(), 3);
				} else
					e.getPlayer().sendMessage("You need a snake charm flute for that!");
			}
			case "Search" -> {
				WorldTasks.scheduleTimer(i -> {
					switch(i) {
						case 1 -> {
							e.getPlayer().faceObject(e.getObject());
							e.getPlayer().setNextAnimation(new Animation(4340));
						}
						case 3 -> {
							if (rollUrnSuccess(e.getPlayer(), ctrl.getCurrentRoom(), varbitValue)) {
								e.getPlayer().setNextAnimation(new Animation(4342));
								e.getPlayer().getSkills().addXp(Constants.THIEVING, getRoomBaseXP(ctrl.getCurrentRoom())* (varbitValue == 0 ? 3 : 2));
								ctrl.updateObject(e.getObject(), 1);
								loot(e.getPlayer(), "pp_urn", ctrl.getCurrentRoom());
							} else {
								e.getPlayer().setNextAnimation(new Animation(4341));
								e.getPlayer().applyHit(new Hit(e.getPlayer().getSkills().getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE));
								e.getPlayer().getPoison().makePoisoned(30);
								e.getPlayer().forceTalk("Ow!");
							}
						}
						case 5 -> {
							e.getPlayer().unlock();
							e.getPlayer().processReceivedHits();
							return false;
						}
					}
					return true;
				});
			}
			}
		}
	};

	public static ObjectClickHandler handleGrandChest = new ObjectClickHandler(new Object[] { 16537 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			PyramidPlunderController ctrl = e.getPlayer().getControllerManager().getController(PyramidPlunderController.class);
			if (ctrl == null) {
				e.getPlayer().setNextWorldTile(EXIT_TILE);
				e.getPlayer().sendMessage("No idea how you got in here. But get out bad boy.");
				return;
			}
			if(Utils.randomInclusive(0, 4) == 1) {
				OwnedNPC swarm = new OwnedNPC(e.getPlayer(), 2001, WorldTile.of(e.getPlayer().getTile()), false);
				swarm.setTarget(e.getPlayer());
			}
			ctrl.updateObject(e.getObject(), 1);
			e.getPlayer().getSkills().addXp(Constants.THIEVING, getRoomBaseXP(ctrl.getCurrentRoom()) * 2);
			loot(e.getPlayer(), "pp_sarcophagus", ctrl.getCurrentRoom());
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
			int lvlReq = (ctrl.getCurrentRoom()+1) * 10 + 1;
			if (e.getPlayer().getSkills().getLevel(Constants.STRENGTH) < lvlReq) {
				e.getPlayer().sendMessage("You need " + lvlReq + " strength...");
				return;
			}
			if (e.getOption().equals("Open")) {
				e.getPlayer().lock();
				boolean success = rollSarcophagusSuccess(e.getPlayer(), ctrl.getCurrentRoom());
				WorldTasks.scheduleTimer(i -> {
					switch(i) {
						case 0 -> e.getPlayer().faceObject(e.getObject());
						case 1 -> e.getPlayer().setNextAnimation(new Animation(success ? 4345 : 4344));
						case 3 -> ctrl.updateObject(e.getObject(), success ? 1 : 0);
						case 6 -> {
							if (success) {
								if (Utils.randomInclusive(0, 4) == 1) {
									OwnedNPC mummy = new OwnedNPC(e.getPlayer(), 2015, WorldTile.of(e.getPlayer().getTile()), false);
									mummy.setTarget(e.getPlayer());
								}
								e.getPlayer().getSkills().addXp(Constants.STRENGTH, getRoomBaseXP(ctrl.getCurrentRoom()));
								ctrl.updateObject(e.getObject(), 2);
								loot(e.getPlayer(), "pp_sarcophagus", ctrl.getCurrentRoom());
							} else {
								e.getPlayer().applyHit(new Hit(e.getPlayer().getSkills().getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE));
								e.getPlayer().forceTalk("Ow!");
							}
						}
						case 8 -> {
							e.getPlayer().unlock();
							return false;
						}
					}
					return true;
				});
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

			if (e.getOption().equals("Open")) {
				if (Utils.randomInclusive(0, 4) == 1) {
					OwnedNPC mummy = new OwnedNPC(e.getPlayer(), 2015, WorldTile.of(e.getPlayer().getTile()), false);
					mummy.setTarget(e.getPlayer());
				}
				ctrl.updateObject(e.getObject(), 1);
				e.getPlayer().getSkills().addXp(Skills.RUNECRAFTING, getRoomBaseXP(ctrl.getCurrentRoom()));
				loot(e.getPlayer(), "pp_sarcophagus_engraved", ctrl.getCurrentRoom());
			} else if (e.getOption().equals("Search")) {
				e.getPlayer().sendMessage("The sarcophagus has already been looted.");
			}
		}
	};

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
				e.getPlayer().lock();
				WorldTasks.scheduleTimer(i -> {
					switch(i) {
						case 1 -> {
							e.getPlayer().faceObject(e.getObject());
							e.getPlayer().setNextAnimation(new Animation(832));
						}
						case 3 -> {
							if (Utils.skillSuccess(e.getPlayer().getSkills().getLevel(Skills.THIEVING), e.getPlayer().getInventory().containsOneItem(1523, 11682) ? 1.3 : 1.0, 190, 190)) {
								e.getPlayer().getSkills().addXp(Constants.THIEVING, getRoomBaseXP(ctrl.getCurrentRoom()) * 2);
								ctrl.updateObject(e.getObject(), 1);
							} else {
								e.getPlayer().sendMessage("You fail to pick the lock.");
								e.getPlayer().unlock();
								return false;
							}
						}
						case 5 -> {
							if (e.getObjectId() == ctrl.getCorrectDoor())
								ctrl.nextRoom();
							else
								e.getPlayer().sendMessage("The door leads nowhere.");
						}
						case 6 -> {
							e.getPlayer().unlock();
							return false;
						}
					}
					return true;
				});
			} else if (e.getOption().equals("Enter")) {
				if (e.getObjectId() == ctrl.getCorrectDoor()) {
					ctrl.nextRoom();
					return;
				} else
					e.getPlayer().sendMessage("You've already checked this door and found it's not the right way.");
			}
		}
	};

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

	private static boolean rollUrnSuccess(Player player, int room, int varbitValue) {
		double boost = player.getAuraManager().getThievingMul();
		if (varbitValue == 2)
			boost += 0.15;
		else if (varbitValue == 3)
			boost += 0.40;
		int chance1 = switch(room) {
			case 1 -> 73;
			case 2 -> 55;
			case 3 -> 32;
			case 4 -> -21;
			case 5 -> -72;
			case 6 -> -150;
			case 7 -> -300;
			default -> -900;
		};
		int chance99 = switch(room) {
			case 1 -> 213;
			case 2 -> 207;
			case 3 -> 203;
			case 4 -> 197;
			case 5 -> 193;
			case 6 -> 187;
			case 7 -> 183;
			default -> 177;
		};
		return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), boost, chance1, chance99, varbitValue != 0 ? 235 : 213);
	}

	private static boolean rollSarcophagusSuccess(Player player, int room) {
		int chance1 = switch(room) {
			case 1 -> 62;
			case 2 -> 30;
			case 3 -> 20;
			case 4 -> 0;
			case 5 -> -50;
			case 6 -> -90;
			case 7 -> -120;
			default -> -200;
		};
		int chance99 = switch(room) {
			case 1 -> 256;
			case 2 -> 226;
			case 3 -> 196;
			case 4 -> 166;
			case 5 -> 136;
			case 6 -> 106;
			case 7 -> 76;
			default -> 46;
		};
		return Utils.skillSuccess(player.getSkills().getLevel(Constants.STRENGTH), 1.0, chance1, chance99, 188);
	}

	private static boolean loot(Player player, String lootTable, int room) {
		String thingLooted = "urns";
		if (lootTable.contains("chest"))
			thingLooted = "grand chests";
		else if (lootTable.contains("sarcophagus"))
			thingLooted = "sarcophagi";
		else if (lootTable.contains("engraved"))
			thingLooted = "engraved sarcophagi";
		player.incrementCount("Pyramid Plunder " + thingLooted + " looted", 1);
		if (rollForBlackIbis(player))
			return false;
		if (lootTable.contains("sarcophagus")) {
			int chance = switch(room) {
				case 1 -> 3500;
				case 2 -> 2250;
				case 3 -> 1250;
				case 4 -> 750;
				default -> 650;
			};
			if (Utils.random(chance * (player.getEquipment().wearingRingOfWealth() ? 0.97 : 1)) == 0) {
				player.getInventory().addItemDrop(PHARAOHS_SCEPTRE, 1);
				World.broadcastLoot(player.getDisplayName() + " has just received a Pharaoh's sceptre from Pyramid Plunder!");
				return true;
			}
		}
		if (!player.containsItem(SCEPTRE_OF_THE_GODS) && lootTable.contains("engraved")) {
			if (Utils.random(650 * (player.getEquipment().wearingRingOfWealth() ? 0.97 : 1)) == 0) {
				player.getInventory().addItemDrop(new Item(SCEPTRE_OF_THE_GODS).addMetaData("teleCharges", 10));
				World.broadcastLoot(player.getDisplayName() + " has just received a Sceptre of the Gods from Pyramid Plunder!");
				return true;
			}
		}
		Item[] drops = DropTable.calculateDrops(DropSets.getDropSet(lootTable + room));
		for (Item item : drops) {
			if (item == null)
				continue;
			player.getInventory().addItemDrop(item);
		}
		return false;
	}

	public static boolean rollForBlackIbis(Player player) {
		if (player.containsItem(BLACK_IBIS[3]))
			return false;
		int rate = player.getEquipment().getWeaponId() == SCEPTRE_OF_THE_GODS ? 1150 : 2300;
		if (player.getEquipment().wearingRingOfWealth())
			rate *= 0.97;
		if (Utils.random(rate) == 0) {
			int drop = -1;
			for (int peices : BLACK_IBIS) {
				if (!player.containsItem(peices)) {
					drop = peices;
					break;
				}
			}
			player.getInventory().addItemDrop(drop, 1);
			World.broadcastLoot(player.getDisplayName() + " has just received a peice of Black Ibis from Pyramid Plunder!");
			return true;
		}
		return false;
	}

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

    /**
     * Use nearby tiles surrounding the trap object to choose a direction for the far tiles.
     * Both near and far point in the same directions per index N, E, S, W
     * If a nearby tile is a trap that is the direction we must go as a player...
     * @param e
     */
	private static void passTrap(ObjectClickEvent e) {
		WorldTile[] nearbyTiles = { e.getObject().getTile().transform(0, 1), e.getObject().getTile().transform(1, 0), e.getObject().getTile().transform(0, -1), e.getObject().getTile().transform(-1, 0) };
		WorldTile[] farTiles = { e.getPlayer().transform(0, 3), e.getPlayer().transform(3, 0), e.getPlayer().transform(0, -3), e.getPlayer().transform(-3, 0) };
		int tileIdx = 0;
		for(WorldTile nearbyTile : nearbyTiles) {
			GameObject obj2 = World.getObject(nearbyTile, ObjectType.SCENERY_INTERACT);
			if (obj2 != null && obj2.getId() == 16517)
				break;
			tileIdx++;
		}
		final WorldTile toTile = farTiles[tileIdx];
		e.getPlayer().lock();
		boolean hasRun = e.getPlayer().getRun();
		WorldTasks.scheduleTimer(i -> {
			switch(i) {
				case 1 -> {
					e.getPlayer().faceObject(e.getObject());
					e.getPlayer().setNextAnimation(new Animation(832));
				}
				case 3 -> {
					e.getPlayer().setRun(false);
					e.getPlayer().addWalkSteps(toTile, 4, false);
				}
				case 5 -> {
					e.getPlayer().setRun(hasRun);
					e.getPlayer().getSkills().addXp(Skills.THIEVING, 10);
				}
				case 6 -> {
					e.getPlayer().unlock();
					return false;
				}
			}
			return true;
		});
	}

	final static WorldTile[] rightHandSpearTraps = {
			WorldTile.of(1927, 4473, 0), WorldTile.of(1928, 4473, 0),
			WorldTile.of(1930, 4452, 0), WorldTile.of(1930, 4453, 0),
			WorldTile.of(1955, 4474, 0), WorldTile.of(1954, 4474, 0),
			WorldTile.of(1961, 4444, 0), WorldTile.of(1961, 4445, 0),
			WorldTile.of(1927, 4428, 0), WorldTile.of(1926, 4428, 0),
			WorldTile.of(1944, 4425, 0), WorldTile.of(1945, 4425, 0),
			WorldTile.of(1974, 4424, 0), WorldTile.of(1975, 4424, 0)
	};
	final static WorldTile[] leftHandSpearTraps = {
			WorldTile.of(1927, 4472, 0), WorldTile.of(1928, 4472, 0),
			WorldTile.of(1931, 4452, 0), WorldTile.of(1931, 4453, 0),
			WorldTile.of(1955, 4473, 0), WorldTile.of(1954, 4473, 0),
			WorldTile.of(1962, 4444, 0), WorldTile.of(1962, 4445, 0),
			WorldTile.of(1927, 4427, 0), WorldTile.of(1926, 4427, 0),
			WorldTile.of(1944, 4424, 0), WorldTile.of(1945, 4424, 0),
			WorldTile.of(1974, 4423, 0), WorldTile.of(1975, 4423, 0)
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
				if(trapTile.matches(obj.getTile()) || (obj.getX() - trapDir.getDx() == trapTile.getX() && obj.getY() - trapDir.getDy() == trapTile.getY())) {
					obj.animate(new Animation(463));
					break;
				}
			}
	}

	private static void hitPlayer(PlayerStepEvent e) {
		Player p = e.getPlayer();
		p.applyHit(new Hit(30, Hit.HitLook.POISON_DAMAGE));
		p.getPoison().makePoisoned(20);
		Direction oppositeDir = Direction.rotateClockwise(e.getStep().getDir(), 4);//180 degree turn
		int dX = oppositeDir.getDx();
		int dY = oppositeDir.getDy();
		WorldTile prevTile = WorldTile.of(e.getTile().getX() + dX, e.getTile().getY() + dY, e.getTile().getPlane());
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
