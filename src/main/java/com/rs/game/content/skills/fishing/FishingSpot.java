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
package com.rs.game.content.skills.fishing;

import com.rs.lib.game.Animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum FishingSpot {

	CRAYFISH(new int[] { 13431 }, null, new Animation(619), Fish.CRAYFISH),
	SHRIMP(new int[] { 303 }, null, new Animation(621), Fish.SHRIMP, Fish.ANCHOVIES),
	SEA_BAIT(new int[] { 307 }, new int[] { 313 }, new Animation(622), Fish.SARDINES, Fish.HERRING),
	GIANT_CARP(new int[] { 307 }, new int[] { 25 }, new Animation(622), Fish.GIANT_CARP),
	BIG_NET(new int[] { 305 }, null, new Animation(620), Fish.MACKEREL, Fish.COD, Fish.BASS, Fish.SEAWEED, Fish.OYSTER, Fish.CASKET, Fish.LEATHER_BOOTS, Fish.LEATHER_GLOVES),
	TUNA_SWORDFISH(new int[] { 311, 10129 }, null, new Animation(618), Fish.TUNA, Fish.SWORDFISH),
	LOBSTER(new int[] { 301 }, null, new Animation(619), Fish.LOBSTER),
	SHARK(new int[] { 311, 10129 }, null, new Animation(618), Fish.SHARK),
	FLY_FISHING(new int[] { 309 }, new int[] { 314, 10087, 10088, 10089, 10090, 10091 }, new Animation(622), Fish.TROUT, Fish.SALMON),
	BARBARIAN_FLY_FISHING(new int[] { 11323 }, new int[] { 11326, 11324, 11334, 313, 314, 10087, 10088, 10089, 10090, 10091 }, new Animation(622), Fish.LEAPING_TROUT, Fish.LEAPING_SALMON, Fish.LEAPING_STURGEON),
	PIKE(new int[] { 307 }, new int[] { 313 }, new Animation(622), Fish.PIKE),
    LAVA_EEL(new int[] { 1585 }, new int[] { 313 }, new Animation(622), Fish.LAVA_EEL),//oily fishing rod, bait
	MONKFISH(new int[] { 303 }, null, new Animation(621), Fish.MONKFISH),
	FROGSPAWN(new int[] { 303 }, null, new Animation(621), Fish.FROGSPAWN),
	CAVEFISH(new int[] { 307 }, new int[] { 313 }, new Animation(622), Fish.CAVEFISH),
	ROCKTAIL(new int[] { 307 }, new int[] { 15263 }, new Animation(622), Fish.ROCKTAIL);

	private final List<Fish> fish;
	private final int[] tool;
	private final int[] bait;
	private final Animation animation;

	private FishingSpot(int[] tool, int[] bait, Animation animation, Fish... fish) {
		this.tool = tool;
		this.bait = bait;
		this.animation = animation;
		this.fish = new ArrayList<>(Arrays.asList(fish));
		this.fish.sort((f1, f2) -> {
			return f2.getLevel()-f1.getLevel();
		});
	}

	public List<Fish> getFish() {
		return fish;
	}

	public int[] getTool() {
		return tool;
	}

	public int[] getBait() {
		return bait;
	}

	public int getLevel() {
		return fish.get(fish.size()-1).getLevel();
	}

	public Animation getAnimation() {
		return animation;
	}
}
