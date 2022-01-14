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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.cores.CoresManager;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.combat.NPCCombat;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AggressiveType;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.pathing.ClipType;
import com.rs.game.pathing.Direction;
import com.rs.game.pathing.DumbRouteFinder;
import com.rs.game.pathing.FixedTileStrategy;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.pathing.RouteFinder;
import com.rs.game.player.Bank;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.combat.PolyporeStaff;
import com.rs.game.player.content.skills.dungeoneering.DungeonRewards.HerbicideSetting;
import com.rs.game.player.content.skills.hunter.BoxHunterType;
import com.rs.game.player.content.skills.prayer.Burying;
import com.rs.game.player.content.skills.prayer.Burying.Bone;
import com.rs.game.player.content.skills.slayer.SlayerMonsters;
import com.rs.game.player.content.world.regions.dungeons.TzHaar;
import com.rs.game.player.controllers.GodwarsController;
import com.rs.game.player.controllers.WildernessController;
import com.rs.game.player.managers.TreasureTrailsManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.file.FileManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
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

public class NPC extends Entity {

	private int id;
	private WorldTile respawnTile;
	private boolean randomWalk;
	private int[] levels;
	private boolean spawned;
	private transient NPCCombat combat;
	private transient boolean blocksOtherNPCs = true;
	private transient boolean ignoreNPCClipping;
	public WorldTile forceWalk;
	private int size;

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
	protected transient ConcurrentHashMap<Object, Object> temporaryAttributes;
	// name changing masks
	private String name;
	private transient boolean changedName;
	private transient boolean permName;
	private int combatLevel;
	private transient boolean changedCombatLevel;
	private transient boolean locked;
	private transient boolean skipWalkStep;

	public boolean switchWalkStep() {
		return skipWalkStep = !skipWalkStep;
	}

	private boolean intelligentRoutefinder;
	public boolean maskTest;

	public NPC(int id, WorldTile tile, Direction direction, boolean permaDeath) {
		super(tile);
		this.id = id;
		respawnTile = new WorldTile(tile);
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
		if (getName().contains("impling")) {
			setRandomWalk(true);
			setClipType(ClipType.FLYING);
		}
		if (getDefinitions().combatLevel >= 200)
			setIgnoreDocile(true);
		levels = NPCCombatDefinitions.getDefs(id).getLevels();
		combat = new NPCCombat(this);
		capDamage = -1;
		lureDelay = 12000;
		// npc is inited on creating instance
		initEntity();
		World.addNPC(this);
		World.updateEntityRegion(this);
		// npc is started on creating instance
		loadMapRegions();
		checkMultiArea();
	}

	public NPC(int id, WorldTile tile, boolean permaDeath) {
		this(id,tile, null, permaDeath);
	}


	public NPC(int id, WorldTile tile) {
		this(id, tile, false);
	}

	public boolean walksOnWater() {
		return (getDefinitions().walkMask & 0x4) != 0;
	}

	public void walkToAndExecute(WorldTile startTile, Runnable event) {
		int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(), getSize(), new FixedTileStrategy(startTile.getX(), startTile.getY()), true);
		int[] bufferX = RouteFinder.getLastPathBufferX();
		int[] bufferY = RouteFinder.getLastPathBufferY(); //TODO expensive call for cutscenes
		if (steps == -1)
			return;
		for (int i = steps - 1; i >= 0; i--)
			if (!addWalkSteps(bufferX[i], bufferY[i], 25, true, true))
				break;
		setRouteEvent(new RouteEvent(startTile, event));
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || nextTransformation != null || changedCombatLevel || changedName || maskTest || permName;
	}

	public void resetLevels() {
		levels = NPCCombatDefinitions.getDefs(id).getLevels();
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
		levels = NPCCombatDefinitions.getDefs(id).getLevels();
	}

	public void setLevels(int[] levels) {
		this.levels = levels;
	}

	public void resetDirection() {
		setNextFaceWorldTile(null);
		setNextFaceEntity(null);
		setFaceAngle(getRespawnDirection());
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		nextTransformation = null;
		changedName = false;
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
		return World.checkNPCClip(this, dir);
	}

	public void processNPC() {
		if (isDead() || locked || World.getPlayersInRegionRange(getRegionId()).isEmpty())
			return;
		if (!combat.process() && routeEvent == null)
			if (!isForceWalking() && !cantInteract && !checkAggressivity() && !hasEffect(Effect.FREEZE))
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
						if (Utils.getDistance(this, respawnTile) > 3 && !getDefinitions().hasAttackOption())
							DumbRouteFinder.addDumbPathfinderSteps(this, respawnTile, getDefinitions().hasAttackOption() ? 7 : 3, getClipType());
					}
				}
		if (isForceWalking())
			if (!hasEffect(Effect.FREEZE))
				if (getX() != forceWalk.getX() || getY() != forceWalk.getY()) {
					if (!hasWalkSteps()) {
						int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(), getSize(), new FixedTileStrategy(forceWalk.getX(), forceWalk.getY()), true);
						int[] bufferX = RouteFinder.getLastPathBufferX();
						int[] bufferY = RouteFinder.getLastPathBufferY();
						for (int i = steps - 1; i >= 0; i--)
							if (!addWalkSteps(bufferX[i], bufferY[i], 25, true, true))
								break;
					}
					if (!hasWalkSteps()) { // failing finding route
						setNextWorldTile(new WorldTile(forceWalk));
						forceWalk = null; // so ofc reached forcewalk place
					}
				} else
					// walked till forcewalk place
					forceWalk = null;
	}

	@Override
	public void processEntity() {
		super.processEntity();
		processNPC();
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
		setFaceAngle(getRespawnDirection());
		combat.reset();
		levels = NPCCombatDefinitions.getDefs(id).getLevels(); // back to real bonuses
		forceWalk = null;
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		setFinished(true);
		World.updateEntityRegion(this);
		World.removeNPC(this);
	}

	public void setRespawnTask() {
		setRespawnTask(-1);
	}

	/**
	 * NPC becomes set to finish/dissapear when the player either
	 * 1. Leaves the region
	 * 2. Logs out
	 *
	 * Reason: For quests
	 * @param p
	 */
	public void lingerForPlayer(Player p) {
		WorldTasks.schedule(new WorldTask() {
			int tick;
			@Override
			public void run() {
				if (tick == 3)
					if (p.hasFinished())
						;
					else
						for (Player rPlayer : World.getPlayersInRegion(getRegionId()))
							if (rPlayer == p)
								tick = 0;
				if (tick == 5) {
					if (!hasFinished())
						finish();
					stop();
				}
				tick++;
			}
		}, 0, 1);
	}

	/**
	 * For quests
	 * @param ticks
	 */
	public void finishAfterTicks(final int ticks) {
		WorldTasks.schedule(new WorldTask() {
			int tick;
			@Override
			public void run() {
				if (tick == ticks) {
					if (!hasFinished())
						finish();
					stop();
				}
				tick++;
			}
		}, 0, 1);
	}

	public void setRespawnTask(int time) {
		if (!hasFinished()) {
			reset();
			setLocation(respawnTile);
			finish();
		}
		CoresManager.schedule(() -> spawn(), time < 0 ? getCombatDefinitions().getRespawnDelay() : time);
	}

	public void deserialize() {
		if (combat == null)
			combat = new NPCCombat(this);
		spawn();
	}

	public void spawn() {
		timeLastSpawned = System.currentTimeMillis();
		setFinished(false);
		World.addNPC(this);
		setLastRegionId(0);
		World.updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
		onRespawn();
	}

	public void onRespawn() {

	}

	public long timeSinceSpawned() {
		return System.currentTimeMillis() - timeLastSpawned;
	}

	public NPCCombat getCombat() {
		return combat;
	}

	public int getAttackLevel() {
		return levels == null ? 0 : levels[0];
	}

	public int getDefenseLevel() {
		return levels == null ? 0 : levels[1];
	}

	public int getStrengthLevel() {
		return levels == null ? 0 : levels[2];
	}

	public int getRangeLevel() {
		return levels == null ? 0 : levels[3];
	}

	public int getMagicLevel() {
		return levels == null ? 0 : levels[4];
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		combat.removeTarget();
		setNextAnimation(null);
		PluginManager.handle(new NPCDeathEvent(this, source));
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(defs.getDeathEmote()));
				else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player player)
						player.getControllerManager().processNPCDeath(NPC.this);
					drop();
					reset();
					setLocation(respawnTile);
					finish();
					if (!isSpawned())
						setRespawnTask();
					if (source.getAttackedBy() == NPC.this) { //no need to wait after u kill
						source.setAttackedByDelay(0);
						source.setAttackedBy(null);
						source.setFindTargetDelay(0);
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
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

			if (DropSets.getDropSet(id) != null && EffigyDrop.dropEffigy(getDefinitions().combatLevel))
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
		if (yellDrop(item.getId())) {
			World.sendWorldMessage("<img=4><shad=000000><col=00FF00>" + dropTo.getDisplayName() + " has just recieved a " + item.getName() + " drop from " + getDefinitions().getName() + "!", false);
			FileManager.writeToFile("droplog.txt", dropTo.getDisplayName() + " has just recieved a " + item.getName() + " drop from " + getDefinitions().getName() + "!");
		}

		final int size = getSize();

		if (dropTo.getInventory().containsItem(18337, 1)) {
			Bone bone = Bone.forId(item.getId());
			if (bone != null && bone != Bone.ACCURSED_ASHES && bone != Bone.IMPIOUS_ASHES && bone != Bone.INFERNAL_ASHES) {
				dropTo.getSkills().addXp(Constants.PRAYER, bone.getExperience());
				Burying.handleNecklaces(dropTo, bone.getId());
				return;
			}
		}

		if (dropTo.getNSV().getB("sendingDropsToBank")) {
			if (item.getDefinitions().isNoted())
				item.setId(item.getDefinitions().certId);
			sendDropDirectlyToBank(dropTo, item);
			return;
		}

		//final WorldTile tile = new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane());
		int value = item.getDefinitions().getValue() * item.getAmount();
		if (value > 90000 || item.getDefinitions().name.contains("Scroll box") || item.getDefinitions().name.contains(" defender") || yellDrop(item.getId()))
			player.sendMessage("<col=cc0033>You received: "+ item.getAmount() + " " + item.getDefinitions().getName()); //
		//player.getPackets().sendTileMessage("<shad=000000>"+item.getDefinitions().getName() + " (" + item.getAmount() + ")", tile, 20000, 50, 0xFF0000);

		switch (item.getId()) {
		case 12158:
		case 12159:
		case 12160:
		case 12161:
		case 12162:
		case 12163:
		case 12168:
			if (dropTo.getInventory().containsItem(25350, 1) && dropTo.getInventory().hasRoomFor(item)) {
				dropTo.getInventory().addItem(item);
				return;
			}
			break;
		case 995:
			if (dropTo.getInventory().containsItem(25351, 1) && dropTo.getInventory().hasRoomFor(item)) {
				dropTo.getInventory().addItem(item);
				return;
			}
			break;
		}

		if (dropTo.getInventory().containsItem(19675, 1))
			for (HerbicideSetting setting : dropTo.herbicideSettings)
				if (item.getId() == setting.getHerb().getHerbId() && (dropTo.getSkills().getLevel(Constants.HERBLORE) >= setting.getHerb().getLevel())) {
					dropTo.getSkills().addXp(Constants.HERBLORE, setting.getHerb().getExperience() * 2);
					return;
				}

		PluginManager.handle(new NPCDropEvent(dropTo, item));
		World.addGroundItem(item, new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane()), dropTo, true, 60);
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
		ItemsContainer<Item> dropCollection = getDropsFor(npcId, npcAmount, player.getEquipment().getRingId() != -1 && ItemDefinitions.getDefs(player.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth"));
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
		player.getPackets().setIFTargetParams(new IFTargetParams(762, 95, 0, 516).enableRightClickOptions(0,1,2,3,4,5,6,9).setDepth(2).enableDrag());
		player.getVars().setVarBit(4893, 1);
		player.getVars().syncVarsToClient();
	}

	@Override
	public int getSize() {
		return size;
	}

	public int getMaxHit() {
		return getCombatDefinitions().getMaxHit();
	}

	public int getMaxHit(AttackStyle style) {
		int maxHit = getAttackLevel();
		if (style == AttackStyle.RANGE)
			maxHit = getRangeLevel();
		else if (style == AttackStyle.MAGE)
			maxHit = getMagicLevel();
		return maxHit;
	}

	public void lowerDefense(float multiplier) {
		lowerStat(NPCCombatDefinitions.DEFENSE, multiplier);
	}

	public void lowerDefense(int drain) {
		lowerStat(NPCCombatDefinitions.DEFENSE, drain);
	}

	public void lowerAttack(float multiplier) {
		lowerStat(NPCCombatDefinitions.ATTACK, multiplier);
	}

	public void lowerAttack(int drain) {
		lowerStat(NPCCombatDefinitions.ATTACK, drain);
	}

	public void lowerStrength(float multiplier) {
		lowerStat(NPCCombatDefinitions.STRENGTH, multiplier);
	}

	public void lowerStrength(int drain) {
		lowerStat(NPCCombatDefinitions.STRENGTH, drain);
	}

	public void lowerMagic(float multiplier) {
		lowerStat(NPCCombatDefinitions.MAGIC, multiplier);
	}

	public void lowerMagic(int drain) {
		lowerStat(NPCCombatDefinitions.MAGIC, drain);
	}

	public void lowerStat(int stat, float multiplier) {
		if (levels != null)
			levels[NPCCombatDefinitions.DEFENSE] -= levels[NPCCombatDefinitions.DEFENSE] * multiplier;
	}

	public void lowerStat(int stat, int levelDrain) {
		if (levels != null) {
			levels[NPCCombatDefinitions.DEFENSE] -= levelDrain;
			if (levels[NPCCombatDefinitions.DEFENSE] < 0)
				levels[NPCCombatDefinitions.DEFENSE] = 0;
		}
	}

	public int getBonus(Bonus bonus) {
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

	public WorldTile getRespawnTile() {
		return respawnTile;
	}

	protected void setRespawnTile(WorldTile respawnTile) {
		this.respawnTile = respawnTile;
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

	public void setForceWalk(WorldTile tile) {
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
		return getPossibleTargets(canAggroNPCs);
	}

	public List<Entity> getPossibleTargets(boolean includeNpcs) {
		ArrayList<Entity> possibleTarget = new ArrayList<>();
		for (int regionId : getMapRegionsIds()) {
			Set<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes != null)
				for (int playerIndex : playerIndexes) {
					Player player = World.getPlayers().get(playerIndex);
					if (isRevenant() && player.isRevenantAggroImmune())
						continue;
					if (player == null
							|| player.isDead()
							|| player.hasFinished()
							|| !player.isRunning()
							|| !canAggroPlayer(player)
							|| (player.isDocile() && !ignoreDocile)
							|| player.getAppearance().isHidden()
							|| !WorldUtil.isInRange(getX(), getY(), getSize(), player.getX(), player.getY(), player.getSize(), getAggroDistance())
							|| (!forceMultiAttacked && (!isAtMultiArea() || !player.isAtMultiArea()) && player.getAttackedBy() != this && (player.inCombat() || player.getFindTargetDelay() > System.currentTimeMillis()))
							|| !lineOfSightTo(player, false)
							|| (!forceAgressive && !WildernessController.isAtWild(this) && player.getSkills().getCombatLevelWithSummoning() >= getCombatLevel() * 2))
						continue;
					possibleTarget.add(player);
				}
			if (includeNpcs) {
				Set<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
				if (npcsIndexes != null)
					for (int npcIndex : npcsIndexes) {
						NPC npc = World.getNPCs().get(npcIndex);
						if (npc == null
								|| npc == this
								|| npc.isDead()
								|| npc.hasFinished()
								|| !canAggroNPC(npc)
								|| !WorldUtil.isInRange(getX(), getY(), getSize(), npc.getX(), npc.getY(), npc.getSize(), getAggroDistance())
								|| !npc.getDefinitions().hasAttackOption()
								|| (!forceMultiAttacked && (!isAtMultiArea() || !npc.isAtMultiArea()) && npc.getAttackedBy() != this && (npc.inCombat() || npc.getFindTargetDelay() > System.currentTimeMillis()))
								|| !lineOfSightTo(npc, false))
							continue;
						possibleTarget.add(npc);
					}
			}
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
		if (!forceAgressive) {
			NPCCombatDefinitions defs = getCombatDefinitions();
			if (defs.getAgressivenessType() == AggressiveType.PASSIVE)
				return false;
		}
		List<Entity> possibleTarget = getPossibleTargets();
		if (!possibleTarget.isEmpty()) {
			Entity target = possibleTarget.get(Utils.random(possibleTarget.size()));
			setTarget(target);
			target.setAttackedBy(target);
			target.setFindTargetDelay(System.currentTimeMillis() + 10000);
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

	public void setForceAgressive(boolean forceAgressive) {
		this.forceAgressive = forceAgressive;
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
		return super.withinDistance(tile, distance);
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

	public boolean isBlocksOtherNPCs() {
		return blocksOtherNPCs;
	}

	public void setBlocksOtherNPCs(boolean blocksOtherNPCs) {
		this.blocksOtherNPCs = blocksOtherNPCs;
	}

	public boolean isIgnoreNPCClipping() {
		return ignoreNPCClipping;
	}

	public void setIgnoreNPCClipping(boolean ignoreNPCClipping) {
		this.ignoreNPCClipping = ignoreNPCClipping;
	}
}
