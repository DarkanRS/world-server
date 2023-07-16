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
package com.rs.game.content.skills.dungeoneering;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.World.DropMethod;
import com.rs.game.content.combat.AttackType;
import com.rs.game.content.skills.cooking.Foods;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.KeyDoors;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.SkillDoors;
import com.rs.game.content.skills.dungeoneering.dialogues.DungeonClimbLadder;
import com.rs.game.content.skills.dungeoneering.dialogues.DungeonExit;
import com.rs.game.content.skills.dungeoneering.dialogues.DungeonLeave;
import com.rs.game.content.skills.dungeoneering.npcs.*;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.blink.Blink;
import com.rs.game.content.skills.dungeoneering.npcs.misc.DungeonFishSpot;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.content.skills.dungeoneering.rooms.puzzles.PoltergeistRoom;
import com.rs.game.content.skills.dungeoneering.skills.*;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringFarming.Harvest;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringMining.DungeoneeringRocks;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringRCD.DungRCSet;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.magic.Rune;
import com.rs.game.content.skills.magic.RuneSet;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.skills.summoning.Summoning;
import com.rs.game.content.skills.util.Category;
import com.rs.game.content.skills.util.CreateActionD;
import com.rs.game.content.skills.util.CreationActionD;
import com.rs.game.content.skills.util.ReqItem;
import com.rs.game.content.world.unorganized_dialogue.SmugglerD;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.*;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;

public class DungeonController extends Controller {

	private transient DungeonManager dungeon;
	private Tile gatestone;
	@SuppressWarnings("unused")
	private int deaths;
	private int voteStage;
	private boolean killedBossWithLessThan10HP;
	private int damageReceived;
	private int meleeDamage, rangeDamage, mageDamage;
	private int healedDamage;
	private boolean showBar;

	public DungeonController(DungeonManager manager) {
		dungeon = manager;
	}

	@Override
	public void start() {
		showDeaths();
		refreshDeaths();
		player.setForceMultiArea(true);
	}

	@Override
	public Genre getGenre() {
		return Music.getGenreByName("Ambient Dungeoneering");
	}


	@Override
	public boolean playAmbientOnControllerRegionEnter() {
		return false;
	}

	/**
	 * No unlocks, we can play and unlock room music in the Room class in a later commit...
	 * @return
	 */
	@Override
	public boolean playAmbientStrictlyBackgroundMusic() {
		return true;
	}

	public void showDeaths() {
		player.getInterfaceManager().sendOverlay(945);
	}

	public void showBar() {
		player.getPackets().setIFHidden(945, 2, !showBar);
	}

	private void hideBar() {
		showBar(false, null);
	}

	public void showBar(boolean show, String name) {
		if (showBar == show)
			return;
		showBar = show;
		showBar();
		if (show)
			player.getPackets().sendVarcString(315, name);
	}

	public void sendBarPercentage(int percentage) {
		player.getPackets().sendVarc(1233, percentage * 2);
	}

	public void reset() {
		//deaths = 0;
		voteStage = 0;
		gatestone = null;
		killedBossWithLessThan10HP = false;
		damageReceived = 0;
		meleeDamage = 0;
		rangeDamage = 0;
		mageDamage = 0;
		healedDamage = 0;
		refreshDeaths();
		showDeaths();
		hideBar();
		player.getAppearance().setBAS(-1);
	}

	@Override
	public boolean canMove(Direction dir) {
		VisibleRoom vr = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
		Tile to = Tile.of(player.getX() + dir.getDx(), player.getY() + dir.getDy(), 0);
		if(vr != null && !vr.canMove(player, to))
			return false;

		Room room = dungeon.getRoom(dungeon.getCurrentRoomReference(player.getTile()));
		if (room != null)
			if (room.getRoom() == DungeonUtils.getBossRoomWithChunk(DungeonConstants.FROZEN_FLOORS, 26, 624)) {
				if (!player.isCantWalk() && World.getObjectWithType(Tile.of(player.getX() + dir.getDx(), player.getY() + dir.getDy(), 0), ObjectType.GROUND_DECORATION) == null) {
					player.getAppearance().setBAS(1429);
					player.setRun(true);
					player.setCantWalk(true);
				}
				if (player.isCantWalk()) {
					Tile nextStep = Tile.of(player.getX() + dir.getDx() * 2, player.getY() + dir.getDy() * 2, 0);
					NPC boss = getNPC(player, "Plane-freezer Lakhrahnaz");
					boolean collides = boss != null && WorldUtil.collides(nextStep.getX(), nextStep.getY(), player.getSize(), boss.getX(), boss.getY(), boss.getSize());
					player.resetWalkSteps();
					GameObject object = World.getObjectWithType(Tile.of(nextStep.getX(), nextStep.getY(), 0), ObjectType.GROUND_DECORATION);
					if (collides || ((object != null && (object.getId() == 49331 || object.getId() == 49333)) || !player.addWalkSteps(nextStep.getX(), nextStep.getY(), 1))) {
						player.setCantWalk(false);
						player.getAppearance().setBAS(-1);
					}
				}
			}
		return dungeon != null && !dungeon.isAtRewardsScreen();
	}

	public int getHealedDamage() {
		return healedDamage;
	}

	@Override
	public void processIncomingHit(Hit hit) {
		if (player.getDungManager().getActivePerk() == KinshipPerk.TANK && player.getEquipment().getShieldId() != -1 && ItemDefinitions.getDefs(player.getEquipment().getShieldId()).name.toLowerCase().contains("shield")) {
			double perc = 0.94 - (player.getDungManager().getKinshipTier(KinshipPerk.TANK) * 0.01);
			hit.setDamage((int) (hit.getDamage() * perc));
		}
		damageReceived += hit.getDamage();
	}

	@Override
	public void processOutgoingHit(Hit hit, Entity target) {
		if (hit.getDamage() <= 0)
			return;
		if (hit.getLook() == HitLook.MELEE_DAMAGE)
			meleeDamage += hit.getDamage();
		else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
			if (player.getDungManager().getActivePerk() == KinshipPerk.KEEN_EYE && player.getCombatDefinitions().getAttackStyle().getAttackType() == AttackType.ACCURATE) {
				int procChance = (int) (40 + (player.getDungManager().getKinshipTier(KinshipPerk.KEEN_EYE) * 6.5));
				if (Utils.random(100) < procChance)
					if (target instanceof NPC npc)
						npc.lowerDefense(1, 0.0);
			}
			rangeDamage += hit.getDamage();
		} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
			mageDamage += hit.getDamage();
			if (player.getDungManager().getActivePerk() == KinshipPerk.BLAZER && hit.getData("combatSpell") != null) {
				if ((hit.getData("blazerBleed") != null) || (hit.getDamage() < 10))
					return;
				int procChance = 5 * player.getDungManager().getKinshipTier(KinshipPerk.BLAZER);
				int damage = hit.getDamage() / 10;
				if (Utils.random(100) < procChance)
					for (int i = 1;i <= 5;i++)
						WorldTasks.delay(2*i, () -> {
							target.applyHit(new Hit(player, damage, HitLook.MAGIC_DAMAGE).setData("blazerBleed", true));
						});
			}
		}
	}

	public int getDamageReceived() {
		return damageReceived;
	}

	public int getMeleeDamage() {
		return meleeDamage;
	}

	public int getRangeDamage() {
		return rangeDamage;
	}

	public int getMageDamage() {
		return mageDamage;
	}

	public int getDamage() {
		return meleeDamage + rangeDamage + mageDamage;
	}

	@Override
	public void sendInterfaces() {
		if (dungeon != null && dungeon.isAtRewardsScreen())
			return;
		showDeaths();
	}

	@Override
	public void processNPCDeath(NPC npc) {
		if (npc instanceof DungeonBoss)
			if (player.getHitpoints() <= 10)
				killedBossWithLessThan10HP = true;
	}

	@Override
	public boolean sendDeath() {
		player.lock(8);
		player.stopAll();
		player.jingle(418);
		if (player.getInventory().containsItem(DungeonConstants.GROUP_GATESTONE, 1)) {
			Tile tile = Tile.of(player.getTile());
			dungeon.setGroupGatestone(Tile.of(player.getTile()));
			World.addGroundItem(new Item(DungeonConstants.GROUP_GATESTONE), tile);
			player.getInventory().deleteItem(DungeonConstants.GROUP_GATESTONE, 1);
			player.sendMessage("Your group gatestone drops to the floor as you die.");
		}
		if (player.getInventory().containsItem(DungeonConstants.GATESTONE, 1)) {
			Tile tile = Tile.of(player.getTile());
			setGatestone(Tile.of(player.getTile()));
			World.addGroundItem(new Item(DungeonConstants.GATESTONE), tile);
			player.getInventory().deleteItem(DungeonConstants.GATESTONE, 1);
			player.sendMessage("Your gatestone drops to the floor as you die.");
		}
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1) {
					player.sendMessage("Oh dear, you are dead!");
					if (dungeon != null)
						for (Player p2 : dungeon.getParty().getTeam()) {
							if (p2 == player)
								continue;
							p2.sendMessage(player.getDisplayName() + " has died.");
						}
				} else if (loop == 3) {
					player.resetReceivedHits();
					if (dungeon != null && dungeon.getParty().getTeam().contains(player)) {
						if (dungeon.isAtBossRoom(player.getTile(), 26, 672, true)) {
							NPC npc = getNPC(player, "Yk'Lagor the Thunderous");
							if (npc != null)
								npc.setNextForceTalk(new ForceTalk("Another kill for the Thunderous!"));
							//npc.playSoundEffect(1928);
						}
						Tile startRoom = dungeon.getHomeTile();
						player.setNextTile(startRoom);
						dungeon.playMusic(player, dungeon.getCurrentRoomReference(startRoom));
						increaseDeaths();
						player.reset();
					}
					player.setNextAnimation(new Animation(-1));
					player.getAppearance().setBAS(-1);
					hideBar();
				} else if (loop == 4)
					stop();
				loop++;
			}
		}, 0, 1);
		return false;
	}

	private void refreshDeaths() {
		player.getVars().setVarBit(7554, getDeaths());
	}

	private void increaseDeaths() {
		Integer deaths = dungeon.getPartyDeaths().get(player.getUsername());
		if (deaths == null)
			deaths = 0;
		else if (deaths == 15)
			return;
		dungeon.getPartyDeaths().put(player.getUsername(), deaths + 1);
		refreshDeaths();
	}

	public int getDeaths() {
		if (dungeon == null)
			return 0;
		Integer deaths = dungeon.getPartyDeaths().get(player.getUsername());
		return deaths == null ? 0 : deaths;//deaths;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		if (dungeon == null || !player.getCombatDefinitions().isDungSpellbook() || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;
		if (Utils.getDistance(toTile, dungeon.getHomeTile()) > 500)
			return false;
		return true;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		return false;
	}

	@Override
	public boolean canTakeItem(GroundItem item) {
		for (KeyDoors key : DungeonConstants.KeyDoors.values())
			if (item.getId() == key.getKeyId()) {
				dungeon.setKey(key, true);
				World.removeGroundItem(player, item, false);
				return false;
			}
		if (item.getId() == DungeonConstants.GROUP_GATESTONE) {
			dungeon.setGroupGatestone(null);
			return true;
		}
		if (item.getId() == DungeonConstants.GATESTONE) {
			if (!item.isPrivate()) {
				World.removeGroundItem(player, item);
				return false;
			}
			if (item.getVisibleToId() != player.getUuid()) {
				player.sendMessage("This isn't your gatestone!");
				return false;
			}
			setGatestone(null);
			return true;
		}
		return true;
	}

	private void openSkillDoor(final SkillDoors s, final GameObject object, final Room room, final int floorType) {
		final int index = room.getDoorIndexByRotation(object.getRotation());
		if (index == -1)
			return;
		final Door door = room.getDoor(index);
		if (door == null || door.getType() != DungeonConstants.SKILL_DOOR)
			return;
		if (door.getLevel() > (player.getSkills().getLevel(s.getSkillId()) + player.getInvisibleSkillBoost(s.getSkillId()))) {
			player.sendMessage("You need a " + Constants.SKILL_NAME[s.getSkillId()] + " level of " + door.getLevel() + " to remove this " + object.getDefinitions().getName().toLowerCase() + ".");
			return;
		}
		int openAnim = -1;
		if (s.getSkillId() == Constants.FIREMAKING) {
			if (!player.getInventory().containsOneItem(DungeonConstants.TINDERBOX)) {
				player.sendMessage("You need a tinderbox to do this.");
				return;
			}
		} else if (s.getSkillId() == Constants.MINING) {
			DungPickaxe defs = DungPickaxe.getBest(player);
			if (defs == null) {
				player.sendMessage("You do not have a pickaxe or do not have the required level to use the pickaxe.");
				return;
			}
			openAnim = defs.getAnimation().getIds()[0];
		} else if (s.getSkillId() == Constants.WOODCUTTING) {
			DungHatchet defs = DungHatchet.getHatchet(player);
			if (defs == null) {
				player.sendMessage("You do not have a hatchet or do not have the required level to use the hatchet.");
				return;
			}
			openAnim = defs.getEmoteId();
		}
		final boolean fail = Utils.random(100) <= 10;
		player.lock(3);
		if (s.getOpenAnim() != -1)
			player.setNextAnimation(new Animation(openAnim != -1 ? openAnim : fail && s.getFailAnim() != -1 ? s.getFailAnim() : s.getOpenAnim()));
		if (s.getOpenGfx() != -1 || s.getFailGfx() != -1)
			player.setNextSpotAnim(new SpotAnim(fail ? s.getFailGfx() : s.getOpenGfx()));
		if (s.getOpenObjectAnim() != -1 && !fail)
			World.sendObjectAnimation(object, new Animation(s.getOpenObjectAnim()));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				if (s.getFailAnim() == -1)
					player.setNextAnimation(new Animation(-1));
				if (!fail) {
					if (room.getDoor(index) == null) //means someone else opeenda t same time
						return;
					player.getSkills().addXp(s.getSkillId(), door.getLevel() * 5 + 10);
					room.setDoor(index, null);
					int openId = s.getOpenObject(floorType);
					if (openId == -1)
						World.removeObject(object);
					else
						dungeon.setDoor(dungeon.getCurrentRoomReference(object.getTile()), -1, openId, object.getRotation());
				} else {
					player.sendMessage(s.getFailMessage());
					player.applyHit(new Hit(player, door.getLevel() * 4, HitLook.TRUE_DAMAGE));
					if (room.getDoor(index) == null) //means someone else opeenda t same time
						return;
					if (s.getFailObjectAnim() != -1)
						World.sendObjectAnimation(object, new Animation(s.getFailObjectAnim()));
				}

			}

		}, 2);

	}

	@Override
	public boolean processNPCClick1(final NPC npc) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;
		VisibleRoom vRoom = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
		if (vRoom == null || !vRoom.processNPCClick1(player, npc))
			return false;
		if (npc.getId() == DungeonConstants.FISH_SPOT_NPC_ID) {
			player.faceEntity(npc);
			player.getActionManager().setAction(new DungeoneeringFishing((DungeonFishSpot) npc));
			return false;
		}
		if (npc.getId() == 10023) {
			FrozenAdventurer adventurer = (FrozenAdventurer) npc;
			adventurer.getFrozenPlayer().getAppearance().transformIntoNPC(-1);
			return false;
		}
		if (npc.getId() == DungeonConstants.SMUGGLER) {
			npc.faceEntity(player);
			player.startConversation(new SmugglerD(player, dungeon.getParty().getComplexity()));
			return false;
		} else if (npc.getId() >= 11076 && npc.getId() <= 11085) {
			DungeoneeringTraps.removeTrap(player, (MastyxTrap) npc, dungeon);
			return false;
		} else if (npc.getId() >= 11096 && npc.getId() <= 11105) {
			DungeoneeringTraps.skinMastyx(player, npc);
			return false;
		} else if (npc instanceof DivineSkinweaver skin) {
			skin.talkTo(player);
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick2(final NPC npc) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;
		VisibleRoom room = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
		if ((room == null) || !room.processNPCClick2(player, npc))
			return false;
		if (npc instanceof Familiar familiar) {
			if (player.getFamiliar() != familiar) {
				player.sendMessage("That isn't your familiar.");
				return false;
			}
			if (familiar.getDefinitions().hasOption("Take")) {
				familiar.takeInventory();
				return false;
			}
			return true;
		}
		if (npc.getDefinitions().hasOption("Mark")) {
			if (!dungeon.getParty().isLeader(player)) {
				player.sendMessage("Only your party's leader can mark a target!");
				return false;
			}
			dungeon.setMark(npc, !player.getHintIconsManager().hasHintIcon(6)); //6th slot
			return false;
		}
		if (npc.getId() == DungeonConstants.SMUGGLER) {
			DungeonResourceShop.openResourceShop(player, dungeon.getParty().getComplexity());
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick3(NPC npc) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;
		VisibleRoom room = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
		if ((room == null) || !room.processNPCClick3(player, npc))
			return false;
		return true;
	}

	public static NPC getNPC(Entity entity, int id) {
		for (NPC npc : World.getNPCsInChunkRange(entity.getChunkId(), 4)) {
			if (npc.getId() == id)
				return npc;
		}
		return null;
	}

	public static NPC getNPC(Entity entity, String name) {
		for (NPC npc : World.getNPCsInChunkRange(entity.getChunkId(), 4)) {
			if (npc.getName().equals(name))
				return npc;
		}
		return null;
	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;
		Room room = dungeon.getRoom(dungeon.getCurrentRoomReference(player.getTile()));
		VisibleRoom vr = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
		if (vr == null || !vr.processObjectClick1(player, object))
			return false;
		int floorType = DungeonUtils.getFloorType(dungeon.getParty().getFloor());
		for (SkillDoors s : SkillDoors.values())
			if (s.getClosedObject(floorType) == object.getId()) {
				openSkillDoor(s, object, room, floorType);
				return false;
			}
		if (object.getId() >= 54439 && object.getId() <= 54456 && object.getDefinitions().containsOption(0, "Cleanse")) {
			@SuppressWarnings("deprecation")
			NPC boss = dungeon.getTemporaryBoss();//getNPC(player, 11708);
			if (boss == null || !(boss instanceof Gravecreeper))
				return false;
			return ((Gravecreeper) boss).cleanseTomb(player, object);
		}
		if (object.getId() == 49265) {
			NPC boss = getNPC(player, "Night-gazer Khighorahk");
			if (boss == null) {
				player.sendMessage("You don't need to light anymore.");
				return false;
			}
			((NightGazerKhighorahk) boss).lightPillar(player, object);
			return false;
		}
		if (object.getId() >= 49274 && object.getId() <= 49279) {
			NPC boss = getNPC(player, "Stomp");
			if (boss != null)
				((Stomp) boss).chargeLodeStone(player, (object.getId() & 0x1));
			return false;
		}
		if (object.getId() == 49268) {
			DungPickaxe defs = DungPickaxe.getBest(player);
			if (defs == null) {
				player.sendMessage("You do not have a pickaxe or do not have the required level to use the pickaxe.");
				return false;
			}
			player.setNextAnimation(defs.getAnimation());
			player.lock(4);
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					player.setNextAnimation(new Animation(-1));
					World.removeObject(object);
				}
			}, 3);
			return false;
		} else if (object.getId() >= 49286 && object.getId() <= 49288) {
			NPC boss = getNPC(player, 10058);
			if (boss != null)
				((DivineSkinweaver) boss).blockHole(player, object);
			return false;
		} else if (object.getId() == 49297) {

			int value = player.getTempAttribs().getI("UNHOLY_CURSEBEARER_ROT");
			if (value >= 6) {
				NPC boss = getNPC(player, "Unholy cursebearer");
				if (boss != null) {
					player.sendMessage("You restore your combat stats, and the skeletal archmage is healed in the process. The font lessens the effect of the rot within your body.");
					player.getTempAttribs().setI("UNHOLY_CURSEBEARER_ROT", 1);
					player.getSkills().restoreSkills();
					boss.heal(boss.getMaxHitpoints() / 10);
				}
			} else
				player.sendMessage("You can't restore your stats yet.");
			return false;
		} else if (object.getId() >= KeyDoors.getLowestObjectId() && object.getId() <= KeyDoors.getMaxObjectId()) {
			int index = room.getDoorIndexByRotation(object.getRotation());
			if (index == -1)
				return false;
			Door door = room.getDoor(index);
			if (door == null || door.getType() != DungeonConstants.KEY_DOOR)
				return false;
			KeyDoors key = KeyDoors.values()[door.getId()];
			if (!Settings.getConfig().isDebug() && !dungeon.hasKey(key) && !player.getInventory().containsItem(key.getKeyId(), 1)) {
				player.sendMessage("You don't have the correct key.");
				return false;
			}
			player.getInventory().deleteItem(key.getKeyId(), 1);
			player.lock(1);
			player.sendMessage("You unlock the door.");
			player.setNextAnimation(new Animation(13798));// unlock key
			dungeon.setKey(key, false);
			room.setDoor(index, null);
			World.removeObject(object);
			return false;
		} else if (object.getId() == DungeonConstants.DUNGEON_DOORS[floorType] || object.getId() == DungeonConstants.DUNGEON_GUARDIAN_DOORS[floorType] || object.getId() == DungeonConstants.DUNGEON_BOSS_DOORS[floorType] || DungeonUtils.isOpenSkillDoor(object.getId(), floorType) || (object.getId() >= KeyDoors.getLowestDoorId(floorType) && object.getId() <= KeyDoors.getMaxDoorId(floorType)) || (object.getDefinitions().getName().equals("Door") && object.getDefinitions().containsOption(0, "Enter")) //theres many ids for challenge doors
				) {
			if (object.getId() == DungeonConstants.DUNGEON_BOSS_DOORS[floorType] && player.inCombat()) {
				player.sendMessage("This door is too complex to unlock while in combat.");
				return false;
			}
			Door door = room.getDoorByRotation(object.getRotation());
			if (door == null) {
				openDoor(object);
				return false;
			}
			if (door.getType() == DungeonConstants.GUARDIAN_DOOR)
				player.sendMessage("The door won't unlock until all of the guardians in the room have been slain.");
			else if (door.getType() == DungeonConstants.KEY_DOOR || door.getType() == DungeonConstants.SKILL_DOOR)
				player.sendMessage("The door is locked.");
			else if (door.getType() == DungeonConstants.CHALLENGE_DOOR)
				player.sendMessage(((PuzzleRoom) vr).getLockMessage());
			return false;
		} else if (object.getId() == DungeonConstants.THIEF_CHEST_LOCKED[floorType]) {
			room = dungeon.getRoom(dungeon.getCurrentRoomReference(player.getTile()));
			int type = room.getThiefChest();
			if (type == -1)
				return false;
			int level = type == 0 ? 1 : (type * 10);
			if (level > player.getSkills().getLevel(Constants.THIEVING)) {
				player.sendMessage("You need a " + Constants.SKILL_NAME[Constants.THIEVING] + " level of " + level + " to open this chest.");
				return false;
			}
			room.setThiefChest(-1);
			player.setNextAnimation(new Animation(536));
			player.lock(2);
			GameObject openedChest = new GameObject(object);
			openedChest.setId(DungeonConstants.THIEF_CHEST_OPEN[floorType]);
			World.spawnObject(openedChest);
			player.getInventory().addItemDrop(DungeonConstants.RUSTY_COINS, Utils.random((type + 1) * 10000) + 1);
			if (Utils.random(2) == 0)
				player.getInventory().addItemDrop(DungeonConstants.CHARMS[Utils.random(DungeonConstants.CHARMS.length)], Utils.random(5) + 1);
			if (Utils.random(3) == 0)
				player.getInventory().addItemDrop(DungeoneeringFarming.getHerbForLevel(level), Utils.random(1) + 1);
			if (Utils.random(4) == 0)
				player.getInventory().addItemDrop(DungeonUtils.getArrows(type + 1), Utils.random(100) + 1);
			if (Utils.random(5) == 0)
				player.getInventory().addItemDrop(DungeonUtils.getRandomWeapon(type + 1), 1);
			player.getSkills().addXp(Constants.THIEVING, DungeonConstants.THIEF_CHEST_XP[type]);
			return false;
		} else if (object.getId() == DungeonConstants.THIEF_CHEST_OPEN[floorType]) {
			player.sendMessage("You already looted this chest.");
			return false;
		} else if (DungeonUtils.isLadder(object.getId(), floorType)) {
			if (voteStage != 0) {
				player.sendMessage("You have already voted to move on.");
				return false;
			}
			player.startConversation(new DungeonClimbLadder(player, this));
			return false;
		} else if (object.getId() == 53977 || object.getId() == 53978 || object.getId() == 53979) {
			int type = object.getId() == 53977 ? 0 : object.getId() == 53979 ? 1 : 2;
			NPC boss = getNPC(player, "Runebound behemoth");
			if (boss != null)
				((RuneboundBehemoth) boss).activateArtifact(player, object, type);
			return false;
		}
		String name = object.getDefinitions().getName().toLowerCase();
		switch (name) {
		case "dungeon exit":
			player.startConversation(new DungeonExit(player, this));
			return false;
		case "water trough":
			player.startConversation(new CreateActionD(player, new Item[][] {{ new Item(17490) }}, new Item[][] {{ new Item(17492) }}, null, new int[] { 883 }, -1, 1));
			return false;
		case "salve nettles":
			DungeoneeringFarming.initHarvest(player, Harvest.SALVE_NETTLES, object);
			return false;
		case "wildercress":
			DungeoneeringFarming.initHarvest(player, Harvest.WILDERCRESS, object);
			return false;
		case "blightleaf":
			DungeoneeringFarming.initHarvest(player, Harvest.BLIGHTLEAF, object);
			return false;
		case "roseblood":
			DungeoneeringFarming.initHarvest(player, Harvest.ROSEBLOOD, object);
			return false;
		case "bryll":
			DungeoneeringFarming.initHarvest(player, Harvest.BRYLL, object);
			return false;
		case "duskweed":
			DungeoneeringFarming.initHarvest(player, Harvest.DUSKWEED, object);
			return false;
		case "soulbell":
			DungeoneeringFarming.initHarvest(player, Harvest.SOULBELL, object);
			return false;
		case "ectograss":
			DungeoneeringFarming.initHarvest(player, Harvest.ECTOGRASS, object);
			return false;
		case "runeleaf":
			DungeoneeringFarming.initHarvest(player, Harvest.RUNELEAF, object);
			return false;
		case "spiritbloom":
			DungeoneeringFarming.initHarvest(player, Harvest.SPIRITBLOOM, object);
			return false;
		case "tangle gum tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.TANGLE_GUM_VINE));
			return false;
		case "seeping elm tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.SEEPING_ELM_TREE));
			return false;
		case "blood spindle tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.BLOOD_SPINDLE_TREE));
			return false;
		case "utuku tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.UTUKU_TREE));
			return false;
		case "spinebeam tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.SPINEBEAM_TREE));
			return false;
		case "bovistrangler tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.BOVISTRANGLER_TREE));
			return false;
		case "thigat tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.THIGAT_TREE));
			return false;
		case "corpsethorn tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.CORPESTHORN_TREE));
			return false;
		case "entgallow tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.ENTGALLOW_TREE));
			return false;
		case "grave creeper tree":
			player.getActionManager().setAction(new DungeoneeringWoodcutting(object, DungTree.GRAVE_CREEPER_TREE));
			return false;
		case "novite ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.NOVITE_ORE));
			return false;
		case "bathus ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.BATHUS_ORE));
			return false;
		case "marmaros ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.MARMAROS_ORE));
			return false;
		case "kratonite ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.KRATONIUM_ORE));
			return false;
		case "fractite ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.FRACTITE_ORE));
			return false;
		case "zephyrium ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.ZEPHYRIUM_ORE));
			return false;
		case "argonite ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.AGRONITE_ORE));
			return false;
		case "katagon ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.KATAGON_ORE));
			return false;
		case "gorgonite ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.GORGONITE_ORE));
			return false;
		case "promethium ore":
			player.getActionManager().setAction(new DungeoneeringMining(object, DungeoneeringRocks.PROMETHIUM_ORE));
			return false;
		case "furnace":
			DungeoneeringSmithing.openSmelting(player);
			return false;
		case "anvil":
			for (int i = 17668;i >= 17650;i -= 2)
				if (player.containsItem(i)) {
					DungeoneeringSmithing.openInterface(player, new Item(i));
					break;
				}
			return false;
		case "runecrafting altar":
			player.startConversation(new DungeoneeringRCD(player, null));
			return false;
		case "spinning wheel":
			ReqItem[] products = ReqItem.getProducts(Category.DUNG_SPINNING);
			if (products == null || products.length <= 0)
				return false;
			player.startConversation(new CreationActionD(player, Category.DUNG_SPINNING, products, 883, 2));
			return false;
		case "summoning obelisk":
			Summoning.openInfusionInterface(player, true);
			if (player.getSkills().getLevel(Constants.SUMMONING) < player.getSkills().getLevelForXp(Constants.SUMMONING)) {
				player.sendMessage("You touch the obelisk", true);
				player.setNextAnimation(new Animation(8502));
				World.sendSpotAnim(object.getTile(), new SpotAnim(1308));
				WorldTasks.schedule(2, () -> {
					player.getSkills().set(Constants.SUMMONING, player.getSkills().getLevelForXp(Constants.SUMMONING));
					player.sendMessage("...and recharge your summoning points.", true);
				});
			}
			return false;
		case "group gatestone portal":
			portalGroupStoneTeleport();
			return false;
		case "sunken pillar":
			Blink boss = (Blink) getNPC(player, "Blink");
			if (boss == null) {
				player.sendMessage("The mechanism doesn't respond.");
				return false;
			}
			if (boss.hasActivePillar()) {
				player.sendMessage("The mechanism will not respond while the other pillar is raised.");
				return false;
			}
			for (Entity t : boss.getPossibleTargets())
				if (t.matches(object.getTile()) || boss.matches(object.getTile())) {
					player.sendMessage("The mechanism cannot be activated while someone is standing there.");
					return false;
				}
			boss.raisePillar(object);
			return false;
		default:
			return true;
		}
	}

	@Override
	public boolean processObjectClick2(final GameObject object) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;

		VisibleRoom vr = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
		if (vr == null || !vr.processObjectClick2(player, object))
			return false;
		if (object.getId() >= DungeonConstants.FARMING_PATCH_BEGINING && object.getId() <= DungeonConstants.FARMING_PATCH_END) {
			if (object.getDefinitions().containsOption("Inspect"))
				return false;
			Harvest harvest = Harvest.values()[((object.getId() - 50042) / 3)];
			if (harvest == null)
				return false;
			DungeoneeringFarming.initHarvest(player, harvest, object);
			return false;
		}
		String name = object.getDefinitions().getName().toLowerCase();
		switch (name) {
		case "runecrafting altar":
			player.startConversation(new DungeoneeringRCD(player, DungRCSet.ELEMENTAL));
			return false;
		case "summoning obelisk":
			if (player.getSkills().getLevel(Constants.SUMMONING) < player.getSkills().getLevelForXp(Constants.SUMMONING)) {
				player.sendMessage("You touch the obelisk", true);
				player.setNextAnimation(new Animation(8502));
				World.sendSpotAnim(object.getTile(), new SpotAnim(1308));
				WorldTasks.schedule(2, () -> {
					player.getSkills().set(Constants.SUMMONING, player.getSkills().getLevelForXp(Constants.SUMMONING));
					player.sendMessage("...and recharge your summoning points.", true);
				});
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick3(GameObject object) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;
		VisibleRoom vr = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
		if (vr == null || !vr.processObjectClick4(player, object))
			return false;
		if (object.getId() >= DungeonConstants.FARMING_PATCH_BEGINING && object.getId() <= DungeonConstants.FARMING_PATCH_END) {
			DungeoneeringFarming.clearHarvest(player, object);
			return false;
		}
		String name = object.getDefinitions().getName().toLowerCase();
		switch (name) {
		case "runecrafting altar":
			player.startConversation(new DungeoneeringRCD(player, DungRCSet.COMBAT));
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick4(final GameObject object) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen() || !dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile())).processObjectClick4(player, object))
			return false;
		String name = object.getDefinitions().getName().toLowerCase();
		switch (name) {
		case "runecrafting altar":
			player.startConversation(new DungeoneeringRCD(player, DungRCSet.OTHER));
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick5(final GameObject object) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;
		VisibleRoom vr = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
		if (vr == null || !vr.processObjectClick5(player, object))
			return false;
		String name = object.getDefinitions().getName().toLowerCase();
		switch (name) {
		case "runecrafting altar":
			player.startConversation(new DungeoneeringRCD(player, DungRCSet.STAVES));
			return false;
		}
		return true;
	}

	public void leaveDungeon() {
		if (dungeon == null || !dungeon.hasStarted())
			return;
		dungeon.getParty().leaveParty(player, false);
	}

	public void voteToMoveOn() {
		if (dungeon == null || !dungeon.hasStarted() || voteStage != 0)
			return;
		voteStage = 1;
		dungeon.voteToMoveOn(player);
	}


	@Override
	public boolean processItemOnObject(GameObject object, Item item) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;
		VisibleRoom room = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
		if ((room == null) || !room.handleItemOnObject(player, object, item))
			return false;
		String name = object.getDefinitions().getName().toLowerCase();
		switch (name) {
		case "farming patch":
			DungeoneeringFarming.plantHarvest(item, player, object, dungeon);
			return true;
		case "furnace":
			DungeoneeringSmithing.openSmelting(player);
			return true;
		case "anvil":
			DungeoneeringSmithing.openInterface(player, item);
			return true;
		case "spinning wheel":
			ReqItem[] products = ReqItem.getProducts(Category.DUNG_SPINNING, item.getId());
			if (products == null || products.length <= 0)
				return false;
			player.startConversation(new CreationActionD(player, Category.DUNG_SPINNING, item.getId(), 883, 2));
			return false;
		}
		return true;
	}

	@Override
	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isAtRewardsScreen())
			return false;
		if (itemUsed.getId() == 17446 || usedWith.getId() == 17446) {
			ReqItem[] products = ReqItem.getProducts(Category.DUNG_NEEDLE_CRAFTING, usedWith.getId());
			if (products == null || products.length <= 0)
				products = ReqItem.getProducts(Category.DUNG_NEEDLE_CRAFTING, itemUsed.getId());
			if (products == null || products.length <= 0)
				return false;
			player.startConversation(new CreationActionD(player, Category.DUNG_NEEDLE_CRAFTING, products[0].getMaterials()[0].getId(), -1, 2));
		} else if (itemUsed.getId() == 17752 || usedWith.getId() == 17752) {
			ReqItem[] products = ReqItem.getProducts(Category.DUNG_BOWSTRINGING, usedWith.getId());
			if (products == null || products.length <= 0)
				products = ReqItem.getProducts(Category.DUNG_BOWSTRINGING, itemUsed.getId());
			if (products == null || products.length <= 0)
				return false;
			player.startConversation(new CreationActionD(player, Category.DUNG_BOWSTRINGING, products[0].getMaterials()[0].getId(), -1, 2));
		} else if (itemUsed.getId() == 17754 || usedWith.getId() == 17754) {
			ReqItem[] products = ReqItem.getProducts(Category.DUNG_KNIFE_FLETCHING, usedWith.getId());
			if (products == null || products.length <= 0)
				products = ReqItem.getProducts(Category.DUNG_KNIFE_FLETCHING, itemUsed.getId());
			if (products == null || products.length <= 0)
				return false;
			player.startConversation(new CreationActionD(player, Category.DUNG_KNIFE_FLETCHING, products[0].getMaterials()[0].getId(), -1, 2));
		} else if (itemUsed.getId() == 17742 || usedWith.getId() == 17742 || itemUsed.getId() == 17747 || usedWith.getId() == 17747) {
			ReqItem[] products = ReqItem.getProducts(Category.DUNG_ARROW_COMBINING, usedWith.getId());
			if (products == null || products.length <= 0)
				products = ReqItem.getProducts(Category.DUNG_ARROW_COMBINING, itemUsed.getId());
			if (products == null || products.length <= 0)
				return false;
			player.startConversation(new CreationActionD(player, Category.DUNG_ARROW_COMBINING, products[0].getMaterials()[1].getId(), -1, 1));
		}
		return true;
	}

	public void openDoor(GameObject object) {
		RoomReference roomReference = dungeon.getCurrentRoomReference(player.getTile());
		if (dungeon.enterRoom(player, roomReference.getRoomX() + Utils.ROTATION_DIR_X[object.getRotation()], roomReference.getRoomY() + Utils.ROTATION_DIR_Y[object.getRotation()]))
			hideBar();
	}

	/**
	 * called once teleport is performed
	 */
	@Override
	public void magicTeleported(int type) {
		dungeon.playMusic(player, dungeon.getCurrentRoomReference(player.getNextTile()));
		hideBar();
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (target instanceof DungeonSlayerNPC npc)
			if (player.getSkills().getLevel(Constants.SLAYER) < npc.getType().getReq()) {
				player.sendMessage("You need a Slayer level of " + npc.getType().getReq() + " in order to attack this monster.");
				return false;
			}
		return true;
	}

	/*
	 * return process normaly
	 */
	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (dungeon == null || !dungeon.hasStarted())
			return false;

		if (dungeon.isAtRewardsScreen()) {
			if (interfaceId == 933)
				if (componentId >= 318 && componentId <= 322)
					if (packet == ClientPacket.IF_OP2)
						player.startConversation(new DungeonLeave(player, this));
					else {
						if (voteStage == 2)
							return false;
						voteStage = 2;
						dungeon.ready(player);
					}
			return false;
		}
		if ((interfaceId == 548 && componentId == 157) || (interfaceId == 746 && componentId == 200)) {
			if (player.getInterfaceManager().containsScreenInter() || player.getInterfaceManager().containsInventoryInter()) {
				player.sendMessage("Please finish what you're doing before opening the dungeon map.");
				return false;
			}
			dungeon.openMap(player);
			return false;
		}
		if (interfaceId == Inventory.INVENTORY_INTERFACE) {
			Item item = player.getInventory().getItem(slotId);
			if (item == null || item.getId() != slotId2)
				return false;
			if (packet == ClientPacket.IF_OP1) {
				for (int index = 0; index < DungeoneeringTraps.ITEM_TRAPS.length; index++)
					if (item.getId() == DungeoneeringTraps.ITEM_TRAPS[index]) {
						DungeoneeringTraps.placeTrap(player, dungeon, index);
						return false;
					}
				for (int element : PoltergeistRoom.HERBS)
					if (item.getId() == element) {
						VisibleRoom room = dungeon.getVisibleRoom(dungeon.getCurrentRoomReference(player.getTile()));
						if (room == null)
							return false;
						if (!(room instanceof PoltergeistRoom)) {
							player.sendMessage("You need to be closer to the poltergeist to cleanse this herb.");
							return false;
						}
						((PoltergeistRoom) room).consecrateHerbs(player, item.getId());
						return false;
					}
			}
			return true;
		} else if (interfaceId == 934)
			DungeoneeringSmithing.handleButtons(player, packet, componentId);
		else if (interfaceId == DungeonResourceShop.RESOURCE_SHOP) {
			if (componentId == 24) {
				int quantity = -1;
				if (packet == ClientPacket.IF_OP2)
					quantity = 1;
				else if (packet == ClientPacket.IF_OP3)
					quantity = 5;
				else if (packet == ClientPacket.IF_OP4)
					quantity = 10;
				else if (packet == ClientPacket.IF_OP5)
					quantity = 50;
				DungeonResourceShop.handlePurchaseOptions(player, slotId, quantity);
			}
			return false;
		} else if (interfaceId == DungeonResourceShop.RESOURCE_SHOP_INV) {
			if (componentId == 0) {
				int quantity = -1;
				if (packet == ClientPacket.IF_OP2)
					quantity = 1;
				else if (packet == ClientPacket.IF_OP3)
					quantity = 5;
				else if (packet == ClientPacket.IF_OP4)
					quantity = 10;
				else if (packet == ClientPacket.IF_OP5)
					quantity = 50;
				DungeonResourceShop.handleSellOptions(player, slotId, slotId2, quantity);
			}
			return false;
		} else if (interfaceId == 950) {
			if (componentId == 24) {
				if (dungeon == null || dungeon.getDungeon() == null)
					return false;
				if (player.inCombat()) {
					player.sendMessage("You cannot do that while in combat.");
					return false;
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, dungeon.getHomeTile(), null, null);
			}
			if (componentId == 2)
				player.getCombatDefinitions().switchDefensiveCasting();
			else if (componentId == 7)
				player.getCombatDefinitions().switchShowCombatSpells();
			else if (componentId == 9)
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			else if (componentId == 11)
				player.getCombatDefinitions().switchShowMiscSpells();
			else if (componentId == 13)
				player.getCombatDefinitions().switchShowSkillSpells();
			else if (componentId >= 15 & componentId <= 17)
				player.getCombatDefinitions().setSortSpellBook(componentId - 15);
			else if (componentId == 39 || componentId == 40)
				stoneTeleport(componentId == 40);
			else if (componentId == 38) {
				if (!player.getInventory().hasFreeSlots()) {
					player.sendMessage("You don't have enough inventory space.");
					return false;
				}
				if (packet == ClientPacket.IF_OP2) {
					player.getInventory().deleteItem(DungeonConstants.GATESTONE, 1);
					if (gatestone != null) {
						GroundItem item = ChunkManager.getChunk(gatestone.getChunkId()).getGroundItem(DungeonConstants.GATESTONE, gatestone, player);
						if (item == null)
							return false;
						World.removeGroundItem(player, item, false);
						setGatestone(null);
					}
				}
				if (!canCreateGatestone())
					return false;
				player.getInventory().addItem(DungeonConstants.GATESTONE, 1);
				player.getSkills().addXp(Constants.MAGIC, 43.5);
				player.setNextAnimation(new Animation(713));
				player.setNextSpotAnim(new SpotAnim(113));
			} else
				Magic.processDungSpell(player, componentId, packet);
			return false;
		}
		return true;
	}

	@Override
	public boolean canDropItem(Item item) {
		if (item.getName().contains("Ring of kinship")) {
			player.simpleDialogue("You cannot destroy that here.");
			return false;
		}
		if (item.getDefinitions().isDestroyItem())
			return true;
		Tile currentTile = Tile.of(player.getTile());
		player.stopAll(false);
		player.getInventory().deleteItem(item);
		if (item.getId() == DungeonConstants.GROUP_GATESTONE)
			dungeon.setGroupGatestone(currentTile);
		else if (item.getId() == DungeonConstants.GATESTONE) {
			setGatestone(currentTile);
			World.addGroundItem(item, currentTile, player, true, -1, DropMethod.NORMAL, -1);
			player.sendMessage("You place the gatestone. You can teleport back to it at any time.");
			return false;
		}
		World.addGroundItem(item, currentTile);
		return false;
	}

	private void stoneTeleport(boolean group) {
		Tile tile = group ? dungeon.getGroupGatestone() : gatestone;

		if (dungeon.isAtBossRoom(player.getTile(), 26, 626, true) || (dungeon.isAtBossRoom(player.getTile(), 26, 672, true) && !YkLagorThunderous.isBehindPillar(player, dungeon, dungeon.getCurrentRoomReference(Tile.of(player.getTile()))))) {
			player.sendMessage("You can't teleport here.");
			return;
		}
		if (tile == null) { // Shouldn't happen for group gatestone
			player.sendMessage("You currently do not have an active gatestone.");
			return;
		}

		if (!group) {
			GroundItem item = ChunkManager.getChunk(gatestone.getChunkId()).getGroundItem(DungeonConstants.GATESTONE, tile, player);
			if (item == null)
				return;
			World.removeGroundItem(player, item);
			player.getInventory().deleteItem(item);
			setGatestone(null);
		}
		if (!Magic.checkRunes(player, true, new RuneSet(Rune.LAW, 3)))
			return;
		Magic.sendTeleportSpell(player, 13288, 13285, 2516, 2517, group ? 64 : 32, 0, tile, 3, false, Magic.MAGIC_TELEPORT, null);
		if (!group) {
			player.setCantWalk(true);
			player.getEmotesManager().setNextEmoteEnd(3); //prevents dropping etc
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setCantWalk(false);
				}
			}, 4);
		}
	}

	private void portalGroupStoneTeleport() {
		Tile tile = dungeon.getGroupGatestone();
		if (tile == null) //cant happen
			return;
		Magic.sendTeleportSpell(player, 14279, 13285, 2518, 2517, 1, 0, tile, 1, false, Magic.OBJECT_TELEPORT, null);
	}

	private boolean canCreateGatestone() {
		if (player.getInventory().containsItem(DungeonConstants.GATESTONE, 1)) {
			player.sendMessage("You already have a gatestone in your pack. Making another would be pointless.");
			return false;
		}
		if (gatestone != null) {
			player.sendMessage("You already have an active gatestone.");
			return false;
		}
		if (!Magic.checkSpellLevel(player, 32))
			return false;
		else if (!Magic.checkRunes(player, true, new RuneSet(Rune.COSMIC, 3)))
			return false;
		return true;
	}

	public void leaveDungeonPermanently() {
		int index = dungeon.getParty().getIndex(player);
		leaveDungeon();
		for (Player p2 : dungeon.getParty().getTeam())
			p2.getPackets().sendVarc(1397 + index, 2);
	}

	@Override
	public boolean processItemOnPlayer(Player p2, Item item, int slot) {
		if (Foods.isConsumable(item) && Foods.eat(p2, item, slot, player))
			player.getInventory().deleteItem(item.getId(), 1);
		return true;
	}

	@Override
	public void forceClose() {
		if (dungeon != null)
			dungeon.getParty().leaveParty(player, false);
		else {
			for (Item item : player.getInventory().getItems().array()) {
				if (DungManager.isBannedDungItem(item))
					player.getInventory().deleteItem(item);
			}
			for (Item item : player.getEquipment().getItemsCopy()) {
				if (DungManager.isBannedDungItem(item))
					player.getEquipment().deleteItem(item.getId(), item.getAmount());
			}
			if (player.getInventory().containsItem(15707))
				player.getInventory().deleteItem(15707, 27);
			if (player.getEquipment().containsOneItem(15707))
				player.getEquipment().deleteItem(15707, 27);
			player.getInterfaceManager().removeOverlay();
			player.setForceMultiArea(false);
			if (player.getFamiliar() != null)
				player.getFamiliar().sendDeath(player);
			player.setLocation(Tile.of(DungeonConstants.OUTSIDE, 2));
		}
	}

	@Override
	public boolean login() {
		removeController();
		player.setNextTile(Tile.of(DungeonConstants.OUTSIDE, 2));
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	private void setGatestone(Tile gatestone) {
		this.gatestone = gatestone;
	}

	public boolean isKilledBossWithLessThan10HP() {
		return killedBossWithLessThan10HP;
	}
}
