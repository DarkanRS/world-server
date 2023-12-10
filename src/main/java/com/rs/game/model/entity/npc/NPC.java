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
package com.rs.game.model.entity.npc;

import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.engine.thread.LowPriorityTaskExecutor;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.bosses.godwars.GodwarsController;
import com.rs.game.content.combat.PolyporeStaff;
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager;
import com.rs.game.content.skills.hunter.BoxHunterType;
import com.rs.game.content.skills.slayer.SlayerMonsters;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.world.areas.dungeons.TzHaar;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.combat.NPCCombat;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AggressiveType;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.Skill;
import com.rs.game.model.entity.pathing.*;
import com.rs.game.model.entity.player.Bank;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.game.tasks.TaskInformation;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.net.packets.encoders.Sound;
import com.rs.lib.net.packets.encoders.Sound.SoundType;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.NPCDeathEvent;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.tools.old.CharmDrop;
import com.rs.utils.DropSets;
import com.rs.utils.EffigyDrop;
import com.rs.utils.NPCClueDrops;
import com.rs.utils.WorldUtil;
import com.rs.utils.drop.Drop;
import com.rs.utils.drop.DropTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NPC extends Entity {

	private int id;
	private Tile respawnTile;
	private boolean randomWalk;
	private Map<Skill, Integer> combatLevels;
	private boolean spawned;
	private transient NPCCombat combat;
	private transient boolean ignoreNPCClipping;
	public Tile forceWalk;
	private int size;
	private boolean hidden = false;
	private transient boolean loadsUpdateZones = false;
	private long lastAttackedByTarget;
	private boolean cantInteract;
	private int capDamage;
	private int lureDelay;
	private boolean cantFollowUnderCombat;
	private boolean forceAgressive;
	private boolean ignoreDocile;
	private int forceAggroDistance;
	private boolean forceFollowClose;
	private boolean forceMultiAttacked;
	private boolean noDistanceCheck;
	private transient long timeLastSpawned;
	private boolean canAggroNPCs;

	// npc masks
	private transient Transformation nextTransformation;
	private transient NPCBodyMeshModifier bodyMeshModifier;
	protected transient ConcurrentHashMap<Object, Object> temporaryAttributes;
	// name changing masks
	private String name;
	private transient boolean changedName;
	private transient boolean permName;
	private int combatLevel;
	private transient boolean changedCombatLevel;
	private transient boolean locked;
	private transient boolean skipWalkStep;
	private transient boolean deleted = false;
	private transient TaskInformation respawnTask;

	public boolean switchWalkStep() {
		return skipWalkStep = !skipWalkStep;
	}

	private boolean intelligentRoutefinder;
	public boolean maskTest;

	public NPC(int id, Tile tile, Direction direction, boolean permaDeath) {
		super(tile);
		this.id = id;
		respawnTile = Tile.of(tile);
		setSpawned(permaDeath);
		combatLevel = -1;
		setHitpoints(getMaxHitpoints());
		setFaceAngle(direction == null ? getRespawnDirection() : direction.getAngle());
		setRandomWalk((getDefinitions().walkMask & 0x2) != 0 || forceRandomWalk(id));
		setClipType((getDefinitions().walkMask & 0x4) != 0 ? ClipType.WATER : ClipType.NORMAL);
		size = getDefinitions().size;
		if (getDefinitions().hasAttackOption())
			setRandomWalk(true);
		if (getDefinitions().getName().toLowerCase().contains("glac"))
			setRandomWalk(false);
		if (id == 7891)
			setRandomWalk(false);
		BoxHunterType npc = BoxHunterType.forId(id);
		if (npc != null)
			setRandomWalk(true);
		if (getDefinitions().combatLevel >= 200)
			setIgnoreDocile(true);
		combatLevels = NPCCombatDefinitions.getDefs(id).getLevels();
		combat = new NPCCombat(this);
		capDamage = -1;
		lureDelay = 12000;
		// npc is inited on creating instance
		initEntity();
		World.addNPC(this);
		ChunkManager.updateChunks(this);
		// npc is started on creating instance
		loadMapRegions();
		checkMultiArea();
	}

	public NPC(int id, Tile tile, boolean permaDeath) {
		this(id,tile, null, permaDeath);
	}


	public NPC(int id, Tile tile) {
		this(id, tile, false);
	}

	public boolean walksOnWater() {
		return (getDefinitions().walkMask & 0x4) != 0;
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || nextTransformation != null || bodyMeshModifier != null || getBas() != -1 || changedCombatLevel || changedName || maskTest || permName;
	}

	public void setLoadsUpdateZones() {
		loadsUpdateZones = true;
	}

	public void resetLevels() {
		combatLevels = NPCCombatDefinitions.getDefs(id).getLevels();
	}

	public void transformIntoNPC(int id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
	}

	public void setNextNPCTransformation(int id) {
		transformIntoNPC(id);
	}

	public void setNPC(int id) {
		this.id = id;
		size = getDefinitions().size;
		combatLevels = NPCCombatDefinitions.getDefs(id).getLevels();
	}

	public void setLevels(Map<Skill, Integer> levels) {
		this.combatLevels = levels;
	}

	public void resetDirection() {
		setNextFaceTile(null);
		setNextFaceEntity(null);
		setFaceAngle(getRespawnDirection());
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		nextTransformation = null;
		changedName = false;
		if (bodyMeshModifier == NPCBodyMeshModifier.RESET)
			bodyMeshModifier = null;
		if (getBas() == -2)
			setBasNoReset(-1);
	}

	public NPCDefinitions getDefinitions(Player player) {
		return NPCDefinitions.getDefs(id, player.getVars());
	}

	public NPCDefinitions getDefinitions() {
		return NPCDefinitions.getDefs(id);
	}

	public NPCCombatDefinitions getCombatDefinitions() {
		return NPCCombatDefinitions.getDefs(id);
	}

	@Override
	public int getMaxHitpoints() {
		return getCombatDefinitions().getHitpoints();
	}

	public int getId() {
		return id;
	}

	public boolean checkNPCCollision(Direction dir) {
		if (isIgnoreNPCClipping() || isIntelligentRouteFinder() || isForceWalking())
			return true;
		return WorldCollision.checkNPCClip(this, dir);
	}
	
	private void restoreTick() {
		for (Skill skill : Skill.values()) {
			int currentLevel = getLevel(skill);
			int normalLevel = getCombatDefinitions().getLevel(skill);
			if (currentLevel > normalLevel)
				setStat(skill, currentLevel - 1);
			else if (currentLevel < normalLevel)
				setStat(skill, Utils.clampI(currentLevel + 1, 0, normalLevel));
		}
	}

	public void processNPC() {
		if (isDead() || locked)
			return;
		//Restore combat stats
		if (getTickCounter() % 100 == 0)
			restoreTick();
		if (!combat.process() && routeEvent == null) {
			if (!isForceWalking() && !cantInteract && !checkAggressivity() && !hasEffect(Effect.FREEZE)) {
				if (!hasWalkSteps() && shouldRandomWalk()) {
					boolean can = Math.random() > 0.9;
					if (can) {
						int moveX = Utils.random(getDefinitions().hasAttackOption() ? 4 : 2, getDefinitions().hasAttackOption() ? 8 : 4);
						int moveY = Utils.random(getDefinitions().hasAttackOption() ? 4 : 2, getDefinitions().hasAttackOption() ? 8 : 4);
						if (Utils.random(2) == 0)
							moveX = -moveX;
						if (Utils.random(2) == 0)
							moveY = -moveY;
						resetWalkSteps();
						DumbRouteFinder.addDumbPathfinderSteps(this, respawnTile.transform(moveX, moveY, 0), getDefinitions().hasAttackOption() ? 7 : 3, getClipType());
						if (Utils.getDistance(this.getTile(), respawnTile) > 3 && !getDefinitions().hasAttackOption())
							DumbRouteFinder.addDumbPathfinderSteps(this, respawnTile, getDefinitions().hasAttackOption() ? 7 : 3, getClipType());
					}
				}
			}
		}
		if (isForceWalking())
			if (!hasEffect(Effect.FREEZE))
				if (getX() != forceWalk.getX() || getY() != forceWalk.getY()) {
					if (!hasWalkSteps()) {
						Route route = RouteFinder.find(getX(), getY(), getPlane(), getSize(), new FixedTileStrategy(forceWalk.getX(), forceWalk.getY()), true);
						for (int i = route.getStepCount() - 1; i >= 0; i--)
							if (!addWalkSteps(route.getBufferX()[i], route.getBufferY()[i], 25, true, true))
								break;
					}
					if (!hasWalkSteps()) { // failing finding route
						setNextTile(Tile.of(forceWalk));
						forceWalk = null; // so ofc reached forcewalk place
					}
				} else
					// walked till forcewalk place
					forceWalk = null;
	}

	@Override
	public void processEntity() {
		try {
			super.processEntity();
			processNPC();
		} catch (Throwable e) {
			Logger.handle(NPC.class, "processEntityNPC", e);
		}
	}

	public int getRespawnDirection() {
		return Direction.getById(getDefinitions().respawnDirection).getAngle();
	}

	private static boolean forceRandomWalk(int npcId) {
		switch (npcId) {
		case 11226:
			return true;
		case 3341:
		case 3342:
		case 3343:
			return true;
		default:
			return false;
		}
	}

	@Override
	public void handlePreHit(final Hit hit) {
		if (capDamage != -1 && hit.getDamage() > capDamage)
			hit.setDamage(capDamage);
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE && hit.getLook() != HitLook.CANNON_DAMAGE)
			return;
		Entity source = hit.getSource();
		if (source == null)
			return;
		if (source instanceof Player player) {
			SlayerMonsters thisMonster = SlayerMonsters.forId(getId());
			if (thisMonster != null)
				if (player.getSkills().getLevel(Constants.SLAYER) < thisMonster.getRequirement()) {
					hit.setDamage(0);
					player.sendMessage("You do not have the slayer level required to damage this monster.");
				}
			if (hit.getDamage() > 0 && isTzhaarMonster() && TzHaar.depleteTokkulZo(player))
				hit.setDamage((int) (hit.getDamage() * 1.1));
		}
		handlePostHit(hit);
	}

	@Override
	public void handlePreHitOut(Entity target, Hit hit) {

	}

	@Override
	public void handlePostHit(Hit hit) {
		if (capDamage != -1 && hit.getDamage() > capDamage)
			hit.setDamage(capDamage);
	}

	@Override
	public void reset() {
		super.reset();
		getInteractionManager().forceStop();
		setFaceAngle(getRespawnDirection());
		combat.reset();
		combatLevels = NPCCombatDefinitions.getDefs(id).getLevels(); // back to real bonuses
		forceWalk = null;
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		setFinished(true);
		ChunkManager.updateChunks(this);
		WorldCollision.fillNPCClip(getTile(), getSize(), false);
		World.removeNPC(this);
	}

	public void setRespawnTask() {
		setRespawnTask(-1);
	}

	/**
	 * For quests
	 * @param ticks
	 */
	public void finishAfterTicks(final int ticks) {
		WorldTasks.scheduleTimer(tick -> {
			if (tick == ticks) {
				if (!hasFinished())
					finish();
				return false;
			}
			return true;
		});
	}

	public void setRespawnTask(int time) {
		if (!hasFinished()) {
			reset();
			setTile(respawnTile);
			finish();
		}
		respawnTask = WorldTasks.schedule(time < 0 ? getCombatDefinitions().getRespawnDelay() : time, () -> spawn());
	}

	public void cancelRespawnTask() {
		if (respawnTask != null)
			WorldTasks.remove(respawnTask);
	}

	public void deserialize() {
		if (combat == null)
			combat = new NPCCombat(this);
		spawn();
	}

	public void spawn() {
		if (deleted)
			return;
		timeLastSpawned = System.currentTimeMillis();
		setFinished(false);
		World.addNPC(this);
		setLastChunkId(0);
		ChunkManager.updateChunks(this);
		loadMapRegions();
		checkMultiArea();
		onRespawn();
	}

	public void permanentlyDelete() {
		finish();
		deleted = true;
	}

	public void onRespawn() {

	}

	public long timeSinceSpawned() {
		return System.currentTimeMillis() - timeLastSpawned;
	}

	public NPCCombat getCombat() {
		return combat;
	}
	
	public int getLevel(Skill skill) {
		return combatLevels == null ? 1 : combatLevels.get(skill);
	}

	public int getAttackLevel() {
		return getLevel(Skill.ATTACK);
	}

	public int getDefenseLevel() {
		return getLevel(Skill.DEFENSE);
	}

	public int getStrengthLevel() {
		return getLevel(Skill.STRENGTH);
	}

	public int getRangeLevel() {
		return getLevel(Skill.RANGE);
	}

	public int getMagicLevel() {
		return getLevel(Skill.MAGE);
	}

	@Override
	public void sendDeath(final Entity source) {
		clearPendingTasks();
		final NPCCombatDefinitions defs = getCombatDefinitions();
		getInteractionManager().forceStop();
		resetWalkSteps();
		if (combat.getTarget() != null)
			combat.getTarget().setAttackedByDelay(0);
		combat.removeTarget();
		if (source.getAttackedBy() == NPC.this) {
			source.setAttackedBy(null);
			source.setFindTargetDelay(0);
		}
		setNextAnimation(null);
		PluginManager.handle(new NPCDeathEvent(this, source));
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0) {
				setNextAnimation(new Animation(defs.getDeathEmote()));
				if (source instanceof Player p)
					soundEffect(getCombatDefinitions().getDeathSound(), 1);
			}
			else if (loop >= defs.getDeathDelay()) {
				if (source instanceof Player player)
					player.getControllerManager().processNPCDeath(NPC.this);
				drop();
				reset();
				setTile(respawnTile);
				finish();
				if (!isSpawned())
					setRespawnTask();
				return false;
			}
			return true;
		});
	}

	public void drop(Player killer) {
		drop(killer, true);
	}

	public void drop(Player killer, boolean verifyCombatDefs) {
		try {
			if (verifyCombatDefs && (getCombatDefinitions() == NPCCombatDefinitions.DEFAULT_DEF || getMaxHitpoints() == 1))
				return;
			String name = getDefinitions(killer).getName();
			if (!name.equals("null") && !name.equals("Fire spirit"))
				killer.sendNPCKill(name);

			if (killer.getControllerManager().getController() != null && killer.getControllerManager().getController() instanceof GodwarsController)
				killer.sendGodwarsKill(this);

			if (killer.hasBossTask()) {
				String taskName = killer.getBossTask().getName().toLowerCase();
				if (this.getDefinitions().getName().equalsIgnoreCase(taskName))
					killer.getBossTask().sendKill(killer, this);
			}

			if (killer.hasSlayerTask() && killer.getSlayer().isOnTaskAgainst(this))
				killer.getSlayer().sendKill(killer, this);

			if (getId() >= 2031 && getId() <= 2037) {
				killer.setBarrowsKillCount(killer.getBarrowsKillCount()+1);
				killer.getVars().setVarBit(464, killer.getBarrowsKillCount()+killer.getKilledBarrowBrothersCount());
			}

			Item[] drops = DropTable.calculateDrops(killer, DropSets.getDropSet(id));

			for (Item item : drops)
				sendDrop(killer, item);

			if (DropSets.getDropSet(id) != null && DropSets.getDropSet(id) != DropSets.DEFAULT_DROPSET && EffigyDrop.dropEffigy(getDefinitions().combatLevel))
				sendDrop(killer, new Item(18778, 1));

			Item[] clues = DropTable.calculateDrops(killer, NPCClueDrops.rollClues(id));
			for (Item item : clues)
				sendDrop(killer, item);

			DropTable charm = CharmDrop.getCharmDrop(NPCDefinitions.getDefs(getId()).getName().toLowerCase());
			if (charm != null)
				for (Drop d : charm.getDrops())
					sendDrop(killer, d.toItem());
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	public void drop() {
		try {
			Player killer = getMostDamageReceivedSourcePlayer();
			if (killer == null)
				return;
			drop(killer);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	public static boolean yellDrop(int itemId) {
		//Nex

		//Revenants
		if ((itemId >= 20125 && itemId <= 20174) || (itemId >= 13845 && itemId <= 13990))
			return true;
		//Barrows
		if (itemId >= 4708 && itemId <= 4759 && itemId != 4740)
			return true;
		//New GWD Items
		if (itemId >= 24974 && itemId <= 25039)
			return true;
		switch (itemId) {
		//KBD
		case 25312:
		case 25314:
		case 25316:
		case 25318:
		case 11286:
			//Slayer Drops
		case 11732:
		case 15486:
		case 11235:
		case 4151:
		case 21369:
			//Dagganoth Drops
		case 6731:
		case 6733:
		case 6735:
		case 6737:
		case 6739:
			//Glacors
		case 21777:
		case 21787:
		case 21790:
		case 21793:
			//KQ
		case 3140:
			//Tormented Demons
		case 14484:
			//Original GWD
		case 11702:
		case 11704:
		case 11706:
		case 11708:
		case 11718:
		case 11722:
		case 11720:
		case 11724:
		case 11726:
		case 11728:
		case 11716:
		case 11730:
			return true;
		default:
			return false;
		}
	}

	public void sendDrop(Player player, Item item) {
		Player dropTo = player;

		if (player.isLootSharing() && player.getAccount().getSocial().getCurrentFriendsChat() != null && !TreasureTrailsManager.isScroll(item.getId())) {
			ArrayList<Player> possible = player.getNearbyFCMembers(this);
			if (possible.size() > 0) {
				dropTo = possible.get(Utils.random(possible.size()));
				for (Player p : possible)
					if (!p.getUsername().equals(dropTo.getUsername()))
						p.sendMessage(dropTo.getDisplayName()+" has recieved: "+item.getAmount()+" "+item.getName()+".");
					else
						p.sendMessage("<col=006600>You recieved: "+item.getAmount()+" "+item.getName()+".");
			}
		}

		dropTo.incrementCount(item.getName()+" drops earned", item.getAmount());

		if (yellDrop(item.getId()))
			World.broadcastLoot(dropTo.getDisplayName() + " has just received a " + item.getName() + " drop from " + getDefinitions().getName() + "!");

		final int size = getSize();
		
		PluginManager.handle(new NPCDropEvent(dropTo, this, item));
		if (item.getId() != -1 && dropTo.getNSV().getB("sendingDropsToBank")) {
			if (item.getDefinitions().isNoted())
				item.setId(item.getDefinitions().certId);
			sendDropDirectlyToBank(dropTo, item);
			return;
		}
		GroundItem gItem = World.addGroundItem(item, Tile.of(getCoordFaceX(size), getCoordFaceY(size), getPlane()), dropTo, true, 60);
		int value = item.getDefinitions().getValue() * item.getAmount();
		if (gItem != null && (value > player.getI("lootbeamThreshold", 90000) || item.getDefinitions().name.contains("Scroll box") || item.getDefinitions().name.contains(" defender") || yellDrop(item.getId())))
			player.getPackets().sendGroundItemMessage(50, 0xFF0000, gItem, "<shad=000000><col=cc0033>You received: "+ item.getAmount() + " " + item.getDefinitions().getName());
	}

	public static void sendDropDirectlyToBank(Player player, Item item) {
		player.getBank().addItem(item, true);
	}

	public static ItemsContainer<Item> getDropsFor(int npcId, int npcAmount, boolean row) {
		ItemsContainer<Item> dropCollection = new ItemsContainer<>(Bank.MAX_BANK_SIZE, true);

		double modifier = 1.0;
		if (row)
			modifier -= 0.01;

		for (int i = 0; i < npcAmount; i++) {
			List<Item> drops = DropSets.getDropSet(npcId).getDropList().genDrop(modifier);
			for (Item item : drops)
				dropCollection.add(item);
			List<Item> clues = NPCClueDrops.rollClues(npcId).getDropList().genDrop(modifier);
			for (Item item : clues)
				dropCollection.add(item);
			if (EffigyDrop.dropEffigy(NPCDefinitions.getDefs(npcId).combatLevel))
				dropCollection.add(new Item(18778, 1));
			DropTable charm = CharmDrop.getCharmDrop(NPCDefinitions.getDefs(npcId).getName().toLowerCase());
			if (charm != null)
				for (Drop d : charm.getDrops())
					dropCollection.add(d.toItem());
		}
		return dropCollection;
	}

	public static void displayDropsFor(Player player, int npcId, int npcAmount) {
		player.sendMessage("<col=FF0000><shad=000000>Calculating drops...");
		LowPriorityTaskExecutor.execute(() -> {
			long start = System.currentTimeMillis();
			ItemsContainer<Item> dropCollection = getDropsFor(npcId, npcAmount, player.getEquipment().wearingRingOfWealth());
			if (dropCollection == null) {
				player.sendMessage("No drops found for that NPC.");
				return;
			}
			dropCollection.sortByItemId();
			player.getTempAttribs().setB("viewingOtherBank", true);
			player.getVars().setVarBit(8348, 0);
			player.getVars().syncVarsToClient();
			player.getInterfaceManager().sendInterface(762);
			player.getPackets().sendRunScript(2319);
			player.getPackets().setIFText(762, 47, npcAmount+" "+NPCDefinitions.getDefs(npcId).getName()+" kills");
			player.getPackets().sendItems(95, dropCollection);
			player.getPackets().setIFEvents(new IFEvents(762, 95, 0, 516).enableRightClickOptions(0,1,2,3,4,5,6,9).setDepth(2).enableDrag());
			player.getVars().setVarBit(4893, 1);
			player.getVars().syncVarsToClient();
			player.sendMessage("<col=FF0000><shad=000000>Calculated drops in " + Utils.formatLong(System.currentTimeMillis() - start) + "ms");
		});
	}

	@Override
	public int getSize() {
		return size;
	}

	public int getMaxHit() {
		return getCombatDefinitions().getMaxHit();
	}

	public int getLevelForStyle(AttackStyle style) {
		int maxHit = getAttackLevel();
		if (style == AttackStyle.RANGE)
			maxHit = getRangeLevel();
		else if (style == AttackStyle.MAGE)
			maxHit = getMagicLevel();
		return maxHit;
	}

	public void lowerDefense(double multiplier, double maxDrain) {
		lowerStat(Skill.DEFENSE, multiplier, maxDrain);
	}

	public void lowerDefense(int drain, double maxDrain) {
		lowerStat(Skill.DEFENSE, drain, maxDrain);
	}

	public void lowerAttack(double multiplier, double maxDrain) {
		lowerStat(Skill.ATTACK, multiplier, maxDrain);
	}

	public void lowerAttack(int drain, double maxDrain) {
		lowerStat(Skill.ATTACK, drain, maxDrain);
	}

	public void lowerStrength(double multiplier, double maxDrain) {
		lowerStat(Skill.STRENGTH, multiplier, maxDrain);
	}

	public void lowerStrength(int drain, double maxDrain) {
		lowerStat(Skill.STRENGTH, drain, maxDrain);
	}

	public void lowerMagic(double multiplier, double maxDrain) {
		lowerStat(Skill.MAGE, multiplier, maxDrain);
	}

	public void lowerMagic(int drain, double maxDrain) {
		lowerStat(Skill.MAGE, drain, maxDrain);
	}
	
	public void lowerRange(double multiplier, double maxDrain) {
		lowerStat(Skill.RANGE, multiplier, maxDrain);
	}

	public void lowerRange(int drain, double maxDrain) {
		lowerStat(Skill.RANGE, drain, maxDrain);
	}
	
	public int getStat(Skill skill) {
		if (combatLevels == null)
			return 0;
		Integer level = combatLevels.get(skill);
		if (level == null)
			return 0;
		return level;
	}
	
	public void setStat(Skill skill, int level) {
		if (combatLevels == null)
			combatLevels = new HashMap<>();
		if (level < 0)
			level = 0;
		combatLevels.put(skill, level);
	}

	public void lowerStat(Skill stat, double multiplier, double maxDrain) {
		if (combatLevels != null)
			setStat(stat, Utils.clampI((int) (getStat(stat) - (getStat(stat) * multiplier)), (int) ((double) getCombatDefinitions().getLevels().get(stat) * maxDrain), getStat(stat)));
	}

	public void lowerStat(Skill stat, int levelDrain, double maxDrain) {
		if (combatLevels != null)
			setStat(stat, Utils.clampI((int) getStat(stat) - levelDrain, (int) ((double) getCombatDefinitions().getLevels().get(stat) * maxDrain), getStat(stat)));
	}

	public int getBonus(Bonus bonus) {
		if (NPCCombatDefinitions.getDefs(id).hasOverriddenBonuses())
			return NPCCombatDefinitions.getDefs(id).getBonus(bonus);
		else
			return NPCDefinitions.getDefs(id).getBonus(bonus);
	}

	public void resetHP() {
		setHitpoints(getMaxHitpoints());
	}

	public Bonus getHighestAttackBonus() {
		int highest = getBonus(Bonus.STAB_ATT);
		Bonus attType = Bonus.STAB_ATT;
		if (getBonus(Bonus.SLASH_ATT) > highest) {
			highest = getBonus(Bonus.SLASH_ATT);
			attType = Bonus.SLASH_ATT;
		}
		if (getBonus(Bonus.CRUSH_ATT) > highest) {
			highest = getBonus(Bonus.CRUSH_ATT);
			attType = Bonus.CRUSH_ATT;
		}
		return attType;
	}

	public Bonus getHighestDefenseBonus() {
		int highest = getBonus(Bonus.STAB_DEF);
		Bonus defType = Bonus.STAB_DEF;
		if (getBonus(Bonus.SLASH_DEF) > highest) {
			highest = getBonus(Bonus.SLASH_DEF);
			defType = Bonus.SLASH_DEF;
		}
		if (getBonus(Bonus.CRUSH_DEF) > highest) {
			highest = getBonus(Bonus.CRUSH_DEF);
			defType = Bonus.CRUSH_DEF;
		}
		return defType;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0;
	}

	public Tile getRespawnTile() {
		return respawnTile;
	}

	protected void setRespawnTile(Tile respawnTile) {
		this.respawnTile = Tile.of(respawnTile);
	}

	public boolean isUnderCombat() {
		return combat.hasTarget();
	}

	@Override
	public void setAttackedBy(Entity target) {
		super.setAttackedBy(target);
		if (target == combat.getTarget() && !(combat.getTarget() instanceof Familiar))
			lastAttackedByTarget = System.currentTimeMillis();
	}

	public boolean canBeAutoRetaliated() {
		return System.currentTimeMillis() - lastAttackedByTarget > lureDelay;
	}

	public boolean isForceWalking() {
		return forceWalk != null;
	}

	public Entity getTarget() {
		return combat.getTarget();
	}

	public void setTarget(Entity entity) {
		if (isForceWalking()) // if force walk not gonna get target
			return;
		combat.setTarget(entity);
		lastAttackedByTarget = System.currentTimeMillis();
	}

	public void removeTarget() {
		if (combat.getTarget() == null)
			return;
		combat.removeTarget();
	}

	public void forceWalkRespawnTile() {
		setForceWalk(respawnTile);
	}

	public void setForceWalk(Tile tile) {
		resetWalkSteps();
		forceWalk = tile;
	}

	public boolean hasForceWalk() {
		return forceWalk != null;
	}

	public boolean isRevenant() {
		return getDefinitions().getName().toLowerCase().contains("revenant");
	}

	public AttackStyle getAttackStyle() {
		return getCombatDefinitions().getAttackStyle();
		//		if (bonuses[2] > 0)
		//			return NPCCombatDefinitions.MAGE;
		//		if (bonuses[1] > 0)
		//			return NPCCombatDefinitions.RANGE;
		//		return NPCCombatDefinitions.MELEE;
	}

	public List<Entity> getPossibleTargets() {
		return getPossibleTargets(getCombatDefinitions().aggroDistance, canAggroNPCs);
	}

	public List<Entity> getPossibleTargets(boolean includeNpcs) {
		return getPossibleTargets(getCombatDefinitions().aggroDistance, includeNpcs);
	}

	public List<Entity> getPossibleTargets(int tileRadius) {
		return getPossibleTargets(tileRadius, canAggroNPCs);
	}

	public List<Entity> getPossibleTargets(int tileRadius, boolean includeNpcs) {
		boolean isNormallyPassive = !forceAgressive && getCombatDefinitions().getAgressivenessType() == AggressiveType.PASSIVE;
		ArrayList<Entity> possibleTarget = new ArrayList<>();
		possibleTarget.addAll(queryNearbyPlayersByTileRange(tileRadius, player -> {
			if (isRevenant() && player.hasEffect(Effect.REV_AGGRO_IMMUNE))
				return false;
			if (player.isDead()
				|| !canAggroPlayer(player)
				|| (isNormallyPassive && !player.hasEffect(Effect.AGGRESSION_POTION))
				|| (!player.hasEffect(Effect.AGGRESSION_POTION) && player.isDocile() && !ignoreDocile)
				|| player.getAppearance().isHidden()
				|| !WorldUtil.isInRange(getX(), getY(), getSize(), player.getX(), player.getY(), player.getSize(), getAggroDistance())
				|| (!forceMultiAttacked && (!isAtMultiArea() || !player.isAtMultiArea()) && player.getAttackedBy() != this && (player.inCombat() || player.getFindTargetDelay() > System.currentTimeMillis()))
				|| !lineOfSightTo(player, false)
				|| (!player.hasEffect(Effect.AGGRESSION_POTION) && !forceAgressive && !WildernessController.isAtWild(this.getTile()) && player.getSkills().getCombatLevelWithSummoning() >= getCombatLevel() * 2))
				return false;
			return true;
		}));
		if (includeNpcs && !isNormallyPassive) {
			possibleTarget.addAll(queryNearbyNPCsByTileRange(tileRadius, npc -> {
				if (npc == this
					|| npc.isDead()
					|| !canAggroNPC(npc)
					|| !WorldUtil.isInRange(getX(), getY(), getSize(), npc.getX(), npc.getY(), npc.getSize(), getAggroDistance())
					|| !npc.getDefinitions().hasAttackOption()
					|| (!forceMultiAttacked && (!isAtMultiArea() || !npc.isAtMultiArea()) && npc.getAttackedBy() != this && (npc.inCombat() || npc.getFindTargetDelay() > System.currentTimeMillis()))
					|| !lineOfSightTo(npc, false))
					return false;
				return true;
			}));
		}
		return possibleTarget;
	}

	private int getAggroDistance() {
		return forceAggroDistance > 0 ? forceAggroDistance : getCombatDefinitions().getAggroDistance();
	}

	public boolean canAggroPlayer(Player target) {
		return true;
	}

	public boolean canAggroNPC(NPC target) {
		return true;
	}

	public boolean checkAggressivity() {
		if ((this instanceof Familiar || !getDefinitions().hasAttackOption()) && !forceAgressive)
			return false;
		List<Entity> possibleTarget = getPossibleTargets();
		if (!possibleTarget.isEmpty()) {
			Entity target = possibleTarget.get(Utils.random(possibleTarget.size()));
			setTarget(target);
			target.setAttackedBy(target);
			//target.setFindTargetDelay(System.currentTimeMillis() + 10000); //TODO makes everything possible aggro to you
			return true;
		}
		return false;
	}

	public boolean isCantInteract() {
		return cantInteract;
	}

	public void setCantInteract(boolean cantInteract) {
		this.cantInteract = cantInteract;
		if (cantInteract)
			combat.reset();
	}

	public int getCapDamage() {
		return capDamage;
	}

	public void setCapDamage(int capDamage) {
		this.capDamage = capDamage;
	}

	public int getLureDelay() {
		return lureDelay;
	}

	public void setLureDelay(int lureDelay) {
		this.lureDelay = lureDelay;
	}

	public boolean isCantFollowUnderCombat() {
		return cantFollowUnderCombat;
	}

	public void setCantFollowUnderCombat(boolean canFollowUnderCombat) {
		cantFollowUnderCombat = canFollowUnderCombat;
	}

	public Transformation getNextTransformation() {
		return nextTransformation;
	}

	@Override
	public String toString() {
		return getDefinitions().getName() + " - " + id + " - " + getX() + " " + getY() + " " + getPlane();
	}

	public boolean isForceAgressive() {
		return forceAgressive;
	}

	public NPC setForceAgressive(boolean forceAgressive) {
		this.forceAgressive = forceAgressive;
		return this;
	}

	public void setForceAggroDistance(int forceAggroDistance) {
		this.forceAggroDistance = forceAggroDistance;
	}

	public boolean isForceFollowClose() {
		return forceFollowClose;
	}

	public void setForceFollowClose(boolean forceFollowClose) {
		this.forceFollowClose = forceFollowClose;
	}

	public boolean isForceMultiAttacked() {
		return forceMultiAttacked;
	}

	public void setForceMultiAttacked(boolean forceMultiAttacked) {
		this.forceMultiAttacked = forceMultiAttacked;
	}

	public boolean shouldRandomWalk() {
		return randomWalk;
	}

	public void setRandomWalk(boolean forceRandomWalk) {
		randomWalk = forceRandomWalk;
	}

	public String getCustomName() {
		return name;
	}

	public void setPermName(String string) {
		name = getDefinitions().getName().equals(string) ? null : string;
		permName = true;
	}

	public void setName(String string) {
		name = getDefinitions().getName().equals(string) ? null : string;
		changedName = true;
	}

	public int getCustomCombatLevel() {
		return combatLevel;
	}

	public int getCombatLevel() {
		return combatLevel >= 0 ? combatLevel : getDefinitions().combatLevel;
	}

	public String getName() {
		return name != null ? name : getDefinitions().getName();
	}

	public String getName(Player player) {
		return name != null ? name : getDefinitions().getName(player.getVars());
	}

	public void setCombatLevel(int level) {
		combatLevel = getDefinitions().combatLevel == level ? -1 : level;
		changedCombatLevel = true;
	}

	public boolean hasChangedName() {
		return permName || changedName;
	}

	public boolean hasChangedCombatLevel() {
		return changedCombatLevel;
	}

	public boolean isSpawned() {
		return spawned;
	}

	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}

	public boolean isNoDistanceCheck() {
		return noDistanceCheck;
	}

	public void setNoDistanceCheck(boolean noDistanceCheck) {
		this.noDistanceCheck = noDistanceCheck;
	}

	public boolean withinDistance(Player tile, int distance) {
		return !hidden && super.withinDistance(tile.getTile(), distance);
	}

	/**
	 * Gets the locked.
	 *
	 * @return The locked.
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Sets the locked.
	 *
	 * @param locked
	 *            The locked to set.
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setLockedForTicks(int ticks) {
		WorldTasks.scheduleTimer(i -> {
			if(i==0)
				this.locked = true;
			if(i==ticks) {
				this.locked = false;
				return false;
			}
			return true;
		});
	}

	@Override
	public boolean canMove(Direction dir) {
		return true;
	}

	public boolean isIntelligentRouteFinder() {
		return intelligentRoutefinder;
	}

	public void setIntelligentRouteFinder(boolean intelligentRoutefinder) {
		this.intelligentRoutefinder = intelligentRoutefinder;
	}

	public int getAttackSpeed() {
		return NPCDefinitions.getDefs(id).getAttackDelay()+1;
	}

	public boolean isIgnoreDocile() {
		return ignoreDocile;
	}

	public void setIgnoreDocile(boolean ignoreDocile) {
		this.ignoreDocile = ignoreDocile;
	}

	private boolean isTzhaarMonster() {
		switch(getId()) {
		case 2591:
		case 2592:
		case 2594:
		case 2595:
		case 2596:
		case 2597:
		case 2600:
		case 2601:
		case 2602:
		case 2603:
		case 2605:
		case 2606:
		case 2607:
		case 2608:
		case 2609:
		case 2611:
		case 2614:
		case 2615:
		case 2616:
		case 2617:
		case 2628:
		case 2629:
		case 2630:
		case 2631:
		case 2632:
		case 2734:
		case 2735:
		case 2736:
		case 2737:
		case 2738:
		case 2739:
		case 2740:
		case 2741:
		case 2742:
		case 2743:
		case 2744:
		case 2745:
		case 2746:
		case 7767:
		case 7768:
		case 7769:
		case 7770:
		case 7771:
		case 7772:
		case 7773:
		case 15170:
		case 15171:
		case 15177:
		case 15178:
		case 15181:
		case 15182:
		case 15183:
		case 15184:
		case 15186:
		case 15187:
		case 15188:
		case 15189:
		case 15190:
		case 15191:
		case 15192:
		case 15196:
		case 15197:
		case 15198:
		case 15199:
		case 15201:
		case 15202:
		case 15203:
		case 15204:
		case 15205:
		case 15206:
		case 15207:
		case 15208:
		case 15209:
		case 15210:
		case 15211:
		case 15212:
		case 15213:
		case 15214:
			return true;
		}
		return false;
	}

	public boolean canBeAttackedBy(Player player) {
		if (getId() == 879 || getId() == 14578)
			if (player.getEquipment().getWeaponId() != 2402 && player.getCombatDefinitions().getSpell() != null && !PolyporeStaff.isWielding(player)) {
				player.sendMessage("I'd better wield Silverlight first.");
				return false;
			}
		return true;
	}

	public boolean canAggroNPCs() {
		return canAggroNPCs;
	}

	public void setCanAggroNPCs(boolean canAggroNPCs) {
		this.canAggroNPCs = canAggroNPCs;
	}

	public boolean blocksOtherNpcs() {
		return true;
	}

	public boolean isIgnoreNPCClipping() {
		return ignoreNPCClipping;
	}

	public void setIgnoreNPCClipping(boolean ignoreNPCClipping) {
		this.ignoreNPCClipping = ignoreNPCClipping;
	}
	
	private Sound playSound(Sound sound) {
		World.playSound(getTile(), sound);
		return sound;
	}
	
	private Sound playSound(int soundId, int delay, SoundType type) {
		return playSound(new Sound(soundId, delay, type));
	}
	
	public void jingle(int jingleId, int delay) {
		playSound(jingleId, delay, SoundType.JINGLE);
	}
	
	public void jingle(int jingleId) {
		playSound(jingleId, 0, SoundType.JINGLE);
	}
	
	public void musicTrack(int trackId, int delay, int volume) {
		playSound(trackId, delay, SoundType.MUSIC).volume(volume);
	}
	
	public void musicTrack(int trackId, int delay) {
		playSound(trackId, delay, SoundType.MUSIC);
	}
	
	public void musicTrack(int trackId) {
		musicTrack(trackId, 100);
	}
	
	public void soundEffect(int soundId, int delay) {
		playSound(soundId, delay, SoundType.EFFECT);
	}
	
	public void soundEffect(int soundId) {
		soundEffect(soundId, 0);
	}
	
	public void voiceEffect(int voiceId, int delay) {
		playSound(voiceId, delay, SoundType.VOICE);
	}
	
	public void voiceEffect(int voiceId) {
		voiceEffect(voiceId, 0);
	}

	public NPCBodyMeshModifier getBodyMeshModifier() {
		return bodyMeshModifier;
	}

	public void setBodyMeshModifier(NPCBodyMeshModifier meshModifier) {
		if (meshModifier == null) {
			bodyMeshModifier = NPCBodyMeshModifier.RESET;
			return;
		}
		bodyMeshModifier = meshModifier;
	}
	
	public NPCBodyMeshModifier modifyMesh() {
		bodyMeshModifier = new NPCBodyMeshModifier(getDefinitions());
		return bodyMeshModifier;
	}
	
	public void resetMesh() {
		setBodyMeshModifier(NPCBodyMeshModifier.RESET);
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isLoadsUpdateZones() {
		return loadsUpdateZones;
	}

    public void persistBeyondCutscene() {
		getTempAttribs().setB("persistBeyondCutscene", true);
    }

	public boolean persistsBeyondCutscene() {
		return getTempAttribs().getB("persistBeyondCutscene");
	}

    public void stopAll() {
		getActionManager().forceStop();
		getInteractionManager().forceStop();
    }
}