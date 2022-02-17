package com.rs.migrator.legacyge;

public class OfferSet {
	public String owner;
	public Offer[] offers;

	public OfferSet(String owner) {
		this.owner = owner;
		offers = new Offer[6];
	}
}