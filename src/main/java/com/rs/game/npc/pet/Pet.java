package com.rs.game.npc.pet;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.pet.PetDetails;
import com.rs.game.player.content.pet.Pets;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

/**
 * Represents a pet.
 * 
 * @author Emperor
 * 
 */
public final class Pet extends NPC {

	/**
	 * The owner.
	 */
	private final Player owner;

	/**
	 * The "near" directions.
	 */
	private final int[][] checkNearDirs;

	/**
	 * The item id.
	 */
	private final int itemId;

	/**
	 * The pet details.
	 */
	private final PetDetails details;

	/**
	 * The growth rate of the pet.
	 */
	private double growthRate;

	/**
	 * The pets type.
	 */
	private final Pets pet;

	/**
	 * Constructs a new {@code Pet} {@code Object}.
	 * 
	 * @param id
	 *            The NPC id.
	 * @param itemId
	 *            The item id.
	 * @param owner
	 *            The owner.
	 * @param tile
	 *            The world tile.
	 */
	public Pet(int id, int itemId, Player owner, WorldTile tile, PetDetails details) {
		super(id, tile);
		this.owner = owner;
		this.itemId = itemId;
		this.checkNearDirs = Utils.getCoordOffsetsNear(super.getSize());
		this.details = details;
		this.pet = Pets.forId(itemId);
		this.setIgnoreNPCClipping(true);
		this.setBlocksOtherNPCs(false);
		if (pet == Pets.TROLL_BABY && owner.getPetManager().getTrollBabyName() != null) {
			setName(owner.getPetManager().getTrollBabyName());
		}
		if (details != null) {
			sendMainConfigurations();
			sendFollowerDetails();
		}
	}

	@Override
	public void processNPC() {
		unlockOrb();
		if (pet == Pets.TROLL_BABY || pet.getFood().length > 0) {
			details.updateHunger(0.025);
			owner.getVars().setVarBit(4286, (int) details.getHunger());
		}
		if (growthRate > 0.000) {
			details.updateGrowth(growthRate);
			owner.getVars().setVarBit(4285, (int) details.getGrowth());
			if (details.getGrowth() == 100.0) {
				growNextStage();
			}
		}
		if (!withinDistance(owner, 12)) {
			call();
			return;
		}
		sendFollow();
	}

	/**
	 * Grows into the next stage of this pet (if any).
	 */
	public void growNextStage() {
		if (details.getStage() == 3) {
			return;
		}
		if (pet == null) {
			return;
		}
		int npcId = pet.getNpcId(details.getStage() + 1);
		if (npcId < 1) {
			return;
		}
		details.setStage(details.getStage() + 1);
		int itemId = pet.getItemId(details.getStage());
		if (pet.getNpcId(details.getStage() + 1) > 0) {
			details.updateGrowth(-100.0);
		}
		owner.getPetManager().setItemId(itemId);
		owner.getPetManager().setNpcId(npcId);
		finish();
		Pet newPet = new Pet(npcId, itemId, owner, owner, details);
		newPet.growthRate = growthRate;
		owner.setPet(newPet);
		owner.sendMessage("<col=ff0000>Your pet has grown larger.</col>");
	}

	/**
	 * Picks up the pet.
	 */
	public void pickup() {
		if (itemId > 50000 || owner.getInventory().hasFreeSlots()) {
			if (itemId < 50000)
				owner.getInventory().addItem(itemId, 1);
			owner.setPet(null);
			owner.getPetManager().setNpcId(-1);
			owner.getPetManager().setItemId(-1);
			switchOrb(false);
			owner.getInterfaceManager().removeWindowInterface(98, 212);
			owner.getPackets().setIFTargetParamsDefault(747, 17, 0, 0);
			finish();
		} else {
			owner.sendMessage("You need more inventory slots to pick up your pet.");
		}
	}

	/**
	 * Calls the pet.
	 */
	public void call() {
		int size = getSize();
		WorldTile teleTile = null;
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
			final WorldTile tile = new WorldTile(new WorldTile(owner.getX() + checkNearDirs[0][dir], owner.getY() + checkNearDirs[1][dir], owner.getPlane()));
			if (World.floorAndWallsFree(tile, size)) {
				teleTile = tile;
				break;
			}
		}
		if (teleTile == null) {
			return;
		}
		setNextWorldTile(teleTile);
	}

	/**
	 * Follows the owner.
	 */
	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
			setNextFaceEntity(owner);
		if (hasEffect(Effect.FREEZE)) {
			return;
		}
		int size = getSize();
		int targetSize = owner.getSize();
		if (WorldUtil.collides(getX(), getY(), size, owner.getX(), owner.getY(), targetSize) && !owner.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(owner.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(owner.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), owner.getY() - size)) {
							return;
						}
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!lineOfSightTo(owner, true) || !WorldUtil.isInRange(getX(), getY(), size, owner.getX(), owner.getY(), targetSize, 0))
			calcFollow(owner, 2, true, false);
	}

	/**
	 * Sends the main configurations for the Pet interface (+ summoning orb).
	 */
	public void sendMainConfigurations() {
		switchOrb(true);
		owner.getVars().setVar(448, itemId);// configures
		owner.getVars().setVar(1160, 243269632); // sets npc emote
		owner.getPackets().sendVarc(1436, 0);
		unlockOrb(); // temporary
	}

	/**
	 * Sends the follower details.
	 */
	public void sendFollowerDetails() {
		if (details == null || owner == null)
			return;
		owner.getVars().setVarBit(4285, (int) details.getGrowth());
		owner.getVars().setVarBit(4286, (int) details.getHunger());
		boolean res = owner.getInterfaceManager().hasRezizableScreen();
		owner.getInterfaceManager().setInterface(true, res ? 746 : 548, res ? 120 : 184, 662);
		unlock();
		owner.getPackets().sendVarc(168, 8);// tab id
	}

	/**
	 * Switch the Summoning orb state.
	 * 
	 * @param enable
	 *            If the orb should be enabled.
	 */
	public void switchOrb(boolean enable) {
		owner.getVars().setVar(1174, enable ? getId() : 0);
		if (enable) {
			unlock();
			return;
		}
		lockOrb();
	}

	/**
	 * Unlocks the orb.
	 */
	public void unlockOrb() {
		owner.getPackets().setIFHidden(747, 9, false);
		Familiar.sendLeftClickOption(owner);
	}

	/**
	 * Unlocks the interfaces.
	 */
	public void unlock() {
		owner.getPackets().setIFHidden(747, 9, false);
	}

	/**
	 * Locks the orb.
	 */
	public void lockOrb() {
		owner.getPackets().setIFHidden(747, 9, true);
	}

	/**
	 * Gets the details.
	 * 
	 * @return The details.
	 */
	public PetDetails getDetails() {
		return details;
	}

	/**
	 * Gets the growthRate.
	 * 
	 * @return The growthRate.
	 */
	public double getGrowthRate() {
		return growthRate;
	}

	/**
	 * Sets the growthRate.
	 * 
	 * @param growthRate
	 *            The growthRate to set.
	 */
	public void setGrowthRate(double growthRate) {
		this.growthRate = growthRate;
	}

	/**
	 * Gets the item id of the pet.
	 * 
	 * @return The item id.
	 */
	public int getItemId() {
		return itemId;
	}

}