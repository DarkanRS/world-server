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
import com.rs.engine.pathfinder.*;
import com.rs.engine.pathfinder.collision.CollisionStrategyType;
import com.rs.engine.thread.AsyncTaskExecutor;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.bosses.godwars.GodwarsController;
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.combat.PolyporeStaffKt;
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager;
import com.rs.game.content.quests.elderkiln.TokkulZoKt;
import com.rs.game.content.skills.hunter.BoxHunterType;
import com.rs.game.content.skills.slayer.SlayerMonsters;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.combat.NPCCombat;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AggressiveType;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.Skill;
import com.rs.game.model.entity.player.Bank;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.AuraManager;
import com.rs.game.model.item.ItemsContainer;
import com.rs.game.tasks.TaskInformation;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.NPCDeathEvent;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.events.NPCKillParticipatedEvent;
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
	private Tile forceWalk;
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
	private transient int attackRange;

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
		setCollisionStrategyType((getDefinitions().walkMask & 0x4) != 0 ? CollisionStrategyType.WATER : CollisionStrategyType.NORMAL);
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
		combatLevels = getCombatDefinitions().getLevels();
		attackRange = getCombatDefinitions().getAttackRange();
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
		combatLevels = getCombatDefinitions().getLevels();
	}

	public void transformIntoNPC(int id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
	}

	public void setNPC(int id) {
		this.id = id;
		size = getDefinitions().size;
		combatLevels = getCombatDefinitions().getLevels();
		attackRange = getCombatDefinitions().getAttackRange();
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

	public int getAttackRange() {
		return attackRange;
	}

	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
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
			int currentLevel = getCombatLevel(skill);
			int normalLevel = getCombatDefinitions().getLevel(skill);
			if (currentLevel > normalLevel)
				setStat(skill, currentLevel - 1);
			else if (currentLevel < normalLevel)
				setStat(skill, Utils.clampI(currentLevel + 1, 0, normalLevel));
		}
	}

	public void processNPC() {
		if (isDead() || isLocked())
			return;
		//Restore combat stats
		if (getTickCounter() % 100 == 0)
			restoreTick();
		if (!combat.process() && routeEvent == null) {
			if (getTickCounter() % 3 == 0 && !isForceWalking() && !cantInteract && !checkAggressivity() && !hasEffect(Effect.FREEZE)) {
				randomWalk();
			}
		}
		if (isForceWalking())
			if (!hasEffect(Effect.FREEZE))
				if (getX() != forceWalk.getX() || getY() != forceWalk.getY()) {
					if (!hasWalkSteps()) {
						Route route = RouteFinderKt.routeEntityToTile(this, forceWalk, 25);
						if (route.getFailed()) {
							tele(Tile.of(forceWalk));
							forceWalk = null;
						} else
							RouteFinderKt.walkRoute(this, route, true);
					}
				} else
					// walked till forcewalk place
					forceWalk = null;
	}

	protected void randomWalk() {
		if (!hasWalkSteps() && shouldRandomWalk()) {
			boolean can = Math.random() > 0.9;
			if (can) {
				int moveX = Utils.random(getDefinitions().hasAttackOption() ? 4 : 2, getDefinitions().hasAttackOption() ? 8 : 4);
				int moveY = Utils.random(getDefinitions().hasAttackOption() ? 4 : 2, getDefinitions().hasAttackOption() ? 8 : 4);
				if (Utils.random(2) == 0) moveX = -moveX;
				if (Utils.random(2) == 0) moveY = -moveY;
				resetWalkSteps();
				DumbRouteFinder.addDumbPathfinderSteps(this, respawnTile.transform(moveX, moveY, 0), getDefinitions().hasAttackOption() ? 7 : 3, getCollisionStrategy());
				if (Utils.getDistance(this.getTile(), respawnTile) > 3 && !getDefinitions().hasAttackOption())
					DumbRouteFinder.addDumbPathfinderSteps(this, respawnTile, getDefinitions().hasAttackOption() ? 7 : 3, getCollisionStrategy());
			}
		}
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
        return switch (npcId) {
            case 11226 -> true;
            case 3341, 3342, 3343 -> true;
            default -> false;
        };
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
			if (TokkulZoKt.depleteTokkulZo(player) && hit.getDamage() > 0 && isTzhaarMonster())
				hit.setDamage((int) (hit.getDamage() * 1.1));
		}
		if (hit.getDamage() >= 200) {
			Bonus bonus = switch(hit.getLook()) {
				case MELEE_DAMAGE -> Bonus.ABSORB_MELEE;
				case RANGE_DAMAGE -> Bonus.ABSORB_RANGE;
				default -> Bonus.ABSORB_MAGIC;
			};
			int reducedDamage = hit.getDamage() * getCombatBonus(bonus) / 100;
			if (reducedDamage > 0) {
				hit.setDamage(hit.getDamage() - reducedDamage);
				hit.addSoaking(reducedDamage);
			}
		}
		handlePostHit(hit);
	}

	@Override
	public void handlePreHitOut(Entity target, Hit hit) {

	}

	@Override
	public void handlePostHit(Hit hit) {
		super.handlePostHit(hit);
		if (capDamage != -1 && hit.getDamage() > capDamage)
			hit.setDamage(capDamage);
		if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
			if (hit.getSource() instanceof Player player && player.getAuraManager().isActivated(AuraManager.Aura.DARK_MAGIC) && hit.getData("darkMagicBleed") == null && Utils.random(100) < 13) {
				for (int i = 1; i <= 4; i++) {
					int finalI = i;
					getTasks().schedule(2 * i, () -> applyHit(new Hit(hit.getSource(), 26 - finalI, hit.getLook()).setData("darkMagicBleed", true)));
				}
			}
		}
	}

	@Override
	public void reset() {
		super.reset();
		getInteractionManager().forceStop();
		setFaceAngle(getRespawnDirection());
		combat.reset();
		combatLevels = getCombatDefinitions().getLevels(); // back to real bonuses
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
			if (tick >= ticks) {
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
		respawnTask = WorldTasks.schedule(time < 0 ? getCombatDefinitions().getRespawnDelay() : time, this::spawn);
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
		if (getCombatDefinitions().getRespawnAnim() != -1)
			anim(getCombatDefinitions().getRespawnAnim());
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
	
	public int getCombatLevel(Skill skill) {
		if (skill == null) return 1;
		return combatLevels == null ? 1 : combatLevels.get(skill);
	}

	public int getAttackLevel() {
		return getCombatLevel(Skill.ATTACK);
	}

	public int getDefenseLevel() {
		return getCombatLevel(Skill.DEFENSE);
	}

	public int getStrengthLevel() {
		return getCombatLevel(Skill.STRENGTH);
	}

	public int getRangeLevel() {
		return getCombatLevel(Skill.RANGE);
	}

	public int getMagicLevel() {
		return getCombatLevel(Skill.MAGE);
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
		getRecievedDamageEntities().forEach(entity -> PluginManager.handle(new NPCKillParticipatedEvent(this, entity)));
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0) {
				setNextAnimation(new Animation(defs.getDeathEmote()));
				soundEffect(source, getCombatDefinitions().getDeathSound(), true);
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
		} catch (Exception | Error e) {
			e.printStackTrace();
		}
    }

	public void drop() {
		try {
			Player killer = getMostDamageReceivedSourcePlayer();
			if (killer == null)
				return;
			drop(killer);
		} catch (Exception | Error e) {
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
        return switch (itemId) {
            //KBD
            //Slayer Drops
            //Dagganoth Drops
            //Glacors
            //KQ
            //Tormented Demons
            //Original GWD
            case 25312, 25314, 25316, 25318, 11286, 11732, 15486, 11235, 4151, 21369, 6731, 6733, 6735, 6737, 6739, 21777, 21787, 21790, 21793, 3140, 14484, 11702, 11704, 11706, 11708, 11718, 11722, 11720, 11724, 11726, 11728, 11716, 11730 ->
                    true;
            default -> false;
        };
	}

	public void sendDrop(Player player, Item item) {
		Player dropTo = player;

		if (player.isLootSharing() && player.getAccount().getSocial().getCurrentFriendsChat() != null && !TreasureTrailsManager.isScroll(item.getId())) {
			ArrayList<Player> possible = player.getNearbyFCMembers(this);
			if (possible.size() > 0) {
				dropTo = possible.get(Utils.random(possible.size()));
				for (Player p : possible)
					if (!p.getUsername().equals(dropTo.getUsername()))
						p.sendMessage(dropTo.getDisplayName()+" has received: "+item.getAmount()+" "+item.getName()+".");
					else
						p.sendMessage("<col=006600>You received: "+item.getAmount()+" "+item.getName()+".");
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
		AsyncTaskExecutor.execute(() -> {
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

	public int getLevelForStyle(CombatStyle style) {
		int maxHit = getAttackLevel();
		if (style == CombatStyle.RANGE)
			maxHit = getRangeLevel();
		else if (style == CombatStyle.MAGIC)
			maxHit = getMagicLevel();
		return maxHit;
	}

	public int lowerDefense(double multiplier, double maxDrain) {
		return lowerStat(Skill.DEFENSE, multiplier, maxDrain);
	}

	public int lowerDefense(int drain, double maxDrain) {
		return lowerStat(Skill.DEFENSE, drain, maxDrain);
	}

	public int lowerAttack(double multiplier, double maxDrain) {
		return lowerStat(Skill.ATTACK, multiplier, maxDrain);
	}

	public int lowerAttack(int drain, double maxDrain) {
		return lowerStat(Skill.ATTACK, drain, maxDrain);
	}

	public int lowerStrength(double multiplier, double maxDrain) {
		return lowerStat(Skill.STRENGTH, multiplier, maxDrain);
	}

	public int lowerStrength(int drain, double maxDrain) {
		return lowerStat(Skill.STRENGTH, drain, maxDrain);
	}

	public int lowerMagic(double multiplier, double maxDrain) {
		return lowerStat(Skill.MAGE, multiplier, maxDrain);
	}

	public int lowerMagic(int drain, double maxDrain) {
		return lowerStat(Skill.MAGE, drain, maxDrain);
	}
	
	public int lowerRange(double multiplier, double maxDrain) {
		return lowerStat(Skill.RANGE, multiplier, maxDrain);
	}

	public int lowerRange(int drain, double maxDrain) {
		return lowerStat(Skill.RANGE, drain, maxDrain);
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

	public int lowerStat(Skill stat, double multiplier, double maxDrain) {
		if (combatLevels != null)
			setStat(stat, Utils.clampI((int) (getStat(stat) - (getStat(stat) * multiplier)), (int) ((double) getCombatDefinitions().getLevels().get(stat) * maxDrain), getStat(stat)));
		return getStat(stat);
	}

	public int lowerStat(Skill stat, int levelDrain, double maxDrain) {
		if (combatLevels != null)
			setStat(stat, Utils.clampI(getStat(stat) - levelDrain, (int) ((double) getCombatDefinitions().getLevels().get(stat) * maxDrain), getStat(stat)));
		return getStat(stat);
	}

	public int getCombatBonus(Bonus bonus) {
		if (getCombatDefinitions().hasOverriddenBonuses())
			return getCombatDefinitions().getBonus(bonus);
		else
			return NPCDefinitions.getDefs(id).getBonus(bonus);
	}

	public void resetHP() {
		setHitpoints(getMaxHitpoints());
	}

	public Bonus getHighestAttackBonus() {
		int highest = getCombatBonus(Bonus.STAB_ATT);
		Bonus attType = Bonus.STAB_ATT;
		if (getCombatBonus(Bonus.SLASH_ATT) > highest) {
			highest = getCombatBonus(Bonus.SLASH_ATT);
			attType = Bonus.SLASH_ATT;
		}
		if (getCombatBonus(Bonus.CRUSH_ATT) > highest) {
			highest = getCombatBonus(Bonus.CRUSH_ATT);
			attType = Bonus.CRUSH_ATT;
		}
		return attType;
	}

	public Bonus getHighestDefenseBonus() {
		int highest = getCombatBonus(Bonus.STAB_DEF);
		Bonus defType = Bonus.STAB_DEF;
		if (getCombatBonus(Bonus.SLASH_DEF) > highest) {
			highest = getCombatBonus(Bonus.SLASH_DEF);
			defType = Bonus.SLASH_DEF;
		}
		if (getCombatBonus(Bonus.CRUSH_DEF) > highest) {
			highest = getCombatBonus(Bonus.CRUSH_DEF);
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

	public Entity getCombatTarget() {
		return combat.getTarget();
	}

	public void setCombatTarget(Entity entity) {
		if (isForceWalking()) // if force walk not gonna get target
			return;
		combat.setTarget(entity);
		lastAttackedByTarget = System.currentTimeMillis();
	}

	public void removeCombatTarget() {
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

	public CombatStyle getCombatStyle() {
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
            return !player.isDead()
                    && canAggroPlayer(player)
                    && (!isNormallyPassive || player.hasEffect(Effect.AGGRESSION_POTION))
                    && (player.hasEffect(Effect.AGGRESSION_POTION) || !player.isDocile() || ignoreDocile)
                    && !player.getAppearance().isHidden() && !player.isTrulyHidden()
                    && WorldUtil.isInRange(getX(), getY(), getSize(), player.getX(), player.getY(), player.getSize(), getAggroDistance())
                    && (forceMultiAttacked || (isAtMultiArea() && player.isAtMultiArea()) || player.getAttackedBy() == this || (!player.inCombat() && player.getFindTargetDelay() <= System.currentTimeMillis()))
                    && lineOfSightTo(player, false)
                    && (player.hasEffect(Effect.AGGRESSION_POTION) || forceAgressive || WildernessController.isAtWild(this.getTile()) || player.getSkills().getCombatLevelWithSummoning() < getCombatLevel() * 2);
        }));
		if (includeNpcs && !isNormallyPassive) {
			possibleTarget.addAll(queryNearbyNPCsByTileRange(tileRadius, npc -> npc != this
                    && !npc.isDead()
                    && canAggroNPC(npc)
                    && WorldUtil.isInRange(getX(), getY(), getSize(), npc.getX(), npc.getY(), npc.getSize(), getAggroDistance())
                    && npc.getDefinitions().hasAttackOption()
                    && (forceMultiAttacked || (isAtMultiArea() && npc.isAtMultiArea()) || npc.getAttackedBy() == this || (!npc.inCombat() && npc.getFindTargetDelay() <= System.currentTimeMillis()))
                    && lineOfSightTo(npc, false)));
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
			setCombatTarget(target);
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
		return "["+getDefinitions().getName() + " - id: " + id + " @ (" + getX() + "," + getY() + "," + getPlane()+")]";
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
        return switch (getId()) {
            case 2591, 2592, 2594, 2595, 2596, 2597, 2600, 2601, 2602, 2603, 2605, 2606, 2607, 2608, 2609, 2611, 2614, 2615, 2616, 2617, 2628, 2629, 2630, 2631, 2632, 2734, 2735, 2736, 2737, 2738, 2739, 2740, 2741, 2742, 2743, 2744, 2745, 2746, 7767, 7768, 7769, 7770, 7771, 7772, 7773, 15170, 15171, 15177, 15178, 15181, 15182, 15183, 15184, 15186, 15187, 15188, 15189, 15190, 15191, 15192, 15196, 15197, 15198, 15199, 15201, 15202, 15203, 15204, 15205, 15206, 15207, 15208, 15209, 15210, 15211, 15212, 15213, 15214 ->
                    true;
            default -> false;
        };
    }

	public boolean canBeAttackedBy(Player player) {
		if (getId() == 879 || getId() == 14578)
			if (player.getEquipment().getWeaponId() != 2402 && player.getCombatDefinitions().getSpell() != null && !PolyporeStaffKt.usingPolypore(player)) {
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