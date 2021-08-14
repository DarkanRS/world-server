package com.rs.game.ge;

import com.rs.game.item.ItemsContainer;
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
}
