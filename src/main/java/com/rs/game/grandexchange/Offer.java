//package com.rs.game.grandexchange;
//
//import com.rs.db.WorldDB;
//import com.rs.game.grandexchange.GrandExchange.GrandExchangeOfferType;
//import com.rs.game.grandexchange.GrandExchange.GrandExchangeType;
//import com.rs.game.grandexchange.GrandExchange.OfferType;
//import com.rs.game.player.Player;
//import com.rs.lib.file.FileManager;
//
//public final class Offer {
//	
//	private String owner;
//	private final OfferType offerType;
//	private GrandExchangeType currentType;
//	private final int itemId;
//	private final int itemAmount;
//	private final int pricePerItem;
//	private int amountLeft;
//	private final int box;
//	private boolean aborted = false;
//	private int cashToClaim = 0;
//	private boolean needsCollected = false;
//	private int amountToClaimFromAbort = 0;
//
//	public Offer(String owner, int box, int itemId, int itemAmount, int pricePerItem, OfferType type) {
//		this.owner = owner;
//		this.box = box;
//		this.itemId = itemId;
//		this.itemAmount = itemAmount;
//		this.pricePerItem = pricePerItem;
//		this.offerType = type;
//		this.amountLeft = itemAmount;
//		updateCurrentType();
//	}
//
//	public void updateCurrentType() {
//		if (offerType == OfferType.BUY) {
//			this.currentType = GrandExchangeType.BUYING;
//		} else {
//			this.currentType = GrandExchangeType.SELLING;
//		}
//		if (aborted) {
//			this.currentType = GrandExchangeType.ABORTED;
//		}
//	}
//
//	public GrandExchangeType getCurrentType() {
//		return currentType;
//	}
//
//	public OfferType getOfferType() {
//		return offerType;
//	}
//
//	public int getAmountLeft() {
//		return amountLeft;
//	}
//
//	public int getBox() {
//		return box;
//	}
//
//	public String getOwner() {
//		return owner;
//	}
//
//	public int getItemId() {
//		return itemId;
//	}
//
//	public int getItemAmount() {
//		return itemAmount;
//	}
//
//	public int getPricePerItem() {
//		return pricePerItem;
//	}
//
//	public int getCashToClaim() {
//		return cashToClaim;
//	}
//
//	public void abort() {
//		if (aborted)
//			return;
//		aborted = true;
//		needsCollected = true;
//		if (offerType == OfferType.BUY) {
//			cashToClaim += (pricePerItem * amountLeft);
//			amountToClaimFromAbort = itemAmount - amountLeft;
//			amountLeft = 0;
//		}
//		updateCurrentType();
//	}
//
//	public void transactBuy(int number, int price, int change) {
//		if (offerType != OfferType.BUY) {
//			FileManager.logError("Sell offer attempted to transact buy offer: " + owner);
//			return;
//		}
//		if (price > pricePerItem)
//			return;
//		if (number > amountLeft)
//			return;
//
//		amountLeft -= number;
//		if (change > 0)
//			cashToClaim += change * number;
//
//		if (amountLeft <= 0) {
//			amountLeft = 0;
//			needsCollected = true;
//		}
//	}
//
//	public void transactSell(int number, int price) {
//		if (offerType != OfferType.SELL) {
//			FileManager.logError("Buy offer attempted to transact sell offer: " + owner);
//			return;
//		}
//		if (price < pricePerItem)
//			return;
//		if (number > amountLeft)
//			return;
//
//		amountLeft -= number;
//		cashToClaim += price * number;
//
//		if (amountLeft <= 0) {
//			amountLeft = 0;
//			needsCollected = true;
//		}
//	}
//
//	public void addChange(int changeToAdd) {
//		cashToClaim += changeToAdd;
//	}
//
//	public void claimAndClear(Player player) {
//		if (amountLeft == 0 || aborted) {
//			if (player.getInventory().getFreeSlots() > 1) {
//				player.lock();
//				WorldDB.getGE().remove(player.getUsername(), box, () -> {
//					if (offerType == OfferType.BUY) {
//						int noted = GrandExchange.getNotedId(itemId);
//						if (noted != -1 && getAmountProcessed() > 0) {
//							player.getInventory().addItem(noted, getAmountProcessed());
//						}
//					} else {
//						if (aborted) {
//							int noted = GrandExchange.getNotedId(itemId);
//							if (GrandExchange.getNotedId(itemId) != -1 && amountLeft > 0) {
//								player.getInventory().addItem(noted, amountLeft);
//							}
//						}
//					}
//					processCashToClaim(player);
//					player.setGrandExchangeOffer(null, box);
//					player.unlock();
//				});
//			} else {
//				player.sendMessage("You don't have enough inventory space.");
//			}
//		} else {
//			processCashToClaim(player);
//			GEHandler.updateOffer(player.getUsername(), this);
//		}
//		GrandExchange.updateGrandExchangeBoxes(player);
//	}
//	
//	public void processCashToClaim(Player player) {
//		if (cashToClaim > 0) {
//			player.getInventory().addItem(995, cashToClaim);
//			cashToClaim = 0;
//		}
//	}
//
//	public boolean stillActive() {
//		return cashToClaim > 0 || amountLeft > 0 || needsCollected;
//	}
//
//	public int getAmountProcessed() {
//		if (!aborted)
//			return itemAmount - amountLeft;
//		return amountToClaimFromAbort;
//	}
//
//	public int getProgressOpcode() {
//		if (aborted) {
//			return GrandExchangeOfferType.ABORTED.getOpcode();
//		}
//		if (offerType == OfferType.BUY) {
//			if (amountLeft == 0) {
//				return GrandExchangeOfferType.FINISHED_BUYING.getOpcode();
//			}
//			return GrandExchangeOfferType.BUYING_PROGRESS.getOpcode();
//		} else {
//			if (amountLeft == 0) {
//				return GrandExchangeOfferType.FINISHED_SELLING.getOpcode();
//			}
//			return GrandExchangeOfferType.SELLING_PROGRESS.getOpcode();
//		}
//	}
//
//	public boolean isAborted() {
//		return aborted;
//	}
//}
