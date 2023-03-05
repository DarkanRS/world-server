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
package com.rs.game.content.bosses.qbd.npcs;

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

/**
 * Represents the Queen Black Dragon.
 *
 * @author Emperor
 *
 */
public final class QueenBlackDragon extends NPC {

	private static final QueenAttack[] PHASE_1_ATTACKS = { new FireBreathAttack(), new MeleeAttack(), new RangeAttack(), new FireWallAttack() };
	private static final QueenAttack[] PHASE_2_ATTACKS = { new FireBreathAttack(), new MeleeAttack(), new RangeAttack(), new FireWallAttack(), new ChangeArmour(), new SoulSummonAttack() };
	private static final QueenAttack[] PHASE_3_ATTACKS = { new FireBreathAttack(), new MeleeAttack(), new RangeAttack(), new FireWallAttack(), new ChangeArmour(), new SoulSummonAttack(), new SoulSiphonAttack() };
	private static final QueenAttack[] PHASE_4_ATTACKS = {
			new FireBreathAttack(),
			new TimeStopAttack(),
			new MeleeAttack(),
			new SuperFireAttack(),
			new RangeAttack(),
			new FireWallAttack(),
			new SuperFireAttack(),
			new ChangeArmour(),
			new SoulSummonAttack(),
			new SoulSiphonAttack(),
			new TimeStopAttack()
	};

	/**
	 * The waking up animation.
	 */
	private static final Animation WAKE_UP_ANIMATION = new Animation(16714);

	/**
	 * The sleeping animation.
	 */
	private static final Animation SLEEP_ANIMATION = new Animation(16742);

	/**
	 * The player.
	 */
	private transient final Player attacker;

	/**
	 * The queen state.
	 */
	private QueenState state = QueenState.SLEEPING;

	/**
	 * The amount of ticks passed.
	 */
	private int ticks;

	/**
	 * The next attack tick count.
	 */
	private int nextAttack;

	/**
	 * The current attacks.
	 */
	private QueenAttack[] attacks;

	/**
	 * The current phase.
	 */
	private int phase;

	/**
	 * The region base location.
	 */
	private final Tile base;

	/**
	 * The list of tortured souls.
	 */
	private final List<TorturedSoul> souls = new ArrayList<>();

	/**
	 * The list of worms.
	 */
	private final List<NPC> worms = new ArrayList<>();

	/**
	 * If the Queen Black Dragon is spawning worms.
	 */
	private boolean spawningWorms;

	/**
	 * The current active artifact.
	 */
	private GameObject activeArtifact;

	/**
	 * The rewards container.
	 */
	private final ItemsContainer<Item> rewards = new ItemsContainer<>(10, true);

	/**
	 * The last amount of hitpoints.
	 */
	private int lastHitpoints = -1;

	/**
	 * Constructs a new {@code QueenBlackDragon} {@code Object}.
	 *
	 * @param attacker
	 *            The player.
	 * @param tile
	 *            The world tile to set the queen on.
	 * @param base
	 *            The dynamic region's base location.
	 */
	public QueenBlackDragon(Player attacker, Tile tile, Tile base) {
		super(15509, tile, true);
		super.setForceMultiArea(true);
		super.setCantFollowUnderCombat(true);
		setCantInteract(true);
		this.base = base;
		this.attacker = attacker;
		nextAttack = 40;
		setHitpoints(getMaxHitpoints());
		activeArtifact = new GameObject(70776, ObjectType.SCENERY_INTERACT, 0, base.transform(33, 31, 0));
		setPhase(1);
		setCapDamage(1000);
		setIgnoreDocile(true);
	}

	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public void setHitpoints(int hitpoints) {
		super.setHitpoints(hitpoints);
		if (attacker == null)
			return;
		if (lastHitpoints != hitpoints) {
			attacker.getPackets().sendVarc(1923, getMaxHitpoints() - hitpoints);
			lastHitpoints = hitpoints;
		}
	}

	@Override
	public void sendDeath(Entity source) {
		switch (phase) {
		case 1:
			attacker.getPackets().sendVarc(1924, 1);
			activeArtifact = new GameObject(70777, ObjectType.SCENERY_INTERACT, 0, base.transform(33, 31, 0));
			attacker.sendMessage("The Queen Black Dragon's concentration wavers; the first artefact is now unguarded.");
			break;
		case 2:
			attacker.getPackets().sendVarc(1924, 3);
			World.spawnObject(new GameObject(70844, ObjectType.SCENERY_INTERACT, 0, base.transform(24, 21, -1)));
			activeArtifact = new GameObject(70780, ObjectType.SCENERY_INTERACT, 0, base.transform(24, 21, 0));
			attacker.sendMessage("The Queen Black Dragon's concentration wavers; the second artefact is now");
			attacker.sendMessage("unguarded.");
			break;
		case 3:
			attacker.getPackets().sendVarc(1924, 5);
			World.spawnObject(new GameObject(70846, ObjectType.SCENERY_INTERACT, 0, base.transform(24, 21, -1)));
			activeArtifact = new GameObject(70783, ObjectType.SCENERY_INTERACT, 0, base.transform(42, 21, 0));
			attacker.sendMessage("The Queen Black Dragon's concentration wavers; the third artefact is now");
			attacker.sendMessage("unguarded.");
			break;
		case 4:
			attacker.getPackets().sendVarc(1924, 7);
			World.spawnObject(new GameObject(70848, ObjectType.SCENERY_INTERACT, 0, base.transform(24, 21, -1)));
			activeArtifact = new GameObject(70786, ObjectType.SCENERY_INTERACT, 0, base.transform(33, 21, 0));
			attacker.sendMessage("The Queen Black Dragon's concentration wavers; the last artefact is now unguarded.");
			break;
		}
		if (phase <= 4)
			World.spawnObject(activeArtifact);
		setCantInteract(true);
		if (phase < 5) {
			setSpawningWorms(true);
			return;
		}
		switchState(QueenState.DEFAULT);
		// TODO: Back to sleep roar
	}

	@Override
	public int getMaxHitpoints() {
		return 7500;
	}

	@Override
	public void processNPC() {
		if (ticks > 5 && !attacker.isHasNearbyInstancedChunks()) {
			finish();
			return;
		}
		if (ticks == -20) {
			switchState(QueenState.DEFAULT);
			switchState(QueenState.SLEEPING);
			setNextAnimation(SLEEP_ANIMATION);
		} else if (ticks >= -19 && ticks <= -10) {
			setNextAnimation(SLEEP_ANIMATION);
		} else if (ticks == -1)
			return;
		ticks++;
		if (spawningWorms) {
			if ((ticks % 16) == 0)
				spawnWorm();
			return;
		}
		if (ticks == 5)
			super.setNextAnimation(WAKE_UP_ANIMATION);
		else if (ticks == 30) {
			setCantInteract(false);
			switchState(QueenState.DEFAULT);
		} else if (ticks == nextAttack) {
			QueenAttack attack;
			while (!(attack = attacks[Utils.random(attacks.length)]).canAttack(this, attacker))
				;
			setNextAttack(attack.attack(this, attacker));
		}
	}

	@Override
	public void finish() {
		for (TorturedSoul s : souls)
			s.finish();
		for (NPC worm : worms)
			worm.finish();
		super.finish();
	}

	/**
	 * Spawns a grotworm.
	 */
	public void spawnWorm() {
		setNextAnimation(new Animation(16747));
		attacker.sendMessage("Worms burrow through her rotting flesh.");
		final Tile destination = base.transform(28 + Utils.random(12), 28 + Utils.random(6), 0);
		WorldProjectile p = World.sendProjectile(this, destination, 3141, 128, 0, 60, 1.5, 5, 3);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (getPhase() > 4)
					return;
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						if (getPhase() > 4)
							return;
						NPC worm = new NPC(15464, destination, true);
						worms.add(worm);
						worm.setForceMultiArea(true);
						worm.getCombat().setTarget(attacker);
					}
				}, p.getTaskDelay()-1);
				attacker.getPackets().sendSpotAnim(new SpotAnim(3142), destination);
			}
		}, 0);
	}

	@Override
	public int getFaceAngle() {
		return 0;
	}

	/**
	 * Gets the attacker.
	 *
	 * @return The attacker.
	 */
	public Player getAttacker() {
		return attacker;
	}

	/**
	 * Gets the state.
	 *
	 * @return The state.
	 */
	public QueenState getState() {
		return state;
	}

	/**
	 * Switches the queen state.
	 *
	 * @param state
	 *            The state.
	 */
	public void switchState(QueenState state) {
		this.state = state;
		if (state.getMessage() != null) {
			String[] messages = state.getMessage().split("(nl)");
			for (String message : messages)
				attacker.sendMessage(message.replace("(", "").replace(")", ""));
		}
		super.transformIntoNPC(state.getNpcId());
		setCapDamage(1000);
		switch (state) {
		case DEFAULT:
			World.spawnObject(new GameObject(70822, ObjectType.SCENERY_INTERACT, 0, base.transform(21, 35, -1)));
			World.spawnObject(new GameObject(70818, ObjectType.SCENERY_INTERACT, 0, base.transform(39, 35, -1)));
			break;
		case HARDEN:
			World.spawnObject(new GameObject(70824, ObjectType.SCENERY_INTERACT, 0, base.transform(21, 35, -1)));
			World.spawnObject(new GameObject(70820, ObjectType.SCENERY_INTERACT, 0, base.transform(39, 35, -1)));
			break;
		case CRYSTAL_ARMOUR:
			World.spawnObject(new GameObject(70823, ObjectType.SCENERY_INTERACT, 0, base.transform(21, 35, -1)));
			World.spawnObject(new GameObject(70819, ObjectType.SCENERY_INTERACT, 0, base.transform(39, 35, -1)));
			break;
		default:
			World.spawnObject(new GameObject(70822, ObjectType.SCENERY_INTERACT, 0, base.transform(21, 35, -1)));
			World.spawnObject(new GameObject(70818, ObjectType.SCENERY_INTERACT, 0, base.transform(39, 35, -1)));
			break;
		}
	}

	/**
	 * Opens the reward chest.
	 *
	 * @param replace
	 *            If the chest should be replaced with an opened one.
	 */
	public void openRewardChest(boolean replace) {
		attacker.getInterfaceManager().sendInterface(1284);
		attacker.getPackets().sendInterSetItemsOptionsScript(1284, 7, 100, 8, 3, "Take", "Bank", "Discard", "Examine");
		attacker.getPackets().setIFRightClickOps(1284, 7, 0, 10, 0, 1, 2, 3);
		attacker.getPackets().sendItems(100, rewards);
		for (Item item : rewards.array()) {
			if (item == null)
				continue;
			if (yellDrop(item.getId()))
				World.broadcastLoot(attacker.getDisplayName() + " has just received a " + item.getName() + " drop from the Queen Black Dragon!");
			attacker.incrementCount(item.getName() + " drops earned", item.getAmount());
		}
		if (replace)
			World.spawnObject(new GameObject(70817, ObjectType.SCENERY_INTERACT, 0, base.transform(30, 28, -1)));
	}

	/**
	 * Sets the state.
	 *
	 * @param state
	 *            The state to set.
	 */
	public void setState(QueenState state) {
		this.state = state;
	}

	/**
	 * Gets the nextAttack.
	 *
	 * @return The nextAttack.
	 */
	public int getNextAttack() {
		return nextAttack;
	}

	/**
	 * Sets the nextAttack value (current ticks + the given amount).
	 *
	 * @param nextAttack
	 *            The amount.
	 */
	public void setNextAttack(int nextAttack) {
		this.nextAttack = ticks + nextAttack;
	}

	/**
	 * Gets the phase.
	 *
	 * @return The phase.
	 */
	public int getPhase() {
		return phase;
	}

	/**
	 * Sets the phase.
	 *
	 * @param phase
	 *            The phase to set.
	 */
	public void setPhase(int phase) {
		this.phase = phase;
		switch (phase) {
		case 1:
			attacks = PHASE_1_ATTACKS;
			break;
		case 2:
			attacks = PHASE_2_ATTACKS;
			break;
		case 3:
			attacks = PHASE_3_ATTACKS;
			break;
		case 4:
			attacks = PHASE_4_ATTACKS;
			break;
		case 5:
		case 6:
			setCantInteract(true);
			for (TorturedSoul soul : souls)
				soul.finish();
			for (NPC worm : worms)
				worm.finish();
			ticks = -22;
			prepareRewards();
			World.removeObject(World.getObject(base.transform(22, 24, -1), ObjectType.SCENERY_INTERACT));
			World.removeObject(World.getObject(base.transform(34, 24, -1), ObjectType.SCENERY_INTERACT));
			attacker.sendMessage("<col=33FFFF>The enchantment is restored! The Queen Black Dragon falls back into her cursed</col>");
			attacker.sendMessage("<col=33FFFF>slumber.</col>");
			attacker.sendNPCKill("Queen Black Dragon");
			if (attacker.hasSlayerTask() && attacker.getSlayer().isOnTaskAgainst(this))
				attacker.getSlayer().sendKill(attacker, this);
			break;
		}
	}

	public static List<Item> genDrop(Player attacker) {
		List<Item> drops = new ArrayList<>();
		Utils.add(drops, DropTable.calculateDrops(attacker, DropSets.getDropSet("QBDMain")));
		Utils.add(drops, DropTable.calculateDrops(attacker, DropSets.getDropSet("QBDSupply")));
		if (Utils.random(128) == 0) {
			Utils.add(drops, DropTable.calculateDrops(attacker, DropSets.getDropSet("rdt_standard")));
			Utils.add(drops, DropTable.calculateDrops(attacker, DropSets.getDropSet("rdt_standard")));
		}
		return drops;
	}

	/**
	 * Prepares the rewards.
	 */
	public void prepareRewards() {
		if (rewards.getUsedSlots() == 0)
			rewards.addAll(genDrop(attacker));
	}

	/**
	 * Gets the base.
	 *
	 * @return The base.
	 */
	public Tile getBase() {
		return base;
	}

	/**
	 * Gets the amount of ticks.
	 *
	 * @return The amount of ticks.
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * Gets the souls.
	 *
	 * @return The souls.
	 */
	public List<TorturedSoul> getSouls() {
		return souls;
	}

	/**
	 * Gets the spawningWorms.
	 *
	 * @return The spawningWorms.
	 */
	public boolean isSpawningWorms() {
		return spawningWorms;
	}

	/**
	 * Sets the spawningWorms.
	 *
	 * @param spawningWorms
	 *            The spawningWorms to set.
	 */
	public void setSpawningWorms(boolean spawningWorms) {
		if (!spawningWorms)
			setNextAnimation(new Animation(16748));
		this.spawningWorms = spawningWorms;
	}

	/**
	 * Gets the worms.
	 *
	 * @return The worms.
	 */
	public List<NPC> getWorms() {
		return worms;
	}

	/**
	 * Gets the activeArtifact.
	 *
	 * @return The activeArtifact.
	 */
	public GameObject getActiveArtifact() {
		return activeArtifact;
	}

	/**
	 * Sets the activeArtifact.
	 *
	 * @param activeArtifact
	 *            The activeArtifact to set.
	 */
	public void setActiveArtifact(GameObject activeArtifact) {
		this.activeArtifact = activeArtifact;
	}

	/**
	 * Gets the rewards.
	 *
	 * @return The rewards.
	 */
	public ItemsContainer<Item> getRewards() {
		return rewards;
	}

}