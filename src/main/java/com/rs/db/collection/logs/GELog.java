package com.rs.db.collection.logs;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.ge.GE;
import com.rs.game.ge.Offer;

import java.util.Objects;
import java.util.UUID;

public class GELog {
	private String uuid;
	private String from, to;
	private String item;
	private int itemId;
	private int amount;
	private int price;

	public GELog(Offer offer1, Offer offer2, int amount, int price) {
		Offer sellOffer = offer1.getOfferType() == GE.OfferType.SELL ? offer1 : offer2;
		Offer buyOffer = offer2.getOfferType() == GE.OfferType.BUY ? offer2 : offer1;
		from = sellOffer.getOwner();
		to = buyOffer.getOwner();
		item = ItemDefinitions.getDefs(sellOffer.getItemId()).name;
		itemId = sellOffer.getItemId();
		this.amount = amount;
		this.price = price;
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GELog geLog = (GELog) o;
		return itemId == geLog.itemId && amount == geLog.amount && price == geLog.price && Objects.equals(uuid, geLog.uuid) && Objects.equals(from, geLog.from) && Objects.equals(to, geLog.to) && Objects.equals(item, geLog.item);
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to, item, itemId, amount, price, uuid);
	}
}
