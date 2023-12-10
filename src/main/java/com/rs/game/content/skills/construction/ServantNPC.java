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
package com.rs.game.content.skills.construction;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.World;
import com.rs.game.content.skills.construction.House.RoomReference;
import com.rs.game.content.skills.construction.HouseConstants.Builds;
import com.rs.game.content.skills.construction.HouseConstants.Room;
import com.rs.game.content.skills.construction.HouseConstants.Servant;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Bank;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.utils.WorldUtil;

public class ServantNPC extends NPC {

	private final Servant servant;
	private final Player owner;
	private final House house;
	private boolean follow, greetGuests;
	private Item lastBankRetrieve;
	private Item lastSawmillSend;

	public ServantNPC(House house) {
		super(house.getServant().getId(), house.getPlayer().getTile().transform(0, 0, 0), true);
		servant = house.getServant();
		owner = house.getPlayer();
		this.house = house;
		if (owner.getSkills().getLevel(Constants.CONSTRUCTION) < servant.getLevel()) {
			house.setServantOrdinal((byte) -1);
		}
	}

	public void fire() {
		house.setServantOrdinal((byte) -1);
	}

	public long getBankDelay() {
		return servant.getBankDelay();
	}

	public boolean isFollowing() {
		return follow;
	}

	public void setFollowing(boolean follow) {
		this.follow = follow;
		if (!follow)
			setNextFaceEntity(null);
	}

	public void makeFood(final Builds[] builds) {
		if (house == null)
			return;
		setFollowing(false);
		if (house.isBuildMode()) {
			owner.sendMessage("Your servant cannot prepare food while in building mode.");
			return;
		}
		String basicResponse = "I apologise, but I cannot serve " + (owner.getAppearance().isMale() ? "Sir" : "Madam") + " without";
		final RoomReference kitchen = house.getRoom(Room.KITCHEN), diningRoom = house.getRoom(Room.DINING_ROOM);
		if (kitchen == null) {
			owner.npcDialogue(getId(), HeadE.CALM_TALK, basicResponse + " a proper kitchen.");
			return;
		}
		if (diningRoom == null) {
			owner.npcDialogue(getId(), HeadE.CALM_TALK, basicResponse + " a proper dining room.");
			return;
		}
		for (Builds build : builds)
			if (!kitchen.containsBuild(build)) {
				owner.npcDialogue(getId(), HeadE.CALM_TALK, basicResponse + " a " + build.toString().toLowerCase() + ".");
				return;
			}

		if (!diningRoom.containsBuild(HouseConstants.Builds.DINING_TABLE)) {
			owner.npcDialogue(getId(), HeadE.CALM_TALK, basicResponse + " a dining table");
			return;
		}

		final Tile kitchenTile = house.getCenterTile(kitchen);
		final Tile diningRoomTile = house.getCenterTile(diningRoom);

		setCantInteract(true);
		house.incrementPaymentStage();

		WorldTasks.schedule(new Task() {

			int count = 0, totalCount = 0, index = 0;

			@Override
			public void run() {
				if (!house.isLoaded()) {
					stop();
					return;
				}
				count++;
				if (count == 1) {
					setNextForceTalk(new ForceTalk("I shall return in a moment."));
					setNextAnimation(new Animation(858));
					totalCount = (builds.length * 3) + count;
				} else if (count == 2)
					setNextTile(Tile.of(World.getFreeTile(kitchenTile, 2)));
				else if (totalCount > 0 && index < builds.length) {
					int calculatedCount = totalCount - count;
					Builds build = builds[index];
					if (calculatedCount % 3 == 0) {
						setNextAnimation(new Animation(build == Builds.STOVE ? 897 : 3659));
						index++;
					} else if (calculatedCount % 1 == 0)
						calcFollow(house.getWorldObjectForBuild(kitchen, build), true);
				} else if (count == totalCount + 3)
					setNextTile(World.getFreeTile(diningRoomTile, 2));
				else if (count == totalCount + 4 || count == totalCount + 5) {
					GameObject diningTable = house.getWorldObjectForBuild(diningRoom, Builds.DINING_TABLE);
					if (count == totalCount + 4)
						calcFollow(diningTable, true);
					else {
						setNextAnimation(new Animation(808));
						int rotation = kitchen.getRotation();
						for (int x = 0; x < (rotation == 1 || rotation == 3 ? 2 : 4); x++)
							for (int y = 0; y < (rotation == 1 || rotation == 3 ? 4 : 2); y++)
								World.addGroundItem(new Item(builds.length == 6 ? 7736 : builds.length == 5 ? house.getServant().getFoodId() : HouseConstants.BEERS[kitchen.getBuildSlot(Builds.BARRELS)]), diningTable.getTile().transform(x, y, 0), null, false, 300);
						setCantInteract(false);
						stop();
					}
				}
			}
		}, 2, 2);
	}

	public enum RequestType {
		WITHDRAW, SAWMILL, UNNOTE, DEPOSIT
	}
	
	public void requestType(int item, int quantity, final RequestType type) {
		final Bank bank = owner.getBank();
		final ItemDefinitions defs = ItemDefinitions.getDefs(item);
		int inventorySize = servant.getInventorySize();
		if (!bank.containsItem(defs.isNoted() ? defs.getCertId() : item, 1) && type == RequestType.WITHDRAW) {
			owner.npcDialogue(getId(), servant == Servant.DEMON_BUTLER ? HeadE.CAT_CALM_TALK2 : HeadE.CALM_TALK, "It appears you do not have this item in your bank.");
			return;
		}
		if (quantity > inventorySize) {
			owner.npcDialogue(getId(), servant == Servant.DEMON_BUTLER ? HeadE.CAT_CALM_TALK2 : HeadE.CALM_TALK, "I'm sorry. I can only hold " + inventorySize + " items during a trip.");
			return;
		}
		setNextNPCTransformation(1957);

		if (type == RequestType.SAWMILL || type == RequestType.UNNOTE) {
			int amountOwned = owner.getInventory().getAmountOf(item);
			if (quantity > amountOwned) {
				quantity = amountOwned;
				if (quantity > inventorySize)
					quantity = inventorySize;
			}
		}

		final int[] plank = SawmillOperator.getPlankForLog(item);
		if (plank != null && type == RequestType.SAWMILL) {
			final int cost = (int) ((plank[1] * 0.7) * quantity);
			if (!owner.getInventory().hasCoins(cost)) {
				owner.npcDialogue(getId(), servant == Servant.DEMON_BUTLER ? HeadE.CAT_CALM_TALK2 : HeadE.CALM_TALK, "You do not have enough coins to cover the costs of the sawmill.");
				return;
			}
		}

		if (type == RequestType.WITHDRAW || type == RequestType.SAWMILL || type == RequestType.UNNOTE) {
			if (type == RequestType.UNNOTE || type == RequestType.WITHDRAW) {
				int freeSlots = owner.getInventory().getFreeSlots();
				if (quantity > freeSlots)
					quantity = freeSlots;
			} else if (type == RequestType.SAWMILL)
				owner.getInventory().removeCoins((int) (quantity * (plank[1] * 0.7)));
			if (type != RequestType.WITHDRAW)
				owner.getInventory().deleteItem(item, quantity);
		}

		final int completeQuantity = quantity;
		setCantInteract(true);

		if (defs.isNoted())
			item = defs.getCertId();
		final int finalItem = item;
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				setNextNPCTransformation(servant.getId());
				setCantInteract(false);
				if (!owner.isRunning() || !house.isLoaded() || !house.getPlayers().contains(owner)) {
					if (type == RequestType.SAWMILL || type == RequestType.UNNOTE)
						bank.addItem(new Item(finalItem, completeQuantity), false);
					return;
				}
				house.incrementPaymentStage();
				if (type == RequestType.WITHDRAW) {
					if (bank.containsItem(finalItem, completeQuantity)) {
						bank.withdrawItemDel(finalItem, completeQuantity);
						owner.getInventory().addItem(finalItem, completeQuantity, true);
					}
				} else if (type == RequestType.SAWMILL)
					owner.getInventory().addItem(plank[0], completeQuantity);
				else if (type == RequestType.UNNOTE)
					owner.getInventory().addItem(finalItem, completeQuantity);
				else
					for (int i = 0; i < completeQuantity; i++)
						bank.depositItem(owner.getInventory().getItems().getThisItemSlot(finalItem), completeQuantity, false);
				owner.npcDialogue(getId(), servant == Servant.DEMON_BUTLER ? HeadE.CAT_CALM_TALK2 : HeadE.CALM_TALK, type == RequestType.DEPOSIT ? "I have successfully deposited your items into your bank. No longer will the items be at risk from thieves." : "I have returned with the items you asked me to retrieve.");
			}
		}, (int) servant.getBankDelay());
	}

	public void call() {
		Tile teleTile = owner.getNearestTeleTile(this);
		if (teleTile != null)
			setNextTile(teleTile);
	}

	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
			setNextFaceEntity(owner);
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
			calcFollow(owner, 2, true);
	}

	@Override
	public void processNPC() {
		if (greetGuests && !withinDistance(getRespawnTile(), 5))
			greetGuests = false;
		if (!follow) {
			super.processNPC();
			return;
		}
		if (!withinDistance(owner, 12)) {
			call();
			return;
		}
		sendFollow();
	}

	public boolean isGreetGuests() {
		return greetGuests;
	}

	public void setGreetGuests(boolean greetGuests) {
		this.greetGuests = greetGuests;
	}

	public Servant getServantData() {
		return servant;
	}

	public Item getLastSawmillSend() {
		return lastSawmillSend;
	}

	public void setLastSawmillSend(Item lastSawmillSend) {
		this.lastSawmillSend = lastSawmillSend;
	}

	public Item getLastBankRetrieve() {
		return lastBankRetrieve;
	}

	public void setLastBankRetrieve(Item lastBankRetrieve) {
		this.lastBankRetrieve = lastBankRetrieve;
	}
}
