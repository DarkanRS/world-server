package com.rs.game.ge;

import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.game.item.ItemsContainer;
import com.rs.game.player.Player;
import com.rs.lib.file.FileManager;
import com.rs.lib.game.Item;

public class Offer {
	private String owner;
	private int box;
	private boolean selling;
	private State state;
	private int itemId;
	private int amount;
	private int price;
	private int completedAmount;
	private ItemsContainer<Item> processedItems = new ItemsContainer<>(2, true);
	
	public Offer(String owner, int box, boolean selling, int itemId, int amount, int price) {
		this.owner = owner;
		this.box = box;
		this.selling = selling;
		this.itemId = itemId;
		this.amount = amount;
		this.price = price;
		this.state = State.SUBMITTING;
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
		if (completedAmount >= this.amount) {
			if (completedAmount > this.amount)
				FileManager.logError("GRAND EXCHANGE COMPLETED AMOUNT HIGHER THAN SALE AMOUNT");
			state = State.FINISHED;
		}
	}

	public void sendItems(Player player) {
		player.getPackets().sendItems(523+box, processedItems);
		if (player.getInterfaceManager().containsInterface(105)) {
			player.getPackets().setIFTargetParams(new IFTargetParams(105, 206, -1, 0).enableRightClickOptions(0,1));
			player.getPackets().setIFTargetParams(new IFTargetParams(105, 208, -1, 0).enableRightClickOptions(0,1));
		}
	}
	
	@Override
	public String toString() {
		return "[" + owner + ", " + box + ", " + selling + ", " + state + ", " + itemId + ", " + amount + ", " + price + "]";
	}

	public boolean process(Offer other) {
		if (state != State.STABLE || other.getState() != State.STABLE)
			return false;
		if (selling && price > other.getPrice())
			return false;
		if (!selling && price < other.getPrice())
			return false;
		if (itemId != other.getItemId())
			return false;
		int numTransact = Math.min(amountLeft(), other.amountLeft());
		this.addCompleted(numTransact);
		other.addCompleted(numTransact);
		processedItems.add(selling ? new Item(995, numTransact * other.price) : new Item(itemId, numTransact));
		other.processedItems.add(selling ? new Item(itemId, numTransact) : new Item(995, numTransact * other.price));
		return true;
	}
}
