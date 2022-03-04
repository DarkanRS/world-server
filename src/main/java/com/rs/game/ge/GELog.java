package com.rs.game.ge;

import com.rs.cache.loaders.ItemDefinitions;

import java.util.Date;
import java.util.Objects;

public class GELog {
	private Date time;
	private String from, to;
	private String item;
	private int itemId;
	private int amount;
	private int price;

	public GELog(Offer offer1, Offer offer2, int amount, int price) {
		this.time = new Date();
		Offer sellOffer = offer1.getOfferType() == GE.OfferType.SELL ? offer1 : offer2;
		Offer buyOffer = offer2.getOfferType() == GE.OfferType.BUY ? offer2 : offer1;
		from = sellOffer.getOwner();
		to = buyOffer.getOwner();
		item = ItemDefinitions.getDefs(sellOffer.getItemId()).name;
		itemId = sellOffer.getItemId();
		this.amount = amount;
		this.price = price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GELog geLog = (GELog) o;
		return itemId == geLog.itemId && amount == geLog.amount && price == geLog.price && Objects.equals(time, geLog.time) && Objects.equals(from, geLog.from) && Objects.equals(to, geLog.to) && Objects.equals(item, geLog.item);
	}

	@Override
	public int hashCode() {
		return Objects.hash(time, from, to, item, itemId, amount, price);
	}
}
