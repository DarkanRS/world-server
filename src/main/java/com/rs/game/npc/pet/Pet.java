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
package com.rs.game.npc.pet;

import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.pet.PetDetails;
import com.rs.game.player.content.pet.Pets;
import com.rs.lib.game.WorldTile;
import com.rs.utils.WorldUtil;

public final class Pet extends NPC {

	private final Player owner;
	private final int itemId;
	private final PetDetails details;
	private double growthRate;
	private final Pets pet;

	public Pet(int id, int itemId, Player owner, WorldTile tile, PetDetails details) {
		super(id, tile);
		this.owner = owner;
		this.itemId = itemId;
		this.details = details;
		pet = Pets.forId(itemId);
		setIgnoreNPCClipping(true);
		setBlocksOtherNPCs(false);
		if (pet == Pets.TROLL_BABY && owner.getPetManager().getTrollBabyName() != null)
			setName(owner.getPetManager().getTrollBabyName());
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
			if (details.getGrowth() == 100.0)
				growNextStage();
		}
		if (!withinDistance(owner, 12)) {
			call();
			return;
		}
		sendFollow();
	}

	public void growNextStage() {
		if ((details.getStage() == 3) || (pet == null))
			return;
		int npcId = pet.getNpcId(details.getStage() + 1);
		if (npcId < 1)
			return;
		details.setStage(details.getStage() + 1);
		int itemId = pet.getItemId(details.getStage());
		if (pet.getNpcId(details.getStage() + 1) > 0)
			details.updateGrowth(-100.0);
		owner.getPetManager().setItemId(itemId);
		owner.getPetManager().setNpcId(npcId);
		finish();
		Pet newPet = new Pet(npcId, itemId, owner, owner, details);
		newPet.growthRate = growthRate;
		owner.setPet(newPet);
		owner.sendMessage("<col=ff0000>Your pet has grown larger.</col>");
	}

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
		} else
			owner.sendMessage("You need more inventory slots to pick up your pet.");
	}

	public void call() {
		WorldTile teleTile = owner.getNearestTeleTile(this);
		if (teleTile != null)
			setNextWorldTile(teleTile);
	}

	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
			setNextFaceEntity(owner);
		if (hasEffect(Effect.FREEZE))
			return;
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
						if (!addWalkSteps(getX(), owner.getY() - size))
							return;
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!lineOfSightTo(owner, true) || !WorldUtil.isInRange(getX(), getY(), size, owner.getX(), owner.getY(), targetSize, 0))
			calcFollow(owner, 2, true, false);
	}

	public void sendMainConfigurations() {
		switchOrb(true);
		owner.getVars().setVar(448, itemId);// configures
		owner.getVars().setVar(1160, 243269632); // sets npc emote
		owner.getPackets().sendVarc(1436, 0);
		unlockOrb(); // temporary
	}

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

	public void switchOrb(boolean enable) {
		owner.getVars().setVar(1174, enable ? getId() : 0);
		if (enable) {
			unlock();
			return;
		}
		lockOrb();
	}

	public void unlockOrb() {
		owner.getPackets().setIFHidden(747, 9, false);
		Familiar.sendLeftClickOption(owner);
	}

	public void unlock() {
		owner.getPackets().setIFHidden(747, 9, false);
	}

	public void lockOrb() {
		owner.getPackets().setIFHidden(747, 9, true);
	}

	public PetDetails getDetails() {
		return details;
	}

	public double getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(double growthRate) {
		this.growthRate = growthRate;
	}

	public int getItemId() {
		return itemId;
	}

}