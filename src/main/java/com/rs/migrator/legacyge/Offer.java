package com.rs.migrator.legacyge;

import com.rs.game.player.Player;
public final class Offer {

	public enum GrandExchangeOfferType {
		ABORTED(-3),
		RESET_GE(0),
		RESET_AFTER_BUY(8),
		RESET_AFTER_SELL(16),
		SUBMITTING_BUY_OFFER(1),
		BUYING_PROGRESS(2),
		BUYING_PROGRESS_2(3),
		BUYING_PROGRESS_3(4),
		FINISHED_BUYING(5),
		BUYING_PROGRESS_4(6),
		BUYING_PROGRESS_5(7),
		SUBMIT_SELL_OFFER(9),
		SELLING_PROGRESS(10),
		SELLING_PROGRESS_2(11),
		SELLING_PROGRESS_3(13),
		SELLING_PROGRESS_4(14),
		SELLING_PROGRESS_5(15),
		FINISHED_SELLING(13);

		private int opcode;

		GrandExchangeOfferType(int opcode) {
			this.opcode = opcode;
		}

		public int getOpcode() {
			return opcode;
		}
	}

	public enum GrandExchangeType {
		BUYING, SELLING, ABORTED;
	}

	public enum OfferType {
		BUY, SELL;
	}

	private String owner;
	private final OfferType offerType;
	private GrandExchangeType currentType;
	private final int itemId;
	private final int itemAmount;
	private final int pricePerItem;
	private int amountLeft;
	private final int box;
	private boolean aborted = false;
	private int cashToClaim = 0;
	private boolean needsCollected = false;
	private int amountToClaimFromAbort = 0;

	public Offer(String owner, int box, int itemId, int itemAmount, int pricePerItem, OfferType type) {
		this.owner = owner;
		this.box = box;
		this.itemId = itemId;
		this.itemAmount = itemAmount;
		this.pricePerItem = pricePerItem;
		this.offerType = type;
		this.amountLeft = itemAmount;
		updateCurrentType();
	}

	public void updateCurrentType() {
		if (offerType == OfferType.BUY) {
			this.currentType = GrandExchangeType.BUYING;
		} else {
			this.currentType = GrandExchangeType.SELLING;
		}
		if (aborted) {
			this.currentType = GrandExchangeType.ABORTED;
		}
	}

	public GrandExchangeType getCurrentType() {
		return currentType;
	}

	public OfferType getOfferType() {
		return offerType;
	}

	public int getAmountLeft() {
		return amountLeft;
	}

	public int getBox() {
		return box;
	}

	public String getOwner() {
		return owner;
	}

	public int getItemId() {
		return itemId;
	}

	public int getItemAmount() {
		return itemAmount;
	}

	public int getPricePerItem() {
		return pricePerItem;
	}

	public int getCashToClaim() {
		return cashToClaim;
	}

	public void abort() {
		if (aborted)
			return;
		aborted = true;
		needsCollected = true;
		if (offerType == OfferType.BUY) {
			cashToClaim += (pricePerItem * amountLeft);
			amountToClaimFromAbort = itemAmount - amountLeft;
			amountLeft = 0;
		}
		updateCurrentType();
	}

	public void addChange(int changeToAdd) {
		cashToClaim += changeToAdd;
	}

	public void processCashToClaim(Player player) {
		if (cashToClaim > 0) {
			player.getInventory().addItem(995, cashToClaim);
			cashToClaim = 0;
		}
	}

	public boolean stillActive() {
		return cashToClaim > 0 || amountLeft > 0 || needsCollected;
	}

	public int getAmountProcessed() {
		if (!aborted)
			return itemAmount - amountLeft;
		return amountToClaimFromAbort;
	}

	public int getProgressOpcode() {
		if (aborted) {
			return GrandExchangeOfferType.ABORTED.getOpcode();
		}
		if (offerType == OfferType.BUY) {
			if (amountLeft == 0) {
				return GrandExchangeOfferType.FINISHED_BUYING.getOpcode();
			}
			return GrandExchangeOfferType.BUYING_PROGRESS.getOpcode();
		} else {
			if (amountLeft == 0) {
				return GrandExchangeOfferType.FINISHED_SELLING.getOpcode();
			}
			return GrandExchangeOfferType.SELLING_PROGRESS.getOpcode();
		}
	}

	public boolean isAborted() {
		return aborted;
	}
}