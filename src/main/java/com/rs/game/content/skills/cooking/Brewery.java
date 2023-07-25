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
package com.rs.game.content.skills.cooking;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

import java.util.Arrays;
import java.util.stream.Stream;

@PluginEventHandler
public class Brewery {

	private static final int BUCKET_OF_WATER = 1929;
	private static final int BARLEY_MALT = 6008;
	private static final int ALE_YEAST = 5767;
	private static final int EMPTY_BUCKET = 1925;
	private static final int THE_STUFF = 8988;
	private static final int BEER_GLASS = 1919;
	private static final int CALQUAT_KEG = 5769;

	private static final Animation ADD_INGREDIENT = new Animation(2292);
	private static final Animation POUR_WATER = new Animation(2283);
	private static final Animation CALQUAT_LEVEL = new Animation(2284);
	private static final Animation BEER_GLASS_LEVEL = new Animation(2285);

	public static final long BREW_TICKS = Ticks.fromHours(12);

	private Brewable brew;
	private int prep;
	private int fermentStage;
	private long lastTime;
	private boolean barrelled;
	private boolean theStuff;
	private boolean spoiled;
	private boolean mature;
	private boolean keldagrim;
	private transient Player player;

	public Brewery(boolean keldagrim) {
		this.keldagrim = keldagrim;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public static LoginHandler onLogin = new LoginHandler(e -> {
		e.getPlayer().getKeldagrimBrewery().updateVars();
		e.getPlayer().getPhasmatysBrewery().updateVars();
	});

	public static ObjectClickHandler handleValve = new ObjectClickHandler(new Object[] { 7442, 7443 }, e -> {
		Brewery brewery = e.getObjectId() == 7442 ? e.getPlayer().getKeldagrimBrewery() : e.getPlayer().getPhasmatysBrewery();
		brewery.turnValve();
	});

	public static ObjectClickHandler handleBarrels = new ObjectClickHandler(new Object[] { 7431, 7432 }, e -> {
		Brewery brewery = e.getObjectId() == 7431 ? e.getPlayer().getKeldagrimBrewery() : e.getPlayer().getPhasmatysBrewery();
		switch(e.getOption()) {
		case "Drain":
			brewery.reset();
			e.getPlayer().sendMessage("You drain the spoiled drink from the vat.");
			break;
		case "Level":
			brewery.level();
			break;
		}
	});

	public static ItemOnObjectHandler handleVats = new ItemOnObjectHandler(new Object[] { 7494, 7495 }, Stream.concat(Arrays.stream(Brewable.values()).map(Brewable::getIngredient), Stream.of(BUCKET_OF_WATER, BARLEY_MALT, ALE_YEAST, THE_STUFF)).toArray(), e -> {
		Brewery brewery = e.getObjectId() == 7494 ? e.getPlayer().getKeldagrimBrewery() : e.getPlayer().getPhasmatysBrewery();
		switch(e.getItem().getId()) {
		case BUCKET_OF_WATER:
			brewery.addWater();
			break;
		case BARLEY_MALT:
			brewery.addMalt();
			break;
		case ALE_YEAST:
			brewery.addYeast();
			break;
		case THE_STUFF:
			brewery.addTheStuff();
			break;
		default:
			brewery.addSecondary(e.getItem().getId());
			break;
		}
		brewery.updateVars();
	});

	public void ferment() {
		if (isFinished())
			return;
		if (Utils.random(80 + player.getSkills().getLevel(Constants.COOKING)) == 0)
			spoiled = true;
		if (Utils.random(5) == 0)
			fermentStage = Utils.clampI(fermentStage+1, 0, 4);
		if (Utils.random(200) == 0)
			fermentStage = 4;
	}

	public void process() {
		if (lastTime <= 0)
			return;
		long currTime = System.currentTimeMillis();
		long timePassed = currTime - lastTime;
		if (timePassed > BREW_TICKS) {
			int cycles = (int) (timePassed / BREW_TICKS);
			for (int i = 0;i < cycles;i++)
				ferment();
			lastTime = currTime - (timePassed % (cycles * BREW_TICKS));
		}
		updateVars();
	}

	public void addWater() {
		if (prep != 0)
			return;
		if (player.getInventory().containsItem(BUCKET_OF_WATER, 2)) {
			player.setNextAnimation(POUR_WATER);
			player.getInventory().deleteItem(BUCKET_OF_WATER, 2);
			player.getInventory().addItem(EMPTY_BUCKET, 2);
			prep = 1;
		} else
			player.sendMessage("You need 2 buckets of water to add to the brew.");
	}

	public void addMalt() {
		if (prep != 1) {
			player.sendMessage("Add water first, then malt, then ale yeast.");
			return;
		}
		if (player.getInventory().containsItem(BARLEY_MALT, 2)) {
			player.setNextAnimation(ADD_INGREDIENT);
			player.getInventory().deleteItem(BARLEY_MALT, 2);
			prep = 2;
		} else
			player.sendMessage("You need two barley malts to add to the brew.");
	}

	public void addSecondary(int itemId) {
		Brewable brew = Brewable.forId(itemId);
		if (brew != null) {
			if (player.getSkills().getLevel(Constants.COOKING) < brew.getLevelRequirement()) {
				player.sendMessage("You need a cooking level of " + brew.getLevelRequirement() + " to create that brew.");
				return;
			}
			if (prep == 2) {
				if (player.getInventory().containsItem(brew.getIngredient(), brew.getIngredientAmount())) {
					player.getInventory().deleteItem(brew.getIngredient(), brew.getIngredientAmount());
					player.setNextAnimation(ADD_INGREDIENT);
					this.brew = brew;
				} else
					player.sendMessage("You need " + brew.getIngredientAmount() + " to create that brew.");
			} else
				player.sendMessage("You need to have added water and barley malt before adding the ale's ingredients.");
		} else
			player.sendMessage("Nothing interesting happens.");
	}

	public void addYeast() {
		if (prep != 2 || brew == null || !player.getInventory().containsItem(ALE_YEAST, 1)) {
			player.sendMessage("The brew isn't ready for the yeast yet.");
			return;
		}
		if (lastTime >= 1) {
			player.sendMessage("The brew is already fermenting.");
			return;
		}
		player.setNextAnimation(ADD_INGREDIENT);
		player.getInventory().deleteItem(ALE_YEAST, 1);
		lastTime = System.currentTimeMillis();
		player.sendMessage("The brew begins to ferment.");
	}

	public void addTheStuff() {
		if (prep != 2 || !player.getInventory().containsItem(THE_STUFF, 1)) {
			player.sendMessage("Add water first, then malt, and the ale's ingredients before adding \"the stuff\".");
			return;
		}
		if (theStuff) {
			player.sendMessage("You've already added \"the stuff\" to this batch.");
			return;
		}
		player.setNextAnimation(ADD_INGREDIENT);
		player.getInventory().deleteItem(THE_STUFF, 1);
		theStuff = true;
	}

	public void turnValve() {
		if (isFinished() && !barrelled) {
			barrelled = true;
			if (Utils.random(100) <= (theStuff ? 75 : 10))
				mature = true;
			updateVars();
		} else
			player.sendMessage("The brew isn't done fermenting yet.");
	}

	public void level() {
		if (brew == Brewable.KELDA_STOUT) {
			if (player.getInventory().containsItem(BEER_GLASS, 1)) {
				player.getInventory().deleteItem(BEER_GLASS, 1);
				player.getInventory().addItem(brew.getBeerGlassId(mature), 1);
				player.setNextAnimation(BEER_GLASS_LEVEL);
				reset();
			} else
				player.sendMessage("You need a beer glass to empty the stout into.");
			return;
		}
		int container = -1;
		if (player.getInventory().containsItem(BEER_GLASS, 8))
			container = BEER_GLASS;
		if (player.getInventory().containsItem(CALQUAT_KEG, 2))
			container = CALQUAT_KEG;
		if (container == -1) {
			player.sendMessage("You need 8 beer glasses or 2 calquat kegs to empty this vat.");
			return;
		}
		player.incrementCount(brew.name() + " brewed");
		if (mature)
			player.incrementCount("Mature " + brew.name() + " brewed");
		player.getInventory().deleteItem(container, container == BEER_GLASS ? 8 : 2);
		player.getInventory().addItem(container == BEER_GLASS ? brew.getBeerGlassId(mature) : brew.getCalquatId(mature), container == BEER_GLASS ? 8 : 2);
		player.getSkills().addXp(Constants.COOKING, brew.getXp()*8);
		player.setNextAnimation(container == BEER_GLASS ? BEER_GLASS_LEVEL : CALQUAT_LEVEL);
		reset();
	}

	public void reset() {
		brew = null;
		prep = 0;
		theStuff = false;
		spoiled = false;
		mature = false;
		barrelled = false;
		fermentStage = 0;
		lastTime = -1;
		updateVars();
	}

	public boolean isFinished() {
		return brew != null && (spoiled || fermentStage >= 4 || (brew == Brewable.KELDA_STOUT && fermentStage >= 3));
	}

	public void updateVars() {
		if (brew == null) {
			setBarrel(0);
			setVat(Utils.clampI(prep, 0, 2));
			return;
		}
		if (isFinished()) {
			if (spoiled) {
				setBarrel(barrelled ? 1 : 0);
				setVat(barrelled ? 0 : 64);
			} else {
				setVat(barrelled ? 0 : brew.getVatVal(fermentStage));
				setBarrel(barrelled ? brew.getBarrelVal(mature) : 0);
			}
		} else
			setVat(brew.getVatVal(fermentStage));
	}

	private void setVat(int value) {
		player.getVars().setVarBit(keldagrim ? 736 : 737, value);
	}

	private void setBarrel(int value) {
		player.getVars().setVarBit(keldagrim ? 738 : 739, value);
	}
}
