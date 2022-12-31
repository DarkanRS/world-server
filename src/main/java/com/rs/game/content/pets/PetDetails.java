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
package com.rs.game.content.pets;

/**
 * A class containing pet details for a certain pet.
 *
 * @author Emperor
 *
 */
public final class PetDetails {

	/**
	 * The hunger rate.
	 */
	private double hunger = 0.0;

	/**
	 * The growth rate.
	 */
	private double growth = 0.0;

	/**
	 * The current stage of the pet (0 - baby, 1 - grown, 2 - overgrown).
	 */
	private int stage;

	/**
	 * Constructs a new {@code PetDetails} {@code Object}.
	 *
	 * @param growth
	 *            The growth value.
	 */
	public PetDetails(double growth) {
		this.growth = growth;
	}

	/**
	 * Increases the hunger value by the given amount.
	 *
	 * @param amount
	 *            The amount.
	 */
	public void updateHunger(double amount) {
		hunger += amount;
		if (hunger < 0.0)
			hunger = 0.0;
		else if (hunger > 100.0)
			hunger = 100.0;
	}

	/**
	 * Increases the growth value by the given amount.
	 *
	 * @param amount
	 *            The amount.
	 */
	public void updateGrowth(double amount) {
		growth += amount;
		if (growth < 0.0)
			growth = 0.0;
		else if (growth > 100.0)
			growth = 100.0;
	}

	/**
	 * Gets the hunger.
	 *
	 * @return The hunger.
	 */
	public double getHunger() {
		return hunger;
	}

	/**
	 * Gets the growth.
	 *
	 * @return The growth.
	 */
	public double getGrowth() {
		return growth;
	}

	/**
	 * Gets the stage.
	 *
	 * @return The stage.
	 */
	public int getStage() {
		return stage;
	}

	/**
	 * Sets the stage.
	 *
	 * @param stage
	 *            The stage to set.
	 */
	public void setStage(int stage) {
		this.stage = stage;
	}
}