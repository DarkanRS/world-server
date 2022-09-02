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
package com.rs.game.ge;

import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.db.WorldDB;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.util.Logger;

public class Offer {
	private String owner;
	private int box;
	private boolean selling;
	private State state;
	private int itemId;
	private int amount;
	private int price;
	private int completedAmount;
	private int totalGold;
	private GE.GrandExchangeType currentType;
	private final GE.OfferType offerType;
	private boolean aborted = false;
	private ItemsContainer<Item> processedItems = new ItemsContainer<>(2, true);

	public Offer(String owner, int box, boolean selling, int itemId, int amount, int price, GE.OfferType type) {
		this.owner = owner;
		this.box = box;
		this.selling = selling;
		this.itemId = itemId;
		this.amount = amount;
		this.price = price;
		offerType = type;
		state = State.SUBMITTING;
		updateCurrentType();
	}

	public void updateCurrentType() {
		if (offerType == GE.OfferType.BUY)
			currentType = GE.GrandExchangeType.BUYING;
		else
			currentType = GE.GrandExchangeType.SELLING;
		if (aborted)
			currentType = GE.GrandExchangeType.ABORTED;
	}

	public enum State {
		EMPTY(),
		SUBMITTING(),
		STABLE(),
		UNK_3(),
		UNK_4(),
		FINISHED(),
		UNK_6(),
		UNK_7()
	}

	public int getStateHash() {
		return state.ordinal() + (selling ? 0x8 : 0);
	}

	public String getOwner() {
		return owner;
	}

	public boolean isSelling() {
		return selling;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getCompletedAmount() {
		return completedAmount;
	}

	public void setCompletedAmount(int completedAmount) {
		this.completedAmount = completedAmount;
	}

	public ItemsContainer<Item> getProcessedItems() {
		return processedItems;
	}

	public int getPrice() {
		return price;
	}

	public int getBox() {
		return box;
	}

	public int getItemId() {
		return itemId;
	}

	public int getAmount() {
		return amount;
	}

	public int amountLeft() {
		return amount - completedAmount;
	}

	public void addCompleted(int num) {
		completedAmount += num;
		if (completedAmount >= amount) {
			if (completedAmount > amount)
				Logger.handle(Offer.class, "addCompleted", "GE completed amount higher than sale amount: " + this.toString(), null);
			state = State.FINISHED;
		}
	}

	public void sendItems(Player player) {
		player.getPackets().sendItems(523+box, processedItems);
		if (player.getInterfaceManager().topOpen(105)) {
			player.getPackets().setIFEvents(new IFEvents(105, 206, -1, 0).enableRightClickOptions(0,1));
			player.getPackets().setIFEvents(new IFEvents(105, 208, -1, 0).enableRightClickOptions(0,1));
		}
	}

	@Override
	public String toString() {
		return "[" + owner + ", " + box + ", " + selling + ", " + state + ", " + itemId + ", " + amount + ", " + price + "]";
	}

	public boolean process(Offer other) {
		if (state != State.STABLE || other.getState() != State.STABLE || (selling && price > other.getPrice()))
			return false;
		if ((!selling && price < other.getPrice()) || (itemId != other.getItemId()))
			return false;
		int numTransact = Math.min(amountLeft(), other.amountLeft());
		int finalPrice = numTransact * other.price;

		WorldDB.getLogs().logGE(this, other, numTransact, price);

		addCompleted(numTransact);
		totalGold += finalPrice;

		other.addCompleted(numTransact);
		other.totalGold += finalPrice;

		if (selling) {
			processedItems.add(new Item(995, finalPrice));
			other.processedItems.add(new Item(itemId, numTransact));
		} else {
			int diff = (price * numTransact) - finalPrice;
			if (diff > 0)
				processedItems.add(new Item(995, diff));
			processedItems.add(new Item(itemId, numTransact));
			other.processedItems.add(new Item(995, finalPrice));
		}
		return true;
	}

	public void abort() {
		state = State.FINISHED;
		processedItems.add(selling ? new Item(itemId, amountLeft()) : new Item(995, amountLeft() * price));
		aborted = true;
		updateCurrentType();
	}

	public int getTotalGold() {
		return totalGold;
	}

	public GE.GrandExchangeType getCurrentType() {
		return currentType;
	}

	public GE.OfferType getOfferType() {
		return offerType;
	}
}
